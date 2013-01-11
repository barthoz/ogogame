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
public class MessageSetModeDone extends Message
{
    /**
     * Properties
     */
    
    private List<Action> actions;
    
    /**
     * Constructor
     */
    
    public MessageSetModeDone() { }
    
    public MessageSetModeDone(List<Action> actions)
    {
        for (Action action : actions)
        {
            action.prepareForSerialization();
        }
        
        this.actions = actions;
    }
    
    /**
     * Getters & Setters
     */
    
    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }
}
