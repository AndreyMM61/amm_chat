/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

        private String name;
//        private Socket socket;
        private BufferedReader in;
	Thread t;

	ThreadListenSocket(String namethread, Socket clientsocket) {
            try {
		name = namethread;
//                socket = clientsocket;
        	System.out.println("Now there is connection " + name);
// Initialization of message buffer                
                messages = new LinkedList();
// Create a stream to read characters from the socket                
                in = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
// Create a thread of client socket and start                
                t = new Thread(this, name);
                t.setPriority(t.getPriority() - 1);
		t.start();  // start a thread
            } catch(Exception except) { 
                except.printStackTrace(); 
            }
        }
	
	public void run() {
            try	{
                
                String line = null;
         	ZoneId zoneUTC = ZoneId.of("UTC");
         	ZonedDateTime timeCurrent;
         	ZonedDateTime timeUTC;
          
         	while((line = in.readLine()) != null) {
                        if (line.substring(line.indexOf(">")+2, line.length()).trim().equalsIgnoreCase("exit")){
                            System.out.println(">> " + name + " is over");
                            break;
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
                    System.out.println("Buffer in close");
                }
                catch (IOException e) {
                    System.err.println("Buffer in not closed");
                }
            }
        }
}
