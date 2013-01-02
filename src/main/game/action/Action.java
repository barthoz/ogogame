/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.action;

import main.exception.ActionNotEnabledException;
import main.game.Game;
import main.game.World;

/**
 *
 * @author Daniel
 */
public abstract class Action
{
    /**
     * Properties
     */
    
    /**
     * Constructor
     */
    
    /**
     * Business logic
     */
    
    public abstract boolean isEnabled(Game game);
    
    public abstract void performAction(Game game) throws ActionNotEnabledException;
    
    /**
     * Getters & Setters
     */
    
    
}
