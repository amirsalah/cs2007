/*
 * This file is part of the nreduce project
 * Copyright (C) 2006-2007 Peter Kelly (pmk@cs.adelaide.edu.au)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * $Id: node.c 656 2007-10-02 06:51:37Z pmkelly $
 *
 */

#ifdef HAVE_CONFIG_H
#include "config.h"
#endif

#define NODE_C

#include "src/nreduce.h"
#include "node.h"
#include "netprivate.h"
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <assert.h>
#include <ctype.h>
#include <stdarg.h>
#include <math.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/select.h>
#include <sys/time.h>
#include <time.h>
#include <fcntl.h>
#include <unistd.h>
#include <signal.h>

const char *log_levels[LOG_COUNT] = {
  "NONE",
  "ERROR",
  "WARNING",
  "INFO",
  "DEBUG1",
  "DEBUG2",
};

static void endpoint_add_message(endpoint *endpt, message *msg);
static endpointid node_add_thread_locked(node *n, const char *type,
                                         endpoint_threadfun fun, void *arg, pthread_t *threadp,
                                         int localid, int stacksize);

static endpoint *find_endpoint(node *n, int localid)
{
  endpoint *endpt;
  assert(NODE_ALREADY_LOCKED(n));
  for (endpt = n->p->endpoints.first; endpt; endpt = endpt->next)
    if (endpt->epid.localid == localid)
      break;
  return endpt;
}

void got_message(node *n, const msgheader *hdr, endpointid source,
                 uint32_t tag, uint32_t size, const void *data)
{
  endpoint *endpt;
  message *newmsg;

  if (NULL == (endpt = find_endpoint(n,hdr->destlocalid))) {

    /* This handles the case where a link message is sent but the destination has
       already exited. If the link were to arrive just before the endpoint exits, then
       the endpoint removal code would take care of sending the exit message. */
    if (MSG_LINK == tag) {
      endpoint_exit_msg eem;
      eem.epid.ip = n->listenip;
      eem.epid.port = n->listenport;
      eem.epid.localid = hdr->destlocalid;
      eem.reason = 0;
      assert(sizeof(endpointid) == size);
      node_send_locked(n,hdr->destlocalid,*(endpointid*)data,MSG_ENDPOINT_EXIT,&eem,sizeof(eem));
    }
    else if (MSG_UNLINK == tag) {
      /* don't need to do anything in this case */
    }
    else {
/*       node_log(n,LOG_WARNING, */
/*                "Message %d received for unknown endpoint %d (source "EPID_FORMAT")", */
/*                hdr->tag,hdr->destlocalid,EPID_ARGS(hdr->source)); */
    }
    return;
  }

  newmsg = (message*)malloc(sizeof(message)+size);
  newmsg->next = NULL;
  newmsg->prev = NULL;
  newmsg->source = source;
  newmsg->dest.ip = n->listenip;
  newmsg->dest.port = n->listenport;
  newmsg->dest.localid = hdr->destlocalid;
  newmsg->tag = tag;
  newmsg->size = size;
  memcpy(newmsg->data,data,size);
  endpoint_add_message(endpt,newmsg);
}

void remove_connection(node *n, connection *conn)
{
  assert(NODE_ALREADY_LOCKED(n));
  node_log(n,LOG_INFO,"Removing connection %s",conn->hostname);
  list_push(&n->p->toclose,(void*)conn->sock);
  llist_remove(&n->p->connections,conn);
  free(conn->hostname);
  array_free(conn->recvbuf);
  array_free(conn->sendbuf);
  free(conn);
}

void start_console(node *n, connection *conn)
{
  socketid *sockid = (socketid*)malloc(sizeof(socketid));
  endpoint *mgrendpt;
  conn->isreg = 1;
  conn->frameids[READ_FRAMEADDR] = 1;
  memcpy(sockid,&conn->sockid,sizeof(socketid));
  conn->owner = node_add_thread_locked(n,"console",console_thread,sockid,NULL,0,0);
  if (NULL != (mgrendpt = find_endpoint(n,IO_ID)))
    endpoint_link_locked(mgrendpt,conn->owner);
}

