#include "src/nreduce.h"
#include "compiler/source.h"
#include "compiler/util.h"
#include "runtime/builtins.h"
#include "runtime.h"
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <assert.h>
#include <ctype.h>
#include <stdarg.h>
#include <math.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <errno.h>
#include <dirent.h>

#include "runtime/rngs.h"
#include "runtime/rvgs.h"
#include <zzip/zzip.h>

#ifndef O_BINARY
#define O_BINARY 0
#endif

//// increase the memory space allocated to the string (str)
char* string_mkroom(char *str, const int newSize)
{
	return realloc(str, newSize);
}

//// read a file contents from either a archive of real directory
void b_zzip_read(task *tsk, pntr *argstack)
{
	char *fileName;
	pntr p = argstack[0];
	int badtype;

	CHECK_ARG(0, CELL_CONS);
	if((badtype = array_to_string(p, &fileName)) >= 0){
		set_error(tsk, "error1: argument is not a string (contains non-char: %s)", cell_types[badtype]);
		return;
	}
	
	ZZIP_FILE* fp = zzip_open (fileName, O_RDONLY|O_BINARY);

    if (! fp){
   		perror (fileName);
    }
    
    int bufSize = 2, blockSize = 1024, numBlocks = 1;
    char buf[bufSize];
    int n, counter = 0;
    char *contents = (char *)calloc(blockSize, sizeof(char));
    
    /* read chunks of bufSize bytes into buf and concatenate them into previous string */
    while( (n = (zzip_read(fp, buf, bufSize-1))) > 0){
    	counter++;
    	
    	if(counter == 1){
//    		strcpy(contents, buf);
			strncat(contents, buf, bufSize-1);
			bufSize = 21;
    	}else{
    		int originalSize = strlen(contents);
    		if( ((blockSize*numBlocks) - (originalSize + 1)) < bufSize){
    			numBlocks++;
    			contents = string_mkroom(contents, blockSize*numBlocks);
    		}

    		strncat(contents, buf, bufSize-1);
//    		printf("%s\n\n\n", contents);
    	}
    	buf[n] = '\0';
    }
    
    argstack[0] = string_to_array(tsk, contents);
	
	zzip_close(fp);
}
