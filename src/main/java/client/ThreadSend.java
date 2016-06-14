/*
 * Stream audition keyboard of the client and forwarding to the server
 */
package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import message.Message;

/**
 *
 * @author mam
 */
public class ThreadSend implements Runnable {
        private BufferedReader keyboard; // Create a stream for reading from the keyboard
        private String name;
        private String nickName;
        private String timeMessage;    
        private ObjectOutputStream out;
	Thread t;


	ThreadSend(String namethread, ObjectOutputStream out, String nickName) {
            try {
		this.name = namethread;
                this.out = out;
                this.nickName = nickName;
                t = new Thread(this, name);
                t.setPriority(t.getPriority() - 1);
		t.start();  // start a thread
            } catch(Exception except) { 
                except.printStackTrace(); 
            }
        }

synchronized void Send (Message msg) throws Exception {
            out.writeObject(msg);
        }

	public void run() {
            try {
// Create a stream to read characters from the socket                
                keyboard = new BufferedReader(new InputStreamReader(System.in)); // Create a stream for reading from the keyboard
                System.out.print(ZonedDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "[" + nickName + "] ");
                while (!t.isInterrupted()) {
                    try {
                        String line = keyboard.readLine();
                        Message message = new Message (nickName, line);
//                        System.out.println("ThreadSend Line 66 " + line);
//                        System.out.println("ThreadSend Line 68 " + message.getMessage());
                        if (!message.getMessage().isEmpty()) {
//                            System.out.println("ThreadSend Line 70 " + message.getMessage());
                            Send(message); // message send to socket
                            if (message.getMessage().equalsIgnoreCase("quit")) {
//                                System.out.println("ThreadSend Line 73 " + message.getMessage());
                                break;
                            }
                        }
                    }
                    catch (Exception ignory) {
//                        System.out.println("ThreadSend Line 79 ");
                        break;
                    }
                    System.out.print(ZonedDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "[" + nickName + "] ");
                } 
//                System.out.println("ThreadSend Line 84");
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
