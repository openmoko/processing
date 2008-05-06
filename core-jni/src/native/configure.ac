AC_PREREQ(2.61)
AC_INIT(processing-core, 0.01, john_lee@openmoko.com)
AM_INIT_AUTOMAKE([foreign])

AC_CONFIG_SRCDIR([core.c])
AC_CONFIG_HEADER([config.h])

dnl Checks for programs.
AC_PROG_CC
AC_PROG_LIBTOOL

dnl Checks for header files.
AC_CHECK_HEADER([jni.h], [], [AC_MSG_ERROR([required header not found])])

dnl Checks for typedefs, structures, and compiler characteristics.
AC_C_INLINE

dnl Check for SDL
SDL_VERSION=1.2.0
AM_PATH_SDL($SDL_VERSION,
            :,
            AC_MSG_ERROR([*** SDL version $SDL_VERSION not found!])
)
CFLAGS="$CFLAGS $SDL_CFLAGS"
LIBS="$LIBS $SDL_LIBS"

dnl Checks for library functions.
AC_CHECK_LIB([SDL_draw], Draw_Pixel, [], [AC_MSG_ERROR([required library not found])])

AC_CONFIG_FILES([Makefile])
AC_OUTPUT
