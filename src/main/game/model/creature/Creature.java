/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.model.creature;

import com.jme3.font.BitmapText;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import main.game.Game;
import main.game.Player;
import main.game.model.cell.Cell;

/**
 *
 * @author Daniel
 */
public abstract class Creature
{

    /**
     * Constants
     */
    
    public final static int TYPE_LAND = 0;
    public final static int TYPE_SEA = 1;
    public final static int TYPE_AIR = 2;
    
    /**
     * Properties
     */
    private Cell location;
    protected Player player;
    protected String id;
    protected Spatial model;
    protected AbstractControl controller;
    protected int health = 100;
    protected int level = 1;
    protected boolean isAlive = true;
    protected int roundDied = -1;
    protected int rangeOfSight = Game.CONST_INIT_RANGE_OF_SIGHT;
    protected int actionRadius = 20;
    protected boolean inFight = false;
    /**
     * tweakers, possible define a function to give more dynamic changes
     */
    private int extraRangeOfSight = 2;
    private int extraActionRadius = 2;
    private int extraHealth = 20;

    private BitmapText creatureHeader;
    
    /**
     * Constructors
     */
    public Creature(Player player, String id, Spatial model)
    {
        this.player = player;
        this.id = id;
        this.model = model;
    }

    /**
     * Business logic
     */
    
    public abstract String getCodeId();
    
    /**
     * Method that handles everyting when a creature gains a level
     * such as extra range of sight, action radius etc.
     */
    public void levelUp()
    {
        level++;
        rangeOfSight += extraRangeOfSight;
        actionRadius += extraActionRadius;
        health += extraHealth;
        if (health > 100)
        {
            health = 100;
        }
    }
    
    public void increaseHealth(int health)
    {        
        this.health += health;
        
        if (this.health > 100)
        {
            this.health = 100;
        }
    }
    
    public void decreaseHealth(int health)
    {
        this.health -= health;
        
        if (this.health <= 0)
        {
            // Creature died
            this.die();
        }
    }
    
    public void die()
    {
        this.health = 0;
        this.isAlive = false;
    }

    /**
     * Getters & Setters
     */
    public Player getPlayer()
    {
        return player;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public Spatial getModel()
    {
        return model;
    }

    public void setModel(Spatial model)
    {
        this.model = model;
    }

    public AbstractControl getController()
    {
        return controller;
    }

    public void setController(AbstractControl controller)
    {
        this.controller = controller;
    }

    public Cell getLocation() {
        return location;
    }

    public void setLocation(Cell location) {
        this.location = location;
    }

    public boolean isInFight()
    {
        return inFight;
    }

    public void setInFight(boolean inFight)
    {
        this.inFight = inFight;
    }

    public int getHealth()
    {
        return health;
    }

    public void setHealth(int health)
    {
        this.health = health;
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public boolean isIsAlive()
    {
        return isAlive;
    }

    public void setIsAlive(boolean isAlive)
    {
        this.isAlive = isAlive;
    }

    public int getRoundDied() {
        return roundDied;
    }

    public void setRoundDied(int roundDied) {
        this.roundDied = roundDied;
    }

    public int getRangeOfSight() {
        return rangeOfSight;
    }

    public void setRangeOfSight(int rangeOfSight) {
        this.rangeOfSight = rangeOfSight;
    }

    public int getActionRadius() {
        return actionRadius;
    }

    public void setActionRadius(int actionRadius) {
        this.actionRadius = actionRadius;
    }

    public BitmapText getCreatureHeader() {
        return creatureHeader;
    }

    public void setCreatureHeader(BitmapText creatureHeader) {
        this.creatureHeader = creatureHeader;
    }
}
