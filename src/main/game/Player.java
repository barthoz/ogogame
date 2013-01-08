/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game;

import main.game.model.creature.Creature;
import com.jme3.scene.Geometry;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import main.game.Game;
import main.game.action.Action;
import main.game.action.creature.CreatureAction;
import main.game.model.Base;

/**
 *
 * @author Daniel
 */
public class Player
{
    /**
     * Properties
     */
    
    private Game game;
    
    private int id;
    private String username;
    private boolean isAlive = true;
    private int food = Game.CONST_INIT_START_FOOD;
    private int numFlees = 0;
    private boolean boughtCreature = false;
    private Base base;
    private List<Action> actions;
    private List<Creature> creatures;
    
    private int allocatedCreatureId;
   
    /**
     * Constructor
     */
    
    public Player(Game game, int id, String username)
    {
        this.game = game;
        this.id = id;
        this.username = username;
        this.actions = new ArrayList<Action>();
        this.creatures = new ArrayList<Creature>();
        
        this.allocatedCreatureId = 0;
    }
    
    /**
     * Business logic
     */
    
    public int retrieveAllocatedCreatureId()
    {
        this.allocatedCreatureId++;
        return this.allocatedCreatureId - 1;
    }
    
    public void addCreature(Creature creature)
    {
        this.creatures.add(creature);
    }
    
    public void removeCreature(Creature creature)
    {
        this.creatures.remove(creature);
    }
    
    public void addAction(Action action)
    {
        this.actions.add(action);
    }
    
    public List<CreatureAction> getCreatureActions(Creature subject)
    {
        List<CreatureAction> creatureActions = new ArrayList<CreatureAction>();
        
        for (Action action : this.actions)
        {
            if (action instanceof CreatureAction)
            {
                if (((CreatureAction) action).getSubject().equals(subject))
                {
                    creatureActions.add((CreatureAction) action);
                }
            }
        }
        
        return creatureActions;
    }
    
    /**
     * Register an action with the Player.
     * 
     * @param action to register
     */
    public void registerAction(Action action)
    {
        this.actions.add(action);
    }
    
    /**
     * Increase the food of the player.
     */
    public void increaseFood(int food)
    {
        this.food += food;
    }
    
    /**
     * Decrease the food of the player.
     * @Pre this.food - food >= 0
     */
    public void decreaseFood(int food)
    {
        this.food -= food;
    }
    
    /**
     * Getters & Setters
     */
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isIsAlive() {
        return isAlive;
    }

    public void setIsAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public int getFood() {
        return food;
    }

    public void setFood(int food) {
        this.food = food;
    }

    public int getNumFlees() {
        return numFlees;
    }

    public void setNumFlees(int numFlees) {
        this.numFlees = numFlees;
    }

    public boolean isBoughtCreature() {
        return boughtCreature;
    }

    public void setBoughtCreature(boolean boughtCreature) {
        this.boughtCreature = boughtCreature;
    }

    public Base getBase() {
        return base;
    }

    public void setBase(Base base) {
        this.base = base;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public Game getGame() {
        return game;
    }

    public List<Creature> getCreatures() {
        return creatures;
    }

    public int getAllocatedCreatureId() {
        return allocatedCreatureId;
    }

    public void setAllocatedCreatureId(int allocatedCreatureId) {
        this.allocatedCreatureId = allocatedCreatureId;
    }
}