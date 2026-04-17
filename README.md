# CD Compiler

A small Java-based compiler project that demonstrates the core phases of compilation:

- lexical analysis
- parsing into an abstract syntax tree (AST)
- intermediate code generation
- simple target code generation
- basic compiler error reporting

This project reads a tiny custom language from an input file, tokenizes it, builds an AST, prints intermediate code, and then emits simple assembly-like final code.

## Features

- Supports variable assignment using `:=`
- Supports arithmetic expressions with `+`, `-`, `*`, `/`
- Supports relational operators `>`, `<`, and `==`
- Supports `if-then[-else]` statements
- Supports `while-do` loops
- Supports `read(...)` and `write(...)`
- Reports lexical and parsing errors with line numbers

## Example Source Language

```txt
read(x);
x := 5 + 3;
if x > 5 then
    write(x);
else
    write(0);
while x < 10 do
    x := x + 1;
```

## Project Structure

```txt
.
|-- Main.java
|-- Lexer.java
|-- Parser.java
|-- Token.java
|-- ASTNode.java
|-- IntermediateCodeGenerator.java
|-- CodeGenerator.java
|-- CompilerErrorHandler.java
|-- in.txt
|-- out.txt
```

## How It Works

1. `Lexer.java` converts source code into tokens.
2. `Parser.java` builds an AST from those tokens.
3. `IntermediateCodeGenerator.java` produces three-address-style intermediate code.
4. `CodeGenerator.java` emits simple assembly-like output.
5. `CompilerErrorHandler.java` collects and prints errors.

## Requirements

- Java 17 or later recommended
- `javac` and `java` available in your terminal

## Compile and Run

Compile all Java files:

```bash
javac *.java
```

Run with the default input file:

```bash
java Main
```

Run with a custom input file:

```bash
java Main path/to/your-input.txt
```

If the input file does not exist, the program creates one with a default sample program.

## Output

When you run the compiler, it prints:

- the source code being compiled
- lexer output (tokens)
- parser output (AST)
- intermediate code
- final generated code
- an error summary

## Sample Final Code Output

```txt
IN x
ADD R0, 5, 3
MOV x, R0
CMP x, 5
SETGT R1
JZ R1, L0
OUT x
JMP L1
L0:
OUT 0
L1:
L2:
CMP x, 10
SETLT R2
JZ R2, L3
ADD R3, x, 1
MOV x, R3
JMP L2
L3:
```

## Notes

- This is an educational compiler project meant for learning compiler design basics.
- The generated target code is pseudo assembly, not machine code for a real processor.
- Compiled `.class` files are currently present in the repository; for a cleaner GitHub repo, you may want to ignore them in `.gitignore`.

