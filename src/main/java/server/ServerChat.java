/*
 * Server of chat
 */
package server;

import java.net.*;
import java.io.*;
/**
 * 
 *
 */
public class ServerChat {
    
    public static void main( String[] args ) {
        if (args.length != 1) {
            System.out.println("Wrong number of arguments. Should be one.");
            System.out.println("1. Port.");
        }
        else {    
            int    serverPort  = Integer.parseInt(args[0]);   // the port to which the server binds
       
            try {
                ServerSocket serverSocket = new ServerSocket(serverPort); // create a server socket and bind it to the port
                BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in)); // create a keyboard buffer
                ThreadServerSocket threadServerSocket = new ThreadServerSocket(serverSocket); // create thread server socket
                String line = null;
// main loop of polling the keyboard             
                while((line = keyboard.readLine()) != null) {
                    if (line.equalsIgnoreCase("quit")){  // if equal to 'exit' to finish the program
                        System.out.println(line);
                        break;
                    }
                    else {
                        System.out.println("Enter \"quit\" for programm over");
                    }
                }
                if (!threadServerSocket.t.isInterrupted()) {
                    threadServerSocket.t.interrupt();
                }
                try {
                    serverSocket.close();
                }
                catch (IOException ignored) {} // errors are not interesting
            } 
            catch(Exception except) { 
                except.printStackTrace(); 
            }
        }
    }
}