static connection *find_connection(node *n, in_addr_t nodeip, unsigned short nodeport)
{
  if (getenv("OUTGOING")) {
    connection *conn;
    for (conn = n->p->connections.first; conn; conn = conn->next)
      if ((conn->ip == nodeip) && (conn->port == nodeport) && conn->outgoing)
        return conn;
    return NULL;
  }
  else {
    connection *conn;
    for (conn = n->p->connections.first; conn; conn = conn->next)
      if ((conn->ip == nodeip) && (conn->port == nodeport))
        return conn;
    return NULL;
  }
}

connection *add_connection(node *n, const char *hostname, int sock, listener *l)
{
  connection *conn = (connection*)calloc(1,sizeof(connection));
  assert(NODE_ALREADY_LOCKED(n));
  assert(IO_ID == n->iothid.localid);
  conn->sockid.coordid = n->iothid;
  conn->sockid.sid = n->p->nextsid++;
  conn->hostname = strdup(hostname);
  conn->ip = 0;
  conn->sock = sock;
  conn->l = l;
  conn->n = n;
/*   conn->readfd = -1; */
  conn->port = -1;
  conn->recvbuf = array_new(1,n->iosize*2);
  conn->sendbuf = array_new(1,n->iosize*2);
  conn->canread = 1;
  conn->canwrite = 1;
  if (l == n->p->mainl)
    array_append(conn->sendbuf,WELCOME_MESSAGE,strlen(WELCOME_MESSAGE));
  llist_append(&n->p->connections,conn);
  return conn;
}

static node *node_new(int loglevel)
{
  node *n = (node*)calloc(1,sizeof(node));
  int pipefds[2];
  char *logenv;

  n->p = (node_private*)calloc(1,sizeof(node_private));
  init_mutex(&n->p->lock);
  pthread_cond_init(&n->p->closecond,NULL);
  n->p->nextlocalid = FIRST_ID;
  n->p->nextsid = 2;
  n->iosize = DEFAULT_IOSIZE;

  if (0 > determine_ip(&n->listenip))
    exit(1);

  if (0 > pipe(pipefds)) {
    perror("pipe");
    exit(1);
  }
  n->p->ioready_readfd = pipefds[0];
  n->p->ioready_writefd = pipefds[1];
  n->p->logfile = stdout;
  n->p->loglevel = loglevel;

  if (NULL != (logenv = getenv("LOG_LEVEL"))) {
    int i;
    for (i = 0; i < LOG_COUNT; i++) {
      if (!strcasecmp(logenv,log_levels[i])) {
        n->p->loglevel = i;
        break;
      }
    }
    if (LOG_COUNT == i) {
      fprintf(stderr,"Invalid log level: %s\n",logenv);
      exit(1);
    }
  }

  return n;
}

static void node_free(node *n)
{
  if (n->p->mainl) {
    lock_node(n);
    node_remove_listener(n,n->p->mainl);
    unlock_node(n);
  }
  node_close_pending(n);
  assert(NULL == n->p->endpoints.first);
  assert(NULL == n->p->listeners.first);

  pthread_cond_destroy(&n->p->closecond);
  destroy_mutex(&n->p->lock);
  close(n->p->ioready_readfd);
  close(n->p->ioready_writefd);
  free(n->p);
  free(n);
}

node *node_start(int loglevel, int port)
{
  node *n = node_new(loglevel);
  listener *l;
  char errmsg[ERRMSG_MAX+1];

  lock_node(n);
  l = node_listen_locked(n,n->listenip,port,0,NULL,0,1,NULL,errmsg,ERRMSG_MAX);
  unlock_node(n);

  if (NULL == l) {
    fprintf(stderr,"%s\n",errmsg);
    node_free(n);
    return NULL;
  }

  node_start_iothread(n);
  return n;
}

void node_run(node *n)
{
  if (0 != pthread_join(n->p->iothread,NULL))
    fatal("pthread_join: %s",strerror(errno));
  node_close_endpoints(n);
  node_close_connections(n);
  node_free(n);
}

