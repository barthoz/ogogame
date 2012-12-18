/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.model.creature;

import com.jme3.scene.Spatial;
import main.game.model.Player;
import main.game.model.control.SeaCreatureControl;

/**
 *
 * @author s116861
 */
public class SeaCreature extends Creature
{
    /**
     * Constructor
     */
    
    public SeaCreature(Player player, int id, Spatial model)
    {
        super(player, id, model);
        this.controller = new SeaCreatureControl(model);
        this.controller.setEnabled(true);
    }
}
