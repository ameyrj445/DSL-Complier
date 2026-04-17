import java.util.*;

public class ASTNode {
    String type, value;
    List<ASTNode> children = new ArrayList<>();

    public ASTNode(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public void add(ASTNode node) {
        children.add(node);
    }

    public ASTNode child(int index) {
        return children.get(index);
    }
}
