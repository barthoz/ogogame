/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.lobby;

import main.connection.GameConnector;

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
        
        LobbyJFrame frame = new LobbyJFrame(this);
        frame.setVisible(true);
    }
}
