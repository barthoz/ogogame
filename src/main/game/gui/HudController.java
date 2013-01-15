/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.gui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import main.game.Game;
import main.game.action.creature.LandAction;
import main.game.model.creature.AirborneCreature;
import main.game.model.creature.Creature;
import main.game.model.creature.LandCreature;
import main.game.model.creature.SeaCreature;

/**
 *
 * @author s116861
 */
public class HudController  extends AbstractAppState implements ScreenController {
 
  private Element numLand;
  private Element numSea;
  private Element numAir;
  private Element numFood;
  private Element setMode;
  private String landText;
  private String seaText;
  private String airText;
  private String foodText;
  private String modeText;
  private Nifty nifty;
  private Screen screen;
  private SimpleApplication app;
  private Game game;
 
  /** custom methods */ 
 
  public HudController(Game game)
  {
      this.game = game;
  }
  
  public HudController(String data) { 
    /** Your custom constructor, can accept arguments */ 
  } 
 
  /** Nifty GUI ScreenControl methods */ 
 
  public void bind(Nifty nifty, Screen screen) {
    this.nifty = nifty;
    this.screen = screen;
  }
 
  public void onStartScreen() { }
 
  public void onEndScreen() { }
  
  public void eat()
  {
      this.nifty.gotoScreen("feedMenu");
  }
  
  public void land()
  {
      LandAction act = new LandAction(this.game.getMe(), (AirborneCreature) this.game.getSelectedObject());
      this.game.getMe().registerAction(act);
  }
  
  public void leave()
  {
      this.game.setLeaveGame(true);
  }
  
  /** jME3 AppState methods */ 
  
  public void init()
  {
    numLand = nifty.getScreen("hud").findElementByName("num_land_text");
    numSea = nifty.getScreen("hud").findElementByName("num_sea_text");
    numAir = nifty.getScreen("hud").findElementByName("num_air_text");
    numFood = nifty.getScreen("hud").findElementByName("num_food_text");
    setMode = nifty.getScreen("hud").findElementByName("set_mode_text");
  }
  
  @Override
  public void initialize(AppStateManager stateManager, Application app) {
    super.initialize(stateManager, app);
    this.app=(SimpleApplication)app;
  }
 
  @Override
  public void update(float tpf) { 
      modeText = "Mode: " + (!game.isInSetMode() ? "Get (" + (10 - (int) (game.getCountGetMode() / 1000f)) + "s left)" : "Set, time left: " + (game.CONST_SET_MODE_TIME_LIMIT - (int) (game.getCountSetMode() / 1000f)) + "s");
     
        
        // Count alive creatures
        int land = 0;
        int sea = 0;
        int air = 0;
        for (Creature creature : game.getMe().getCreatures())
        {
            if (creature.isIsAlive())
            {
                if(creature instanceof LandCreature){
                    land++;
                }
                else if(creature instanceof SeaCreature){
                    sea++;
                }
                else if(creature instanceof AirborneCreature){
                    air++;
                }
            }
        }
        landText = "              x" + land;
        seaText = "              x" + sea;
        airText = "              x" + air;
        foodText = "              x" + game.getMe().getFood();
        
      numLand.getRenderer(TextRenderer.class).setText(landText);
      numSea.getRenderer(TextRenderer.class).setText(seaText);
      numAir.getRenderer(TextRenderer.class).setText(airText);
      numFood.getRenderer(TextRenderer.class).setText(foodText);
      setMode.getRenderer(TextRenderer.class).setText(modeText);
  }
 
  public void showCreatureMenu(){
      nifty.gotoScreen("creatureMenu");
  }
}
