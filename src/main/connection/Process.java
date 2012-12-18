/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.connection;

import main.game.model.Player;

/**
 *
 * @author Daniel
 */
public class Process
{
    /**
     * Properties
     */
    
    private String ip;
    private int port;
    private Player player;
    
    /**
     * Constructor
     */
    
    public Process(String ip, int port, Player player)
    {
        this.ip = ip;
        this.port = port;
        this.player = player;
    }
}
