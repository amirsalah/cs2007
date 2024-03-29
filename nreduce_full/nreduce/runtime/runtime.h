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
 * $Id: runtime.h 679 2007-11-05 23:36:51Z pmkelly $
 *
 */

#ifndef _RUNTIME_H
#define _RUNTIME_H

#include "src/nreduce.h"
#include "compiler/source.h"
#include "compiler/bytecode.h"
#include "network/node.h"
#include <stdio.h>
#include <sys/time.h>
#include <netdb.h>
#include <pthread.h>
#include <assert.h>
#include <setjmp.h>
#include <signal.h>

struct task;
struct gaddr;

#define ENGINE_INTERPRETER 0
#define ENGINE_NATIVE      1
#define ENGINE_REDUCER     2

#define MAX_FRAME_SIZE   1024
#define MAX_CAP_SIZE     1024

/* Arithmetic operations */
#define B_ADD            0
#define B_SUBTRACT       1
#define B_MULTIPLY       2
#define B_DIVIDE         3
#define B_MOD            4

/* Numeric comparison */
#define B_EQ             5
#define B_NE             6
#define B_LT             7
#define B_LE             8
#define B_GT             9
#define B_GE             10

/* Bit operations */
#define B_BITSHL         11
#define B_BITSHR         12
#define B_BITAND         13
#define B_BITOR          14
#define B_BITXOR         15
#define B_BITNOT         16

/* Numeric functions */
#define B_SQRT           17
#define B_FLOOR          18
#define B_CEIL           19
#define B_NUMTOSTRING    20
#define B_STRINGTONUM1   21

/* Logical operations */
#define B_IF             22
#define B_AND            23
#define B_OR             24
#define B_NOT            25

/* Lists and arrays */
#define B_CONS           26
#define B_HEAD           27
#define B_TAIL           28
#define B_ARRAYSIZE      29
#define B_ARRAYSKIP      30
#define B_ARRAYPREFIX    31
#define B_STRCMP1        32    
#define B_TESTSTRING     33
#define B_TESTARRAY      34

/* Sequential/parallel directives */
#define B_SEQ            35
#define B_PAR            36
#define B_PARHEAD        37

/* Filesystem access */
#define B_OPENFD         38
#define B_READCHUNK      39
#define B_READDIR1       40
#define B_FEXISTS        41
#define B_FISDIR         42

/* Networking */
#define B_OPENCON        43
#define B_READCON        44
#define B_STARTLISTEN    45
#define B_ACCEPT         46

/* Terminal and network output */
#define B_NCHARS         47
#define B_PRINT          48
#define B_PRINTARRAY     49
#define B_PRINTEND       50

/* Other */
#define B_ERROR          51
#define B_GETOUTPUT      52
#define B_GENID          53
#define B_EXIT           54

#define B_ABS            55
#define B_JNEW           56
#define B_JCALL          57
#define B_ISCONS         58

#define B_CXSLT1         59
#define B_CACHE          60

#define B_CONNPAIR       61
#define B_MKCONN         62
#define B_SPAWN          63
#define B_COMPILE        64

#define B_ISSPACE        65

#define NUM_BUILTINS     66

#ifdef NDEBUG
#define checkcell(_c) (_c)
#else
#define checkcell(_c) ({ if (CELL_EMPTY == (_c)->type) \
                          fatal("access to free'd cell %p",(_c)); \
                        (_c); })
#endif

//#define celltype(_c) ((_c)->type)
#define celltype(_c) (checkcell(_c)->type)
#define pntrtype(p) (is_pntr(p) ? celltype(get_pntr(p)) : CELL_NUMBER)

typedef struct {
  unsigned int data[2];
} pntr;

