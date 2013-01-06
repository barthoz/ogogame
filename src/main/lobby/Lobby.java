/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.lobby;

import main.network.GameConnector;

/**
 *
 * @author Daniel
 */
public class Lobby
{
    /**
     * Properties
     */
    
    private GameConnector gameConnector;
    
    private Me me;
    
    /**
     * Constructor
     */
    
    public Lobby()
    {
        this.gameConnector = new GameConnector();
        this.me = null;
        
        //LobbyJFrame frame = new LobbyJFrame(this);
        //frame.setVisible(true);
    }
    
    /**
     * Business logic
     */
    
    /**
     * Getters & Setters
     */
    
    public GameConnector getGameConnector() {
        return gameConnector;
    }

    public void setGameConnector(GameConnector gameConnector) {
        this.gameConnector = gameConnector;
    }

    public Me getMe() {
        return me;
    }

    public void setMe(Me me) {
        this.me = me;
    }
}
