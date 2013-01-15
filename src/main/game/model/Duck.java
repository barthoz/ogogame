/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.model;

import com.jme3.audio.AudioNode;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import main.game.Game;
import main.game.model.cell.Cell;
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
    private Cell location;
    private boolean quackable = true;

    /**
     * Constructor
     */
    public Duck(Spatial model, Game game, Cell location)
    {
        this.model = model;
        this.location = location;
        this.controller = new DuckControl(model, game, this);

        model.setLocalTranslation(location.getWorldCoordinates());
    }

    /**
     * Business Logic
     */
    public void quack(AudioNode audio, boolean force)
    {
        if (quackable || force)
        {
            audio.playInstance();
            quackable = false;
        }
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

    public Cell getLocation()
    {
        return location;
    }

    public void setLocation(Cell location)
    {
        this.location = location;
    }

    public boolean isQuackable()
    {
        return quackable;
    }

    public void setQuackable(boolean quackable)
    {
        this.quackable = quackable;
    }
    
    
}