#define CELL_EMPTY       0x00
#define CELL_APPLICATION 0x01  /* left: function (cell*)   right: argument (cell*) */
#define CELL_BUILTIN     0x02  /* left: bif (int)                                  */
#define CELL_CONS        0x03  /* left: head (cell*)       right: tail (cell*)     */
#define CELL_REMOTEREF   0x04  /*                                                  */
#define CELL_IND         0x05  /* left: tgt (cell*)                                */
#define CELL_SCREF       0x06  /* left: scomb (scomb*)                             */
#define CELL_AREF        0x07  /* left: array (cell*)                              */
#define CELL_HOLE        0x08  /*                                                  */
#define CELL_FRAME       0x09  /* left: frame (frame*)                             */
#define CELL_CAP         0x0A  /* left: cap (cap*)                                 */
#define CELL_NIL         0x0B  /*                                                  */
#define CELL_NUMBER      0x0C  /*                                                  */
#define CELL_SYMBOL      0x0D  /*                                                  */
#define CELL_SYSOBJECT   0x0E  /* left: obj (sysobject*)                           */
#define CELL_COUNT       0x0F

typedef struct cell {
  int type;
  int flags;
  pntr field1;
  pntr field2;
} cell;

#define PNTR_MASK  0xFFF80000
#define INDEX_MASK 0x0003FFFF
#define PNTR_VALUE 0xFFF00000
#define NULL_PNTR (*(pntr*)NULL_PNTR_BITS)
#define MAX_ARRAY_SIZE (1 << 18)

#define pfield1(__p) (get_pntr(__p)->field1)
#define pfield2(__p) (get_pntr(__p)->field2)
#define ppfield1(__p) (get_pntr(pfield1(__p)))
#define ppfield2(__p) (get_pntr(pfield2(__p)))
#define pglobal(__p) ((global*)ppfield1(__p))
#define pframe(__p) ((frame*)ppfield1(__p))
#define psysobject(__p) ((sysobject*)ppfield1(__p))
#define pntrdouble(__p) (*(double*)&(__p))
#define set_pntrdouble(__p,__val) ({ (*(double*)&(__p)) = (__val); })
#define is_pntr(__p) (((__p).data[1] & PNTR_MASK) == PNTR_VALUE)
#define make_pntr(__p,__c) { (__p).data[0] = (unsigned int)(__c); \
                             (__p).data[1] = PNTR_VALUE; }
#define make_aref_pntr(__p,__c,__i) { assert((__i) < MAX_ARRAY_SIZE); \
                            (__p).data[0] = (unsigned int)(__c); \
                            (__p).data[1] = (PNTR_VALUE | (__i)); }
#define aref_index(__p) ((__p).data[1] & ~PNTR_MASK)
#define aref_array(__p) ((carray*)get_pntr(get_pntr(__p)->field1))
#define get_pntr(__p) (assert(is_pntr(__p)), ((cell*)(*((unsigned int*)&(__p)))))
#define pntrequal(__a,__b) (((__a).data[0] == (__b).data[0]) && ((__a).data[1] == (__b).data[1]))

#define is_nullpntr(__p) (is_pntr(__p) && ((cell*)1 == get_pntr(__p)))
#ifndef NO_STATEMENT_EXPRS
#define INLINE_RESOLVE_PNTR
#endif

#ifdef INLINE_RESOLVE_PNTR
#define resolve_pntr(x) ({ pntr __x = (x);        \
                           while (CELL_IND == pntrtype(__x)) \
                             __x = get_pntr(__x)->field1; \
                           __x; })
#else
pntr resolve_pntr(pntr p);
#endif


#define check_global(_g) (assert(!(_g)->freed))

typedef struct waitqueue {
  struct frame *frames;
  list *fetchers;
} waitqueue;

typedef void (*builtin_f)(struct task *tsk, pntr *argstack);

#define ALWAYS_VALUE 1
#define MAYBE_UNEVAL 0

#define ALWAYS_TRUE 1
#define MAYBE_FALSE 0

#define IMPURE 0
#define PURE   1

typedef struct builtin {
  char *name;
  int nargs;
  int nstrict;
  int reswhnf;
  int restrue;
  int pure;
  builtin_f f;
} builtin;

