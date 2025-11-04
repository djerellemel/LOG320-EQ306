import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Classe Plateau
 * Représente le plateau de jeu du projet Lines of Action.
 * Elle gère la configuration initiale, les déplacements, la génération des mouvements possibles
 * et l’évaluation heuristique pour le joueur.
 */

public class Plateau {

    /** Joueur maximisant (utilisé dans l’algorithme Minimax). */
    public Player playerMax;

    /** Joueur minimisant (utilisé dans l’algorithme Minimax). */
    public Player playerMin;

    /** Dernier mouvement choisi ou à envoyer au serveur. */
    public String moveToSend;

    /** Tableau des lettres représentant les colonnes du plateau (A–H). */
    private String[] lettres = {"A", "B", "C", "D", "E", "F", "G", "H"};

    /** Colonne courante utilisée pour la conversion index → position (A–H). */
    private int colonne = 0;

    /** Structure principale stockant l’état du plateau : clé = position (ex: "D6"), valeur = symbole ('X', 'O', '.'). */
    private Map<String, Character> mapPlateau = new LinkedHashMap<>();

    /** Nombre de pièces restantes pour un joueur donné (utile pour les vérifications de fin de partie). */
    private int piecesRestante = 12;

    /** Tableau des décalages en X pour les 8 directions de mouvement possibles. */
    private int[] dx = {0, 1, 1, 1, 0, -1, -1, -1};

    /** Tableau des décalages en Y pour les 8 directions de mouvement possibles. */
    private int[] dy = {1, 1, 0, -1, -1, -1, 0, 1};

    /** Liste mémorisant les positions précédentes du joueur courant. */
    private ArrayList<String> previousPlayerPieces = new ArrayList<>();

    /** Liste mémorisant les positions précédentes de l’adversaire. */
    private ArrayList<String> previousOpponentPieces = new ArrayList<>();

    /**
     * Constructeur principal : initialise le plateau à partir d’une configuration donnée.
     * ici on recupere la string generé par le serveur afin d'avoir la configuration de base du tableau.
     * on place les pions( ici nos pions sont x pour les rouges et O pour les noirs)
     * @param configPlateau Chaîne représentant la configuration initiale envoyée par le serveur.
     */
    public Plateau(String configPlateau) {
        String plateauToTrim = configPlateau.replaceAll("\\s", "");
        char tab[] = plateauToTrim.toCharArray();
        int j = 1;

        for (int i = 1; i <= tab.length; i++) {
            if (tab[i - 1] == '2') {
                mapPlateau.put(lettres[colonne] + String.valueOf(9 - j), 'X');
            } else if (tab[i - 1] == '4') {
                mapPlateau.put(lettres[colonne] + String.valueOf(9 - j), 'O');
            } else {
                mapPlateau.put(lettres[colonne] + String.valueOf(9 - j), '.');
            }

            if (i % 8 == 0) {
                colonne = 0;
                j++;
            } else {
                colonne++;
            }
        }
    }

    /**
     * Constructeur alternatif : copie un plateau existant.
     * @param plateau Plateau existant sous forme de map.
     */
    public Plateau(Map<String, Character> plateau) {
        this.mapPlateau.putAll(plateau);
    }

    /** Constructeur vide (utile pour initialisation par défaut). */
    public Plateau() {}

    /**
     * Retourne la structure représentant le plateau.
     * @return La Map contenant les positions et les symboles associés.
     */
    public Map<String, Character> getMapPlateau() {
        return this.mapPlateau;
    }

