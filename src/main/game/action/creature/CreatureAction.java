/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.action.creature;

import main.game.Game;
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
    
    protected transient Creature subject;
    
    protected String subjectId;
    
    /**
     * Constructor
     */
    
    /**
     * Business logic
     */
    
    @Override
    public void prepareForSerialization()
    {
        super.prepareForSerialization();
        
        this.subjectId = this.subject.getId();
    }
    
    @Override
    public void deserialize(Game game)
    {
        super.deserialize(game);
        
        this.subject = game.getWorld().findCreatureById(subjectId);
    }
    
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
