/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.model.cell;

import com.jme3.math.Vector3f;
import main.game.model.creature.Creature;
import main.game.model.creature.SeaCreature;

/**
 *
 * @author Adrian
 */
public class LandCell extends Cell
{
    /**
     * Properties
     */
    
    /**
     * Constructor
     */
    
    public LandCell(int xCoor, int yCoor, Vector3f worldCoordinates)
    {
        super(xCoor, yCoor, worldCoordinates);
    }
    /**
     * A creature is allowed in a LandCell if it is not a SeaCreature
     *
     * @param creature
     * @return false if creature is a seacreature, else false
     */
    @Override
    public boolean creatureAllowed(Creature creature)
    {
        if (creature instanceof SeaCreature)
        {
            return false;
        }
        return true;
    }
}
