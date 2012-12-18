/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.model.creature;

import com.jme3.scene.Spatial;
import main.game.model.Player;
import main.game.model.control.LandCreatureControl;

/**
 *
 * @author s116861
 */
public class LandCreature extends Creature
{
    /**
     * Constructor
     */
    
    public LandCreature(Player player, int id, Spatial model)
    {
        super(player, id, model);
        this.controller = new LandCreatureControl(model);
        this.controller.setEnabled(true);
    }
}