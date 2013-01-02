/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.action;

import main.game.model.creature.Creature;

/**
 *
 * @author s116861
 */
public class CreatureAction extends Action
{
    /**
     * Properties
     */
    
    private Creature subject;
    
    /**
     * Constructor
     */
    
    public CreatureAction() { }
    
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
