.PHONY: all
all: preproc core.jar

core.jar: $(wildcard src/processing/core/*.java)
	-mkdir classes
	javac -source 1.3 -sourcepath src -d classes $^
	jar cf core.jar -C classes processing

.PHONY: preproc
preproc:
	perl preproc.pl

.PHONY: clean
clean:
	rm -rf classes
	rm core.jar
