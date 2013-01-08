/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.network.message;

/**
 *
 * @author s116861
 */
public class Message
{
    /**
     * Properties
     */
    
    public final static int TYPE_GAMECREDENTIALS = 0;
    public final static int TYPE_JOIN_REQUEST = 1;
    public final static int TYPE_JOIN_APPROVED = 2;
    
    private int messageType;
    
    /**
     * Business logic
     */
    
    public Message() { }
    
    public Message(int messageType)
    {
        this.messageType = messageType;
    }
    
    /**
     * Getters & Setters
     */
    
    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }
}
