/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.net.*;
/**
 *
 * @author mam
 */
public class ClientChat {
    public static void main(String[] args) {
    	
        if (args.length < 4)
        {
            throw new NullPointerException("Wrong number of arguments. Should be four.\n"
                    + "1. Ip address;\n2. Port;\n3. Nick;\n4. Password.");
        }
        
        
        String serverAddress = args[0];   // The IP address of the server 
        int    serverPort    = Integer.parseInt(args[1]);   // the port to bind the server.
        String nickName      = args[2];   // the client name
        String nickPassword  = args[3];   // the client password

        try {
            InetAddress ipAddress = InetAddress.getByName(serverAddress); // create an object with the IP address.
            System.out.println("Try to connect to the IP address " + serverAddress + " and port " + serverPort + "...");
            Socket socket = new Socket(ipAddress, serverPort); // create a socket with the IP address and port of the server.
            System.out.println("Now there is connection!!!");
            System.out.println();

            ThreadSend sendToSocket = new ThreadSend("Thread send", socket, nickName);
            ThreadListen listen = new ThreadListen("Thread listen", socket, nickName);
            
            while (sendToSocket.t.isAlive() && listen.t.isAlive()) {};

            System.out.println("Exit from main while!!!");
            System.exit(0);
/*
            if (sendToSocket.t.isAlive()) {
                sendToSocket.t.interrupt();
                sendToSocket.Close();
            }
            System.out.println("sendToSocket!!! - " + sendToSocket.t.isAlive());
            if (listen.t.isAlive()) listen.Close();
            System.out.println("listen!!! - " + listen.t.isAlive());
*/
            /*
            String line = null;
            ZonedDateTime timeUTC;
            ZonedDateTime timeCurrent = ZonedDateTime.now();
            ZoneId zoneCurrent = timeCurrent.getZone();
            String timeMessage;        

            // Create a streams socket
            PrintWriter    out;
            BufferedReader in;
// Create a stream to read characters from the socket                
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("<" + nickName + "> "); // add nick and send the entered term to the server to discover
            while((line = in.readLine()) != null) {
                    if (line.equalsIgnoreCase("end")) {
                        break;
                    }
// Parse the string to get the UTC time                
                    timeMessage = line; 
                    timeMessage = line.substring(0, timeMessage.indexOf("<"));
                    timeUTC = ZonedDateTime.parse(timeMessage);
// Convert time to the current area and adding to the message display
                    timeCurrent = timeUTC.withZoneSameInstant(zoneCurrent);
                    System.out.println(timeCurrent.format(DateTimeFormatter.ofPattern("HH:mm")) + line.substring(line.indexOf("<"), line.length()));
            }
            line = null;
            ThreadListen listen = new ThreadListen("Thread listen", socket, nickName);

            System.out.print(ZonedDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) + "<" + nickName + "> ");

            while ((line = keyboard.readLine()) != null) {
                if (line.length() != 0) out.println("<" + nickName + "> " + line); // Add nick to line and send to socket
                if (line.equalsIgnoreCase("exit")){
                    break;
                }
                System.out.print(ZonedDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) + "<" + nickName + "> ");
            } 
*/            
        } catch (Exception except) {
            except.printStackTrace();
        }
    }    
}
