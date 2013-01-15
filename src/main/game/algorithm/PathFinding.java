/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.algorithm;

import com.jme3.cinematic.MotionPath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.terrain.geomipmap.TerrainQuad;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import main.exception.NoReachablePathException;
import main.game.model.cell.*;
import main.game.model.creature.*;

/**
 *
 * @author s116861
 */
public class PathFinding
{
    /**
     * Properties
     */
    
    public final static int airCreatureHeight = 160;
    public final static int seaCreatureHeight = -10;
    
    /**
     * Constructor
     */
    
    /**
     * Business logic
     */
    
    public static MotionPath createMotionPath(TerrainQuad terrain, Cell[][] cells, Cell from, Cell to, Creature creature) throws NoReachablePathException
    {
        if (creature instanceof AirborneCreature)
        {
            AirborneCreature airCreature = (AirborneCreature) creature;
            airCreature.takeOff();
        }
        
        if (!to.creatureAllowed(creature))
        {
            throw new NoReachablePathException();
        }
        
        MotionPath motionPath = new MotionPath();
        
        /**
         * Different for air creatures
         */
        if (creature instanceof AirborneCreature)
        {
            // Add take off waypoint
            Vector3f takeOffWayPoint = new Vector3f(from.getWorldCoordinates().x, airCreatureHeight, from.getWorldCoordinates().z);
            
            motionPath.addWayPoint(creature.getModel().getLocalTranslation());
            motionPath.addWayPoint(takeOffWayPoint);
            
            int numSteps = 100;
            float diffX = (to.getWorldCoordinates().x - from.getWorldCoordinates().x) / (float) numSteps;
            float diffZ = (to.getWorldCoordinates().z - from.getWorldCoordinates().z) / (float) numSteps;
            
            for (int i = 0; i < numSteps; i++)
            {
                float x = from.getWorldCoordinates().x + i * diffX;
                float z = from.getWorldCoordinates().z + i * diffZ;
                //float y = terrain.getHeight(new Vector2f(x, z)) + airCreatureHeight;
                motionPath.addWayPoint(new Vector3f(x, airCreatureHeight, z));
            }
        }
        else
        {
            List<Cell> findPath = findPath(cells, from, to, creature);

            Cell previousCell = null;

            for (Cell cell : findPath)
            {
                if (previousCell != null)
                {
                    // Smoothen path
                    int numParts = 5;
                    float xDiff = (cell.getWorldCoordinates().x - previousCell.getWorldCoordinates().x) / (float) numParts;
                    float zDiff = (cell.getWorldCoordinates().z - previousCell.getWorldCoordinates().z) / (float) numParts;

                    for (int i = 0; i < numParts; i++)
                    {
                        float x = previousCell.getWorldCoordinates().x + i * xDiff;
                        float z = previousCell.getWorldCoordinates().z + i * zDiff;
                        Vector3f part = null;
                        
                        if (creature instanceof SeaCreature)
                        {
                            System.out.println("test");
                            part = new Vector3f(x, 0, z);
                        }
                        else
                        {
                            part = new Vector3f(x, terrain.getHeight(new Vector2f(x, z)) - 100, z);
                        }
                        
                        motionPath.addWayPoint(part);
                    }
                }

                if (creature instanceof SeaCreature)
                {
                    Vector3f old = cell.getWorldCoordinates();
                    motionPath.addWayPoint(new Vector3f(old.x, creature.getModel().getLocalTranslation().y, old.z));
                }
                else
                {
                    motionPath.addWayPoint(cell.getWorldCoordinates());
                }

                previousCell = cell;
            }
        }
        
        motionPath.setCycle(false);
        return motionPath;
    }
    
    private static List<Cell> findPath(Cell[][] cells, Cell from, Cell to, Creature creature) throws NoReachablePathException
    {
        List<Cell> openList = new ArrayList<Cell>();
        List<Cell> closedList = new ArrayList<Cell>();
        Map<Cell, Cell> cameFrom = new HashMap<Cell, Cell>();
        
        Map<Cell, Integer> gScore = new HashMap<Cell, Integer>();
        Map<Cell, Integer> fScore = new HashMap<Cell, Integer>();
        
        /**
         * Initialize start
         */
        openList.add(from);
        gScore.put(from, 0);
        fScore.put(from, gScore.get(from) + computeEstimatedCost(from, to));
        
        Cell currentCell = null;
        
        while (!openList.isEmpty())
        {
            /**
             * Find cell with lowest f score
             */
            
            currentCell = null;
            
            int minFScore = Integer.MAX_VALUE;
            
            for (Cell cell : openList)
            {
                if (fScore.get(cell) < minFScore)
                {
                    currentCell = cell;
                    minFScore = fScore.get(cell);
                }
            }
            
            if (currentCell == null)
            {
                break;
            }
            
            System.out.println("Step: " + currentCell.getXCoor() + " - " + currentCell.getYCoor());
            
            // currentCell is now the cell in the open list with lowest f score
            
            if (currentCell == to)
            {
                return reconstructPath(cameFrom, to);
            }
            
            openList.remove(currentCell);
            closedList.add(currentCell);
            
            for (Cell neighbour : retrieveNeighbouringCells(cells, currentCell, creature))
            {
                if (!closedList.contains(neighbour))
                {
                    int gScoreTentative = gScore.get(currentCell) + computeGScoreIncrease(currentCell, neighbour);
                    
                    if (!openList.contains(neighbour) || gScoreTentative <= gScore.get(neighbour))
                    {
                        cameFrom.put(neighbour, currentCell);
                        gScore.put(neighbour, gScoreTentative);
                        fScore.put(neighbour, gScoreTentative + computeEstimatedCost(neighbour, to));
                        
                        if (!openList.contains(neighbour))
                        {
                            openList.add(neighbour);
                        }
                    }
                }
            }
        }
        
        throw new NoReachablePathException();
    }
    
    private static int computeGScoreIncrease(Cell cell, Cell neighbour)
    {
        int diffX = Math.abs(cell.getXCoor() - neighbour.getXCoor());
        int diffY = Math.abs(cell.getYCoor() - neighbour.getYCoor());
        
        if (diffX > 0 && diffY > 0)
        {
            return 14; // diagonal
        }
        else
        {
            return 10; // orthogonal
        }
    }
    
    private static List<Cell> reconstructPath(Map<Cell, Cell> cameFrom, Cell currentCell)
    {
        if (cameFrom.containsKey(currentCell))
        {
            List<Cell> path = reconstructPath(cameFrom, cameFrom.get(currentCell));
            path.add(currentCell);
            return path;
        }
        else
        {
            List<Cell> path = new ArrayList<Cell>();
            path.add(currentCell);
            return path;
        }
    }
    
    private static int computeEstimatedCost(Cell currentCell, Cell to)
    {
        return 10 * (Math.abs(currentCell.getXCoor() - to.getXCoor())
               + Math.abs(currentCell.getYCoor() - to.getYCoor()));
    }
    
    private static List<Cell> retrieveNeighbouringCells(Cell[][] cells, Cell cell, Creature creature)
    {
        List<Cell> neighbours = new ArrayList<Cell>();
        
        int x = cell.getXCoor();
        int y = cell.getYCoor();
        
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
        
        List<Cell> finalNeighbours = new ArrayList<Cell>();
        
        for (Cell neighbour : neighbours)
        {
            if (neighbour.creatureAllowed(creature) && neighbour.getBase() == null)
            {
                finalNeighbours.add(neighbour);
            }
        }
        
        return finalNeighbours;
    }
}
