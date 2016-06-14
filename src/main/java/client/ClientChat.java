/*
 * Client of chat
 */
package client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import message.Message;
/**
 *
 * @author mam
 */
public class ClientChat {
    public static void main(String[] args) {
    	
        if (args.length != 3) {
            System.out.println("Wrong number of arguments. Should be three.");
            System.out.println("1. Ip address;");
            System.out.println("2. Port;");
            System.out.println("3. Nick;");
//            System.out.println("4. Password.");
        }
        else {
        
            String serverAddress = args[0];   // The IP address of the server 
            int    serverPort    = Integer.parseInt(args[1]);   // the port to bind the server.
            String nickName      = args[2];   // the client name
//            String nickPassword  = args[3];   // the client password

//            Thread.currentThread().setPriority(Thread.currentThread().getPriority() - 1);
            Message message;
            ObjectInputStream in;
            ObjectOutputStream out;
            
            try {
                InetAddress ipAddress = InetAddress.getByName(serverAddress); // create an object with the IP address.
                System.out.println("Try to connect to the IP address " + serverAddress + " and port " + serverPort + "...");
                Socket socket = new Socket(ipAddress, serverPort); // create a socket with the IP address and port of the server.
                System.out.println("Now there is connection!!!");
                System.out.println();

// Create a stream to write messages to the socket                
                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
//                System.out.println("ClientChat Line 45");
// Create a stream to read messages from the socket                
                in   = new ObjectInputStream(socket.getInputStream());
//                System.out.println("ClientChat Line 48");
                out.writeObject(new Message(nickName, "login")); // send the entered term to the server to discover
//                System.out.println("ClientChat Line 52");
                while((message = (Message)in.readObject()) != null) {
                    if (message.getMessage().equalsIgnoreCase("end")) {
                        String line = message.getTime() + "<" + message.getNickName() + "> Users - ";
                        if (message.getUsers().length != 0) {
                            String listUsers[] = message.getUsers();
                            for (String user: listUsers) {
                                line += "[" + user +"] ";
                            }
                            line += "- " + listUsers.length;
                        }
                        else {
                            line += "0";
                        }
                        System.out.println(line);
                        break;
                    }
                    System.out.println(message.getTime() + "<" + message.getNickName() + "> " + message.getMessage());
//                    System.out.println("ClientChat Line 57");
                }

                ThreadSend sendToSocket = new ThreadSend("Thread send", out, nickName);
//                System.out.println("ClientChat Line 63");
                ThreadListen listen = new ThreadListen("Thread listen", in, nickName);
//                System.out.println("ClientChat Line 65");
            
                while (sendToSocket.t.isAlive() && listen.t.isAlive()) { Thread.sleep(10); }
                
                System.out.println("Exit from main while!!!");
                System.exit(0);
            } 
            catch (Exception except) {
                except.printStackTrace();
            }
        }
    }    
}
