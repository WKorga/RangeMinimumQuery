import java.util.Objects;

public class Node {
    private int index;
    private char value;
    private Node parent=null;
    private Node left = null;
    private Node right = null;

    public Node(int index, char value) {
        this.index = index;
        this.value = value;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setValue(char value) {
        this.value = value;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public int getIndex() {
        return index;
    }

    public char getValue() {
        return value;
    }

    public Node getParent() {
        return parent;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return index == node.index &&
                value == node.value &&
                Objects.equals(parent, node.parent) &&
                Objects.equals(left, node.left) &&
                Objects.equals(right, node.right);
    }

    @Override
    public int hashCode() {

        return Objects.hash(index, value, parent, left, right);
    }
}
