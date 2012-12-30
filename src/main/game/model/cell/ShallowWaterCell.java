/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.model.cell;

import main.game.model.creature.Creature;

/**
 *
 * @author Adrian
 */
public class ShallowWaterCell extends Cell
{

    /**
     * All creatures are allowed
     *
     * @param creature
     * @return true
     */
    @Override
    public boolean creatureAllowed(Creature creature)
    {
        return true;
    }
}
