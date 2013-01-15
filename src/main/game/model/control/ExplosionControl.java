/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.model.control;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.effect.ParticleEmitter;
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
import main.game.action.Action;
import main.game.algorithm.PathFinding;
import main.game.model.Explosion;
import main.game.model.creature.AirborneCreature;

/**
 *
 * @author s116861
 */
public class ExplosionControl extends AbstractControl implements Savable, Cloneable
{

    /**
     * Properties
     */
    private Node spatial;
    private Game game;
    private Explosion controllee;
    private ParticleEmitter flame, flash, spark, roundspark, smoketrail, debris,
            shockwave;
    private float time=0;
    private float state=0;

    /**
     * Constructors
     */
    /**
     * Serialization constructor.
     */
    public ExplosionControl()
    {
    }

    public ExplosionControl(Node spatial, Game game, Explosion controllee)
    {

        super.setSpatial(spatial);
        //this.spatial = spatial;
        this.game = game;
        this.controllee = controllee;
        flame = (ParticleEmitter)spatial.getChild("Flame");
        flash = (ParticleEmitter)spatial.getChild("Flash");
        spark = (ParticleEmitter)spatial.getChild("Spark");
        smoketrail = (ParticleEmitter)spatial.getChild("SmokeTrail");
        debris = (ParticleEmitter)spatial.getChild("Debris");
        shockwave = (ParticleEmitter)spatial.getChild("Shockwave");
    }

    /**
     * Optional custom constructor with arguments that can init custom fields.
     * Note: you cannot modify the spatial here yet!
     */
    public ExplosionControl(Spatial spatial, Game game)
    {
        super.setSpatial(spatial);
        this.game = game;
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

    @Override
    public void update(float tpf)
    {
        this.controlUpdate(tpf);
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
            time += tpf;
            if (time > 1f && state == 0)
            {
                flash.emitAllParticles();
                spark.emitAllParticles();
                smoketrail.emitAllParticles();
                debris.emitAllParticles();
                shockwave.emitAllParticles();
                state++;
            }
            if (time > 1f + .05f && state == 1)
            {
                flame.emitAllParticles();
                roundspark.emitAllParticles();
                state++;
            }
            
            if (time > 5 && state == 2)
            {
                flash.killAllParticles();
                spark.killAllParticles();
                smoketrail.killAllParticles();
                debris.killAllParticles();
                flame.killAllParticles();
                roundspark.killAllParticles();
                shockwave.killAllParticles();
                
                this.game.removeExplosion(this.controllee);
            }
        }
    }

    @Override
    public Control cloneForSpatial(Spatial spatial)
    {
        final ExplosionControl control = new ExplosionControl();
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