void node_log(node *n, int level, const char *format, ...)
{
  va_list ap;
  char *newfmt;
  if (level > n->p->loglevel)
    return;
  assert(0 <= level);
  assert(LOG_COUNT > level);

  /* Use only a single vfprintf call to avoid interleaving of messages from different threads */
  newfmt = (char*)malloc(8+1+strlen(format)+1+1);
  sprintf(newfmt,"%-8s %s\n",log_levels[level],format);
  va_start(ap,format);
  vfprintf(n->p->logfile,newfmt,ap);
  va_end(ap);
  free(newfmt);
}

listener *node_listen_locked(node *n, in_addr_t ip, int port, int notify, void *data,
                             int dontaccept, int ismain, endpointid *owner, char *errmsg,
                             int errlen)
{
  int fd;
  int actualport = 0;
  int yes = 1;
  struct sockaddr_in local_addr;
  struct sockaddr_in new_addr;
  socklen_t new_size = sizeof(struct sockaddr_in);
  listener *l;

  local_addr.sin_family = AF_INET;
  local_addr.sin_port = htons(port);
  local_addr.sin_addr.s_addr = ip;
  memset(&local_addr.sin_zero,0,8);

  if (-1 == (fd = socket(AF_INET,SOCK_STREAM,0))) {
    snprintf(errmsg,errlen,"socket: %s",strerror(errno));
    return NULL;
  }

  if (-1 == setsockopt(fd,SOL_SOCKET,SO_REUSEADDR,&yes,sizeof(int))) {
    snprintf(errmsg,errlen,"setsockopt: %s",strerror(errno));
    close(fd);
    return NULL;
  }

  if (-1 == bind(fd,(struct sockaddr*)&local_addr,sizeof(struct sockaddr))) {
    snprintf(errmsg,errlen,"bind: %s",strerror(errno));
    close(fd);
    return NULL;
  }

  if (0 > getsockname(fd,(struct sockaddr*)&new_addr,&new_size)) {
    snprintf(errmsg,errlen,"getsockname: %s",strerror(errno));
    close(fd);
    return NULL;
  }
  actualport = ntohs(new_addr.sin_port);

  if (-1 == listen(fd,LISTEN_BACKLOG)) {
    snprintf(errmsg,errlen,"listen: %s",strerror(errno));
    close(fd);
    return NULL;
  }

  assert((0 == port) || (actualport == port));

  l = (listener*)calloc(1,sizeof(listener));
  l->ip = ip;
  l->port = actualport;
  l->fd = fd;
  l->notify = notify;
  l->data = data;
  l->dontaccept = dontaccept;
  if (owner)
    l->owner = *owner;

  if (ismain) {
    n->listenport = actualport;
    n->p->mainl = l;

    n->iothid.ip = n->listenip;
    n->iothid.port = n->listenport;
    n->iothid.localid = IO_ID;
  }

  l->sockid.coordid = n->iothid;
  l->sockid.sid = n->p->nextsid++;
  llist_append(&n->p->listeners,l);
  node_notify(n);

  return l;
}

void node_remove_listener(node *n, listener *l)
{
  connection *conn;
  assert(NODE_ALREADY_LOCKED(n));
  conn = n->p->connections.first;
  while (conn) {
    connection *next = conn->next;
    if (conn->l == l) {
      notify_closed(n,conn,0);
      remove_connection(n,conn);
    }
    conn = next;
  }

  llist_remove(&n->p->listeners,l);
  node_notify(n);

  list_push(&n->p->toclose,(void*)l->fd);
  free(l);
}

void node_close_pending(node *n)
{
  while (n->p->toclose) {
    int fd = (int)n->p->toclose->data;
    list *next = n->p->toclose->next;
    close(fd);
    free(n->p->toclose);
    n->p->toclose = next;
  }
}

/* This works as long as we don't recycle localids... */
static void node_waitclose_locked(node *n, int localid)
{
  assert(NODE_ALREADY_LOCKED(n));
  while (1) {
    int alive = 0;
    endpoint *endpt;
    for (endpt = n->p->endpoints.first; endpt; endpt = endpt->next)
      if (endpt->epid.localid == localid)
        alive = 1;

    if (!alive)
      break;

    pthread_cond_wait(&n->p->closecond,&n->p->lock);
  }
}

