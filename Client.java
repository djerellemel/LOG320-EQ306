import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        Socket MyClient;
        BufferedInputStream input;
        BufferedOutputStream output;
        Player player = null;

        try {
            MyClient = new Socket("localhost", 8888);
            input = new BufferedInputStream(MyClient.getInputStream());
            output = new BufferedOutputStream(MyClient.getOutputStream());

            while (true) {
                char cmd = (char) input.read();

                // Début de partie (Rouge ou Noir)
                if (cmd == '1' || cmd == '2') {
                    byte[] aBuffer = new byte[2048];
                    int size = input.read(aBuffer);
                    String s = new String(aBuffer, 0, size).trim();

                    Plateau plateau = new Plateau(s);
                    plateau.printPlateau();

                    int color = (cmd == '1') ? Mark.RED : Mark.BLACK;
                    player = new Player(color);

                    if (cmd == '1') {
                        System.out.println("Vous êtes Rouge (vous commencez la partie)");
                        Move best = player.chooseMove(plateau);
                        System.out.println("Coup choisi : " + best);
                        output.write(best.toString().getBytes());
                        output.flush();
                    } else {
                        System.out.println("Vous êtes Noir (attendez le premier coup des rouges)");
                    }
                }

                // Tour de jeu : le serveur envoie le dernier coup et attend la réponse
                if (cmd == '3') {
                    byte[] aBuffer = new byte[2048];
                    int size = input.read(aBuffer);
                    String s = new String(aBuffer, 0, size).trim();

                    Plateau plateau = new Plateau(s);
                    plateau.printPlateau();

                    Move best = player.chooseMove(plateau);
                    System.out.println("Coup choisi : " + best);
                    output.write(best.toString().getBytes());
                    output.flush();
                }

                // Coup invalide : rejouer un autre coup
                if (cmd == '4') {
                    System.out.println(" Coup invalide, l'IA va rejouer un nouveau coup...");

                    byte[] aBuffer = new byte[2048];
                    int size = input.read(aBuffer);
                    String s = new String(aBuffer, 0, size).trim();

                    Plateau plateau = new Plateau(s);
                    plateau.printPlateau();

                    Move best = player.chooseMove(plateau);
                    System.out.println("Nouveau coup choisi : " + best);
                    output.write(best.toString().getBytes());
                    output.flush();
                }

                // Partie terminée
                if (cmd == '5') {
                    System.out.println(" Partie terminée !");
                    break;
                }
            }

            MyClient.close();
        } catch (IOException e) {
            System.out.println("Erreur : " + e);
        }
    }
}
