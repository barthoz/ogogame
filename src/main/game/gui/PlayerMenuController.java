/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game.gui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.thoughtworks.xstream.XStream;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.controls.RadioButton;
import de.lessvoid.nifty.effects.EffectEventId;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.PanelRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.exception.ActionNotEnabledException;
import main.game.Game;
import main.game.Player;
import main.game.action.SpawnAction;
import main.game.model.creature.AirborneCreature;
import main.game.model.creature.Creature;
import main.game.model.creature.LandCreature;
import main.game.model.creature.SeaCreature;

/**
 *
 * @author s116861
 */
public class PlayerMenuController extends AbstractAppState implements ScreenController
{

    private Nifty nifty;
    private Screen screen;
    private SimpleApplication app;
    private Game game;
    Element[] panel;
    Element[] colorPanel;
    Element[] nameLabel;
    Element[] creatureLabel;

    /**
     * custom methods
     */
    public void cancel()
    {
        System.out.println("cancelled");
        nifty.gotoScreen("hud");
    }

    public PlayerMenuController(Game game)
    {
        this.game = game;
        this.nifty = game.getNifty();

        panel = new Element[6];
        colorPanel = new Element[6];
        nameLabel = new Element[6];
        creatureLabel = new Element[6];
    }

    public PlayerMenuController(String data)
    {
        /**
         * Your custom constructor, can accept arguments
         */
    }

    /**
     * Nifty GUI ScreenControl methods
     */
    public void bind(Nifty nifty, Screen screen)
    {
        this.nifty = nifty;
        this.screen = screen;
    }

    public void onStartScreen()
    {
    }

    public void onEndScreen()
    {
    }

    /**
     * jME3 AppState methods
     */
    public void createNewCreatue(Creature creature)
    {
    }

    public void init()
    {
        for (int i = 0; i < 10; i++)
        {
            panel[i] = nifty.getScreen("playerMenu").findElementByName("player_id" + i);
            colorPanel[i] = nifty.getScreen("playerMenu").findElementByName("player_color_id" + i);
            nameLabel[i] = nifty.getScreen("playerMenu").findElementByName("player_name_id" + i);
            creatureLabel[i] = nifty.getScreen("playerMenu").findElementByName("player_creature_id" + i);
        }
    }
    

    @Override
    public void initialize(AppStateManager stateManager, Application app)
    {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
    }

    @Override
    public void update(float tpf)
    {
        List<Player> players = game.getPlayers();
        for (int i = 0; i < 10; i++)
        {
            if (i < players.size())
            {
                panel[i].setVisible(true);
                colorPanel[i].getRenderer(PanelRenderer.class).setBackgroundColor(new Color(game.getColors()[i][0],game.getColors()[i][1],game.getColors()[i][2],game.getColors()[i][3]));
                nameLabel[i].getRenderer(TextRenderer.class).setText(players.get(i).getUsername());
                creatureLabel[i].getRenderer(TextRenderer.class).setText(Integer.toString(players.get(i).getCreatures().size()));
            }
            else{
                panel[i].setVisible(false);
            }
        }
    }
}

