/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.setup;

import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 *
 * @author s116861
 */
public class GameCamera extends Camera
{
    /**
     * Properties
     */
    
    private int speed = 5;
    
    private AnalogListener analogListener = new AnalogListener()
    {
        public void onAnalog(String name, float value, float tpf)
        {
          if (name.equals("Forward"))
          {
              Vector3f v = getLocation();
              //Vector3f newLocation = new Vector3f(v.x + 10, v.y, v.z);
              //setLocation(newLocation);
              setLocation(v);
          }
          if (name.equals("Backward"))
          {
              
          }
          if (name.equals("StrafeLeft"))
          {
              
          }
          if (name.equals("StrafeRight"))
          {
              
          }
        }
  };
    
    /**
     * Constructors
     */
    public GameCamera() { }
    
    public void initializeCamera(InputManager inputManager)
    {
        /**
         * Initialize keys
         */
        
        inputManager.addMapping("Forward",  new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("Backward",   new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("StrafeLeft",  new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("StrafeRight", new KeyTrigger(KeyInput.KEY_RIGHT));
        
        // Add the names to the action listener.
        inputManager.addListener(analogListener, new String[]{"Forward", "Backward", "StrafeLeft", "StrafeRight"});
    
        /**
         * Initialize camera location
         */
        System.out.println(this == null);
        Vector3f initialLocation = new Vector3f(0f, 0f, 5f);
        setLocation(initialLocation);
    }
}
