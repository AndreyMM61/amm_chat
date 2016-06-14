/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package message;

import java.sql.Time;
import java.util.*;
import java.io.*;

/**
 *
 * @author Андрей
 */
public class Message implements Serializable {

    private String nickname;
    private String message;
    private String[] users;
    private Date time;
    
// Constructor for client
    public Message(String nickname, String message){
        this.nickname = nickname;
        this.message = message;
        this.time = java.util.Calendar.getInstance().getTime();
    }

// Constructor for server
    public Message(String nickname, String message, String[] users){
        this.nickname = nickname;
        this.message = message;
        this.time = java.util.Calendar.getInstance().getTime();
        this.users = users;
    }
// Set new message    
    public void setMessage(String message) {
        this.message = message;
    }
// Set new users online
    public void setUsers(String[] users) {
        this.users = users;
    }

    public String getNickName() {
        return this.nickname;
    }

    public String getMessage() {
        return this.message;
    }

    public String[] getUsers() {
        return this.users;
    }

    public String getTime(){
        Time tm = new Time(this.time.getTime());
        return tm.toString();
    }
}
