/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.model.cell;

import com.jme3.math.Vector3f;
import main.game.World;
import main.game.model.creature.AirborneCreature;
import main.game.model.creature.Creature;
import main.game.model.creature.SeaCreature;

/**
 *
 * @author Adrian
 */
public class DeepWaterCell extends Cell
{
    /**
     * Properties
     */
    
    /**
     * Constructor
     */
    
    public DeepWaterCell(World world, int xCoor, int yCoor, Vector3f worldCoordinates)
    {
        super(world, xCoor, yCoor, worldCoordinates);
    }
    
    /**
     * Business logic
     */
    
    
    
    /**
     * A creatur is allowed in a DeepWaterCell if it is a SeaCreature, or if it
     * is a AirborneCreature and airborne
     *
     * @param creature
     * @return true if satisfies conditions
     */
    @Override
    public boolean creatureAllowed(Creature creature)
    {
        if (creature instanceof SeaCreature)
        {
            return true;
        }
        
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
