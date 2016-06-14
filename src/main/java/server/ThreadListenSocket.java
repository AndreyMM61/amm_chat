/*
 * The listener thread client clientSocket
 */
package server;

import java.io.*;
import java.net.Socket;
import java.util.*;
import message.Message;

/**
 *
 * @author mam
 */
public class ThreadListenSocket implements Runnable {
       	LinkedList<Message> messages;
        String nickname;

        private String name;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private Socket clientSocket;
	Thread t;

	ThreadListenSocket(String nickname, String namethread, Socket clientsocket, ObjectOutputStream out, ObjectInputStream in) {
            try {
                this.nickname = nickname;
		this.name = namethread;
                this.clientSocket = clientsocket;
                this.out = out;
                this.in = in;
// Initialization of message buffer                
                messages = new LinkedList<Message>();
        	System.out.println("Now there is connection " + name);
// Create a thread of client clientSocket and start                
                t = new Thread(this, name);
                t.setPriority(t.getPriority() - 1);
		t.start();  // start a thread
            } catch(Exception except) { 
                except.printStackTrace(); 
            }
        }

synchronized void Send (Message message) throws Exception {
            out.writeObject(message);
        }

synchronized void Close () {
            try {
                clientSocket.close();
            }
            catch (Exception ignory) {
            }
        }

	public void run() {
            try	{
                
                Message message;
         	while(!t.isInterrupted()) {
                        try {
                            message = (Message)in.readObject();
                            if (message.getMessage().isEmpty()) {
                                break;
                            }
                        }
                        catch (Exception ignory) {
                            break;
                        }
                        if (message.getMessage().equalsIgnoreCase("quit")){
                            System.out.println(">> " + name + " is over");
                            break;
                        }
           		System.out.println(message.getTime() + "<" + message.getNickName() + "> " + message.getMessage());
// send to of message fuffer. 
                        messages.addLast(message);
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
