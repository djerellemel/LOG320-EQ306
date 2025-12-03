import java.io.*;
import java.net.*;
import java.util.Scanner;

class Client {

    private static Socket MyClient;
    private static BufferedInputStream input;
    private static BufferedOutputStream output;
    private static Plateau plateau = new Plateau();

    /** Variables pour mesurer le temps moyen des coups */
    private static long totalMoveTime = 0;
    private static int moveCount = 0;

    public static void main(String[] args) {

        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Entrez l'adresse IP du serveur (par défaut: localhost) : ");
            String serverIP = scanner.nextLine().trim();

            System.out.print("Entrez le port du serveur (par défaut: 8888) : ");
            String port = scanner.nextLine().trim();
            int serverPort;

            if (port.isEmpty()) {
                serverPort = 8888;
            } else {
                try {
                    serverPort = Integer.parseInt(port);
                    if (serverPort < 1 || serverPort > 65535) {
                        System.out.println(" Port invalide. Le port par défaut (8888) sera utilisé.");
                        serverPort = 8888;
                    }
                } catch (NumberFormatException e) {
                    System.out.println(" Entrée non valide. Le port par défaut (8888) sera utilisé.");
                    serverPort = 8888;
                }
            }

            if (serverIP.isEmpty()) {
                serverIP = "localhost";
            }

            MyClient = new Socket(serverIP, serverPort);

            input = new BufferedInputStream(MyClient.getInputStream());
            output = new BufferedOutputStream(MyClient.getOutputStream());
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                char cmd = 0;
                cmd = (char) input.read();
                System.out.println(cmd);

                // ========================
                // CAS 1 : Joueur Rouge
                // ========================
                if (cmd == '1') {
                    System.out.println("Nouvelle partie! Vous jouez rouge, entrez votre premier coup : ");
                    byte[] aBuffer = new byte[1024];
                    int size = input.available();
                    input.read(aBuffer, 0, size);

                    String s = new String(aBuffer).trim();
                    plateau = new Plateau(s);
                    plateau.setPlayers(cmd);

                    plateau.printPlateau();

                    long start = System.currentTimeMillis();
                    plateau.miniMax(cmd, plateau);
                    long end = System.currentTimeMillis();

                    totalMoveTime += (end - start);
                    moveCount++;

                    String move = plateau.moveToSend;

                    System.out.println("------------- Meilleur coup calculé : " + move);
                    plateau.play(move, plateau.playerMax);

                    output.write(move.getBytes(), 0, move.length());
                    output.flush();
                }

                // ========================
                // CAS 2 : Joueur Noir
                // ========================
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

                // ============================================
                // CAS 3 : Le serveur demande un nouveau coup
                // ============================================
                if (cmd == '3') {
                    byte[] aBuffer = new byte[16];
                    int size = input.available();
                    input.read(aBuffer, 0, size);

                    String s = new String(aBuffer);
                    System.out.println("Dernier coup : " + s);

                    plateau.play(s.replaceAll("\\s", ""), plateau.playerMin);
                    plateau.printPlateau();

                    System.out.println("Calcul du meilleur coup...");

                    int alpha = Integer.MIN_VALUE;
                    int beta = Integer.MAX_VALUE;

                    long start = System.currentTimeMillis();
                    plateau.algoMinMax(plateau, plateau.playerMax, 3, alpha, beta);
                    long end = System.currentTimeMillis();

                    totalMoveTime += (end - start);
                    moveCount++;

                    String move = plateau.moveToSend;

                    System.out.println("------------- Meilleur coup calculé : " + move);
                    plateau.play(move, plateau.playerMax);

                    System.out.println("******** Plateau après exécution du coup ********");
                    plateau.printPlateau();

                    output.write(move.getBytes(), 0, move.length());
                    output.flush();
                }

                // ============================================
                // CAS 4 : Coup invalide
                // ============================================
                if (cmd == '4') {
                    System.out.println("Coup invalide, entrez un nouveau coup : ");
                    String move = console.readLine();
                    output.write(move.getBytes(), 0, move.length());
                    output.flush();
                }

                // ============================================
                // CAS 5 : Fin de partie
                // ============================================
                if (cmd == '5') {
                    byte[] aBuffer = new byte[16];
                    int size = input.available();
                    input.read(aBuffer, 0, size);
                    String s = new String(aBuffer);

                    System.out.println("Partie terminée. Dernier coup joué : " + s);

                    // 👍 Affichage du Average move time
                    if (moveCount > 0) {
                        double average = (double) totalMoveTime / moveCount;
                        System.out.println("Average move time: " + average + " ms");
                    }

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
