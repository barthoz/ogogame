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
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;
import main.game.Game;
import main.game.algorithm.PathFinding;
import main.game.model.creature.SeaCreature;

/**
 *
 * @author s116861
 */
public class SeaCreatureControl extends AbstractControl implements Savable, Cloneable, AnimEventListener
{

    /**
     * Properties
     */
    private Spatial stand;
    private Game game;
    private SeaCreature controllee;
    private AnimChannel channel;
    private AnimControl control;
    private float time = 0;

    /**
     * Constructors
     */
    /**
     * Serialization constructor.
     */
    public SeaCreatureControl()
    {
    }

    /**
     * Optional custom constructor with arguments that can init custom fields.
     * Note: you cannot modify the spatial here yet!
     */
    public SeaCreatureControl(Node spatial, Game game, SeaCreature controllee)
    {
        
        this.stand= spatial.getChild("Move");
        super.setSpatial(spatial);
        this.game = game;
        this.controllee = controllee;
        control = stand.getControl(AnimControl.class);
        control.addListener(this);
        channel = control.createChannel();
        channel.setAnim("Move");
    }

    /**
     * Business logic
     */
    /**
     * This is your init method. Optionally, you can modify the spatial from
     * here (transform it, initialize userdata, etc).
     */
    @Override
    public void setSpatial(Spatial spatial)
    {
        super.setSpatial(spatial);
    }

    /**
     * Implement your spatial's behaviour here. From here you can modify the
     * scene graph and the spatial (transform them, get and set userdata, etc).
     * This loop controls the spatial while the Control is enabled.
     */
    @Override
    protected void controlUpdate(float tpf)
    {
        if (spatial != null)
        {
            time += 2 * tpf;
            Vector3f pos = spatial.getLocalTranslation();
            pos.y = (float) (PathFinding.seaCreatureHeight + 3 * Math.sin(time));
            spatial.setLocalTranslation(pos);
            // spatial.rotate(tpf,tpf,tpf); // example behaviour
        }
    }

    @Override
    public Control cloneForSpatial(Spatial spatial)
    {
        final SeaCreatureControl control = new SeaCreatureControl();
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

    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName)
    {
        if (animName.equals("Move"))
        {
            channel.setAnim("Move", 0.50f);
            channel.setLoopMode(LoopMode.DontLoop);
            channel.setSpeed(1f);
        }
    }

    public void onAnimChange(AnimControl control, AnimChannel channel, String animName)
    {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public Spatial getStand()
    {
        return stand;
    }

    public void setStand(Spatial stand)
    {
        this.stand = stand;
    }
}
