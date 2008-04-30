#include "SDL/SDL.h"
#include "processing_core_PApplet.h"

static SDL_Surface *screen = NULL;
Uint32 stroke = 0;

/*
 * Set the pixel at (x, y) to the given value
 * NOTE: The surface must be locked before calling this!
 */
void (*putpixel) (int x, int y, Uint32 pixel);

static inline void _putpixel_8(int x, int y, Uint32 pixel)
{
    Uint8 *p = (Uint8 *) screen->pixels + y * screen->pitch + x * 1;
    *p = pixel;
}

static inline void _putpixel_16(int x, int y, Uint32 pixel)
{
    Uint8 *p = (Uint8 *) screen->pixels + y * screen->pitch + x * 2;
    *(Uint16 *) p = pixel;
}

static inline void _putpixel_24(int x, int y, Uint32 pixel)
{
    Uint8 *p = (Uint8 *) screen->pixels + y * screen->pitch + x * 3;
    if (SDL_BYTEORDER == SDL_BIG_ENDIAN) {
	p[0] = (pixel >> 16) & 0xff;
	p[1] = (pixel >> 8) & 0xff;
	p[2] = pixel & 0xff;
    } else {
	p[0] = pixel & 0xff;
	p[1] = (pixel >> 8) & 0xff;
	p[2] = (pixel >> 16) & 0xff;
    }
    *p = pixel;
}

static inline void _putpixel_32(int x, int y, Uint32 pixel)
{
    Uint8 *p = (Uint8 *) screen->pixels + y * screen->pitch + x * 4;
    *(Uint32 *) p = pixel;
}

void sdl_init(int width, int height)
{
    /* Initialize defaults, Video and Audio */
    if (SDL_Init(SDL_INIT_VIDEO) < 0)
	err_exit();

    screen = SDL_SetVideoMode(width, height, 0, SDL_SWSURFACE);
    if (screen == NULL)
	err_exit();

    switch (screen->format->BytesPerPixel) {
    case 1:
	putpixel = _putpixel_8;
	break;

    case 2:
	putpixel = _putpixel_16;
	break;

    case 3:
	putpixel = _putpixel_24;
	break;

    case 4:
	putpixel = _putpixel_32;
	break;
    }

    stroke = SDL_MapRGB(screen->format, 0xff, 0xff, 0x00);
}

/*
 * Class:     processing_core_PApplet
 * Method:    native_unload
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_processing_core_PApplet_native_1unload
    (JNIEnv * jnienv, jobject jobj) {
    fprintf(stderr, "%s\n", __func__);
    SDL_Quit();
}

/*
 * Class:     processing_core_PApplet
 * Method:    native_createGraphics
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_processing_core_PApplet_native_1createGraphics
    (JNIEnv * jnienv, jobject jobj, jint width, jint height) {
    fprintf(stderr, "%s width = %d, height = %d\n", __func__, (int) width,
	    (int) height);
    if (SDL_WasInit(SDL_INIT_VIDEO) == 0)
	sdl_init((int) width, (int) height);
}

/*
 * Class:     processing_core_PApplet
 * Method:    native_point
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_processing_core_PApplet_native_1point
    (JNIEnv * jnienv, jobject jobj, jint x, jint y) {
    /* Lock the screen for direct access to the pixels */
    if (SDL_MUSTLOCK(screen)) {
	if (SDL_LockSurface(screen) < 0) {
	    fprintf(stderr, "Can't lock screen: %s\n", SDL_GetError());
	    return;
	}
    }

    putpixel(x, y, stroke);

    if (SDL_MUSTLOCK(screen)) {
	SDL_UnlockSurface(screen);
    }
}

/*
 * Class:     processing_core_PApplet
 * Method:    native_stroke
 * Signature: (F)V
 */
JNIEXPORT void JNICALL Java_processing_core_PApplet_native_1stroke
    (JNIEnv * jnienv, jobject jobj, jint gray) {
    stroke = SDL_MapRGB(screen->format, gray, gray, gray);
}

/*
 * Class:     processing_core_PApplet
 * Method:    native_update_rect
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_processing_core_PApplet_native_1update_1rect
    (JNIEnv * jnienv, jobject jobj, jint width, jint height) {
    fprintf(stderr, "%s\n", __func__);
    SDL_UpdateRect(screen, 0, 0, width, height);
}
