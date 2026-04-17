import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        CompilerErrorHandler.clear();

        Path inputPath = args.length > 0 ? Path.of(args[0]) : Path.of("in.txt");
        String code = loadOrCreateInput(inputPath);

        System.out.println("Using input file: " + inputPath.toAbsolutePath());
        System.out.println("===== SOURCE CODE =====");
        System.out.println(code);

        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();

        System.out.println("\n===== LEXER OUTPUT (TOKENS) =====");
        tokens.forEach(System.out::println);

        Parser parser = new Parser(tokens);
        ASTNode tree = parser.parseProgram();
        System.out.println("\n===== PARSER OUTPUT (AST) =====");
        printAst(tree, 0);

        System.out.println("\n===== INTERMEDIATE CODE =====");
        IntermediateCodeGenerator icg = new IntermediateCodeGenerator();
        icg.generate(tree);

        System.out.println("\n===== FINAL CODE =====");
        CodeGenerator cg = new CodeGenerator();
        cg.generate(tree);

        printErrorSummary();
    }

    private static String loadOrCreateInput(Path inputPath) {
        try {
            if (!Files.exists(inputPath)) {
                Files.writeString(inputPath, getDefaultInput(), StandardCharsets.UTF_8);
                System.out.println("Created default input file: " + inputPath.toAbsolutePath());
            }
            String content = Files.readString(inputPath, StandardCharsets.UTF_8);
            // Guard against UTF-8 BOM so the lexer does not see it as a token.
            if (!content.isEmpty() && content.charAt(0) == '\uFEFF') {
                content = content.substring(1);
            }
            return content;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read input file '" + inputPath + "': " + e.getMessage(), e);
        }
    }

    private static String getDefaultInput() {
        return """
                read(x);
                x := 5 + 3;
                if x > 5 then
                    write(x);
                else
                    write(0);
                while x < 10 do
                    x := x + 1;
                """;
    }

    private static void printAst(ASTNode node, int depth) {
        if (node == null) {
            return;
        }

        String indent = "  ".repeat(depth);
        if (node.value == null || node.value.isEmpty()) {
            System.out.println(indent + node.type);
        } else {
            System.out.println(indent + node.type + "(" + node.value + ")");
        }

        for (ASTNode child : node.children) {
            printAst(child, depth + 1);
        }
    }

    private static void printErrorSummary() {
        int count = CompilerErrorHandler.getErrorCount();
        System.out.println("\n===== ERROR SUMMARY =====");
        if (count == 0) {
            System.out.println("No errors found.");
            return;
        }
        System.out.println("Total errors: " + count);
    }
}
