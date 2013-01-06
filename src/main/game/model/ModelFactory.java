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
    
    public static Base createBase(AssetManager assetManager, int id, Player player, Cell location)
    {
        // First create the spatial
        Box box = new Box(Vector3f.ZERO, 32, 32, 32);
        Geometry baseModel = new Geometry("base_" + id, box);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        
        
        //Spatial baseModel = assetManager.loadModel("Models/Tree.j3o");
        baseModel.setUserData("modelType", "Base");
        baseModel.setUserData("parentId", id);
        //Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        baseModel.setMaterial(mat);
        
        // Create the base object
        Base base = new Base(id, baseModel, player, location);
        
        return base;
    }
    
    public static Creature createCreature(AssetManager assetManager, String id, Player player, String creatureType)
    {
        Spatial creatureModel = null;
        Creature creature = null;
        
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        
        if (creatureType.equals(LandCreature.CODE_ID))
        {
            creatureModel = assetManager.loadModel("Models/Tree.j3o");
            mat.setColor("Color", ColorRGBA.Brown);
            creature = new LandCreature(player, id, creatureModel);
        }
        else if (creatureType.equals(SeaCreature.CODE_ID))
        {
            creatureModel = assetManager.loadModel("Models/Tree.j3o");
            mat.setColor("Color", ColorRGBA.Cyan);
            creature = new SeaCreature(player, id, creatureModel);
        }
        else if (creatureType.equals(AirborneCreature.CODE_ID))
        {
            creatureModel = assetManager.loadModel("Models/Tree.j3o");
            mat.setColor("Color", ColorRGBA.Pink);
            creature = new AirborneCreature(player, id, creatureModel);
        }
        
        creatureModel.setUserData("parentId", id);
        creatureModel.setUserData("modelType", creatureType);
        
        // Determine spawn location
        Cell location = player.getBase().getClosestSpawnableCell(creature);
        
        creature.setLocation(location);
        creatureModel.setLocalTranslation(location.getWorldCoordinates());
        
        return creature;
    }
}
