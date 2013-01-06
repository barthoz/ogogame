/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.action.creature;

import main.game.action.Action;
import main.game.model.creature.Creature;

/**
 *
 * @author s116861
 */
public abstract class CreatureAction extends Action
{
    /**
     * Properties
     */
    
    protected Creature subject;
    
    /**
     * Constructor
     */
    
    /**
     * Business logic
     */
    
    /**
     * Getters & Setters
     */
    
    public Creature getSubject() {
        return subject;
    }

    public void setSubject(Creature subject) {
        this.subject = subject;
    }
}
