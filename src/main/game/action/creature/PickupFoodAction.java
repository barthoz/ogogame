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
import com.jme3.scene.Node;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.exception.ActionNotEnabledException;
import main.exception.NoReachablePathException;
import main.game.Game;
import main.game.Player;
import main.game.algorithm.PathFinding;
import main.game.model.FoodSource;
import main.game.model.control.AirborneCreatureControl;
import main.game.model.control.FoodSourceControl;
import main.game.model.control.LandCreatureControl;
import main.game.model.creature.AirborneCreature;
import main.game.model.creature.Creature;
import main.game.model.creature.LandCreature;
import main.game.model.creature.SeaCreature;

/**
 *
 * @author s116861
 */
public class PickupFoodAction extends CreatureAction
{
    /**
     * Properties
     */
    
    private transient FoodSource foodSource;
    private int foodSourceId;
    
    /**
     * Constructor
     */
    
    public PickupFoodAction(Player player, Creature subject, FoodSource foodSource)
    {
        this.player = player;
        this.subject = subject;
        this.foodSource = foodSource;
        this.foodSourceId = foodSource.getId();
    }

    /**
     * Business logic
     */
    
    @Override
    public void deserialize(Game game)
    {
        super.deserialize(game);
        
        this.foodSource = game.getWorld().findFoodSourceById(this.foodSourceId);
    }
    
    @Override
    public boolean isEnabled(Game game)
    {
        if (this.foodSource.getLocation().creatureAllowed(subject) 
            && !this.subject.isInFight()
            && this.foodSource.hasFood()
            && this.subject.getLocation().distance(this.foodSource.getLocation()) <= this.subject.getActionRadius())
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
        if (!this.isEnabled(game))
        {
            throw new ActionNotEnabledException();
        }
        else
        {
            try
            {
                final MotionPath path = PathFinding.createMotionPath(game.getTerrain(), game.getWorld().getCells(), this.subject.getLocation(), this.foodSource.getLocation(), this.subject);
                
                final Cinematic cinematic = new Cinematic(game.getWorld().getWorldNode(), 20);
                MotionEvent track = new MotionEvent(this.subject.getModel(), path);
                
                 if(subject instanceof LandCreature)
                {
                    LandCreatureControl c= (LandCreatureControl)this.subject.getController();
                    Node s = (Node) c.getSpatial();
                    s.detachChild(c.getStand());
                    s.attachChild(c.getMove());
                    c.setSpatial(null);
                    c.setSpatial(s);
                    track.setDirectionType(MotionEvent.Direction.Path);
                }
                else if (subject instanceof SeaCreature){
                    track.setDirectionType(MotionEvent.Direction.LookAt);
                    track.setLookAt(this.foodSource.getLocation().getWorldCoordinates(), Vector3f.UNIT_Y);

                }
                else if (subject instanceof AirborneCreature)
                {
                   AirborneCreatureControl c = (AirborneCreatureControl) subject.getController();
                   Node s = (Node) c.getSpatial();
                   s.detachChild(c.getStand());
                   s.attachChild(c.getMove());
                   c.setSpatial(null);
                   c.setSpatial(s);
                   track.setDirectionType(MotionEvent.Direction.None);
                }
                
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
                
                //final Vector3f airDestination = foodSource.getLocation().getWorldCoordinates();//new Vector3f(foodSource.getLocation().getWorldCoordinates().x, PathFinding.airCreatureHeight, foodSource.getLocation().getWorldCoordinates().z);
                
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
                        if(subject instanceof LandCreature)
                        {
                            LandCreatureControl c= (LandCreatureControl)subject.getController();
                            Node s = (Node) c.getSpatial();
                            s.detachChild(c.getMove());
                            s.attachChild(c.getStand());
                            c.setSpatial(null);
                            c.setSpatial(s);
                        }

                        if(subject instanceof AirborneCreature)
                        {
                            AirborneCreatureControl c = (AirborneCreatureControl)subject.getController();
                            Node s = (Node) c.getSpatial();
                            s.detachChild(c.getMove());
                            s.attachChild(c.getStand());
                            c.setSpatial(null);
                            c.setSpatial(s);
                        }
                        
                        FoodSourceControl c= (FoodSourceControl)foodSource.getController();
                        Node s = (Node) c.getSpatial();
                        s.detachChild(c.getFull());
                        c.setSpatial(null);
                        c.setSpatial(s);
                        subject.getModel().setLocalTranslation(foodSource.getLocation().getWorldCoordinates());
                        //subject.setLocation(foodSource.getLocation());
                    }
                    
                });
                cinematic.play();
            } catch (NoReachablePathException ex) {
                Logger.getLogger(MoveAction.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            // When succeeded, perform some action
            this.foodSource.eat();
            this.player.increaseFood(1);
            
            /**
            * Update locations
            */

            // Update old location
            if (this.subject instanceof AirborneCreature)
            {
               this.subject.getLocation().removeCreature(this.subject, ((AirborneCreature) this.subject).isAirborne());
               ((AirborneCreature) this.subject).land();
            }
            else
            {
               this.subject.getLocation().removeCreature(this.subject, false);
            }

            // Update new location
            this.foodSource.getLocation().addCreature(this.subject, false);
            
            this.subject.setLocation(this.foodSource.getLocation());
            
            /**
            * Set in fight if necessary after moving
            */
            boolean mixedTeamsInCell = false;

            for (Creature creature : this.foodSource.getLocation().getOccupants())
            {
               if (!creature.getPlayer().equals(this.player))
               {
                   mixedTeamsInCell = true;
                   break;
               }
            }
            
            for (Creature creature : this.foodSource.getLocation().getAirborneOccupants())
            {
                if (!creature.getPlayer().equals(this.player))
                {
                    mixedTeamsInCell = true;
                    break;
                }
            }

            if (mixedTeamsInCell)
            {
               for (Creature creature : this.foodSource.getLocation().getOccupants())
               {
                   creature.setInFight(true);
               }
               
               for (Creature creature : this.foodSource.getLocation().getAirborneOccupants())
               {
                   creature.setInFight(true);
               }
            }
        }
    }
    
    /**
     * Getters & Setters
     */
}
