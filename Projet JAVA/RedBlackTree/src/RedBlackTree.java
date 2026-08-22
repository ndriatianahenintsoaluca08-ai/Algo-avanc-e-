/**
 * Arbre Binaire de Recherche Rouge-Noir (implementation "maison", sans
 * bibliotheque de structure de donnees : uniquement des classes/objets).
 *
 * Proprietes Rouge-Noir respectees :
 *  1. Chaque noeud est ROUGE ou NOIR.
 *  2. La racine est NOIRE.
 *  3. Toutes les feuilles (NIL) sont NOIRES.
 *  4. Un noeud ROUGE a ses deux enfants NOIRS (pas deux rouges consecutifs).
 *  5. Tout chemin d'un noeud vers ses feuilles NIL descendantes contient
 *     le meme nombre de noeuds NOIRS (hauteur noire constante).
 */
public class RedBlackTree {

    private final Node NIL;   // sentinelle, toujours NOIRE
    private Node root;
    private int taille;

    public RedBlackTree() {
        this.NIL = new Node(null, Node.NOIR, null);
        NIL.left = NIL;
        NIL.right = NIL;
        NIL.parent = NIL;
        this.root = NIL;
        this.taille = 0;
    }

    public boolean estVide() {
        return root == NIL;
    }

    public int taille() {
        return taille;
    }

    /** Racine de l'arbre (a utiliser en lecture seule, ex. pour l'affichage graphique). */
    public Node getRoot() {
        return root;
    }

    /** Indique si un noeud est la sentinelle NIL (donc une feuille vide). */
    public boolean estFeuille(Node n) {
        return n == NIL;
    }

    // ------------------------------------------------------------------
    //  ROTATIONS
    // ------------------------------------------------------------------

