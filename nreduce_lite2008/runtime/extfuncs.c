#include "extfuncs.h"

const extfunc extfunc_info[NUM_EXTFUNCS];

int get_extfunc(const char *name)
{
  int i;
  for (i = 0; i < NUM_EXTFUNCS; i++)
    if (!strcmp(name,extfunc_info[i].name))
      return i;
  return -1;
}

/******** util functions *********/
//// similar to b_cons(), which returns a pntr pointing to a cell
pntr make_cons(task *tsk, pntr head, pntr tail)
{
	pntr newCellPntr;
	cell *res = alloc_cell(tsk);
	res->type = CELL_CONS;
	res->field1 = head;
	res->field2 = tail;

	make_pntr(newCellPntr,res);
	return newCellPntr;
}


pntr head(task *tsk, pntr pCell){
    pntr cellHead;
    if(pntrtype(pCell) == CELL_CONS){
        cellHead = get_pntr(pCell)->field1;
    }else {
        set_error(tsk,"head: expected cons, got %s",cell_types[pntrtype(pCell)]);
    }
    
    return cellHead;
}

pntr tail(task *tsk, pntr pCell){
    pntr cellTail;
    
    if(pntrtype(pCell) == CELL_CONS){
        cellTail = get_pntr(pCell)->field2;
    }else {
        set_error(tsk,"tail: expected cons, got %s",cell_types[pntrtype(pCell)]);
    }
    
    return cellTail;
}
//// get the last cell from a list
cell* get_last_cell(task *tsk, pntr *list)
{
	pntr tail;
	cell *lastCell;
	
	if( pntrtype(*list) != CELL_CONS ){
		return NULL;
	}else{
		lastCell = get_pntr(*list);
		tail = resolve_pntr(get_pntr(*list)->field2);
		while(!pntrequal(tail, tsk->globnilpntr)){
			lastCell = get_pntr(tail);
			tail = resolve_pntr(lastCell->field2);
		}
		return lastCell;
	}
}

//// concatenate 2 lists, which are formed with (cons * (cons * ... nil))
pntr connect_lists(task *tsk, pntr *list1, pntr *list2)
{
	cell *lastCell = get_last_cell(tsk, list1);
	
	make_pntr(lastCell->field2, get_pntr(*list2));
	return *list1;
}


//// increase the memory space allocated to the string (str)
char* string_mkroom(char *str, const int newSize)
{
	return realloc(str, newSize);
}

//// my version of data_to_list
/*
pntr data_to_list(task *tsk, const char *data, int size, pntr tail)
{
	int i;
	pntr preList;
	
	if(size <= 0){
		return tail;
	}
	
	cell *lastCell = alloc_cell(tsk);
	lastCell->type = CELL_CONS;
	set_pntrdouble(lastCell->field1, data[size-1]);
	lastCell->field2 = tsk->globnilpntr;
	make_pntr(preList, lastCell);
	
	for(i=size-2; i>=0; i--){
		cell *ch = alloc_cell(tsk);
    	ch->type = CELL_CONS;
    	set_pntrdouble(ch->field1,data[i]);
    	ch->field2 = preList;
    	make_pntr(preList, ch);
	}
	
	return preList;
}
*/


/****************** external functions ************************/

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

//// Check if the given file is a real directory or a Zip-archive
static void b_zzip_dir_real(task *tsk, pntr *argstack)
{
	char *fileName;
	int badtype;
	pntr p = argstack[0];
	int dirtype;
	
	CHECK_ARG(0, CELL_CONS);
	if((badtype = array_to_string(p, &fileName)) >= 0){
		set_error(tsk, "error1: argument is not a string (contains non-char: %s)", cell_types[badtype]);
		return;
	}
	
	//// the file to be ckecked exists, then we check it
	static const char* ext[] = { "", ".exe", ".EXE", 0 };
	ZZIP_DIR* dir = zzip_opendir_ext_io (fileName, ZZIP_PREFERZIP, ext, 0);

    if(dir){
    	if(zzip_dir_real(dir)){
    		dirtype = 1;
//	   		printf("%s is a directory.\n", fileName);
    	}else{
    		dirtype = 0;
//    		printf("%s is a zip-archive.\n", fileName);
    	}
//    	zzip_dir_close(dir);
    }else{
    	//// file failed to be open
    	dirtype = -1;
    }
	setnumber(&argstack[0], dirtype);
	
	return;
}

