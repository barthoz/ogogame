/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.network.message;

import java.util.List;
import main.game.GameCredentials;
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
    
    private List<Client> tokenRing;
    private GameCredentials gameCredentials;
    
    
    /**
     * Constructor
     */
    
    public MessageStartGame() { }
    
    public MessageStartGame(List<Client> tokenRing, GameCredentials gameCredentials)
    {
        this.tokenRing = tokenRing;
        this.gameCredentials = gameCredentials;
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

    public GameCredentials getGameCredentials() {
        return gameCredentials;
    }

    public void setGameCredentials(GameCredentials gameCredentials) {
        this.gameCredentials = gameCredentials;
    }
    
}
