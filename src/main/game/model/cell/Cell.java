/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.model.cell;

import java.util.List;
import main.game.model.creature.Creature;
import static java.lang.Math.*;

/**
 *
 * @author Daniel
 */
public abstract class Cell
{

    /**
     * Properties
     */
    private int xCoor = 0;
    private int yCoor = 0;
    private List<Creature> occupant = null;
    private List<Creature> airborneOccupant = null;

    /**
     * Constructor
     */
    public Cell()
    {
    }

    /**
     * Business logic
     */
    /**
     * Calculates the distance between this cell and otherCell
     *
     * @param otherCell
     * @return distance from this cell to otherCell ceiled to an integer value
     */
    public int distance(Cell otherCell)
    {
        int x = otherCell.getXCoor();
        int y = otherCell.getYCoor();
        double result = sqrt(pow(xCoor - x, 2) + pow(yCoor - y, 2));
        return (int) ceil(result);
    }

    /**
     * Method to check wether a creature is allowed in this cell
     *
     * @param creature
     * @return boolean verifying that a creature is allowed in this cell
     */
    public abstract boolean creatureAllowed(Creature creature);

    /**
     * Getters & Setters
     */
    public int getXCoor()
    {
        return xCoor;
    }

    public void setXCoor(int xCoor)
    {
        this.xCoor = xCoor;
    }

    public int getYCoor()
    {
        return yCoor;
    }

    public void setYCoor(int yCoor)
    {
        this.yCoor = yCoor;
    }

    public List<Creature> getOccupant()
    {
        return occupant;
    }

    public void setOccupant(List<Creature> occupant)
    {
        this.occupant = occupant;
    }

    public List<Creature> getAirborneOccupant()
    {
        return airborneOccupant;
    }

    public void setAirborneOccupant(List<Creature> airborneOccupant)
    {
        this.airborneOccupant = airborneOccupant;
    }
}
