package main;

import main.lobby.Lobby;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import main.game.Game;
import main.game.GameCredentials;
import main.network.InitialServer;

/**
 * This is where the magic all begins.
 * 
 * @author Daniel
 */
public class Main
{
    public static void main(String[] args)
    {
        
        InitialServer server = new InitialServer();
        //server.broadcastGame();
        Lobby lobby = new Lobby();
        Game game = new Game(lobby, new GameCredentials(0, "test", "hostnamehere"));
        game.start();
    }
}