void node_close_endpoints(node *n)
{
  endpoint *endpt;
  lock_mutex(&n->p->lock);
  while (NULL != (endpt = n->p->endpoints.first)) {
    node_send_locked(n,endpt->epid.localid,endpt->epid,MSG_KILL,NULL,0);
    node_waitclose_locked(n,endpt->epid.localid);
  }
  unlock_mutex(&n->p->lock);
}

void node_close_connections(node *n)
{
  lock_node(n);
  while (n->p->connections.first) {
    notify_closed(n,n->p->connections.first,0);
    remove_connection(n,n->p->connections.first);
  }
  unlock_node(n);
}

#ifdef DEBUG_SHORT_KEEPALIVE
int set_keepalive(node *n, int sock, int s, char *errmsg, int errlen)

{
  int one = 1;
  if (0 > setsockopt(sock,IPPROTO_TCP,TCP_KEEPIDLE,&s,sizeof(int))) {
    snprintf(errmsg,errlen,"setsockopt(TCP_KEEPIDLE): %s",strerror(errno));
    return -1;
  }

  if (0 > setsockopt(sock,IPPROTO_TCP,TCP_KEEPINTVL,&s,sizeof(int))) {
    snprintf(errmsg,errlen,"setsockopt(TCP_KEEPINTVL): %s",strerror(errno));
    return -1;
  }

  if (0 > setsockopt(sock,IPPROTO_TCP,TCP_KEEPCNT,&one,sizeof(int))) {
    snprintf(errmsg,errlen,"setsockopt(TCP_KEEPCNT): %s",strerror(errno));
    return -1;
  }

  if (0 > setsockopt(sock,SOL_SOCKET,SO_KEEPALIVE,&one,sizeof(int))) {
    snprintf(errmsg,errlen,"setsockopt(SO_KEEPALIVE): %s",strerror(errno));
    return -1;
  }

  return 0;
}
#endif

connection *node_connect_locked(node *n, const char *dest, in_addr_t destaddr,
                                int port, int othernode, char *errmsg, int errlen)
{
  struct sockaddr_in addr;
  int sock;
  int connected = 0;
  connection *conn;
  char *hostname;
  int r;

  assert(NODE_ALREADY_LOCKED(n));

  addr.sin_family = AF_INET;
  addr.sin_port = htons(port);
  memset(&addr.sin_zero,0,8);

  if (dest) {
    int error = 0;
    if (0 > lookup_address(dest,&addr.sin_addr.s_addr,&error)) {
      snprintf(errmsg,errlen,"%s",hstrerror(error));
      return NULL;
    }
  }
  else {
    addr.sin_addr.s_addr = destaddr;
  }

  if (0 > (sock = socket(AF_INET,SOCK_STREAM,0))) {
    snprintf(errmsg,errlen,"socket: %s",strerror(errno));
    perror("socket");
    return NULL;
  }

  if (0 > set_non_blocking(sock)) {
    snprintf(errmsg,errlen,"cannot set non-blocking socket");
    return NULL;
  }

  /* We expect the connect() call to not succeed immediately, because the socket is in
     non-blocking mode */
  r = connect(sock,(struct sockaddr*)&addr,sizeof(struct sockaddr));
  assert(0 > r);
  if (EINPROGRESS != errno) {
    snprintf(errmsg,errlen,"connect: %s",strerror(errno));
    return NULL;
  }

#ifdef DEBUG_SHORT_KEEPALIVE
  /* Set a small keepalive interval so connection timeouts can be tested easily */
  if (0 > set_keepalive(n,sock,2,errmsg,errlen)) {
    return NULL;
  }
#endif

  hostname = lookup_hostname(addr.sin_addr.s_addr);

  if (othernode) {
    assert(n->listenport == n->p->mainl->port);
    conn = add_connection(n,hostname,sock,n->p->mainl);
    array_append(conn->sendbuf,&n->listenport,sizeof(unsigned short));
  }
  else {
    conn = add_connection(n,hostname,sock,NULL);
    conn->isreg = 1;
  }

  free(hostname);
  conn->connected = connected;
  conn->ip = addr.sin_addr.s_addr;
  conn->port = port;
  conn->outgoing = 1;

  if (conn->connected)
    notify_connect(n,conn,0);

  node_notify(n);

  return conn;
}

