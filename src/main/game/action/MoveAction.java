/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.action;

import com.jme3.cinematic.Cinematic;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionTrack;
import com.jme3.math.Vector3f;
import main.exception.ActionNotEnabledException;
import main.game.Game;
import main.game.World;
import main.game.model.creature.Creature;

/**
 *
 * @author s116861
 */
public class MoveAction extends CreatureAction
{
    /**
     * Properties
     */
    
    /**
     * Constructors
     */
    
    public MoveAction(Creature creature)
    {
        this.subject = creature;
    }
    
    /**
     * Business Logic
     */
    
    @Override
    public boolean isEnabled(Game game)
    {
        return true;
    }

    @Override
    public void performAction(Game game) throws ActionNotEnabledException
    {
        if (!isEnabled(game))
        {
            throw new ActionNotEnabledException();
        }
        else
        {
            System.out.println("Test Move");
            Cinematic cinematic = new Cinematic(game.getWorld().getWorldNode(), 5);
            MotionPath path = new MotionPath();
            path.addWayPoint(this.subject.getModel().getWorldTranslation());
            path.addWayPoint(new Vector3f(this.subject.getModel().getWorldTranslation().x, this.subject.getModel().getWorldTranslation().y + 10, 0));
            path.setCycle(false);
            MotionTrack track = new MotionTrack(this.subject.getModel(), path);
            cinematic.addCinematicEvent(1, track);
            game.getStateManager().attach(cinematic);
           
            cinematic.play();
            
            
        }
    }
    
    /**
     * Getters & Setters
     */
}
