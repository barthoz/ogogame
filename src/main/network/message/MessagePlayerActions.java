/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.network.message;

import java.util.List;
import main.game.action.Action;

/**
 *
 * @author s116861
 */
public class MessagePlayerActions extends Message
{
    /**
     * Properties
     */
    
    private List<Action> actions;
    
    /**
     * Constructor
     */
    
    public MessagePlayerActions() { }
    
    public MessagePlayerActions(List<Action> actions)
    {
        this.actions = actions;
    }
}
