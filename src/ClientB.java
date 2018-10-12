import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientB {
	public String A, B, Na, Nb;
	public static String KbString;
	public static String pack;
	final static int P = 997;
	final static int BASE = 9;
	static int port = 9877;
	static int Kb;
	
	public static void main(String[] args) throws IOException
	{
		//Begin with communications with KDH to obtain a's private key
				Socket BtoK = new Socket("127.0.0.1", port);
				PrintWriter output = new PrintWriter(BtoK.getOutputStream(), true);
				BufferedReader input = new BufferedReader(new InputStreamReader(BtoK.getInputStream()));
				
				int a = (int)(Math.random() * 997);
				int baseToA = (int)(Math.pow(BASE,  a)) % P;
				
				//Send result to kdc, and recieve their result
				output.print(baseToA);
				int baseToB = input.read();
				
				//Now calculate the private shared key
				int baseToAB = (int)Math.pow(baseToB, a) % P;
				output.print(baseToAB);
				
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
				
				ServerSocket B = new ServerSocket(9877);
				Socket A = B.accept();
				
				PrintWriter send = new PrintWriter(A.getOutputStream(), true);
				BufferedReader recieve = new BufferedReader(new InputStreamReader(A.getInputStream()));
				
				//Recieve the key packet from Alice
				String bPacket = recieve.readLine();
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
				send.print(Ks);
				
	}

}
