
/*
 * Image loading library
 * This is a wrapper around libjpeg and libpng
 * It needs to be linked with libjpeg and libpng
 * Authors: Anthony Dick and Rhys Hill
 * Version: 1.3
 * - removed imageNew function overload
 * - added imageLoadFlip
 * - added PNG image loading
 */

#ifndef __IMAGE_LOADER_H__
#define __IMAGE_LOADER_H__

#include <stdint.h>

enum PixelType {
	kRGB = 0,		// 24 bit RGB. The type you'll probably use
	kRGBA,			// 32 bit RGB with alpha.
	kGrey,			// 8 bit greyscale
	kBW,			// Binary (black and white)
	kYUV,			// Intensity and chroma, stored as 24 bits
	kHSV,			// Hue, saturation, intensity, stored as 24 bits
	kNumPixelFormats
};
typedef enum PixelType PixelType;

// A bit of C style information hiding here...
typedef struct Image Image;

// Always call this first!
// Initialises and allocates memory for an image
Image *imageNew( );

// Load image from file. Memory is allocated for the image data.
// Returns 0 on failure, non-0 on success
int imageLoad( const char *fileName, Image *img );	

// Load image from file, and store scanlines in reverse order.
// Matches OpenGL texture coordinates.
// Memory is allocated for the image data.
// Returns 0 on failure, non-0 on success
int imageLoadFlip( const char *fileName, Image *img );	

// Free memory, and reset image parameters.
// Call this when you're finished with the image
void imageDestroy( Image *img );		

// Allocate memory for an image in memory, and initialise it to be black.
// Not required if loading texture from file - use for procedural textures.
Image *imageNewMem( const PixelType pt, const uint32_t w, const uint32_t h );

// Return width of image in pixels
uint32_t imageWidth( const Image *img );

// Return height of image in pixels
uint32_t imageHeight( const Image *img );

// Return number of channels in image.
// For an RGB image this is 3 (R, G and B). For greyscale, 1.
uint32_t imageNumChannels( const Image *img );

// Returns the pixel type. See above enumeration for possibilites.
PixelType imagePixelType( const Image *img );

// Returns a pointer to the raw image data. This is what OpenGl needs for texture.
uint8_t	*imageData( const Image *img );

// A couple of convenience functions, if you require access to individual pixels
void imageGetPixel( const uint32_t x, const uint32_t y, uint8_t pixel[], const Image *img );
void imageSetPixel( const uint32_t x, const uint32_t y, uint8_t pixel[], const Image *img );

#endif
