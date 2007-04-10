/* 
 * The image loader library
 * Just loads JPEGS and PNGs. Does no format conversion.
 * Based on JPEG and PNG library example code
 */

#include <stdlib.h>
#include <stdio.h>
#include <memory.h>
#include <sys/stat.h>
#include <assert.h>

#include <png.h>
#include <jpeglib.h>
//#include <setjmp.h>

#include "ImageLoader.h"

#define max(a,b) ((a)<(b)?(b):(a))

#define DebugMsg printf

// The image structure
typedef struct Image
{
	PixelType			fType;
	uint32_t			fWidth;
	uint32_t			fHeight;
	uint32_t			fChannels;
	uint8_t*			fData;
} Image;

// Private function declarations

static int32_t gcd( int32_t a, int32_t b );
static void convertFloatToFraction( float f, int32_t mult, int32_t& num, int32_t& den );
static void reallocate( Image *img );
static void setPixelType( PixelType p, Image *img );
static void setSize( uint32_t w, uint32_t h, Image *img );

static int loadImage( const char *filename, int flip, Image* img );
static int readInJPEG( const uint8_t* buf, uint32_t len, Image* img, int fast, float scale, int flip );
static int readInPNG( const uint8_t* buf, uint32_t len, Image* img, int flip );

// The image functions

Image *imageNew( )
{
	Image *img =  (Image *) malloc( sizeof( Image ) );

	img->fType = kNumPixelFormats;
	img->fWidth = 0;
	img->fHeight = 0;
	img->fChannels = 0;
	img->fData = NULL;

	return img;
}

Image *imageNewMem( const PixelType pt, const uint32_t w, const uint32_t h )
{
	Image *img = imageNew();
	setPixelType( pt, img );
	setSize( w, h, img );

	return img;
}

void imageDestroy( Image *img )
{
	img->fType = kNumPixelFormats;
	img->fWidth = 0;
	img->fHeight = 0;
	img->fChannels = 0;

	if ( img->fData != NULL )
	{
		free( img->fData );
		img->fData = NULL;
	}

	if ( img != NULL )
	{
		free( img );
		img = NULL;
	}
}

int imageLoad( const char *filename, Image* img )
{
	loadImage( filename, 0, img );
}

int imageLoadFlip( const char *filename, Image* img )
{
	loadImage( filename, 1, img );
}

// The accessor functions...

uint32_t	imageWidth( const Image *img )	{ return img->fWidth; }
uint32_t	imageHeight( const Image *img )	{ return img->fHeight; }
uint32_t	imageNumChannels( const Image *img ) { return img->fChannels; }
PixelType	imagePixelType( const Image *img ) { return img->fType; }
uint8_t		*imageData( const Image *img) { return img->fData; }

void imageGetPixel( const uint32_t x, const uint32_t y, uint8_t pixel[], const Image *img )
{
	uint32_t nc = img->fChannels;

	for ( uint32_t c = 0; c < nc; c++ )
		pixel[ c ] = img->fData[ nc * ( x + y * img->fWidth ) + c ];	
}

void imageSetPixel( const uint32_t x, const uint32_t y, uint8_t pixel[], const Image *img )
{
	uint32_t nc = img->fChannels;

	for ( uint32_t c = 0; c < nc; c++ )
		img->fData[ nc * ( x + y * img->fWidth ) + c ] = pixel[ c ];
}

