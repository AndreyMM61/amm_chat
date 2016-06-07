package server;

import java.net.*;
import java.io.*;
/**
 * 
 *
 */
public class ServerChat 
{
    public static void main( String[] args )
    {
        if (args.length < 1)
        {
            throw new NullPointerException("Wrong number of arguments. Should be one.\n"
                    + "1. Port.");
        }

        int    serverPort  = Integer.parseInt(args[0]);   // the port to which the server binds
       
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort); // create a server socket and bind it to the port
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
            ThreadServerSocket threadServerSocket = new ThreadServerSocket(serverSocket);
            String line = null;
            
            while((line = keyboard.readLine()) != null) {
                if (line.equalsIgnoreCase("exit")){
                    System.out.println(line);
                    break;
                }
                else {
                    System.out.println("Enter exit for programm over");
                }
            }
            if (!threadServerSocket.t.isInterrupted()) {
                threadServerSocket.t.interrupt();
            }
            try {
                serverSocket.close();
            }
            catch (IOException ignored) {} //ошибки неинтересны
        } catch(Exception except) { 
            except.printStackTrace(); 
        }
    }
}
