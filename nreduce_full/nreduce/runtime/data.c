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
 * $Id: data.c 621 2007-08-25 02:35:30Z pmkelly $
 *
 */

#ifdef HAVE_CONFIG_H
#include "config.h"
#endif

#include "src/nreduce.h"
#include "compiler/bytecode.h"
#include "compiler/source.h"
#include "runtime.h"
#include "messages.h"
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <assert.h>
#include <ctype.h>
#include <stdarg.h>
#include <math.h>
#include <errno.h>

reader read_start(task *tsk, const char *data, int size)
{
  reader rd;
  rd.tsk = tsk;
  rd.data = data;
  rd.size = size;
  rd.pos = 0;
  return rd;
}

static void read_bytes(reader *rd, void *data, int count)
{
  assert(rd->pos+count <= rd->size);
  memcpy(data,&rd->data[rd->pos],count);
  rd->pos += count;
}

static void read_check_tag(reader *rd, int tag)
{
  int got;
  read_bytes(rd,&got,sizeof(int));
  assert(got == tag);
}

static void read_tagged_bytes(reader *rd, int tag, void *data, int count)
{
  read_check_tag(rd,tag);
  read_bytes(rd,data,count);
}

void read_char(reader *rd, char *c)
{
  read_tagged_bytes(rd,CHAR_TAG,c,sizeof(char));
}

void read_int(reader *rd, int *i)
{
  read_tagged_bytes(rd,INT_TAG,i,sizeof(int));
}

void read_double(reader *rd, double *d)
{
  read_tagged_bytes(rd,DOUBLE_TAG,d,sizeof(double));
}

void read_string(reader *rd, char **s)
{
  int len;
  read_check_tag(rd,STRING_TAG);
  read_bytes(rd,&len,sizeof(int));
  assert(rd->pos+len <= rd->size);
  *s = (char*)malloc(len+1);
  memcpy(*s,&rd->data[rd->pos],len);
  (*s)[len] = '\0';
  rd->pos += len;
}

void read_binary(reader *rd, char *b, int len)
{
  int blen;
  read_check_tag(rd,BINARY_TAG);
  read_bytes(rd,&blen,sizeof(int));
  assert(blen == len);
  assert(rd->pos+len <= rd->size);
  memcpy(b,&rd->data[rd->pos],len);
  rd->pos += len;
}

void read_gaddr(reader *rd, gaddr *a)
{
  read_check_tag(rd,GADDR_TAG);
  read_bytes(rd,&a->tid,sizeof(int));
  read_bytes(rd,&a->lid,sizeof(int));
  if ((-1 != a->tid) || (-1 != a->lid))
    rd->tsk->naddrsread++;
}

