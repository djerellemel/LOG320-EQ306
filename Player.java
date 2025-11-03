import java.util.*;

public class Player {
    private int color;
    private int opponentColor;
    private int depth = 3;

    public Player(int color) {
        this.color = color;
        this.opponentColor = (color == Mark.RED ? Mark.BLACK : Mark.RED);
    }

    public Move chooseMove(Plateau board) {
        Move bestMove = null;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int bestValue = Integer.MIN_VALUE;

        List<Move> moves = board.getAllPossibleMoves(color);
        if (moves.isEmpty()) return null;

        for (Move m : moves) {
            Plateau newBoard = board.copy();
            newBoard.applyMove(m);
            int value = minimax(newBoard, depth - 1, alpha, beta, false);
            if (value > bestValue) {
                bestValue = value;
                bestMove = m;
            }
            alpha = Math.max(alpha, value);
        }
        return bestMove;
    }

    private int minimax(Plateau board, int depth, int alpha, int beta, boolean maximizing) {
        if (depth == 0 || board.isConnected(color) || board.isConnected(opponentColor))
            return evaluate(board);

        if (maximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : board.getAllPossibleMoves(color)) {
                Plateau newBoard = board.copy();
                newBoard.applyMove(move);
                int eval = minimax(newBoard, depth - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break;
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : board.getAllPossibleMoves(opponentColor)) {
                Plateau newBoard = board.copy();
                newBoard.applyMove(move);
                int eval = minimax(newBoard, depth - 1, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break;
            }
            return minEval;
        }
    }

    private int evaluate(Plateau board) {
        int myPieces = board.countPieces(color);
        int oppPieces = board.countPieces(opponentColor);
        int score = (myPieces - oppPieces) * 10;
        if (board.isConnected(color)) score += 1000;
        if (board.isConnected(opponentColor)) score -= 1000;
        return score;
    }
}
