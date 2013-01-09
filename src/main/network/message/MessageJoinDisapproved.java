/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.network.message;

/**
 *
 * @author s116861
 */
public class MessageJoinDisapproved extends Message
{
    /**
     * Properties
     */
    
    private String reason;
    
    /**
     * Constructor
     */
    
    public MessageJoinDisapproved() { }
    
    public MessageJoinDisapproved(String reason)
    {
        this.reason = reason;
    }
    
    /**
     * Getters & Setters
     */
    
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