static void b_zzip_version(task *tsk, pntr *argstack)
{
//	char *version;
}

//// read the directory entries of given dir/archive
static void b_zzip_read_dirent(task *tsk, pntr *argstack)
{
	char *fileName;
	pntr p = argstack[0];
	int badtype;
	
	CHECK_ARG(0, CELL_CONS);
	if((badtype = array_to_string(p, &fileName)) >= 0){
		set_error(tsk, "error1: argument is not a string (contains non-char: %s)", cell_types[badtype]);
		return;
	}

    ZZIP_DIR * dir;
    ZZIP_DIRENT * d;
  
    dir = zzip_opendir(fileName);
    if (! dir){
    	fprintf (stderr, "did not open %s: ", fileName);
    	set_error(tsk, "error1: could not handle file: %s", fileName);
		return;
    }

    char *singleFileName;
    char *compressionType;
    int fSize = 20;
    char fileSize[fSize];
    char compressedSize[fSize];
    pntr pSingleFileName, pCompressionType, pFileSize, pCompressedSize;
    pntr preList, singleList;
    int counter = 0;
    
	/* read each dir entry, a list for each file */
	while ((d = zzip_readdir (dir))){
		counter++;
		/* orignal size / compression-type / compression-ratio / filename */
		singleFileName = d->d_name;
		pSingleFileName = string_to_array(tsk, singleFileName);	//// convert the string to cons list
		
//		sprintf(compressionType, "%s ", zzip_compr_str(d->d_compr)); //// NOTE: executing this func will change the tsk->steamstack, very weird
																	//// NOTE: overflow caused here
		compressionType = (char *)zzip_compr_str(d->d_compr);
		pCompressionType = string_to_array(tsk, compressionType);
		
//		snprintf(fileSize, 5, "%d ", d->st_size);	//// NOTE: executing this func will change the tsk->steamstack, very weird
		format_double(fileSize, fSize, d->st_size);
		pFileSize = string_to_array(tsk, fileSize);

//		sprintf(compressedSize, "%d ", d->d_csize);
		format_double(compressedSize, fSize, d->d_csize);
		pCompressedSize = string_to_array(tsk, compressedSize);
		
//		printf("cell type: %s \t", cell_type(preList));
		//// link the cons lists to form a new list
//		singleList = connect_lists(tsk, &pSingleFileName, &pCompressionType);
//		singleList = connect_lists(tsk, &singleList, &pFileSize);
//		singleList = connect_lists(tsk, &singleList, &pCompressedSize);
		
		//// make cons from the last element to the beginning element
		singleList = make_cons(tsk, pCompressedSize, tsk->globnilpntr);
		singleList = make_cons(tsk, pFileSize, singleList);
		singleList = make_cons(tsk, pCompressionType, singleList);
		singleList = make_cons(tsk, pSingleFileName, singleList);
		
		 
		if(counter == 1){
			preList = make_cons(tsk, singleList, tsk->globnilpntr);
//			printf("cell type: %s \t", cell_type(preList));
		}else{
			preList = make_cons(tsk, singleList, preList);
//			printf("cell type: %s \n", cell_type(preList));
		}
	}
	
	argstack[0] = preList;
//	printf("cell type: %s \n", cell_type(argstack[0]));
}

/*********************** Test drawRectangles1 ***************************************/
typedef struct Rectangle Rectangle;
typedef struct Color Color;

struct Rectangle {
    int width;
    int height;
    Color *col;
};

struct Color {
    int red;
    int blue;
    int green;
};

