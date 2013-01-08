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
    
    private LobbyJFrame lobbyFrame;
    
    /**
     * Constructor
     */
    
    public Lobby()
    {
        this.gameConnector = new GameConnector();
        this.me = null;
        
        this.lobbyFrame = new LobbyJFrame(this);
        lobbyFrame.setVisible(true);
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

    public LobbyJFrame getLobbyFrame() {
        return lobbyFrame;
    }

    public void setLobbyFrame(LobbyJFrame lobbyFrame) {
        this.lobbyFrame = lobbyFrame;
    }
}
