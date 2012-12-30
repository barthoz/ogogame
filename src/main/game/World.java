/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game;

import main.game.model.cell.Cell;
import com.jme3.material.Material;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.List;
import main.game.model.*;
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
    private List<Player> players;
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
        this.players = new ArrayList<Player>();
        this.bases = new ArrayList<Base>();
        this.foodSources = new ArrayList<FoodSource>();
        this.creatures = new ArrayList<Creature>();
    }

    /**
     * Business logic
     */
    /**
     * Add a creature to the world and player.
     *
     * @Pre 0 <= type <= 2 && player != null
     * @param player
     */
    public void addCreature(Player player, int type)
    {
        Spatial creatureModel = null;
        Creature creature = null;

        switch (type)
        {
            case Creature.TYPE_LAND:
                creatureModel = game.getAssetManager().loadModel("Models/Tree.j3o");
                creatureModel.setUserData("modelType", "CreatureLand");
                creature = new LandCreature(player, new String(retrieveAllocatedId() + ""), creatureModel);
                break;
            case Creature.TYPE_SEA:
                creatureModel = game.getAssetManager().loadModel("Models/Tree.j3o");
                creatureModel.setUserData("modelType", "CreatureSea");
                creature = new SeaCreature(player, new String(retrieveAllocatedId() + ""), creatureModel);
                break;
            case Creature.TYPE_AIR:
                creatureModel = game.getAssetManager().loadModel("Models/Tree.j3o");
                creatureModel.setUserData("modelType", "CreatureAirborne");
                creature = new AirborneCreature(player, new String(retrieveAllocatedId() + ""), creatureModel);
                break;
        }

        creatureModel.setUserData("parentId", creature.getId());
        Material mat = new Material(game.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        creatureModel.setMaterial(mat);
        this.creatureContainer.attachChild(creatureModel);
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

    public List<Player> getPlayers()
    {
        return players;
    }

    public void setPlayers(List<Player> players)
    {
        this.players = players;
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
