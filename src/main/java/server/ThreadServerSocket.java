/*
 * The main server socket thread
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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Андрей
 */

public class ThreadServerSocket implements Runnable {
    List<ThreadListenSocket> listThreadListenSocket = Collections.synchronizedList(new ArrayList<ThreadListenSocket>());
    List<Socket> listSocket = Collections.synchronizedList(new ArrayList<Socket>());
    
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
                    
                while(!t.isInterrupted()) {
         	 
                    System.out.println("Waiting for the client...");
                    Socket socket = new Socket();

                    try {
                        socket = serverSocket.accept(); // Server wait connection
                    }
                    catch (IOException ignored) { 
                        Thread.currentThread().interrupt();
                        continue;
                    }
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String nick = in.readLine(); // Waiting string from the client with nick.
                    
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    synchronized (listThreadListenSocket) {
                        for (int i=0; i<threadMessages.Messages.size(); i++) {
                            out.println(threadMessages.Messages.get(i));
                        }
                        ZoneId zoneUTC = ZoneId.of("UTC");
                        ZonedDateTime timeCurrent;
                        ZonedDateTime timeUTC;
           		timeCurrent = ZonedDateTime.now();
           		timeUTC = timeCurrent.withZoneSameInstant(zoneUTC);
                        String line = timeUTC + "<Server>";
                        for (int i=0; i<listThreadListenSocket.size(); i++) {
                            if (i == 0) line += " Now in chat - ";
                            line = line + listThreadListenSocket.get(i).nickname;
                            if (i < (listThreadListenSocket.size()-1)) line += ", ";
                            else line += ". ";
                        }
                        line += "Users - " + listThreadListenSocket.size();
                        out.println(line);
                    }
                    out.println("end");
                    out.flush();

                    synchronized (listThreadListenSocket) {
                        listSocket.add(socket);
                        ThreadListenSocket listen = new ThreadListenSocket(nick, (listThreadListenSocket.size()) + ". Thread listen " + nick, socket);
                        listThreadListenSocket.add(listen);
                    }
                    
                }
                if (!threadMessages.t.isInterrupted()) {
                    threadMessages.t.interrupt();
                }
                while (!listThreadListenSocket.isEmpty()) {
                    if (!listThreadListenSocket.get(0).t.isInterrupted()) {
                        listThreadListenSocket.get(0).t.interrupt();
                        listThreadListenSocket.get(0).Send("exit");
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
