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

    private Game tempGame;
    
    /**
     * Constructors
     */
    public AttackAction(Player player, Creature subject, Creature opponent, Cell destination)
    {
        this.player = player;
        this.subject = subject;
        this.opponent = opponent;
        this.opponentId = opponent.getId();
        this.destination = destination;
        this.destinationX = destination.getXCoor();
        this.destinationY = destination.getYCoor();
        this.randomKey = new Random().nextInt(1000);
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
        this.tempGame = game;
        
        if (!isEnabled(game))
        {
            throw new ActionNotEnabledException();
        }
        else
        {
            if (!this.subject.getLocation().equals(this.destination))
            {
                try
                {
                    subject.setInFight(true);
                    opponent.setInFight(true);
                    
                    // in moves that alter the subjects cell we have to check if 
                    // there are multiple creatures in the new cell and set them 
                    // all in a fight

                    List<Creature> cell = destination.getOccupants();
                    for (int i = 0; i < cell.size(); i++)
                    {
                        if (!cell.get(i).getPlayer().equals(this.player))
                        {
                            for (Creature c : destination.getOccupants())
                            {
                                c.setInFight(true);
                            }
                            break;
                        }
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
