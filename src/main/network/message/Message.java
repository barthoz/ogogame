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
    
    private int fromClientId = -1;
    
    /**
     * Business logic
     */
    
    public Message() { }
    
    /**
     * Getters & Setters
     */
    
    public int getFromClientId() {
        return fromClientId;
    }

    public void setFromClientId(int fromClientId) {
        this.fromClientId = fromClientId;
    }
}
