
public class Main {
    public static void main(String[] args) {

        System.out.println("Hello welcome to the game Lines Of Action, let's Start the game");
        String setplateau = "0222222040040004400200044000000440000004400000044000000402222220";
        Plateau plateau = new Plateau(setplateau);
        // Affiche le plateau initial
        plateau.printPlateau();

        System.out.println("-------------------------------");
 /*    
        plateau.play("A5-C5", playerMax);
        plateau.play("D6-D8", playerMin);
        plateau.play("A7-D4", playerMax);
        plateau.play("F8-H6", playerMin);
        plateau.play("A4-A8", playerMax);
        plateau.play("B8-E5", playerMin);

      plateau.play("C8-E6");
        plateau.play("H4-F6");
        plateau.play("B8-D6");
        plateau.play("H3-F1");

        plateau.printPlateau();

        // Génère et affiche les mouvements possibles pour la position après le mouvement
        String positionAfterMove = "A3"; // Choisir la position après le mouvement
        List<String> movements = plateau.generateMovements(positionAfterMove);

        System.out.println("Movements possible after the move at " + positionAfterMove + ":");
        for (String movement : movements) {
            System.out.println(movement);
        }
    */

        //plateau.miniMax('1', plateau);
        //System.out.println(plateau.moveToSend);
        System.out.println(plateau.generateMovements("D8", 'X'));
    }
}
