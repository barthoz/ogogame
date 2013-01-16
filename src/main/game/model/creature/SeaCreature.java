/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.model.creature;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import main.game.Player;
import main.game.model.ModelFactory;
import main.game.model.control.AirborneCreatureControl;
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
    
    public final static int CONST_COST = 3;
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
    
    @Override
    public void die()
    {
        super.die();
        
        // Change model
        SeaCreatureControl c = (SeaCreatureControl) this.controller;
        Node s = (Node) c.getSpatial();
        s.detachChild(c.getStand());
        s.attachChild(ModelFactory.getDeathTomb(this.player.getGame()));
        c.setSpatial(null);
        c.setSpatial(s);
    }
}
