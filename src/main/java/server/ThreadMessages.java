/*
 * The main thread message passing
 */
package server;

import java.net.Socket;
import java.util.List;
import message.Message;

/**
 *
 * @author Андрей
 */
public class ThreadMessages implements Runnable {
        List<Message> queueMessages;

        private List<ThreadListenSocket> listThreadListenSocket;
        private List<Socket> listSocket; 
        private List<String> listUsers;
        private String name;
	Thread t;

        ThreadMessages (List<ThreadListenSocket> listthreadlistensocket, List<Socket> listsocket, List<String> listusers, List<Message> queueMessages) {
            try {
                this.listThreadListenSocket = listthreadlistensocket;
                this.listSocket = listsocket;
                this.listUsers = listusers;
                this.queueMessages = queueMessages;
                name = "Thread of messages";
        	System.out.println(name);
// Create a thread of message and start                
                t = new Thread(this, name);
                t.setPriority(t.getPriority() - 1);
		t.start();  // start a thread
            } catch(Exception except) { 
                except.printStackTrace(); 
            }
        }
        
	public void run() {
            try	{
// Initialization of message buffer                
                while (!t.isInterrupted()) {
                    synchronized (listThreadListenSocket) {
                        for (int i=0; i<listThreadListenSocket.size(); ) {
                                if (!listThreadListenSocket.get(i).t.isAlive()) {
                                    Message msg = new Message("Server", listThreadListenSocket.get(i).nickname + " out of the chat");
                                    for (int j=0; j<listThreadListenSocket.size(); j++) {
                                        if (j != i) {
                                            listThreadListenSocket.get(j).Send(msg);
                                        }
                                    }
                                    listThreadListenSocket.remove(i);
                                    listSocket.remove(i);
                                    listUsers.remove(i);
                                    continue;
                                }
                                while (!listThreadListenSocket.get(i).messages.isEmpty()) {
                                    Message msg = listThreadListenSocket.get(i).messages.poll();
                                    synchronized (queueMessages) {
                                        queueMessages.add(msg);
                                        while (queueMessages.size() > 20) {
                                            queueMessages.remove(0);
                                        }
                                    }
                                    for (int j=0; j<listThreadListenSocket.size(); j++) {
                                        if (j != i) {
                                            listThreadListenSocket.get(j).Send(msg);
                                        }
                                    }
                                }
                                i++;
                        }
                    }
                    try {
                        Thread.sleep(10);
                    }
                    catch (InterruptedException ignory) {
                        Thread.currentThread().interrupt();
                        continue;
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
