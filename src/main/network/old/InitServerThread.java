/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.network.old;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class InitServerThread implements Runnable
{

	InitServer server;
	BufferedReader userIn;

	public InitServerThread(InitServer server)
	{
		this.server = server;
	}

	@Override
	public void run()
	{
		try
		{
			// Accepted connection
			Socket socket = server.getServerSocket().accept();
			System.out.println("Accepted Connection!");
			server.addSocket(socket);

			// Set the input stream of the socket
			BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			server.addSocketIn(socketIn);

			// Set the output stream of the socket
			DataOutputStream socketOut = new DataOutputStream(socket.getOutputStream());
			server.addSocketOut(socketOut);

			// Oke
			System.out.println("bliep bliep bliep");

			// Read input
			String input = socketIn.readLine();

			// If the input is equal to REQ_CONNECTION
			if (input.equals(InitServer.REQ_CONNECTION))
			{
				// Add client and send confirmation to client
				Client client = new Client(socketIn.readLine(), socket.getInetAddress());
				server.addClient(client);
				System.out.println(client.getName() + " " + client.getIpAddress());
				socketOut.writeBytes(InitServer.REQ_ACCEPTED + "\n");
				socketOut.writeBytes(client.getIpAddress().toString() + "\n");
				socketOut.flush();
				System.out.println("Client " + client.getName() + " is connected");
			}

		}
		catch (Exception e)
		{
			System.err.println(e);
		}
	}
}