static int loadImage( const char *filename, int flip, Image* img )
{
	int res = 0;
	FILE* input = fopen( filename, "rb" );
	if ( input == NULL )
		return 0;
	
	uint8_t*	buffer	= NULL;
	uint8_t*	realBuf	= NULL;
	
	// Read in the whole file.
	// Make sure the buffer is 16 byte aligned for libjpeg
	struct stat		inputStats;
	fstat( fileno( input ), &inputStats );
	realBuf = (uint8_t *) malloc( inputStats.st_size + 1 + 16 );
	buffer = (uint8_t *)( ( (intptr_t) realBuf ) & ~0xF );
	if ( buffer < realBuf )
		buffer += 16;
	
	fread( buffer, sizeof( uint8_t ), inputStats.st_size, input );
	buffer[ inputStats.st_size ] = 0;
	
	fclose( input );

	if ( ( buffer[ 0 ] == 0xFF ) && ( buffer[ 1 ] == 0xD8 ) )// && ( buf[ 2 ] == 0xFF ) && ( buf[ 3 ] == 0xE0 ) )
	{
		res = readInJPEG( buffer, inputStats.st_size, img, 1, 1.0f, flip );
	}
	else if (	( buffer[ 0 ] == 0x89 ) && ( buffer[ 1 ] == 'P'  ) && ( buffer[ 2 ] == 'N'  ) && ( buffer[ 3 ] == 'G' ) &&
				( buffer[ 4 ] == '\r' ) && ( buffer[ 5 ] == '\n' ) && ( buffer[ 6 ] == 0x1A ) && ( buffer[ 7 ] == '\n' ) )
	{
		res = readInPNG( buffer, inputStats.st_size, img, flip );
	}
	else
	{
		DebugMsg( "Unknown magic number (0x %X %X %X %X)!\n",
			buffer[ 0 ],
			buffer[ 1 ],
			buffer[ 2 ],
			buffer[ 3 ] );
	}

	free( realBuf );

	return res;
}

// Now we start getting into weird JPEG library stuff...
// Ignore if at all possible

typedef struct _mem_source_mgr {
	struct jpeg_source_mgr pub;	    // base fields
	//NOTE: this "pub" field must come first.  The internal methods
	//think that this really is only a jpeg_source_mgr struct and will
	//read off the fields from the start.  We cast to a mem_source_mgr
	//inside the "callback" functions...
	
	JOCTET* buffer;		    // memory buffer
	unsigned int buf_len;           // size of buffer
} mem_source_mgr;
typedef mem_source_mgr* mem_src_ptr;

// function to initialise the memory source
static void jpeg_memory_source( j_decompress_ptr cinfo, const char* buf_ptr, unsigned int buf_len );

// Callback functions for the source manager:
static void init_source(j_decompress_ptr cinfo)
{
}

static boolean fill_input_buffer (j_decompress_ptr cinfo)
{
	return TRUE;
}

static void skip_input_data ( j_decompress_ptr cinfo, long num_bytes )
{
	// simply adjust pointers as all data is already in the buffer:
	mem_src_ptr src;
	src = (mem_src_ptr) cinfo->src;
	
	src->pub.next_input_byte += (size_t) num_bytes;
	src->pub.bytes_in_buffer -= (size_t) num_bytes;
}

static void term_source (j_decompress_ptr cinfo)
{
}

// Initialiser function:
static void jpeg_memory_source( j_decompress_ptr cinfo, const char* buf_ptr, unsigned int buf_len )
{
	mem_src_ptr src;
	
	if (cinfo->src == NULL) {	/* first time for this JPEG object? */
	// allocate memory using the libjpeg memory "backend"
	// JPOOL_PERMANENT gets storage that lasts as long as the JPEG object (cinfo)
	cinfo->src = (struct jpeg_source_mgr *)
		(*cinfo->mem->alloc_small) ((j_common_ptr) cinfo,
			JPOOL_PERMANENT,
			sizeof(mem_source_mgr));
	}
	
	src = (mem_src_ptr) cinfo->src;                // a "down"cast
	src->pub.init_source = init_source;
	src->pub.fill_input_buffer = fill_input_buffer;
	src->pub.skip_input_data = skip_input_data;
	src->pub.resync_to_restart = jpeg_resync_to_restart; /* use default method */
	src->pub.term_source = term_source;
	
	src->buffer = (JOCTET*) buf_ptr;
	src->buf_len = buf_len;
	
	src->pub.next_input_byte = src->buffer;
	src->pub.bytes_in_buffer = buf_len;
}


/*
 * ERROR HANDLING:
 *
 * The JPEG library's standard error handler (jerror.c) is divided into
 * several "methods" which you can override individually.  This lets you
 * adjust the behavior without duplicating a lot of code, which you might
 * have to update with each future release.
 *
 * Our example here shows how to override the "error_exit" method so that
 * control is returned to the library's caller when a fatal error occurs,
 * rather than calling exit() as the standard error_exit method does.
 *
 * We use C's setjmp/longjmp facility to return control.  This means that the
 * routine which calls the JPEG library must first execute a setjmp() call to
 * establish the return point.  We want the replacement error_exit to do a
 * longjmp().  But we need to make the setjmp buffer accessible to the
 * error_exit routine.  To do this, we make a private extension of the
 * standard JPEG error handler object.  (If we were using C++, we'd say we
 * were making a subclass of the regular error handler.)
 *
 * Here's the extended error handler struct:
 */

