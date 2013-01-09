/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.model;

import main.game.Player;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import main.game.Game;
import main.game.model.cell.Cell;
import main.game.model.creature.*;

/**
 *
 * @author Daniel
 */
public class ModelFactory
{
    /**
     * Business logic
     */
    
    public static Duck createDuck(AssetManager assetManager, Game game, Cell location)
    {
        // First create the spatial
        Spatial duckModel = assetManager.loadModel("Models/Land/stilstaand.mesh.xml");
        
        duckModel.setLocalScale(0.1f);
        
        duckModel.setUserData("modelType", "Duck");
       // duckModel.setUserData("parentId", id);
        
        Duck duck = new Duck(duckModel, game, location);
        
        return duck;
    }
    
    public static Base createBase(AssetManager assetManager, int id, Player player, Cell location)
    {
        // First create the spatial
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        
        Spatial baseModel = assetManager.loadModel("Models/Land/stilstaand.mesh.xml");
        //baseModel.setLocalScale(0.2f);
        baseModel.setUserData("modelType", "Base");
        baseModel.setUserData("parentId", id);
        //Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        baseModel.setMaterial(mat);
        
        // Create the base object
        Base base = new Base(id, baseModel, player, location);
        
        return base;
    }
    
    public static FoodSource createFoodSource(AssetManager assetManager, int id, Game game, Cell location)
    {
        // First create the spatial
        Spatial fsModel = assetManager.loadModel("Models/Land/stilstaand.mesh.xml");
        //creatureModel.setLocalScale(0.2f);
        
        fsModel.setUserData("modelType", "FoodSource");
        fsModel.setUserData("parentId", id);
        
        FoodSource foodSource = new FoodSource(id, fsModel, game, location);
        
        return foodSource;
    }
    
    public static Creature createCreature(AssetManager assetManager, String id, Player player, String creatureType)
    {
        Spatial creatureModel = null;
        Creature creature = null;
        
        //Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        
        if (creatureType.equals(LandCreature.CODE_ID))
        {
            creatureModel = assetManager.loadModel("Models/Land/stilstaand.mesh.xml");
            //creatureModel.setLocalScale(0.2f);
            creature = new LandCreature(player, id, creatureModel);
        }
        else if (creatureType.equals(SeaCreature.CODE_ID))
        {
            creatureModel = assetManager.loadModel("Models/Sea/move.mesh.xml");
            creatureModel.setLocalScale(4f);
            creature = new SeaCreature(player, id, creatureModel);
        }
        else if (creatureType.equals(AirborneCreature.CODE_ID))
        {
            creatureModel = assetManager.loadModel("Models/Air/stilstaand.mesh.xml");
            creature = new AirborneCreature(player, id, creatureModel);
        }
        
        creatureModel.setUserData("parentId", id);
        creatureModel.setUserData("modelType", creatureType);
        
        // Determine spawn location
        //Cell location = player.getBase().getLocation().getWorld().getCells()[32][32];
        Cell location = player.getBase().getClosestSpawnableCell(creature);
        
        creature.setLocation(location);
        creatureModel.setLocalTranslation(location.getWorldCoordinates());
        
        return creature;
    }
}
