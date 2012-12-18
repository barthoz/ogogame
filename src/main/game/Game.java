/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import main.game.model.Player;
import main.game.model.creature.Creature;
import main.lobby.Lobby;

/**
 *
 * @author Daniel
 */
public class Game extends SimpleApplication
{
    /**
     * Setup
     */
    
    private boolean isRunning = true;
    
    private ActionListener actionListener = new ActionListener() {
    public void onAction(String name, boolean keyPressed, float tpf) {
      if (name.equals("Pause") && !keyPressed)
      {
        isRunning = !isRunning;
      }
      if (name.equals("Select"))
      {
          CollisionResults results = new CollisionResults();
          Ray ray = new Ray(cam.getLocation(), cam.getDirection());
          world.getSelectableObjects().collideWith(ray, results);
          
          System.out.println("----- Collisions? " + results.size() + "-----");
          for (int i = 0; i < results.size(); i++)
          {
            float dist = results.getCollision(i).getDistance();
            Vector3f pt = results.getCollision(i).getContactPoint();
            String hit = results.getCollision(i).getGeometry().getName();
            System.out.println("* Collision #" + i);
            System.out.println("  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
            System.out.println(" Creature id: " + world.findCreatureById((Integer) results.getCollision(i).getGeometry().getUserData("parentId")).getId());
          }
      }
    }
  };
 
  private AnalogListener analogListener = new AnalogListener() {
    public void onAnalog(String name, float value, float tpf) {
      if (isRunning)
      {
        if (name.equals("Rotate")) {
          player.rotate(0, value*speed, 0);
        }
        if (name.equals("Right")) {
          Vector3f v = player.getLocalTranslation();
          player.setLocalTranslation(v.x + value*speed, v.y, v.z);
        }
        if (name.equals("Left")) {
          Vector3f v = player.getLocalTranslation();
          player.setLocalTranslation(v.x - value*speed, v.y, v.z);
        }
      } else {
        System.out.println("Press P to unpause.");
      }
    }
  };
    
    /**
     * Game identification
     */
    
    private GameCredentials gameCredentials;
    private Lobby lobby;
    
    /**
     * Game constants
     */
    
    public final static int CONST_CREATURES_LIMIT = 10;
    public final static int CONST_SET_MODE_TIME_LIMIT = 60;
    public final static int CONST_INIT_RANGE_OF_SIGHT = 10;
    public final static int CONST_INIT_START_FOOD = 10;
    
    /**
     * Game state variables
     */
    
    private boolean started;
    private boolean inSetMode;
    private int round = 0;
    private int regenTime = 10;
    
    /**
     * Properties
     */
    
    private World world;
    private Player me;
    
    /**
     * Constructor
     */
    
    public Game(Lobby lobby, GameCredentials gameCredentials)
    {
        super();
        
        this.lobby = lobby;
        this.gameCredentials = gameCredentials;
        
        this.me = new Player(this, 0, "Daniel");
    }
    
    /**
     * Business logic
     */
    
    /**
     * Initialize the game.
     */
    @Override
    public void simpleInitApp()
    {
        Node worldNode = new Node("worldNode");
        rootNode.attachChild(worldNode);
        this.world = new World(this, worldNode);
        
        this.world.addCreature(this.me, Creature.TYPE_LAND);
        
        /*Spatial tree = assetManager.loadModel("Models/Tree.j3o");
        Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        tree.setMaterial(mat2);
        //rootNode.attachChild(tree);*/
        
        Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        player = new Geometry("blue cube", b);
        Material mat = new Material(assetManager,
          "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        player.setMaterial(mat);
        rootNode.attachChild(player);
        
        initKeys();
    }

    /**
     * Initialize key mappings
     */
    private void initKeys()
    {
        // You can map one or several inputs to one named action
        inputManager.addMapping("Pause",  new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("Left",   new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("Right",  new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("Rotate", new KeyTrigger(KeyInput.KEY_SPACE));
                                          //new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("Select", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        
        
        // Add the names to the action listener.
        inputManager.addListener(actionListener, new String[]{"Pause", "Select"});
        inputManager.addListener(analogListener, new String[]{"Left", "Right", "Rotate"});
    }
    
    /**
     * Perform the update loop.
     * 
     * @param tpf 
     */
    @Override
    public void simpleUpdate(float tpf)
    {
        /**
         * Add creature controllers
         */
        
        for (Creature creature : this.world.getCreatures())
        {
            creature.getController().update(tpf);
        }
    }

    @Override
    public void simpleRender(RenderManager rm)
    {
        //TODO: add render code
    }
    
    /**
     * Start the game.
     */
    @Override
    public void start()
    {
        super.start();
        this.started = true;
    }
    
    /**
     * End the game.
     */
    public void end()
    {
        super.stop();
        this.started = false;        
    }
    
    private void toGetMode()
    {
        
    }
    
    private void nextRound()
    {
        
    }
    
    protected Geometry player;
}
