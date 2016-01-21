/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wsChatServer;

import javax.jws.WebService;
import javax.jws.WebMethod;
import java.util.*;

@WebService
public class ChatServer {
    // Create a hashtable of two Strings, one for users, one for messages
    private static Hashtable<String, String> userList = new Hashtable<String, String>();

    /**
     * Join method that adds the user hashtable
     * @param user 
     */
    @WebMethod
    public void join(String user){
            userList.put(user,"");
    }
    
    /**
     * Talk method that sends the message to all users
     * @param message 
     */
    @WebMethod
    public void talk(String message){
        // For all keysets in the hashtable
        for (String users : userList.keySet()){
            // Get the user string from the hastable
            userList.get(users);  
            // Add the message to all users on the hashtable
            userList.put(users, message);
        }
    }
    
    /**
     * Read the messages
     * @param user
     * @return 
     */
    @WebMethod
    public String listen(String user) {
        // Get the users messages
        String tempMessage = userList.get(user).toString();
        // delete the users message
        userList.put(user, "");
        // return the message to output in the otherTextArea
        return tempMessage;
    }
    
    /**
     * Leave hashtable
     * @param user 
     */
    @WebMethod
    public void leave(String user){
        // remove the user from the hashtable
        userList.remove(user);
    }
    
    /**
     * Private message method
     * @param userToSendTo
     * @param user
     * @param message 
     */
    @WebMethod
    public void privateMessage (String userToSendTo, String user, String message){
        // If the hashtable contains the userToSendTo string
        if(userList.containsKey(userToSendTo)){
            // Display the message on the userToSendTo's chat
            userList.put(userToSendTo, user + " Private message : " + message);
            // Display the message on the User's chat
            userList.put(user, user + " Private message : " + message);
        } 
        // If the hashtable doesnt contain the desired userToSendTo
        else if(!userList.containsKey(userToSendTo)){
            // Output an error to the user's chat
            userList.put(user, "Error " + userToSendTo +  " is not in the server");
        }
    }
}
