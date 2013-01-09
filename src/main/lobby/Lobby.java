/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.lobby;

import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.Main;
import main.game.Game;
import main.game.GameCredentials;
import main.network.Client;
import main.network.GameConnector;
import main.network.InitialClient;
import main.network.InitialServer;

/**
 *
 * @author Daniel
 */
public class Lobby
{
    /**
     * Properties
     */
    
    private GameConnector gameConnector;
    
    //private Me me;
    
    
    private LobbyJFrame lobbyFrame;
    private IntermediateClientJFrame intermediateClientJFrame;
    private IntermediateHostJFrame intermediateServerJFrame;
    private StartGameJFrame startGameJFrame;
    
    private List<GameCredentials> availableGames = new ArrayList<GameCredentials>();
    private List<String> playersInGame = new ArrayList<String>();
    
    private InitialClient initialClient = null;
    private InitialServer initialServer = null;
    
    private List<Client> tokenRing;
    private Client me;
    
    /**
     * Constructor
     */
    
    public Lobby()
    {
        this.gameConnector = new GameConnector();
        //this.me = null;
        
        this.lobbyFrame = new LobbyJFrame(this);
        this.intermediateClientJFrame = new IntermediateClientJFrame(this);
        this.intermediateServerJFrame = new IntermediateHostJFrame(this);
        this.startGameJFrame = new StartGameJFrame(this);
        lobbyFrame.setVisible(true);
        
        try {
            GameCredentials testGame = new GameCredentials(0, "test", "hostnamehere", NetworkInterface.getByInetAddress(Inet4Address.getLocalHost()).getInterfaceAddresses().get(0).getAddress().toString().replaceFirst("/", ""));
            
            Game game = new Game(this, testGame);
            game.start();
            
            this.initialServer = new InitialServer(this);
            this.initialClient = new InitialClient(this);
            //this.initialServer.broadcastGame(testGame);
            //this.initialServer.listenToClients();
            //this.initialClient.listenToServers();
            
        } catch (SocketException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Business logic
     */
    
    public void addGameCredentials(GameCredentials gameCredentials)
    {
        for (GameCredentials gc : this.availableGames)
        {
            if (gc.getInitialHostIp().equals(gameCredentials.getInitialHostIp()))
            {
                return;
            }
        }
        
        this.availableGames.add(gameCredentials);
        this.lobbyFrame.updateAvailableGames();
    }
    
    public void addPlayer(String username)
    {
        if (!this.playersInGame.contains(username))
        {
            this.playersInGame.add(username);
            this.intermediateClientJFrame.updatePlayers();
            this.intermediateServerJFrame.updatePlayers();
        }
    }
    
    public void joinGame(GameCredentials gameCredentials, String username)
    {
        this.initialClient.joinGame(gameCredentials, username);
    }
    
    public void createGame(String gameName, String username)
    {
        try {
           String address = NetworkInterface.getByInetAddress(Inet4Address.getLocalHost()).getInterfaceAddresses().get(0).getAddress().toString().replaceFirst("/", "");
           
           GameCredentials gameCredentials = new GameCredentials(0, gameName, username, address);
           Client me = new Client(0, address, username);
           this.initialServer.broadcastGame(gameCredentials, me);
           this.initialServer.listenToClients();
        } catch (SocketException ex) {
                Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, null, ex);    
        } catch (UnknownHostException ex) {
            Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void startGame(Client me, List<Client> tokenRing)
    {
        System.out.println("Server started!");
        this.me = me;
        this.tokenRing = tokenRing;
        me.setSocket(this.initialClient.getSocket());
        me.startListening();
    }
    
    /**
     * Getters & Setters
     */
    
    public GameConnector getGameConnector() {
        return gameConnector;
    }

    public void setGameConnector(GameConnector gameConnector) {
        this.gameConnector = gameConnector;
    }

    public LobbyJFrame getLobbyFrame() {
        return lobbyFrame;
    }

    public void setLobbyFrame(LobbyJFrame lobbyFrame) {
        this.lobbyFrame = lobbyFrame;
    }

    public List<GameCredentials> getAvailableGames() {
        return availableGames;
    }

    public void setAvailableGames(List<GameCredentials> availableGames) {
        this.availableGames = availableGames;
    }

    public IntermediateClientJFrame getIntermediateClientJFrame() {
        return intermediateClientJFrame;
    }

    public void setIntermediateClientJFrame(IntermediateClientJFrame intermediateClientJFrame) {
        this.intermediateClientJFrame = intermediateClientJFrame;
    }

    public IntermediateHostJFrame getIntermediateServerJFrame() {
        return intermediateServerJFrame;
    }

    public void setIntermediateServerJFrame(IntermediateHostJFrame intermediateServerJFrame) {
        this.intermediateServerJFrame = intermediateServerJFrame;
    }

    public List<String> getPlayersInGame() {
        return playersInGame;
    }

    public void setPlayersInGame(List<String> playersInGame) {
        this.playersInGame = playersInGame;
    }

    public InitialClient getInitialClient() {
        return initialClient;
    }

    public void setInitialClient(InitialClient initialClient) {
        this.initialClient = initialClient;
    }

    public InitialServer getInitialServer() {
        return initialServer;
    }

    public void setInitialServer(InitialServer initialServer) {
        this.initialServer = initialServer;
    }

    public StartGameJFrame getStartGameJFrame() {
        return startGameJFrame;
    }

    public void setStartGameJFrame(StartGameJFrame startGameJFrame) {
        this.startGameJFrame = startGameJFrame;
    }
}