struct my_error_mgr {
  struct jpeg_error_mgr pub;    /* "public" fields */

  jmp_buf setjmp_buffer;        /* for return to caller */
};

typedef struct my_error_mgr * my_error_ptr;

/*
 * Here's the routine that will replace the standard error_exit method:
 */

static void my_error_exit (j_common_ptr cinfo)
{
	/* cinfo->err really points to a my_error_mgr struct, so coerce pointer */
	my_error_ptr myerr = (my_error_ptr) cinfo->err;
	
	/* Always display the message. */
	/* We could postpone this until after returning, if we chose. */
	(*cinfo->err->output_message) (cinfo);
	
	/* Return control to the setjmp point */
	longjmp(myerr->setjmp_buffer, 1);
}

static void my_output_message (j_common_ptr cinfo)
{
	char buffer[JMSG_LENGTH_MAX];
	
	/* Create the message */
	(*cinfo->err->format_message) (cinfo, buffer);
	
	DebugMsg( "%s\n", buffer );
}

/******************** JPEG DECOMPRESSION SAMPLE INTERFACE *******************/

/* This half of the example shows how to read data from the JPEG decompressor.
 * It's a bit more refined than the above, in that we show:
 *   (a) how to modify the JPEG library's standard error-reporting behavior;
 *   (b) how to allocate workspace using the library's memory manager.
 *
 * Just to make this example a little different from the first one, we'll
 * assume that we do not intend to put the whole image into an in-memory
 * buffer, but to send it line-by-line someplace else.  We need a one-
 * scanline-high JSAMPLE array as a work buffer, and we will let the JPEG
 * memory manager allocate it for us.  This approach is actually quite useful
 * because we don't need to remember to deallocate the buffer separately: it
 * will go away automatically when the JPEG object is cleaned up.
 */

/*
 * Sample routine for JPEG decompression.  We assume that the source file name
 * is passed in.  We want to return 1 on success, 0 on error.
 */
