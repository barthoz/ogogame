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
     * Constructor
     */
    
    public AirborneCreature(Player player, int id, Spatial model)
    {
        super(player, id, model);
        this.controller = new AirborneCreatureControl(model);
        this.controller.setEnabled(true);
    }
}
