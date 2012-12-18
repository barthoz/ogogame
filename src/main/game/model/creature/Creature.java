/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.model.creature;

import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
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
    protected int id;
    protected Spatial model;
    protected AbstractControl controller;
    
    /**
     * Constructors
     */
    
    public Creature(Player player, int id, Spatial model)
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
