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
    
    /**
     * Constructor
     */
    
    public InitialServer()
    {
        
    }
    
    /**
     * Business logic
     */
    
    public void broadcastGame(GameCredentials gameCredentials)
    {
        this.serverBroadcaster = new ServerBroadcaster(gameCredentials);
        
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
                    DatagramSocket socket = new DatagramSocket(PORT);
                    byte[] buffer = new byte[2048];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                    while (true && started)
                    {
                        socket.receive(packet);
                        String strMessage = new String(buffer, 0, packet.getLength());
                        packet.setLength(buffer.length);
                        
                        // Handle message
                        Message message = (Message) xstream.fromXML(strMessage);
                        System.out.println(strMessage);
                        
                        if (message instanceof MessageJoinRequest)
                        {
                            MessageJoinRequest messageJoinReq = (MessageJoinRequest) message;
                            
                            // Create new client
                            Client client = new Client(retrieveNewId(), packet.getAddress().getHostAddress(), messageJoinReq.getUsername());
                            System.out.println("Added client! Details:");
                            System.out.println(xstream.toXML(client));
                            clients.add(client);
                            
                            // Success, so send message back to client with success
                            Message messageApproved = new Message(Message.TYPE_JOIN_APPROVED);
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
