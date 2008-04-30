#! /bin/sh
autoreconf -v -i -f || exit 1
./configure "$@"
