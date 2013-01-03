/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.model;

import com.jme3.scene.Spatial;
import main.game.Game;
import main.game.model.control.DuckControl;

/**
 *
 * @author Daniel
 */
public class Duck
{

    /**
     * Properties
     */
    private Spatial model;
    private DuckControl controller;

    /**
     * Constructor
     */
    public Duck(Spatial model, Game game)
    {
        this.model = model;
        this.controller = new DuckControl(model, game, this);
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

    public DuckControl getController()
    {
        return controller;
    }

    public void setController(DuckControl controller)
    {
        this.controller = controller;
    }
}
