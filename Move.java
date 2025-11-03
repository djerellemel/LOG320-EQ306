public class Move {
    private int fromX, fromY;
    private int toX, toY;

    public Move(int fromX, int fromY, int toX, int toY) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
    }

    // --- Getters ---
    public int getFromX() { return fromX; }
    public int getFromY() { return fromY; }
    public int getToX() { return toX; }
    public int getToY() { return toY; }

    // --- Conversion en texte pour envoi au serveur ---
    public String toString() {
        return coordToString(fromX, fromY) + "-" + coordToString(toX, toY);
    }

    // Convertit une position (x, y) en format "A1", "H8", etc.
    private String coordToString(int x, int y) {
        char col = (char) ('A' + x);
        int row = 8 - y;
        return "" + col + row;
    }
}
