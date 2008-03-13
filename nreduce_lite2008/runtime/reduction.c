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
 * $Id: reduction.c 613 2007-08-23 11:40:12Z pmkelly $
 *
 */

#ifdef HAVE_CONFIG_H
#include "config.h"
#endif

#include "src/nreduce.h"
#include "compiler/source.h"
#include "runtime/runtime.h"
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <assert.h>
#include <ctype.h>
#include <stdarg.h>
#include <math.h>

//// make a pointer (pntr), pointing to the scomb (sc) reference (sc ref).
//// the superconinator reference (scref) is stored in a cell, which is allocated from the task.
static pntr makescref(task *tsk, scomb *sc)
{
  cell *c = alloc_cell(tsk);	//// allocate a free cell to store (scref)
  pntr p;
  c->type = CELL_SCREF;
  make_pntr(c->field1,sc);
  make_pntr(p,c);
  return p;
}

//// snode: supercombinator (sc) body == sc->body
//// names: the argument names of the supercombinator (sc)
//// values: the values of corresponding arguments of the scomb (sc)
static pntr instantiate_scomb_r(task *tsk, scomb *sc, snode *source,
                                stack *names, pntrstack *values)
{
  cell *dest;
  pntr p;
  switch (source->type) {
  case SNODE_APPLICATION: {
  	//// create a cell based on snode
    dest = alloc_cell(tsk);	//// get a free cell from the task(tsk)
    dest->type = CELL_APPLICATION;
    dest->field1 = instantiate_scomb_r(tsk,sc,source->left,names,values);
    dest->field2 = instantiate_scomb_r(tsk,sc,source->right,names,values);
    make_pntr(p,dest);	//// make a pntr to the new cell
    return p;
  }
  case SNODE_SYMBOL: {
    int pos;
    for (pos = names->count-1; 0 <= pos; pos--) {
      if (!strcmp((char*)names->data[pos],source->name)) {
        dest = alloc_cell(tsk);
        dest->type = CELL_IND;
        dest->field1 = values->data[pos];
        make_pntr(p,dest);
        return p;
      }
    }
    fprintf(stderr,"Unknown variable: %s\n",source->name);
    exit(1);
    return NULL_PNTR;
  }
  case SNODE_LETREC: {
    int oldcount = names->count;
    letrec *rec;
    int i;
    pntr res;
    for (rec = source->bindings; rec; rec = rec->next) {
      cell *hole = alloc_cell(tsk);
      hole->type = CELL_HOLE;
      stack_push(names,(char*)rec->name);
      make_pntr(p,hole);
      pntrstack_push(values,p);
    }
    i = 0;
    for (rec = source->bindings; rec; rec = rec->next) {
      res = instantiate_scomb_r(tsk,sc,rec->value,names,values);
      assert(CELL_HOLE == get_pntr(values->data[oldcount+i])->type);
      get_pntr(values->data[oldcount+i])->type = CELL_IND;
      get_pntr(values->data[oldcount+i])->field1 = res;
      i++;
    }
    res = instantiate_scomb_r(tsk,sc,source->body,names,values);
    names->count = oldcount;
    values->count = oldcount;
    return res;
  }
  case SNODE_BUILTIN:
	//// create a cell indicating the build-in function, based on given snode
    dest = alloc_cell(tsk);
    dest->type = CELL_BUILTIN;
    make_pntr(dest->field1,source->bif);
    make_pntr(p,dest);
    return p;
  case SNODE_SCREF: {
    return makescref(tsk,source->sc);
  }
  case SNODE_NIL:
    return tsk->globnilpntr;
  case SNODE_NUMBER: {
    pntr p;
    set_pntrdouble(p,source->num);
    return p;
  }
  case SNODE_STRING:
    return string_to_array(tsk,source->value);
  default:
    abort();
    break;
  }
}

//// instatiate supercombinator (sc), with the arguments stored in stack (s)
pntr instantiate_scomb(task *tsk, pntrstack *s, scomb *sc)
{
  stack *names = stack_new();	//// stack (names) used to store the arguments names of the scomb (sc)
  pntrstack *values = pntrstack_new();
  pntr res;

  int i;
  for (i = 0; i < sc->nargs; i++) {	//// add all arguments to stack (names)
    int pos = s->count-1-i;
    assert(pos >= 0);
    stack_push(names,(char*)sc->argnames[i]);	//// push argument names into stack (names)
    pntrstack_push(values,pntrstack_at(s,pos));	//// push the values of arguments into stack (values)
  }

  res = instantiate_scomb_r(tsk,sc,sc->body,names,values);

  stack_free(names);
  pntrstack_free(values);
  return res;
}

