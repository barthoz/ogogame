/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.game.GameCredentials;
import main.network.message.Message;
import main.network.message.MessageJoinRequest;
import com.thoughtworks.xstream.XStream;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import main.game.Player;
import main.game.action.Action;
import main.lobby.Lobby;
import main.network.message.MessageGameCredentials;
import main.network.message.MessageJoinApproved;
import main.network.message.MessageJoinDisapproved;
import main.network.message.MessagePassToken;
import main.network.message.MessagePing;
import main.network.message.MessagePlayerJoined;
import main.network.message.MessagePong;
import main.network.message.MessageStartGame;

/**
 *
 * @author s116861
 */
public class InitialServer
{
    /**
     * Properties
     */
    
    private Lobby lobby;
    
    public final static int PORT = 13337;
    
    private XStream xstream = new XStream();
    
    private ServerBroadcaster serverBroadcaster = null;
    private List<Client> clients = new ArrayList<Client>();
    private boolean started = true;
    private DatagramSocket socket = null;
    private Client me;
    
    /**
     * Constructor
     */
    
    public InitialServer(Lobby lobby)
    {
        this.lobby = lobby;
        
        try
        {
            try
            {
                this.socket = new DatagramSocket(PORT);
                this.socket.setSoTimeout(10);
            } catch (SocketException ex) {
                Logger.getLogger(InitialServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            InetAddress localHost = Inet4Address.getLocalHost();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
            String address = networkInterface.getInterfaceAddresses().get(0).getAddress().toString().replaceFirst("/", "");
            
            //Client me = new Client(0, address, "Test");
            //this.clients.add(me);
        } catch (SocketException ex) {
            Logger.getLogger(InitialServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(InitialServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Business logic
     */
    
    public void broadcastGame(GameCredentials gameCredentials, Client me)
    {
        this.me = me;
        this.clients.add(me);
        this.lobby.addPlayer(me.getUsername());
        this.serverBroadcaster = new ServerBroadcaster(gameCredentials, socket);
        
        Thread thread = new Thread(this.serverBroadcaster);
        thread.start();
    }
    
    public void stopBroadcasting()
    {
        this.serverBroadcaster.stopBroadcasting();
    }
    
    public void listenToClients()
    {
        this.started = true;
        
        Thread listening = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    //DatagramSocket socket = new DatagramSocket(PORT);
                    byte[] buffer = new byte[2048];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                    while (started)
                    {
                        try
                        {
                            socket.receive(packet);
                            
                            String strMessage = new String(buffer, 0, packet.getLength());
                            packet.setLength(buffer.length);

                            // Handle message
                            Message message = (Message) xstream.fromXML(strMessage);
                            System.out.println("Server in: " + strMessage);

                            if (message instanceof MessageJoinRequest)
                            {
                                MessageJoinRequest messageJoinReq = (MessageJoinRequest) message;

                                // Check whether client is allowed to join.
                                if (clients.size() > 6 || lobby.getPlayersInGame().contains(messageJoinReq.getUsername()))
                                {
                                    // Client disapproved
                                    String reason;
                                    if (clients.size() > 6)
                                    {
                                        reason = "Server is full.";
                                    }
                                    else if (lobby.getPlayersInGame().add(messageJoinReq.getUsername()))
                                    {
                                        reason = "Username has already been taken.";
                                    }

                                    MessageJoinDisapproved msgDisapproved = new MessageJoinDisapproved();
                                    byte[] sendBuffer = xstream.toXML(msgDisapproved).getBytes();
                                    DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, packet.getAddress(), Client.PORT);
                                    socket.send(sendPacket);
                                }
                                else
                                {
                                    // Create new client
                                    Client client = new Client(retrieveNewId(), packet.getAddress().getHostAddress(), messageJoinReq.getUsername());
                                    System.out.println("Added client! Details:");
                                    clients.add(client);
                                    lobby.addPlayer(client.getUsername());
                                    
                                    // Wake up client
                                    byte[] pingBuffer = xstream.toXML(new MessagePing()).getBytes();
                                    DatagramPacket wakePacket = new DatagramPacket(pingBuffer, pingBuffer.length, InetAddress.getByName(client.getAddress()), Client.PORT);
                                    for (int i = 0; i < 15; i++)
                                    {
                                        socket.send(wakePacket);
                                    }
                                    
                                    // Success, so send message back to client with success
                                    MessageJoinApproved messageApproved = new MessageJoinApproved(client);
                                    String strMessageApproved = xstream.toXML(messageApproved);
                                    byte[] sendBuffer = strMessageApproved.getBytes();
                                    DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, InetAddress.getByName(client.getAddress()), Client.PORT);
                                    System.out.println("Server out: " + strMessageApproved);
                                    socket.send(sendPacket);

                                    // Send the update to all clients that a player has joined
                                    MessagePlayerJoined msgPlayerJoined = new MessagePlayerJoined(client.getUsername());
                                    sendBuffer = xstream.toXML(msgPlayerJoined).getBytes();

                                    for (Client c : clients)
                                    {
                                        sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, InetAddress.getByName(c.getAddress()), Client.PORT);
                                        socket.send(sendPacket);
                                    }

                                    // Send the update to the new client of all joined clients
                                    for (Client c : clients)
                                    {
                                        MessagePlayerJoined msgPlayerJoinedNew = new MessagePlayerJoined(c.getUsername());
                                        System.out.println("Server out: " + xstream.toXML(msgPlayerJoinedNew));
                                        sendBuffer = xstream.toXML(msgPlayerJoinedNew).getBytes();

                                        sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, InetAddress.getByName(client.getAddress()), Client.PORT);
                                        socket.send(sendPacket);                                    
                                    }
                                }
                            }
                        }
                        catch (SocketTimeoutException ex)
                        {
                           
                        }
                        
                        /*try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(InitialServer.class.getName()).log(Level.SEVERE, null, ex);
                        }*/
                    }
                } catch (IOException ex) {
                    Logger.getLogger(InitialServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        listening.start();
    }
    
    public void stopListening()
    {
        this.started = false;
    }
    
    /**
     * Start the game. InitialHost is dropped, client is initialized and initial host has the token.
     */
    public void startGame()
    {
        this.stopListening();
        
        // Check which clients are still active        
        Thread listening = new Thread(new Runnable()
        {
            private boolean polling = true;
            
            public void run()
            {                    
                final Thread pinging = new Thread(new Runnable()
                {
                    public void run()
                    {
                        System.out.println("Server out (pinging): " + xstream.toXML(new MessagePing()));
                        byte[] sendMsg = xstream.toXML(new MessagePing()).getBytes();
                        DatagramPacket packetPing;

                        for (Client client : clients)
                        {
                            try
                            {
                                packetPing = new DatagramPacket(sendMsg, sendMsg.length, InetAddress.getByName(client.getAddress()), Client.PORT);
                                socket.send(packetPing);
                            }
                            catch (UnknownHostException ex)
                            {
                                Logger.getLogger(InitialServer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            catch (IOException ex)
                            {
                                Logger.getLogger(InitialServer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } 
                    }
                });

                Thread polling = new Thread(new Runnable()
                {
                    private boolean isPolling = true;

                    public void run() 
                    {
                        Map<Client, Boolean> responded = new HashMap<Client, Boolean>();

                        for (Client client : clients)
                        {
                            responded.put(client, false);
                        }

                        responded.put(me, true);

                        // Poll clients for 3 seconds
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask()
                        {
                            @Override
                            public void run()
                            {
                                isPolling = false;
                                System.out.println("Polling finished.");
                            }
                        }, 3000);

                        byte[] buffer = new byte[2048];
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                        pinging.start();
                        
                        while (isPolling)
                        {
                            try
                            {
                                socket.receive(packet);

                                String strMessage = new String(buffer, 0, packet.getLength());
                                packet.setLength(buffer.length);

                                // Handle message
                                Message message = (Message) xstream.fromXML(strMessage);
                                System.out.println("(Start-game polling) Server in: " + strMessage);

                                if (message instanceof MessagePong)
                                {
                                    System.out.println(getClientById(message.getFromClientId()));
                                    responded.put(getClientById(message.getFromClientId()), true);
                                }
                            }
                            catch (SocketTimeoutException ex)
                            {
                                System.out.println("Polling");
                            }
                            catch (IOException ex)
                            {
                                Logger.getLogger(InitialServer.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            try
                            {
                                Thread.sleep(10);
                            }
                            catch (InterruptedException ex)
                            {
                                Logger.getLogger(InitialServer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        for (Client client : responded.keySet())
                        {
                            System.out.println("Client id: " + client.getId() + " - addr: " + client.getAddress() + " - responded: " + responded.get(client));

                            if (responded.get(client).equals(false))
                            {
                                clients.remove(client);
                            }
                        }

                        // Check whether number of new clients are between 2 and 6
                        if (clients.size() >= 2 && clients.size() <= 6)
                        {
                            //stopListening();
                            stopBroadcasting();
                            buildTokenRing();

                            MessageStartGame message = new MessageStartGame(clients, serverBroadcaster.getGameCredentials());
                            String strMessage = xstream.toXML(message);
                            byte[] sendBuffer = strMessage.getBytes();

                            for (Client client : clients)
                            {
                                if (client != me)
                                {
                                    try {
                                        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, InetAddress.getByName(client.getAddress()), Client.PORT);
                                        socket.send(sendPacket);
                                    } catch (UnknownHostException ex) {
                                        Logger.getLogger(InitialServer.class.getName()).log(Level.SEVERE, null, ex);
                                    } catch (IOException ex) {
                                        Logger.getLogger(InitialServer.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }

                            lobby.startGame(me, clients, serverBroadcaster.getGameCredentials(), true);
                        }
                        else
                        {
                            System.out.println("Cannot start game.");
                        }
                    }
                });

                polling.start();
            }
        });
        
        listening.start();
    }
    
    private Client getClientById(int id)
    {
        for (Client client : this.clients)
        {
            if (client.getId() == id)
            {
                return client;
            }
        }
        
        return null;
    }
    
    /**
     * Builds token ring.
     * 
     * @Pre this.clients != null
     * @Post this.clients is now a token ring
     */
    private void buildTokenRing()
    {
        int i = 0;
        int numClients = this.clients.size();
        
        for (Client client : this.clients)
        {
            client.setId(i);
            
            if (i == 0)
            {
                client.setInNeighbour(this.clients.get(numClients - 1));
                client.setOutNeighbour(this.clients.get(1));
            }
            else if (i == numClients - 1)
            {
                client.setInNeighbour(this.clients.get(numClients - 2));
                client.setOutNeighbour(this.clients.get(0));
            }
            else
            {
                client.setInNeighbour(this.clients.get(i - 1));
                client.setOutNeighbour(this.clients.get(i + 1));
            }
            
            i++;
        }
    }
    
    private int retrieveNewId()
    {
        int maxId = -1;
        
        for (Client client : this.clients)
        {
            if (client.getId() > maxId)
            {
                maxId = client.getId();
            }
        }
        
        return maxId + 1;
    }
    
    
    /**
     * Getters & Setters
     */
}
