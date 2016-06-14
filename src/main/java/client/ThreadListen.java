/*
 * The listener thread socket
 */
package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import message.Message;

/**
 *
 * @author Андрей
 */
public class ThreadListen implements Runnable {
        private String name;
        private String nickName;
        private ObjectInputStream in;
	Thread t;

	ThreadListen(String namethread, ObjectInputStream in, String nickName) {
            try {
		this.name = namethread;
                this.in = in;
                this.nickName = nickName;
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
                
                Message message;

         	while(!t.isInterrupted()) {
                    message = (Message)in.readObject();
                    if (!message.getMessage().isEmpty()) {
                        if (message.getMessage().equalsIgnoreCase("quit")){
                            System.out.println("Server is interrupted");
                            break;
                        }
                        System.out.println();
                        System.out.println(message.getTime() + "<" + message.getNickName() + "> " + message.getMessage());
                        System.out.print(ZonedDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "[" + nickName + "] ");
                    }
        	}
            } 
            catch(Exception e) {
//		System.out.println(">> " + name + " is interrupted");
                e.printStackTrace(); 
            }
            finally {
                try {
                    in.close();
                    System.out.println("Buffer in close");
                }
                catch (Exception e) {
                    System.err.println(e.toString());
                    e.printStackTrace(); 
                }
            }
        }
}