void node_handle_endpoint_exit(node *n, endpoint_exit_msg *m)
{
  connection *conn;
  listener *l;

  conn = n->p->connections.first;
  while (conn) {
    connection *next = conn->next;
    /* no need to notify here; since the owner has exited */
    if (!endpointid_isnull(&conn->owner) && endpointid_equals(&conn->owner,&m->epid))
      remove_connection(n,conn);
    conn = next;
  }

  l = n->p->listeners.first;
  while (l) {
    listener *next = l->next;
    if (!endpointid_isnull(&l->owner) && endpointid_equals(&l->owner,&m->epid))
      node_remove_listener(n,l);
    l = next;
  }
}

void node_send_locked(node *n, uint32_t sourcelocalid, endpointid destendpointid,
                      uint32_t tag, const void *data, uint32_t size)
{
  connection *conn;
  msgheader hdr;

  assert(NODE_ALREADY_LOCKED(n));
  assert(n->listenport == n->p->mainl->port);
  assert(INADDR_ANY != destendpointid.ip);
  assert(0 < destendpointid.port);

  memset(&hdr,0,sizeof(msgheader));
  hdr.sourcelocalid = sourcelocalid;
  hdr.destlocalid = destendpointid.localid;
  hdr.size1 = size;
  hdr.tag1 = tag;

  if ((destendpointid.ip == n->listenip) &&
      (destendpointid.port == n->listenport)) {
    endpointid source = { ip: n->listenip, port: n->listenport, localid: sourcelocalid };
    got_message(n,&hdr,source,tag,size,data);
  }
  else {
    if (NULL == (conn = find_connection(n,destendpointid.ip,destendpointid.port))) {
      char errmsg[ERRMSG_MAX+1];
      unsigned char *addrbytes = (unsigned char*)&destendpointid.ip;
      node_log(n,LOG_INFO,"No connection yet to %u.%u.%u.%u:%d; establishing",
               addrbytes[0],addrbytes[1],addrbytes[2],addrbytes[3],destendpointid.port);
      conn = node_connect_locked(n,NULL,destendpointid.ip,destendpointid.port,1,errmsg,ERRMSG_MAX);
      if (NULL == conn) {
        if (MSG_LINK == tag) {
          endpointid epid = { ip: n->listenip, port: n->listenport, localid: sourcelocalid };
          endpoint_exit_msg eem = { epid: destendpointid, reason: 0 };
          node_send_locked(n,sourcelocalid,epid,MSG_ENDPOINT_EXIT,&eem,sizeof(eem));
        }
        return;
      }
    }

    array_append(conn->sendbuf,&hdr,sizeof(msgheader));
    array_append(conn->sendbuf,data,size);
    node_notify(n);
  }
}

void node_shutdown(node *n)
{
  lock_node(n);
  n->p->shutdown = 1;
  node_notify(n);
  unlock_node(n);
}

void node_notify(node *n)
{
  char c = 1;
  assert(NODE_ALREADY_LOCKED(n));
  if (!n->p->notified) {
    write(n->p->ioready_writefd,&c,1);
    n->p->notified = 1;
  }
}

void done_writing(node *n, connection *conn)
{
  if (!conn->canwrite)
    return;
  assert(NODE_ALREADY_LOCKED(n));
  assert(conn->canwrite);
  if (0 > shutdown(conn->sock,SHUT_WR))
    node_log(n,LOG_WARNING,"shutdown(SHUT_WR) on %s:%d failed: %s",
             conn->hostname,conn->port,strerror(errno));
  conn->canwrite = 0;
  if (!conn->canread && !conn->canwrite) {
    notify_closed(n,conn,0);
    remove_connection(n,conn);
  }
}

void done_reading(node *n, connection *conn)
{
  assert(NODE_ALREADY_LOCKED(n));
  if (!conn->canread)
    return;
  assert(conn->canread);
  if (0 > shutdown(conn->sock,SHUT_RD))
    node_log(n,LOG_WARNING,"shutdown(SHUT_RD) on %s:%d failed: %s",
             conn->hostname,conn->port,strerror(errno));
  conn->canread = 0;
  if (!conn->canread && !conn->canwrite) {
    notify_closed(n,conn,0);
    remove_connection(n,conn);
  }
}

