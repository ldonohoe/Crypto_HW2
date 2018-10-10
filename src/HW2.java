/**
 * Implementation of the Needham-Schroeder protocol, using Computational Diffie-Helman key exchange protocol
 * @author donohl
 *
 */
public class HW2 {

	private static String Ka;
	private static String Kb;
	//Generates a random integer key to be sent as the session key for bob and alice
	//Encrypts two packets A and B
	//Packet B is encrypted using Client Bs public key, and contains the session key
	//	as well as the identity of the second client
	//Packet A is encrypted with Client As public key, and contains As Nonce, the session key, 
	//	the identity of the other client, and Packet B
	private static String key_dist(String A, String B, int Na) {

		int key = (int)(Math.random() * 10000) % 1024;
		String keyText = Encrypt.binToString(key, 10);
		String packetB = keyText + "||" + A;
		packetB = Encrypt.encrypt_file(packetB, Kb);
		
		String packetA = keyText + "||" + B + "||" + Na + "||" + packetB;
		packetA = Encrypt.encrypt_file(packetA, Ka);
		
		return packetA;
	}
	
	//Generates the private keys for Alice and Bob using 
	// the Computational Diffie-Helman key exchange protocol
	private static int gen_key() {
		
		
		return 0;
	}
	
	public static void main(String[] args) {
		//This will be timestamp
		System.out.println(java.time.LocalTime.now().toString());
		//Identity of Alice and Bob
		String A = "Alice";
		String B = "Bob";
		
		//Nonce for Alice and Bob respectively.
		int Na = (int)(Math.random() * 10000) % 1024;
		int Nb = (int)(Math.random() * 10000) % 1024;
		
		
		
		
		
		
	}

}
