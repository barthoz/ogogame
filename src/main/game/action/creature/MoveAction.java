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
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.exception.ActionNotEnabledException;
import main.exception.NoReachablePathException;
import main.game.Game;
import main.game.Player;
import main.game.World;
import main.game.algorithm.PathFinding;
import main.game.model.cell.Cell;
import main.game.model.creature.AirborneCreature;
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
    
    private transient Cell destination;
    
    private int destinationX;
    
    private int destinationY;
    
    /**
     * Constructors
     */
    
    public MoveAction(Player player, Creature subject, Cell destination)
    {
        this.player = player;
        this.subject = subject;
        this.destination = destination;
    }
    
    /**
     * Business Logic
     */
    
    @Override
    public void prepareForSerialization()
    {
        super.prepareForSerialization();
        
        this.destinationX = this.destination.getXCoor();
        this.destinationY = this.destination.getYCoor();
    }
    
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
            try
            {
                final MotionPath path = PathFinding.createMotionPath(game.getTerrain(), game.getWorld().getCells(), this.subject.getLocation(), this.destination, this.subject);
                
                final Cinematic cinematic = new Cinematic(game.getWorld().getWorldNode(), 20);
                MotionTrack track = new MotionTrack(this.subject.getModel(), path);
                track.setDirectionType(MotionEvent.Direction.Path);
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
                
                final Vector3f airDestination = new Vector3f(destination.getWorldCoordinates().x, PathFinding.airCreatureHeight, destination.getWorldCoordinates().z);
                
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
                        
                        if (subject instanceof AirborneCreature)
                        {
                            subject.getModel().setLocalTranslation(airDestination);
                        }
                        else
                        {
                            subject.getModel().setLocalTranslation(destination.getWorldCoordinates());
                        }
                        
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
