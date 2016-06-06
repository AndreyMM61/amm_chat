package server;

import java.net.*;
import java.io.*;
import java.util.*;
import server.ThreadListenSocket;
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
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
            ThreadServerSocket threadServerSocket = new ThreadServerSocket(serverPort);
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
            threadServerSocket.isDone = true;
            if (!threadServerSocket.t.isInterrupted()) {
                threadServerSocket.t.interrupt();
//                threadServerSocket.t.wait(1000);
            }
        } catch(Exception except) { 
            except.printStackTrace(); 
        }
    }
}
