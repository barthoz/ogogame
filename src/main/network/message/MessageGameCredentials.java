/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.network.message;

import main.game.GameCredentials;

/**
 *
 * @author s116861
 */
public class MessageGameCredentials extends Message
{
    /**
     * Properties
     */
    
    private GameCredentials gameCredentials;
    
    /**
     * Constructor
     */
    
    public MessageGameCredentials() { }
    
    public MessageGameCredentials(GameCredentials gameCredentials)
    {
        this.gameCredentials = gameCredentials;
    }

}