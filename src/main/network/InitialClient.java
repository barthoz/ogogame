/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.network;

import com.thoughtworks.xstream.XStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.lobby.Lobby;
import main.network.message.Message;
import main.network.message.MessageGameCredentials;
import main.network.message.MessageJoinRequest;

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
    
    /**
     * Constructor
     */
    
    public InitialClient(Lobby lobby)
    {
        this.lobby = lobby;        
    }
    
    /**
     * Business logic
     */
    
    public void listenToServers()
    {
        Thread listening = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    DatagramSocket socket = new DatagramSocket(Client.PORT);
                    byte[] buffer = new byte[2048];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                    long count = 0;
                    
                    while (true && count < 10000)
                    {
                        socket.receive(packet);
                        String strMessage = new String(buffer, 0, packet.getLength());
                        packet.setLength(buffer.length);
                        
                        // Handle message
                        Message message = (Message) xstream.fromXML(strMessage);
                        System.out.println(strMessage);
                        
                        if (message instanceof MessageGameCredentials)
                        {
                            MessageGameCredentials messageGameCredentials = (MessageGameCredentials) message;
                            
                            // Add new server to lobbyframe if it is new
                            System.out.println(strMessage);
                        }
                        
                        try {
                            count += 10;
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
}