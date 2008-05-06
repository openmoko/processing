#include <SDL.h>
#include <SDL_draw.h>
#include "processing_core_PGraphicsSDL2D.h"

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
    if (SDL_WasInit(SDL_INIT_VIDEO) != 0)
	return;

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

void sdl_point(int x, int y)
{
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

void sdl_draw_line(int x1, int y1, int x2, int y2)
{
    if (x1 != x2 && y1 != y2) {
	Draw_Line(screen, (Sint16) x1, (Sint16) y1, (Sint16) x2, (Sint16) y2, stroke);
	return;
    }
    if (x1 == x2) {
	Draw_VLine(screen, (Sint16) x1, (Sint16) y1, (Sint16) y2, stroke);
	return;
    }
    if (y1 == y2) {
	Draw_HLine(screen, (Sint16) x1, (Sint16) y1, (Sint16) x2, stroke);
	return;
    }
}

/*
 * Class:     processing_core_PGraphicsSDL2D
 * Method:    native_init
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_processing_core_PGraphicsSDL2D_native_1init
(JNIEnv * jnienv, jobject jobj) {
    fprintf(stderr, "%s\n", __func__);
    sdl_init(480, 640);
    stroke = SDL_MapRGB(screen->format, 255, 255, 255);
}

/*
 * Class:     processing_core_PGraphicsSDL2D
 * Method:    native_unload
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_processing_core_PGraphicsSDL2D_native_1unload
(JNIEnv * jnienv, jobject jobj) {
    fprintf(stderr, "%s\n", __func__);
    SDL_Quit();
}

/*
 * Class:     processing_core_PGraphicsSDL2D
 * Method:    native_line
 * Signature: (IIII)V
 */
JNIEXPORT void JNICALL Java_processing_core_PGraphicsSDL2D_native_1line
(JNIEnv * jnienv, jobject jobj, jint x1, jint y1, jint x2, jint y2) {
    sdl_draw_line((int) x1, (int) y1, (int) x2, (int) y2);
}

/*
 * Class:     processing_core_PGraphicsSDL2D
 * Method:    native_point
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_processing_core_PGraphicsSDL2D_native_1point
(JNIEnv * jnienv, jobject jobj, jint x, jint y) {
    sdl_point((int) x, (int) y);
}

/*
 * Class:     processing_core_PGraphicsSDL2D
 * Method:    native_update_rect
 * Signature: (IIII)V
 */
JNIEXPORT void JNICALL Java_processing_core_PGraphicsSDL2D_native_1update_1rect
(JNIEnv * jnienv, jobject jobj, jint x1, jint y1, jint x2, jint y2) {
    fprintf(stderr, "%s\n", __func__);
    SDL_UpdateRect(screen, (int) x1, (int) y1, (int) x2, (int) y2);
}