    private void leftRotate(Node x) {
        Node y = x.right;
        x.right = y.left;
        if (y.left != NIL) {
            y.left.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == NIL) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }
        y.left = x;
        x.parent = y;
    }

    private void rightRotate(Node x) {
        Node y = x.left;
        x.left = y.right;
        if (y.right != NIL) {
            y.right.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == NIL) {
            root = y;
        } else if (x == x.parent.right) {
            x.parent.right = y;
        } else {
            x.parent.left = y;
        }
        y.right = x;
        x.parent = y;
    }

    // ------------------------------------------------------------------
    //  INSERTION
    // ------------------------------------------------------------------

    /**
     * Insere une cle dans l'arbre. Ne fait rien si la cle existe deja.
     * @return true si la cle a ete inseree, false si elle existait deja.
     */
    public boolean inserer(String key) {
        Node y = NIL;
        Node x = root;

        while (x != NIL) {
            y = x;
            int cmp = key.compareTo(x.key);
            if (cmp == 0) {
                return false; // cle deja presente
            } else if (cmp < 0) {
                x = x.left;
            } else {
                x = x.right;
            }
        }

        Node z = new Node(key, Node.ROUGE, NIL);
        z.parent = y;
        if (y == NIL) {
            root = z;
        } else if (key.compareTo(y.key) < 0) {
            y.left = z;
        } else {
            y.right = z;
        }

        taille++;
        insererFixup(z);
        return true;
    }

    private void insererFixup(Node z) {
        while (z.parent.color == Node.ROUGE) {
            if (z.parent == z.parent.parent.left) {
                Node oncle = z.parent.parent.right;
                if (oncle.color == Node.ROUGE) {
                    // Cas 1 : l'oncle est rouge -> on recolore
                    z.parent.color = Node.NOIR;
                    oncle.color = Node.NOIR;
                    z.parent.parent.color = Node.ROUGE;
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.right) {
                        // Cas 2 : z est un enfant droit -> rotation gauche pour ramener au cas 3
                        z = z.parent;
                        leftRotate(z);
                    }
                    // Cas 3 : z est un enfant gauche -> recoloration + rotation droite
                    z.parent.color = Node.NOIR;
                    z.parent.parent.color = Node.ROUGE;
                    rightRotate(z.parent.parent);
                }
            } else {
                // symetrique (parent = enfant droit du grand-parent)
                Node oncle = z.parent.parent.left;
                if (oncle.color == Node.ROUGE) {
                    z.parent.color = Node.NOIR;
                    oncle.color = Node.NOIR;
                    z.parent.parent.color = Node.ROUGE;
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.left) {
                        z = z.parent;
                        rightRotate(z);
                    }
                    z.parent.color = Node.NOIR;
                    z.parent.parent.color = Node.ROUGE;
                    leftRotate(z.parent.parent);
                }
            }
        }
        root.color = Node.NOIR;
    }

    // ------------------------------------------------------------------
    //  RECHERCHE
    // ------------------------------------------------------------------

    public boolean rechercher(String key) {
        return rechercherNode(root, key) != NIL;
    }

    Node rechercherNode(Node x, String key) {
        while (x != NIL) {
            int cmp = key.compareTo(x.key);
            if (cmp == 0) {
                return x;
            } else if (cmp < 0) {
                x = x.left;
            } else {
                x = x.right;
            }
        }
        return NIL;
    }

    // ------------------------------------------------------------------
    //  SUPPRESSION
    // ------------------------------------------------------------------

    /**
     * Remplace le sous-arbre enracine en u par le sous-arbre enracine en v
     * (reconnecte simplement le parent, sans toucher aux enfants de v).
     */
    private void transplant(Node u, Node v) {
        if (u.parent == NIL) {
            root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }
        v.parent = u.parent;
    }

    /** Renvoie le noeud de cle minimale du sous-arbre enracine en x. */
    private Node minimum(Node x) {
        while (x.left != NIL) {
            x = x.left;
        }
        return x;
    }

    /**
     * Supprime la cle de l'arbre si elle existe.
     * @return true si la cle a ete supprimee, false si elle n'existait pas.
     */
    public boolean supprimer(String key) {
        Node z = rechercherNode(root, key);
        if (z == NIL) {
            return false; // cle absente
        }

        Node y = z;                    // noeud effectivement retire ou deplace
        boolean yColorOrigine = y.color;
        Node x;                        // noeud qui vient prendre la place de y

        if (z.left == NIL) {
            x = z.right;
            transplant(z, z.right);
        } else if (z.right == NIL) {
            x = z.left;
            transplant(z, z.left);
        } else {
            // z a deux enfants : on le remplace par son successeur (min du sous-arbre droit)
            y = minimum(z.right);
            yColorOrigine = y.color;
            x = y.right;

            if (y.parent == z) {
                x.parent = y; // pour garder x.parent valide meme si x == NIL
            } else {
                transplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }
            transplant(z, y);
            y.left = z.left;
            y.left.parent = y;
            y.color = z.color;
        }

        taille--;

        // Si le noeud physiquement retire (y) etait noir, on a perdu une unite
        // de hauteur noire sur son chemin -> il faut reequilibrer.
        if (yColorOrigine == Node.NOIR) {
            supprimerFixup(x);
        }
        return true;
    }

    private void supprimerFixup(Node x) {
        while (x != root && x.color == Node.NOIR) {
            if (x == x.parent.left) {
                Node frere = x.parent.right;
                if (frere.color == Node.ROUGE) {
                    // Cas 1 : le frere est rouge -> on le fait "passer" au noir
                    frere.color = Node.NOIR;
                    x.parent.color = Node.ROUGE;
                    leftRotate(x.parent);
                    frere = x.parent.right;
                }
                if (frere.left.color == Node.NOIR && frere.right.color == Node.NOIR) {
                    // Cas 2 : les deux enfants du frere sont noirs -> on recolore le frere
                    frere.color = Node.ROUGE;
                    x = x.parent;
                } else {
                    if (frere.right.color == Node.NOIR) {
                        // Cas 3 : enfant gauche du frere rouge, droit noir -> rotation droite sur le frere
                        frere.left.color = Node.NOIR;
                        frere.color = Node.ROUGE;
                        rightRotate(frere);
                        frere = x.parent.right;
                    }
                    // Cas 4 : enfant droit du frere rouge -> rotation gauche sur le parent
                    frere.color = x.parent.color;
                    x.parent.color = Node.NOIR;
                    frere.right.color = Node.NOIR;
                    leftRotate(x.parent);
                    x = root;
                }
            } else {
                // symetrique (x = enfant droit)
                Node frere = x.parent.left;
                if (frere.color == Node.ROUGE) {
                    frere.color = Node.NOIR;
                    x.parent.color = Node.ROUGE;
                    rightRotate(x.parent);
                    frere = x.parent.left;
                }
                if (frere.right.color == Node.NOIR && frere.left.color == Node.NOIR) {
                    frere.color = Node.ROUGE;
                    x = x.parent;
                } else {
                    if (frere.left.color == Node.NOIR) {
                        frere.right.color = Node.NOIR;
                        frere.color = Node.ROUGE;
                        leftRotate(frere);
                        frere = x.parent.left;
                    }
                    frere.color = x.parent.color;
                    x.parent.color = Node.NOIR;
                    frere.left.color = Node.NOIR;
                    rightRotate(x.parent);
                    x = root;
                }
            }
        }
        x.color = Node.NOIR;
    }

    // ------------------------------------------------------------------
    //  PARCOURS : prefixe (RGD), infixe (GRD), postfixe (GDR)
    // ------------------------------------------------------------------

    /** Parcours prefixe : Racine -> Gauche -> Droite */
    public String parcoursPrefixe() {
        StringBuilder sb = new StringBuilder();
        parcoursPrefixe(root, sb);
        return sb.toString().trim();
    }

    private void parcoursPrefixe(Node x, StringBuilder sb) {
        if (x == NIL) return;
        sb.append(x.key).append(x.isRed() ? "(R) " : "(N) ");
        parcoursPrefixe(x.left, sb);
        parcoursPrefixe(x.right, sb);
    }

    /** Parcours infixe : Gauche -> Racine -> Droite (donne l'ordre trie) */
    public String parcoursInfixe() {
        StringBuilder sb = new StringBuilder();
        parcoursInfixe(root, sb);
        return sb.toString().trim();
    }

    private void parcoursInfixe(Node x, StringBuilder sb) {
        if (x == NIL) return;
        parcoursInfixe(x.left, sb);
        sb.append(x.key).append(x.isRed() ? "(R) " : "(N) ");
        parcoursInfixe(x.right, sb);
    }

    /** Parcours postfixe : Gauche -> Droite -> Racine */
    public String parcoursPostfixe() {
        StringBuilder sb = new StringBuilder();
        parcoursPostfixe(root, sb);
        return sb.toString().trim();
    }

    private void parcoursPostfixe(Node x, StringBuilder sb) {
        if (x == NIL) return;
        parcoursPostfixe(x.left, sb);
        parcoursPostfixe(x.right, sb);
        sb.append(x.key).append(x.isRed() ? "(R) " : "(N) ");
    }

    // ------------------------------------------------------------------
    //  AMELIORATIONS : hauteur, hauteur noire, validation des proprietes
    // ------------------------------------------------------------------

    public int hauteur() {
        return hauteur(root);
    }

    private int hauteur(Node x) {
        if (x == NIL) return 0;
        return 1 + Math.max(hauteur(x.left), hauteur(x.right));
    }

    /**
     * Verifie que l'arbre respecte bien toutes les proprietes Rouge-Noir.
     * Utile pour valider automatiquement l'implementation apres chaque insertion.
     */
    public boolean estValide() {
        if (root.color != Node.NOIR) {
            return false; // propriete 2 : racine noire
        }
        return verifierNoeud(root) != -1;
    }

    /**
     * Retourne la hauteur noire du sous-arbre si valide, -1 sinon.
     * Verifie au passage : pas de rouge-rouge, hauteur noire constante.
     */
    private int verifierNoeud(Node x) {
        if (x == NIL) {
            return 1; // NIL compte pour 1 (noeud noir)
        }
        if (x.isRed()) {
            if (x.left.isRed() || x.right.isRed()) {
                return -1; // propriete 4 violee : deux rouges consecutifs
            }
        }
        int hg = verifierNoeud(x.left);
        int hd = verifierNoeud(x.right);
        if (hg == -1 || hd == -1 || hg != hd) {
            return -1; // propriete 5 violee : hauteurs noires differentes
        }
        return hg + (x.color == Node.NOIR ? 1 : 0);
    }

    // ------------------------------------------------------------------
    //  AFFICHAGE VISUEL DE L'ARBRE
    // ------------------------------------------------------------------

    public void afficherArbre() {
        if (root == NIL) {
            System.out.println("(arbre vide)");
            return;
        }
        afficherArbre(root, "", true);
    }

    private void afficherArbre(Node x, String prefixe, boolean estRacine) {
        if (x == NIL) return;
        String couleur = x.isRed() ? "R" : "N";
        System.out.println(prefixe + (estRacine ? "" : "+-- ") + x.key + " [" + couleur + "]");
        String nouveauPrefixe = prefixe + (estRacine ? "" : "    ");
        if (x.left != NIL || x.right != NIL) {
            afficherArbre(x.left, nouveauPrefixe, false);
            afficherArbre(x.right, nouveauPrefixe, false);
        }
    }
}