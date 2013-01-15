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
import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.effects.EffectEventId;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.exception.ActionNotEnabledException;
import main.game.Game;
import main.game.action.SpawnAction;
import main.game.model.creature.AirborneCreature;
import main.game.model.creature.Creature;
import main.game.model.creature.LandCreature;
import main.game.model.creature.SeaCreature;

/**
 *
 * @author s116861
 */
public class CreatureMenuController extends AbstractAppState implements ScreenController
{

    private Nifty nifty;
    private Screen screen;
    private SimpleApplication app;
    private Game game;
    Element[] panel;
    Element[] checkBox;
    Element[] idLabel;
    Element[] levelLabel;
    Element[] healthLabel;
    Element[] staminaLabel;
    Element[] image;
    NiftyImage img;

    /**
     * custom methods
     */
    public void feedCreature(int numFood)
    {
        game.feedSelectedCreature(numFood);
        nifty.gotoScreen("hud");
    }

    public void cancel()
    {
        System.out.println("cancelled");
        for(int i = 0; i < 10; i++){
            CheckBox c = (CheckBox)checkBox[i];
            if(c.isChecked()){
                
            }
        }
        nifty.gotoScreen("hud");
    }

    public CreatureMenuController(Game game)
    {
        this.game = game;
        this.nifty = game.getNifty();

        panel = new Element[10];
        checkBox = new Element[10];
        idLabel = new Element[10];
        levelLabel = new Element[10];
        healthLabel = new Element[10];
        staminaLabel = new Element[10];
        image = new Element[10];
    }

    public CreatureMenuController(String data)
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
            panel[i] = nifty.getScreen("creatureMenu").findElementByName("check_id" + i);
            checkBox[i] = nifty.getScreen("creatureMenu").findElementByName("mark_id" + i);
            idLabel[i] = nifty.getScreen("creatureMenu").findElementByName("number_id" + i);
            levelLabel[i] = nifty.getScreen("creatureMenu").findElementByName("level_id" + i);
            healthLabel[i] = nifty.getScreen("creatureMenu").findElementByName("health_id" + i);
            staminaLabel[i] = nifty.getScreen("creatureMenu").findElementByName("stamina_id" + i);
            image[i] = nifty.getScreen("creatureMenu").findElementByName("type_id" + i);
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
        List<Creature> creatures = game.getMe().getCreatures();
        for (int i = 0; i < 10; i++)
        {
            if (i < creatures.size())
            {
                panel[i].setVisible(true);
                idLabel[i].getRenderer(TextRenderer.class).setText(creatures.get(i).getId());
                levelLabel[i].getRenderer(TextRenderer.class).setText(Integer.toString(creatures.get(i).getLevel()));
                healthLabel[i].getRenderer(TextRenderer.class).setText(Integer.toString(creatures.get(i).getHealth()));

                if (creatures.get(i) instanceof LandCreature)
                {
                    staminaLabel[i].getRenderer(TextRenderer.class).setText("-");
                    img = nifty.getRenderEngine().createImage("Interface/land.png", false);
                } else if (creatures.get(i) instanceof SeaCreature)
                {
                    staminaLabel[i].getRenderer(TextRenderer.class).setText("-");
                    img = nifty.getRenderEngine().createImage("Interface/sea.png", false);
                } else if (creatures.get(i) instanceof AirborneCreature)
                {
                    staminaLabel[i].getRenderer(TextRenderer.class).setText(Integer.toString(((AirborneCreature) creatures.get(i)).getStamina()));
                    img = nifty.getRenderEngine().createImage("Interface/air.png", false);
                }
                image[i].getRenderer(ImageRenderer.class).setImage(img);
            }
            else{
                panel[i].setVisible(false);
            }
        }
    }
}
