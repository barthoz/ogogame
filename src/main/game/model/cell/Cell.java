/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.model.cell;

import com.jme3.math.Vector3f;
import java.util.List;
import main.game.model.creature.Creature;
import static java.lang.Math.*;
import java.util.HashSet;
import java.util.Set;
import main.game.World;

/**
 *
 * @author Daniel
 */
public abstract class Cell
{

    /**
     * Properties
     */    
    private World world;
    private int x;
    private int y;
    
    private int xCoor;
    private int yCoor;
    private Vector3f worldCoordinates;
    private List<Creature> occupant = null;
    private List<Creature> airborneOccupant = null;

    /**
     * Constructor
     */
    public Cell(World world, int xCoor, int yCoor, Vector3f worldCoordinates)
    {
        this.world = world;
        this.xCoor = xCoor;
        this.yCoor = yCoor;
        this.worldCoordinates = worldCoordinates;
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
     * Retrieves all neighbouring cells which are either orthogonal or diagonal to this cell.
     * 
     * @return 
     */
    public Set<Cell> retrieveNeighbouringCells()
    {
        Cell[][] cells = this.world.getCells();
        Set<Cell> neighbours = new HashSet<Cell>();
        
        int x = this.xCoor;
        int y = this.yCoor;
        
        // left
        if (x > 1)
        {
           // up
           if (y > 1)
           {
               neighbours.add(cells[x - 1][y - 1]);
               neighbours.add(cells[x][y - 1]);
           }

           // middle
           neighbours.add(cells[x - 1][y]);

           // down
           if (y + 1 < cells[0].length)
           {
               neighbours.add(cells[x - 1][y + 1]);
               neighbours.add(cells[x][y + 1]);
           }
        }
        
        // right
        if (x + 1 < cells.length)
        {
            // up
            if (y > 1)
            {
                neighbours.add(cells[x + 1][y - 1]);
            }
            
            // middle
            neighbours.add(cells[x + 1][y]);
            
            // down
            if (y + 1 < cells[0].length)
            {
                neighbours.add(cells[x + 1][y + 1]);
            }
        }
        
        return neighbours;
    }
    
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

    public int getxCoor() {
        return xCoor;
    }

    public void setxCoor(int xCoor) {
        this.xCoor = xCoor;
    }

    public int getyCoor() {
        return yCoor;
    }

    public void setyCoor(int yCoor) {
        this.yCoor = yCoor;
    }

    public Vector3f getWorldCoordinates() {
        return worldCoordinates;
    }

    public void setWorldCoordinates(Vector3f worldCoordinates) {
        this.worldCoordinates = worldCoordinates;
    }
}
