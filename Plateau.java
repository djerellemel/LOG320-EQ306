import java.util.ArrayList;

// IMPORTANT: Il ne faut pas changer la signature des méthodes
// de cette classe, ni le nom de la classe.
// Vous pouvez par contre ajouter d'autres méthodes (ça devrait 
// être le cas)
class Plateau {
    private Mark[][] board;  //on crée une instance du tableau

    private final int TAILLE = 3;

    //initialise le tableau de jeu avec des case vides
    public Plateau() {
        board = new Mark[TAILLE][TAILLE];
        for (int i = 0; i < TAILLE; i++) {
            for (int j = 0; j < TAILLE; j++) {
                board[i][j] = Mark.EMPTY;
            }
        }
    }

    // Place la pièce 'mark' sur le plateau, à la position spécifiée dans Move
    public void play(Move m, Mark mark) {
        if (board[m.getRow()][m.getCol()] == Mark.EMPTY) {
            board[m.getRow()][m.getCol()] = mark;
        }
    }

    //permet d'annuler un déplacement
    public void undo(Move m) {

        board[m.getRow()][m.getCol()] = Mark.EMPTY;
    }

    //Permet d'évaluer si c'est une victoire, un échec ou un match null
    // retourne  100 pour une victoire
    //          -100 pour une défaite
    //           0   pour un match nul
    public int evaluate(Mark mark) {
        Mark opponent = (mark == Mark.X) ? Mark.O : Mark.X;

        for (int i = 0; i < 3; i++) {
            if (board[i][0] == mark && board[i][1] == mark && board[i][2] == mark) return 100;
            if (board[0][i] == mark && board[1][i] == mark && board[2][i] == mark) return 100;
        }

        if (board[0][0] == mark && board[1][1] == mark && board[2][2] == mark) return 100;
        if (board[0][2] == mark && board[1][1] == mark && board[2][0] == mark) return 100;

        for (int i = 0; i < 3; i++) {
            if (board[i][0] == opponent && board[i][1] == opponent && board[i][2] == opponent) return -100;
            if (board[0][i] == opponent && board[1][i] == opponent && board[2][i] == opponent) return -100;
        }

        if (board[0][0] == opponent && board[1][1] == opponent && board[2][2] == opponent) return -100;
        if (board[0][2] == opponent && board[1][1] == opponent && board[2][0] == opponent) return -100;

        for (int i = 0; i < TAILLE; i++) {
            for (int j = 0; j < TAILLE; j++) {
                if (board[i][j] == Mark.EMPTY) return 0;
            }
        }

        return 0;
    }

    //Permet d'avoir les déplacements de jeu possibles
    public ArrayList<Move> getAvailableMoves() {
        ArrayList<Move> moves = new ArrayList<>();
        for (int i = 0; i < TAILLE; i++) {
            for (int j = 0; j < TAILLE; j++) {
                if (board[i][j] == Mark.EMPTY) {
                    moves.add(new Move(i, j));
                }
            }
        }
        return moves;
    }

    //Permet d'afficher le tableau de grille
    public void displayBoard() {
        for (int i = 0; i < TAILLE; i++) {
            for (int j = 0; j < TAILLE; j++) {
                System.out.print(board[i][j] == Mark.EMPTY ? "-" : board[i][j]);
                if (j < 2) System.out.print(" |");
            }
            System.out.println();
        }
        System.out.println();
    }
}
