/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedReader;
import java.io.IOException;
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
	private Thread t;

	ThreadListen(String namethread, Socket socket, String nickName) {
            try {
		this.name = namethread;
                this.nickName = nickName;
//        	System.out.println("Constructor " + name);
// Create a stream to read characters from the socket                
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
// Create a thread of client socket and start                
                t = new Thread(this, name);
		t.start();  // start a thread
            } catch(Exception except) { 
                except.printStackTrace(); 
            }
        }
	
	public void run() {
            try	{
                
                String line = null;
                ZonedDateTime timeUTC;
                ZonedDateTime timeCurrent = ZonedDateTime.now();
                ZoneId zoneCurrent = timeCurrent.getZone();
                String timeMessage;        
          
         	while((line = in.readLine()) != null) {
// Parse the string to get the UTC time                
                    timeMessage = line; 
                    timeMessage = line.substring(0, timeMessage.indexOf("<"));
                    timeUTC = ZonedDateTime.parse(timeMessage);
// Convert time to the current area and adding to the message display
                    timeCurrent = timeUTC.withZoneSameInstant(zoneCurrent);
                    System.out.println();
                    System.out.println(timeCurrent.format(DateTimeFormatter.ofPattern("HH:mm")) + line.substring(line.indexOf("<"), line.length()));
                    System.out.print(ZonedDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) + "<" + nickName + "> ");
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
