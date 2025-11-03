import java.util.*;

public class Plateau {
    private int[][] board = new int[8][8];

    public Plateau() {}

    public Plateau(String s) {
        String[] vals = s.trim().split(" ");
        int index = 0;
        for (int y = 0; y < 8; y++)
            for (int x = 0; x < 8; x++)
                board[y][x] = Integer.parseInt(vals[index++]);
    }

    public Plateau copy() {
        Plateau p = new Plateau();
        for (int i = 0; i < 8; i++)
            p.board[i] = Arrays.copyOf(board[i], 8);
        return p;
    }

    // ----------- Génération de tous les coups possibles -----------
    public List<Move> getAllPossibleMoves(int color) {
        List<Move> moves = new ArrayList<>();
        int opponent = (color == Mark.RED) ? Mark.BLACK : Mark.RED;

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (board[y][x] == color) {
                    int[][] directions = {
                        {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                        {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
                    };
                    for (int[] d : directions) {
                        int dx = d[0], dy = d[1];
                        int count = countPiecesInLine(x, y, dx, dy);
                        int nx = x + dx * count;
                        int ny = y + dy * count;
                        if (isInside(nx, ny) && canJump(x, y, dx, dy, count, color)) {
                            int dest = board[ny][nx];
                            if (dest == Mark.EMPTY || dest == opponent)
                                moves.add(new Move(x, y, nx, ny));
                        }
                    }
                }
            }
        }
        return moves;
    }

    private int countPiecesInLine(int x, int y, int dx, int dy) {
        int count = 1;
        int i = x + dx, j = y + dy;
        while (isInside(i, j)) {
            if (board[j][i] != Mark.EMPTY) count++;
            i += dx; j += dy;
        }
        i = x - dx; j = y - dy;
        while (isInside(i, j)) {
            if (board[j][i] != Mark.EMPTY) count++;
            i -= dx; j -= dy;
        }
        return count;
    }

    private boolean canJump(int x, int y, int dx, int dy, int steps, int color) {
        int opponent = (color == Mark.RED) ? Mark.BLACK : Mark.RED;
        for (int k = 1; k < steps; k++) {
            int nx = x + dx * k;
            int ny = y + dy * k;
            if (!isInside(nx, ny)) return false;
            if (board[ny][nx] == opponent) return false;
        }
        return true;
    }

    private boolean isInside(int x, int y) {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    // ----------- Application d’un coup -----------
    public void applyMove(Move move) {
        int x1 = move.getX1(), y1 = move.getY1();
        int x2 = move.getX2(), y2 = move.getY2();
        int color = board[y1][x1];
        board[y1][x1] = Mark.EMPTY;
        board[y2][x2] = color;
    }

    // ----------- Fonctions utilitaires -----------
    public int countPieces(int color) {
        int c = 0;
        for (int y = 0; y < 8; y++)
            for (int x = 0; x < 8; x++)
                if (board[y][x] == color) c++;
        return c;
    }

    public boolean isConnected(int color) {
        boolean[][] visited = new boolean[8][8];
        int total = countPieces(color);
        int[] start = findFirstPiece(color);
        if (start == null) return false;
        int connected = dfs(start[0], start[1], color, visited);
        return connected == total;
    }

    private int dfs(int x, int y, int color, boolean[][] visited) {
        if (!isInside(x, y) || visited[y][x] || board[y][x] != color)
            return 0;
        visited[y][x] = true;
        int count = 1;
        int[][] dirs = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };
        for (int[] d : dirs)
            count += dfs(x + d[0], y + d[1], color, visited);
        return count;
    }

    private int[] findFirstPiece(int color) {
        for (int y = 0; y < 8; y++)
            for (int x = 0; x < 8; x++)
                if (board[y][x] == color)
                    return new int[]{x, y};
        return null;
    }

    public void printPlateau() {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++)
                System.out.print(board[y][x] + " ");
            System.out.println();
        }
        System.out.println();
    }
}
