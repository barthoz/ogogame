/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.network;

import com.thoughtworks.xstream.XStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.game.GameCredentials;
import main.lobby.Lobby;
import main.network.message.*;
import main.network.old.InitClient;

/**
 *
 * @author s116861
 */
public class InitialClient
{
    /**
     * Properties
     */
    
    private XStream xstream = new XStream();
    private Lobby lobby;
    
    private DatagramSocket socket = null;
    private Client me;
    private boolean joiningServer = false;
    
    /**
     * Constructor
     */
    
    public InitialClient(Lobby lobby)
    {
        try {
            this.lobby = lobby;
            this.socket = new DatagramSocket(Client.PORT);
        } catch (SocketException ex) {
            Logger.getLogger(InitialClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Business logic
     */
    
    /**
     * Listen to server(s) for 10 seconds.
     */
    public void listenToServers()
    {
        Thread listening = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    //DatagramSocket socket = new DatagramSocket(Client.PORT);
                    byte[] buffer = new byte[2048];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                    long count = 0;
                    
                    //GameCredentials gameCredentials = null;
                    
                    while (true && count < 5000)
                    {
                        socket.receive(packet);
                        String strMessage = new String(buffer, 0, packet.getLength());
                        packet.setLength(buffer.length);
                        
                        // Handle message
                        Message message = (Message) xstream.fromXML(strMessage);
                        System.out.println("Client in: " + strMessage);
                        
                        if (message instanceof MessageGameCredentials)
                        {
                            MessageGameCredentials messageGameCredentials = (MessageGameCredentials) message;
                            
                            // Add new server to lobbyframe if it is new
                            //System.out.println("Found!");
                            //System.out.println(strMessage);
                            
                            //gameCredentials = messageGameCredentials.getGameCredentials();
                            lobby.addGameCredentials(messageGameCredentials.getGameCredentials());
                            break;
                        }
                        
                        try {
                            count += 100;
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(InitialServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                    //lobby.getLobbyFrame().updateAvailableGames(lobby.getAvailableGames());
                    
                    /*if (gameCredentials != null)
                    {
                        joinGame(gameCredentials, "Daniel");
                    }*/
                    
                } catch (IOException ex) {
                    Logger.getLogger(InitialServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        listening.start();
    }
    
    /**
     * Join game.
     * @param gameCredentials
     * @param username 
     */
    public void joinGame(GameCredentials gameCredentials, String username)
    {
        try
        {
            // Open socket
            //DatagramSocket socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(gameCredentials.getInitialHostIp());
            
            /**
             * Send join request
             */
            
            MessageJoinRequest msgJoinReq = new MessageJoinRequest(username);
            String strMsg = xstream.toXML(msgJoinReq);
            byte[] message = strMsg.getBytes();

            System.out.println("Client out: " + strMsg);
            
            DatagramPacket packet = new DatagramPacket(message, message.length, address, InitialServer.PORT);
            socket.send(packet);
            
            /**
             * Listen to server for response (for 10 seconds, then continue if approved)
             */
                        
            Thread thread = new Thread(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        //DatagramSocket socket = new DatagramSocket(Client.PORT);
                        byte[] buffer = new byte[2048];
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                        long count = 0;
                        
                        while (true && count < 5000)
                        {
                            socket.receive(packet);
                            String strMessage = new String(buffer, 0, packet.getLength());
                            packet.setLength(buffer.length);
                            
                            // Add new server to lobbyframe if it is new
                            Message message = (Message) xstream.fromXML(strMessage);
                            System.out.println("Client in: " + strMessage);
                            
                            if (message instanceof MessageJoinApproved)
                            {
                                MessageJoinApproved msgJoinApproved = (MessageJoinApproved) message;
                                
                                // Add new server to lobbyframe if it is new
                                me = msgJoinApproved.getClient();
                                System.out.println("Approved!");
                                joiningServer = true;
                                break;
                            }
                            else if (message instanceof MessageJoinDisapproved)
                            {
                                MessageJoinDisapproved msgJoinDisapproved = (MessageJoinDisapproved) message;
                                
                                System.out.println("Disapproved! Reason: " + msgJoinDisapproved.getReason());
                                joiningServer = false;
                                break;
                            }
                            
                            try {
                                count += 10;
                                Thread.sleep(10);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(InitialClient.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        
                        while (true && joiningServer)
                        {
                            socket.receive(packet);
                            String strMessage = new String(buffer, 0, packet.getLength());
                            packet.setLength(buffer.length);
                            
                            // Add new server to lobbyframe if it is new
                            Message message = (Message) xstream.fromXML(strMessage);
                            System.out.println("Client in: " + strMessage);
                            
                            if (message instanceof MessagePlayerJoined)
                            {
                                MessagePlayerJoined msgPlayerJoined = (MessagePlayerJoined) message;
                                lobby.addPlayer(msgPlayerJoined.getUsername());
                                System.out.println("Player joined: " + msgPlayerJoined.getUsername());
                            }
                            else if (message instanceof MessagePing)
                            {
                                byte[] sendMsg = xstream.toXML(new MessagePong()).getBytes();
                                DatagramPacket sendPacket = new DatagramPacket(sendMsg, sendMsg.length, packet.getAddress(), InitialServer.PORT);
                                socket.send(sendPacket);
                            }
                            else if (message instanceof MessageStartGame)
                            {
                                MessageStartGame msgStartGame = (MessageStartGame) message;
                                
                                // Find me in token ring
                                // (new instance of me since it has been sent over the network with the message)
                                Client newMe = null;
                                for (Client client : msgStartGame.getTokenRing())
                                {
                                    if (client.getId() == me.getId())
                                    {
                                        newMe = client;
                                    }
                                }
                                
                                lobby.startGame(newMe, msgStartGame.getTokenRing(), socket);
                                joiningServer = false;
                            }
                            
                            try
                            {
                                Thread.sleep(10);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(InitialClient.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } catch (SocketException ex) {
                        Logger.getLogger(InitialClient.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(InitialClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            
            thread.start();
            
            // Close socket
            //socket.close();
        }
        catch (SocketException ex) {        
            Logger.getLogger(InitialClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(InitialClient.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    /**
     * Getters & Setters
     */
    
    public DatagramSocket getSocket() {
        return socket;
    }

    public void setSocket(DatagramSocket socket) {
        this.socket = socket;
    }
}