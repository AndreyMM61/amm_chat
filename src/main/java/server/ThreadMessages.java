/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Андрей
 */
public class ThreadMessages {
       	LinkedList<String> Messages;
        boolean isDone;

        private List<ThreadListenSocket> listThreadListenSocket;
        private List<Socket> listSocket; 
        private String name;
	Thread t;

        ThreadMessages (List<ThreadListenSocket> listthreadlistensocket, List<Socket> listsocket) {
            try {
                isDone = false;
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
                    for (int i=0; i<listThreadListenSocket.size(); i++) {
//                        if (!listThreadListenSocket.get(i).messages.isEmpty()) {
                            while (!listThreadListenSocket.get(i).messages.isEmpty()) {
                                String msg = listThreadListenSocket.get(i).messages.poll();
                                Messages.addLast(msg);
                                while (Messages.size() > 20) {
                                    Messages.remove();
                                }
                                for (int j=0; j<listThreadListenSocket.size(); j++) {
                                    if (!listThreadListenSocket.get(j).equals(listThreadListenSocket.get(i))) {
                                        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(listSocket.get(j).getOutputStream())), true);
                                        out.println(msg);
                                        out.flush();
//                                        out.close();
                                    }
                                }
                            }
//                        }
                    }
 //                   Thread.sleep(20);
                }
            } 
            catch(Exception except) {
		System.out.println(">> " + name + " is interrupted");
                except.printStackTrace(); 
            }
        }
}
