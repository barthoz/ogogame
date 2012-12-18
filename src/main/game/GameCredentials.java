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
    
    /**
     * Constructor
     */
    
    public GameCredentials(int gameId, String gameName, String initialHostUsername)
    {
        this.gameId = gameId;
        this.gameName = gameName;
        this.initialHostUsername = initialHostUsername;
    }
    
    /**
     * Getters & Setters
     */
    
    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getInitialHostUsername() {
        return initialHostUsername;
    }

    public void setInitialHostUsername(String initialHostUsername) {
        this.initialHostUsername = initialHostUsername;
    }
}
