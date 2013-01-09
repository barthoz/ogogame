/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.network.message.Message;
import com.thoughtworks.xstream.XStream;
import java.net.InetAddress;
import main.game.Player;
import main.network.message.*;

/**
 *
 * @author s116861
 */
public class Client
{
    /**
     * Properties
     */
    
    public final static int PORT = 13338;
    
    private int id;
    private String address;
    private String username;
    private Client inNeighbour;
    private Client outNeighbour;
    
    private transient DatagramSocket socket;
    private transient boolean isListening = false;
    private transient Player player;
    
    /**
     * Constructor
     */
    
    public Client(int id, String address, String username)
    {
        this.id = id;
        this.address = address;
        this.username = username;
    }
    
    /**
     * Business logic
     */
    
    /**
     * @Pre socket != null
     */
    public void startListening()
    {
        this.isListening = true;
        
        Thread listening = new Thread(new Runnable()
        {
            public void run()
            {
                XStream xstream = new XStream();
                byte[] buffer = new byte[2048];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                while (true && isListening)
                {
                    try
                    {
                        socket.receive(packet);
                        String strMessage = new String(buffer, 0, packet.getLength());
                        packet.setLength(buffer.length);

                        // Handle message
                        Message message = (Message) xstream.fromXML(strMessage);
                        System.out.println("Client in: " + strMessage);

                        byte[] sendBuffer;
                        DatagramPacket sendPacket;
                        Message sendMessage = null;
                        
                        if (message.getFromClientId() == id)
                        {
                            // Pass token onto next neighbour
                            sendMessage = new MessagePassToken();
                        }
                        else if (message instanceof MessagePassToken)
                        {
                            // We have the token, create our MessagePlayerActions or MessageLeaveGame message here.
                            
                        }
                        else
                        {
                            // Update the game from messages
                            if (message instanceof MessageLeaveGame)
                            {

                            }
                            else if (message instanceof MessagePlayerActions)
                            {
                                // We have received actions from a player

                            }
                            
                            // Pass message onto the next neighbour (unchanged)
                        }
                        
                        sendMessage.setFromClientId(id);
                        sendBuffer = xstream.toXML(sendMessage).getBytes();
                        sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, InetAddress.getByName(outNeighbour.getAddress()), Client.PORT);
                        socket.send(sendPacket);
                            
                        try
                        {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        
        listening.start();
    }
    
    public void stopListening()
    {
        this.isListening = false;
    }

    @Override
    public int hashCode() {
        int hash = this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Client other = (Client) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }
    
    
    
    /**
     * Getters & Setters
     */
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }    

    public Client getInNeighbour() {
        return inNeighbour;
    }

    public void setInNeighbour(Client inNeighbour) {
        this.inNeighbour = inNeighbour;
    }

    public Client getOutNeighbour() {
        return outNeighbour;
    }

    public void setOutNeighbour(Client outNeighbour) {
        this.outNeighbour = outNeighbour;
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public void setSocket(DatagramSocket socket) {
        this.socket = socket;
    }
}
