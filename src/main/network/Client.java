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
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import main.game.Game;
import main.game.Player;
import main.game.action.Action;
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
    //public final static int INGAME_PORT = 13339;
    
    private int id;
    private String address;
    private String username;
    private Client inNeighbour;
    private Client outNeighbour;
    
    private transient DatagramSocket socket;
    private transient boolean isListening = false;
    private transient Player player;
    private transient Game game;
    
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
    
    public void startListening()
    {        
        this.isListening = true;
        
        Thread listening = new Thread(new Runnable()
        {
            private Map<Integer, Boolean> setModeDoneMap = new HashMap<Integer, Boolean>();
            
            public void run()
            {
                // Initialize set turns
                for (Player p : game.getPlayers())
                {
                    setModeDoneMap.put(p.getId(), false);
                }
                
                XStream xstream = new XStream();
                byte[] buffer = new byte[2048];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                long numTimeouts = 0;
                
                while (isListening)
                {
                    try
                    {
                        // Check whether the number of timeouts exceed 10 and we are the host
                        if (numTimeouts > 15 && id == 0)
                        {
                            MessagePassToken restartMessage = new MessagePassToken();
                            restartMessage.setFromClientId(id);
                            byte[] restartBuffer = xstream.toXML(restartMessage).getBytes();

                            System.out.println("(Restart token-ring) Client out: " + xstream.toXML(restartMessage));
                            DatagramPacket restartPacket = new DatagramPacket(restartBuffer, restartBuffer.length, InetAddress.getByName(outNeighbour.getAddress()), Client.PORT);
                            socket.send(restartPacket);
                            
                            numTimeouts = 0; // restart the timeout count
                        }
                        
                        socket.receive(packet);                        
                        numTimeouts = 0; // restart timeout count
                        
                        String strMessage = new String(buffer, 0, packet.getLength());
                        packet.setLength(buffer.length);
                        
                        // Handle message
                        Message message = (Message) xstream.fromXML(strMessage);
                        
                        if (message instanceof MessagePassToken || message instanceof MessageSetModeDone || message instanceof MessageLeaveGame || message instanceof MessagePlayerActions)
                        {
                            //if (!(message instanceof MessagePassToken))
                            //{
                                System.out.println("Client in: " + strMessage);
                            //}

                            byte[] sendBuffer;
                            DatagramPacket sendPacket;
                            Message sendMessage = null;
                            
                            if (message.getFromClientId() == id)
                            {
                                // Pass token onto next neighbour
                                sendMessage = new MessagePassToken();
                                sendMessage.setFromClientId(id);
                            }
                            else if (message instanceof MessagePassToken)
                            {
                                // We have the token, create our MessagePlayerActions or MessageLeaveGame message here.

                                // Check whether our set mode is done
                                if (game.setModeDone && !game.setModeSent)
                                {
                                    // Send message to all players that our set mode is done
                                    game.setModeSent = true;
                                    setModeDoneMap.put(id, true);

                                    sendMessage = new MessageSetModeDone(player.getActions());
                                    sendMessage.setFromClientId(id);
                                }
                                else
                                {
                                    sendMessage = new MessagePassToken();
                                    sendMessage.setFromClientId(id);
                                }
                            }
                            else
                            {
                                // Update the game from messages
                                if (message instanceof MessageSetModeDone)
                                {
                                    setModeDoneMap.put(message.getFromClientId(), true);
                                    
                                    List<Action> actions = ((MessageSetModeDone) message).getActions();
                                    
                                    for (Action action : actions)
                                    {
                                        action.deserialize(game);
                                    }
                                    
                                    game.getPlayerById(message.getFromClientId()).setActions(actions);

                                    boolean allDone = true;

                                    for (Integer tempId : setModeDoneMap.keySet())
                                    {
                                        System.out.println("SETMODEDONE - Client: " + tempId + " - Done: " + setModeDoneMap.get(tempId));
                                        
                                        if (!setModeDoneMap.get(tempId))
                                        {
                                            allDone = false;
                                            break;
                                        }
                                    }

                                    if (allDone)
                                    {
                                        for (Integer id : setModeDoneMap.keySet())
                                        {
                                            setModeDoneMap.put(id, false);
                                        }

                                        game.getModeBlocked = false;
                                        game.setModeSent = false;
                                    }
                                }
                                else if (message instanceof MessageLeaveGame)
                                {

                                }
                                else if (message instanceof MessagePlayerActions)
                                {
                                    // We have received actions from a player

                                }

                                // Pass message onto the next neighbour (unchanged)
                                sendMessage = message;
                            }

                            //sendMessage = new MessagePassToken();
                            sendBuffer = xstream.toXML(sendMessage).getBytes();

                            System.out.println("Client out: " + xstream.toXML(sendMessage));
                            sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, InetAddress.getByName(outNeighbour.getAddress()), Client.PORT);
                            socket.send(sendPacket);
                        }
                        else
                        {
                            System.out.println("UNEXPECTED MESSAGE: " + strMessage);
                        }
                            
                        try
                        {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    catch (SocketTimeoutException ex)
                    {
                        numTimeouts++; // increase the number of timeouts by 1
                        System.out.println("Client time-out - " + numTimeouts);
                    }
                    catch (IOException ex)
                    {
                        System.out.println("IO-exception");
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        
        listening.start();
        
        // Pass token on to next user
        /*if (beginsWithToken)
        {
            try {
                XStream xstream = new XStream();
                Message sendMessage = new MessagePassToken();
                sendMessage.setFromClientId(id);
                System.out.println(xstream.toXML(sendMessage));
                byte[] sendBuffer = xstream.toXML(sendMessage).getBytes();
                //DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, InetAddress.getByName(outNeighbour.getAddress()), Client.INGAME_PORT);
                //socket.send(sendPacket);
            } catch (UnknownHostException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }*/
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

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