#if defined(Darwin) 
//#define USE_JPEG_RGBA
#endif
static int readInJPEG( const uint8_t* buf, uint32_t len, Image* img, int fast, float scale, int flip )
{
	/* This struct contains the JPEG decompression parameters and pointers to
	 * working space (which is allocated as needed by the JPEG library).
	 */
	struct jpeg_decompress_struct cinfo;
	
	/* We use our private extension JPEG error handler.
	 * Note that this struct must live as long as the main JPEG parameter
	 * struct, to avoid dangling-pointer problems.
	 */
	struct my_error_mgr jerr;
	/* More stuff */
	JSAMPARRAY buffer;            /* Output row buffer */
	int row_stride;               /* physical row width in output buffer */
	
	/* Step 1: allocate and initialize JPEG decompression object */
	
	/* We set up the normal JPEG error routines, then override error_exit. */
	cinfo.err = jpeg_std_error( &jerr.pub );
	jerr.pub.error_exit = my_error_exit;
	jerr.pub.output_message = my_output_message;
	
	/* Establish the setjmp return context for my_error_exit to use. */
	if ( setjmp( jerr.setjmp_buffer ) )
	{
		/* If we get here, the JPEG code has signaled an error.
		 * We need to clean up the JPEG object, close the input file, and return.
		 */
		jpeg_destroy_decompress( &cinfo );
		
		return 0;
	}
	
	/* Now we can initialize the JPEG decompression object. */
	jpeg_create_decompress( &cinfo );
	
	/* Step 2: specify data source (eg, a file) */
	jpeg_memory_source( &cinfo, (const char *) buf, len );
	
	/* Step 3: read file parameters with jpeg_read_header() */
	
	(void) jpeg_read_header( &cinfo, TRUE );
	/* We can ignore the return value from jpeg_read_header since
	 *   (a) suspension is not possible with the stdio data source, and
	 *   (b) we passed TRUE to reject a tables-only JPEG file as an error.
	 * See libjpeg.doc for more info.
	 */
	
	/* Step 4: set parameters for decompression */
	
	/* In this example, we don't need to change any of the defaults set by
	 * jpeg_read_header(), so we do nothing here.
	 */
	switch ( cinfo.out_color_space )
	{
		case JCS_CMYK:
			{
				setPixelType( kRGB, img );
				cinfo.out_color_space = JCS_RGB;
			}
			break;
		case JCS_GRAYSCALE:
			{
				setPixelType( kGrey, img );
			}
			break;
		case JCS_RGB:
			{
//#ifndef _WIN32
//				if ( img.getPixelType() == Image::kARGB )
//					cinfo.out_color_space = JCS_RGBA;
//				else
//#endif
					setPixelType( kRGB, img );
			}
			break;
		case JCS_YCbCr:
			{
				setPixelType( kYUV, img );
			}
			break;
		case JCS_YCCK:
			{
				setPixelType( kRGB, img );
				cinfo.out_color_space = JCS_RGB;
			}
			break;
		default:
			assert( 0 );
	}
	
	if ( fast )
		cinfo.dct_method = JDCT_IFAST; //JDCT_ISLOW; //JDCT_IVEC; //JDCT_IFAST; //JDCT_FLOAT; 
	
	int32_t numerator;
	int32_t denominator;
	convertFloatToFraction( scale, 1000, numerator, denominator );
	cinfo.output_width  = (JDIMENSION)( cinfo.output_width  * numerator / denominator );
	cinfo.output_height = (JDIMENSION)( cinfo.output_height * numerator / denominator );
	cinfo.scale_num		= numerator;
	cinfo.scale_denom	= denominator;
	
	/* Step 5: Start decompressor */
	
	(void) jpeg_start_decompress( &cinfo );
	/* We can ignore the return value since suspension is not possible
	 * with the stdio data source.
	 */
	
	
	/* We may need to do some setup of our own at this point before reading
	 * the data.  After jpeg_start_decompress() we have the correct scaled
	 * output image dimensions available, as well as the output colormap
	 * if we asked for color quantization.
	 * In this example, we need to make an output work buffer of the right size.
	 */
	/* JSAMPLEs per row in output buffer */
	row_stride = cinfo.output_width * cinfo.output_components;
	/* Make a one-row-high sample array that will go away when done with image */
	buffer = (*cinfo.mem->alloc_sarray)( (j_common_ptr) &cinfo, JPOOL_IMAGE, row_stride, 1 );
	
	/* Step 6: while (scan lines remain to be read) */
	/*           jpeg_read_scanlines(...); */
	
	setSize( cinfo.output_width, cinfo.output_height, img );
	
	/* Here we use the library's state variable cinfo.output_scanline as the
	 * loop counter, so that we don't have to keep track ourselves.
	 */
//	uint32_t j = 0;
	while ( cinfo.output_scanline < cinfo.output_height )
	{
		/* jpeg_read_scanlines expects an array of pointers to scanlines.
		 * Here the array is only one element long, but you could ask for
		 * more than one scanline at a time if that's more convenient.
		 */
		int32_t rowsRead = jpeg_read_scanlines( &cinfo, buffer, 1 );
		assert( rowsRead == 1 );
		
		uint8_t* dst = imageData( img );
		if ( flip )
		{
			memcpy( &dst[ ( cinfo.output_height - cinfo.output_scanline ) * cinfo.output_width * cinfo.output_components ],
				buffer[ 0 ],
				cinfo.output_width * cinfo.output_components );
		}
		else
		{
			memcpy( &dst[ ( cinfo.output_scanline - 1 ) * cinfo.output_width * cinfo.output_components ],
				buffer[ 0 ],
				cinfo.output_width * cinfo.output_components );
		}
	}
	
	/* Step 7: Finish decompression */
	
	(void) jpeg_finish_decompress( &cinfo );
	/* We can ignore the return value since suspension is not possible
	 * with the stdio data source.
	 */
	
	/* Step 8: Release JPEG decompression object */
	
	/* This is an important step since it will release a good deal of memory. */
	jpeg_destroy_decompress( &cinfo );
	
	return 1;
}

// And now PNG loading stuff

