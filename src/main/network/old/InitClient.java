/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.network.old;

import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class InitClient
{
    public final static int PORT = 13337;

    public static void main(String[] args) throws Exception
    {
            BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
            Client client = null;

            // Collect all data
            System.out.print("Host IP address: ");
            String ipAddress = userIn.readLine();
            System.out.print("TCP Port: ");
            int port = Integer.parseInt(userIn.readLine());
            System.out.print("Username: ");
            String name = userIn.readLine();

            // Listen to the socket
            Socket socket = new Socket(ipAddress, port);

            // Set the output stream of the socket
            DataOutputStream socketOut = new DataOutputStream(socket.getOutputStream());

            // Set the input stream of the socket
            BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send a request for a connection
            socketOut.writeBytes(InitServer.REQ_CONNECTION + "\n" + name + "\n");
            socketOut.flush();

            String message = new String();
            while (true)
            {
                    // Reset the message
                    message = "";

                    // Send the output when the output stream is not empty
                    if (userIn.ready())
                    {
                            String userInMsg = userIn.readLine();

                            if (InitServer.START.equals(userInMsg))
                            {
                                    socketOut.writeBytes(InitServer.START + "\n");
                                    socketOut.flush();
                            }
                            else
                            {
                                    socketOut.writeBytes(userIn.readLine() + "\n");
                                    socketOut.flush();
                            }
                    }

                    // If the input is not empty set the message
                    if (socketIn.ready())
                    {
                            message = socketIn.readLine();
                    }

                    // If the input is not empty and the message is a REQ_ACCEPTED then
                    // confirm it
                    if (message.equals(InitServer.REQ_ACCEPTED))
                    {
                            System.out.println("Server has accepted the connection");
                            client = new Client(name, InetAddress.getByName(socketIn.readLine().replaceFirst("/", "")));
                    }

                    if (message.equals(InitServer.GENERATE_RING))
                    {
                            System.out.println("Let's Generate the ring...");
                            if (client != null)
                            {
                                    Client neighborIn = new Client(socketIn.readLine(), InetAddress.getByName(socketIn.readLine().replaceFirst("/", "")));
                                    Client neighborOut = new Client(socketIn.readLine(), InetAddress.getByName(socketIn.readLine().replaceFirst("/", "")));
                                    client.setNeighborIn(neighborIn);
                                    client.setNeighborOut(neighborOut);
                                    System.out.println("Neighbors added!");
                            }
                            else
                            {
                                    System.out.println("Cannot generate ring because the connection is not accepted");
                            }
                    }

                    // If the message is not REQ_ACCEPTED print the message
                    if (!message.equals(InitServer.REQ_ACCEPTED) || !message.equals(InitServer.GENERATE_RING))
                    {
                            if (message != "")
                            {
                                    System.out.println(message);
                            }

                    }


                    if (client != null && client.getNeighborIn() != null && client.getNeighborOut() != null)
                    {
                            System.out.println("In: " + client.getNeighborIn().getName());
                            System.out.println("Out: " + client.getNeighborOut().getName());
                    }

                    // Reduce CPU usage with a sleep
                    Thread.sleep(10000);
            }
    }
}