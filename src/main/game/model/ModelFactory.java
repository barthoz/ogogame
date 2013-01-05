/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.model;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;
import main.game.model.cell.Cell;
import main.game.model.creature.LandCreature;

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
        Spatial baseModel = assetManager.loadModel("Models/Tree.j3o");
        baseModel.setUserData("modelType", "Base");
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        baseModel.setMaterial(mat);
        
        // Create the base object
        Base base = new Base(id, baseModel, player, location);
        
        return base;
    }
}
