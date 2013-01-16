/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.model.creature;

import com.jme3.cinematic.Cinematic;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import main.game.Player;
import main.game.model.ModelFactory;
import main.game.model.control.AirborneCreatureControl;
import main.game.model.control.LandCreatureControl;

/**
 *
 * @author s116861
 */
public class AirborneCreature extends Creature
{
    /**
     * Properties
     */
    
    public final static int CONST_COST = 5;
    public final static int CONST_STAMINA_DECREASE = 15;
    public final static int CONST_STAMINA_INCREASE = 20;
    public final static String CODE_ID = "Creature-Airborne";
    
    private int stamina = 100;
    private boolean airborne = false;
    
    
    
    /**
     * Constructor
     */
    
    public AirborneCreature(Player player, String id, Node model)
    {
        super(player, id, model);
        this.controller = new AirborneCreatureControl(model, player.getGame(), this);
        this.controller.setEnabled(true);
    }
    
    /**
     * Business logic
     */
    
    @Override
    public String getCodeId()
    {
        return "Creature-Airborne";
    }
    
    public void land()
    {
        this.airborne = false;
    }
    
    public void takeOff()
    {
        this.airborne = true;
    }
    
    /**
     * Increase the stamina for this round.
     * 
     * @Pre this.isAirborne == false
     */
    public void increaseStamina()
    {
        if (this.stamina + CONST_STAMINA_INCREASE >= 100)
        {
            this.stamina = 100;
        }
        else
        {
            this.stamina += CONST_STAMINA_INCREASE;
        }
    }
    
    /**
     * Decrease the stamina for this round.
     * 
     * @Pre this.isAirbone == true
     */
    public void decreaseStamina()
    {
        if (this.stamina - CONST_STAMINA_DECREASE <= 0)
        {
            this.stamina = 0;
            this.die();
        }
        else
        {
            this.stamina -= CONST_STAMINA_DECREASE;
        }
    }
    
    @Override
    public void die()
    {
        super.die();
        
        // Change model
        AirborneCreatureControl c = (AirborneCreatureControl) this.controller;
        Node s = (Node) c.getSpatial();
        s.detachChild(c.getStand());
        s.attachChild(ModelFactory.getDeathTomb(this.player.getGame()));
        c.setSpatial(null);
        c.setSpatial(s);
        
        // If airborne, put it on the ground
        if (this.airborne)
        {
            Cinematic cinematic = new Cinematic(this.location.getWorld().getWorldNode(), 5);
            MotionPath path = new MotionPath();
            path.addWayPoint(this.model.getWorldTranslation());
            path.addWayPoint(this.location.getWorldCoordinates());
            
            MotionEvent track = new MotionEvent(this.model, path);
            cinematic.addCinematicEvent(0, track);
            cinematic.fitDuration();
            this.player.getGame().getStateManager().attach(cinematic);
            cinematic.play();
        }
    }
    
    /**
     * Getters & Setters
     */
    
    public int getStamina() {
        return stamina;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public boolean isAirborne() {
        return airborne;
    }

    public void setAirborne(boolean airborne) {
        this.airborne = airborne;
    }
}
