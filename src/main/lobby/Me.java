/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.lobby;

import main.game.Game;
import main.game.Player;

/**
 *
 * @author Daniel
 */
public class Me
{
    /**
     * Properties
     */
    
    private Game currentGame;
    private Player currentPlayer;
    
    /**
     * Constructor
     */
    
    public Me(Game currentGame, Player currentPlayer)
    {
        this.currentGame = currentGame;
        this.currentPlayer = currentPlayer;
    }
    
    /**
     * Getters & Setters
     */
    
    public Game getCurrentGame()
    {
        return currentGame;
    }

    public void setCurrentGame(Game currentGame)
    {
        this.currentGame = currentGame;
    }

    public Player getCurrentPlayer()
    {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer)
    {
        this.currentPlayer = currentPlayer;
    }
}
