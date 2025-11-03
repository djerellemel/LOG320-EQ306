import java.util.*;

public class Player {
    private int color;
    private int opponentColor;
    private long timeLimit = 1000; // 1 seconde max par coup
    private long startTime;

    public Player(int color) {
        this.color = color;
        this.opponentColor = (color == Mark.RED ? Mark.BLACK : Mark.RED);
    }

    // ------------------ CHOIX DU COUP ------------------
    public Move chooseMove(Plateau board) {
        startTime = System.currentTimeMillis();
        List<Move> moves = board.getAllPossibleMoves(color);

        if (moves == null || moves.isEmpty()) {
            System.out.println("Aucun coup possible pour " + (color == Mark.RED ? "rouge" : "noir"));
            return null;
        }

        System.out.println("Nombre de mouvements possibles : " + moves.size());

        Move bestMove = moves.get(0);
        int bestValue = Integer.MIN_VALUE;

        for (Move m : moves) {
            if (isTimeUp()) break;

            // Sauvegarde de l'état avant d'appliquer le coup
            int fromX = m.getFromX(), fromY = m.getFromY();
            int toX = m.getToX(), toY = m.getToY();
            int piece = board.getCell(fromX, fromY);
            int captured = board.getCell(toX, toY);

            // Joue le coup temporairement
            board.applyMove(m);

            // Évalue la position
            int value = evaluate(board);

            // Annule le coup
            board.setCell(fromX, fromY, piece);
            board.setCell(toX, toY, captured);

            if (value > bestValue) {
                bestValue = value;
                bestMove = m;
            }
        }

        return bestMove;
    }

    // ------------------ ÉVALUATION SIMPLE ------------------
    private int evaluate(Plateau board) {
        int myPieces = board.countPieces(color);
        int oppPieces = board.countPieces(opponentColor);
        int score = (myPieces - oppPieces) * 10;

        // Bonus de cohésion
        score -= computeCohesion(board, color) / 2;

        // Pas besoin de test de connectivité ici (trop lent pour le PC1)
        return score;
    }

    // ------------------ COHÉSION ------------------
    private int computeCohesion(Plateau board, int color) {
        List<int[]> pieces = new ArrayList<>();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (board.getCell(x, y) == color)
                    pieces.add(new int[]{x, y});
            }
        }

        if (pieces.size() < 2) return 0;

        int total = 0;
        for (int i = 0; i < pieces.size() - 1; i++) {
            int dx = Math.abs(pieces.get(i)[0] - pieces.get(i + 1)[0]);
            int dy = Math.abs(pieces.get(i)[1] - pieces.get(i + 1)[1]);
            total += Math.max(dx, dy);
        }
        return total;
    }

    private boolean isTimeUp() {
        return (System.currentTimeMillis() - startTime) > timeLimit;
    }
}
