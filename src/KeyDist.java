import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class KeyDist {

	public static String A;
	public static String B;
	public static String Na;
	public static String KaString, KbString;
	public String Nb;
	public static String pack;
	final static int P = 997;
	final static int BASE = 9;
	static int port = 9877;
	static int Ka, Kb; 
    
    public static void main(String args[]) throws IOException 
    { 
    	//Start server for sending data
    	// Send to A first, then B, then distribute Ks
    	ServerSocket KDC = new ServerSocket(0);
    	Socket KtoA = KDC.accept();
    	
    	PrintWriter send = new PrintWriter(KtoA.getOutputStream(), true);
		BufferedReader recieve = new BufferedReader(new InputStreamReader(KtoA.getInputStream()));
		
		//Compute the shared key with A, just as is done in A
    	int b = (int)Math.random() * P;
    	
    	int baseToB = (int)Math.pow(BASE, b) % P;
    	send.print(baseToB);
    	
    	//A sends back their result
    	int baseToA = recieve.read();
    	int baseToAB = (int)Math.pow(baseToA, b) % P;
    	
    	send.print(baseToAB);
    	int baseToAB2 = recieve.read();
    	
    	//Check that both results match
    	if (baseToAB == baseToAB2) {
    		System.out.println("Confirmed");
    		Ka = baseToAB;
    		KaString = Integer.toBinaryString(Ka);
    	}
    	
    	KtoA.close();
    	
    	//Repeat the exact same process with B...
    	Socket KtoB = KDC.accept();
    	
    	PrintWriter sendB = new PrintWriter(KtoB.getOutputStream(), true);
		BufferedReader recieveB = new BufferedReader(new InputStreamReader(KtoB.getInputStream()));
		
		//Compute the shared key with A, just as is done in A
    	int c = (int)Math.random() * P;
    	
    	int baseToC = (int)Math.pow(BASE, c) % P;
    	sendB.print(baseToC);
    	
    	//A sends back their result
    	int baseToD = recieveB.read();
    	int baseToCD = (int)Math.pow(baseToD, c) % P;
    	
    	sendB.print(baseToCD);
    	int baseToCD2 = recieveB.read();
    	
    	//Check that both results match
    	if (baseToCD == baseToCD2) {
    		System.out.println("Confirmed");
    		Kb = baseToCD;
    		KbString = Integer.toBinaryString(Kb);
    	}
    	
    	KtoB.close();
    	
    	//Now that the keys have been shared, we can begin
    	//	Doing some key distribution
    	
    	
    	
    	System.out.println("Key Boi");
    } 
}