    /**
     * Génère la liste des mouvements possibles pour une pièce donnée.
     * @param position Position de départ (ex: "C8").
     * @param player Symbole du joueur ('X' ou 'O').
     * @return Liste de mouvements valides (ex: ["C8-C5", "C8-F5"]).
     */
    public List<String> generateMovements(String position, char player) {
        List<String> movements = new ArrayList<>();
        char currentPlayer = player;

        // Vérifie si la position est valide et non vide
        if (isValidStartPosition(position, currentPlayer)) {
            int row = position.charAt(1) - '0';
            int col = position.charAt(0) - 'A';

            // Génère les mouvements pour chaque direction
            movements.addAll(generateVerticalMovements(row, col, currentPlayer));
            movements.addAll(generateHorizontalMovements(row, col, currentPlayer));
            movements.addAll(generateDiagonalForwardMovements(row, col, currentPlayer));
            movements.addAll(generateDiagonalBackwardMovements(row, col, currentPlayer));
        }

        return movements;
    }

    /**
     * Vérifie si la position de départ est valide pour le joueur.
     * @param position Position de départ.
     * @param currentPlayer Symbole du joueur.
     * @return true si la position contient une pièce du joueur, false sinon.
     */
    private boolean isValidStartPosition(String position, char currentPlayer) {
        return mapPlateau.containsKey(position) && mapPlateau.get(position).equals(currentPlayer);
    }

    /**
     * Compte le nombre total de pièces (toutes couleurs confondues) dans la colonne.
     * @param row Ligne de départ.
     * @param col Colonne de départ.
     * @return Nombre total de pièces verticalement.
     */
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

    /**
     * Compte le nombre total de pièces sur la ligne horizontale.
     * @param row Ligne du pion.
     * @param col Colonne du pion.
     * @return Nombre de pièces sur la ligne.
     */
    private int countHorizontalPieces(int row, int col) {
        int count = 0;
        for (int j = 0; j < 8; j++) {
            String position = lettres[j] + row;
            if (mapPlateau.containsKey(position) && !mapPlateau.get(position).equals('.')) {
                count++;
            }
        }
        return count;
    }

    /**
     * Compte le nombre de pièces présentes sur la diagonale ↘↖.
     * @param row Ligne du pion.
     * @param col Colonne du pion.
     * @return Nombre total de pièces sur la diagonale.
     */
    private int countDiagonalForwardPieces(int row, int col) {
        int count = 0;
        // Ascendant
        for (int i = row, j = col; i >= 1 && j >= 0; i--, j--) {
            String position = lettres[j] + i;
            if (!mapPlateau.get(position).equals('.')) count++;
        }
        // Descendant
        for (int i = row + 1, j = col + 1; i <= 8 && j < 8; i++, j++) {
            String position = lettres[j] + i;
            if (!mapPlateau.get(position).equals('.')) count++;
        }
        return count;
    }

    /**
     * Compte le nombre de pièces présentes sur la diagonale ↙↗.
     * @param row Ligne du pion.
     * @param col Colonne du pion.
     * @return Nombre total de pièces sur cette diagonale.
     */
    private int countDiagonalBackwardPieces(int row, int col) {
        int count = 0;
        for (int i = row + 1, j = col - 1; i <= 8 && j >= 0; i++, j--) {
            String position = lettres[j] + i;
            if (!mapPlateau.get(position).equals('.')) count++;
        }
        for (int i = row, j = col; i >= 1 && j < 8; i--, j++) {
            String position = lettres[j] + i;
            if (!mapPlateau.get(position).equals('.')) count++;
        }
        return count;
    }

    /**
     * Génère les mouvements verticaux valides pour une pièce donnée.
     * @param row Ligne actuelle.
     * @param col Colonne actuelle.
     * @param currentPlayer Joueur courant.
     * @return Liste des mouvements verticaux possibles.
     */
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

    /**
     * Génère les mouvements horizontaux valides pour une pièce donnée.
     * @param row Ligne actuelle.
     * @param col Colonne actuelle.
     * @param currentPlayer Joueur courant.
     * @return Liste des mouvements horizontaux possibles.
     */
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

