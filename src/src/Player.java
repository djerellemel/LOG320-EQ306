public class Player {

    private char current;
    private char oppenent;

    public Player(char i){
        if(i=='1'){
            this.current='O'; //la premiere valeur de la string venu du serveur est le choix de notre champ ( soit rouge, soit noir)
            this.oppenent='X';
        }else{
            this.current='X'; //la premiere valeur de la string venu du serveur est le choix de notre champ ( soit rouge, soit noir)
            this.oppenent='O';
        }

    }

    public char getCurrent() {
        return current;
    }

    public char getOppenent() {
        return oppenent;
    }

    public String getNonValue(String c){
        if(c.equals("X")){
            return "O";
        }else{
            return "X";
        }

    }

}