//// reduce a single cell, whose pointer is (p)
static void reduce_single(task *tsk, pntrstack *s, pntr p)
{
  pntrstack_push(s,p);
  reduce(tsk,s);
  pntrstack_pop(s);
}

//// pntr stack (s) used to store CELL pointers, 
void reduce(task *tsk, pntrstack *s)
{
  int reductions = 0;	//// reduction counter, increase 1 when a redex is reduced
  pntr redex = s->data[s->count-1]; ////The data at the top of stack is the first redex to be sloved

  /* REPEAT */
  while (1) {
    int oldtop = s->count;    ////Save old top of the stack
    pntr target;

	//// Gabage collection performed once the task is allocated too much memory (bytes)
    if (tsk->alloc_bytes > COLLECT_THRESHOLD)
      local_collect(tsk);

    redex = s->data[s->count-1];
    reductions++;

    target = resolve_pntr(redex);

    /* 1. Unwind the spine until something other than an application node is encountered. */
    pntrstack_push(s,target);

    while (CELL_APPLICATION == pntrtype(target)) {
      target = resolve_pntr(get_pntr(target)->field1);
      pntrstack_push(s,target);
    }

    /* 2. Examine the cell at the tip of the spine */
    switch (pntrtype(target)) {

    case CELL_SCREF: {
      int i;
      int destno;
      pntr dest;
      pntr res;
      scomb *sc = (scomb*)get_pntr(get_pntr(target)->field1);	//// get the super combinator pntr which is stored in the CELL

      /* If there are not enough arguments to the supercombinator, we cannot instantiate it.
         The expression is in WHNF, so we can return. */
      //// check the number of arguments by examing the pntr stack (s), application cells pntrs have been added prior to this step
      //// the arguments cells are added in the (while(CELL_APPLICATION == ...))
      if (s->count-1-oldtop < sc->nargs) {
        /* TODO: maybe reduce the args that we do have? */
        s->count = oldtop;
        return;
      }

      destno = s->count-1-sc->nargs;    ////Destination  Number
      dest = pntrstack_at(s,destno);

      /* We have enough arguments present to instantiate the supercombinator */
      //// fetch argument from top to bottom (i--), note: s->count starts from 1, stack->data starts from 0
      //// the top of stack (s), which is s->count-1 is the pntr to the supercombinator itself, so the arguments start from (i-1)
      for (i = s->count-1; i >= s->count-sc->nargs; i--) {
        pntr arg;
        assert(i > oldtop);
        arg = pntrstack_at(s,i-1);	
        assert(CELL_APPLICATION == pntrtype(arg));
        //// replace stack (s) originally storing the supercombinator pntr and application pntr, with pntr to its arguments
        s->data[i] = get_pntr(arg)->field2; 
      }

      assert((CELL_APPLICATION == pntrtype(dest)) ||
             (CELL_SCREF == pntrtype(dest)));
	  
	  //// instantiate the supercombinator: make cells for snode, which are the supercombinator body
	  //// after instantiation, the graph is ready to reduced
      res = instantiate_scomb(tsk,s,sc);
      get_pntr(dest)->type = CELL_IND;
      get_pntr(dest)->field1 = res;

      s->count = oldtop;
      continue;
    }
    case CELL_CONS:
    case CELL_NIL:
    case CELL_NUMBER:
      /* The item at the tip of the spine is a value; this means the expression is in WHNF.
         If there are one or more arguments "applied" to this value then it's considered an
         error, e.g. caused by an attempt to pass more arguments to a function than it requires. */
      if (1 < s->count-oldtop) {
        printf("Attempt to apply %d arguments to a value\n",s->count-oldtop-1);
        exit(1);
      }

      s->count = oldtop;
      return;
      /* b. A built-in function. Check the number of arguments available. If there are too few
         arguments the expression is in WHNF so STOP. Otherwise evaluate any arguments required,
         execute the built-in function and overwrite the root of the redex with the result. */
    case CELL_BUILTIN: {

      int bif = (int)get_pntr(get_pntr(target)->field1);	//// get the build in function(bif) index
      int reqargs;		//// number of arguments required
      int strictargs;	//// number of must-have arguments
      int i;
      int strictok = 0;
      assert(bif);
      assert(NUM_BUILTINS > bif);

      reqargs = builtin_info[bif].nargs;
      strictargs = builtin_info[bif].nstrict;

	  //// make sure the number of arguments is sufficient
      if (s->count-1 < reqargs + oldtop) {
        fprintf(stderr,"Built-in function %s requires %d args; have only %d\n",
                builtin_info[bif].name,reqargs,s->count-1-oldtop);
        exit(1);
      }

      /* Replace application cells on stack with the corresponding arguments */
      //// note the corresponding arguments may also be application cells 
      for (i = s->count-1; i >= s->count-reqargs; i--) {
        pntr arg = pntrstack_at(s,i-1);
        assert(i > oldtop);
        assert(CELL_APPLICATION == pntrtype(arg));
        s->data[i] = get_pntr(arg)->field2;
      }

      /* Reduce arguments */
      //// reduce arguments from the outermost application cell, which closest to the tip of the graph
      for (i = 0; i < strictargs; i++)
        reduce_single(tsk,s,s->data[s->count-1-i]);

      /* Ensure all stack items are values (not indirection cells) */
      for (i = 0; i < strictargs; i++)
        s->data[s->count-1-i] = resolve_pntr(s->data[s->count-1-i]);

      /* Are any strict arguments not yet reduced? */
      for (i = 0; i < strictargs; i++) {
        pntr argval = resolve_pntr(s->data[s->count-1-i]);
        if (CELL_APPLICATION != pntrtype(argval)) {
          strictok++;
        }
        else {
          break;
        }
      }

      builtin_info[bif].f(tsk,&s->data[s->count-reqargs]);
      if (tsk->error)
        fatal("%s",tsk->error);
      s->count -= (reqargs-1);

      /* UPDATE */

      s->data[s->count-1] = resolve_pntr(s->data[s->count-1]);

      get_pntr(s->data[s->count-2])->type = CELL_IND;
      get_pntr(s->data[s->count-2])->field1 = s->data[s->count-1];

      s->count--;
      break;
    }
    default:
      fprintf(stderr,"Encountered %s\n",cell_types[pntrtype(target)]);
      abort();
      break;
    }

    s->count = oldtop;
  }
  /* END */
}

