/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.model.cell;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import java.util.List;
import main.game.model.creature.Creature;
import static java.lang.Math.*;
import java.util.ArrayList;
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
    private List<Creature> occupants = new ArrayList<Creature>();
    private List<Creature> airborneOccupants = new ArrayList<Creature>();

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
     * Add occupant.
     * 
     * @param creature
     * @param airborne 
     */
    public void addCreature(Creature creature, boolean airborne)
    {
        if (airborne)
        {
            this.airborneOccupants.add(creature);
        }
        else
        {
            this.occupants.add(creature);
        }
    }
    
    /**
     * Remove occupant.
     * 
     * @param creature
     * @param airborne 
     */
    public void removeCreature(Creature creature, boolean airborne)
    {
        if (airborne)
        {
            this.airborneOccupants.remove(creature);
        }
        else
        {
            this.occupants.remove(creature);
        }
    }
    
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
    public List<Cell> retrieveNeighbouringCells()
    {
        Cell[][] cells = this.world.getCells();
        List<Cell> neighbours = new ArrayList<Cell>();
        
        int x = this.xCoor;
        int y = this.yCoor;
        
        // left
        if (x > 1)
        {
           // up
           if (y > 1)
           {
               neighbours.add(cells[x - 1][y - 1]);
           }

           // middle
           neighbours.add(cells[x - 1][y]);

           // down
           if (y + 1 < cells[0].length)
           {
               neighbours.add(cells[x - 1][y + 1]);
           }
        }
        
        // middle-top
        if (y + 1 < cells[0].length)
        {
           neighbours.add(cells[x][y + 1]);
        }
        
        // middle-bottom
        if (y > 1)
        {
            neighbours.add(cells[x][y - 1]);
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
     * Repositions creatures on the cell. in case there 
     * are multiple creatures or there were
     * multiple creatures and now only one.
     */
    public void repositionCreatures()
    {
        if (occupants.size() == 1)
        {
            occupants.get(0).getModel().setLocalTranslation(this.worldCoordinates);
        } 
        else
        {
            List<Cell> neighbours = this.retrieveNeighbouringCells();
            Cell neighbour;
            if (occupants.size() == 2)
            {
                if (neighbours.get(0).distance(this) > neighbours.get(1).distance(this))
                {
                    neighbour = neighbours.get(0);
                } 
                else
                {
                    neighbour = neighbours.get(1);
                }
                Vector3f diff = this.worldCoordinates.subtract(neighbour.getWorldCoordinates());
                diff.setY(0);
                diff = diff.divide(2);
                occupants.get(0).getModel().setLocalTranslation(this.worldCoordinates.add(diff));
                occupants.get(1).getModel().setLocalTranslation(this.worldCoordinates.subtract(diff));
            } 
            else
            {
                System.out.println("this is 3+ : " + occupants.size());
                if (neighbours.get(0).distance(this) <= neighbours.get(1).distance(this))
                {
                    neighbour = neighbours.get(0);
                } 
                else
                {
                    neighbour = neighbours.get(1);
                }
                Vector3f diff = this.worldCoordinates.subtract(neighbour.getWorldCoordinates());
                diff.setY(0);
                diff = diff.divide(2);
                float radius = FastMath.sqrt(FastMath.pow(diff.x, 2) + FastMath.pow(diff.z, 2));
                
                for (int i = 0; i < occupants.size(); i++)
                {
                    diff.setX(radius * FastMath.cos(i * FastMath.TWO_PI / occupants.size()));
                    diff.setZ(radius * FastMath.sin(i * FastMath.TWO_PI / occupants.size()));
                    occupants.get(i).getModel().setLocalTranslation(this.worldCoordinates.add(diff));
                }
            }
        }
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

    public List<Creature> getOccupants()
    {
        return occupants;
    }

    public void setOccupants(List<Creature> occupant)
    {
        this.occupants = occupant;
    }

    public List<Creature> getAirborneOccupants()
    {
        return airborneOccupants;
    }

    public void setAirborneOccupants(List<Creature> airborneOccupant)
    {
        this.airborneOccupants = airborneOccupant;
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

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }
}
