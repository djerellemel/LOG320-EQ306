import java.util.List;

/**
 * Classe Main
 * Cette classe constitue le point d’entrée principal du programme « Lines of Action ».
 * Elle sert à initialiser le plateau de jeu, à afficher sa configuration initiale
 * et à tester certaines fonctionnalités de la classe {@link Plateau}, comme la
 * génération de mouvements ou l’évaluation du plateau.
 * Elle peut être utilisée en mode local (sans serveur) pour valider le bon
 * fonctionnement des méthodes de manipulation du plateau et de l’algorithme Minimax.
 */
public class Main {

    /**
     * Méthode principale du programme.
     * Elle initialise un plateau de jeu avec une configuration de départ
     * prédéfinie, affiche cette configuration, et appelle les fonctions
     * de test (génération de mouvements, affichage des coups possibles, etc.)
     * @param args Arguments passés au programme (non utilisés ici).
     */
    public static void main(String[] args) {

        System.out.println("Hello, welcome to the game Lines Of Action — let's start the game!");
        System.out.println("---------------------------------------------------------------");

        /**
         * Chaîne représentant la configuration initiale du plateau :
         * 0 → Case vide
         * 2 → Pion noir ('X')
         * 4 → Pion rouge ('O')
         * Cette configuration correspond à l’état initial standard
         * du jeu « Lines of Action ».
         */
        String setplateau = "0222222040040004400200044000000440000004400000044000000402222220";

        // Création du plateau de jeu à partir de la configuration initiale
        Plateau plateau = new Plateau(setplateau);

        // Affichage du plateau initial dans la console
        System.out.println("Plateau initial :");
        plateau.printPlateau();

        System.out.println("-------------------------------");

        /*
         * ----------------------------------------------------------------------
         * SECTION DE TESTS (commentée par défaut)
         * ----------------------------------------------------------------------
         * 
         * Cette section permet de tester plusieurs actions successives
         * pour valider la logique de déplacement et de capture.
         * 
         * Exemples de coups :
         *   - plateau.play("A5-C5", playerMax);
         *   - plateau.play("D6-D8", playerMin);
         *   - plateau.play("A7-D4", playerMax);
         *   - plateau.play("F8-H6", playerMin);
         * 
         * Ces appels permettent de vérifier :
         *   - la mise à jour du plateau après chaque coup,
         *   - la capture correcte des pions adverses,
         *   - la validité des mouvements selon les règles du jeu.
         *
         * Vous pouvez décommenter cette section pour exécuter les tests manuellement.
         */
        /*
        plateau.play("A5-C5", playerMax);
        plateau.play("D6-D8", playerMin);
        plateau.play("A7-D4", playerMax);
        plateau.play("F8-H6", playerMin);
        plateau.play("A4-A8", playerMax);
        plateau.play("B8-E5", playerMin);

        plateau.play("C8-E6");
        plateau.play("H4-F6");
        plateau.play("B8-D6");
        plateau.play("H3-F1");

        plateau.printPlateau();

        // Exemple : Génération des mouvements possibles après un coup donné
        String positionAfterMove = "A3";
        List<String> movements = plateau.generateMovements(positionAfterMove, 'X');

        System.out.println("Mouvements possibles à partir de la position " + positionAfterMove + " :");
        for (String movement : movements) {
            System.out.println(movement);
        }
        */

        /*
         * Exemple d’appel de l’algorithme Minimax :
         * Permet de calculer le meilleur coup possible
         * pour le joueur courant en fonction de la configuration actuelle.
         */
        // plateau.miniMax('1', plateau);
        // System.out.println("Coup optimal trouvé : " + plateau.moveToSend);

        /**
         * Exemple de génération de mouvements possibles pour une pièce donnée.
         * Ici, on demande les mouvements valides pour la pièce 'X' située à D8.
         */
        System.out.println("Mouvements possibles pour la pièce à D8 :");
        System.out.println(plateau.generateMovements("D8", 'X'));
    }
}