void read_pntr(reader *rd, pntr *pout)
{
  /* TODO: determine if this refers to an object we already have a copy of, and return
     that object instead. (see Trinder96 2.3.2) */
  task *tsk = rd->tsk;
  int type;
  read_check_tag(rd,PNTR_TAG);
  read_int(rd,&type);

  if (CELL_NUMBER == type) {
    double d;
    read_double(rd,&d);
    set_pntrdouble(*pout,d);
    assert(!is_pntr(*pout));
  }
  else if (CELL_NIL == type) {
    *pout = tsk->globnilpntr;
  }
  else if (CELL_REMOTEREF == type) {
    gaddr addr;
    read_gaddr(rd,&addr);
    *pout = global_lookup(tsk,addr,NULL_PNTR);
  }
  else {
    gaddr addr;
    read_gaddr(rd,&addr);

    switch (type) {
    case CELL_AREF: {
      int index;
      int elemsize;
      int size;
      carray *carr;
      int i;

      read_int(rd,&index);
      read_int(rd,&elemsize);
      read_int(rd,&size);

      assert((1 == elemsize) || (sizeof(pntr) == elemsize));
      assert(MAX_ARRAY_SIZE >= size);
      assert(index < size);

      carr = carray_new(tsk,elemsize,size,NULL,NULL);
      carr->size = size;
      assert(carr->alloc == size);
      if (1 == elemsize)
        read_binary(rd,carr->elements,size);
      else
        for (i = 0; i < size; i++)
          read_pntr(rd,&((pntr*)carr->elements)[i]);
      read_pntr(rd,&carr->tail);
      make_aref_pntr(*pout,carr->wrapper,index);
      break;
    }
    case CELL_CONS: {
      cell *c;
      pntr head;
      pntr tail;

      read_pntr(rd,&head);
      read_pntr(rd,&tail);

      c = alloc_cell(tsk);
      c->type = CELL_CONS;
      c->field1 = head;
      c->field2 = tail;
      make_pntr(*pout,c);
      break;
    }
    case CELL_FRAME: {
      frame *fr = frame_new(tsk,1);
      int i;
      int count;
      int address;

      fr->c = alloc_cell(tsk);
      fr->c->type = CELL_FRAME;
      make_pntr(fr->c->field1,fr);
      make_pntr(*pout,fr->c);

      read_int(rd,&address);
      fr->instr = bc_instructions(tsk->bcdata)+address;

      count = fr->instr->expcount;
      for (i = 0; i < count; i++)
        read_pntr(rd,&fr->data[i]);
      break;
    }
    case CELL_CAP: {
      cap *cp = cap_alloc(tsk,1,0,0);
      cell *capcell;
      int i;

      capcell = alloc_cell(tsk);
      capcell->type = CELL_CAP;
      make_pntr(capcell->field1,cp);
      make_pntr(*pout,capcell);

      read_int(rd,&cp->arity);
      read_int(rd,&cp->address);
      read_int(rd,&cp->fno);
      read_int(rd,&cp->sl.fileno);
      read_int(rd,&cp->sl.lineno);
      read_int(rd,&cp->count);

      assert(MAX_CAP_SIZE > cp->count);

      cp->data = (pntr*)calloc(cp->count,sizeof(pntr));
      for (i = 0; i < cp->count; i++)
        read_pntr(rd,&cp->data[i]);
      break;
    }
    case CELL_SYSOBJECT:
      /* FIXME: these should't migrate... how to handle them? */
      fatal("FIXME: can't receive sysobject");
      break;
    default:
      fatal("read_pntr: got unexpected cell type %d",type);
    }

    if (NULL == addrhash_lookup(tsk,addr)) {
/*       printf("%d: read_pntr: Storing %s with addr %d.%d", */
/*              tsk->tid,cell_types[type],addr.tid,addr.lid); */
      add_global(tsk,addr,*pout);
    }
    else {
/*       printf("%d: read_pntr: Received %s that I already have",tsk->tid,cell_types[type]); */
    }

    /*
    if (CELL_AREF == type) {
      carray *carr = aref_array(*pout);
      int index = aref_index(*pout);
      if (1 == carr->elemsize) {
        printf("(index %d, %d chars)",index,carr->size);
      }
      else {
        pntr *elements = (pntr*)carr->elements;
        int values = 0;
        int i;
        for (i = 0; i < carr->size; i++) {
          pntr val = resolve_pntr(elements[i]);
          if (CELL_REMOTEREF != pntrtype(val))
            values++;
        }
        printf("(index %d %d/%d values)",index,values,carr->size);
      }
    }

    printf("\n");
    */
  }
}

void read_end(reader *rd)
{
  assert(rd->size == rd->pos);
}

array *write_start(void)
{
  return array_new(sizeof(char),0);
}

void write_tag(array *wr, int tag)
{
  array_append(wr,&tag,sizeof(int));
}

void write_char(array *wr, char c)
{
  write_tag(wr,CHAR_TAG);
  array_append(wr,&c,sizeof(char));
}

void write_int(array *wr, int i)
{
  write_tag(wr,INT_TAG);
  array_append(wr,&i,sizeof(int));
}

void write_double(array *wr, double d)
{
  write_tag(wr,DOUBLE_TAG);
  array_append(wr,&d,sizeof(d));
}

void write_string(array *wr, char *s)
{
  int len = strlen(s);
  write_tag(wr,STRING_TAG);
  array_append(wr,&len,sizeof(int));
  array_append(wr,s,len);
}

void write_binary(array *wr, const void *b, int len)
{
  write_tag(wr,BINARY_TAG);
  array_append(wr,&len,sizeof(int));
  array_append(wr,b,len);
}

void write_gaddr(array *wr, task *tsk, gaddr a)
{
  write_tag(wr,GADDR_TAG);
  array_append(wr,&a.tid,sizeof(int));
  array_append(wr,&a.lid,sizeof(int));
  add_gaddr(&tsk->inflight,a);
}

void write_ref(array *arr, task *tsk, pntr p)
{
  write_pntr(arr,tsk,p,1);
}

