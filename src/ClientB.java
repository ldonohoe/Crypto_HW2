import java.io.*;
import java.net.*;

public class ClientB {
	public String A, B, Na, Nb;
	public static String KbString;
	public static String pack;
	final static int P = 997;
	final static int BASE = 9;
	static int keyPort = 9878;
	static int aPort = 9880;
	static int Kb;
	
	public static void main(String[] args) throws IOException
	{
		//Begin with communications with KDH to obtain a's private key
				Socket BtoK = new Socket("127.0.0.1", keyPort);
				DataOutputStream output = new DataOutputStream(BtoK.getOutputStream());
				DataInputStream input = new DataInputStream(new BufferedInputStream(BtoK.getInputStream()));
				int a = (int)(Math.random() * 997);
				int baseToA = (int)(Math.pow(BASE,  a)) % P;
				
				//Send result to kdc, and recieve their result
				output.write(baseToA);
				int baseToB = input.read();
				
				//Now calculate the private shared key
				int baseToAB = (int)Math.pow(baseToB, a) % P;
				output.write(baseToAB);
				
				int kdcResult = input.read();
				
				//Check that the Diffie-Helman succeeded
				if (baseToAB == kdcResult) {
					System.out.println("Kb successfully computed");
					Kb = baseToAB;
					KbString = Integer.toBinaryString(Kb);
				}
				//No longer need this socket
				BtoK.close();
				
				//Start a server to wait for A to send the key packet
				
				ServerSocket B = new ServerSocket(aPort);
				Socket A = B.accept();
				
				DataOutputStream send = new DataOutputStream(A.getOutputStream());
				DataInputStream recieve = new DataInputStream(new BufferedInputStream(A.getInputStream()));
				//Recieve the key packet from Alice
				String bPacket = recieve.readUTF();
				String key = Encrypt.decrypt_file(bPacket, KbString);
				
				//Parse out data from key
				int i = key.indexOf("||");
				String Ks = key.substring(0,i);
				key = key.substring(i+2);
				i = key.indexOf("||");
				String IDa = key.substring(0, i);
				key = key.substring(i);
				//Remainder of key should be timeStamp
				
				System.out.println("Ks is " + Ks + "\n IDa is " + IDa + "\n time is " + key);
				
				//Finally, Send the session key back to A to confirm
				send.writeUTF(Ks);
				
	}

}
