/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.model.creature;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import main.game.Player;
import main.game.model.ModelFactory;
import main.game.model.control.LandCreatureControl;

/**
 *
 * @author s116861
 */
public class LandCreature extends Creature
{
    /**
     * Properties
     */
    
    public final static int CONST_COST = 2;
    public final static String CODE_ID = "Creature-Land";
    
    /**
     * Constructor
     */
    
    public LandCreature(Player player, String id, Node model)
    {
        super(player, id, model);
        this.controller = new LandCreatureControl(model, player.getGame(), this);
        this.controller.setEnabled(true);
    }
    
    /**
     * Business logic
     */
    
    @Override
    public String getCodeId()
    {
        return "Creature-Land";
    }
    
    @Override
    public void die()
    {
        super.die();
        
        // Change model
        LandCreatureControl c = (LandCreatureControl) this.controller;
        Node s = (Node) c.getSpatial();
        s.detachChild(c.getStand());
        s.attachChild(ModelFactory.getDeathTomb(this.player.getGame()));
        c.setSpatial(null);
        c.setSpatial(s);
    }
}