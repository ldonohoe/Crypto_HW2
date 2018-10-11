import java.net.*;
import java.io.*;

public class Client {
	// initialize socket and input output streams
	private Socket socket = null;
	private DataInputStream input = null;
	private DataOutputStream out = null;
	private DataInputStream recieve = null;

	public String A, B, Na, Nb;
	public static String pack;

	// constructor to put ip address and port
	@SuppressWarnings("deprecation")
	public Client(String address, int port) {
		// establish a connection
		try {
			socket = new Socket(address, port);
			System.out.println("Connected");

			// takes input from terminal
			input = new DataInputStream(System.in);

			// sends output to the socket
			out = new DataOutputStream(socket.getOutputStream());

			recieve = new DataInputStream(socket.getInputStream());
		} catch (UnknownHostException u) {
			System.out.println(u);
		} catch (IOException i) {
			System.out.println(i);
		}

		// string to read message from input
		String line = "";

		// keep reading until "Recieved" is input
		while (!line.equals("End")) {
			try {
				line = input.readLine();
				out.writeUTF(line);

			} catch (IOException i) {
				System.out.println(i);
				break;
			}
		}

		// close the connection
		try {
			pack = recieve.readUTF();
			input.close();
			out.close();
			recieve.close();
			socket.close();
		} catch (IOException i) {
			System.out.println(i);
		}
	}

	public static void main(String args[]) {
		// Initial Connection to contact the KDC
		Client client = new Client("127.0.0.1", 9877);
		
		System.out.println("pack is " + pack);
	}
}