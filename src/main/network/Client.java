/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.network;

import java.net.InetAddress;
import java.net.Socket;

public class Client implements Runnable
{
	public final static String RING_MSG = "RING_MSG";
	
	private InetAddress ipAddress;
	private String name;
	private Client neighborIn;
	private Client neighborOut;
	private boolean hasToken;
	private String criticalSection;
	private Socket socketIn;
	private Socket socketOut;
	
	public Client(String name, InetAddress ipAddress){
		this.name = name;
		this.ipAddress = ipAddress;
		hasToken = false;
	}
	
	public InetAddress getIpAddress()
	{
		return ipAddress;
	}
	public void setIpAddress(InetAddress ipAddress)
	{
		this.ipAddress = ipAddress;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void updateCriticalSection(){
		
	}
	
	public void assignToken(){
		this.hasToken = true;
	}
	
	public void passToken(){
		this.hasToken = false;
	}
	
	public Client getNeighborIn()
	{
		return neighborIn;
	}

	public void setNeighborIn(Client neighborIn)
	{
		this.neighborIn = neighborIn;
	}

	public Client getNeighborOut()
	{
		return neighborOut;
	}

	public void setNeighborOut(Client neighborOut)
	{
		this.neighborOut = neighborOut;
	}

	public String getCriticalSection()
	{
		return criticalSection;
	}

	public void setCriticalSection(String criticalSection)
	{
		this.criticalSection = criticalSection;
	}
	
	@Override
	public void run(){
		
	}
	
	
}