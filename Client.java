import java.io.*;
import java.net.*;


class Client {
	public static void main(String[] args) {
         
	Socket MyClient;
	BufferedInputStream input;
	BufferedOutputStream output;
	Plateau plateau = new Plateau();
	Player player = null;
	
	try {
		MyClient = new Socket("localhost", 8888);

	   	input = new BufferedInputStream(MyClient.getInputStream());
		output = new BufferedOutputStream(MyClient.getOutputStream());
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
	   	while(true){
			char cmd = 0;
            cmd = (char)input.read();
            System.out.println(cmd);
            // Debut de la partie en joueur blanc
            if(cmd == '1'){
                byte[] aBuffer = new byte[1024];
				int size = input.available();
				//System.out.println("size " + size);
				input.read(aBuffer,0,size);
                String s = new String(aBuffer).trim();
				
				plateau = new Plateau(s);
				plateau.printPlateau();
				player = new Player(cmd);

                System.out.println(s);


                System.out.println("Nouvelle partie! Vous jouer blanc, entrez votre premier coup : ");
				plateau.miniMax('O', plateau);
                String move = plateau.moveToSend;
				plateau.play(move, player);
				output.write(move.getBytes(),0,move.length());
				output.flush();
            }
            // Debut de la partie en joueur Noir
            if(cmd == '2'){
                System.out.println("Nouvelle partie! Vous jouer noir, attendez le coup des blancs");
                byte[] aBuffer = new byte[1024];
				
				int size = input.available();
				//System.out.println("size " + size);
				input.read(aBuffer,0,size);
                String s = new String(aBuffer).trim();
				plateau = new Plateau(s);
				plateau.printPlateau();
				player = new Player(cmd);
                System.out.println(s);

            }


			// Le serveur demande le prochain coup
			// Le message contient aussi le dernier coup joue.
	    if(cmd == '3'){
		byte[] aBuffer = new byte[16];
				
		int size = input.available();
		System.out.println("size :" + size);
		input.read(aBuffer,0,size);
				
		String s = new String(aBuffer);
		System.out.println("Dernier coup :"+ s);
		plateau.play(s.replaceAll("\\s", ""), player);

		System.out.println("Entrez votre coup : ");
		plateau.miniMax('O', plateau);
		String move = plateau.moveToSend;
		plateau.play(move, player);
		output.write(move.getBytes(),0,move.length());
		output.flush();
				
	     }
		// Le dernier coup est invalide
		if(cmd == '4'){
			System.out.println("Coup invalide, entrez un nouveau coup : ");
				String move = null;
			move = console.readLine();
			output.write(move.getBytes(),0,move.length());
			output.flush();

		}
		// La partie est terminée
	    if(cmd == '5'){
                byte[] aBuffer = new byte[16];
                int size = input.available();
                input.read(aBuffer,0,size);
		String s = new String(aBuffer);
		System.out.println("Partie Terminé. Le dernier coup joué est: "+s);
		String move = null;
		move = console.readLine();
		output.write(move.getBytes(),0,move.length());
		output.flush();
				
	    }
        }
	}
	catch (IOException e) {
   		System.out.println(e);
	}
	
    }
}

