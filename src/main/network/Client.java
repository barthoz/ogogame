/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.network;

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
    private int baseId;
    
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

    public int getBaseId() {
        return baseId;
    }

    public void setBaseId(int baseId) {
        this.baseId = baseId;
    }
}
