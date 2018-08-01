# Two-Pass Linker in Java
## Built by Vikas Nair, for Operating Systems (Lab 1)
## 4 Feb 2018

A Java implementation of a two-pass linker on a 200 word machine. The linker relocates external addresses and resolves external differences.

Accepts an input file consisting of a series of program modules. On the first pass, the linker reads the definition list and module sizes to arrange a symbol table. On the second pass, the linker reads the use list and module program content to arrange a memory map. Outputs error and warning messages when appropriate.

### Compile
```
javac Linker.java
```

### Run
```
java Linker inputs/input-X.txt
```

NOTE: this program is not intended to run on actual object modules. Simply demonstrates understanding of a rudimentary two-pass linker on test input.