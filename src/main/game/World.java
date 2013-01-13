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
    private Node duckContainer;
    /**
     * Properties
     */
    private List<Base> bases;
    private List<FoodSource> foodSources;
    private List<Creature> creatures;
    private Duck duck;
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
        this.duckContainer = new Node("duckContainer");

        this.worldNode.attachChild(this.selectableObjects);
        this.selectableObjects.attachChild(this.duckContainer);
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
        /*List<Cell> spawnCells = new ArrayList<Cell>();

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

            FoodSource foodSource = ModelFactory.createFoodSource(this.game, i, spawnCells.get(foodSourceLocationPos));
            this.foodSources.add(foodSource);
            this.foodSourceContainer.attachChild(foodSource.getModel());
            foodSource.getModel().setLocalTranslation(foodSource.getLocation().getWorldCoordinates());
            spawnCells.remove(foodSourceLocationPos);
        }
        
        for (FoodSource foodSource : this.foodSources)
        {
            System.out.println("this.foodSources.add(ModelFactory.createFoodSource(this.game, " + foodSource.getId() + ", cells[" + foodSource.getLocation().getXCoor() + "][" + foodSource.getLocation().getYCoor() + "]));");
        }*/
        
        this.foodSources.add(ModelFactory.createFoodSource(this.game, 0, cells[52][36]));
        this.foodSources.add(ModelFactory.createFoodSource(this.game, 1, cells[24][0]));
        this.foodSources.add(ModelFactory.createFoodSource(this.game, 2, cells[23][41]));
        this.foodSources.add(ModelFactory.createFoodSource(this.game, 3, cells[23][29]));
        this.foodSources.add(ModelFactory.createFoodSource(this.game, 4, cells[63][41]));
        this.foodSources.add(ModelFactory.createFoodSource(this.game, 5, cells[47][38]));
        this.foodSources.add(ModelFactory.createFoodSource(this.game, 6, cells[22][19]));
        this.foodSources.add(ModelFactory.createFoodSource(this.game, 7, cells[45][37]));
        this.foodSources.add(ModelFactory.createFoodSource(this.game, 8, cells[63][63]));
        this.foodSources.add(ModelFactory.createFoodSource(this.game, 9, cells[48][30]));
        this.foodSources.add(ModelFactory.createFoodSource(this.game, 10, cells[20][22]));
        this.foodSources.add(ModelFactory.createFoodSource(this.game, 11, cells[35][18]));
        this.foodSources.add(ModelFactory.createFoodSource(this.game, 12, cells[56][35]));
        this.foodSources.add(ModelFactory.createFoodSource(this.game, 13, cells[45][1]));
        this.foodSources.add(ModelFactory.createFoodSource(this.game, 14, cells[60][47]));
        this.foodSources.add(ModelFactory.createFoodSource(this.game, 15, cells[51][26]));
        this.foodSources.add(ModelFactory.createFoodSource(this.game, 16, cells[25][1]));
        this.foodSources.add(ModelFactory.createFoodSource(this.game, 17, cells[61][25]));
        this.foodSources.add(ModelFactory.createFoodSource(this.game, 18, cells[23][23]));
        this.foodSources.add(ModelFactory.createFoodSource(this.game, 19, cells[55][3]));
        this.foodSources.add(ModelFactory.createFoodSource(this.game, 20, cells[58][51]));
        this.foodSources.add(ModelFactory.createFoodSource(this.game, 21, cells[33][52]));
        this.foodSources.add(ModelFactory.createFoodSource(this.game, 22, cells[3][44]));
        this.foodSources.add(ModelFactory.createFoodSource(this.game, 23, cells[42][6]));
        this.foodSources.add(ModelFactory.createFoodSource(this.game, 24, cells[1][22]));
        this.foodSources.add(ModelFactory.createFoodSource(this.game, 25, cells[13][42]));
        this.foodSources.add(ModelFactory.createFoodSource(this.game, 26, cells[36][0]));
        this.foodSources.add(ModelFactory.createFoodSource(this.game, 27, cells[33][35]));
        this.foodSources.add(ModelFactory.createFoodSource(this.game, 28, cells[7][49]));
        this.foodSources.add(ModelFactory.createFoodSource(this.game, 29, cells[20][55]));
    }

    /**
     * Initialize all food sources.
     */
    public void initializeDuck()
    {
        Cell duckLocation;
        this.cells[40][40] = new RockCell(this, 27, 34, this.cells[40][40].getWorldCoordinates());

        duckLocation = this.cells[40][40];
        this.duck = ModelFactory.createDuck(this.game.getAssetManager(), this.game, duckLocation);

        this.duckContainer.attachChild(duck.getModel());
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

    public Duck findDuck()
    {
        return duck;
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

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Node getCreatureContainer() {
        return creatureContainer;
    }

    public void setCreatureContainer(Node creatureContainer) {
        this.creatureContainer = creatureContainer;
    }

    public Node getBaseContainer() {
        return baseContainer;
    }

    public void setBaseContainer(Node baseContainer) {
        this.baseContainer = baseContainer;
    }

    public Node getFoodSourceContainer() {
        return foodSourceContainer;
    }

    public void setFoodSourceContainer(Node foodSourceContainer) {
        this.foodSourceContainer = foodSourceContainer;
    }

    public Node getDuckContainer() {
        return duckContainer;
    }

    public void setDuckContainer(Node duckContainer) {
        this.duckContainer = duckContainer;
    }

    public Duck getDuck() {
        return duck;
    }

    public void setDuck(Duck duck) {
        this.duck = duck;
    }
}
