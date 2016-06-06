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
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Андрей
 */
public class ThreadMessages implements Runnable {
       	LinkedList<String> Messages;

        private List<ThreadListenSocket> listThreadListenSocket;
        private List<Socket> listSocket; 
        private ReentrantLock lockData;
        private String name;
	Thread t;

        ThreadMessages (List<ThreadListenSocket> listthreadlistensocket, List<Socket> listsocket, ReentrantLock lockdata) {
            try {
                listThreadListenSocket = listthreadlistensocket;
                listSocket = listsocket;
                lockData = lockdata;
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
                while (t.isAlive()) {
                    for (int i=0; i<listThreadListenSocket.size(); i++) {
                        if (!listThreadListenSocket.get(i).messages.isEmpty()) {
                            while (!listThreadListenSocket.get(i).messages.isEmpty()) {
                                String msg = listThreadListenSocket.get(i).messages.poll();
                                Messages.addLast(msg);
                                while (Messages.size() > 20) {
                                    Messages.remove();
                                }
                                for (int j=0; j<listThreadListenSocket.size(); j++) {
                                    if (j != i) {
                                        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(listSocket.get(j).getOutputStream())), true);
                                        out.println(msg);
                                        out.flush();
//                                        out.close();
                                    }
                                }
                            }
                        }
                    }
                    Thread.sleep(200);
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
