/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.network;

import main.network.old.InitClient;
import com.thoughtworks.xstream.XStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.game.GameCredentials;
import main.network.message.*;

/**
 *
 * @author s116861
 */
public class ServerBroadcaster implements Runnable
{
    /**
     * Properties
     */
    
    private boolean started = true;
    private GameCredentials gameCredentials;
    
    /**
     * Constructor
     */
    
    public ServerBroadcaster(GameCredentials gameCredentials)
    {
        this.gameCredentials = gameCredentials;
    }
    
    /**
     * Business logic
     */
    
    public void stopBroadcasting()
    {
        this.started = false;
    }
    
    public void run()
    {
        try
        {
            XStream xstream = new XStream();
            String ip;

            InetAddress localHost = Inet4Address.getLocalHost();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
            String[] subnet = networkInterface.getInterfaceAddresses().get(0).getAddress().toString().replaceFirst("/", "").split("\\.");

            String beginIp = subnet[0] + "." + subnet[1] + "." + subnet[2] + ".";
            
            while (true && this.started)
            {
                try
                {
                    MessageGameCredentials msgGameCredentials = new MessageGameCredentials(this.gameCredentials);
                    String strMsg = xstream.toXML(msgGameCredentials);

                    byte[] message = strMsg.getBytes();

                    DatagramSocket socket = new DatagramSocket();

                    for (int i = 1; i <= 255; i++)
                    {
                        ip = beginIp + i;
                        InetAddress address = InetAddress.getByName(ip.toString());
                        DatagramPacket packet = new DatagramPacket(message, message.length, address, InitClient.PORT);

                        socket.send(packet);
                    }

                    socket.close();

                    try
                    {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException ex) { }
                }
                catch (Exception e) { }
            }
        }
        catch (SocketException ex)
        {
            Logger.getLogger(InitialServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (UnknownHostException ex)
        {
            Logger.getLogger(InitialServer.class.getName()).log(Level.SEVERE, null, ex);
        }   
    }
}