    /**
     * Génère les mouvements diagonaux ↘↖ pour une pièce donnée.
     * @param row Ligne actuelle.
     * @param col Colonne actuelle.
     * @param currentPlayer Joueur courant.
     * @return Liste des mouvements diagonaux ↘↖ possibles.
     */
    public List<String> generateDiagonalForwardMovements(int row, int col, char currentPlayer) {
        List<String> movements = new ArrayList<>();
        int count = countDiagonalForwardPieces(row, col);
        // Mouvement en diagonale vers le haut et la gauche
        int newRow = row - count;
        int newCol = col - count;
        if (isValidMove(newRow, newCol) && isMoveValidForPiece(row, col, newRow, newCol, currentPlayer))
            movements.add(generateMoveString(row, col, newRow, newCol));
        // Mouvement en diagonale vers le bas et la droite
        int newRow2 = row + count;
        int newCol2 = col + count;
        if (isValidMove(newRow2, newCol2) && isMoveValidForPiece(row, col, newRow2, newCol2, currentPlayer))
            movements.add(generateMoveString(row, col, newRow2, newCol2));
        return movements;
    }

    /**
     * Génère les mouvements diagonaux ↙↗ pour une pièce donnée.
     * @param row Ligne actuelle.
     * @param col Colonne actuelle.
     * @param currentPlayer Joueur courant.
     * @return Liste des mouvements diagonaux ↙↗ possibles.
     */
    public List<String> generateDiagonalBackwardMovements(int row, int col, char currentPlayer) {
        List<String> movements = new ArrayList<>();
        int count = countDiagonalBackwardPieces(row, col);
         // Mouvement en diagonale vers le haut et la droite
        int newRow = row - count;
        int newCol = col + count;
        if (isValidMove(newRow, newCol) && isMoveValidForPiece(row, col, newRow, newCol, currentPlayer))
            movements.add(generateMoveString(row, col, newRow, newCol));
        // Mouvement en diagonale vers le bas et la gauche
        int newRow2 = row + count;
        int newCol2 = col - count;
        if (isValidMove(newRow2, newCol2) && isMoveValidForPiece(row, col, newRow2, newCol2, currentPlayer))
            movements.add(generateMoveString(row, col, newRow2, newCol2));
        return movements;
    }

