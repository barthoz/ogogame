/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.model.creature;

import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import main.game.Game;
import main.game.model.Player;

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
    
    protected Player player;
    protected String id;
    protected Spatial model;
    protected AbstractControl controller;
    
    protected int health = 100;
    protected int level = 1;
    protected boolean isAlive = true;
    protected int roundDied = -1;
    protected int rangeOfSight = Game.CONST_INIT_RANGE_OF_SIGHT;
    protected int actionRadius;
    protected boolean inFight = false;
    
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
     * Getters & Setters
     */
    
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Spatial getModel() {
        return model;
    }

    public void setModel(Spatial model) {
        this.model = model;
    }

    public AbstractControl getController() {
        return controller;
    }

    public void setController(AbstractControl controller) {
        this.controller = controller;
    }
}
