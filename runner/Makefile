.PHONY: all
all: $(wildcard src/*/*/*.java)
	test -e classes || mkdir classes
	javac -cp "unix-0.4.jar:dbus-2.4.jar:debug-disable-1.1.jar:core.jar" -sourcepath src -d classes $^

.PHONY: clean
clean:
	rm -rf classes
