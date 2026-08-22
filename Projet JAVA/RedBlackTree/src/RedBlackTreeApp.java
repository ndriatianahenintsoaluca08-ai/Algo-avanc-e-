import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Petite application JavaFX qui visualise en direct un Arbre Rouge-Noir :
 * insertion, suppression et recherche redessinent l'arbre a chaque operation,
 * avec les couleurs reelles des noeuds (rouge/noir) et une mise en surbrillance
 * lors d'une recherche.
 */
public class RedBlackTreeApp extends Application {

    private static final double RAYON = 20;
    private static final double ESPACE_X = 55;
    private static final double ESPACE_Y = 80;
    private static final double MARGE = 45;

    private RedBlackTree arbre = new RedBlackTree();

    private final Pane zoneDessin = new Pane();
    private final Label statutLabel = new Label();
    private final Label statsLabel = new Label();
    private final Map<Node, double[]> positions = new HashMap<>();

    private final String[] motsDemo = {
        "banane", "mangue", "ananas", "litchi", "orange", "kiwi",
        "papaye", "citron", "fraise", "poire", "letchi", "corossol",
        "goyave", "ravinala", "vanille", "girofle", "cannelle"
    };
    private final Random rnd = new Random();

    @Override
    public void start(Stage stage) {
        // ---- Barre de commandes ----
        TextField champCle = new TextField();
        champCle.setPromptText("cle (ex: mangue)");
        champCle.setPrefWidth(160);

        Button btnInserer = new Button("Inserer");
        Button btnSupprimer = new Button("Supprimer");
        Button btnRechercher = new Button("Rechercher");
        Button btnAleatoire = new Button("Ajout aleatoire");
        Button btnVider = new Button("Vider l'arbre");

        btnInserer.setOnAction(e -> inserer(champCle.getText()));
        btnSupprimer.setOnAction(e -> supprimer(champCle.getText()));
        btnRechercher.setOnAction(e -> rechercher(champCle.getText()));
        btnAleatoire.setOnAction(e -> ajoutAleatoire());
        btnVider.setOnAction(e -> vider());
        champCle.setOnAction(e -> inserer(champCle.getText()));

        HBox barreCommandes = new HBox(10, champCle, btnInserer, btnSupprimer, btnRechercher, btnAleatoire, btnVider);
        barreCommandes.setPadding(new Insets(10));
        barreCommandes.setAlignment(Pos.CENTER_LEFT);
        barreCommandes.setStyle("-fx-background-color: #1e293b;");
        stylerBouton(btnInserer, "#16a34a");
        stylerBouton(btnSupprimer, "#dc2626");
        stylerBouton(btnRechercher, "#2563eb");
        stylerBouton(btnAleatoire, "#7c3aed");
        stylerBouton(btnVider, "#475569");
        champCle.setStyle("-fx-background-radius: 6; -fx-padding: 6;");

        // ---- Legende ----
        HBox legende = construireLegende();

        VBox entete = new VBox(barreCommandes, legende);

        // ---- Zone de dessin ----
        zoneDessin.setStyle("-fx-background-color: #0f172a;");
        zoneDessin.setPrefSize(900, 500);
        ScrollPane scroll = new ScrollPane(zoneDessin);
        scroll.setFitToWidth(false);
        scroll.setFitToHeight(false);
        scroll.setStyle("-fx-background: #0f172a;");

        // ---- Bas de page : statut + stats ----
        statutLabel.setTextFill(Color.web("#e2e8f0"));
        statutLabel.setFont(Font.font("Consolas", 13));
        statsLabel.setTextFill(Color.web("#94a3b8"));
        statsLabel.setFont(Font.font("Consolas", 12));
        VBox pied = new VBox(4, statutLabel, statsLabel);
        pied.setPadding(new Insets(8, 14, 10, 14));
        pied.setStyle("-fx-background-color: #1e293b;");

        BorderPane racine = new BorderPane();
        racine.setTop(entete);
        racine.setCenter(scroll);
        racine.setBottom(pied);

        Scene scene = new Scene(racine, 960, 640);
        stage.setTitle("Visualisation - Arbre Binaire Rouge-Noir");
        stage.setScene(scene);
        stage.show();

        statutLabel.setText("Pret. Insere une cle pour commencer.");
        redessiner();
    }

