/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.network;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author s116861
 */
public class InitialServer
{
    /**
     * Properties
     */
    
    /**
     * Constructor
     */
    
    public InitialServer()
    {
        
    }
    
    /**
     * Business logic
     */
    
    public void broadcastGame()
    {
        try
        {
            InetAddress localHost = Inet4Address.getLocalHost();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
            System.out.println();
            String[] subnet = networkInterface.getInterfaceAddresses().get(0).getAddress().toString().replaceFirst("/", "").split("\\.");
            
            String beginIp = subnet[0] + "." + subnet[1] + "." + subnet[2] + ".";
            String ip;
            
            for (int i = 1; i <= 255; i++)
            {
                ip = beginIp + i;
                System.out.println(ip);
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
    
    
    /**
     * Getters & Setters
     */
}
