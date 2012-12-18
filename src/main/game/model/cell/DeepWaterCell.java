/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.model.cell;

import main.game.model.creature.AirborneCreature;
import main.game.model.creature.Creature;
import main.game.model.creature.SeaCreature;

/**
 *
 * @author Adrian
 */
public class DeepWaterCell extends Cell {

    /**
     * A creatur is allowed in a DeepWaterCell if it is a SeaCreature, or if it
     * is a AirborneCreature and airborne
     *
     * @param creature
     * @return true if satisfies conditions
     */
    @Override
    public boolean creatureAllowed(Creature creature) {
        if (creature instanceof SeaCreature) {
            return true;
        }
        if (creature instanceof AirborneCreature) {
            AirborneCreature airCreature = (AirborneCreature) creature;

            if (airCreature.isAirborne()) {
                return true;
            }

        }
        return false;
    }
}
