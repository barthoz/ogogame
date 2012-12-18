/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.model;

import com.jme3.scene.Spatial;

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
    
    /**
     * Constructor
     */
    
    public FoodSource(int id, Spatial model)
    {
        this.id = id;
        this.model = model;
        this.hasFood = true;
        this.roundLastEaten = -1;
    }
    
    /**
     * Business logic
     */
    
    /**
     * Getters & Setters
     */
}
