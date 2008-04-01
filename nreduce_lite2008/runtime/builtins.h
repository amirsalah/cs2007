#ifndef BUILTINS_H_
#define BUILTINS_H_

#endif /*BUILTINS_H_*/

#include "src/nreduce.h"
#include "compiler/source.h"
#include "compiler/util.h"
#include "runtime.h"
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <assert.h>
#include <ctype.h>
#include <stdarg.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <errno.h>
#include <dirent.h>


void setnumber(pntr *cptr, double val);

//// check if the type of specified argument (by number) is the same as _type
#define CHECK_ARG(_argno,_type) {                                       \
    if ((_type) != pntrtype(argstack[(_argno)])) {                      \
      invalid_arg(tsk,argstack[(_argno)],0,(_argno),(_type));           \
      return;                                                           \
    }                                                                   \
  }

#define CHECK_NUMERIC_ARGS(bif)                                         \
  if ((CELL_NUMBER != pntrtype(argstack[1])) ||                         \
      (CELL_NUMBER != pntrtype(argstack[0]))) {                         \
    invalid_binary_args(tsk,argstack,bif);                              \
    return;                                                             \
  }