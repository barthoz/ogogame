/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.network.message;

import main.network.Client;

/**
 *
 * @author Daniel
 */
public class MessageJoinApproved extends Message
{
    /**
     * Properties
     */
    
    private Client client;
    
    /**
     * Constructor
     */
    
    public MessageJoinApproved() { }
    
    public MessageJoinApproved(Client client)
    {
        this.client = client;
    }
}
