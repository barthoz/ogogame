/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.model;

import com.jme3.scene.Spatial;
import main.game.Game;
import main.game.model.control.FoodSourceControl;

/**
 *
 * @author Daniel
 */
public class FoodSource
{
    /**
     * Properties
     */
    
    private Spatial model;
    private int id;
    private boolean hasFood;
    private int roundLastEaten;
    private FoodSourceControl controller;
    
    /**
     * Constructor
     */
    
    public FoodSource(int id, Spatial model, Game game)
    {
        this.id = id;
        this.model = model;
        this.hasFood = true;
        this.roundLastEaten = -1;
        this.controller = new FoodSourceControl(model, game, this);
    }
    
    /**
     * Business logic
     */
    
    /**
     * Getters & Setters
     */
}
