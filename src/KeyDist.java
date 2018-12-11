import java.io.*;
import java.net.*;

public class KeyDist {

	public static String A;
	public static String B;
	public static String Na;
	public static String KaString, KbString;
	public String Nb;
	public static String pack;
	final static int P = 997;
	final static int BASE = 9;
	static int aPort = 9877;
    static int bPort = 9878;
    static int aPort2 = 9879;
	static int Ka, Kb; 
    
    public static void main(String[] args) throws IOException 
    { 
    	//Start server for sending data
    	// Send to A first, then B, then distribute Ks
    	ServerSocket KDCtoA = new ServerSocket(aPort);
    	Socket KtoA = KDCtoA.accept();
    	System.out.println("A Connected");
    	
    	DataOutputStream send = new DataOutputStream(KtoA.getOutputStream());
		DataInputStream recieve = new DataInputStream(new BufferedInputStream(KtoA.getInputStream()));
		
		//Compute the shared key with A, just as is done in A
    	int b = (int)Math.random() * P;
    	
    	System.out.println("Sending initial");
    	int baseToB = (int)Math.pow(BASE, b) % P;
    	send.write(baseToB);
    	
    	System.out.println("Recieve 1");
    	//A sends back their result
         
    	int baseToA = recieve.read();
    	int baseToAB = (int)Math.pow(baseToA, b) % P;
        System.out.println("Recieved " + baseToA);
    	
    	System.out.println("Sending 2");
    	send.write(baseToAB);
    	System.out.println("Recieving 2");
    	int baseToAB2 = recieve.read();
    	
    	//Check that both results match
    	if (baseToAB == baseToAB2) {
    		System.out.println("Confirmed");
    		Ka = baseToAB;
    		KaString = Integer.toBinaryString(Ka);
    	}
        System.out.println(KaString);
    	
    	KtoA.close();
        KDCtoA.close();
    	
    	//Repeat the exact same process with B...
        ServerSocket KDCtoB = new ServerSocket(bPort);
    	Socket KtoB = KDCtoB.accept();
    	System.out.println("B Connected");

    	
    	DataOutputStream sendB = new DataOutputStream(KtoB.getOutputStream());
        DataInputStream receiveB = new DataInputStream(new BufferedInputStream(KtoB.getInputStream()));
		//Compute the shared key with A, just as is done in A
    	int c = (int)Math.random() * P;
    	
    	int baseToC = (int)Math.pow(BASE, c) % P;
    	sendB.write(baseToC);
    	
    	//A sends back their result
    	int baseToD = receiveB.read();
    	int baseToCD = (int)Math.pow(baseToD, c) % P;
    	
    	sendB.write(baseToCD);
    	int baseToCD2 = receiveB.read();
    	
    	//Check that both results match
    	if (baseToCD == baseToCD2) {
    		System.out.println("Confirmed");
    		Kb = baseToCD;
    		KbString = Integer.toBinaryString(Kb);
    	}
    	
    	KtoB.close();
    	KDCtoB.close();

    	//Now that the keys have been shared, we can begin
    	//	Doing some key distribution
    	
        ServerSocket KDCtoA2 = new ServerSocket(aPort2);
    	Socket KeyToA = KDCtoA2.accept();
        System.out.println("Connected to A");
    	DataOutputStream sendA = new DataOutputStream(KeyToA.getOutputStream());
        DataInputStream recieveA = new DataInputStream(new BufferedInputStream(KeyToA.getInputStream()));
    	String packet = recieveA.readUTF();
    	String packetA = HW2.key_dist(packet, KaString, KbString);
    	
    	//Send encrypted packet of key stuff to A, and be done
    	sendA.writeUTF(packetA);
        System.out.println("Sent all to A");

    	KeyToA.close();
    	KDCtoA2.close();
    	
    	System.out.println("Keys successfully distributed!");
    } 
}

