public class IntermediateCodeGenerator {
    private int temp = 0;
    private int label = 0;

    public String generate(ASTNode node) {
        if (node == null) return "";

        if (node.type.equals("PROGRAM")) {
            for (ASTNode child : node.children) {
                generate(child);
            }
            return "";
        }

        if (node.type.equals("NUM") || node.type.equals("ID"))
            return node.value;

        if (node.type.equals("PLUS") || node.type.equals("MINUS")
                || node.type.equals("MUL") || node.type.equals("DIV")) {
            String left = generate(node.children.get(0));
            String right = generate(node.children.get(1));

            String t = "t" + temp++;
            System.out.println(t + " = " + left + " " + node.value + " " + right);
            return t;
        }

        if (node.type.equals("GT") || node.type.equals("LT") || node.type.equals("EQ")) {
            String left = generate(node.child(0));
            String right = generate(node.child(1));
            String t = "t" + temp++;
            System.out.println(t + " = " + left + " " + node.value + " " + right);
            return t;
        }

        if (node.type.equals("ASSIGN")) {
            String var = node.children.get(0).value;
            String val = generate(node.children.get(1));
            System.out.println(var + " = " + val);
            return "";
        }

        if (node.type.equals("READ")) {
            System.out.println("READ " + node.value);
            return "";
        }

        if (node.type.equals("WRITE")) {
            String val = generate(node.children.get(0));
            System.out.println("PRINT " + val);
            return "";
        }

        if (node.type.equals("IF")) {
            String condition = generate(node.child(0));
            String elseLabel = "L" + label++;
            String endLabel = "L" + label++;

            System.out.println("IF_FALSE " + condition + " GOTO " + elseLabel);
            generate(node.child(1));

            if (node.children.size() > 2) {
                System.out.println("GOTO " + endLabel);
                System.out.println(elseLabel + ":");
                generate(node.child(2));
                System.out.println(endLabel + ":");
            } else {
                System.out.println(elseLabel + ":");
            }
            return "";
        }

        if (node.type.equals("WHILE")) {
            String startLabel = "L" + label++;
            String endLabel = "L" + label++;

            System.out.println(startLabel + ":");
            String condition = generate(node.child(0));
            System.out.println("IF_FALSE " + condition + " GOTO " + endLabel);
            generate(node.child(1));
            System.out.println("GOTO " + startLabel);
            System.out.println(endLabel + ":");
            return "";
        }

        return "";
    }
}
