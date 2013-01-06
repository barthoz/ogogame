/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.action;

import main.exception.ActionNotEnabledException;
import main.game.Game;
import main.game.Player;
import main.game.model.ModelFactory;
import main.game.model.creature.AirborneCreature;
import main.game.model.creature.Creature;
import main.game.model.creature.LandCreature;
import main.game.model.creature.SeaCreature;

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
        return true;
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
            Creature creature = ModelFactory.createCreature(game.getAssetManager(), this.player.getId() + "_" + this.player.retrieveAllocatedCreatureId(), this.player, this.creatureType);
            this.player.addCreature(creature);
        }
    }
    
    /**
     * Getters & Setters
     */
}
