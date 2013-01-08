/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.action;

import main.exception.ActionNotEnabledException;
import main.game.Game;
import main.game.Player;

/**
 *
 * @author Daniel
 */
public class LeaveAction extends Action
{
    /**
     * Properties
     */
    
    /**
     * Constructor
     */
    
    public LeaveAction(Player player)
    {
        this.player = player;
    }
    
    /**
     * Business logic
     */
    
    @Override
    public boolean isEnabled(Game game)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void performAction(Game game) throws ActionNotEnabledException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Getters & Setters
     */
}
