/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.action.creature;

import main.exception.ActionNotEnabledException;
import main.game.Game;
import main.game.Player;
import main.game.model.creature.Creature;

/**
 *
 * @author s116861
 */
public class EatAction extends CreatureAction
{
    /**
     * Properties
     */
    
    public final static int HEALTH_INCREASE = 5;
    
    private int numFood;
    
    /**
     * Constructor
     */
    
    public EatAction(Player player, Creature subject, int numFood)
    {
        this.player = player;
        this.subject = subject;
        this.numFood = numFood;
    }
    
    /**
     * Business logic
     */

    @Override
    public boolean isEnabled(Game game)
    {
        if (this.player.getFood() - numFood < 0)
        {
            return false;
        }
        else
        {
            return true;
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
            // Perform action
            this.subject.increaseHealth(HEALTH_INCREASE * this.numFood);
            this.player.decreaseFood(this.numFood);
        }
    }
}
