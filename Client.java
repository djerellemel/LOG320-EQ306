import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {

    // État courant côté client
    private static Plateau currentBoard = null;
    private static Player  player       = null;
    private static int     myColor      = Mark.RED; // sera mis à jour au début de partie

    public static void main(String[] args) {
        try (Socket sock = new Socket("localhost", 8888)) {
            BufferedInputStream  in  = new BufferedInputStream(sock.getInputStream());
            BufferedOutputStream out = new BufferedOutputStream(sock.getOutputStream());

            System.out.println("✅ Connecté au serveur LOA (localhost:8888)");

            while (true) {
                int b = in.read();                 // lecture bloquante d’une commande
                if (b == -1) break;
                char cmd = (char) b;

                // -------------------- DÉBUT DE PARTIE --------------------
                if (cmd == '1' || cmd == '2') {
                    String payload  = readMessage(in);     // le plateau arrive en chiffres
                    String boardStr = keepDigits(payload);
                    if (boardStr.isEmpty()) continue;

                    currentBoard = new Plateau(boardStr);
                    currentBoard.printPlateau();

                    int color = (cmd == '1') ? Mark.RED : Mark.BLACK;
                    player  = new Player(color);
                    myColor = color;

                    if (cmd == '1') {
                        System.out.println("🎮 Vous êtes Rouge (vous commencez)");
                        Move m = safeChoose(currentBoard);
                        System.out.println("Coup IA : " + m);
                        send(out, m.toString());
                    } else {
                        System.out.println("🎮 Vous êtes Noir (attendre le premier coup des rouges)");
                    }
                }

                // -------------------- TOUR DE JEU --------------------
                if (cmd == '3') {
                    // Le serveur envoie: "<dernier_coup> [plateau_en_chiffres...]"
                    String payload = readMessage(in);
                    if (payload == null) payload = "";
                    String[] parts = payload.trim().split(" ", 2);
                    String lastMoveTxt = parts.length >= 1 ? parts[0].trim() : "";
                    String boardStr    = (parts.length == 2) ? keepDigits(parts[1]) : "";

                    System.out.println("Dernier coup reçu : " + lastMoveTxt);

                    if (!boardStr.isEmpty()) {
                        // Cas complet: on reconstruit depuis les chiffres
                        currentBoard = new Plateau(boardStr);
                    } else if (currentBoard != null) {
                        // Cas incomplet: on applique localement le coup texte si possible (ex: "A6-A8")
                        applyTextMoveIfPossible(currentBoard, lastMoveTxt);
                    } else {
                        // Sécurité : pas de plateau, on passe
                        continue;
                    }

                    Move m = safeChoose(currentBoard);
                    System.out.println("Coup IA : " + m);
                    send(out, m.toString());
                }

                // -------------------- COUP INVALIDE --------------------
                if (cmd == '4') {
                    System.out.println("⚠️ Coup invalide — l’IA rejoue…");
                    String payload  = readMessage(in);   // parfois plateau, parfois rien
                    String boardStr = keepDigits(payload);
                    if (!boardStr.isEmpty()) {
                        currentBoard = new Plateau(boardStr);
                    }
                    Move m = safeChoose(currentBoard);
                    System.out.println("Nouveau coup IA : " + m);
                    send(out, m.toString());
                }

                // -------------------- FIN DE PARTIE --------------------
                if (cmd == '5') {
                    System.out.println("🏁 Partie terminée.");
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Erreur de connexion : " + e.getMessage());
        }
    }

    // -------------------- OUTILS DE COMMUNICATION --------------------

    // Lecture bloquante d’un message (correspondant à la commande lue juste avant)
    private static String readMessage(BufferedInputStream in) throws IOException {
        byte[] buf = new byte[8192];
        int n = in.read(buf);  // bloquant
        if (n <= 0) return "";
        return new String(buf, 0, n).trim();
    }

    private static void send(BufferedOutputStream out, String msg) throws IOException {
        out.write(msg.getBytes());
        out.flush();
    }

    // Conserver uniquement les tokens numériques (les 64 valeurs du plateau)
    private static String keepDigits(String s) {
        if (s == null || s.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        String[] tokens = s.split("\\s+");
        for (String t : tokens) {
            if (t.matches("\\d+")) {
                sb.append(t).append(" ");
            }
        }
        return sb.toString().trim();
    }

    // -------------------- LOGIQUE DE JEU (fallbacks robustes) --------------------

    // Choisit un coup via Player; si jamais null, prend un coup légal trivial
    private static Move safeChoose(Plateau b) {
        Move m = player.chooseMove(b);
        if (m != null) return m;

        java.util.List<Move> list = b.getAllPossibleMoves(myColor);
        if (list.isEmpty()) {
            // En ultime recours, on essaie l’autre couleur (ne devrait pas arriver)
            int other = (myColor == Mark.RED) ? Mark.BLACK : Mark.RED;
            list = b.getAllPossibleMoves(other);
        }
        return list.isEmpty() ? new Move(0, 0, 0, 0) : list.get(0);
    }

    // Applique un coup texte "A6-A8" si on peut le parser; ignore "G1" (information incomplète)
    private static void applyTextMoveIfPossible(Plateau b, String moveTxt) {
        if (moveTxt == null) return;
        moveTxt = moveTxt.trim().toUpperCase();
        if (!moveTxt.contains("-")) return; // ex: "G1" -> insuffisant

        String[] ab = moveTxt.split("-");
        if (ab.length != 2) return;

        int[] from = parseCoord(ab[0]);
        int[] to   = parseCoord(ab[1]);
        if (from == null || to == null) return;

        int piece = b.getCell(from[0], from[1]);
        if (piece < 0) return;
        b.setCell(to[0],  to[1],  piece);
        b.setCell(from[0], from[1], Mark.EMPTY);
    }

    // "A6" -> [x,y] avec x:0..7, y:0..7 (0 = rangée du haut)
    private static int[] parseCoord(String s) {
        if (s == null || s.length() < 2) return null;
        char col = s.charAt(0);
        if (col < 'A' || col > 'H') return null;
        int x = col - 'A';
        String rowStr = s.substring(1);
        int row;
        try {
            row = Integer.parseInt(rowStr);
        } catch (Exception e) {
            return null;
        }
        if (row < 1 || row > 8) return null;
        int y = 8 - row; // 1 -> 7, 8 -> 0
        return new int[]{x, y};
    }
}
