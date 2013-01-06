/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.model.control;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;
import java.util.List;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;
import main.game.Game;
import main.game.action.Action;
import main.game.action.creature.CreatureAction;
import main.game.model.creature.LandCreature;

/**
 *
 * @author s116861
 */
public class LandCreatureControl extends AbstractControl implements Savable, Cloneable
{
    /**
     * Properties
     */
    
    private Game game;
    private LandCreature controllee;
    private Action openAction;
    
    /**
     * Constructors
     */
    
    /**
     * Serialization constructor.
     */
    public LandCreatureControl() { }
    
    /**
     * Optional custom constructor with arguments that can init custom fields.
     * Note: you cannot modify the spatial here yet!
     */
    public LandCreatureControl(Spatial spatial, Game game, LandCreature controllee)
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
        if (this.game.isInSetMode())
        {
            /**
             * Game in SET-mode
             */
            
            
        }
        else
        {
            /**
             * Game in GET-mode
             */
            
            // Check if there are any open actions
            if (this.openAction != null)
            {
                // Identify the type of action and perform it
                
            }
            else
            {
                // Check if there are any other actions
                List<CreatureAction> actions = this.controllee.getPlayer().getCreatureActions(this.controllee);
                if (actions.size() > 0)
                {
                    // Perform the next action
                    this.openAction = actions.get(0);
                }
                else
                {
                    // We are done performing all actions
                }
            }
        }
        
      if (spatial != null)
      {
        //spatial.rotate(tpf,tpf,tpf); // example behaviour
      }
    }

    @Override
    public Control cloneForSpatial(Spatial spatial)
    {
      final LandCreatureControl control = new LandCreatureControl();
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
