package tools;

import socket.Client;
import socket.Serveur;

public class Main {
 	public static void main(final String[] args) {
 
         Thread serverThread = new Thread() {
             public void run() {
                 try {
 					Serveur.main(args);
 				} catch (Exception e) {
 					
 					e.printStackTrace();
 				}
             }
         };
 
         Thread clientThread = new Thread() {
             public void run() {
                 try {
 					Client.main(args);
 				} catch (Exception e) {
 					
 					e.printStackTrace();
 				}
             }
         };
 
         serverThread.start();
 
         try {
             Thread.sleep(1000);
         } catch (InterruptedException e) {
             e.printStackTrace();
         }
 
          clientThread.start();
      }
  }