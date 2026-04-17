import java.util.*;

public class Parser {
    private final List<Token> tokens;
    private int pos = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Token peek() {
        return tokens.get(Math.min(pos, tokens.size() - 1));
    }

    private Token consume() {
        return tokens.get(pos++);
    }

    private boolean match(String type) {
        if (peek().type.equals(type)) {
            consume();
            return true;
        }
        return false;
    }

    private Token expect(String type, String msg) {
        if (!peek().type.equals(type)) {
            throw CompilerErrorHandler.asException(peek().line, msg);
        }
        return consume();
    }

    private void error(String msg) {
        throw CompilerErrorHandler.asException(peek().line, msg);
    }

    public ASTNode parseProgram() {
        ASTNode root = new ASTNode("PROGRAM", "");

        while (!peek().type.equals("EOF")) {
            try {
                root.add(parseStatement());
            } catch (Exception e) {
                CompilerErrorHandler.report(e);
                if (!peek().type.equals("EOF")) {
                    pos++;
                }
            }
        }
        return root;
    }

    private ASTNode parseStatement() {
        Token t = peek();

        if (t.type.equals("ID")) return parseAssignment();
        if (t.type.equals("READ")) return parseRead();
        if (t.type.equals("WRITE")) return parseWrite();
        if (t.type.equals("IF")) return parseIf();
        if (t.type.equals("WHILE")) return parseWhile();

        throw CompilerErrorHandler.asException(t.line, "Invalid statement");
    }

    private ASTNode parseAssignment() {
        ASTNode node = new ASTNode("ASSIGN", "");
        node.add(new ASTNode("ID", expect("ID", "Expected identifier").value));
        expect("ASSIGN", "Expected ':='");
        node.add(parseExpression());
        expect("SEMI", "Missing ';'");

        return node;
    }

    private ASTNode parseRead() {
        consume();
        expect("LP", "Missing '(' after read");
        ASTNode node = new ASTNode("READ", expect("ID", "Expected identifier inside read").value);
        expect("RP", "Missing ')'");
        expect("SEMI", "Missing ';'");

        return node;
    }

    private ASTNode parseWrite() {
        consume();
        expect("LP", "Missing '(' after write");
        ASTNode node = new ASTNode("WRITE", "");
        node.add(parseExpression());
        expect("RP", "Missing ')'");
        expect("SEMI", "Missing ';'");

        return node;
    }

    private ASTNode parseIf() {
        consume();
        ASTNode node = new ASTNode("IF", "");
        node.add(parseCondition());
        expect("THEN", "Missing 'then'");
        node.add(parseStatement());

        if (peek().type.equals("ELSE")) {
            consume();
            node.add(parseStatement());
        }

        return node;
    }

    private ASTNode parseWhile() {
        consume();
        ASTNode node = new ASTNode("WHILE", "");
        node.add(parseCondition());
        expect("DO", "Missing 'do'");
        node.add(parseStatement());
        return node;
    }

    private ASTNode parseCondition() {
        ASTNode left = parseExpression();

        if (peek().type.equals("GT") || peek().type.equals("LT") || peek().type.equals("EQ")) {
            Token op = consume();
            ASTNode node = new ASTNode(op.type, op.value);
            node.add(left);
            node.add(parseExpression());
            return node;
        }

        return left;
    }

    private ASTNode parseExpression() {
        ASTNode left = parseTerm();

        while (pos < tokens.size() &&
                (peek().type.equals("PLUS") || peek().type.equals("MINUS"))) {

            Token op = consume();
            ASTNode right = parseTerm();

            ASTNode node = new ASTNode(op.type, op.value);
            node.add(left);
            node.add(right);

            left = node;
        }
        return left;
    }

    private ASTNode parseTerm() {
        ASTNode left = parseFactor();

        while (peek().type.equals("MUL") || peek().type.equals("DIV")) {
            Token op = consume();
            ASTNode node = new ASTNode(op.type, op.value);
            node.add(left);
            node.add(parseFactor());
            left = node;
        }

        return left;
    }

    private ASTNode parseFactor() {
        Token t = peek();

        if (match("LP")) {
            ASTNode node = parseExpression();
            expect("RP", "Missing ')'");
            return node;
        }

        if (t.type.equals("ID") || t.type.equals("NUM")) {
            consume();
            return new ASTNode(t.type, t.value);
        }

        error("Unexpected token '" + peek().value + "'");
        return new ASTNode("ERROR", "");
    }
}
