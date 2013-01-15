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
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import main.game.Game;
import main.game.model.creature.Creature;

/**
 *
 * @author s116861
 */
public class HudController  extends AbstractAppState implements ScreenController {
 
  
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
  
  /** jME3 AppState methods */ 
 
  @Override
  public void initialize(AppStateManager stateManager, Application app) {
    super.initialize(stateManager, app);
    this.app=(SimpleApplication)app;
    
  }
 
  @Override
  public void update(float tpf) { 
      /*modeInfo.setText("Mode: " + (!this.inSetMode ? "Get (" + (10 - (int) (this.countGetMode / 1000f)) + "s left)" : "Set, time left: " + (CONST_SET_MODE_TIME_LIMIT - (int) (this.countSetMode / 1000f)) + "s"));
        roundInfo.setText("Round number: " + this.round);
        
        // Count alive creatures
        int alive = 0;
        for (Creature creature : me.getCreatures())
        {
            if (creature.isIsAlive())
            {
                alive++;
            }
        }
        
        playerInfo.setText("Creatures: " + alive + " / Food: " + me.getFood());*/
  }
 
  public void showCreatureMenu(){
      nifty.gotoScreen("creatureMenu");
  }
}
