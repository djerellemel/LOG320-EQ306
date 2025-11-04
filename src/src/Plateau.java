import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Plateau {

    public Player playerMax; 
    public Player playerMin;
    public String moveToSend;
    private String[] lettres={"A","B","C","D","E","F","G","H"};
    private int colonne=0; // permet de changer de colonne quand je veux placer les pion dans mon tableau
    private Map<String,Character> mapPlateau = new LinkedHashMap<>();
    private int piecesRestante = 12;
    
    private int[] dx = { 0, 1, 1, 1, 0, -1, -1, -1 };
    private int[] dy = { 1, 1, 0, -1, -1, -1, 0, 1 };

    private ArrayList<String> previousPlayerPieces = new ArrayList<>();
    private ArrayList<String> previousOpponentPieces = new ArrayList<>();

    /**
     * configPlateau le string generé par le serveur au depart
     * ici on recupere la string generé par le serveur afin d'avoir la configuration de base du tableau.
     * on place les pions( ici nos pions sont x pour les rouges et O pour les noirs)
     *
     * le but ici est de faciliter la recherche et la modification. exple: si le serveur joue D6-D8 on fai la recherche de D8 dans la
     * mapPlateau et on place le pion du serveur de D6 a D8 et on supprime ce qui se trouve dans D6.
     * */
    public Plateau(String configPlateau){
        String plateauToTrim = configPlateau.replaceAll("\\s", "");
        char tab[]=plateauToTrim.toCharArray();
        int j=1;

        for(int i=1;i<=tab.length;i++){

                if(tab[i-1]=='2'){
                    mapPlateau.put(lettres[colonne]+String.valueOf(9-j),'X');
                }else if(tab[i-1]=='4'){
                    mapPlateau.put(lettres[colonne]+String.valueOf(9-j),'O');
                }else{
                    mapPlateau.put(lettres[colonne]+String.valueOf(9-j),'.');
                }

                if(i%8==0){
                    colonne=0;
                    j++;
                }else{
                    colonne++;
                }
        }
    }

    public Plateau(Map<String,Character> plateau){
        this.mapPlateau.putAll(plateau);
    }
    public Plateau() {
        //TODO Auto-generated constructor stub
    }

    /**
     *  prends en entrée la position du pion et retourne la liste des jeux possible: Exple:  C8 retur <liste> C9,B3, E9 </liste>
     *  les lettres A,B,C,D.... doivent etre en majuscule...
     * */
    public Map<String,Character> getMapPlateau() {
        return this.mapPlateau;
    }

    /**
     *  prends en entrée la position du pion et retourne la liste des jeux possible: Exple:  C8 retur <liste> C9,B3, E9 </liste>
     *  les lettres A,B,C,D.... doivent etre en majuscule...
     * */
    public List<String> generateMovements(String position, char player) {
        List<String> movements = new ArrayList<>();
        char currentPlayer = player;

        // Vérifie si la position est valide et non vide
        //System.out.println("Je suis le joueur #"+currentPlayer+"\n");
        if (isValidStartPosition(position, currentPlayer)) {
            int row = position.charAt(1) - '0';
            int col = position.charAt(0) - 'A';
        
            // Génère les mouvements pour chaque direction
            //System.out.println("generateVerticalMovements :"+generateVerticalMovements(row, col, currentPlayer));
            movements.addAll(generateVerticalMovements(row, col, currentPlayer));

            //System.out.println("generateHorizontalMovements :"+generateHorizontalMovements(row, col, currentPlayer));
            movements.addAll(generateHorizontalMovements(row, col, currentPlayer));

            //System.out.println("generateDiagonalForwardMovements :"+generateDiagonalForwardMovements(row, col, currentPlayer));
            movements.addAll(generateDiagonalForwardMovements(row, col, currentPlayer));

            //System.out.println("generateDiagonalBackwardMovements :"+generateDiagonalBackwardMovements(row, col, currentPlayer));
            movements.addAll(generateDiagonalBackwardMovements(row, col, currentPlayer));
        }

        return movements;
    }

    private boolean isValidStartPosition(String position, char currentPlayer) {
        return mapPlateau.containsKey(position) && mapPlateau.get(position).equals(currentPlayer);
    }

    private int countVerticalPieces(int row, int col) {
        int count = 0;
        for (int i = 1; i <= 8; i++) {
            String position = lettres[col] + i;
            if (mapPlateau.containsKey(position) && !mapPlateau.get(position).equals('.')) {
                count++;
            }
        }

        return count;
    }

    private int countHorizontalPieces(int row, int col) {
        int count = 0;

        for (int j = 0; j < 8; j++) {
            String position = lettres[j] + row;
            if ( mapPlateau.containsKey(position) && !mapPlateau.get(position).equals('.')) {
                count++;
            }
        }

        return count;
    }

    private int countDiagonalForwardPieces(int row, int col) {
        int count = 0;

        // Ascendant
        for (int i = row , j = col; i >= 1 && j >= 0; i--, j--) {
            String position = lettres[j] + i;
            if (!mapPlateau.get(position).equals('.')) {
                count++;
            } 
        }

        // Descendant
        for (int i = row + 1, j = col + 1; i <= 8 && j < 8; i++, j++) {
            String position = lettres[j] + i;
            if ( !mapPlateau.get(position).equals('.')) {
                count++;
            } 
        }

        return count;
    }

    private int countDiagonalBackwardPieces(int row, int col) {
        int count = 0;

        for (int i = row + 1, j = col - 1; i <= 8 && j >= 0; i++, j--) {
            String position = lettres[j] + i;
            if (!mapPlateau.get(position).equals('.')) {
                count++;
            } 
        }

        for (int i = row, j = col; i >= 1 && j < 8; i--, j++) {
            String position = lettres[j] + i;
            if (!mapPlateau.get(position).equals('.')) {
                count++;
            } 
        }

        return count;
    }

    public List<String> generateVerticalMovements(int row, int col, char currentPlayer) {
        List<String> movements = new ArrayList<>();

        for (int i = -1; i <= 1; i++) {
            if (i == 0) continue;

            int newRow = row + i * countVerticalPieces(row, col);
            int newCol = col;

            if (isValidMove(newRow, newCol) && isMoveValidForPiece(row, col, newRow, newCol, currentPlayer)) {
                movements.add(generateMoveString(row, col, newRow, newCol));
            }
        }

        return movements;
    }

    public List<String> generateHorizontalMovements(int row, int col, char currentPlayer) {
        List<String> movements = new ArrayList<>();

        for (int j = -1; j <= 1; j++) {
            if (j == 0) continue;

            int newRow = row;
            int newCol = col + j * countHorizontalPieces(row, col);

            if (isValidMove(newRow, newCol) && isMoveValidForPiece(row, col, newRow, newCol, currentPlayer)) {
                movements.add(generateMoveString(row, col, newRow, newCol));
            }
        }

        return movements;
    }

    public List<String> generateDiagonalForwardMovements(int row, int col, char currentPlayer) {
        List<String> movements = new ArrayList<>();

        int count = countDiagonalForwardPieces(row, col);

        // Mouvement en diagonale vers le haut et la gauche
        int newRow = row - count;
        int newCol = col - count;
        if (isValidMove(newRow, newCol) && isMoveValidForPiece(row, col, newRow, newCol, currentPlayer)) {
            movements.add(generateMoveString(row, col, newRow, newCol));
        }

        // Mouvement en diagonale vers le bas et la droite
        int newRow2 = row + count;
        int newCol2 = col + count;
        if (isValidMove(newRow2, newCol2) && isMoveValidForPiece(row, col, newRow2, newCol2, currentPlayer)) {
            movements.add(generateMoveString(row, col, newRow2, newCol2));
        }

        return movements;
    }

    public List<String> generateDiagonalBackwardMovements(int row, int col, char currentPlayer) {
        List<String> movements = new ArrayList<>();

        int count = countDiagonalBackwardPieces(row, col);

        // Mouvement en diagonale vers le haut et la droite
        int newRow = row - count;
        int newCol = col + count;
        if (isValidMove(newRow, newCol) && isMoveValidForPiece(row, col, newRow, newCol, currentPlayer)) {
            movements.add(generateMoveString(row, col, newRow, newCol));
        }

        // Mouvement en diagonale vers le bas et la gauche
        int newRow2 = row + count;
        int newCol2 = col - count;
        if (isValidMove(newRow2, newCol2) && isMoveValidForPiece(row, col, newRow2, newCol2, currentPlayer)) {
            movements.add(generateMoveString(row, col, newRow2, newCol2));
        }

        return movements;
    }

    private boolean isMoveValidForPiece(int fromRow, int fromCol, int toRow, int toCol, char currentPlayer) {
        String toPosition = lettres[toCol] + toRow;
        char playerSymbol = currentPlayer;
    
        // Vérifier si la case de destination est valide et s'il y a un pion de son camp sur la case de destination
        if (!isValidMove(toRow, toCol) || (mapPlateau.containsKey(toPosition) && mapPlateau.get(toPosition).equals(playerSymbol))) {
            return false;
        }
    
        // Vérifier si le mouvement respecte les règles du déplacement vertical, horizontal, ou diagonal
        boolean isVerticalMove = fromRow == toRow;
        boolean isHorizontalMove = fromCol == toCol;
        boolean isDiagonalMove = Math.abs(fromRow - toRow) == Math.abs(fromCol - toCol);
    
        if (!(isVerticalMove || isHorizontalMove || isDiagonalMove)) {
            return false;
        }
    
        // Vérifier s'il y a un pion adverse entre la position actuelle et la position future
        return isPathClear(fromRow, fromCol, toRow, toCol, currentPlayer);
    }

    private boolean isPathClear(int fromRow, int fromCol, int toRow, int toCol, char currentPlayer) {
        int rowIncrement = Integer.signum(toRow - fromRow);
        int colIncrement = Integer.signum(toCol - fromCol);
    
        int i = fromRow + rowIncrement;
        int j = fromCol + colIncrement;
    
        while (i != toRow || j != toCol) {
            String position = lettres[j] + i;
            char opponentSymbol = (currentPlayer == 'O') ? 'X': 'O';
            if (mapPlateau.containsKey(position) && mapPlateau.get(position).equals(opponentSymbol)) {
                return false; // Il y a un pion adverse entre la position actuelle et la position future
            }
            i += rowIncrement;
            j += colIncrement;
        }
    
        return true; // Le chemin est vide
    }

    private boolean isValidMove(int row, int col) {
        return row >= 1 && row <= 8 && col >= 0 && col < 8;
    }

    private String generateMoveString(int fromRow, int fromCol, int toRow, int toCol) {
        return lettres[fromCol] + fromRow + "-" + lettres[toCol] + toRow;
    }

    /**
 * Évalue la position actuelle du joueur dans le jeu.
 * @param player Le joueur dont la position est évaluée.
 * @return La valeur de l'évaluation de la position.
 */
    public int evaluate(Player player) {
        int cpuPieces = Collections.frequency(mapPlateau.values(), player.getCurrent());
        int adversePieces = Collections.frequency(mapPlateau.values(), player.getOppenent());
        int cpuConnectedPieces = examinePlateau(player.getCurrent()).size();
        int adverseConnectedPieces = examinePlateau(player.getOppenent()).size();

        int mobilityScore = calculateMobilityScore(player.getCurrent());
        int centerControlScore = calculateCenterControlScore(player.getCurrent());
        int convergenceScore = calculateConvergenceScore(player.getCurrent());

        int evaluation = 0;

        if(cpuConnectedPieces == cpuPieces){
            evaluation = Integer.MAX_VALUE;
        }else if(adverseConnectedPieces == adversePieces){
            evaluation = Integer.MIN_VALUE;
        }else{
             // Ajustez les poids en fonction de l'importance relative de chaque composante
            evaluation = (cpuConnectedPieces - adverseConnectedPieces) * 25 +
                          mobilityScore * 10 +
                          centerControlScore * 15 +
                          convergenceScore * 10;
        }
        
        return evaluation;
    }

    /**
     * Calcule le score de mobilité pour un joueur donné.
     * 
     * @param player Le joueur pour lequel le score de mobilité est calculé.
     * @return Le score de mobilité du joueur.
     */
    private int calculateMobilityScore(char player) {
        int mobilityScore = 0;
        for (String key : mapPlateau.keySet()) {
            if (mapPlateau.get(key) == player) {
                List<String> movements = generateMovements(key, player);
                mobilityScore += movements.size(); // Ajoute le nombre de mouvements disponibles pour chaque pion
            }
        }
        return mobilityScore;
    }

    /**
     * Calcule le score de contrôle du centre pour un joueur donné.
     * 
     * @param player Le joueur pour lequel le score de contrôle du centre est
     *               calculé.
     * @return Le score de contrôle du centre du joueur.
     */
    private int calculateCenterControlScore(char player) {
        int centerControlScore = 0;
        for (String key : mapPlateau.keySet()) {
            if (mapPlateau.get(key) == player) {
                char col = key.charAt(0);
                int row = Integer.parseInt(key.substring(1));
                if ((col >= 'C' && col <= 'F') && (row >= 3 && row <= 6)) {
                    centerControlScore += 1; // Ajoute une valeur pour chaque pion au centre du plateau
                }
            }
        }
        return centerControlScore;
    }

    /**
     * Calcule le score de convergence pour un joueur donné.
     * 
     * @param player Le joueur pour lequel le score de convergence est calculé.
     * @return Le score de convergence du joueur.
     */
    private int calculateConvergenceScore(char player) {
        int convergenceScore = 0;
        List<String> keys = new ArrayList<>(mapPlateau.keySet());

        for (int i = 0; i < keys.size(); i++) {
            String key1 = keys.get(i);
            if (mapPlateau.get(key1) == player) {
                for (int j = i + 1; j < keys.size(); j++) {
                    String key2 = keys.get(j);
                    if (mapPlateau.get(key2) == player) {
                        int distance = calculateDistance(key1, key2); // Calculer la distance entre deux pions
                        if (distance <= 2) {
                            convergenceScore += 1; // Ajouter une valeur si deux pions sont proches l'un de l'autre
                        }
                    }
                }
            }
        }

        return convergenceScore;
    }

    /**
     * Calcule la distance entre deux positions sur le plateau.
     * 
     * @param position1 La première position.
     * @param position2 La deuxième position.
     * @return La distance entre les deux positions.
     */
    private int calculateDistance(String position1, String position2) {
        char col1 = position1.charAt(0);
        int row1 = Integer.parseInt(position1.substring(1));
        char col2 = position2.charAt(0);
        int row2 = Integer.parseInt(position2.substring(1));

        int colDiff = Math.abs(col1 - col2);
        int rowDiff = Math.abs(row1 - row2);

        // La distance est le maximum de la différence de colonne et de la différence de
        // ligne
        return Math.max(colDiff, rowDiff);
    }

    private  Set<String> examinePlateau(char player){

            String startPosition = findStartPosition(player);
            Set <String> allVisitedPositions = new HashSet<>();
            Set<String> visitedPositions = new HashSet<>();
            Set<String> longuestPositions = new HashSet<>();

            dfs(startPosition, player, visitedPositions, longuestPositions, allVisitedPositions);

            return longuestPositions;
        }

    private void dfs(String currentPosition, char player, Set<String> visitedPositions, Set<String> longuestPositions,  Set<String> allVisitedPositions){

            List<String> neighbors = getNeighbors(currentPosition, player);
            String key = new String();
            allVisitedPositions.add(currentPosition);
            visitedPositions.add(currentPosition);

            if(longuestPositions.size() < visitedPositions.size()){
                longuestPositions.clear();
                longuestPositions.addAll(visitedPositions);
            }

            for(String neighbor : neighbors){
                if(!visitedPositions.contains(neighbor) && !allVisitedPositions.contains(neighbor))
                    dfs(neighbor, player, visitedPositions, longuestPositions, allVisitedPositions);
            }

            if(neighbors.size() == 0 || visitedPositionsContainsAllNeighboursFromLastPosition(visitedPositions, neighbors)){
                key = nextStartPosition(visitedPositions, allVisitedPositions, player);
                if(key != null){
                    if(key.charAt(0) == '0'){
                        key = key.substring(1);
                        dfs(key, player, visitedPositions, longuestPositions, allVisitedPositions);
                    }else if(key.charAt(0) == '1'){
                        key = key.substring(1);
                        Set<String> availablePositions = new HashSet<>();
                        dfs(key, player, availablePositions, longuestPositions, allVisitedPositions);
                    }
                }
            }       
        }

    private List<String> getNeighbors(String currentPosition, char player){
            List<String> neighbors = new ArrayList<String>();

            char col = currentPosition.charAt(0);
            int row = Integer.parseInt(currentPosition.substring(1));

            for(int i = 0 ; i < 8 ; i++){
                int neighborCol = col + dx[i];
                int neighborRow = row + dy[i];

                if(neighborCol >= 65 && neighborCol <= 72 && neighborRow >= 1 && neighborRow <= 8){
                    String neighborPosition = String.valueOf((char)neighborCol) + neighborRow;

                    if(mapPlateau.get(neighborPosition).equals(player)){
                        neighbors.add(neighborPosition);
                    }
                } 
            }

            return neighbors;
        }

    private String findStartPosition(char player){

            Set<String> keySet = mapPlateau.keySet();
            List<String> listKeys = new ArrayList<String>(keySet);
            String keyToStart = "";
            int i = 0;

            while(keyToStart.equals("")){
                if(mapPlateau.get(listKeys.get(i)).equals(player))
                    keyToStart = listKeys.get(i);
                i++; 
            }
            return keyToStart;
        }

    private String nextStartPosition(Set<String> visitedPositions, Set<String> allVisitedPositions, char player){
        Set<String> keySet = mapPlateau.keySet();
        List<String> listKeys = new ArrayList<String>(keySet);

        String keyNeighborsToVisitedPosition = new String();
        String keyNextStartPosition = new String();

        for(String key : listKeys){
            if(mapPlateau.get(key).equals(player)){
                if(!visitedPositions.contains(key) && isKeyNeighborToVisitedPosition(key, visitedPositions, player)){
                    keyNeighborsToVisitedPosition = key;
                }else if(!allVisitedPositions.contains(key)){
                    keyNextStartPosition = key;
                }
            }
        }
        if(!keyNeighborsToVisitedPosition.equals("")){
            return "0" + keyNeighborsToVisitedPosition;
        }else if(!keyNextStartPosition.equals("")){
            return "1" + keyNextStartPosition;
        }else{
            return null;
        }
    }

    private boolean visitedPositionsContainsAllNeighboursFromLastPosition(Set<String> visitedPositions, List<String> neighbours) {
        for(String neighbour : neighbours){
            if(!visitedPositions.contains(neighbour))
                return false;
        }
        return true;
    }

    private boolean isKeyNeighborToVisitedPosition(String key, Set<String> visitedPositions, char player){
        
        List<String> voisins = getNeighbors(key, player);
        for(String position : visitedPositions){
            if(voisins.contains(position)){
                return true;
            }
        }
        return false;
    } 

    public void miniMax(char player, Plateau plateau){

        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        
        algoMinMax(plateau, playerMax, 3,alpha, beta);
    }

    public int algoMinMax(Plateau plateau, Player joueur, int profondeur, int alpha, int beta) {
        if (profondeur == 0){
            return plateau.evaluate(playerMax);
        }
    
        List<String> keys = getKeys(joueur, plateau);
    
        if (joueur == playerMax) {
            int bestMove = Integer.MIN_VALUE;
            for (String key : keys) {
                List<String> mouvements = generateMovements(key, joueur.getCurrent());
                for (String mouvement : mouvements) {
                    Plateau board = plateau;
                    //Pour eviter les mauvais coups: je conserve la valeur de la case d'arriver dans previousCoup
                    char previousCoup;
                    String tab[] = mouvement.split("-");
                    tab[0] = tab[0].substring(0, 2);
                    tab[1]= tab[1].substring(0, 2);
                    previousCoup=this.mapPlateau.get(tab[1]);
                    board.play(mouvement, joueur);

                    if(board.evaluate(joueur)==Integer.MAX_VALUE && profondeur == 3){
                        bestMove = Integer.MAX_VALUE;
                        moveToSend = mouvement;
                        return Integer.MAX_VALUE;
                    }
                    
                    int move = algoMinMax(board, playerMin, profondeur - 1, alpha, beta);
                    board.undoPlay(mouvement, joueur,previousCoup);

                    if (move > bestMove) {
                        bestMove = move;
                        if (profondeur == 3) {
                            moveToSend = mouvement;
                            if(move == Integer.MAX_VALUE)
                                return bestMove;
                        }
                    }
                    alpha = Math.max(alpha, move);
                    if (beta <= alpha) {
                        return bestMove;
                    }
                }
            }
            return bestMove;
        } else {
            int worstMove = Integer.MAX_VALUE;
            for (String key : keys) {
                List<String> mouvements = generateMovements(key, joueur.getCurrent());
                for (String mouvement : mouvements) {
                    Plateau board = plateau;
                    //Pour eviter les mauvais coups: je conserve la valeur de la case d'arriver dans previousCoup
                    char previousCoup;
                    String tab[] = mouvement.split("-");
                    tab[0] = tab[0].substring(0, 2);
                    tab[1]= tab[1].substring(0, 2);
                    previousCoup=this.mapPlateau.get(tab[1]);
                    //Fin

                    board.play(mouvement, joueur);
                    int move = algoMinMax(board, playerMax, profondeur - 1, alpha, beta);
                    // au moment de undo je passe ce qu'il y'avait dans la case d'arrivé du play
                    board.undoPlay(mouvement, joueur,previousCoup);
                    if (move < worstMove) {
                        worstMove = move;
                    }
                    beta = Math.min(beta, move);
                    if (beta <= alpha) {
                        return worstMove;
                    }
                }
            }
            return worstMove;
        }
    }

    private List<String> getKeys(Player player, Plateau plateau){
        List<String> keysPlateau = new ArrayList<String>(plateau.getMapPlateau().keySet());
        List<String> keysFiltered = new ArrayList<String>();

        for(String key : keysPlateau){
            if(mapPlateau.get(key).equals(player.getCurrent()))
                keysFiltered.add(key);
        }

        return keysFiltered;
    }

    /**
     * impression du plateau a partir de la LinkedHashmap mapPlateau, apres 8 impression sur la meme lettres, il va a la lettres
     * ajout de l'Espace apres chaque impression pour avoir un tableau plus visible
     * les points (.) veulent dire que la case est vide
     * */
    public void printPlateau(){
        List<String> keys = new ArrayList<String>(mapPlateau.keySet());
        int i = 1;
        for(String key : keys){
            char value = mapPlateau.get(key);
            System.out.print(value + "   ");
            if(i % 8 == 0){
                System.out.println();
            }
            i++;
        }
        System.out.println("-------------------------");
    }
    /**
     * recois en entré une chaine ce caractere ex D6-D8
     * split et recupere ce qui se trouve dans D6 et le place dans D8 et efface ce qui etait dans D6 et remplace par le vide ou le point(.)
     * plus tard il faut verifier s'il a le droit de jouer ce coup ( utile lors de la competition a la fin du labo)
     * */
    public void play(String mouvement, Player joueur) {
        String tab[] = mouvement.split("-");
        tab[0] = tab[0].substring(0, 2);
        tab[1]= tab[1].substring(0, 2);

        //Garder en memoire les endroits ou on enleve un pion opposé avec le dernier mouv effectuées (pour la récursion dans algominmax)
        if(joueur == playerMax){
            if(this.mapPlateau.get(tab[1]).equals(joueur.getOppenent())){
                previousOpponentPieces.add(tab[1]);
            }
        }else if(joueur == playerMin){
            if(this.mapPlateau.get(tab[1]).equals(joueur.getOppenent())){
                previousPlayerPieces.add(tab[1]);
            }
        }

        this.mapPlateau.put(tab[1].trim(), this.mapPlateau.get(tab[0].trim()));
        this.mapPlateau.put(tab[0], '.');
    }

    public void undoPlay(String mouvement, Player player,char previousCoup){
        String tab[] = mouvement.split("-");
        this.mapPlateau.put(tab[0].trim(), this.mapPlateau.get(tab[1].trim()));

        if(player == playerMax){
            /*if(previousOpponentPieces.contains(tab[1])){
                previousOpponentPieces.remove(tab[1]);
                this.mapPlateau.put(tab[1], player.getOppenent());
            }else{

            }*/

            this.mapPlateau.put(tab[1], previousCoup);
        }else if(player == playerMin){
            /*if(previousPlayerPieces.contains(tab[1])){
                previousPlayerPieces.remove(tab[1]);
                this.mapPlateau.put(tab[1], player.getOppenent());
            }else{

            }*/

            this.mapPlateau.put(tab[1], previousCoup);
        }
    }

    public void setPlayers(char i){
        playerMax = new Player(i);
        if(i == '1')
            playerMin = new Player('2');
        else
            playerMin = new Player('1');
    }

   
}