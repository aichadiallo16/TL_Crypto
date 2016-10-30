package tools;

import java.util.Scanner;

import socket.Client;
import socket.Serveur;

public class Main {
 	public static void main(final String[] args) {
 
 		String rep = "s";
 		 		Scanner scanner = new Scanner(System.in);
 		 		System.out.println("Demarrage en tant que serveur ou client: [s/c]");
 		 		while(!rep.equals("s") || !rep.equals("c")) {
 		 	
 		 		String readString = scanner.nextLine();
 		 		/**
 		 		  * Initialisation comme serveur 
 		 		  */
 		 		 		if (readString.equals("s") ) {		 		
         Thread serverThread = new Thread() {
             public void run() {
                 try {
 					Serveur.main(args);
 				} catch (Exception e) {
 					
 					e.printStackTrace();
 				}
             }
         };
         serverThread.start();
 		 		 		}
/**
  * Initialisation comme client 
  */
	else if (readString.equals("c") ) {
 		 		 	 
         Thread clientThread = new Thread() {
             public void run() {
                 try {
 					Client.main(args);
 				} catch (Exception e) {
 					
 					e.printStackTrace();
 				}
             }
         };
 
         
 
         try {
             Thread.sleep(1000);
         } catch (InterruptedException e) {
             e.printStackTrace();
         }
 
          clientThread.start();
      }
  }
 		 		scanner.close();
 	}
}