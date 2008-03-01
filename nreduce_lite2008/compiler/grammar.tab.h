/* A Bison parser, made by GNU Bison 2.3.  */

/* Skeleton interface for Bison's Yacc-like parsers in C

   Copyright (C) 1984, 1989, 1990, 2000, 2001, 2002, 2003, 2004, 2005, 2006
   Free Software Foundation, Inc.

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2, or (at your option)
   any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 51 Franklin Street, Fifth Floor,
   Boston, MA 02110-1301, USA.  */

/* As a special exception, you may create a larger work that contains
   part or all of the Bison parser skeleton and distribute that work
   under terms of your choice, so long as that work isn't itself a
   parser generator using the skeleton or a modified version thereof
   as a parser skeleton.  Alternatively, if you modify or redistribute
   the parser skeleton itself, you may (at your option) remove this
   special exception, which will cause the skeleton and the resulting
   Bison output files to be licensed under the GNU General Public
   License without this special exception.

   This special exception was added by the Free Software Foundation in
   version 2.2 of Bison.  */

/* Tokens.  */
#ifndef YYTOKENTYPE
# define YYTOKENTYPE
   /* Put the tokens into the symbol table, so that GDB and other debuggers
      know about them.  */
   enum yytokentype {
     SYMBOL = 258,
     LAMBDA = 259,
     LAMBDAWORD = 260,
     NIL = 261,
     IMPORT = 262,
     LETREC = 263,
     IN = 264,
     FUNCTION = 265,
     LET = 266,
     IF = 267,
     ELSE = 268,
     FOREACH = 269,
     APPEND = 270,
     EQ = 271,
     NE = 272,
     LE = 273,
     GE = 274,
     SHL = 275,
     SHR = 276,
     CONDAND = 277,
     CONDOR = 278,
     INTEGER = 279,
     DOUBLE = 280,
     STRING = 281
   };
#endif
/* Tokens.  */
#define SYMBOL 258
#define LAMBDA 259
#define LAMBDAWORD 260
#define NIL 261
#define IMPORT 262
#define LETREC 263
#define IN 264
#define FUNCTION 265
#define LET 266
#define IF 267
#define ELSE 268
#define FOREACH 269
#define APPEND 270
#define EQ 271
#define NE 272
#define LE 273
#define GE 274
#define SHL 275
#define SHR 276
#define CONDAND 277
#define CONDOR 278
#define INTEGER 279
#define DOUBLE 280
#define STRING 281




#if ! defined YYSTYPE && ! defined YYSTYPE_IS_DECLARED
typedef union YYSTYPE
#line 49 "grammar.y"
{
  struct snode *c;
  char *s;
  int i;
  double d;
  struct list *l;
  struct letrec *lr;
}
/* Line 1489 of yacc.c.  */
#line 110 "grammar.tab.h"
	YYSTYPE;
# define yystype YYSTYPE /* obsolescent; will be withdrawn */
# define YYSTYPE_IS_DECLARED 1
# define YYSTYPE_IS_TRIVIAL 1
#endif

extern YYSTYPE yylval;

#if ! defined YYLTYPE && ! defined YYLTYPE_IS_DECLARED
typedef struct YYLTYPE
{
  int first_line;
  int first_column;
  int last_line;
  int last_column;
} YYLTYPE;
# define yyltype YYLTYPE /* obsolescent; will be withdrawn */
# define YYLTYPE_IS_DECLARED 1
# define YYLTYPE_IS_TRIVIAL 1
#endif

extern YYLTYPE yylloc;
