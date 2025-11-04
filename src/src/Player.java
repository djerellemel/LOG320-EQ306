/**
 * Classe Player

 * Représente un joueur dans le jeu « Lines of Action ». 
 * Chaque joueur est identifié par un symbole ('X' ou 'O') 
 * et possède également une référence vers le symbole de son adversaire.
 * Cette classe sert principalement à :
 * Initialiser le joueur selon son rôle (rouge ou noir)
 * Fournir les symboles représentant le joueur courant et l’adversaire
 * Identifier le symbole inverse d’un caractère donné
 */
public class Player {

    /** 
     * Symbole représentant le joueur courant.
     * Ce symbole est soit 'X' (pion noir) soit 'O' (pion rouge),
     * selon la valeur envoyée par le serveur au début de la partie.
     */
    private char current;

    /** 
     * Symbole représentant le joueur adverse. 
     * Il est automatiquement défini à l’opposé du symbole du joueur courant.
     */
    private char oppenent;

    /**
     * Constructeur du joueur.
     * Initialise les symboles du joueur courant et de son adversaire
     * selon le caractère reçu du serveur.
     * @param i Valeur reçue du serveur indiquant le rôle du joueur :
     * '1' → Le joueur joue les pions rouges ('O')
     * '2' → Le joueur joue les pions noirs ('X')
     */
    public Player(char i) {
        if (i == '1') {
            this.current = 'O'; // Le joueur 1 (rouge)
            this.oppenent = 'X';
        } else {
            this.current = 'X'; // Le joueur 2 (noir)
            this.oppenent = 'O';
        }
    }

    /**
     * Retourne le symbole du joueur courant.
     *
     * @return Le caractère représentant le joueur ('X' ou 'O').
     */
    public char getCurrent() {
        return current;
    }

    /**
     * Retourne le symbole de l’adversaire.
     *
     * @return Le caractère représentant l’adversaire ('X' ou 'O').
     */
    public char getOppenent() {
        return oppenent;
    }

    /**
     * Retourne le symbole inverse du caractère fourni.
     * Utile pour déterminer à qui appartient une case donnée sur le plateau.
     *
     * @param c Chaîne représentant un symbole de joueur ("X" ou "O").
     * @return "O" si le paramètre est "X", sinon "X".
     */
    public String getNonValue(String c) {
        if (c.equals("X")) {
            return "O";
        } else {
            return "X";
        }
    }
}
