AC_PREREQ(2.61)
AC_INIT(processing-core, 0.01, john_lee@openmoko.com)
AM_INIT_AUTOMAKE([foreign])

AC_CONFIG_SRCDIR([sdl.c])
AC_CONFIG_HEADER([config.h])

dnl Checks for programs.
AC_PROG_CC
AC_PROG_LIBTOOL

dnl Checks for header files.
AC_CHECK_HEADER([jni.h], [], [AC_MSG_ERROR([required header not found])])

dnl Checks for typedefs, structures, and compiler characteristics.
AC_C_INLINE

dnl Check for SDL
AC_ARG_WITH(sdl,
	AS_HELP_STRING([--with-sdl], [use sdl (default is NO)]),
	[ac_cv_use_sdl=$withval],
	[ac_cv_use_sdl=no])
AC_CACHE_CHECK([whether to use sdl], [ac_cv_use_sdl], [ac_cv_use_sdl=no])
AM_CONDITIONAL([USE_SDL], [test x$ac_cv_use_sdl != xno])

if test "x$ac_cv_use_sdl" != "xno" ; then
   SDL_VERSION=1.2.0
   AM_PATH_SDL($SDL_VERSION, [], AC_MSG_ERROR([*** SDL version $SDL_VERSION not found!]))
   CFLAGS="$CFLAGS $SDL_CFLAGS"
   LIBS="$LIBS $SDL_LIBS"

   dnl Checks for library functions.
   AC_CHECK_LIB([SDL_draw], Draw_Pixel, [LIBS="$LIBS -lSDL_draw"],
   [AC_MSG_ERROR([required library SDL_draw not found])])
fi

dnl Check X

AC_CONFIG_FILES([Makefile])
AC_OUTPUT
