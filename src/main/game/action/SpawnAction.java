/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.action;

import main.exception.ActionNotEnabledException;
import main.game.Game;
import main.game.Player;
import main.game.model.ModelFactory;
import main.game.model.creature.*;

/**
 *
 * @author Daniel
 */
public class SpawnAction extends Action
{
    /**
     * Properties
     */
    
    private String creatureType;
    
    /**
     * Constructor
     */
    
    public SpawnAction(Player player, String creatureType)
    {
        this.player = player;
        this.creatureType = creatureType;
    }
    
    /**
     * Business logic
     */
    
    @Override
    public boolean isEnabled(Game game)
    {
        if (this.player.getFood() - ModelFactory.getCreatureCost(this.creatureType) >= 0
            && this.player.getCreatures().size() < Game.CONST_CREATURES_LIMIT)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public void performAction(Game game) throws ActionNotEnabledException
    {
        if (!this.isEnabled(game))
        {
            throw new ActionNotEnabledException();
        }
        else
        {
            String id = this.player.getId() + "_" + this.player.retrieveAllocatedCreatureId();
            Creature creature = ModelFactory.createCreature(game, id, this.player, this.creatureType);
            this.player.addCreature(creature);
            game.getWorld().addCreature(creature);
            //game.getWorld().getSelectableObjects().attachChild(creature.getModel());
            creature.getLocation().repositionCreatures();
            System.out.println(id);
            
            // Update food
            this.player.decreaseFood(ModelFactory.getCreatureCost(this.creatureType));
        }
    }
    
    /**
     * Getters & Setters
     */
}
