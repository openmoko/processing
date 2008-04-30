#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

# jni.h
# sdl

AC_PREREQ(2.61)
AC_INIT(processing-core, 0.01, john_lee@openmoko.com)
AM_INIT_AUTOMAKE([foreign])

AC_CONFIG_SRCDIR([core.c])
AC_CONFIG_HEADER([config.h])

# Checks for programs.
AC_PROG_CC
AC_PROG_LIBTOOL

# Checks for libraries.
AC_CHECK_LIB([SDL], [SDL_Init])

# Checks for header files.
AC_CHECK_HEADER([SDL/SDL.h], [], [AC_MSG_ERROR([required header not found])])
AC_CHECK_HEADER([jni.h], [], [AC_MSG_ERROR([required header not found])])

# Checks for typedefs, structures, and compiler characteristics.
AC_C_INLINE

# Checks for library functions.

AC_CONFIG_FILES([Makefile])
AC_OUTPUT
