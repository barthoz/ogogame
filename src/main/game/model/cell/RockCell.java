/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.model.cell;

import com.jme3.math.Vector3f;
import main.game.World;
import main.game.model.creature.AirborneCreature;
import main.game.model.creature.Creature;

/**
 *
 * @author Adrian
 */
public class RockCell extends Cell
{
    /**
     * Properties
     */
    
    /**
     * Constructor
     */
    
    public RockCell(World world, int xCoor, int yCoor, Vector3f worldCoordinates)
    {
        super(world, xCoor, yCoor, worldCoordinates);
    }
    /**
     * A creature is allowed in a RockCell if it is airborne
     *
     * @param creature
     * @return true if creature is an airborne creature and airborne
     */
    @Override
    public boolean creatureAllowed(Creature creature)
    {
        if (creature instanceof AirborneCreature)
        {
            AirborneCreature airCreature = (AirborneCreature) creature;

            if (airCreature.isAirborne())
            {
                return true;
            }

        }
        
        return false;
    }
}
