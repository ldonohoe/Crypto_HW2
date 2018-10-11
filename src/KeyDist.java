import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class KeyDist {

	//initialize socket and input stream 
    private Socket          	socket   = null; 
    private ServerSocket    	server   = null; 
    private DataInputStream 	in       = null; 
    private DataOutputStream 	out 	 = null;
    
    // constructor with port 
    public KeyDist(int port) 
    { 
        // starts server and waits for a connection 
        try
        { 
            server = new ServerSocket(port); 
            System.out.println("Server started"); 
  
            System.out.println("Waiting for a client ..."); 
  
            socket = server.accept(); 
            System.out.println("Client accepted"); 
  
            // takes input from the client socket 
            in = new DataInputStream( 
                new BufferedInputStream(socket.getInputStream())); 
           
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            String line = ""; 
            String packet;
            // reads message from client until "Over" is sent 
            System.out.println("Lookinng for input");
            while (!line.equals("End")) 
            { 
                try
                { 
                    line = in.readUTF(); 
                    System.out.println(line); 
                    if (line.equalsIgnoreCase("end"))
                    	break;
                    packet = HW2.key_dist(line);
                    System.out.println(packet);
                    out.writeUTF(packet);
  
                } 
                catch(IOException i) 
                { 
                    System.out.println(i); 
                    break;
                } 
            } 
            System.out.println("Closing connection"); 
  
            // close connection 
            socket.close(); 
            in.close(); 
        } 
        catch(IOException i) 
        { 
            System.out.println(i); 
        } 
    } 
  
    public static void main(String args[]) 
    { 
        KeyDist server = new KeyDist(9877); 
    } 
}

