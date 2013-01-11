package main;

import main.lobby.Lobby;
import com.thoughtworks.xstream.XStream;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        Logger.getLogger("NiftyInputEventHandlingLog").setLevel(Level.OFF);
        Logger.getLogger("NiftyEventBusLog").setLevel(Level.OFF);
        Logger.getLogger("NiftyImageManager").setLevel(Level.OFF);
        Lobby lobby = new Lobby();
    }
}
