import java.io.*;
import java.net.*;


class Client {
	public static void main(String[] args) {
         
		Socket MyClient;
		BufferedInputStream input;
		BufferedOutputStream output;
		Plateau plateau = new Plateau();
		String ipv4= "";
		
		try {
			System.out.println("Veillez entré l'adresse du serveur: ");
			
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
					System.out.println("Nouvelle partie! Vous jouer blanc, entrez votre premier coup : ");
					byte[] aBuffer = new byte[1024];
					int size = input.available();
					input.read(aBuffer,0,size);

					String s = new String(aBuffer).trim();
					plateau = new Plateau(s);
					plateau.setPlayers(cmd);
					plateau.printPlateau();
					plateau.miniMax(cmd, plateau);
					String move = plateau.moveToSend;
					System.out.println("-------------Le best Move est: "+move);
					plateau.play(move, plateau.playerMax);

					output.write(move.getBytes(),0,move.length());
					output.flush();
				}

				// Debut de la partie en joueur Noir
				if(cmd == '2'){
					System.out.println("Nouvelle partie! Vous jouer noir, attendez le coup des blancs");
					byte[] aBuffer = new byte[1024];	
					int size = input.available();
					input.read(aBuffer,0,size);

					String s = new String(aBuffer).trim();
					plateau = new Plateau(s);
					plateau.setPlayers(cmd);
					plateau.printPlateau();
				}

				// Le serveur demande le prochain coup
				// Le message contient aussi le dernier coup joue.
				if(cmd == '3'){
					byte[] aBuffer = new byte[16];
							
					int size = input.available();
					input.read(aBuffer,0,size);
							
					String s = new String(aBuffer);
					System.out.println("Dernier coup :"+ s);
					plateau.play(s.replaceAll("\\s", ""), plateau.playerMin);
					plateau.printPlateau();
					System.out.println("Entrez votre coup : ");
					int alpha = Integer.MIN_VALUE;
        			int beta = Integer.MAX_VALUE;
					plateau.algoMinMax(plateau, plateau.playerMax, 3, alpha, beta);
					String move = plateau.moveToSend;
					System.out.println("-------------Le best Move est: "+move);
					plateau.play(move, plateau.playerMax);

					System.out.println("*********************** Plateau apres avoir obtenu le coup et apre avoir joué:  ");
					plateau.printPlateau();
					output.write(move.getBytes(),0,move.length());
					output.flush();
					//plateau.printPlateau();
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
		}catch (IOException e) {
			System.out.println(e);
		}
			
	}
}

