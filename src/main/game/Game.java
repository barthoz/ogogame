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
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.exception.ActionNotEnabledException;
import main.game.action.MoveAction;
import main.game.model.Player;
import main.game.model.creature.AirborneCreature;
import main.game.model.creature.Creature;
import main.game.model.creature.LandCreature;
import main.game.model.creature.SeaCreature;
import main.game.setup.RtsCam;
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
          Vector2f click2d = inputManager.getCursorPosition();
          Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
          Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();

          CollisionResults results = new CollisionResults();
          Ray ray = new Ray(click3d, dir);
          world.getSelectableObjects().collideWith(ray, results);
          
          // Check if something was selected
          if (results.getClosestCollision() != null)
          {
            Geometry selectedGeometry = results.getClosestCollision().getGeometry();
            String modelType = selectedGeometry.getUserData("modelType");

            /**
             * Determine which model has been selected
             */

            if (modelType.equals("CreatureLand"))
            {
                LandCreature creature = (LandCreature) world.findCreatureById((String) selectedGeometry.getUserData("parentId"));
                MoveAction act = new MoveAction(creature);
                  try {
                      act.performAction(parent);
                  } catch (ActionNotEnabledException ex) {
                      Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                  }
            }
            else if (modelType.equals("CreatureSea"))
            {
                SeaCreature creature = (SeaCreature) world.findCreatureById((String) selectedGeometry.getUserData("parentId"));
            }
            else if (modelType.equals("CreatureAirborne"))
            {
                AirborneCreature creature = (AirborneCreature) world.findCreatureById((String) selectedGeometry.getUserData("parentId"));
            }
            else if (modelType.equals("FoodSource"))
            {

            }
            else if (modelType.equals("Base"))
            {

            }
            else if (modelType.equals("Duck"))
            {

            }
          }
          
          
          
          //System.out.println("----- Collisions? " + results.size() + "-----");
          for (int i = 0; i < results.size(); i++)
          {
              //String modelId = (String) results.getCollision(i).getGeometry().getUserData("parentId");
              
              
            float dist = results.getCollision(i).getDistance();
            Vector3f pt = results.getCollision(i).getContactPoint();
            String hit = results.getCollision(i).getGeometry().getName();
            //System.out.println("* Collision #" + i);
            //System.out.println("  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
            System.out.println(" Creature id: " + world.findCreatureById((String) results.getCollision(i).getGeometry().getUserData("parentId")).getId());
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
    
    private Game parent = this;
  
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
        /**
         * Initialize mouse
         */
        
        inputManager.setCursorVisible(true);
        
        /**
         * Initialize camera
         */
        
        flyCam.setEnabled(false);
        final RtsCam rtsCam = new RtsCam(cam, rootNode);
        rtsCam.registerWithInput(inputManager);
        rtsCam.setCenter(new Vector3f(20,0.5f,20));
        
        /**
         * Initialize world
         */
        
        Node worldNode = new Node("worldNode");
        rootNode.attachChild(worldNode);
        this.world = new World(this, worldNode);
        
        this.world.addCreature(this.me, Creature.TYPE_LAND);
        
        Box box = new Box(new Vector3f(0, -4, -5), 15, .2f, 15);
        Geometry floor = new Geometry("the Floor", box);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Gray);
        floor.setMaterial(mat1);
        worldNode.attachChild(floor);
        
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
    
    /**
     * Getters & setters
     */
    
    public boolean isIsRunning() {
        return isRunning;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public GameCredentials getGameCredentials() {
        return gameCredentials;
    }

    public void setGameCredentials(GameCredentials gameCredentials) {
        this.gameCredentials = gameCredentials;
    }

    public Lobby getLobby() {
        return lobby;
    }

    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isInSetMode() {
        return inSetMode;
    }

    public void setInSetMode(boolean inSetMode) {
        this.inSetMode = inSetMode;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getRegenTime() {
        return regenTime;
    }

    public void setRegenTime(int regenTime) {
        this.regenTime = regenTime;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public Player getMe() {
        return me;
    }

    public void setMe(Player me) {
        this.me = me;
    }

    public Geometry getPlayer() {
        return player;
    }

    public void setPlayer(Geometry player) {
        this.player = player;
    }
}
