import java.util.*;

public class Lexer {
    private final String input;
    private int pos = 0;
    private int line = 1;

    public Lexer(String input) {
        this.input = input;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (pos < input.length()) {
            char c = input.charAt(pos);

            if (c == '\n') {
                line++;
                pos++;
            }

            else if (Character.isWhitespace(c)) pos++;

            else if (Character.isLetter(c)) {
                String word = readWord();

                switch (word) {
                    case "if" -> tokens.add(new Token("IF", word, line));
                    case "then" -> tokens.add(new Token("THEN", word, line));
                    case "else" -> tokens.add(new Token("ELSE", word, line));
                    case "while" -> tokens.add(new Token("WHILE", word, line));
                    case "do" -> tokens.add(new Token("DO", word, line));
                    case "read" -> tokens.add(new Token("READ", word, line));
                    case "write" -> tokens.add(new Token("WRITE", word, line));
                    default -> tokens.add(new Token("ID", word, line));
                }
            }

            else if (Character.isDigit(c)) {
                tokens.add(new Token("NUM", readNumber(), line));
            }

            else if (c == '+') { tokens.add(new Token("PLUS", "+", line)); pos++; }
            else if (c == '-') { tokens.add(new Token("MINUS", "-", line)); pos++; }
            else if (c == '*') { tokens.add(new Token("MUL", "*", line)); pos++; }
            else if (c == '/') { tokens.add(new Token("DIV", "/", line)); pos++; }

            else if (c == '>') { tokens.add(new Token("GT", ">", line)); pos++; }
            else if (c == '<') { tokens.add(new Token("LT", "<", line)); pos++; }

            else if (c == '=') {
                pos++;
                if (pos < input.length() && input.charAt(pos) == '=') {
                    tokens.add(new Token("EQ", "==", line));
                    pos++;
                } else {
                    CompilerErrorHandler.report(line, "Expected '=' after '='");
                }
            }

            else if (c == ':') {
                pos++;
                if (pos < input.length() && input.charAt(pos) == '=') {
                    tokens.add(new Token("ASSIGN", ":=", line));
                    pos++;
                } else {
                    CompilerErrorHandler.report(line, "Expected '=' after ':'");
                }
            }

            else if (c == '(') { tokens.add(new Token("LP", "(", line)); pos++; }
            else if (c == ')') { tokens.add(new Token("RP", ")", line)); pos++; }
            else if (c == ';') { tokens.add(new Token("SEMI", ";", line)); pos++; }

            else {
                CompilerErrorHandler.report(line, "Invalid character '" + c + "'");
                pos++;
            }
        }
        tokens.add(new Token("EOF", "", line));
        return tokens;
    }

    private String readWord() {
        StringBuilder sb = new StringBuilder();
        while (pos < input.length() && Character.isLetter(input.charAt(pos))) {
            sb.append(input.charAt(pos++));
        }
        return sb.toString();
    }

    private String readNumber() {
        StringBuilder sb = new StringBuilder();
        while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
            sb.append(input.charAt(pos++));
        }
        return sb.toString();
    }
}
