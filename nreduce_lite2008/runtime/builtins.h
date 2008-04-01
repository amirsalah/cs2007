#ifndef BUILTINS_H_
#define BUILTINS_H_

#endif /*BUILTINS_H_*/

static const char *numnames[4] = {"first", "second", "third", "fourth"};

static unsigned char NAN_BITS[8] = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xF8, 0xFF };

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