int drawRectangles(char *userName, double xPos, int yPos, Rectangle *rect){
    //Check the arguments

    if(strcmp(userName, "SmAxlL")){
        return 0;
    }
    
    if(xPos != 1.5){
        return 0;
    }
    
    if(yPos != 2){
        return 0;
    }
    
    if( (rect->width != 10) | (rect->height != 15) | (rect->col->red != 100) | (rect->col->blue != 150)){
        return 0;
    }
    
    return 1;
}

// Declare constructor and destructor fucntions

Rectangle *newRectangle();
void free_Rectangle(Rectangle *r);
Color *new_Color();
void free_Color(Color *col);

Rectangle *new_Rectangle() {
    Rectangle *rect = malloc(sizeof(Rectangle));
    
    return rect;
}

void free_Rectangle(Rectangle *r) {
    free_Color(r->col);
    free(r);
}

Color *new_Color(){
    Color *col = malloc(sizeof(Color));
    
    return col;
}

void free_Color(Color *col){
    free(col);
}


static void b_drawRectangles1(task *tsk, pntr *argstack)
{
    pntr val1 = argstack[3]; // userName
    pntr val2 = argstack[2]; // xPos
    pntr val3 = argstack[1]; // yPos
    pntr val4 = argstack[0]; // Rectangle
    
    int badtype;
    
    char *userName;
    double xPos;
    int yPos;
    Rectangle *rect = new_Rectangle();

    
    int result;

    /* Check validity of each parameter */
    CHECK_ARG(3, CELL_CONS);
    CHECK_ARG(2, CELL_NUMBER);
    CHECK_ARG(1, CELL_NUMBER);
    CHECK_ARG(0, CELL_CONS);
    
    /* initialize all the struct */
    if( (badtype = array_to_string(val1,&userName)) > 0) {
        set_error(tsk,"string: argument is not a string (contains non-char: %s)",
                  cell_types[badtype]);
        return;
      }
    
    xPos = pntrdouble(val2);
    yPos = pntrdouble(val3);
    
    //    int type = pntrtype(val4_val1);
    //    pntr i = get_pntr(val4)->field1;
    //    set_pntrdouble(p, number);

//    pntr t = resolve_pntr(get_pntr(val4)->field2);
    int type;
    
    pntr val4_val1 = resolve_pntr(head(tsk, val4));
    rect->width = pntrdouble(val4_val1);
    
    pntr val4_val2 = resolve_pntr(head(tsk, resolve_pntr(tail(tsk, val4))));
    rect->height = pntrdouble(val4_val2);
    
    /* if val4_val3 is also a number, then pntr val4_val3 = head(tail(tail val4)) */
    Color *col = new_Color();
    rect->col = col;
    
    pntr col_val = resolve_pntr(head (tsk, resolve_pntr(tail (tsk, resolve_pntr((tail(tsk, val4))) ))));  // root pntr for Color struct
//    type = pntrtype(col_val);
    pntr col_val1 = resolve_pntr(head(tsk, col_val));
//    type = pntrtype(col_val1);
    col->red = pntrdouble(col_val1);
    
    pntr col_val2 = resolve_pntr(head(tsk, resolve_pntr(tail (tsk, col_val))));
    col->blue = pntrdouble(col_val2);
    
    pntr col_val3 = resolve_pntr(head(tsk, resolve_pntr(tail (tsk, resolve_pntr(tail (tsk, col_val))))));
    col->green = pntrdouble(col_val3);
    
    result = drawRectangles(userName, xPos, yPos, rect);
    
    setnumber(&argstack[0], result);
    
}


const extfunc extfunc_info[NUM_EXTFUNCS] = {
//// zzip functions
{ "zzip_dir_real",  1, 1, b_zzip_dir_real  },

//// zzip version
{ "zzip_version",     0, 0, b_zzip_version    },
{ "zzip_read_dirent", 1, 1, b_zzip_read_dirent},
{ "zzip_read",        1, 1, b_zzip_read	      },
{ "drawRectangles1",  4, 4, b_drawRectangles1  },
};