static void *endpoint_thread(void *data)
{
  endpoint *endpt = (endpoint*)data;
  node *n = endpt->n;
  list *l;

  enable_invalid_fpe();

  /* Execute the thread */
  endpt->p->fun(n,endpt,endpt->p->data);

  /* Remove the endpoint */
  lock_node(n);
  for (l = endpt->p->inlinks; l; l = l->next) {
    endpointid link = *(endpointid*)l->data;
    if (!endpointid_equals(&endpt->epid,&link)) {
      endpoint_exit_msg eem;
      eem.epid = endpt->epid;
      eem.reason = 1; /* FIXME: should make this settable by the endpoint */
      node_send_locked(n,endpt->epid.localid,link,MSG_ENDPOINT_EXIT,&eem,sizeof(eem));
    }
  }
  llist_remove(&n->p->endpoints,endpt);
  pthread_cond_broadcast(&n->p->closecond);

  /* Free data */
  while (NULL != endpt->p->mailbox.first) {
    message *next = endpt->p->mailbox.first->next;
    message_free(endpt->p->mailbox.first);
    endpt->p->mailbox.first = next;
  }
  list_free(endpt->p->inlinks,free);
  list_free(endpt->p->outlinks,free);
  destroy_mutex(&endpt->p->mailbox.lock);
  pthread_cond_destroy(&endpt->p->mailbox.cond);
  free(endpt->p->type);
  free(endpt->p);
  free(endpt);
  unlock_node(n);
  return NULL;
}

static endpointid node_add_thread_locked(node *n, const char *type,
                                         endpoint_threadfun fun, void *arg, pthread_t *threadp,
                                         int localid, int stacksize)
{
  pthread_t thread;
  pthread_attr_t attr;
  endpointid epid;
  endpoint *endpt;

  assert(NODE_ALREADY_LOCKED(n));
  assert(0 < n->listenport);
  assert(n->listenport == n->p->mainl->port);

  /* Assign endpoint id */
  epid.ip = n->listenip;
  epid.port = n->listenport;
  if (0 == localid) {
    if (UINT32_MAX == n->p->nextlocalid)
      fatal("Out of local identifiers");
    epid.localid = n->p->nextlocalid++;
  }
  else {
    epid.localid = localid;
  }

  /* Create endpoint */
  endpt = (endpoint*)calloc(1,sizeof(endpoint));
  endpt->p = (endpoint_private*)calloc(1,sizeof(endpoint_private));
  init_mutex(&endpt->p->mailbox.lock);
  pthread_cond_init(&endpt->p->mailbox.cond,NULL);
  endpt->epid = epid;
  endpt->p->type = strdup(type);
  endpt->p->data = arg;
  endpt->n = n;
  endpt->p->fun = fun;
  llist_append(&n->p->endpoints,endpt);

  /* Start thread */
  if (0 != pthread_attr_init(&attr))
    fatal("pthread_attr_init: %s",strerror(errno));

  if ((0 < stacksize) && (0 != pthread_attr_setstacksize(&attr,stacksize)))
    fatal("pthread_attr_setstacksize: %s",strerror(errno));

  if ((NULL == threadp) && (0 != pthread_attr_setdetachstate(&attr,PTHREAD_CREATE_DETACHED)))
    fatal("pthread_attr_setdetachstate: %s",strerror(errno));

  if (0 != pthread_create(&thread,&attr,endpoint_thread,endpt))
    fatal("pthread_create: %s",strerror(errno));

  if (0 != pthread_attr_destroy(&attr))
    fatal("pthread_attr_destroy: %s",strerror(errno));

  if (threadp)
    *threadp = thread;

  endpt->p->thread = thread;

  return epid;
}

endpointid node_add_thread(node *n, const char *type, endpoint_threadfun fun, void *arg,
                           pthread_t *threadp)
{
  endpointid epid;
  lock_node(n);
  epid = node_add_thread_locked(n,type,fun,arg,threadp,0,0);
  unlock_node(n);
  return epid;
}