struct png_buffer
{
	char*	buf;
	size_t	len;
	size_t	pos;
};
typedef struct png_buffer png_buffer;

static void png_read_from_memory( png_structp png_ptr, png_bytep data, png_size_t length )
{
	png_buffer* p = (png_buffer* ) png_get_io_ptr( png_ptr );
	memcpy( data, p->buf + p->pos, length );
	p->pos += length;
	
	assert( p->pos <= p->len );
}

int readInPNG( const uint8_t* buf, uint32_t len, Image* img, int flip )
{
	png_structp		png_ptr;
	png_infop		info_ptr;
	png_bytepp		image_rows;
	unsigned int	sig_read		= 0;
	
	/* Create and initialize the png_struct with the desired error handler
	* functions.  If you want to use the default stderr and longjump method,
	* you can supply NULL for the last three parameters.  We also supply the
	* the compiler header file version, so that we know if the application
	* was compiled with a compatible version of the library.  REQUIRED
	*/
	png_ptr = png_create_read_struct(	PNG_LIBPNG_VER_STRING,
										(png_voidp) NULL,
										(png_error_ptr) NULL,
										(png_error_ptr) NULL );
	
	if (png_ptr == NULL)
	{
		return 0;
	}
	
	/* Allocate/initialize the memory for image information.  REQUIRED. */
	info_ptr = png_create_info_struct(png_ptr);
	if (info_ptr == NULL)
	{
		png_destroy_read_struct(&png_ptr, (png_infopp)NULL, (png_infopp)NULL);
		return 0;
	}
	
	/* Set error handling if you are using the setjmp/longjmp method (this is
	* the normal method of doing things with libpng).  REQUIRED unless you
	* set up your own error handlers in the png_create_read_struct() earlier.
	*/
	
	if (setjmp(png_jmpbuf(png_ptr)))
	{
		/* Free all of the memory associated with the png_ptr and info_ptr */
		png_destroy_read_struct(&png_ptr, &info_ptr, (png_infopp)NULL);
		
		/* If we get here, we had a problem reading the file */
		return 0;
	}
	
	/* One of the following I/O initialization methods is REQUIRED */
	/* Set up the input control if you are using standard C streams */
	png_buffer	theBuf;
	theBuf.buf = (char *) buf;
	theBuf.len = len;
	theBuf.pos = 0;
	png_set_read_fn( png_ptr, &theBuf, png_read_from_memory );
	
	/* If we have already read some of the signature */
	png_set_sig_bytes(png_ptr, sig_read);
	
	/*
	* If you have enough memory to read in the entire image at once,
	* and you need to specify only transforms that can be controlled
	* with one of the PNG_TRANSFORM_* bits (this presently excludes
	* dithering, filling, setting background, and doing gamma
	* adjustment), then you can read the entire image (including
	* pixels) into the info structure with this call:
	*/
	png_read_png(png_ptr, info_ptr, PNG_TRANSFORM_IDENTITY, NULL);
	
	/* At this point you have read the entire image */
	image_rows = png_get_rows( png_ptr, info_ptr );
	
	assert( info_ptr->bit_depth == 8 );
	
	uint32_t comp = 0;
	switch ( info_ptr->color_type )
	{
		case PNG_COLOR_TYPE_RGB_ALPHA:
			setPixelType( kRGBA, img );
			comp = 4;
			break;
		case PNG_COLOR_TYPE_RGB:
			setPixelType( kRGB, img );
			comp = 3;
			break;
		case PNG_COLOR_TYPE_GRAY:
			setPixelType( kGrey, img );
			comp = 1;
			break;
		default:
			assert( 0 );
	}
	
	setSize( info_ptr->width, info_ptr->height, img );
	uint8_t *data = imageData( img );
	
	for ( uint32_t j = 0; j < (uint32_t) info_ptr->height; j++ )
	{
		/*if ( info_ptr->color_type == PNG_COLOR_TYPE_RGB_ALPHA )
		{
			for ( uint32_t i = 0; i < (uint32_t) info_ptr->width; i++ )
			{
				if ( flip )
				{
					data[ (info_ptr->height-j-1) * info_ptr->width * comp + comp * i + 0 ] = image_rows[ j ][ comp * i + 0 ];
					data[ (info_ptr->height-j-1) * info_ptr->width * comp + comp * i + 1 ] = image_rows[ j ][ comp * i + 1 ];
					data[ (info_ptr->height-j-1) * info_ptr->width * comp + comp * i + 2 ] = image_rows[ j ][ comp * i + 2 ];
					data[ (info_ptr->height-j-1) * info_ptr->width * comp + comp * i + 3 ] = image_rows[ j ][ comp * i + 3 ];
				}
				else
				{
					data[ j * info_ptr->width * comp + comp * i + 0 ] = image_rows[ j ][ comp * i + 0 ];
					data[ j * info_ptr->width * comp + comp * i + 1 ] = image_rows[ j ][ comp * i + 1 ];
					data[ j * info_ptr->width * comp + comp * i + 2 ] = image_rows[ j ][ comp * i + 2 ];
					data[ j * info_ptr->width * comp + comp * i + 3 ] = image_rows[ j ][ comp * i + 3 ];
				}
			}
		}
		else*/ 
//		if ( info_ptr->color_type == PNG_COLOR_TYPE_RGB || info_ptr->color_type == PNG_COLOR_TYPE_RGB_ALPHA )
//		{
			if ( flip )
			{
				memcpy( data + ((info_ptr->height-j-1) * info_ptr->width * comp), image_rows[ j ], info_ptr->width * comp );
			}
			else
			{
				memcpy( data + (j * info_ptr->width * comp), image_rows[ j ], info_ptr->width * comp );
			}
//		}
//		else
//		{
//			for ( uint32_t i = 0; i < (uint32_t) info_ptr->width; i++ )
//				for ( uint32_t c = 0; c < comp; c++ )
//					if ( flip )
//						data[ (info_ptr->height-j-1) * info_ptr->width * comp + comp * i + c ] = image_rows[ j ][ comp * i + c ];
//					else
//						data[ j * info_ptr->width * comp + comp * i + c ] = image_rows[ j ][ comp * i + c ];
//		}
	}
	
	/* clean up after the read, and free any memory allocated - REQUIRED */
	png_destroy_read_struct(&png_ptr, &info_ptr, (png_infopp)NULL);
	
	return 1;
}

