CPPFLAGS += -I/usr/lib/jvm/java-6-sun/include -I/usr/lib/jvm/java-6-sun/include/linux
CFLAGS += -g -fPIC

.PHONY: all
all: libprocessing-core.so

.PHONY: headers
headers:
	javah -classpath ../../classes processing.core.PApplet

$(wildcard *.c): headers

libprocessing-core.so: core.o
	${CC} ${CFLAGS} -lSDL -shared -o $@ $^

.PHONY: clean
clean:
	rm -rf *.o *.so processing_core*.h
