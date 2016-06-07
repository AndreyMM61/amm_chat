/*
 * The listener thread socket
 */
package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Андрей
 */
public class ThreadListen implements Runnable {
        private String name;
        private String nickName;
        private BufferedReader in;
        private Socket socket;
	Thread t;

	ThreadListen(String namethread, Socket socket, String nickName) {
            try {
		this.name = namethread;
                this.socket = socket;
                this.nickName = nickName;
// Create a stream to read characters from the socket                
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
// Create a thread of client socket and start                
                t = new Thread(this, name);
                t.setPriority(t.getPriority() - 1);
		t.start();  // start a thread
            } catch(Exception except) { 
                except.printStackTrace(); 
            }
        }

synchronized void Close () {
            try {
                if (!socket.isClosed()) {
                    socket.close();
                    System.out.println("Socket in close");
                }
                in.close();
                System.out.println("Buffer in close");
            }
            catch (Exception e) {
                System.err.println(e.toString());
                e.printStackTrace(); 
            }
        }
	
	public void run() {
            try	{
                
                String line = null;
                ZonedDateTime timeUTC;
                ZonedDateTime timeCurrent = ZonedDateTime.now();
                ZoneId zoneCurrent = timeCurrent.getZone();
                String timeMessage;        
          
         	while(!t.isInterrupted()) {
                    line = in.readLine();
                    if (!line.isEmpty()) {
                        if (line.equalsIgnoreCase("exit")){
                            System.out.println("Server is interrupted");
                            Thread.currentThread().interrupt();
                            break;
                        }
// Parse the string to get the UTC time                
                        timeMessage = line; 
                        timeMessage = line.substring(0, timeMessage.indexOf("<"));
                        timeUTC = ZonedDateTime.parse(timeMessage);
// Convert time to the current area and adding to the message display
                        timeCurrent = timeUTC.withZoneSameInstant(zoneCurrent);
                        System.out.println();
                        System.out.println(timeCurrent.format(DateTimeFormatter.ofPattern("HH:mm")) + line.substring(line.indexOf("<"), line.length()));
                        System.out.println(ZonedDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) + "<" + nickName + "> ");
                    }
        	}
            } 
            catch(Exception e) {
		System.out.println(">> " + name + " is interrupted");
                e.printStackTrace(); 
            }
            finally {
                try {
                    Close();
                }
                catch (Exception e) {
                    System.err.println(e.toString());
                    e.printStackTrace(); 
                }
            }
        }
}