endpointid node_add_thread2(node *n, const char *type, endpoint_threadfun fun, void *arg,
                            pthread_t *threadp, int localid, int stacksize)
{
  endpointid epid;
  lock_node(n);
  epid = node_add_thread_locked(n,type,fun,arg,threadp,localid,stacksize);
  unlock_node(n);
  return epid;
}

void node_stats(node *n, int *regconnections, int *listeners)
{
  connection *conn;
  listener *l;
  lock_node(n);
  for (conn = n->p->connections.first; conn; conn = conn->next) {
    if (conn->isreg)
      (*regconnections)++;
  }
  for (l = n->p->listeners.first; l; l = l->next) {
    if (l != n->p->mainl)
      (*listeners)++;
  }
  unlock_node(n);
}

int node_get_endpoints(node *n, const char *type, endpointid **epids)
{
  int count = 0;
  int i = 0;
  endpoint *endpt;

  lock_node(n);

  for (endpt = n->p->endpoints.first; endpt; endpt = endpt->next)
    if (!strcmp(endpt->p->type,type))
      count++;

  *epids = (endpointid*)calloc(count,sizeof(endpointid));

  for (endpt = n->p->endpoints.first; endpt; endpt = endpt->next)
    if (!strcmp(endpt->p->type,type))
      (*epids)[i++] = endpt->epid;

  unlock_node(n);
  return count;
}

static int endpoint_has_link(endpoint *endpt, endpointid epid)
{
  list *l;
  assert(NODE_ALREADY_LOCKED(endpt->n));
  for (l = endpt->p->outlinks; l; l = l->next)
    if (endpointid_equals((endpointid*)l->data,&epid))
      return 1;
  return 0;
}

static int link_ok_to_have(endpointid *epids, int count, endpointid epid)
{
  int i;
  for (i = 0; i < count; i++)
    if (!endpointid_isnull(&epids[i]) && endpointid_equals(&epids[i],&epid))
      return 1;
  return 0;
}

int endpoint_check_links(endpoint *endpt, endpointid *epids, int count)
{
  int r = 1;
  int i;
  list *l;
  lock_node(endpt->n);
  for (i = 0; i < count; i++) {
    if (!endpointid_isnull(&epids[i]) && !endpoint_has_link(endpt,epids[i]))
      r = 0;
  }
  for (l = endpt->p->outlinks; l; l = l->next) {
    endpointid *linkid = (endpointid*)l->data;
    if (!link_ok_to_have(epids,count,*linkid))
      r = 0;
  }
  unlock_node(endpt->n);
  return r;
}

void endpoint_link_locked(endpoint *endpt, endpointid to)
{
  endpointid *copy = (endpointid*)malloc(sizeof(endpointid));
  assert(NODE_ALREADY_LOCKED(endpt->n));
  if (endpoint_has_link(endpt,to))
    return;
  memcpy(copy,&to,sizeof(endpointid));
  list_push(&endpt->p->outlinks,copy);
  node_send_locked(endpt->n,endpt->epid.localid,to,MSG_LINK,&endpt->epid,sizeof(endpointid));
}

static void endpoint_list_remove(list **lptr, endpointid to)
{
  while (*lptr) {
    if (endpointid_equals(((endpointid*)(*lptr)->data),&to)) {
      list *old = *lptr;
      *lptr = (*lptr)->next;
      free(old->data);
      free(old);
      return;
    }
    lptr = &((*lptr)->next);
  }
}

void endpoint_unlink_locked(endpoint *endpt, endpointid to)
{
  assert(NODE_ALREADY_LOCKED(endpt->n));
  endpoint_list_remove(&endpt->p->outlinks,to);
  node_send_locked(endpt->n,endpt->epid.localid,to,MSG_UNLINK,&endpt->epid,sizeof(endpointid));
}

void endpoint_link(endpoint *endpt, endpointid to)
{
  /* FIXME: the endpointid included in the payload of the link and unlink messages is redundant.
     Just use the source instead. */
  assert(!endpointid_isnull(&to));
  lock_node(endpt->n);
  endpoint_link_locked(endpt,to);
  unlock_node(endpt->n);
}

