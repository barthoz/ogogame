/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.model.cell;

import com.jme3.math.Vector3f;
import main.game.World;
import main.game.model.creature.Creature;

/**
 *
 * @author Adrian
 */
public class ShallowWaterCell extends Cell
{
    /**
     * Properties
     */
    
    /**
     * Constructor
     */
    
    public ShallowWaterCell(World world, int xCoor, int yCoor, Vector3f worldCoordinates)
    {
        super(world, xCoor, yCoor, worldCoordinates);
    }
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