#define SYSOBJECT_FILE           0
#define SYSOBJECT_CONNECTION     1
#define SYSOBJECT_LISTENER       2
#define SYSOBJECT_JAVA           3
#define SYSOBJECT_COUNT          4

typedef struct {
  endpointid managerid;
  unsigned int jid;
} javaid;

typedef struct sysobject {
  int type;
  int fd;
  char *hostname;
  int port;
  int connected;
  char *buf;
  int len;
  int closed;
  int error;
  char errmsg[ERRMSG_MAX+1];
  pntr listenerso;
  struct sysobject *newso;
  struct task *tsk;
  socketid sockid;
  javaid jid;
  cell *c;
  pntr p;
  int newclosed;
  int newerror;
  int frameids[FRAMEADDR_COUNT];
} sysobject;

typedef struct carray {
  int alloc;
  int size;
  int elemsize;
  pntr tail;
  cell *wrapper;
  int nchars;
  int pad;
  char elements[];
} carray;

typedef struct pntrstack {
  int alloc;
  int count;
  pntr *data;
  int limit;
} pntrstack;

typedef struct psentry {
  pntr p;
  int next;
} psentry;

#define phash2(_p) ((unsigned char)(_p[0] + _p[1] + _p[2] + _p[3] + _p[4] + _p[5] + _p[6] + _p[7]))
#define phash(_p) phash2(((unsigned char*)&(_p)))

typedef struct pntrset {
  int map[256];
  psentry *data;
  int alloc;
  int count;
} pntrset;

typedef struct pmentry {
  pntr p;
  snode *s;
  char *name;
  pntr val;
  int next;
} pmentry;

typedef struct pntrmap {
  int map[256];
  pmentry *data;
  int alloc;
  int count;
} pntrmap;

typedef struct cap {
  int arity;
  int address;
  int fno; /* temp */
  sourceloc sl;
  int count;

  pntr *data;
} cap;

/* frame states */
#define STATE_NEW       0
#define STATE_SPARKED   1
#define STATE_RUNNING   2
#define STATE_BLOCKED   3
#define STATE_DONE      4

typedef struct frame {
  const instruction *instr;
  waitqueue wq;

  cell *c;

  int state;
  int resume;

  pntr *retp;

  struct frame *freelnk;
  struct frame *waitlnk;
  struct frame *rnext;

  int pad;
  struct global *waitglo;
  pntr data[0];
} frame;

typedef struct frameq {
  frame *first;
  frame *last;
} frameq;

typedef struct procstats {
  int *usage;
  int ninstrs;

  /* Memory allocation */
  int cell_allocs;
  int array_allocs;
  int array_resizes;
  int frame_allocs;
  int cap_allocs;
  int gcs;

  /* Functions */
  int *funcalls;
  int *frames;
  int *caps;
} procstats;

typedef struct gaddr {
  int tid;
  int lid;
} gaddr;

/* For "entry items" (globally visible objects that this task owns):
     tsk = this task id
     id = (tsk)-unique id for the object
     p = pointer to the actual object

  For "exit items" (globally visible objects on another task that we have a ref to):
     tsk = the task (not us) that owns the object
     id = (tsk)-unique id for the object      -- may be -1
     p = pointer to a REMOTE_REF object

  For "replicas" (local copies we've made of objects that other tasks own):
     tsk = the task (not us) that owns the object
     id = (tsk)-unique id for the object
     p = pointer to our copy of the object
*/
typedef struct global {
  gaddr addr;
  pntr p;

  int fetching;
  waitqueue wq;
  int flags;
  struct global *pntrnext;
  struct global *addrnext;
} global;

/* task */

typedef struct block {
  struct block *next;
  int pad;
  cell values[BLOCK_SIZE];
} block;

typedef struct frameblock {
  struct frameblock *next;
  int pad;
  char mem[FRAMEBLOCK_SIZE];
} frameblock;

typedef struct ioframe {
  frame *f;
  int freelnk;
} ioframe;

