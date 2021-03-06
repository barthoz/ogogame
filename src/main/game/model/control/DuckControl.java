/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.model.control;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;
import main.game.Game;
import main.game.model.Duck;

/**
 *
 * @author s116861
 */
public class DuckControl extends AbstractControl implements Savable, Cloneable
{
    /**
     * Properties
     */
    
    private Game game;
    private Duck controllee;
    private static final float TIMELIMIT = 30;
    private float time = TIMELIMIT;
    
    /**
     * Constructors
     */
    
    /**
     * Serialization constructor.
     */
    public DuckControl() { }
    
    /**
     * Optional custom constructor with arguments that can init custom fields.
     * Note: you cannot modify the spatial here yet!
     */
    public DuckControl(Spatial spatial, Game game, Duck controllee)
    { 
      super.setSpatial(spatial);
      this.game = game;
      this.controllee = controllee;
    } 

    /**
     * Business logic
     */
    
    /** This is your init method. Optionally, you can modify 
      * the spatial from here (transform it, initialize userdata, etc). */
    @Override
    public void setSpatial(Spatial spatial)
    {
      super.setSpatial(spatial);
    }


    /** Implement your spatial's behaviour here.
      * From here you can modify the scene graph and the spatial
      * (transform them, get and set userdata, etc).
      * This loop controls the spatial while the Control is enabled. */
    @Override
    protected void controlUpdate(float tpf)
    {
      //if(spatial != null)
      //{
        time += tpf;
        if(time>TIMELIMIT){
            controllee.setQuackable(true);
            time = 0;
        }
      //}
    }

    @Override
    public Control cloneForSpatial(Spatial spatial)
    {
      final DuckControl control = new DuckControl();
      /* Optional: use setters to copy userdata into the cloned control */
      // control.setIndex(i); // example
      control.setSpatial(spatial);
      return control;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp)
    {
       /* Optional: rendering manipulation (for advanced users) */
    }

    @Override
    public void read(JmeImporter im) throws IOException
    {
        super.read(im);
        // im.getCapsule(this).read(...);
    }

    @Override
    public void write(JmeExporter ex) throws IOException
    {
        super.write(ex);
        // ex.getCapsule(this).write(...);
    }    
}
