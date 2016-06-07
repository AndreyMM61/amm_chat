/*
 * The listener thread client socket
 */
package server;

import java.io.*;
import java.net.Socket;
import java.time.*;
import java.util.*;

/**
 *
 * @author mam
 */
public class ThreadListenSocket implements Runnable {
       	LinkedList<String> messages;
        String nickname;

        private String name;
        private BufferedReader in;
        private Socket socket;
        private PrintWriter out;
	Thread t;

	ThreadListenSocket(String nick, String namethread, Socket clientsocket) {
            try {
                nickname = nick;
		name = namethread;
                socket = clientsocket;
        	System.out.println("Now there is connection " + name);
// Initialization of message buffer                
                messages = new LinkedList();
// Create a stream to read characters from the socket                
                in = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
                out =  new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientsocket.getOutputStream())), true);
// Create a thread of client socket and start                
                t = new Thread(this, name);
                t.setPriority(t.getPriority() - 1);
		t.start();  // start a thread
            } catch(Exception except) { 
                except.printStackTrace(); 
            }
        }

synchronized void Send (String msg) {
            out.println(msg);
            out.flush();
        }

synchronized void Close () {
            try {
                socket.close();
            }
            catch (Exception ignory) {
            }
        }

	public void run() {
            try	{
                
                String line;
         	ZoneId zoneUTC = ZoneId.of("UTC");
         	ZonedDateTime timeCurrent;
         	ZonedDateTime timeUTC;
          
         	while(!t.isInterrupted()) {
                        try {
                            line = in.readLine();
                            if (line == null) {
                                Thread.currentThread().interrupt();
                                continue;
                            }
                        }
                        catch (Exception ignory) {
                            Thread.currentThread().interrupt();
                            continue;
                        }
                        if (line.substring(line.indexOf(">")+2, line.length()).trim().equalsIgnoreCase("exit")){
                            System.out.println(">> " + name + " is over");
                            Thread.currentThread().interrupt();
                            continue;
                        }
// Current time to UTC
           		timeCurrent = ZonedDateTime.now();
           		timeUTC = timeCurrent.withZoneSameInstant(zoneUTC);
                        line = timeUTC + line;
           		System.out.println(line);
// Add time to string and send to of message fuffer. 
                        messages.addLast(line);
         	}
            } 
            catch(Exception except) {
		System.out.println(">> " + name + " is interrupted");
                except.printStackTrace(); 
            }
            finally {
                try {
                    in.close();
                    System.out.println(">> " + name + ". Buffer in close");
                }
                catch (IOException e) {
                    System.err.println(">> " + name + ". Buffer in not closed");
                }
            }
        }
}