void write_pntr(array *arr, task *tsk, pntr p, int refonly)
{
  p = resolve_pntr(p);
  write_tag(arr,PNTR_TAG);

  if (CELL_NUMBER == pntrtype(p)) {
    write_int(arr,CELL_NUMBER);
    write_double(arr,pntrdouble(p));
  }
  else if (CELL_NIL == pntrtype(p)) {
    write_int(arr,CELL_NIL);
  }
  else if ((CELL_REMOTEREF == pntrtype(p)) && (0 <= pglobal(p)->addr.lid)) {
    write_int(arr,CELL_REMOTEREF);
    write_gaddr(arr,tsk,pglobal(p)->addr);
  }
  else if ((CELL_REMOTEREF == pntrtype(p)) ||
           (CELL_HOLE == pntrtype(p)) || /* FIXME: should attach waiter instead? */
           refonly) {
    global *glo = make_global(tsk,p);
    write_int(arr,CELL_REMOTEREF);
    write_gaddr(arr,tsk,glo->addr);
  }
  else {
    gaddr addr = global_addressof(tsk,p);
    write_int(arr,pntrtype(p));
    write_gaddr(arr,tsk,addr);

    switch (pntrtype(p)) {
    case CELL_AREF: {
      carray *carr = aref_array(p);
      int index = aref_index(p);
      int i;

      assert((1 == carr->elemsize) || (sizeof(pntr) == carr->elemsize));
      assert(MAX_ARRAY_SIZE >= carr->size);
      assert(index < carr->size);

      write_int(arr,index);
      write_int(arr,carr->elemsize);
      write_int(arr,carr->size);

      if (1 == carr->elemsize)
        write_binary(arr,carr->elements,carr->size);
      else
        for (i = 0; i < carr->size; i++)
          write_ref(arr,tsk,((pntr*)carr->elements)[i]);
      write_ref(arr,tsk,carr->tail);
      break;
    }
    case CELL_CONS: {
      write_ref(arr,tsk,get_pntr(p)->field1);
      write_ref(arr,tsk,get_pntr(p)->field2);
      break;
    }
    case CELL_FRAME: {
      frame *f = (frame*)get_pntr(get_pntr(p)->field1);
      int i;
      int count = f->instr->expcount;
      int address = f->instr-bc_instructions(tsk->bcdata);

      /* FIXME: if frame is sparked, schedule it; if frame is running, just send a ref (and
         add waiter?) */
      assert(STATE_NEW == f->state);

      write_int(arr,address);
      for (i = 0; i < count; i++)
        write_ref(arr,tsk,f->data[i]);
      break;
    }
    case CELL_CAP: {
      cap *cp = (cap*)get_pntr(get_pntr(p)->field1);
      int i;
      write_int(arr,cp->arity);
      write_int(arr,cp->address);
      write_int(arr,cp->fno);
      write_int(arr,cp->sl.fileno);
      write_int(arr,cp->sl.lineno);
      write_int(arr,cp->count);
      for (i = 0; i < cp->count; i++)
        write_ref(arr,tsk,cp->data[i]);
      break;
    }
    case CELL_SYSOBJECT:
      /* FIXME: these should't migrate... how to handle them? */
      fatal("FIXME: can't send sysobject");
      break;
    default:
      fatal("write: invalid pntr type %d",pntrtype(p));
      break;
    }
  }
}

void write_vformat(array *wr, task *tsk, const char *fmt, va_list ap)
{
  for (; *fmt; fmt++) {
    switch (*fmt) {
    case 'c':
      write_char(wr,(char)(va_arg(ap,int)));
      break;
    case 'i':
      write_int(wr,va_arg(ap,int));
      break;
    case 'd':
      write_double(wr,va_arg(ap,double));
      break;
    case 's':
      write_string(wr,va_arg(ap,char*));
      break;
    case 'b': {
      const void *b = va_arg(ap,const void*);
      int len = va_arg(ap,int);
      write_binary(wr,b,len);
      break;
    }
    case 'a':
      write_gaddr(wr,tsk,va_arg(ap,gaddr));
      break;
    case 'p':
      write_pntr(wr,tsk,va_arg(ap,pntr),0);
      break;
    case 'r':
      write_ref(wr,tsk,va_arg(ap,pntr));
      break;
    default:
      fatal("invalid write format character");
    }
  }
}

void write_format(array *wr, task *tsk, const char *fmt, ...)
{
  va_list ap;
  va_start(ap,fmt);
  write_vformat(wr,tsk,fmt,ap);
  va_end(ap);
}

void write_end(array *wr)
{
  array_free(wr);
}

void msg_send(task *tsk, int dest, int tag, char *data, int size)
{
  if (NULL != tsk->inflight) {
    int count = 0;
    list *l;
    for (l = tsk->inflight; l; l = l->next) {
      gaddr *addr = (gaddr*)l->data;
      array_append(tsk->inflight_addrs[dest],addr,sizeof(gaddr));
      count++;
    }
    array_append(tsk->unack_msg_acount[dest],&count,sizeof(int));
    list_free(tsk->inflight,free);
    tsk->inflight = NULL;
  }

  endpoint_send(tsk->endpt,tsk->idmap[dest],tag,data,size);
}

void msg_fsend(task *tsk, int dest, int tag, const char *fmt, ...)
{
  va_list ap;
  array *wr;

  wr = write_start();
  va_start(ap,fmt);
  write_vformat(wr,tsk,fmt,ap);
  va_end(ap);

  msg_send(tsk,dest,tag,wr->data,wr->nbytes);
  write_end(wr);
}