void endpoint_unlink(endpoint *endpt, endpointid to)
{
  lock_node(endpt->n);
  endpoint_unlink_locked(endpt,to);
  unlock_node(endpt->n);
}

void endpoint_interrupt(endpoint *endpt) /* Can be called from native code */
{
  endpt->interrupt = 1;
  if (endpt->signal)
    pthread_kill(endpt->p->thread,SIGUSR1);
  if (IO_ID == endpt->epid.localid)
    node_notify(endpt->n);
}

static void endpoint_add_message(endpoint *endpt, message *msg)
{
  assert(NODE_ALREADY_LOCKED(endpt->n));
  if (MSG_LINK == msg->tag) {
    endpointid *epid = (endpointid*)malloc(sizeof(endpointid));
    memcpy(epid,msg->data,sizeof(endpointid));
    assert(sizeof(endpointid) == msg->size);
    assert(!endpointid_isnull(epid));
    list_push(&endpt->p->inlinks,epid);
    free(msg);
  }
  else if (MSG_UNLINK == msg->tag) {
    assert(sizeof(endpointid) == msg->size);
    endpoint_list_remove(&endpt->p->inlinks,*(endpointid*)msg->data);
    message_free(msg);
  }
  else {

    if (MSG_ENDPOINT_EXIT == msg->tag) {
      endpoint_exit_msg *eem = (endpoint_exit_msg*)msg->data;
      assert(sizeof(endpoint_exit_msg) == msg->size);
      endpoint_list_remove(&endpt->p->inlinks,eem->epid);
      endpoint_list_remove(&endpt->p->outlinks,eem->epid);
    }

    lock_mutex(&endpt->p->mailbox.lock);
    if (!endpt->p->closed) {
      llist_append(&endpt->p->mailbox,msg);
      endpoint_interrupt(endpt);
      pthread_cond_broadcast(&endpt->p->mailbox.cond);
    }
    unlock_mutex(&endpt->p->mailbox.lock);
  }
}

void endpoint_send_locked(endpoint *endpt, endpointid dest, int tag, const void *data, int size)
{
  node_send_locked(endpt->n,endpt->epid.localid,dest,tag,data,size);
}

void endpoint_send(endpoint *endpt, endpointid dest, uint32_t tag, const void *data, uint32_t size)
{
  lock_node(endpt->n);
  node_send_locked(endpt->n,endpt->epid.localid,dest,tag,data,size);
  unlock_node(endpt->n);
}

message *endpoint_receive(endpoint *endpt, int delayms)
{
  message *msg;
  lock_mutex(&endpt->p->mailbox.lock);

  if ((NULL == endpt->p->mailbox.first) && (0 != delayms) && !endpt->p->closed) {
    if (0 > delayms) {
      pthread_cond_wait(&endpt->p->mailbox.cond,&endpt->p->mailbox.lock);
    }
    else if (0 < delayms) {
      struct timeval now;
      struct timespec abstime;
      gettimeofday(&now,NULL);
      now = timeval_addms(now,delayms);
      abstime.tv_sec = now.tv_sec;
      abstime.tv_nsec = now.tv_usec * 1000;
      pthread_cond_timedwait(&endpt->p->mailbox.cond,&endpt->p->mailbox.lock,&abstime);
    }
  }

  msg = endpt->p->mailbox.first;
  if (NULL != msg)
    llist_remove(&endpt->p->mailbox,msg);
  unlock_mutex(&endpt->p->mailbox.lock);
  return msg;
}

int endpointid_equals(const endpointid *e1, const endpointid *e2)
{
  return ((e1->ip == e2->ip) && (e1->port == e2->port) && (e1->localid == e2->localid));
}

int endpointid_isnull(const endpointid *epid)
{
  return (0 == epid->localid);
}

void message_free(message *msg)
{
  free(msg);
}

int socketid_equals(const socketid *a, const socketid *b)
{
  return (endpointid_equals(&a->coordid,&b->coordid) && (a->sid == b->sid));
}

int socketid_isnull(const socketid *a)
{
  return (0 == a->sid);
}
