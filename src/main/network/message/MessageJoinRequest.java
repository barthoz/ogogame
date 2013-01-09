/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.network.message;

/**
 *
 * @author s116861
 */
public class MessageJoinRequest extends Message
{
    /**
     * Properties
     */
    
    private String username;
    
    /**
     * Constructor
     */
    
    public MessageJoinRequest() { }
    
    public MessageJoinRequest(String username)
    {
        this.username = username;
    }
    
    /**
     * Getters & Setters
     */
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
