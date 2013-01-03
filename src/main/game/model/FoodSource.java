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
    private final int id;
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

    public boolean isHasFood()
    {
        return hasFood;
    }

    public void setHasFood(boolean hasFood)
    {
        this.hasFood = hasFood;
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
}
