public class Main {

    public static void main(String[] args) {
        RedBlackTree arbre = new RedBlackTree();

        String[] mots = {
            "banane", "mangue", "ananas", "litchi", "orange",
            "kiwi", "papaye", "citron", "fraise", "poire"
        };

        System.out.println("=== Insertion des cles ===");
        for (String mot : mots) {
            boolean ok = arbre.inserer(mot);
            System.out.println("Insertion de \"" + mot + "\" -> " + (ok ? "OK" : "deja present"));
        }

        System.out.println();
        System.out.println("=== Structure de l'arbre ===");
        arbre.afficherArbre();

        System.out.println();
        System.out.println("=== Parcours ===");
        System.out.println("Prefixe  (Racine-Gauche-Droite) : " + arbre.parcoursPrefixe());
        System.out.println("Infixe   (Gauche-Racine-Droite) : " + arbre.parcoursInfixe());
        System.out.println("Postfixe (Gauche-Droite-Racine) : " + arbre.parcoursPostfixe());

        System.out.println();
        System.out.println("=== Recherche ===");
        String[] recherches = {"kiwi", "mangue", "ravinala"};
        for (String r : recherches) {
            System.out.println("Recherche de \"" + r + "\" -> " + (arbre.rechercher(r) ? "trouve" : "absent"));
        }

        System.out.println();
        System.out.println("=== Verifications ===");
        System.out.println("Nombre de noeuds     : " + arbre.taille());
        System.out.println("Hauteur de l'arbre   : " + arbre.hauteur());
        System.out.println("Proprietes RN valides: " + arbre.estValide());

        System.out.println();
        System.out.println("=== Suppression ===");
        String[] aSupprimer = {"mangue", "ananas", "kiwi", "ravinala"};
        for (String mot : aSupprimer) {
            boolean ok = arbre.supprimer(mot);
            System.out.println("Suppression de \"" + mot + "\" -> " + (ok ? "OK" : "absent, rien a faire"));
        }

        System.out.println();
        System.out.println("=== Structure apres suppressions ===");
        arbre.afficherArbre();

        System.out.println();
        System.out.println("Infixe apres suppressions : " + arbre.parcoursInfixe());
        System.out.println("Nombre de noeuds          : " + arbre.taille());
        System.out.println("Hauteur de l'arbre         : " + arbre.hauteur());
        System.out.println("Proprietes RN valides      : " + arbre.estValide());

        System.out.println();
        System.out.println("=== Suppression complete (vidange totale) ===");
        String[] tousLesMots = {"banane", "litchi", "orange", "papaye", "citron", "fraise", "poire"};
        for (String mot : tousLesMots) {
            arbre.supprimer(mot);
        }
        System.out.println("Arbre vide ? " + arbre.estVide());
        System.out.println("Proprietes RN valides sur arbre vide : " + arbre.estValide());
    }
}