typedef struct task {

  /* general */
  procstats stats;
  pntr argsp;

  /* bytecode */
  char *bcdata;
  int bcsize;
  int *bcaddr_to_fno;

  /* communication */
  int tid;
  int groupsize;
  node *n;
  socketid out_sockid;
  sysobject *out_so;
  endpoint *endpt;
  endpointid *idmap;

  /* distributed memory management */
  global **pntrhash;
  global **addrhash;
  int naddrsread;
  array **distmarks;
  endpointid gc;
  int gciter;
  array **inflight_addrs;
  array **unack_msg_acount;

  /* I/O requests */
  int ioalloc;
  int iocount;
  ioframe *ioframes;
  int iofree;
  int netpending;

  /* runtime info */
  int done;
  frame **runptr;
  frame *rtemp;
  int nextlid;
  int *gcsent;
  list *inflight;
  char *error;
  sourceloc errorsl;
  frame *freeframe;
  unsigned int nextid;

  /* memory */
  block *blocks;
  frameblock *frameblocks;
  cell *freeptr;
  pntr globnilpntr;
  pntr globtruepntr;
  pntr *strings;
  int nstrings;
  pntrstack *streamstack;
  pntrstack *markstack;
  int indistgc;
  int newcellflags;
  int inmark;
  int alloc_bytes;
  int framesize;
  int framesperblock;

  /* startup info (used by manager) */
  int haveidmap;
  int threadrunningfds[2];

  /* runtime execution info (used by interpreter and native) */
  struct timeval nextfish;
  struct timeval nextgc;
  int newfish;
  int usr1setup;

  /* interpreter */
  jmp_buf jbuf;

  /* native execution */
  unsigned char **bpaddrs[2];
  void *code;
  int codesize;
  int *cpu_to_bcaddr;
  void *bcend_addr;
  void *trap_addr;
  void *caperror_addr;
  void *argerror_addr;
  void *normal_esp;
  int native_finished;
  unsigned char bcbackup[2][5];
  int trap_pending;
  int trap_bcaddr;

  /* tracing */
  pntr trace_root;
  source *trace_src;
  int trace_steps;
  char *trace_dir;
  int trace_type;
  int tracing;

} task;

task *task_new(int tid, int groupsize, const char *bcdata, int bcsize, array *args, node *n,
               socketid out_sockid, endpointid *epid);
void task_free(task *tsk);
void print_profile(task *tsk);

global *pntrhash_lookup(task *tsk, pntr p);
global *addrhash_lookup(task *tsk, gaddr addr);
void pntrhash_add(task *tsk, global *glo);
void addrhash_add(task *tsk, global *glo);
void pntrhash_remove(task *tsk, global *glo);
void addrhash_remove(task *tsk, global *glo);

global *add_global(task *tsk, gaddr addr, pntr p);
pntr global_lookup_existing(task *tsk, gaddr addr);
pntr global_lookup(task *tsk, gaddr addr, pntr val);
global *make_global(task *tsk, pntr p);
gaddr global_addressof(task *tsk, pntr p);

void add_gaddr(list **l, gaddr addr);
void remove_gaddr(task *tsk, list **l, gaddr addr);

#define add_frame_queue llist_prepend
#define add_frame_queue_end llist_append
#define remove_frame_queue llist_remove

void spark_frame(task *tsk, frame *f);
void unspark_frame(task *tsk, frame *f);
void run_frame(task *tsk, frame *f);
void run_frame_toend(task *tsk, frame *f);

void check_runnable(task *tsk);
void block_frame(task *tsk, frame *f);
void unblock_frame(task *tsk, frame *f);
void unblock_frame_toend(task *tsk, frame *f);
#define done_frame(tsk,_f) \
{ \
  assert(STATE_RUNNING == (_f)->state); \
  assert((_f) == *tsk->runptr); \
 \
  *tsk->runptr = (_f)->rnext; \
  (_f)->rnext = NULL; \
  (_f)->state = STATE_DONE; \
}
int frame_fno(task *tsk, frame *f);

