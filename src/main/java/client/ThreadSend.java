/*
 * Stream audition keyboard of the client and forwarding to the server
 */
package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author mam
 */
public class ThreadSend implements Runnable {
        private BufferedReader keyboard; // Create a stream for reading from the keyboard
        private String name;
        private String nickName;
        private BufferedReader in;
        private PrintWriter out;
        private String line;
        private ZonedDateTime timeUTC;
        private ZonedDateTime timeCurrent;
        private ZoneId zoneCurrent;
        private String timeMessage;    
        private Socket socket;
	Thread t;


	ThreadSend(String namethread, Socket socket, String nickName) {
            try {
		this.name = namethread;
                this.socket = socket;
                this.nickName = nickName;
                line = null;
// Create a thread of client socket and start                
                ZonedDateTime timeCurrent = ZonedDateTime.now();
                ZoneId zoneCurrent = timeCurrent.getZone();
// Create a stream to read characters from the socket                
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                Send("<" + nickName + "> "); // add nick and send the entered term to the server to discover
                while((line = in.readLine()) != null) {
                    if (line.equalsIgnoreCase("end")) {
                        break;
                    }
// Parse the string to get the UTC time                
                    timeMessage = line; 
                    timeMessage = line.substring(0, timeMessage.indexOf("<"));
                    timeUTC = ZonedDateTime.parse(timeMessage);
// Convert time to the current area and adding to the message display
                    timeCurrent = timeUTC.withZoneSameInstant(zoneCurrent);
                    System.out.println(timeCurrent.format(DateTimeFormatter.ofPattern("HH:mm")) + line.substring(line.indexOf("<"), line.length()));
                }
                line = null;

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

	public void run() {
            try {
// Create a stream to read characters from the socket                
                keyboard = new BufferedReader(new InputStreamReader(System.in)); // Create a stream for reading from the keyboard
                System.out.print(ZonedDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) + "<" + nickName + "> ");

                while (!t.isInterrupted()) {
                    try {
                        line = keyboard.readLine();
                        if (!line.isEmpty()) {
                            Send("<" + nickName + "> " + line); // Add nick to line and send to socket
                            if (line.equalsIgnoreCase("exit")){
                                Thread.currentThread().interrupt();
                                continue;
                            }
                        }
                    }
                    catch (Exception ignory) {
                        Thread.currentThread().interrupt();
                        continue;
                    }
                    System.out.print(ZonedDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) + "<" + nickName + "> ");
                } 
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
