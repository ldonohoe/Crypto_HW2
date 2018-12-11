import java.io.*;
import java.net.*;

public class ClientA {
	public static String A;
	public static String B;
	public static String Na;
	public static String KaString;
	public String Nb;
	public static String pack;
	final static int P = 997;
	final static int BASE = 9;
	static int keyPort = 9877;
	static int bPort = 9880;
	static int keyPort2 = 9879;
	static int Ka;
	

	public static void main(String[] args) throws IOException
	{
		
		//Begin with communications with KDH to obtain a's private key
		Socket AtoK = new Socket("127.0.0.1", keyPort);
		DataOutputStream output = new DataOutputStream(AtoK.getOutputStream());
		DataInputStream input = new DataInputStream(new BufferedInputStream(AtoK.getInputStream()));
		 
		int a = (int)(Math.random() * P);
		int baseToA = (int)(Math.pow(BASE,  a)) % P;
		
		System.out.println("Sending 1");
		//Send result to kdc, and recieve their result

		output.write(baseToA);
		System.out.println("Sent");

		System.out.println("Reading 1");
		int baseToB = input.read();// = input.read();
		System.out.println("Read");

		//Now calculate the private shared key
		int baseToAB = (int)Math.pow(baseToB, a) % P;
		output.write(baseToAB);
		
		int kdcResult = input.read();
		
		//Check that the Diffie-Helman succeeded
		if (baseToAB == kdcResult) {
			System.out.println("Ka successfully computed");
			Ka = baseToAB;
			KaString = Integer.toBinaryString(Ka);
			
		}
		//No longer need this socket
		AtoK.close();
		
		//Now prepare for obtaining session key between A and B
		// Need to generate : Nonce1, ID for A and B
		//Nonce will be a randomm number between 0 and 2^16
		int N1 = (int)Math.random() * Integer.MAX_VALUE/2;
		int idA = (int)Math.random() * 1024;
		int idB = (int)Math.random() * 1024;
		
		//Convert to Strings
		A = Integer.toBinaryString(idA);
		B = Integer.toBinaryString(idB);
		Na = Integer.toBinaryString(N1);
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){

		}
		Socket AtoK2 = new Socket("127.0.0.1", keyPort2);
		DataOutputStream output2 = new DataOutputStream(AtoK2.getOutputStream());
		DataInputStream input2 = new DataInputStream(new BufferedInputStream(AtoK2.getInputStream()));
			
		String sendPacket =  A + "||" + B + "||" + Na;
		output2.writeUTF(sendPacket);
		
		String keyPacket = input2.readUTF();
		
		//Now that we have this packet, we can begin communicating with Bob.
		// After breaking it apart and decrypting it of course
		//E[Ks|||IDb||N1||E[Ks||IDa]]
		System.out.println(KaString);
		String decryptKey = Encrypt.decrypt_file(keyPacket, KaString);
		
		System.out.println(decryptKey);
		//Parse the decrypted text into the individual pieces of data
		int i = decryptKey.indexOf("||"); // Ks|||IDb||N1||E[Ks||IDa]
		String Ks = decryptKey.substring(0, i); // ||IDb||Na||... 
		decryptKey = decryptKey.substring(i+2); // IDb||Na...
		i = decryptKey.indexOf("||");
		String bID = decryptKey.substring(0, i); // ||Na...
		decryptKey = decryptKey.substring(i+2); // Na...
		i = decryptKey.indexOf("||");
		String timeStamp = decryptKey.substring(0, i);
		decryptKey = decryptKey.substring(i+2); // E[Ks||IDa\
		
		//Error Checking if the packet is correctly decrypted
		System.out.println("Ks is " + Ks + "\n bID is " + bID + "\n time is " + timeStamp);
		
		//Remaining string is to send to B
		Socket AtoB = new Socket("127.0.0.1", bPort);
		DataOutputStream toBob = new DataOutputStream(AtoB.getOutputStream());
		DataInputStream fromBob = new DataInputStream(new BufferedInputStream(AtoB.getInputStream()));
		
		toBob.writeUTF(decryptKey);
		
		String bobKey = fromBob.readUTF();
		
		if (bobKey.equals(Ks)) {
			System.out.println("Securely distributed key, can now communicate with securenessity");
		}
		
		
		
		System.out.println("pack is " + pack);
	}
}
