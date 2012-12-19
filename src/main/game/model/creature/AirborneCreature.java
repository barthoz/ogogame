/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.model.creature;

import com.jme3.scene.Spatial;
import main.game.model.Player;
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
    
    private int stamina = 100;
    private boolean airborne = false;
    
    /**
     * Constructor
     */
    
    public AirborneCreature(Player player, int id, Spatial model)
    {
        super(player, id, model);
        this.controller = new AirborneCreatureControl(model);
        this.controller.setEnabled(true);
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
