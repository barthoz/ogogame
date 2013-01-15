/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.action.creature;

import com.jme3.cinematic.Cinematic;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.CinematicEvent;
import com.jme3.cinematic.events.CinematicEventListener;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import main.exception.ActionNotEnabledException;
import main.game.Game;
import main.game.Player;
import main.game.algorithm.PathFinding;
import main.game.model.control.AirborneCreatureControl;
import main.game.model.control.LandCreatureControl;
import main.game.model.creature.AirborneCreature;
import main.game.model.creature.LandCreature;

/**
 *
 * @author s116861
 */
public class LandAction extends CreatureAction
{
    /**
     * Constructors
     */
    
    public LandAction(Player player, AirborneCreature subject)
    {
        this.player = player;
        this.subject = subject;
    }
    
    /**
     * Business logic
     */
    
    @Override
    public boolean isEnabled(Game game)
    {
        if (((AirborneCreature) this.subject).isAirborne() && this.subject.getLocation().getBase() == null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public void performAction(Game game) throws ActionNotEnabledException
    {
        MotionPath path = new MotionPath();
        path.addWayPoint(this.subject.getModel().getWorldTranslation());
        path.addWayPoint(this.subject.getLocation().getWorldCoordinates());
        
        Cinematic cinematic = new Cinematic(game.getWorld().getWorldNode(), 20);
        MotionEvent track = new MotionEvent(this.subject.getModel(), path);
        
        cinematic.addCinematicEvent(0, track);
        cinematic.fitDuration();
        game.getStateManager().attach(cinematic);
        
        cinematic.addListener(new CinematicEventListener()
        {
            public void onPlay(CinematicEvent cinematic) { }

            public void onPause(CinematicEvent cinematic) { }

            public void onStop(CinematicEvent cinematic)
            {
                subject.getModel().setLocalTranslation(subject.getLocation().getWorldCoordinates());

                // Reposition creatures
                subject.getLocation().repositionCreatures();
            }

        });
        
        cinematic.play();
        
        ((AirborneCreature) this.subject).land();
        
        /**
         * Update locations
         */
        
        // Old location
        this.subject.getLocation().removeCreature(this.subject, true);
        
        // New location
        this.subject.getLocation().addCreature(this.subject, false);
    }
    
}