// Finally some private helper functions

static void setSize( uint32_t w, uint32_t h, Image *img )
{
	if ( ( w != img->fWidth ) || ( h != img->fHeight ) )
	{
		img->fWidth = w;
		img->fHeight = h;
		reallocate( img );
	}
}

static void setPixelType( PixelType p, Image *img )
{
	if ( p != img->fType )
	{
		uint32_t c = 0;
		
		switch ( p )
		{
			case kRGB:
				c = 3;
				break;
			case kRGBA:
				c = 4;
				break;
			case kGrey:
				c = 1;
				break;
			case kYUV:
				c = 3;
				break;
			case kHSV:
				c = 3;
				break;
			case kBW:
				c = 1;
				break;
			default:
				assert( 0 );
		}
		
		img->fType = p;
		img->fChannels = c;
		
		reallocate( img );
	}
}

static void reallocate( Image *img )
{
	uint32_t w = img->fWidth;
	uint32_t h = img->fHeight;
	uint32_t c = img->fChannels;

	if ( img->fData != NULL )
		free( img->fData );

//	if ( w > 0 && h > 0 && c > 0 )
//	{
	// Ensure data is at least 16 bytes, for reasons of libjpeg
	img->fData = (uint8_t *) malloc( sizeof(uint8_t) * ( max( w * h * c, (uint32_t) 16 ) ) );
	memset( img->fData, 0, max( w * h * c, (uint32_t) 16 ) );
//		img->fData = (uint8_t *) malloc( sizeof(uint8_t) * ( w * h * c ) );
//		memset( img->fData, 0, ( w * h * c ) );
//	}
//	else
//	{
//		img->fData = NULL;
//	}
}

static int32_t gcd( int32_t a, int32_t b )
{
	int32_t q = a / b;
	int32_t r = a % b;
	
	if ( r == 0 )
		return b;
	
	return gcd( q, r );
}

static void convertFloatToFraction( float f, int32_t mult, int32_t& num, int32_t& den )
{
	num = (int32_t)( mult * f );
	den = mult;
	
	int32_t g = gcd( num, den );
	
	num = num / g;
	den = den / g;
}

