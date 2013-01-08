/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.game.GameCredentials;

public class InitServer
{

	// Constants
	public final static String REQ_CONNECTION = "REQ_CONN";
	public final static String REQ_ACCEPTED = "REQ_ACC";
	public final static String REQ_DENIED = "REQ_DEN";
	public final static String GENERATE_RING = "GEN_RING";
	public final static String START = "START";

	// Arraylist of clients
	private ArrayList<Client> clients;

	// Arraylist of Sockets
	private ArrayList<Socket> sockets;

	// Arraylist of socket outputs
	private ArrayList<DataOutputStream> socketsOut;

	// Arraylist of socket inputs
	private ArrayList<BufferedReader> socketsIn;

	// Serversocket
	private ServerSocket serverSocket;

	/**
	 * Constructor
	 */
	public InitServer(ServerSocket serverSocket)
	{
		this.clients = new ArrayList<Client>();
		this.sockets = new ArrayList<Socket>();
		this.socketsOut = new ArrayList<DataOutputStream>();
		this.socketsIn = new ArrayList<BufferedReader>();
		this.serverSocket = serverSocket;
	}

	/**
	 * Add a client
	 * 
	 * @param client
	 */
	public synchronized void addClient(Client client)
	{
		clients.add(client);
	}

	public synchronized void addSocket(Socket socket)
	{
		sockets.add(socket);
	}

	public synchronized void addSocketOut(DataOutputStream socketOut)
	{
		socketsOut.add(socketOut);
	}

	public synchronized void addSocketIn(BufferedReader socketIn)
	{
		socketsIn.add(socketIn);
	}

	public ArrayList<Client> getClients()
	{
		return this.clients;
	}

	public ServerSocket getServerSocket()
	{
		return this.serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket)
	{
		this.serverSocket = serverSocket;
	}

	public void generateRing() throws IOException
	{
		System.out.println("Receiving generate ring message");
		for (Client client : this.clients)
		{
			int index = clients.indexOf(client);
			if (index != 0 && index != (clients.size() - 1))
			{
				DataOutputStream socketOut = this.socketsOut.get(index);
				Client clientOut = this.clients.get(index - 1);
				Client clientIn = this.clients.get(index + 1);
				socketOut.writeBytes(InitServer.GENERATE_RING + "\n");
				socketOut.writeBytes(clientIn.getName() + "\n" + clientIn.getIpAddress().toString() + "\n");
				socketOut.writeBytes(clientOut.getName() + "\n" + clientOut.getIpAddress().toString() + "\n");
				socketOut.flush();
			}
			else if (index == 0)
			{
				DataOutputStream socketOut = this.socketsOut.get(index);
				Client clientOut = this.clients.get(this.clients.size() - 1);
				Client clientIn = this.clients.get(index + 1);
				socketOut.writeBytes(InitServer.GENERATE_RING + "\n");
				socketOut.writeBytes(clientIn.getName() + "\n" + clientIn.getIpAddress().toString() + "\n");
				socketOut.writeBytes(clientOut.getName() + "\n" + clientOut.getIpAddress().toString() + "\n");
				socketOut.flush();
			}
			else
			{
				DataOutputStream socketOut = this.socketsOut.get(index);
				Client clientOut = this.clients.get(index - 1);
				Client clientIn = this.clients.get(0);
				socketOut.writeBytes(InitServer.GENERATE_RING + "\n");
				socketOut.writeBytes(clientIn.getName() + "\n" + clientIn.getIpAddress().toString() + "\n");
				socketOut.writeBytes(clientOut.getName() + "\n" + clientOut.getIpAddress().toString() + "\n");
				socketOut.flush();
			}
		}
	}

	public void startRing()
	{

	}

	public void sendMessageToAllClients(String message) throws IOException
	{
		for (DataOutputStream socketOut : this.socketsOut)
		{
			socketOut.writeBytes(message + "\n");
			socketOut.flush();
		}
	}

	public String getMessagesFromAllClients() throws IOException
	{
		String message = "";
		for (BufferedReader socketIn : this.socketsIn)
		{
			if (socketIn.ready())
			{
				message = message + socketIn.readLine();

			}
		}

		return message;
	}

	public String getClientsOverview()
	{
		String overview = "";
		int counter = 0;
		for (Client client : this.getClients())
		{
			overview = overview + "Client " + counter + " : " + client.getName() + "\n";
		}
		return overview;
	}

	public static void main(String[] args) throws Exception
	{
		// Get port number
		System.out.print("PORT: ");
		BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
		int port = Integer.parseInt(userIn.readLine());
		System.out.println("Server is started on port: " + port);

		// Initiate sockets
		ServerSocket serverSocket = new ServerSocket(port);
		InitServer server = new InitServer(serverSocket);
		InitServerThread initServerThread = new InitServerThread(server);
		for (int i = 0; i <= 6; i++)
		{
			Thread thread = new Thread(initServerThread, "Thread" + i);
			thread.start();
		}

		while (true)
		{

			// System.out.println(server.getClientsOverview());

			// If the socket input is not empty
			String input = server.getMessagesFromAllClients();
			if (input != "")
			{
				if (input.equals(InitServer.START))
				{
					System.out.println("Send generate ring to clients");
					server.generateRing();
					System.out.println("Generation complete");
					// server.startRing();
				}
				else
				{
					System.out.println(input);
				}
			}

			// If the user input is not empty
			if (userIn.ready())
			{
				String message = userIn.readLine();
				server.sendMessageToAllClients(message);
			}

			// Reduce CPU usage with a sleep
			Thread.sleep(10);
		}

	}
        
        public void broadcastGame(GameCredentials gameCredentials)
        {
            try
            {
                InetAddress localHost = Inet4Address.getLocalHost();
                NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
                System.out.println();
                String[] subnet = networkInterface.getInterfaceAddresses().get(0).getAddress().toString().replaceFirst("/", "").split("\\.");

                final String beginIp = subnet[0] + "." + subnet[1] + "." + subnet[2] + ".";
                
                Thread thread = new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        String ip;
                        
                        while (true)
                        {
                            for (int i = 1; i <= 255; i++)
                            {
                                ip = beginIp + i;
                                System.out.println(ip);
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ex) {
                                
                            }
                        }
                    }
                });
                
                thread.start();
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
