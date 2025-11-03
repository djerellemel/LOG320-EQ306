import java.util.ArrayList;

// IMPORTANT: Il ne faut pas changer la signature des méthodes
// de cette classe, ni le nom de la classe.
// Vous pouvez par contre ajouter d'autres méthodes (ça devrait 
// être le cas)
class Player {
    private Mark cpuMark;
    private int numExploredNodes; // Contient le nombre de noeuds visités (le nombre d'appel à la fonction MinMax ou Alpha Beta). Normalement, la variable devrait être incrémentée au début de votre MinMax ou Alpha Beta.

    // Le constructeur reçoit en paramètre le
    // joueur MAX (X ou O)
    public Player(Mark cpu) {
        this.cpuMark = cpu;
        this.numExploredNodes = 0;
    }

    //Permet de retourner le nombre de noeuds explorés durant une recherche MinMax ou Alpha-Beta.
    public int getNumOfExploredNodes() {
        return numExploredNodes;
    }

    // Retourne la liste des coups possibles avec l'algorithme MinMax
    //Cette liste contient plusieurs coups possibles si et seuleument si plusieurs coups ont le même score.
    public ArrayList<Move> getNextMoveMinMax(Plateau board) {
        numExploredNodes = 0;
        int bestScore = Integer.MIN_VALUE;
        ArrayList<Move> bestMoves = new ArrayList<>();

        for (Move move : board.getAvailableMoves()) {
            board.play(move, cpuMark);
            int score = minMax(board, false);
            board.undo(move);

            if (score > bestScore) {
                bestMoves.clear();
                bestMoves.add(move);
                bestScore = score;
            } else if (score == bestScore) {
                bestMoves.add(move);
            }
        }

        return bestMoves;
    }

    // Retourne la liste des coups possiblesavec l'algorithme Alpha beta
    //Cette liste contient plusieurs coups possibles si et seuleument si plusieurs coups ont le même score.
    public ArrayList<Move> getNextMoveAB(Plateau board) {
        numExploredNodes = 0;
        int bestScore = Integer.MIN_VALUE;
        ArrayList<Move> bestMoves = new ArrayList<>();

        for (Move move : board.getAvailableMoves()) {
            board.play(move, cpuMark);
            int score = alphaBeta(board, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            board.undo(move);

            if (score > bestScore) {
                bestScore = score;
                bestMoves.clear();
                bestMoves.add(move);
            } else if (score == bestScore) {
                bestMoves.add(move);
            }
        }

        return bestMoves;
    }

    //Cette méthode implémente la logique de l'algorithme MinMax
    //Permet de générer l'abres des coups possibles avec un appel récursif à chaque coup
    private int minMax(Plateau board, boolean isMaximizing) {
        numExploredNodes++;
        int score = board.evaluate(cpuMark);
        if (score == 100 || score == -100 || board.getAvailableMoves().isEmpty()) {
            return score;
        }

        if (isMaximizing) {
            int best = Integer.MIN_VALUE;
            for (Move move : board.getAvailableMoves()) {
                board.play(move, cpuMark);
                best = Math.max(best, minMax(board, false));
                board.undo(move);
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            Mark opponent = (cpuMark == Mark.X) ? Mark.O : Mark.X;
            for (Move move : board.getAvailableMoves()) {
                board.play(move, opponent);
                best = Math.min(best, minMax(board, true));
                board.undo(move);
            }
            return best;
        }
    }

    //Cette méthode implémente la logique de l'algorithme Alpha Beta
    //Permet de générer l'abres des coups possibles avec un appel récursif à chaque coup
    private int alphaBeta(Plateau board, int alpha, int beta, boolean isMaximizing) {
        numExploredNodes++;
        int score = board.evaluate(cpuMark);
        if (score == 100 || score == -100 || board.getAvailableMoves().isEmpty()) {
            return score;
        }

        if (isMaximizing) {
            int best = Integer.MIN_VALUE;
            for (Move move : board.getAvailableMoves()) {
                board.play(move, cpuMark);
                best = Math.max(best, alphaBeta(board, alpha, beta, false));
                board.undo(move);
                alpha = Math.max(alpha, best);
                if (beta <= alpha) break;
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            Mark opponent = (cpuMark == Mark.X) ? Mark.O : Mark.X;
            for (Move move : board.getAvailableMoves()) {
                board.play(move, opponent);
                best = Math.min(best, alphaBeta(board, alpha, beta, true));
                board.undo(move);
                beta = Math.min(beta, best);
                if (beta <= alpha) break;
            }
            return best;
        }
    }
}
