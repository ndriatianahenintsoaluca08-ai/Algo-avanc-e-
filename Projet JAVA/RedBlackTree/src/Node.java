/**
 * Un noeud de l'arbre binaire de recherche Rouge-Noir.
 * Cle de type String (comparaison lexicographique via compareTo).
 */
public class Node {

    public static final boolean ROUGE = true;
    public static final boolean NOIR = false;

    String key;
    boolean color;   // ROUGE ou NOIR
    Node left;
    Node right;
    Node parent;

    public Node(String key, boolean color, Node nil) {
        this.key = key;
        this.color = color;
        this.left = nil;
        this.right = nil;
        this.parent = nil;
    }

    public String getKey() {
        return key;
    }

    public boolean isRed() {
        return color == ROUGE;
    }
}