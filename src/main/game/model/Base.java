/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.model;

import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import main.game.model.control.BaseControl;

/**
 *
 * @author Daniel
 */
public class Base
{

    /**
     * Properties
     */
    private final int id;
    private Spatial model;
    private Player player;
    private BaseControl controller;

    /**
     * Constructors
     */
    public Base(int id, Spatial model, Player player)
    {
        this.id = id;
        this.model = model;
        this.player = player;
        this.controller = new BaseControl(model, player.getGame(), this);
    }

    /**
     * Getters & Setters
     */
    public int getId()
    {
        return id;
    }

    public Spatial getModel()
    {
        return model;
    }

    public void setModel(Spatial model)
    {
        this.model = model;
    }

    public Player getPlayer()
    {
        return player;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }

    public BaseControl getController()
    {
        return controller;
    }

    public void setController(BaseControl controller)
    {
        this.controller = controller;
    }
}
