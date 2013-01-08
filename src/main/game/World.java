/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game;

import main.game.model.cell.Cell;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;
import java.util.List;
import main.game.model.*;
import main.game.model.cell.DeepWaterCell;
import main.game.model.cell.LandCell;
import main.game.model.cell.RockCell;
import main.game.model.creature.*;

/**
 *
 * @author Daniel
 */
public class World
{

    private Game game;
    /**
     * World objects
     */
    private Node worldNode;
    private Node selectableObjects;
    private Node creatureContainer;
    private Node baseContainer;
    private Node foodSourceContainer;
    /**
     * Properties
     */
    
    private List<Base> bases;
    private List<FoodSource> foodSources;
    private List<Creature> creatures;
    private Cell[][] cells;
    
    /**
     * Bookkeeping
     */
    private int allocatedId;

    /**
     * Constructor
     */
    
    public World(Game game, Node worldNode)
    {
        this.game = game;
        this.allocatedId = 0;

        /**
         * Initialize world in openGL
         */
        this.worldNode = worldNode;
        this.selectableObjects = new Node("selectableObjects");
        this.creatureContainer = new Node("creatureContainer");
        this.baseContainer = new Node("baseContainer");
        this.foodSourceContainer = new Node("foodSourceContainer");

        this.worldNode.attachChild(this.selectableObjects);
        this.selectableObjects.attachChild(this.creatureContainer);
        this.selectableObjects.attachChild(this.baseContainer);
        this.selectableObjects.attachChild(this.foodSourceContainer);

        /**
         * Initialize object lists
         */
        
        this.bases = new ArrayList<Base>();
        this.foodSources = new ArrayList<FoodSource>();
        this.creatures = new ArrayList<Creature>();
    }

    /**
     * Business logic
     */
    
    /**
     * Initialize all bases in the game. Register them with the current players.
     * 
     * @Pre this.players.size <= 6 && for all players: 0 <= player.getId() <= 5
     */
    public void initializeBases()
    {
        Cell[] baseLocations = new Cell[6];
        this.cells[27][34] = new RockCell(this, 27, 34, this.cells[27][34].getWorldCoordinates());
        this.cells[27][13] = new RockCell(this, 27, 13, this.cells[27][13].getWorldCoordinates());
        this.cells[45][16] = new RockCell(this, 45, 16, this.cells[45][16].getWorldCoordinates());
        this.cells[58][34] = new RockCell(this, 58, 34, this.cells[58][34].getWorldCoordinates());
        this.cells[40][51] = new RockCell(this, 40, 51, this.cells[40][51].getWorldCoordinates());
        this.cells[10][46] = new RockCell(this, 10, 46, this.cells[10][46].getWorldCoordinates());
        
        baseLocations[0] = this.cells[27][34];
        baseLocations[1] = this.cells[27][13];
        baseLocations[2] = this.cells[45][16];
        baseLocations[3] = this.cells[58][34];
        baseLocations[4] = this.cells[40][51];
        baseLocations[5] = this.cells[10][46];
        
        for (Player player : this.game.getPlayers())
        {
            Base base = ModelFactory.createBase(this.game.getAssetManager(), player.getId(), player, baseLocations[player.getId()]);
            player.setBase(base);
            this.bases.add(base);
            this.baseContainer.attachChild(base.getModel());
        }
    }
    
    /**
     * Initialize all food sources.
     */
    public void initializeFoodSources()
    {
        List<Cell> spawnCells = new ArrayList<Cell>();
        
        for (int i = 0; i < this.cells.length; i++)
        {
            for (int j = 0; j < this.cells[0].length; j++)
            {
                if (!(cells[i][j] instanceof RockCell))
                {
                    spawnCells.add(cells[i][j]);
                }
            }
        }
        
        int numFoodSources = 30;
        
        for (int i = 0; i < numFoodSources; i++)
        {
            int foodSourceLocationPos = (int) Math.round(Math.random() * (spawnCells.size() - 1));
            
            FoodSource foodSource = ModelFactory.createFoodSource(this.game.getAssetManager(), i, this.game, spawnCells.get(foodSourceLocationPos));
            this.foodSources.add(foodSource);
            this.foodSourceContainer.attachChild(foodSource.getModel());
            foodSource.getModel().setLocalTranslation(foodSource.getLocation().getWorldCoordinates());
            spawnCells.remove(foodSourceLocationPos);
        }
    }
    
    /**
     * Add a creature to the world and player.
     *
     * @Pre 0 <= type <= 2 && player != null
     * @param player
     */
    public void addCreature(Creature creature)
    {
        this.creatures.add(creature);
    }

    /**
     * Remove creature from the world and player.
     *
     * @param creature
     */
    public void removeCreature(Creature creature)
    {
        Spatial foundSpatial = null;

        // Find spatial that belongs to this creature
        for (Spatial spatial : this.creatureContainer.getChildren())
        {
            String id = spatial.getUserData("parentId");

            if (creature.getId().equals(id))
            {
                foundSpatial = spatial;
                break;
            }
        }

        this.creatureContainer.detachChild(foundSpatial);
        this.creatures.remove(creature);
        creature.getPlayer().removeCreature(creature);
    }

    /**
     * Find creature by id.
     *
     * @Pre creature exists with this id
     * @param id to look for
     * @return creature with creature.getId() == id
     */
    public Creature findCreatureById(String id)
    {
        for (Creature creature : this.creatures)
        {
            if (creature.getId().equals(id))
            {
                return creature;
            }
        }

        return null;
    }

    /**
     * Find base by id.
     */
    public Base findBaseById(int id)
    {
        for (Base base : this.bases)
        {
            if (base.getId() == id)
            {
                return base;
            }
        }
        
        return null;
    }
    
    /**
     * Find food source by id.
     */
    public FoodSource findFoodSourceById(int id)
    {
        for (FoodSource foodSource : this.foodSources)
        {
            if (foodSource.getId() == id)
            {
                return foodSource;
            }
        }
        
        return null;
    }
    
    /**
     * Retrieve an unique id.
     *
     * @return integer which is guaranteed to be unique after calling this
     * function
     */
    private int retrieveAllocatedId()
    {
        this.allocatedId++;
        return this.allocatedId - 1;
    }

    public Cell getCellFromWorldCoordinates(Vector3f contactPoint)
    {
        int cell_size = 32;
        
        int i = (int) Math.round((contactPoint.x + 1024) / (float) cell_size - 0.5);
        int j = (int) Math.round((contactPoint.z + 1024) / (float) cell_size - 0.5);
        
        return this.cells[i][j];
    }
    
    /**
     * Getters & Setters
     */
    public Node getSelectableObjects()
    {
        return selectableObjects;
    }

    public void setSelectableObjects(Node selectableObjects)
    {
        this.selectableObjects = selectableObjects;
    }

    public List<Base> getBases()
    {
        return bases;
    }

    public void setBases(List<Base> bases)
    {
        this.bases = bases;
    }

    public List<FoodSource> getFoodSources()
    {
        return foodSources;
    }

    public void setFoodSources(List<FoodSource> foodSources)
    {
        this.foodSources = foodSources;
    }

    public List<Creature> getCreatures()
    {
        return creatures;
    }

    public void setCreatures(List<Creature> creatures)
    {
        this.creatures = creatures;
    }

    public Cell[][] getCells()
    {
        return cells;
    }

    public void setCells(Cell[][] cells)
    {
        this.cells = cells;
    }

    public Node getWorldNode()
    {
        return worldNode;
    }
}
