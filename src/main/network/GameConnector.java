/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.network;

import com.thoughtworks.xstream.XStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.lobby.Me;
import main.game.GameCredentials;
import main.game.action.Action;
import main.game.Player;

/**
 *
 * @author Daniel
 */
public class GameConnector
{
    /**
     * Properties
     */
    
    private List<Process> localGroup;
    
    private Process neighbouringProcess;
    
    /**
     * Constructor
     */
    
    public GameConnector()
    {
        this.localGroup = new ArrayList();
    }
    
    /**
     * Business logic
     */
    
    /**
     * Add a process to the local group.
     * 
     * @param process to add
     */
    public void addProcess(Process process)
    {
        this.localGroup.add(process);
    }
    
    /**
     * Remove a process from the local group
     * 
     * @param process to remove
     */
    public void removeProcess(Process process)
    {
        this.localGroup.remove(process);
    }
    
    public void sendGameActions(Me me)
    {
        
    }
    
    public void sendSetMoveDone(Me me)
    {
        
    }
    
    public Map<Player, List<Action>> receiveActions()
    {
        return null;
    }
    
    public void broadcastGame(GameCredentials gameCredentials)
    {
        
    }
    
    public void sendQuack()
    {
        
    }
    
    public void broadcastSetModeDone()
    {
        
    }
    
    public void broadcastGetModeDone()
    {
        
    }
}
