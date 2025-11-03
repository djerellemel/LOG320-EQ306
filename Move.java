public class Move {
    private int x1, y1, x2, y2;

    public Move(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public int getX1() { return x1; }
    public int getY1() { return y1; }
    public int getX2() { return x2; }
    public int getY2() { return y2; }

    // Format pour le serveur (ex: "A3-B5")
    @Override
    public String toString() {
        return coordToString(x1, y1) + "-" + coordToString(x2, y2);
    }

    private String coordToString(int x, int y) {
        char col = (char) ('A' + x);
        int row = 8 - y;
        return "" + col + row;
    }

    public static int[] stringToCoord(String coord) {
        coord = coord.trim().toUpperCase();
        int x = coord.charAt(0) - 'A';
        int y = 8 - Character.getNumericValue(coord.charAt(1));
        return new int[]{x, y};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Move)) return false;
        Move m = (Move) o;
        return x1 == m.x1 && y1 == m.y1 && x2 == m.x2 && y2 == m.y2;
    }

    @Override
    public int hashCode() {
        return (x1 * 31 + y1) * 31 + x2 * 31 + y2;
    }

    public void printMove() {
        System.out.println("Move: " + toString());
    }
}