int set_error(task *tsk, const char *format, ...);

/* client */

typedef struct {
  int rc;
} output_arg;

void start_launcher(node *n, const char *bcdata, int bcsize,
                    endpointid *managerids, int count, pthread_t *threadp, socketid out_sockid,
                    int argc, const char **argv);
void output_thread(node *n, endpoint *endpt, void *arg);
int do_client(char *initial_str, int argc, const char **argv);

/* data */

typedef struct reader {
  task *tsk;
  const char *data;
  int size;
  int pos;
} reader;

#define CHAR_TAG   0x44912234
#define INT_TAG    0xA492BC09
#define DOUBLE_TAG 0x44ABC92F
#define STRING_TAG 0x93EB1123
#define BINARY_TAG 0x559204A3
#define GADDR_TAG  0x85113B1C
#define PNTR_TAG   0xE901FA12

reader read_start(task *tsk, const char *data, int size);
void read_char(reader *rd, char *c);
void read_int(reader *rd, int *i);
void read_double(reader *rd, double *d);
void read_string(reader *rd, char **s);
void read_binary(reader *rd, char *b, int len);
void read_gaddr(reader *rd, gaddr *a);
void read_pntr(reader *rd, pntr *pout);
void read_end(reader *rd);

array *write_start(void);
void write_tag(array *wr, int tag);
void write_char(array *wr, char c);
void write_int(array *wr, int i);
void write_double(array *wr, double d);
void write_string(array *wr, char *s);
void write_binary(array *wr, const void *b, int len);
void write_gaddr(array *wr, task *tsk, gaddr a);
void write_ref(array *arr, task *tsk, pntr p);
void write_pntr(array *arr, task *tsk, pntr p, int refonly);
void write_vformat(array *wr, task *tsk, const char *fmt, va_list ap);
void write_format(array *wr, task *tsk, const char *fmt, ...);
void write_end(array *wr);

void msg_send(task *tsk, int dest, int tag, char *data, int size);
void msg_fsend(task *tsk, int dest, int tag, const char *fmt, ...);

/* reduction */

pntr instantiate_scomb(task *tsk, pntrstack *s, scomb *sc);
void reduce(task *h, pntrstack *s);
void run_reduction(source *src, char *trace_dir, int trace_type, array *args);

/* builtin */

void invalid_arg(task *tsk, pntr arg, int bif, int argno, int type);
void invalid_binary_args(task *tsk, pntr *argstack, int bif);

carray *carray_new(task *tsk, int dsize, int alloc, carray *oldarr, cell *usewrapper);
void carray_append(task *tsk, carray **arr, const void *data, int totalcount, int dsize);
pntr data_to_list(task *tsk, const char *data, int size, pntr tail);

pntr socketid_string(task *tsk, socketid sockid);
pntr mkcons(task *tsk, pntr head, pntr tail);

int get_builtin(const char *name);
pntr string_to_array(task *tsk, const char *str);
int array_to_string(pntr refpntr, char **str);
int flatten_list(pntr refpntr, pntr **data);

/* worker */

int standalone(const char *bcdata, int bcsize, int argc, const char **argv);
int string_to_mainchordid(node *n, const char *str, endpointid *out);
int worker(int port, const char *initial_str);

/* cell */

cell *alloc_cell(task *tsk);
sysobject *new_sysobject(task *tsk, int type);
sysobject *find_sysobject(task *tsk, const socketid *sockid);
void free_global(task *tsk, global *glo);
void free_cell_fields(task *tsk, cell *v);

void clear_marks(task *tsk, short bit);
void mark_roots(task *tsk, short bit);
void sweep(task *tsk, int all);
void mark_global(task *tsk, global *glo, short bit);
void local_collect(task *tsk);
void memusage(task *tsk, int *cells, int *bytes, int *alloc, int *connections, int *listeners);

