public class CodeGenerator {
    private int label = 0;
    private int register = 0;
    private final StringBuilder output = new StringBuilder();

    public void generate(ASTNode node) {
        if (node == null) return;

        if (node.type.equals("PROGRAM")) {
            for (ASTNode child : node.children) {
                generate(child);
            }
            return;
        }

        if (node.type.equals("READ")) {
            emit("IN " + node.value);
            return;
        }

        if (node.type.equals("WRITE")) {
            String value = emitExpression(node.child(0));
            emit("OUT " + value);
            return;
        }

        if (node.type.equals("ASSIGN")) {
            String value = emitExpression(node.child(1));
            emit("MOV " + node.child(0).value + ", " + value);
            return;
        }

        if (node.type.equals("IF")) {
            String elseLabel = "L" + label++;
            String endLabel = "L" + label++;
            String condition = emitCondition(node.child(0));

            emit("JZ " + condition + ", " + elseLabel);
            generate(node.child(1));
            if (node.children.size() > 2) {
                emit("JMP " + endLabel);
                emit(elseLabel + ":");
                generate(node.child(2));
                emit(endLabel + ":");
            } else {
                emit(elseLabel + ":");
            }
            return;
        }

        if (node.type.equals("WHILE")) {
            String start = "L" + label++;
            String end = "L" + label++;
            String condition;

            emit(start + ":");
            condition = emitCondition(node.child(0));
            emit("JZ " + condition + ", " + end);
            generate(node.child(1));
            emit("JMP " + start);
            emit(end + ":");
            return;
        }
    }

    private String emitCondition(ASTNode node) {
        if (node.type.equals("GT") || node.type.equals("LT") || node.type.equals("EQ")) {
            String left = emitExpression(node.child(0));
            String right = emitExpression(node.child(1));
            String reg = nextRegister();
            emit("CMP " + left + ", " + right);
            emit("SET" + node.type + " " + reg);
            return reg;
        }
        return emitExpression(node);
    }

    private String emitExpression(ASTNode node) {
        if (node.type.equals("NUM") || node.type.equals("ID")) {
            return node.value;
        }

        String left = emitExpression(node.child(0));
        String right = emitExpression(node.child(1));
        String reg = nextRegister();

        switch (node.type) {
            case "PLUS" -> emit("ADD " + reg + ", " + left + ", " + right);
            case "MINUS" -> emit("SUB " + reg + ", " + left + ", " + right);
            case "MUL" -> emit("MUL " + reg + ", " + left + ", " + right);
            case "DIV" -> emit("DIV " + reg + ", " + left + ", " + right);
            default -> {
                return emitCondition(node);
            }
        }

        return reg;
    }

    private String nextRegister() {
        return "R" + register++;
    }

    private void emit(String line) {
        System.out.println(line);
        output.append(line).append(System.lineSeparator());
    }

    public String getOutput() {
        return output.toString();
    }
}
