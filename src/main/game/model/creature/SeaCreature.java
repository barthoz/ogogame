/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.model.creature;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import main.game.Player;
import main.game.model.control.SeaCreatureControl;

/**
 *
 * @author s116861
 */
public class SeaCreature extends Creature
{
    /**
     * Properties
     */
    
    public final static String CODE_ID = "Creature-Sea";
    
    /**
     * Constructor
     */
    
    public SeaCreature(Player player, String id, Node model)
    {
        super(player, id, model);
        this.controller = new SeaCreatureControl(model, player.getGame(), this);
        this.controller.setEnabled(true);
    }
    
    /**
     * Business logic
     */
    
    @Override
    public String getCodeId()
    {
        return "Creature-Sea";
    }
}
