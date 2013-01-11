/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.action;

import java.io.Serializable;
import main.exception.ActionNotEnabledException;
import main.game.Game;
import main.game.Player;
import main.game.World;

/**
 *
 * @author Daniel
 */
public abstract class Action implements Serializable
{
    /**
     * Properties
     */
    
    protected transient Player player;
    
    protected int playerId;
    
    /**
     * Constructor
     */
    
    /**
     * Business logic
     */
    
    public void prepareForSerialization()
    {
        this.playerId = this.player.getId();
    }
    
    public void deserialize(Game game)
    {
        this.player = game.getPlayerById(this.playerId);
    }
    
    public abstract boolean isEnabled(Game game);
    
    public abstract void performAction(Game game) throws ActionNotEnabledException;
    
    /**
     * Getters & Setters
     */
    
    
}
