/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.model.creature;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import main.game.Player;
import main.game.model.control.AirborneCreatureControl;

/**
 *
 * @author s116861
 */
public class AirborneCreature extends Creature
{
    /**
     * Properties
     */
    
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
