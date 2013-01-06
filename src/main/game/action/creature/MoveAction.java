/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.action.creature;

import main.game.action.creature.CreatureAction;
import com.jme3.cinematic.Cinematic;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.MotionPathListener;
import com.jme3.cinematic.events.CinematicEvent;
import com.jme3.cinematic.events.CinematicEventListener;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.cinematic.events.MotionTrack;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.exception.ActionNotEnabledException;
import main.exception.NoReachablePathException;
import main.game.Game;
import main.game.World;
import main.game.algorithm.PathFinding;
import main.game.model.cell.Cell;
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
    
    private Cell destination;
    
    /**
     * Constructors
     */
    
    public MoveAction(Creature creature, Cell destination)
    {
        this.subject = creature;
        this.destination = destination;
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
            
            /**MotionPath path = new MotionPath();
            path.addWayPoint(this.subject.getModel().getWorldTranslation());
            path.addWayPoint(new Vector3f(this.subject.getModel().getWorldTranslation().x, this.subject.getModel().getWorldTranslation().y + 10, 0));
            path.setCycle(false);*/
            
            try {//game.getWorld().getCells()[40][40] -> game.getWorld().getCells()[55][40]
                final MotionPath path = PathFinding.createMotionPath(game.getTerrain(), game.getWorld().getCells(), this.subject.getLocation(), this.destination, this.subject);
                
                
                final Cinematic cinematic = new Cinematic(game.getWorld().getWorldNode(), 20);
                MotionTrack track = new MotionTrack(this.subject.getModel(), path);
                cinematic.addCinematicEvent(0, track);
                cinematic.fitDuration();
                game.getStateManager().attach(cinematic);
                
                path.addListener(new MotionPathListener()
                {
                    public void onWayPointReach(MotionEvent control, int wayPointIndex)
                    {
                        if (path.getNbWayPoints() == wayPointIndex + 1)
                        {
                            //control.getSpatial().setLocalTranslation(destination.getWorldCoordinates().subtract(control.getSpatial().getWorldTranslation()));
                            cinematic.stop();
                        }
                    }
                });
                
                cinematic.addListener(new CinematicEventListener()
                {

                    public void onPlay(CinematicEvent cinematic) {
                        //throw new UnsupportedOperationException("Not supported yet.");
                    }

                    public void onPause(CinematicEvent cinematic) {
                        //throw new UnsupportedOperationException("Not supported yet.");
                    }

                    public void onStop(CinematicEvent cinematic) {
                        //throw new UnsupportedOperationException("Not supported yet.");
                        subject.getModel().setLocalTranslation(destination.getWorldCoordinates());//.subtract(subject.getModel().getWorldTranslation()));
                        subject.setLocation(destination);
                    }
                    
                });
                cinematic.play();
            } catch (NoReachablePathException ex) {
                Logger.getLogger(MoveAction.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }
    }
    
    /**
     * Getters & Setters
     */
}
