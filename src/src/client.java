import java.io.*;
import java.net.*;

/**
 * Classe Client
 * Représente le client réseau du jeu « Lines of Action ». Cette classe établit une
 * connexion avec le serveur du jeu, reçoit les commandes et l'état du plateau,
 * puis calcule et envoie les meilleurs coups à jouer en utilisant l’algorithme
 * Minimax implémenté dans la classe {@link Plateau}.
 * 
 * Fonctions principales :
 * 
 *   Établir la communication TCP avec le serveur (port 8888)
 *   Recevoir et interpréter les commandes envoyées par le serveur
 *   Mettre à jour le plateau et les joueurs selon le message reçu
 *   Appeler les méthodes d’évaluation et de décision (Minimax / Alpha-Bêta)
 *   Afficher l’évolution du plateau à chaque tour
 * 
 * Commandes serveur :
 *  Début de partie (joueur blanc)
 *   Début de partie (joueur noir)
 *   Demande du prochain coup
 *   Coup invalide
 *   Fin de partie
 */
class Client {

    /** Objet Socket utilisé pour établir la connexion avec le serveur. */
    private static Socket MyClient;

    /** Flux d’entrée permettant de recevoir les messages du serveur. */
    private static BufferedInputStream input;

    /** Flux de sortie permettant d’envoyer les coups au serveur. */
    private static BufferedOutputStream output;

    /** Objet représentant le plateau de jeu (structure de données principale). */
    private static Plateau plateau = new Plateau();

    /** Adresse IPv4 du serveur (saisie par l’utilisateur). */
    private static String ipv4 = "";

    /**
     * Point d’entrée du programme client.
     * Gère la communication réseau, la réception des commandes, la mise à jour
     * du plateau et le calcul des coups à envoyer au serveur.
     * @param args Arguments passés à l’exécution (non utilisés ici).
     */
    public static void main(String[] args) {

        try {
            System.out.println("Veuillez entrer l'adresse du serveur : ");
            // Création de la connexion réseau (par défaut : localhost)
            MyClient = new Socket("localhost", 8888);

            // Initialisation des flux de communication
            input = new BufferedInputStream(MyClient.getInputStream());
            output = new BufferedOutputStream(MyClient.getOutputStream());
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

            // Boucle principale de traitement des commandes serveur
            while (true) {
                char cmd = 0;
                cmd = (char) input.read();
                System.out.println(cmd);

                /**
                 * ==========================================
                 * CAS 1 → Début de la partie en joueur rouge
                 * ==========================================
                 * Le client reçoit la configuration initiale du plateau
                 * et doit immédiatement jouer le premier coup.
                 */
                if (cmd == '1') {
                    System.out.println("Nouvelle partie! Vous jouez rouge, entrez votre premier coup : ");
                    byte[] aBuffer = new byte[1024];
                    int size = input.available();
                    input.read(aBuffer, 0, size);

                    String s = new String(aBuffer).trim();
                    plateau = new Plateau(s);
                    plateau.setPlayers(cmd);
                    plateau.printPlateau();

                    plateau.miniMax(cmd, plateau);
                    String move = plateau.moveToSend;

                    System.out.println("------------- Meilleur coup calculé : " + move);

                    plateau.play(move, plateau.playerMax);
                    output.write(move.getBytes(), 0, move.length());
                    output.flush();
                }

                /**
                 * ==========================================
                 * CAS 2 → Début de la partie en joueur noir
                 * ==========================================
                 * Le client attend le premier coup des rouges,
                 * puis affiche la configuration du plateau.
                 */
                if (cmd == '2') {
                    System.out.println("Nouvelle partie! Vous jouez noir, attendez le coup des rouges...");
                    byte[] aBuffer = new byte[1024];
                    int size = input.available();
                    input.read(aBuffer, 0, size);

                    String s = new String(aBuffer).trim();
                    plateau = new Plateau(s);
                    plateau.setPlayers(cmd);
                    plateau.printPlateau();
                }

                /**
                 * =====================================================
                 * CAS 3 → Le serveur demande au client de jouer un coup
                 * =====================================================
                 * Le message contient aussi le dernier coup joué
                 * par l’adversaire (ex: "D6-D8").
                 */
                if (cmd == '3') {
                    byte[] aBuffer = new byte[16];
                    int size = input.available();
                    input.read(aBuffer, 0, size);

                    String s = new String(aBuffer);
                    System.out.println("Dernier coup : " + s);

                    // Mise à jour du plateau avec le coup adverse
                    plateau.play(s.replaceAll("\\s", ""), plateau.playerMin);
                    plateau.printPlateau();

                    System.out.println("Calcul du meilleur coup...");

                    int alpha = Integer.MIN_VALUE;
                    int beta = Integer.MAX_VALUE;

                    // Recherche du meilleur coup par Minimax
                    plateau.algoMinMax(plateau, plateau.playerMax, 3, alpha, beta);
                    String move = plateau.moveToSend;

                    System.out.println("------------- Meilleur coup calculé : " + move);
                    plateau.play(move, plateau.playerMax);

                    System.out.println("******** Plateau après exécution du coup ********");
                    plateau.printPlateau();

                    // Envoi du coup au serveur
                    output.write(move.getBytes(), 0, move.length());
                    output.flush();
                }

                /**
                 * ==========================================
                 * CAS 4 → Coup invalide
                 * ==========================================
                 * Le serveur rejette le coup précédent.
                 * Le joueur doit saisir manuellement un nouveau coup.
                 */
                if (cmd == '4') {
                    System.out.println("Coup invalide, entrez un nouveau coup : ");
                    String move = console.readLine();
                    output.write(move.getBytes(), 0, move.length());
                    output.flush();
                }

                /**
                 * ==========================================
                 * CAS 5 → Fin de partie
                 * ==========================================
                 * Le serveur envoie le dernier coup et attend une
                 * confirmation ou un nouveau message avant la fermeture.
                 */
                if (cmd == '5') {
                    byte[] aBuffer = new byte[16];
                    int size = input.available();
                    input.read(aBuffer, 0, size);
                    String s = new String(aBuffer);

                    System.out.println("Partie terminée. Dernier coup joué : " + s);
                    System.out.print("Appuyez sur Entrée pour quitter ou rejouer : ");

                    String move = console.readLine();
                    output.write(move.getBytes(), 0, move.length());
                    output.flush();
                }
            }

        } catch (IOException e) {
            System.out.println("Erreur de communication : " + e);
        }
    }
}