frame *frame_new(task *tsk, int addalloc);
#define frame_free(tsk,_f) \
{ \
  assert(tsk->done || ((NULL == _f->retp) && (NULL == _f->c)));       \
  assert(tsk->done || (STATE_DONE == _f->state) || (STATE_NEW == _f->state)); \
  assert(tsk->done || (NULL == _f->wq.frames)); \
  assert(tsk->done || (NULL == _f->wq.fetchers)); \
  if (_f->wq.fetchers) \
    list_free(_f->wq.fetchers,free); \
 \
  _f->freelnk = tsk->freeframe; \
  tsk->freeframe = _f; \
}

cap *cap_alloc(task *tsk, int arity, int address, int fno);
void cap_dealloc(cap *c);

/* interpreter */

void eval_remoteref(task *tsk, frame *f2, pntr p);
void resume_fetchers(task *tsk, waitqueue *wq, pntr obj);
void print_task_sourceloc(task *tsk, FILE *f, sourceloc sl);
void add_pending_mark(task *tsk, gaddr addr);
void spark(task *tsk, frame *f);
void cap_error(task *tsk, pntr cappntr);
void handle_error(task *tsk);
void make_item_frame(task *tsk, frame *runnable, int expcount, int pos);
void interpreter_sigfpe(int sig, siginfo_t *ino, void *uc1);
void interpreter_thread(node *n, endpoint *endpt, void *arg);

/* memory */

pntrstack *pntrstack_new(void);
void pntrstack_push(pntrstack *s, pntr p);
pntr pntrstack_at(pntrstack *s, int pos);
pntr pntrstack_pop(pntrstack *s);
pntr pntrstack_top(pntrstack *s);
void pntrstack_free(pntrstack *s);

void pntrstack_grow(int *alloc, pntr **data, int size);

/* graph */

pntr graph_replace(task *tsk, pntr root, pntr old, pntr new);
int graph_size(pntr root);

pntrset *pntrset_new();
void pntrset_free(pntrset *ps);
void pntrset_add(pntrset *ps, pntr p);
int pntrset_contains(pntrset *ps, pntr p);
void pntrset_clear(pntrset *ps);

pntrmap *pntrmap_new();
void pntrmap_free(pntrmap *pm);
int pntrmap_add(pntrmap *pm, pntr p, snode *s, char *name, pntr val);
int pntrmap_lookup(pntrmap *pm, pntr p);

/* tosyntax */

snode *graph_to_syntax(source *src, pntr p);
void print_graph(source *src, pntr p);

/* trace */

#define TRACE_NORMAL    0
#define TRACE_LANDSCAPE 1

typedef void (*dotfun)(FILE *f, pntr p, pntr arg);
void dot_graph(const char *prefix, int number, pntr root, int doind,
               dotfun fun, pntr arg, const char *msg, int landscape);
void trace_step(task *tsk, pntr target, int allapps, const char *format, ...);

/* native */

void native_sigusr1(int sig, siginfo_t *ino, void *uc1);
void native_sigfpe(int sig, siginfo_t *ino, void *uc1);
void native_sigsegv(int sig, siginfo_t *ino, void *uc1);
void native_compile(char *bcdata, int bcsize, array *cpucode, task *tsk);

/* taskman */

void start_manager(node *n);

/* java */

int get_callinfo(task *tsk, pntr obj, pntr method, char **targetname, char **methodname);
int serialise_args(task *tsk, pntr args, array *arr);
pntr decode_java_response(task *tsk, const char *str, endpointid source);
void send_jcmd(endpoint *endpt, int ioid, int oneway, const char *data, int cmdlen);
void java_thread(node *n, endpoint *endpt, void *arg);

#ifndef BUILTINS_C
extern const builtin builtin_info[NUM_BUILTINS];
#endif

#ifndef MEMORY_C
extern unsigned char NULL_PNTR_BITS[8];
extern const char *cell_types[CELL_COUNT];
extern const char *sysobject_types[SYSOBJECT_COUNT];
extern const char *frame_states[5];
#endif

#ifndef TASK_C
extern pthread_key_t task_key;
extern int engine_type;
#endif

#endif