    /**
     * Vérifie si un déplacement est valide pour une pièce donnée selon les règles du jeu.
     * @param fromRow Ligne de départ.
     * @param fromCol Colonne de départ.
     * @param toRow Ligne d’arrivée.
     * @param toCol Colonne d’arrivée.
     * @param currentPlayer Joueur courant.
     * @return true si le mouvement est valide, false sinon.
     */
    private boolean isMoveValidForPiece(int fromRow, int fromCol, int toRow, int toCol, char currentPlayer) {
        String toPosition = lettres[toCol] + toRow;
        char playerSymbol = currentPlayer;
        
        // Vérifier si la case de destination est valide et s'il y a un pion de son camp sur la case de destination
        if (!isValidMove(toRow, toCol) || 
           (mapPlateau.containsKey(toPosition) && mapPlateau.get(toPosition).equals(playerSymbol))) {
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

    /**
     * Vérifie si le chemin entre deux positions est libre (aucun pion adverse entre les deux).
     * @param fromRow Ligne de départ.
     * @param fromCol Colonne de départ.
     * @param toRow Ligne d’arrivée.
     * @param toCol Colonne d’arrivée.
     * @param currentPlayer Joueur courant.
     * @return true si le chemin est libre, false sinon.
     */
    private boolean isPathClear(int fromRow, int fromCol, int toRow, int toCol, char currentPlayer) {
        int rowIncrement = Integer.signum(toRow - fromRow);
        int colIncrement = Integer.signum(toCol - fromCol);
        int i = fromRow + rowIncrement;
        int j = fromCol + colIncrement;

        while (i != toRow || j != toCol) {
            String position = lettres[j] + i;
            char opponentSymbol = (currentPlayer == 'O') ? 'X' : 'O';
            if (mapPlateau.containsKey(position) && mapPlateau.get(position).equals(opponentSymbol)) {
                return false; // Il y a un pion adverse entre la position actuelle et la position future
            }
            i += rowIncrement;
            j += colIncrement;
        }
        return true; 
    }

    /**
     * Vérifie si une position donnée se trouve à l’intérieur du plateau.
     * @param row Ligne.
     * @param col Colonne.
     * @return true si la position est valide, false sinon.
     */
    private boolean isValidMove(int row, int col) {
        return row >= 1 && row <= 8 && col >= 0 && col < 8;
    }

    /**
     * Génère une représentation textuelle du mouvement.
     * @param fromRow Ligne de départ.
     * @param fromCol Colonne de départ.
     * @param toRow Ligne d’arrivée.
     * @param toCol Colonne d’arrivée.
     * @return Chaîne de la forme "A3-B3".
     */
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

        if (cpuConnectedPieces == cpuPieces) {
            evaluation = Integer.MAX_VALUE;
        } else if (adverseConnectedPieces == adversePieces) {
            evaluation = Integer.MIN_VALUE;
        } else {
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
     * La mobilité représente le nombre total de mouvements possibles pour toutes les pièces du joueur.
     * Plus le joueur a de mouvements disponibles, plus il a de flexibilité stratégique.
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
     * Le contrôle du centre mesure la présence des pions dans la zone centrale du plateau (C3 à F6),
     * ce qui favorise la connectivité et la mobilité stratégique.
     *
     * @param player Le joueur pour lequel le score de contrôle du centre est calculé.
     * @return Le score de contrôle du centre du joueur.
     */
    private int calculateCenterControlScore(char player) {
        int centerControlScore = 0;
        for (String key : mapPlateau.keySet()) {
            if (mapPlateau.get(key) == player) {
                char col = key.charAt(0);
                int row = Integer.parseInt(key.substring(1));
                if ((col >= 'C' && col <= 'F') && (row >= 3 && row <= 6)) {
                    centerControlScore += 1;// Ajoute une valeur pour chaque pion au centre du plateau
                }
            }
        }
        return centerControlScore;
    }

    /**
     * Calcule le score de convergence pour un joueur donné.
     * Le score de convergence mesure à quel point les pièces d’un joueur sont rapprochées
     * les unes des autres, favorisant la connectivité et donc les conditions de victoire.
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
                        int distance = calculateDistance(key1, key2);// Calculer la distance entre deux pions
                        if (distance <= 2) {
                            convergenceScore += 1; // Si deux pions sont proches, on ajoute un point
                        }
                    }
                }
            }
        }
        return convergenceScore;
    }

    /**
     * Calcule la distance entre deux positions sur le plateau.
     * Cette distance est utilisée dans le calcul de la convergence.
     *
     * @param position1 La première position (ex. "D6").
     * @param position2 La deuxième position (ex. "E5").
     * @return La distance entre les deux positions, basée sur la métrique de Chebyshev.
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

    /**
     * Identifie les positions connectées d’un joueur sur le plateau.
     * Cette méthode explore toutes les pièces connectées via un parcours DFS.
     *
     * @param player Le symbole du joueur ('X' ou 'O').
     * @return Un ensemble de positions représentant le groupe connecté le plus grand.
     */
    private Set<String> examinePlateau(char player) {
        String startPosition = findStartPosition(player);
        Set<String> allVisitedPositions = new HashSet<>();
        Set<String> visitedPositions = new HashSet<>();
        Set<String> longuestPositions = new HashSet<>();

        dfs(startPosition, player, visitedPositions, longuestPositions, allVisitedPositions);
        return longuestPositions;
    }

    /**
     * Parcours récursif (DFS) pour explorer toutes les positions connectées.
     *
     * @param currentPosition La position actuelle.
     * @param player Le joueur à explorer.
     * @param visitedPositions Ensemble des positions déjà visitées.
     * @param longuestPositions Ensemble contenant la plus grande chaîne connectée.
     * @param allVisitedPositions Ensemble global de toutes les positions parcourues.
     */
    private void dfs(String currentPosition, char player, Set<String> visitedPositions,
                     Set<String> longuestPositions, Set<String> allVisitedPositions) {

        List<String> neighbors = getNeighbors(currentPosition, player);
        allVisitedPositions.add(currentPosition);
        visitedPositions.add(currentPosition);

        if (longuestPositions.size() < visitedPositions.size()) {
            longuestPositions.clear();
            longuestPositions.addAll(visitedPositions);
        }

        for (String neighbor : neighbors) {
            if (!visitedPositions.contains(neighbor) && !allVisitedPositions.contains(neighbor))
                dfs(neighbor, player, visitedPositions, longuestPositions, allVisitedPositions);
        }

        if (neighbors.isEmpty() || visitedPositionsContainsAllNeighboursFromLastPosition(visitedPositions, neighbors)) {
            String key = nextStartPosition(visitedPositions, allVisitedPositions, player);
            if (key != null) {
                if (key.charAt(0) == '0') {
                    key = key.substring(1);
                    dfs(key, player, visitedPositions, longuestPositions, allVisitedPositions);
                } else if (key.charAt(0) == '1') {
                    key = key.substring(1);
                    Set<String> availablePositions = new HashSet<>();
                    dfs(key, player, availablePositions, longuestPositions, allVisitedPositions);
                }
            }
        }
    }

    /**
     * Récupère les voisins directs (8 directions) d’une position donnée appartenant au joueur.
     *
     * @param currentPosition Position courante.
     * @param player Joueur concerné.
     * @return Liste des positions voisines contenant une pièce du même joueur.
     */
    private List<String> getNeighbors(String currentPosition, char player) {
        List<String> neighbors = new ArrayList<>();

        char col = currentPosition.charAt(0);
        int row = Integer.parseInt(currentPosition.substring(1));

        for (int i = 0; i < 8; i++) {
            int neighborCol = col + dx[i];
            int neighborRow = row + dy[i];

            if (neighborCol >= 'A' && neighborCol <= 'H' && neighborRow >= 1 && neighborRow <= 8) {
                String neighborPosition = String.valueOf((char) neighborCol) + neighborRow;

                if (mapPlateau.get(neighborPosition).equals(player)) {
                    neighbors.add(neighborPosition);
                }
            }
        }
        return neighbors;
    }

    /**
     * Trouve une position de départ valide pour un joueur donné.
     * @param player Symbole du joueur.
     * @return Une position de départ contenant une pièce du joueur.
     */
    private String findStartPosition(char player) {
        List<String> listKeys = new ArrayList<>(mapPlateau.keySet());
        String keyToStart = "";
        int i = 0;

        while (keyToStart.equals("")) {
            if (mapPlateau.get(listKeys.get(i)).equals(player))
                keyToStart = listKeys.get(i);
            i++;
        }
        return keyToStart;
    }

    /**
     * Détermine la prochaine position à explorer lors du parcours DFS.
     * @param visitedPositions Positions déjà visitées dans la composante courante.
     * @param allVisitedPositions Ensemble global des positions visitées.
     * @param player Joueur concerné.
     * @return Une chaîne indiquant la prochaine position à visiter (préfixée par 0 ou 1 selon le type de reprise).
     */
    private String nextStartPosition(Set<String> visitedPositions, Set<String> allVisitedPositions, char player) {
        List<String> listKeys = new ArrayList<>(mapPlateau.keySet());
        String keyNeighborsToVisitedPosition = "";
        String keyNextStartPosition = "";

        for (String key : listKeys) {
            if (mapPlateau.get(key).equals(player)) {
                if (!visitedPositions.contains(key) && isKeyNeighborToVisitedPosition(key, visitedPositions, player)) {
                    keyNeighborsToVisitedPosition = key;
                } else if (!allVisitedPositions.contains(key)) {
                    keyNextStartPosition = key;
                }
            }
        }
        if (!keyNeighborsToVisitedPosition.equals("")) {
            return "0" + keyNeighborsToVisitedPosition;
        } else if (!keyNextStartPosition.equals("")) {
            return "1" + keyNextStartPosition;
        } else {
            return null;
        }
    }

    /**
     * Vérifie si toutes les positions voisines d’une position ont déjà été visitées.
     *
     * @param visitedPositions Ensemble des positions visitées.
     * @param neighbours Liste des voisins de la position courante.
     * @return true si toutes les positions voisines sont déjà visitées, false sinon.
     */
    private boolean visitedPositionsContainsAllNeighboursFromLastPosition(Set<String> visitedPositions, List<String> neighbours) {
        for (String neighbour : neighbours) {
            if (!visitedPositions.contains(neighbour))
                return false;
        }
        return true;
    }

    /**
     * Vérifie si une position donnée est voisine d’une des positions déjà visitées.
     *
     * @param key Position à vérifier.
     * @param visitedPositions Ensemble des positions visitées.
     * @param player Joueur concerné.
     * @return true si la position est voisine d’une position visitée, false sinon.
     */
    private boolean isKeyNeighborToVisitedPosition(String key, Set<String> visitedPositions, char player) {
        List<String> voisins = getNeighbors(key, player);
        for (String position : visitedPositions) {
            if (voisins.contains(position)) {
                return true;
            }
        }
        return false;
    }

        /**
     * Lance l'algorithme Minimax avec élagage alpha-bêta pour déterminer le meilleur coup.
     * Cette méthode initialise les bornes alpha et bêta, puis appelle la fonction récursive principale.
     *
     * @param player Le symbole du joueur ('X' ou 'O').
     * @param plateau L'état actuel du plateau.
     */
    public void miniMax(char player, Plateau plateau) {
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        algoMinMax(plateau, playerMax, 3, alpha, beta);
    }

    /**
     * Implémente l’algorithme Minimax avec élagage alpha-bêta.
     * Cet algorithme évalue récursivement les positions du jeu jusqu’à une profondeur donnée
     * afin de déterminer le coup optimal pour le joueur courant.
     * 
     * @param plateau L’état actuel du plateau.
     * @param joueur Le joueur dont c’est le tour.
     * @param profondeur La profondeur de recherche (ex. 3 pour une recherche limitée à 3 niveaux).
     * @param alpha La meilleure valeur maximale connue (borne inférieure).
     * @param beta La meilleure valeur minimale connue (borne supérieure).
     * @return La meilleure évaluation trouvée pour ce nœud.
     */
    public int algoMinMax(Plateau plateau, Player joueur, int profondeur, int alpha, int beta) {
        if (profondeur == 0) {
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
                    tab[1] = tab[1].substring(0, 2);
                    previousCoup = this.mapPlateau.get(tab[1]);

                    board.play(mouvement, joueur);

                    if (board.evaluate(joueur) == Integer.MAX_VALUE && profondeur == 3) {
                        bestMove = Integer.MAX_VALUE;
                        moveToSend = mouvement;
                        return Integer.MAX_VALUE;
                    }

                    int move = algoMinMax(board, playerMin, profondeur - 1, alpha, beta);
                    board.undoPlay(mouvement, joueur, previousCoup);

                    if (move > bestMove) {
                        bestMove = move;
                        if (profondeur == 3) {
                            moveToSend = mouvement;
                            if (move == Integer.MAX_VALUE)
                                return bestMove;
                        }
                    }
                    alpha = Math.max(alpha, move);
                    if (beta <= alpha) {
                        return bestMove; // Coupure alpha
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
                    tab[1] = tab[1].substring(0, 2);
                    previousCoup = this.mapPlateau.get(tab[1]);

                    board.play(mouvement, joueur);
                    int move = algoMinMax(board, playerMax, profondeur - 1, alpha, beta);
                    // au moment de undo je passe ce qu'il y'avait dans la case d'arrivé du play
                    board.undoPlay(mouvement, joueur, previousCoup);

                    if (move < worstMove) {
                        worstMove = move;
                    }
                    beta = Math.min(beta, move);
                    if (beta <= alpha) {
                        return worstMove; // Coupure bêta
                    }
                }
            }
            return worstMove;
        }
    }

    /**
     * Récupère les positions contenant les pions du joueur spécifié.
     * Cette méthode sert à limiter la recherche Minimax aux pièces appartenant au joueur courant.
     *
     * @param player Le joueur dont on veut récupérer les positions.
     * @param plateau Le plateau à analyser.
     * @return Liste des positions contenant les pions du joueur.
     */
    private List<String> getKeys(Player player, Plateau plateau) {
        List<String> keysPlateau = new ArrayList<>(plateau.getMapPlateau().keySet());
        List<String> keysFiltered = new ArrayList<>();

        for (String key : keysPlateau) {
            if (mapPlateau.get(key).equals(player.getCurrent()))
                keysFiltered.add(key);
        }
        return keysFiltered;
    }

    /**
     * Affiche le plateau de jeu actuel sous forme lisible dans la console.
     * Chaque ligne correspond à une rangée du plateau.
     * Les points (.) indiquent les cases vides.
     */
    public void printPlateau() {
        List<String> keys = new ArrayList<>(mapPlateau.keySet());
        int i = 1;
        for (String key : keys) {
            char value = mapPlateau.get(key);
            System.out.print(value + "   ");
            if (i % 8 == 0) {
                System.out.println();
            }
            i++;
        }
        System.out.println("-------------------------");
    }

    /**
     * Joue un coup sur le plateau.
     * Cette méthode met à jour l’état interne en déplaçant le pion et en supprimant la pièce capturée le cas échéant.
     * 
     * @param mouvement Mouvement au format "D6-D8".
     * @param joueur Le joueur effectuant le coup.
     */
    public void play(String mouvement, Player joueur) {
        String tab[] = mouvement.split("-");
        tab[0] = tab[0].substring(0, 2);
        tab[1] = tab[1].substring(0, 2);

        // Sauvegarde des positions des pièces capturées pour restaurer lors du backtracking.
        if (joueur == playerMax) {
            if (this.mapPlateau.get(tab[1]).equals(joueur.getOppenent())) {
                previousOpponentPieces.add(tab[1]);
            }
        } else if (joueur == playerMin) {
            if (this.mapPlateau.get(tab[1]).equals(joueur.getOppenent())) {
                previousPlayerPieces.add(tab[1]);
            }
        }

        this.mapPlateau.put(tab[1].trim(), this.mapPlateau.get(tab[0].trim()));
        this.mapPlateau.put(tab[0], '.');
    }

    /**
     * Annule un coup joué précédemment sur le plateau.
     * Cette méthode est utilisée dans le backtracking du Minimax pour restaurer l’état précédent.
     *
     * @param mouvement Mouvement joué précédemment (ex: "D6-D8").
     * @param player Joueur qui avait joué le coup.
     * @param previousCoup La valeur originale de la case d’arrivée avant le déplacement.
     */
    public void undoPlay(String mouvement, Player player, char previousCoup) {
        String tab[] = mouvement.split("-");
        this.mapPlateau.put(tab[0].trim(), this.mapPlateau.get(tab[1].trim()));

        // Restaure la pièce d’origine sur la case d’arrivée
        if(player == playerMax){
            this.mapPlateau.put(tab[1], previousCoup);
        }else if(player == playerMin){
           this.mapPlateau.put(tab[1], previousCoup);
        }
    }

    /**
     * Initialise les joueurs (maximisant et minimisant) selon le symbole reçu du serveur.
     * Le joueur Max est celui qui commence, le joueur Min représente l’adversaire.
     *
     * @param i Le symbole du joueur initial ('1' ou '2').
     */
    public void setPlayers(char i) {
        playerMax = new Player(i);
        if (i == '1')
            playerMin = new Player('2');
        else
            playerMin = new Player('1');
    }
}


   
