/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.action.creature;

import com.jme3.cinematic.Cinematic;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.MotionPathListener;
import com.jme3.cinematic.events.CinematicEvent;
import com.jme3.cinematic.events.CinematicEventListener;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.math.Vector3f;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.exception.ActionNotEnabledException;
import main.exception.NoReachablePathException;
import main.game.Game;
import main.game.Player;
import main.game.algorithm.PathFinding;
import main.game.model.cell.Cell;
import main.game.model.creature.AirborneCreature;
import main.game.model.creature.Creature;

/**
 *
 * @author s117889
 */
public class FleeAction extends CreatureAction
{

    private transient Cell destination;
    private int destinationX;
    private int destinationY;

    /**
     * Constructors
     */
    public FleeAction(Player player, Creature subject, Cell destination)
    {
        this.player = player;
        this.subject = subject;
        this.destination = destination;
        this.destinationX = destination.getXCoor();
        this.destinationY = destination.getYCoor();
    }

    /**
     * Business Logic
     */
    
    @Override
    public void deserialize(Game game)
    {
        super.deserialize(game);
        
        this.destination = game.getWorld().getCells()[this.destinationX][this.destinationY];
    }

    @Override
    public boolean isEnabled(Game game)
    {
        if (this.subject.isInFight()
            && !this.subject.getLocation().equals(this.destination)
            && this.destination.creatureAllowed(this.subject)
            && this.player.getFood() - this.player.getFleeCost() >= 0
            && this.subject.getLocation().distance(this.destination) <= this.subject.getActionRadius())
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
        if (!isEnabled(game))
        {
            throw new ActionNotEnabledException();
        }
        else
        {
            try
            {
                /**
                 * Update flee cost
                 */
                
                this.player.decreaseFood(this.player.getFleeCost());
                this.player.increaseFleeCost();
                
                if (subject.getLocation().getOccupants().size() <= 2)
                {
                    for (Creature c : subject.getLocation().getOccupants())
                    {
                        c.setInFight(false);
                    }
                }
                else
                {
                    subject.setInFight(false);
                }
                final MotionPath path = PathFinding.createMotionPath(game.getTerrain(), game.getWorld().getCells(), this.subject.getLocation(), this.destination, this.subject);

                final Cinematic cinematic = new Cinematic(game.getWorld().getWorldNode(), 20);
                MotionEvent track = new MotionEvent(this.subject.getModel(), path);

                /**
                 * Not sure how to fix this
                 */
                //track.setDirectionType(MotionEvent.Direction.Path);
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
                    public void onPlay(CinematicEvent cinematic)
                    {
                        //throw new UnsupportedOperationException("Not supported yet.");
                    }

                    public void onPause(CinematicEvent cinematic)
                    {
                        //throw new UnsupportedOperationException("Not supported yet.");
                    }

                    public void onStop(CinematicEvent cinematic)
                    {
                        //throw new UnsupportedOperationException("Not supported yet.");

                        if (subject instanceof AirborneCreature)
                        {
                            subject.getModel().setLocalTranslation(airDestination);
                        } else
                        {
                            subject.getModel().setLocalTranslation(destination.getWorldCoordinates());
                        }

                        subject.setLocation(destination);
                    }
                });
                cinematic.play();
            }
            catch (NoReachablePathException ex)
            {
                Logger.getLogger(MoveAction.class.getName()).log(Level.SEVERE, null, ex);
            }
            
           /**
            * Update locations
            */

            // Update old location
            if (this.subject instanceof AirborneCreature)
            {
               this.subject.getLocation().removeCreature(this.subject, ((AirborneCreature) this.subject).isAirborne());
            }
            else
            {
               this.subject.getLocation().removeCreature(this.subject, false);
            }

            // Update new location
            if (this.subject instanceof AirborneCreature)
            {
               ((AirborneCreature) this.subject).setAirborne(true);
               this.destination.addCreature(this.subject, true);
            }
            else
            {
               this.destination.addCreature(this.subject, false);
            }
        }
    }
    /**
     * Getters & Setters
     */
}