static void stream(task *tsk, pntr lst)
{
  tsk->streamstack = pntrstack_new();   ////Create new stack to store pointers to application nodes
  pntrstack_push(tsk->streamstack,lst);	
  while (tsk->streamstack->count > 0) {
    pntr p;
    reduce(tsk,tsk->streamstack);

    p = pntrstack_pop(tsk->streamstack);
    p = resolve_pntr(p);
    if (CELL_NIL == pntrtype(p)) {
      /* nothing */
    }
    else if (CELL_NUMBER == pntrtype(p)) {
      double d = pntrdouble(p);
      if ((d == floor(d)) && (0 < d) && (128 > d))
        printf("%c",(int)d);
      else
        printf("?");
    }
    else if (CELL_CONS == pntrtype(p)) {
      pntrstack_push(tsk->streamstack,get_pntr(p)->field2);
      pntrstack_push(tsk->streamstack,get_pntr(p)->field1);
    }
    else if (CELL_APPLICATION == pntrtype(p)) {
      fprintf(stderr,"Too many arguments applied to function\n");
      exit(1);
    }
    else {
      fprintf(stderr,"Bad cell type returned to printing mechanism: %s\n",cell_types[pntrtype(p)]);
      exit(1);
    }
  }
  pntrstack_free(tsk->streamstack);
  tsk->streamstack = NULL;
}

//// initialize the root pointer, pointing to a root cell, which stores the reference to "main" supercombinator 
void run_reduction(source *src)
{
  scomb *mainsc;        //// main supercombinator
  cell *app;			//// root cell
  pntr rootp;  			//// root pointer
  task *tsk;

  tsk = task_new(0, 0, NULL, 0);

  mainsc = get_scomb(src,"main");

  app = alloc_cell(tsk);	//// allocate first free cell to app
  app->type = CELL_APPLICATION;
  app->field1 = makescref(tsk,mainsc);	//// the field1 of app points to a cell storing refenrence to (mainsc)
  app->field2 = tsk->globnilpntr;
  make_pntr(rootp,app);

  stream(tsk,rootp);

  task_free(tsk);
}

