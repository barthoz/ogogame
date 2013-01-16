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
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.exception.ActionNotEnabledException;
import main.exception.NoReachablePathException;
import main.game.Game;
import main.game.Player;
import main.game.algorithm.PathFinding;
import main.game.model.Explosion;
import main.game.model.ModelFactory;
import main.game.model.cell.Cell;
import main.game.model.control.AirborneCreatureControl;
import main.game.model.control.LandCreatureControl;
import main.game.model.control.SeaCreatureControl;
import main.game.model.creature.AirborneCreature;
import main.game.model.creature.Creature;
import main.game.model.creature.LandCreature;
import main.game.model.creature.SeaCreature;

/**
 *
 * @author s117889
 */
public class AttackAction extends CreatureAction
{

    /**
     * Properties
     */
    private transient Cell destination;
    private transient Creature opponent;
    private String opponentId;
    private int destinationX;
    private int destinationY;
    private int randomKey;
    private boolean isGenerated;

    private Game tempGame;
    
    /**
     * Constructors
     */
    public AttackAction(Player player, Creature subject, Creature opponent, Cell destination, boolean isGenerated)
    {
        this.player = player;
        this.subject = subject;
        this.opponent = opponent;
        this.opponentId = opponent.getId();
        this.destination = destination;
        this.destinationX = destination.getXCoor();
        this.destinationY = destination.getYCoor();
        this.randomKey = new Random().nextInt(1000);
        this.isGenerated = isGenerated;
    }

    /**
     * Business Logic
     */
    @Override
    public void prepareForSerialization()
    {
        super.prepareForSerialization();
    }
    
    @Override
    public void deserialize(Game game)
    {
        super.deserialize(game);
        
        this.destination = game.getWorld().getCells()[this.destinationX][this.destinationY];
        this.opponent = game.getWorld().findCreatureById(this.opponentId);
    }

    @Override
    public boolean isEnabled(Game game)
    {
        if (this.destination.creatureAllowed(subject)
            && !this.player.getCreatures().contains(opponent)
            && this.subject.getLocation().distance(this.destination) <= this.subject.getActionRadius()
            && game.getWorld().getCreatures().contains(this.opponent)) // if player has not left yet
        {
            if (this.isGenerated)
            {
                if (this.destination.getOccupants().contains(this.opponent) || this.destination.getAirborneOccupants().contains(this.opponent))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }
    }

    @Override
    public void performAction(Game game) throws ActionNotEnabledException
    {
        this.tempGame = game;
        
        if (!isEnabled(game))
        {
            throw new ActionNotEnabledException();
        }
        else
        {
            this.subject.setInFight(true);
            this.opponent.setInFight(true);
            
            if (!this.subject.getLocation().equals(this.destination))
            {
                try
                {                    
                    // in moves that alter the subjects cell we have to check if 
                    // there are multiple creatures in the new cell and set them 
                    // all in a fight
                    
                    boolean mixedTeamsInCell = false;
                    
                    for (Creature creature : this.destination.getOccupants())
                    {
                        if (!creature.getPlayer().equals(this.player))
                        {
                            mixedTeamsInCell = true;
                            break;
                        }
                    }
                    
                    if (mixedTeamsInCell)
                    {
                        for (Creature creature : this.destination.getOccupants())
                        {
                            creature.setInFight(true);
                        }
                    }
                    
                    final MotionPath path = PathFinding.createMotionPath(game.getTerrain(), game.getWorld().getCells(), this.subject.getLocation(), this.destination, this.subject);

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
                        track.setLookAt(destination.getWorldCoordinates(), Vector3f.UNIT_Y);

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

                            if (subject instanceof AirborneCreature)
                            {
                                subject.getModel().setLocalTranslation(airDestination);
                            } else
                            {
                                subject.getModel().setLocalTranslation(destination.getWorldCoordinates());
                            }

                            subject.setLocation(destination);
                            
                            fight();
                        }
                    });
                    cinematic.play();
                } catch (NoReachablePathException ex)
                {
                    Logger.getLogger(MoveAction.class.getName()).log(Level.SEVERE, null, ex);
                }
                
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
                    ((AirborneCreature) this.subject).takeOff();
                    this.destination.addCreature(this.subject, true);
                }
                else
                {
                    this.destination.addCreature(this.subject, false);
                }
            }
            else
            {
                fight();
            }
            
            this.subject.setLocation(this.destination);
        }
    }

    private void fight()
    {        
        // define some algorithm to find the winner and the quantity of damage
        Explosion explosion = new Explosion(tempGame, destination);
        
        int hp = (int) Math.sqrt((double)subject.getLevel()/(double)opponent.getLevel());
        hp *= 0.02* this.randomKey;
        opponent.decreaseHealth(hp);
        
        if (this.subject.getHealth() <= 0)
        {
            //subject.setHealth(0);
            this.subject.setInFight(false);
            this.opponent.setInFight(false);
        }
        
        if (this.opponent.getHealth() <= 0)
        {
            this.subject.setInFight(false);
            this.opponent.setInFight(false);
        }
    }
    
    /**
     * Getters & Setters
     */
}
