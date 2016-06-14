/*
 * The main server socket thread
 */
package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import message.Message;

/**
 *
 * @author Андрей
 */

public class ThreadServerSocket implements Runnable {
    List<ThreadListenSocket> listThreadListenSocket = Collections.synchronizedList(new ArrayList<ThreadListenSocket>());
    List<Socket> listSocket = Collections.synchronizedList(new ArrayList<Socket>());
    List<String> listUsers = Collections.synchronizedList(new ArrayList<String>());
    List<Message> queueMessages = Collections.synchronizedList(new ArrayList<Message>());

    private Message message;
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
                ThreadMessages threadMessages = new ThreadMessages(listThreadListenSocket, listSocket, listUsers, queueMessages);
                    
                while(!t.isInterrupted()) {
         	 
                    System.out.println("Waiting for the client...");
                    Socket socket;
                    ObjectInputStream in;
                    ObjectOutputStream out;

                    try {
                        socket = serverSocket.accept(); // Server wait connection
// Create a stream to write messages to the socket                
                        out = new ObjectOutputStream(socket.getOutputStream());
                        out.flush();
// Create a stream to read messages from the socket                
                        in  = new ObjectInputStream(socket.getInputStream());
                    }
                    catch (IOException ignored) { 
                        break;
                    }
                    
                    message = (Message)in.readObject();
                    String nickName = message.getNickName(); // Waiting string from the client with nickName.
                    
                    synchronized (queueMessages) {
                        for (int i=0; i<queueMessages.size(); i++) {
                            out.writeObject(queueMessages.get(i));
                        }
                    }
                    String users[] = (String[])listUsers.toArray(new String[0]);
                    out.writeObject(new Message("Server", "end", users));

                    synchronized (listThreadListenSocket) {
                        listUsers.add(nickName);
                        listSocket.add(socket);
                        ThreadListenSocket listen = new ThreadListenSocket(nickName, (listThreadListenSocket.size()) + ". Thread listen " + nickName, socket, out, in);
                        listThreadListenSocket.add(listen);
                    }
                    
                }
                if (!threadMessages.t.isInterrupted()) {
                    threadMessages.t.interrupt();
                }
                while (!listThreadListenSocket.isEmpty()) {
                    if (!listThreadListenSocket.get(0).t.isInterrupted()) {
                        listThreadListenSocket.get(0).t.interrupt();
                        listThreadListenSocket.get(0).Send(new Message("Server", "quit"));
                        listThreadListenSocket.get(0).Close();
                        listThreadListenSocket.remove(0);
                    }
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
