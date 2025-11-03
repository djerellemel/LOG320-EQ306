import java.util.*;

public class Plateau {
    private int[][] board = new int[8][8];

    // ------------------ CONSTRUCTEURS ------------------
    public Plateau() {}

    public Plateau(String s) {
        String[] values = s.trim().split(" ");
        int idx = 0;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                try {
                    board[y][x] = Integer.parseInt(values[idx++]);
                } catch (Exception e) {
                    board[y][x] = 0;
                }
            }
        }
    }

    // ------------------ COPIE ------------------
    public Plateau copy() {
        Plateau p = new Plateau();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                p.board[y][x] = this.board[y][x];
            }
        }
        return p;
    }

    // ------------------ APPLICATION ET RESTAURATION DE COUPS ------------------
    public void applyMove(Move m) {
        int fromX = m.getFromX();
        int fromY = m.getFromY();
        int toX = m.getToX();
        int toY = m.getToY();
        int color = board[fromY][fromX];
        board[fromY][fromX] = Mark.EMPTY;
        board[toY][toX] = color;
    }

    // Sert à annuler un coup après évaluation (utilisé par Player)
    public void setCell(int x, int y, int value) {
        if (x >= 0 && x < 8 && y >= 0 && y < 8) {
            board[y][x] = value;
        }
    }

    // ------------------ GÉNÉRATION DE MOUVEMENTS ------------------
    public List<Move> getAllPossibleMoves(int color) {
        List<Move> moves = new ArrayList<>();
        int opponent = (color == Mark.RED) ? Mark.BLACK : Mark.RED;

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (board[y][x] == color) {
                    int[][] dirs = {
                        {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                        {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
                    };

                    for (int[] d : dirs) {
                        int dx = d[0];
                        int dy = d[1];
                        int count = countPiecesInLine(x, y, dx, dy);

                        int nx = x + dx * count;
                        int ny = y + dy * count;

                        if (!isInside(nx, ny)) continue;

                        int dest = board[ny][nx];
                        if (dest == Mark.EMPTY || dest == opponent) {
                            moves.add(new Move(x, y, nx, ny));
                        }
                    }
                }
            }
        }
        return moves;
    }

    // Compte les pions dans la ligne (pour LOA)
    private int countPiecesInLine(int x, int y, int dx, int dy) {
        int count = 1;
        int nx = x + dx;
        int ny = y + dy;

        while (isInside(nx, ny)) {
            if (board[ny][nx] != Mark.EMPTY) count++;
            nx += dx;
            ny += dy;
        }

        nx = x - dx;
        ny = y - dy;
        while (isInside(nx, ny)) {
            if (board[ny][nx] != Mark.EMPTY) count++;
            nx -= dx;
            ny -= dy;
        }

        return count;
    }

    // ------------------ UTILITAIRES ------------------
    public int getCell(int x, int y) {
        if (!isInside(x, y)) return -1;
        return board[y][x];
    }

    public boolean isInside(int x, int y) {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    public int countPieces(int color) {
        int count = 0;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (board[y][x] == color) count++;
            }
        }
        return count;
    }

    // Affiche le plateau dans la console (utile pour debug)
    public void printPlateau() {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                System.out.print(board[y][x] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                sb.append(board[y][x]).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
