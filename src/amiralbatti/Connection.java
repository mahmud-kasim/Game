package AmiralBatti;

import java.net.*;
import java.io.*;

/**
 * 
 *
 * @author       Mahmud Kasım
 
 */
public class Connection implements Runnable
{
	private String IPAddress;
	private int port;
	private boolean connected = false;
	private Socket link;
	private ObjectInputStream input;
	private ObjectOutputStream output;

	private volatile Object objectQueue[] = new Object[0];

	/**
	* Overloaded as the server connection, taking the port to listen to,
	* as well as a timeout in seconds.
	* @param        portNumber   the port this server connection listens to
	* @param        seconds   the timeout value in seconds
	* @exception    SocketException when the connection times out on connect
	*/
	public Connection(int portNumber, int seconds) throws SocketException
	{
		port = portNumber;
		try
		{
			ServerSocket socket = new ServerSocket(port, 1);
			socket.setSoTimeout(seconds*1000);
			link = socket.accept();
			output = new ObjectOutputStream( link.getOutputStream() );
			output.flush();

			input = new ObjectInputStream( link.getInputStream() );

			IPAddress = link.getInetAddress().toString();
			connected = true;
			Thread go = new Thread(this, "ObjectQueue");
			go.setDaemon(true);
			go.start();
		}
		catch (IOException e)
		{
			e.printStackTrace(); //code to handle error here
		}
	}

	/**
	* Overloaded as the server connection, taking the port to listen to.
	* @param        portNumber   the port this server connection listens to
	* @exception    SocketException should never be thrown.
	*/
	public Connection(int portNumber) throws SocketException
	{
		this(portNumber, 0);
	}

	/**
	* Connection overloaded as the client connection. An instance of the connection object must first
	* be instanced as a server connection on the address that is specified to this client object.
	* @param        address   the address of the server to connect to.
	* @param        portNumber   the port this server connection listens to
	* @exception    UnknownHostException if no server is listening on the specified port
	*/
	public Connection(String address, int portNumber) throws UnknownHostException
	{
		IPAddress = address;
		port = portNumber;
		try
		{
			link = new Socket(IPAddress, port);
			output = new ObjectOutputStream( link.getOutputStream() );
			output.flush();

			input = new ObjectInputStream( link.getInputStream() );

			connected = true;
			Thread go = new Thread(this, "ObjectQueue");
			go.setDaemon(true);
			go.start();
		}
		catch (IOException e)
		{
			e.printStackTrace(); //Hatalar Burda !!!
		}
	}

	/**
	* Dİrekt olarak çağırılmıyor,Otomatik olarak thred 
	*/
	public void run()
	{
		while (connected)
		{
			try
			{
				Object temp[] = new Object[objectQueue.length+1];
				for (int i=0;i<objectQueue.length;i++) temp[i]=objectQueue[i];
				temp[temp.length-1] = input.readObject();
				objectQueue = temp;

				Thread.sleep(0);		//Düzgün gittiği sürece DOkunma )()
			}
			catch(IOException e)
			{
				e.printStackTrace();
				connected = false;
			}
			catch(ClassNotFoundException e)
			{
				e.printStackTrace();
				connected = false;
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	* iki taraf arasında bağlantı oldugunda (True) 
        * Bağlantı hala devam ediyorsa Saldırrrrr _?___ :))
	* @return       boolean
	*/
	public boolean established()
	{
		return connected;
	}

	/**
	* object döndürüyor sadece birtanesi ise 
	* @return       Object
	*/
	public Object getObject()
	{
		Object obj = null;

		if (objectQueue.length>0)
		{
			obj = objectQueue[0];

			Object temp[] = new Object[objectQueue.length-1];
			for (int i=0;i<temp.length;i++) temp[i]=objectQueue[i+1];
			objectQueue = temp;
		}
		return obj;
	}

	/**
	* 
	* @param       obj       
	*/
	public void sendObject(Object obj)
	{
		if (connected)
		{
			try
			{
				output.writeObject(obj);
				output.flush();
			}
			catch(IOException e)
			{
				e.printStackTrace();
				connected = false;
				//javax.swing.JOptionPane.showMessageDialog(null, "Disconnected");
			}
		}
	}

	/**
	* Henzü bağlanmadıysa sıfır(0)
	* @return       int
	*/
	public int getPort()   {   return port;   }

	/**
	* Returns the IP address of the connecting object.
	* @return       String
	*/
	public String getOtherIP()
	{
		return (connected) ? IPAddress : "Bagli degil";
	}

	/**
	* ip geri döndürüyor
        * bağlantı olduğunda
	* @return       String
	*/
	static public String getMyIP()
	{
		try
		{
			return InetAddress.getLocalHost().toString();
		}
		catch(UnknownHostException e)
		{
			return "Bilinmeyen Host";
		}
	}

	/**
	* Temizliyor Resource'u
	*/
	protected void finalize()
	{
		try
		{
			output.close();
			link.close();
			super.finalize();
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}
}