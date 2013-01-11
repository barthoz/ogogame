/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.audio.AudioNode;
import com.jme3.audio.LowPassFilter;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.DepthOfFieldFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.util.SkyFactory;
import com.jme3.water.WaterFilter;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.screen.DefaultScreenController;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.exception.ActionNotEnabledException;
import main.game.action.Action;
import main.game.action.SpawnAction;
import main.game.action.creature.MoveAction;
import main.game.action.creature.PickupFoodAction;
import main.game.gui.HudController;
import main.game.gui.SpawnMenuController;
import main.game.model.Base;
import main.game.model.FoodSource;
import main.game.model.cell.Cell;
import main.game.model.cell.DeepWaterCell;
import main.game.model.cell.LandCell;
import main.game.model.cell.RockCell;
import main.game.model.cell.ShallowWaterCell;
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
     * While running
     */
    private Object selectedObject = null;
    /**
     * Setup
     */
    private boolean isRunning = true;
    private ActionListener actionListener = new ActionListener()
    {
        public void onAction(String name, boolean keyPressed, float tpf)
        {
            if (name.equals("Pause") && !keyPressed)
            {
                isRunning = !isRunning;
            }

            /**
             * [RIGHT-CLICK] Select creature, base
             */
            if (!keyPressed && name.equals("RightClick"))
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
                    Spatial selectedSpatial = results.getClosestCollision().getGeometry().getParent();
                    //Geometry selectedGeometry = results.getClosestCollision().getGeometry();
                    String modelType = selectedSpatial.getUserData("modelType");

                    /**
                     * Determine which model has been selected
                     */
                    Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    mat.setColor("Color", ColorRGBA.Orange);

                    if (modelType.equals("Base"))
                    {
                        Base base = (Base) world.findBaseById((Integer) selectedSpatial.getUserData("parentId"));

                        // You can only select your own base
                        if (base.getPlayer().equals(me))
                        {
                            // Show spawn menu
                            nifty.gotoScreen("spawnMenu");
                        }
                    }
                    else if (modelType.equals(LandCreature.CODE_ID))
                    {
                        System.out.println((String) selectedSpatial.getUserData("parentId"));
                        LandCreature creature = (LandCreature) world.findCreatureById((String) selectedSpatial.getUserData("parentId"));
                        selectedObject = creature;
                        creature.getModel().setMaterial(mat);
                    }
                    else if (modelType.equals(SeaCreature.CODE_ID))
                    {
                        SeaCreature creature = (SeaCreature) world.findCreatureById((String) selectedSpatial.getUserData("parentId"));
                        selectedObject = creature;
                        creature.getModel().setMaterial(mat);
                    }
                    else if (modelType.equals(AirborneCreature.CODE_ID))
                    {
                        AirborneCreature creature = (AirborneCreature) world.findCreatureById((String) selectedSpatial.getUserData("parentId"));
                        selectedObject = creature;
                        creature.getModel().setMaterial(mat);
                    }
                    else if (modelType.equals("Duck"))
                    {
                        world.findDuck().quack(quackAudio);
                    }
                }
            }

            /**
             * [LEFT-CLICK while nothing selected]
             */
            if (!keyPressed && name.equals("Select") && selectedObject == null)
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

                    // Determine which model has been selected
                }
            } /**
             * [LEFT-CLICK while something selected] Select terrain, food source
             */
            else if (!keyPressed && name.equals("Select") && selectedObject != null)
            {
                Vector2f click2d = inputManager.getCursorPosition();
                Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
                Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();

                CollisionResults terrainResults = new CollisionResults();
                CollisionResults results = new CollisionResults();
                Ray ray = new Ray(click3d, dir);

                terrain.collideWith(ray, terrainResults);
                world.getSelectableObjects().collideWith(ray, results);
                // Check if something was selected
                if (terrainResults.getClosestCollision() != null)
                {
                    Cell selectedCell = null;

                    // Determine which cell was selected
                    Vector3f contactPoint = terrainResults.getClosestCollision().getContactPoint();

                    if (contactPoint.y < 0)
                    {
                        // Water cell must be selected

                        Vector3f inverseDir = dir.negate();
                        inverseDir = inverseDir.normalize();
                        inverseDir = inverseDir.mult(0.2f);
                        Vector3f result = contactPoint.clone();

                        while (result.y < 0)
                        {
                            result = result.add(inverseDir);
                        }

                        selectedCell = world.getCellFromWorldCoordinates(result);
                    } else
                    {
                        selectedCell = world.getCellFromWorldCoordinates(contactPoint);
                    }

                    if (selectedObject instanceof Creature)
                    {
                        if (results.size() > 0)
                        {
                            if (results.getClosestCollision().getGeometry().getParent().getUserData("modelType").equals("FoodSource") && !(selectedObject instanceof AirborneCreature))
                            {
                                /**
                                 * PickupFoodAction
                                 */
                                PickupFoodAction act = new PickupFoodAction(me, (Creature) selectedObject, world.findFoodSourceById((Integer) results.getClosestCollision().getGeometry().getParent().getUserData("parentId")));
                                me.registerAction(act);
                                
                                /*try
                                {
                                    act.performAction(parent);
                                } catch (ActionNotEnabledException ex)
                                {
                                    Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                                }*/
                            }
                        } else
                        {
                            /**
                             * MoveAction
                             */
                            MoveAction act = new MoveAction(me, (Creature) selectedObject, selectedCell);
                            me.registerAction(act);
                            /*try
                            {
                                act.performAction(parent);
                            } catch (ActionNotEnabledException ex)
                            {
                                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                            }*/

                            // De-select creature
                            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                            mat.setColor("Color", ColorRGBA.White);
                            ((Creature) selectedObject).getModel().setMaterial(mat);
                        }
                    }

                    selectedObject = null;
                }
            }

        }
    };
    private AnalogListener analogListener = new AnalogListener()
    {
        public void onAnalog(String name, float value, float tpf)
        {
            if (isRunning)
            {
                if (name.equals("Rotate"))
                {
                    player.rotate(0, value * speed, 0);
                }
                if (name.equals("Right"))
                {
                    Vector3f v = player.getLocalTranslation();
                    player.setLocalTranslation(v.x + value * speed, v.y, v.z);
                }
                if (name.equals("Left"))
                {
                    Vector3f v = player.getLocalTranslation();
                    player.setLocalTranslation(v.x - value * speed, v.y, v.z);
                }

            } else
            {
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
    public final static int CONST_SET_MODE_TIME_LIMIT = 4;
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
    private List<Player> players;
    /**
     * Terrain
     */
    private Vector3f lightDir = new Vector3f(-4.9236743f, -1.27054665f, 5.896916f);
    private WaterFilter water;
    TerrainQuad terrain;
    Material matRock;
    private BulletAppState bulletAppState;
    private RigidBodyControl landscape;
    AudioNode waves;
    LowPassFilter underWaterAudioFilter = new LowPassFilter(0.5f, 0.1f);
    LowPassFilter underWaterReverbFilter = new LowPassFilter(0.5f, 0.1f);
    LowPassFilter aboveWaterAudioFilter = new LowPassFilter(1, 1);
    //This part is to emulate tides, slightly varrying the height of the water plane
    private float time = 0.0f;
    private float waterHeight = 0.0f;
    private float initialWaterHeight = 0.8f;
    private boolean underWater = false;
    
    /**
     * GUI
     */
    
    NiftyJmeDisplay niftyDisplay;
    Nifty nifty;
    private AudioNode quackAudio;

    /**
     * Networking
     */
    
    public boolean setModeDone = false;
    public boolean setModeSent = false;
    
    /**
     * Constructor
     */
    public Game(Lobby lobby, GameCredentials gameCredentials)
    {
        super();

        this.lobby = lobby;
        this.gameCredentials = gameCredentials;

        this.players = new ArrayList<Player>();
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
        rtsCam.setCenter(new Vector3f(20f, 50f, 20f));

        /**
         * Initialize GUI setup
         */
        this.niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
        this.nifty = niftyDisplay.getNifty();

        nifty.fromXml("Interface/gui.xml", "hud", new HudController(this), new SpawnMenuController(this));
        guiViewPort.addProcessor(niftyDisplay);

        nifty.gotoScreen("hud");

        /**
         * Initialize world
         */
        Node worldNode = new Node("worldNode");
        rootNode.attachChild(worldNode);
        this.world = new World(this, worldNode);

        Box box = new Box(new Vector3f(0, -4, -5), 15, .2f, 15);
        Geometry floor = new Geometry("the Floor", box);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Gray);
        floor.setMaterial(mat1);
        worldNode.attachChild(floor);

        Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        player = new Geometry("blue cube", b);
        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        player.setMaterial(mat);
        //rootNode.attachChild(player);

        /**
         * Initialize world-terrain
         */
        createTerrain(this.world.getWorldNode());
        createGrid();
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(lightDir);
        sun.setColor(ColorRGBA.White.clone().multLocal(1.7f));
        this.world.getWorldNode().addLight(sun);

        Spatial sky = SkyFactory.createSky(assetManager, "Scenes/Beach/FullskiesSunset0068.dds", false);
        sky.setLocalScale(350);

        this.world.getWorldNode().attachChild(sky);
        cam.setFrustumFar(4000);

        water = new WaterFilter(rootNode, lightDir);
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(water);
        BloomFilter bloom = new BloomFilter();
        bloom.setExposurePower(55);
        bloom.setBloomIntensity(1.0f);
        fpp.addFilter(bloom);
        LightScatteringFilter lsf = new LightScatteringFilter(lightDir.mult(-300));
        lsf.setLightDensity(1.0f);
        fpp.addFilter(lsf);
        DepthOfFieldFilter dof = new DepthOfFieldFilter();
        dof.setFocusDistance(0);
        dof.setFocusRange(100);
        fpp.addFilter(dof);

        water.setWaveScale(0.003f);
        water.setMaxAmplitude(2f);
        water.setFoamExistence(new Vector3f(1f, 4, 0.5f));
        water.setFoamTexture((Texture2D) assetManager.loadTexture("Common/MatDefs/Water/Textures/foam2.jpg"));
        water.setRefractionStrength(0.2f);

        water.setWaterHeight(initialWaterHeight);
        underWater = cam.getLocation().y < waterHeight;

        waves = new AudioNode(assetManager, "Sound/Environment/Ocean Waves.ogg", false);
        waves.setLooping(true);
        waves.setReverbEnabled(true);

        if (underWater)
        {
            waves.setDryFilter(new LowPassFilter(0.5f, 0.1f));
        } else
        {
            waves.setDryFilter(aboveWaterAudioFilter);
        }
        audioRenderer.playSource(waves);
        viewPort.addProcessor(fpp);
        water.setFoamTexture((Texture2D) assetManager.loadTexture("Common/MatDefs/Water/Textures/foam3.jpg"));

        /**
         * Initialize me
         */
        if (this.me == null || this.players == null)
        {
            Player me = new Player(this, 0, "TestPlayer");
            this.players.add(me);
            this.me = me;


            Player secondPlayer = new Player(this, 1, "Player2");
            this.players.add(secondPlayer);
        }

        /**
         * Initialize world
         */
        this.world.initializeBases();
        this.world.initializeFoodSources();
        this.world.initializeDuck();

        initKeys();
        initSetMode();

        //(set audio location
        quackAudio = new AudioNode(assetManager, "Sounds/Quack.ogg", false);
        quackAudio.setLooping(false);
        quackAudio.setPositional(true);
        quackAudio.move(this.world.findDuck().getLocation().getWorldCoordinates());
        quackAudio.setVolume(5);
        rootNode.attachChild(quackAudio);
        
        /**
         * Some testing
         */
        //this.world.addCreature(this.me, Creature.TYPE_LAND, this.world.getCells()[32][32]);
        /*SpawnAction spawn = new SpawnAction(this.me, LandCreature.CODE_ID);
        try
        {
            spawn.performAction(this);
        } catch (ActionNotEnabledException ex)
        {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }

    /**
     * Initialize key mappings
     */
    private void initKeys()
    {
        // You can map one or several inputs to one named action
        inputManager.addMapping("Pause", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("Rotate", new KeyTrigger(KeyInput.KEY_SPACE));
        //new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("Select", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("RightClick", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));


        // Add the names to the action listener.
        inputManager.addListener(actionListener, new String[]
                {
                    "Pause", "Select", "RightClick"
                });
        inputManager.addListener(analogListener, new String[]
                {
                    "Left", "Right", "Rotate"
                });
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
         * set your ears at the cam position, so we can hear 3D sounds
         */
        listener.setLocation(cam.getLocation());
        listener.setRotation(cam.getRotation());

        /**
         * Add creature controllers
         */
        for (Creature creature : this.world.getCreatures())
        {
            creature.getController().update(tpf);
        }

        /**
         * Add food source controllers
         */
        
        for (FoodSource foodSource : this.world.getFoodSources())
        {
            foodSource.getController().update(tpf);
        }
        
        /**
         * Add duck controller
         */
        
        this.world.findDuck().getController().update(tpf);
        
        /**
         * Terrain
         */
        time += tpf;
        waterHeight = (float) Math.cos(((time * 0.6f) % FastMath.TWO_PI)) * 1.5f;
        water.setWaterHeight(initialWaterHeight + waterHeight);
        if (water.isUnderWater() && !underWater)
        {

            waves.setDryFilter(new LowPassFilter(0.5f, 0.1f));
            underWater = true;
        }
        if (!water.isUnderWater() && underWater)
        {
            underWater = false;
            waves.setDryFilter(new LowPassFilter(1, 1f));

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
        this.started = true;
        super.start();
    }

    /**
     * End the game.
     */
    public void end()
    {
        super.stop();
        this.started = false;
    }

    private void initSetMode()
    {
        this.setModeDone = false;
        this.setModeSent = false;
        
        System.out.println("Begin SET-mode");

        Timer timer = new Timer();

        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                disableActions();
                setModeDone = true;
                //lobby.getGameConnector().broadcastSetModeDone();
                //Map<Player, List<Action>> actionMap = lobby.getGameConnector().receiveActions();
                /*for (Player player : players)
                 {
                 for (Action action : actionMap.get(player))
                 {
                 player.addAction(action);
                 }
                 }*/

                initGetMode();
            }
        }, CONST_SET_MODE_TIME_LIMIT * 1000);

        enableActions();
    }

    private void initGetMode()
    {
        System.out.println("Begin GET-mode");
        /**
         * Perform all actions
         */
        for (Player player : this.players)
        {
            for (Action action : player.getActions())
            {
                try
                {
                    action.performAction(this);
                } catch (ActionNotEnabledException ex)
                {
                    Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        lobby.getGameConnector().broadcastGetModeDone();
        initSetMode();
    }

    private void disableActions()
    {
    }

    private void enableActions()
    {
    }

    private void nextRound()
    {
    }
    protected Geometry player;

    private void createTerrain(Node rootNode)
    {
        /**
         * Set up Physics
         */
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        /**
         * 1. Create terrain material and load four textures into it.
         */
        Material mat_terrain = new Material(assetManager,
                "Common/MatDefs/Terrain/Terrain.j3md");

        /**
         * 1.1) Add ALPHA map (for red-blue-green coded splat textures)
         */
        mat_terrain.setTexture("Alpha", assetManager.loadTexture(
                "Textures/alphamap.jpg"));

        /**
         * 1.2) Add GRASS texture into the red layer (Tex1).
         */
        Texture grass = assetManager.loadTexture(
                "Textures/Terrain/splat/road.jpg");
        grass.setWrap(Texture.WrapMode.Repeat);
        mat_terrain.setTexture("Tex1", grass);
        mat_terrain.setFloat("Tex1Scale", 64f);

        /**
         * 1.3) Add DIRT texture into the green layer (Tex2)
         */
        Texture dirt = assetManager.loadTexture(
                "Textures/Terrain/splat/grass.jpg");
        dirt.setWrap(Texture.WrapMode.Repeat);
        mat_terrain.setTexture("Tex2", dirt);
        mat_terrain.setFloat("Tex2Scale", 32f);

        /**
         * 1.4) Add ROAD texture into the blue layer (Tex3)
         */
        Texture rock = assetManager.loadTexture(
                "Textures/Terrain/splat/dirt.jpg");
        rock.setWrap(Texture.WrapMode.Repeat);
        mat_terrain.setTexture("Tex3", rock);
        mat_terrain.setFloat("Tex3Scale", 128f);

        /**
         * 2. Create the height map
         */
        AbstractHeightMap heightmap = null;
        Texture heightMapImage = assetManager.loadTexture(
                "Textures/heightmap_raw.jpg");
        heightmap = new ImageBasedHeightMap(heightMapImage.getImage());
        heightmap.load();

        /**
         * 3. We have prepared material and heightmap. Now we create the actual
         * terrain: 3.1) Create a TerrainQuad and name it "my terrain". 3.2) A
         * good value for terrain tiles is 64x64 -- so we supply 64+1=65. 3.3)
         * We prepared a heightmap of size 1024x1024 -- so we supply
         * 1024+1=1025. 3.4) As LOD step scale we supply Vector3f(1,1,1). 3.5)
         * We supply the prepared heightmap itself.
         */
        int patchSize = 65;
        terrain = new TerrainQuad("my terrain", patchSize, 1025, heightmap.getHeightMap());

        /**
         * 4. We give the terrain its material, position & scale it, and attach
         * it.
         */
        terrain.setMaterial(mat_terrain);
        terrain.setLocalTranslation(0, -100, 0);
        terrain.setLocalScale(2f, 1f, 2f);
        rootNode.attachChild(terrain);

        /**
         * 5. The LOD (level of detail) depends on were the camera is:
         */
        TerrainLodControl control = new TerrainLodControl(terrain, getCamera());
        terrain.addControl(control);

        /**
         * 6. Add physics:
         */
        // We set up collision detection for the scene by creating a
        // compound collision shape and a static RigidBodyControl with mass zero.*/
        CollisionShape terrainShape = CollisionShapeFactory.createMeshShape((Node) terrain);
        landscape = new RigidBodyControl(terrainShape, 0);
        terrain.addControl(landscape);

        bulletAppState.getPhysicsSpace().add(terrain);

    }

    private void createGrid()
    {
        float height;
        int cell_size = 32;
        int num_cells = 2048 / cell_size;
        Cell[][] cells = new Cell[num_cells][num_cells];

        Vector3f trans = terrain.getWorldTranslation();
        Vector3f worldCoors;

        for (int i = 0; i < num_cells; i++)
        {
            for (int j = 0; j < num_cells; j++)
            {
                float xCoord = -1024 + i * cell_size + cell_size / 2f;
                float yCoord = -1024 + j * cell_size + cell_size / 2f;
                height = terrain.getHeight(new Vector2f(xCoord, yCoord));
                worldCoors = new Vector3f(xCoord, height, yCoord);
                worldCoors = worldCoors.add(trans);
                //System.out.println(trans);

                Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");

                /*
                 * use constructor for cell in this way:
                 * new Cell(xCoor, yCoor, worldCoors)
                 * xCoor and yCoor are the atributes in the corisponding class
                 * worldCoor should be added for knowing the absolute position
                 * of the cell in our world.
                 */
                if (height <= 90)
                {
                    cells[i][j] = new DeepWaterCell(this.world, i, j, worldCoors);
                    cells[i][j].setWorldCoordinates(new Vector3f(worldCoors.x, 90 - 100, worldCoors.z));
                    mat.setColor("Color", ColorRGBA.Blue);
                } else if (height > 90 && height <= 105)
                {
                    cells[i][j] = new ShallowWaterCell(this.world, i, j, worldCoors);
                    mat.setColor("Color", ColorRGBA.Cyan);
                } else if (height > 105 && height <= 130)
                {
                    cells[i][j] = new LandCell(this.world, i, j, worldCoors);
                    mat.setColor("Color", ColorRGBA.Green);
                } else if (height > 130)
                {
                    cells[i][j] = new RockCell(this.world, i, j, worldCoors);
                    mat.setColor("Color", ColorRGBA.Gray);
                }

                /*Box box = new Box(Vector3f.ZERO, 1, 1, 1);
                 Geometry geometry = new Geometry("box_" + i + "_" + j, box);
               
                 Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                 geometry.setMaterial(mat);*/

                //this.world.getWorldNode().attachChild(geometry);
                //geometry.setLocalTranslation(cells[i][j].getWorldCoordinates());/
            }
        }

        this.world.setCells(cells);
    }

    /**
     * Getters & setters
     */
    public boolean isIsRunning()
    {
        return isRunning;
    }

    public void setIsRunning(boolean isRunning)
    {
        this.isRunning = isRunning;
    }

    public GameCredentials getGameCredentials()
    {
        return gameCredentials;
    }

    public void setGameCredentials(GameCredentials gameCredentials)
    {
        this.gameCredentials = gameCredentials;
    }

    public Lobby getLobby()
    {
        return lobby;
    }

    public void setLobby(Lobby lobby)
    {
        this.lobby = lobby;
    }

    public boolean isStarted()
    {
        return started;
    }

    public void setStarted(boolean started)
    {
        this.started = started;
    }

    public boolean isInSetMode()
    {
        return inSetMode;
    }

    public void setInSetMode(boolean inSetMode)
    {
        this.inSetMode = inSetMode;
    }

    public int getRound()
    {
        return round;
    }

    public void setRound(int round)
    {
        this.round = round;
    }

    public int getRegenTime()
    {
        return regenTime;
    }

    public void setRegenTime(int regenTime)
    {
        this.regenTime = regenTime;
    }

    public World getWorld()
    {
        return world;
    }

    public void setWorld(World world)
    {
        this.world = world;
    }

    public Player getMe()
    {
        return me;
    }

    public void setMe(Player me)
    {
        this.me = me;
    }

    public Geometry getPlayer()
    {
        return player;
    }

    public void setPlayer(Geometry player)
    {
        this.player = player;
    }

    public TerrainQuad getTerrain()
    {
        return terrain;
    }

    public void setTerrain(TerrainQuad terrain)
    {
        this.terrain = terrain;
    }

    public List<Player> getPlayers()
    {
        return players;
    }

    public void setPlayers(List<Player> players)
    {
        this.players = players;
    }
}