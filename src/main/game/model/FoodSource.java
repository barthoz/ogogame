/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.model;

import com.jme3.scene.Spatial;
import main.game.Game;
import main.game.model.cell.Cell;
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
    private final int id;
    private boolean hasFood;
    private int roundLastEaten;
    private FoodSourceControl controller;
    private Cell location;

    /**
     * Constructor
     */
    public FoodSource(int id, Spatial model, Game game, Cell location)
    {
        this.id = id;
        this.model = model;
        this.location = location;
        this.hasFood = true;
        this.roundLastEaten = -1;
        this.controller = new FoodSourceControl(model, game, this);
        
        this.location.setFoodSource(this);
    }

    /**
     * Business logic
     */
    
    /**
     * sets the boolean hasFood to true if necesarry
     * @param currentRound 
     * @param regenTime 
     */
    public void regenerateFood(int currentRound, int regenTime)
    {
        if (!hasFood)
        {
            if (currentRound - roundLastEaten >= regenTime)
            {
                hasFood = true;
            }
        }
    }

    public int getId()
    {
        return id;
    }
    
    public void refill()
    {
        this.hasFood = true;
    }
    
    public void eat()
    {
        this.hasFood = false;
    }

    /**
     * Getters & Setters
     */
    public Spatial getModel()
    {
        return model;
    }

    public void setModel(Spatial model)
    {
        this.model = model;
    }

    public boolean hasFood()
    {
        return this.hasFood;
    }

    public int getRoundLastEaten()
    {
        return roundLastEaten;
    }

    public void setRoundLastEaten(int roundLastEaten)
    {
        this.roundLastEaten = roundLastEaten;
    }

    public FoodSourceControl getController()
    {
        return controller;
    }

    public void setController(FoodSourceControl controller)
    {
        this.controller = controller;
    }

    public Cell getLocation() {
        return location;
    }

    public void setLocation(Cell location) {
        this.location = location;
    }
}