    private void stylerBouton(Button b, String couleurHex) {
        b.setStyle("-fx-background-color: " + couleurHex + "; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
    }

    private HBox construireLegende() {
        Circle cRouge = new Circle(8, Color.web("#dc2626"));
        Circle cNoir = new Circle(8, Color.web("#1f2937"));
        cNoir.setStroke(Color.web("#94a3b8"));
        Label lRouge = new Label("Noeud ROUGE");
        Label lNoir = new Label("Noeud NOIR");
        lRouge.setTextFill(Color.web("#e2e8f0"));
        lNoir.setTextFill(Color.web("#e2e8f0"));
        HBox box = new HBox(8, cRouge, lRouge, new Label("    "), cNoir, lNoir);
        box.setPadding(new Insets(4, 14, 8, 14));
        box.setAlignment(Pos.CENTER_LEFT);
        box.setStyle("-fx-background-color: #1e293b;");
        return box;
    }

    // ------------------------------------------------------------------
    //  ACTIONS
    // ------------------------------------------------------------------

    private void inserer(String cleBrute) {
        String cle = nettoyer(cleBrute);
        if (cle == null) return;
        boolean ok = arbre.inserer(cle);
        statutLabel.setText(ok
                ? "Insere \"" + cle + "\" -> l'arbre a ete reequilibre si necessaire."
                : "\"" + cle + "\" existe deja, aucune insertion.");
        redessiner();
    }

    private void supprimer(String cleBrute) {
        String cle = nettoyer(cleBrute);
        if (cle == null) return;
        boolean ok = arbre.supprimer(cle);
        statutLabel.setText(ok
                ? "Supprime \"" + cle + "\" -> l'arbre a ete reequilibre si necessaire."
                : "\"" + cle + "\" est absent, rien a supprimer.");
        redessiner();
    }

    private void rechercher(String cleBrute) {
        String cle = nettoyer(cleBrute);
        if (cle == null) return;
        Node trouve = arbre.rechercherNode(arbre.getRoot(), cle);
        boolean present = !arbre.estFeuille(trouve);
        statutLabel.setText(present
                ? "\"" + cle + "\" trouve dans l'arbre (mis en surbrillance)."
                : "\"" + cle + "\" est absent de l'arbre.");
        redessiner();
        if (present) {
            surlignerNoeud(trouve);
        }
    }

    private void ajoutAleatoire() {
        for (int essai = 0; essai < motsDemo.length; essai++) {
            String mot = motsDemo[rnd.nextInt(motsDemo.length)];
            if (arbre.inserer(mot)) {
                statutLabel.setText("Insertion aleatoire : \"" + mot + "\".");
                redessiner();
                return;
            }
        }
        statutLabel.setText("Tous les mots de demonstration sont deja presents.");
    }

    private void vider() {
        arbre = new RedBlackTree();
        statutLabel.setText("Arbre vide.");
        redessiner();
    }

    private String nettoyer(String brut) {
        if (brut == null) return null;
        String s = brut.trim();
        if (s.isEmpty()) {
            statutLabel.setText("Entre une cle non vide avant de valider.");
            return null;
        }
        return s;
    }

    // ------------------------------------------------------------------
    //  DESSIN
    // ------------------------------------------------------------------

    private int compteurX;

    private void redessiner() {
        zoneDessin.getChildren().clear();
        positions.clear();
        compteurX = 0;

        Node racine = arbre.getRoot();
        if (arbre.estFeuille(racine)) {
            Text vide = new Text(20, 30, "(arbre vide - insere une cle pour commencer)");
            vide.setFill(Color.web("#64748b"));
            vide.setFont(Font.font("Consolas", 14));
            zoneDessin.getChildren().add(vide);
            zoneDessin.setPrefSize(900, 100);
        } else {
            calculerPositions(racine, 0);
            dessinerLiens(racine);
            for (Map.Entry<Node, double[]> entry : positions.entrySet()) {
                dessinerNoeud(entry.getKey(), entry.getValue());
            }
            double largeur = Math.max(900, compteurX * ESPACE_X + 2 * MARGE);
            double hauteur = Math.max(500, arbre.hauteur() * ESPACE_Y + 2 * MARGE);
            zoneDessin.setPrefSize(largeur, hauteur);
        }

        statsLabel.setText(String.format(
                "Noeuds : %d   |   Hauteur : %d   |   Proprietes Rouge-Noir valides : %s",
                arbre.taille(), arbre.hauteur(), arbre.estValide() ? "oui" : "NON (bug !)"));
    }

    /** Parcours infixe : place les noeuds de gauche a droite, niveau par niveau en profondeur. */
    private void calculerPositions(Node x, int profondeur) {
        if (arbre.estFeuille(x)) return;
        calculerPositions(x.left, profondeur + 1);
        double posX = MARGE + compteurX * ESPACE_X;
        double posY = MARGE + profondeur * ESPACE_Y;
        positions.put(x, new double[]{posX, posY});
        compteurX++;
        calculerPositions(x.right, profondeur + 1);
    }

    private void dessinerLiens(Node x) {
        if (arbre.estFeuille(x)) return;
        double[] pos = positions.get(x);
        if (!arbre.estFeuille(x.left)) {
            double[] posEnfant = positions.get(x.left);
            zoneDessin.getChildren().add(creerLigne(pos, posEnfant));
        }
        if (!arbre.estFeuille(x.right)) {
            double[] posEnfant = positions.get(x.right);
            zoneDessin.getChildren().add(creerLigne(pos, posEnfant));
        }
        dessinerLiens(x.left);
        dessinerLiens(x.right);
    }

    private Line creerLigne(double[] a, double[] b) {
        Line ligne = new Line(a[0], a[1], b[0], b[1]);
        ligne.setStroke(Color.web("#475569"));
        ligne.setStrokeWidth(2);
        return ligne;
    }

    private void dessinerNoeud(Node n, double[] pos) {
        boolean rouge = n.isRed();
        Circle cercle = new Circle(pos[0], pos[1], RAYON);
        cercle.setFill(Color.web(rouge ? "#dc2626" : "#1f2937"));
        cercle.setStroke(Color.web(rouge ? "#7f1d1d" : "#94a3b8"));
        cercle.setStrokeWidth(2);

        Text texte = new Text(n.getKey());
        texte.setFill(Color.WHITE);
        texte.setFont(Font.font("Consolas", FontWeight.BOLD, 11));
        // centrer le texte approximativement dans le cercle
        double largeurApprox = n.getKey().length() * 6.0;
        texte.setX(pos[0] - largeurApprox / 2);
        texte.setY(pos[1] + 4);

        zoneDessin.getChildren().addAll(cercle, texte);
    }

    private void surlignerNoeud(Node n) {
        double[] pos = positions.get(n);
        if (pos == null) return;
        Circle halo = new Circle(pos[0], pos[1], RAYON + 6);
        halo.setFill(Color.TRANSPARENT);
        halo.setStroke(Color.web("#facc15"));
        halo.setStrokeWidth(3);
        zoneDessin.getChildren().add(halo);
    }

    public static void main(String[] args) {
        launch(args);
    }
}