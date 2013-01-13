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
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
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
    
    public final static int PORT_BROADCASTLISTEN = 13340;
    
    private XStream xstream = new XStream();
    private Lobby lobby;
    
    private DatagramSocket broadcastSocket = null;
    private DatagramSocket socket = null;
    private Client me;
    private boolean joiningServer = false;
    
    private boolean serverStarted;
    private List<Client> tokenRing;
    
    /**
     * Constructor
     */
    
    public InitialClient(Lobby lobby)
    {
        try {
            this.lobby = lobby;
            this.socket = new DatagramSocket(Client.PORT);
            this.socket.setSoTimeout(500);
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
            private boolean polling = true;
            
            public void run()
            {
                try
                {
                    DatagramSocket broadcastSocket = new DatagramSocket(PORT_BROADCASTLISTEN);
                    broadcastSocket.setSoTimeout(500);
                    byte[] buffer = new byte[2048];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                    //long count = 0;
                    
                    //GameCredentials gameCredentials = null;
                    
                    // Poll server for 5 seconds
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask()
                    {
                        @Override
                        public void run()
                        {
                            System.out.println("No servers found");
                            polling = false;
                        }
                    }, 5000);
                    
                    while (polling)
                    {
                        //socket.setSoTimeout(5000);
                        try
                        {
                            broadcastSocket.receive(packet);
                            
                            String strMessage = new String(buffer, 0, packet.getLength());
                            packet.setLength(buffer.length);

                            // Handle message
                            Message message = (Message) xstream.fromXML(strMessage);
                            System.out.println("(Listening to broadcasts) Client in: " + strMessage);

                            if (message instanceof MessageGameCredentials)
                            {
                                MessageGameCredentials messageGameCredentials = (MessageGameCredentials) message;

                                // Add new server to lobbyframe if it is new
                                //System.out.println("Found!");
                                //System.out.println(strMessage);

                                //gameCredentials = messageGameCredentials.getGameCredentials();
                                lobby.addGameCredentials(messageGameCredentials.getGameCredentials());
                                //break;
                            }
                        }
                        catch (SocketTimeoutException ex)
                        {
                            System.out.println("Still no servers found");
                        }
                        
                        try {
                            //count += 100;
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(InitialServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                    broadcastSocket.close();
                    
                    System.out.println("Done");
                    
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
        /**
         * Open thread that listens to incoming messages
         */
        
        Thread listener = new Thread(new Runnable()
        {
            private boolean listening = true;
            private boolean hasSentRequest = false;
            
            public void run()
            {
                byte[] buffer = new byte[2048];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                
                Client me = null;
                
                while (this.listening)
                {
                    try
                    {
                        socket.receive(packet);
                        String strMessage = new String(buffer, 0, packet.getLength());
                        Message message = (Message) xstream.fromXML(strMessage);
                        
                        System.out.println("Client listener: " + strMessage);
                   
                        if (message instanceof MessagePing)
                        {
                            MessagePong msgPong = new MessagePong();
                            msgPong.setFromClientId(me.getId());
                            byte[] sendMsg = xstream.toXML(msgPong).getBytes();
                            DatagramPacket sendPacket = new DatagramPacket(sendMsg, sendMsg.length, packet.getAddress(), InitialServer.PORT);
                            socket.send(sendPacket);
                        }
                        if (message instanceof MessageJoinApproved)
                        {
                            me = ((MessageJoinApproved) message).getClient();
                        }
                        else if (message instanceof MessageJoinDisapproved)
                        {
                            System.out.println("Join disapproved: " + ((MessageJoinDisapproved) message).getReason());
                            this.stopListening();
                        }
                        else if (message instanceof MessagePlayerJoined)
                        {
                            lobby.addPlayer(((MessagePlayerJoined) message).getUsername());
                        }
                        else if (message instanceof MessageStartGame)
                        {
                            MessageStartGame msgStartGame = (MessageStartGame) message;

                            // Find me in token ring
                            // (new instance of me since it has been sent over the network with the message)
                            for (Client client : msgStartGame.getTokenRing())
                            {
                                if (client.getId() == me.getId())
                                {
                                    me = client;
                                }
                            }
                            
                            lobby.startGame(me, msgStartGame.getTokenRing(), msgStartGame.getGameCredentials());
                            this.stopListening();
                        }
                        
                        packet.setLength(buffer.length);
                    }                    
                    catch (SocketTimeoutException ex)
                    {
                        System.out.println("Listening...");
                    }
                    catch (IOException ex)
                    {
                        Logger.getLogger(InitialClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    try
                    {
                        Thread.sleep(10);
                    }
                    catch (InterruptedException ex)
                    {
                        Logger.getLogger(InitialClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
            private void stopListening()
            {
                this.listening = false;
            }
        });
        
        listener.start();
        
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(InitialClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        /**
         * Send join request
         */
        
        try
        {
            InetAddress address = InetAddress.getByName(gameCredentials.getInitialHostIp());
            MessageJoinRequest msgJoinReq = new MessageJoinRequest(username);
            String reqstrMsg = xstream.toXML(msgJoinReq);
            byte[] reqMessage = reqstrMsg.getBytes();

            System.out.println("Client out: " + reqstrMsg);
            DatagramPacket reqPacket = new DatagramPacket(reqMessage, reqMessage.length, address, InitialServer.PORT);
            socket.send(reqPacket);
        }   
        catch (UnknownHostException ex)
        {
            Logger.getLogger(InitialClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(InitialClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
        
        
    public void oldJoinGame(GameCredentials gameCredentials, String username)
    {   
        try {
            //try
            //{
                // Open socket
                
                /**
                 * Listen to server for response (for 10 seconds, then continue if approved)
                 */
                            
                Thread thread = new Thread(new Runnable()
                {
                    private boolean polling;
                    public void run()
                    {
                        System.out.println("Start thread");
                        try
                        {
                            //DatagramSocket socket = new DatagramSocket(Client.PORT);
                            byte[] buffer = new byte[2048];
                            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                            //long count = 0;
                            
                            // Poll server for 5 seconds
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask()
                            {
                                @Override
                                public void run()
                                {
                                    polling = false;
                                    System.out.println("Stop polling");
                                }
                            }, 5000);
                            
                            System.out.println("Start polling");
                            
                           socket.setSoTimeout(500);
                           
                            while (polling)
                            { 
                                try
                                {
                                    socket.receive(packet);
                                    
                                    String strMessage = new String(buffer, 0, packet.getLength());
                                    packet.setLength(buffer.length);

                                    // Add new server to lobbyframe if it is new
                                    Message message = (Message) xstream.fromXML(strMessage);
                                    System.out.println("(Polling) Client in: " + strMessage);

                                    if (message instanceof MessageJoinApproved)
                                    {
                                        MessageJoinApproved msgJoinApproved = (MessageJoinApproved) message;

                                        // Add new server to lobbyframe if it is new
                                        me = msgJoinApproved.getClient();
                                        System.out.println("Approved!");
                                        joiningServer = true;
                                        //break;
                                    }
                                    else if (message instanceof MessageJoinDisapproved)
                                    {
                                        MessageJoinDisapproved msgJoinDisapproved = (MessageJoinDisapproved) message;

                                        System.out.println("Disapproved! Reason: " + msgJoinDisapproved.getReason());
                                        joiningServer = false;
                                        //break;
                                    }
                                }
                                catch (SocketTimeoutException ex)
                                {
                                    System.out.println("Timeout. No response yet.");
                                }
                                
                                try {
                                    //count += 10;
                                    Thread.sleep(10);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(InitialClient.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            
                            while (joiningServer)
                            {
                                try
                                {
                                    socket.receive(packet);
                                    
                                    String strMessage = new String(buffer, 0, packet.getLength());
                                    packet.setLength(buffer.length);

                                    // Add new server to lobbyframe if it is new
                                    Message message = (Message) xstream.fromXML(strMessage);
                                    System.out.println("(joining server) Client in: " + strMessage);
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

                                        me = newMe;
                                        tokenRing = msgStartGame.getTokenRing();
                                        serverStarted = true;

                                        joiningServer = false;

                                        lobby.startGame(me, tokenRing, msgStartGame.getGameCredentials());

                                        break;
                                    }
                                }
                                catch (SocketTimeoutException ex)
                                {
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
                
                /**
                * Send join request
                */

               InetAddress address = InetAddress.getByName(gameCredentials.getInitialHostIp());

              MessageJoinRequest msgJoinReq = new MessageJoinRequest(username);
              String reqstrMsg = xstream.toXML(msgJoinReq);
              byte[] reqMessage = reqstrMsg.getBytes();

              System.out.println("Client out: " + reqstrMsg);

              DatagramPacket reqPacket = new DatagramPacket(reqMessage, reqMessage.length, address, InitialServer.PORT);
              socket.send(reqPacket);
                
                // Close socket
                //socket.close();
            //}       
            } catch (UnknownHostException ex) {
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