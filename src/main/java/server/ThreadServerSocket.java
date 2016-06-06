/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
    boolean isDone;
    List<ThreadListenSocket> listThreadListenSocket = new ArrayList<ThreadListenSocket>();
    List<Socket> listSocket = new ArrayList<Socket>();
//    List<String> listMessages = new ArrayList<String>(32);
    
    private int serverPort;
    private String name;
    Thread t;
    
        ThreadServerSocket(int port) {
                isDone = false;
                serverPort = port;
                name = "Thread of server socket";
        	System.out.println(name);
// Create a thread of server socket and start                
                t = new Thread(this, name);
                t.start();
        }        
	public void run() {
            try	{
                ServerSocket serverSocket = new ServerSocket(serverPort); // create a server socket and bind it to the port
                ThreadMessages threadMessages = new ThreadMessages(listThreadListenSocket, listSocket);
                    
                while(!isDone) {
         	 
                    System.out.println("Waiting for the client...");
                    Socket socket = new Socket();
                    socket = serverSocket.accept(); // Server wait connection
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String nick = in.readLine(); // Waiting string from the client with nick.
                    
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    for (int i=0; i<threadMessages.Messages.size(); i++) {
                        out.println(threadMessages.Messages.get(i));
                    }
                    out.println("end");
                    out.flush();
                    
                    for (int i=0; i<listThreadListenSocket.size();) {
                        if (listThreadListenSocket.get(i).t.isInterrupted()) {
                            listThreadListenSocket.remove(i);
                            if (listSocket.get(i).isClosed()) {
                                listSocket.remove(i);
                            }
                            continue;
                        }
                        i++;
                    }
                    System.out.println("Next index socket " + listSocket.size() + " and thread " + listThreadListenSocket.size());
                    
                    listSocket.add(socket);
                    ThreadListenSocket listen = new ThreadListenSocket((listThreadListenSocket.size() + 1) + ". Thread listen " + nick, socket);
                    listThreadListenSocket.add(listen);
                    
                }
                threadMessages.isDone = true;
                if (!threadMessages.t.isInterrupted()) {
                    threadMessages.t.interrupt();
                    threadMessages.t.wait(500);
                }
                for (int i=0; i<listThreadListenSocket.size(); i++) {
                    if (!listThreadListenSocket.get(i).t.isInterrupted()) {
                        listThreadListenSocket.get(i).t.interrupt();
                        listThreadListenSocket.get(i).t.wait(10);
                    }
                    listSocket.get(i).close();
                    listSocket.remove(i);
                }
            } 
            catch(Exception except) {
		System.out.println(">> " + name + " is interrupted");
                except.printStackTrace(); 
            }
        }
}
