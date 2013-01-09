/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.network.message;

import java.util.List;
import main.network.Client;

/**
 *
 * @author Daniel
 */
public class MessageStartGame extends Message
{
    /**
     * Properties
     */
    
    List<Client> tokenRing;
    
    /**
     * Constructor
     */
    
    public MessageStartGame() { }
    
    public MessageStartGame(List<Client> tokenRing)
    {
        this.tokenRing = tokenRing;
    }

    /**
     * Getters & Setters
     */
    
    public List<Client> getTokenRing() {
        return tokenRing;
    }

    public void setTokenRing(List<Client> tokenRing) {
        this.tokenRing = tokenRing;
    }
    
}
