/**
 * Implementation of the Needham-Schroeder protocol, using Computational Diffie-Helman key exchange protocol
 * @author donohl
 *
 */
public class HW2 {

	private static String Ka;
	private static String Kb;
	
	public static String[] parseString(String inPack) {
		String A, B, Na;
		int i = inPack.indexOf("||");
		A = inPack.substring(0,  i);
		inPack = inPack.substring(i+2);
		i = inPack.indexOf("||");
		B = inPack.substring(0, i);
		inPack = inPack.substring(i+2);
		Na = inPack;
		String[] outPack = new String[3];
		outPack[0] = A;
		outPack[1] = B;
		outPack[2] = Na;
		return outPack;
		
	}
	//Generates a random integer key to be sent as the session key for bob and alice
	//Encrypts two packets A and B
	//Packet B is encrypted using Client Bs public key, and contains the session key
	//	as well as the identity of the second client
	//Packet A is encrypted with Client As public key, and contains As Nonce, the session key, 
	//	the identity of the other client, and Packet B
	public static String key_dist(String inPack) {
		gen_key();
		String A = null, B=null;
		int Na=0;
		String[] newPack = parseString(inPack);
		A = newPack[0];
		B = newPack[1];
		Na = Integer.parseInt(newPack[2]);
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
	private static void gen_key() {
		int shared = 23;
		int base = 5;
		
		int aSecret = 3;
		int kdcSecret = 4;
		
		int mod = (int)Math.pow(base, aSecret) % shared;
		int mod2 = (int)Math.pow(base, kdcSecret) % shared;
		
		assert((int)Math.pow(mod2, aSecret)%shared == (int)Math.pow(mod2,  kdcSecret)%shared);
		
		Ka = Encrypt.binToString((int)Math.pow(mod2, aSecret)%shared, 10);
		
		//Now repeat for B
		shared = 37;
		base = 7;
		
		int bSecret = 5;
		kdcSecret = 6;
		
		mod = (int)Math.pow(base, bSecret) % shared;
		mod2 = (int)Math.pow(base, kdcSecret) % shared;
		
		assert((int)Math.pow(mod2, bSecret)%shared == (int)Math.pow(mod2,  kdcSecret)%shared);
		
		Kb = Encrypt.binToString((int)Math.pow(mod2, bSecret)%shared, 10);
			
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
