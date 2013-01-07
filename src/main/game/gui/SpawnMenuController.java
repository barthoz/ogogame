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
import java.util.logging.Level;
import java.util.logging.Logger;
import main.exception.ActionNotEnabledException;
import main.game.Game;
import main.game.action.SpawnAction;

/**
 *
 * @author s116861
 */
public class SpawnMenuController  extends AbstractAppState implements ScreenController {
 
  private Nifty nifty;
  private Screen screen;
  private SimpleApplication app;
  private Game game;
 
  /** custom methods */
  public void spawnCreature(String creatureType)
  {
      SpawnAction action = new SpawnAction(this.game.getMe(), creatureType);
        try {
            action.performAction(this.game);
        } catch (ActionNotEnabledException ex) {
            Logger.getLogger(SpawnMenuController.class.getName()).log(Level.SEVERE, null, ex);
        }
      System.out.println("spawned: " + creatureType);
  }
  
  public void cancel()
  {
      System.out.println("cancelled");
      nifty.gotoScreen("hud");
  }
 
  public SpawnMenuController(Game game)
  {
      this.game = game;
  }
  
  public SpawnMenuController(String data) { 
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
    /** jME update loop! */ 
  }
 
}
