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
import com.thoughtworks.xstream.XStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.network.InitialClient;

/**
 * This is where the magic all begins.
 * 
 * @author Daniel
 */
public class Main
{
    public static XStream xstream = new XStream();
    
    public static void main(String[] args)
    {
        /*try {
            GameCredentials testGame = new GameCredentials(0, "test", "hostnamehere", NetworkInterface.getByInetAddress(Inet4Address.getLocalHost()).getInterfaceAddresses().get(0).getAddress().toString().replaceFirst("/", ""));
            
            Lobby lobby = new Lobby();
            Game game = new Game(lobby, testGame);
            game.start();
            
            InitialServer server = new InitialServer();
            InitialClient client = new InitialClient(lobby);
            server.broadcastGame(testGame);
            server.listenToClients();
            client.listenToServers();
            
        } catch (SocketException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        
        Lobby lobby = new Lobby();
    }
}
