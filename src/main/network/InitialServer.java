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
import main.network.message.MessageJoinApproved;
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
    
    public final static int PORT = 13337;
    
    private XStream xstream = new XStream();
    
    private ServerBroadcaster serverBroadcaster = null;
    private List<Client> clients = new ArrayList<Client>();
    private boolean started = true;
    private DatagramSocket socket = null;
    
    /**
     * Constructor
     */
    
    public InitialServer()
    {
        try
        {
            try
            {
                this.socket = new DatagramSocket(PORT);
            } catch (SocketException ex) {
                Logger.getLogger(InitialServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            InetAddress localHost = Inet4Address.getLocalHost();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
            String address = networkInterface.getInterfaceAddresses().get(0).getAddress().toString().replaceFirst("/", "");
            
            Client me = new Client(0, address, "Test");
            this.clients.add(me);
        } catch (SocketException ex) {
            Logger.getLogger(InitialServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(InitialServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Business logic
     */
    
    public void broadcastGame(GameCredentials gameCredentials)
    {
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

                    while (true && started)
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
                            
                            // Create new client
                            Client client = new Client(retrieveNewId(), packet.getAddress().getHostAddress(), messageJoinReq.getUsername());
                            System.out.println("Added client! Details:");
                            //System.out.println(xstream.toXML(client));
                            clients.add(client);
                            
                            // Success, so send message back to client with success
                            MessageJoinApproved messageApproved = new MessageJoinApproved();
                            String strMessageApproved = xstream.toXML(messageApproved);
                            byte[] sendBuffer = strMessageApproved.getBytes();
                            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, packet.getAddress(), Client.PORT);
                            socket.send(sendPacket);
                        }
                        
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(InitialServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
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
        this.stopBroadcasting();
        this.buildTokenRing();
        this.initializeBases();
        
        MessageStartGame message = new MessageStartGame(this.clients);
        String strMessage = xstream.toXML(message);
        byte[] sendBuffer = strMessage.getBytes();
        
        for (Client client : this.clients)
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
    
    /**
     * Give unique base to each client.
     * 
     * @Pre this.clients.size() <= 6
     * @Post each client is assigned a unique base
     */
    private void initializeBases()
    {
        int i = 0;
        
        for (Client client : this.clients)
        {
            client.setBaseId(i);
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
