/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.game;

/**
 *
 * @author Daniel
 */
public class GameCredentials
{

    /**
     * Properties
     */
    private int gameId;
    private String gameName;
    private String initialHostUsername;
    private String initialHostIp;

    /**
     * Constructor
     */
    
    // XStream constructor
    public GameCredentials() { }
    
    public GameCredentials(int gameId, String gameName, String initialHostUsername, String initialHostIp)
    {
        this.gameId = gameId;
        this.gameName = gameName;
        this.initialHostUsername = initialHostUsername;
        this.initialHostIp = initialHostIp;
    }

    /**
     * Business logic
     */
    
    @Override
    public String toString()
    {
        return this.gameName + " (" + this.initialHostIp + ") - " + this.initialHostUsername;
    }
    
    /**
     * Getters & Setters
     */
    public int getGameId()
    {
        return gameId;
    }

    public void setGameId(int gameId)
    {
        this.gameId = gameId;
    }

    public String getGameName()
    {
        return gameName;
    }

    public void setGameName(String gameName)
    {
        this.gameName = gameName;
    }

    public String getInitialHostUsername()
    {
        return initialHostUsername;
    }

    public void setInitialHostUsername(String initialHostUsername)
    {
        this.initialHostUsername = initialHostUsername;
    }

    public String getInitialHostIp() {
        return initialHostIp;
    }

    public void setInitialHostIp(String initialHostIp) {
        this.initialHostIp = initialHostIp;
    }
    
}
