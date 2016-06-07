/*
 * The main thread message passing
 */
package server;

import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Андрей
 */
public class ThreadMessages implements Runnable {
       	LinkedList<String> Messages;

        private List<ThreadListenSocket> listThreadListenSocket;
        private List<Socket> listSocket; 
        private String name;
	Thread t;

        ThreadMessages (List<ThreadListenSocket> listthreadlistensocket, List<Socket> listsocket) {
            try {
                listThreadListenSocket = listthreadlistensocket;
                listSocket = listsocket;
                name = "Thread of messages";
        	System.out.println(name);
// Initialization of message buffer                
                Messages = new LinkedList();
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
                while (!t.isInterrupted()) {
                    synchronized (listThreadListenSocket) {
                        for (int i=0; i<listThreadListenSocket.size(); ) {
                                if (!listThreadListenSocket.get(i).t.isAlive()) {
                                    listThreadListenSocket.remove(i);
                                    listSocket.remove(i);
                                    continue;
                                }
                                while (!listThreadListenSocket.get(i).messages.isEmpty()) {
                                    String msg = listThreadListenSocket.get(i).messages.poll();
                                    Messages.addLast(msg);
                                    while (Messages.size() > 20) {
                                        Messages.remove();
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
                        Thread.sleep(5);
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
