#include "modules.h"
#define prelude_module \
"len1 !total lst =\n"\
"(if lst\n"\
"  (len1 (+ total 1) (tail lst))\n"\
"  total)\n"\
"\n"\
"len lst =\n"\
"(len1 0 lst)\n"\
"\n"\
"skip n lst =\n"\
"(if (<= n 0)\n"\
"  lst\n"\
"  (skip (- n 1) (tail lst)))\n"\
"\n"\
"item n lst = (head (skip n lst))\n"\
"\n"\
"prefix n lst =\n"\
"(if (<= n 0)\n"\
"  nil\n"\
"  (cons (head lst) (prefix (- n 1) (tail lst))))\n"\
"\n"\
"sub !start !count lst =\n"\
"(prefix count (skip start lst))\n"\
"\n"\
"append a b =\n"\
"(if a\n"\
"  (cons (head a) (append (tail a) b))\n"\
"  b)\n"\
"\n"\
"appendn n a b =\n"\
"(if (== n 2)\n"\
"  (append a b)\n"\
"  (appendn (- n 1) (append a b)))\n"\
"\n"\
"append1 a b =\n"\
"(append a (cons b nil))\n"\
"\n"\
"forcelist1 lst =\n"\
"(if lst\n"\
"  (seq (head lst) (forcelist1 (tail lst)))\n"\
"  nil)\n"\
"\n"\
"forcelist lst =\n"\
"(seq (forcelist1 lst) lst)\n"\
"\n"\
"listn n a =\n"\
"(if (== n 1)\n"\
"  (cons a nil)\n"\
"  (consn n (cons a nil)))\n"\
"\n"\
"consn n a b =\n"\
"(if (== n 2)\n"\
"  (append1 a b)\n"\
"  (consn (- n 1) (append1 a b)))\n"\
"\n"\
"strcmp a b =\n"\
"(strcmpcons a b)\n"\
"\n"\
"strcmpcons a b =\n"\
"(if a\n"\
"  (if b\n"\
"    (if (== (head a) (head b))\n"\
"      (strcmp (tail a) (tail b))\n"\
"      (- (head a) (head b)))\n"\
"    1)\n"\
"  (if b\n"\
"    (- 0 1)\n"\
"    0))\n"\
"\n"\
"\n"\
"streq a b = (== (strcmp a b) 0)\n"\
"\n"\
"map f lst =\n"\
"(if lst\n"\
"  (cons (f (head lst))\n"\
"    (map f (tail lst)))\n"\
"  nil)\n"\
"\n"\
"filter f lst =\n"\
"(if lst\n"\
"  (if (f (head lst))\n"\
"    (cons (head lst) (filter f (tail lst)))\n"\
"    (filter f (tail lst)))\n"\
"  nil)\n"\
"\n"\
"reverse1 lst rev =\n"\
"(if lst\n"\
"  (reverse1 (tail lst) (cons (head lst) rev))\n"\
"  rev)\n"\
"\n"\
"reverse lst =\n"\
"(reverse1 lst nil)\n"\
"\n"\
"\n"\
"stringtonum str =\n"\
"(stringtonum1 (forcelist str))\n"\
"\n"\
"error str =\n"\
"(error1 (forcelist str))\n"\
" "
const module_info module_defs[2] = {
{ "prelude", "(module) prelude.elc", prelude_module },
{ 0, 0, 0 },
};
const module_info *modules = module_defs;
