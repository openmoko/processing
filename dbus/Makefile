# requires dbus-java 2.4 and its dependency.

DEBUG ?= disable
JARDIR ?= ${HOME}/usr/share/java
JARFILES = ${JARDIR}/dbus.jar:${JARDIR}/debug-${DEBUG}.jar:${JARDIR}/hexdump.jar:${JARDIR}/unix.jar

JAVAC ?= javac
JAR ?= jar

.PHONY: all
all: processing-dbus.jar

processing-dbus.jar: $(wildcard src/processing/dbus/*.java)
	rm -rf classes
	mkdir classes
	${JAVAC} -classpath "${JARFILES}:../core/bin" -sourcepath src -d classes $^
	${JAR} cf $@ -C classes processing

.PHONY: clean
clean:
	rm -rf classes processing-dbus.jar
