/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Андрей
 */
public class ThreadServerSocket implements Runnable {
    List<ThreadListenSocket> listThreadListenSocket = new ArrayList<ThreadListenSocket>();
    List<Socket> listSocket = new ArrayList<Socket>();
    
    private ServerSocket serverSocket; // create a server socket and bind it to the port
    private String name;
    Thread t;
    
        ThreadServerSocket(ServerSocket serversocket) {
                serverSocket = serversocket;
                name = "Thread of server socket";
        	System.out.println(name);
// Create a thread of server socket and start                
                t = new Thread(this, name);
                t.start();
        }        
        public void run() {
            try	{
                ThreadMessages threadMessages = new ThreadMessages(listThreadListenSocket, listSocket);
                    
                while(t.isAlive()) {
         	 
                    System.out.println("Waiting for the client...");
                    Socket socket = new Socket();

                    try {
                        socket = serverSocket.accept(); // Server wait connection
                    }
                    catch (IOException ignored) { 
                        break; 
                    }
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String nick = in.readLine(); // Waiting string from the client with nick.
                    
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    for (int i=0; i<threadMessages.Messages.size(); i++) {
                        out.println(threadMessages.Messages.get(i));
                    }
                    out.println("end");
                    out.flush();

                    listSocket.add(socket);
                    ThreadListenSocket listen = new ThreadListenSocket((listThreadListenSocket.size()) + ". Thread listen " + nick, socket);
                    listThreadListenSocket.add(listen);
                    
                }
                if (!threadMessages.t.isInterrupted()) {
                    threadMessages.t.interrupt();
                }
                for (int i=0; i<listThreadListenSocket.size(); i++) {
                    if (!listThreadListenSocket.get(i).t.isInterrupted()) {
                        listThreadListenSocket.get(i).t.interrupt();
                    }
                    listSocket.get(i).close();
                    listSocket.remove(i);
                }
            } 
            catch(Exception except) {
		System.out.println(">> " + name + " is interrupted");
                except.printStackTrace(); 
            }
            finally {
                System.out.println(">> " + name + " is over");
            }
        }
}
