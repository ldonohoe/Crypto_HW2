import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/*
 * Liam Donohoe
 * Implementation of Toy DES
 * September 20, 2018
 * Cryptography
 */

public class Encrypt {
	//Initialized arrays for permutation and sBoxes
	static int IP[] = { 2, 6, 3, 1, 4, 8, 5, 7 };
	static int IIP[] = { 4, 1, 3, 5, 7, 2, 8, 6 };
	static int P10[] = { 3, 5, 2, 7, 4, 10, 1, 9, 8, 6 };
	static int P8[] = { 6, 3, 7, 4, 8, 5, 10, 9 };
	static int P4[] = { 2, 4, 3, 1 };

	static int s0[][] = { { 1, 0, 3, 2 }, { 3, 2, 1, 0 }, { 0, 2, 1, 3 }, { 3, 1, 3, 2 } };
	static int s1[][] = { { 0, 1, 2, 3 }, { 2, 0, 1, 3 }, { 3, 0, 1, 0 }, { 2, 1, 0, 3 } };

	static int key1;
	static int key2;

	/**
	 * Simple helper function to pop n bits from right right
	 * of an integer, return them in an array.
	 * Used to facilitate conversion of integers to Strings of binary
	 * @param input; the integer being converted
	 * @param n; the number of bits to be removed
	 * @return an array of n bits popped from input
	 */
	public static int[] popBits(int input, int n) {
		int[] bits = new int[n];
		for (int i = 0; i < n; i++) {
			bits[i] = (1) & (input >> 0);
			input >>= 1;
		}
		return bits;
	}

	
	/**
	 * Simple helper function, Converts an integer to a String 
	 * @param input; the value to be converted to a string
	 * @param len; the length of the string to be created
	 * @return a String representing the integer in binary
	 */
	
	public static String binToString(int input, int len) {
		String binString = "";
		int[] bitVals = popBits(input, len);

		for (int i = len - 1; i >= 0; i--) {
			binString += bitVals[i];
		}
		return binString;
	}


	/**
	 * Reorders the bits from inPerm to the order defined in perms. 
	 * 
	 * @param inPerm; The integer to be permutated
	 * @param perms; The new positions for the bits in inPerm
	 * @param numBits; Number of bits in the inputted value.
	 * 				   This value is used for the case of reducing 10
	 * 				   To 8 bits using P8. 
	 * @return A permutation of inPerm based on perms
	 */
	public static int permutation(int inPerm, int[] perms, int numBits) {

		int bitLen = perms.length;
		char[] bitVals = binToString(inPerm, numBits).toCharArray();
		char[] permBits = new char[bitLen];

		for (int i = 0; i < bitLen; i++) {
			int newPos = perms[i];
			permBits[i] = bitVals[newPos - 1];
		}

		String permString = new String(permBits);
		int outPerm = Integer.parseInt(permString, 2);

		return outPerm;
	}

	/**
	 * Simple helper function for splitting 8 bits into 4 and 4
	 * @param pre; value to pull bits from
	 * @return integer value of 4 leftmost bits
	 * @requires pre is not > 8 bits
	 */
	public static int getLeft(int pre) {
		int l = pre >> 4;

		return l;

	}

	/**
	 * Simple helper function for splitting 8 bits into 4 and 4
	 * @param pre; value to pull bits from
	 * @return integer value of 4 rightmost bits
	 * @requires pre is not > 8 bits
	 */
	public static int getRight(int pre) {

		return (((1 << 4) - 1) & (pre));

	}
	
	/**
	 * Helper function to generate the two keys used in DES
	 * @param tenBit; The key value used to generate each key in DES
	 */
	public static void genKeys(int tenBit) {
		String tenString = new String(binToString(tenBit, 10));

		for (int i = 0; i < 3; i++) {
			String left = tenString.substring(0, 5);

			String right = tenString.substring(5);

			char l = left.charAt(0);
			char r = right.charAt(0);
			left = left.substring(1);
			right = right.substring(1);
			left += l;
			right += r;

			tenString = left + right;
			if (i == 0) {
				int shiftInt = Integer.parseInt(tenString, 2);
				key1 = permutation(shiftInt, P8, 10);
			}
		}
		int shiftInt = Integer.parseInt(tenString, 2);

		key2 = permutation(shiftInt, P8, 10);
	}
	
	/**
	 * Helper function to compute the s() value in the fiestel function
	 * @param input; 4 bit integer used to get result from sBox
	 * @param sBox; source for the value to be output
	 * @return position in sBox corresponding to [0]+[3], [1]+[2] in input binary
	 */
	public static int s(int input, int[][] sBox) {
		String fourBit = binToString(input, 4);
		String column = "" + fourBit.charAt(0) + fourBit.charAt(3);
		String row = "" + fourBit.charAt(1) + fourBit.charAt(2);

		int rowVal = Integer.parseInt(row, 2);
		int colVal = Integer.parseInt(column, 2);

		int out = sBox[rowVal][colVal];

		return out;
	}
	
	/**
	 * Helper function to calculate the Fiestel value
	 * @param input; right 4 bits of input
	 * @param key; key value for the iteration of F, either key1 or key2
	 * @return result of Fiestel function
	 */
	public static int fiest(int input, int key) {
		//Expand the input to eight bytes
		int inputToEight = fourToEight(input);

		//XOR input with the key
		int cross = inputToEight ^ key;
		
		//Split into two halves
		int left = getLeft(cross);
		int right = getRight(cross);

		//Get sBox values using s helper function
		left = s(left, s0);
		right = s(right, s1);

		//Convert left and right to strings to combine easily, then convert back to integer
		String lString = binToString(left, 2);
		String rString = binToString(right, 2);

		String four = lString + rString;
		int fourVal = Integer.parseInt(four, 2);
		
		//Perform P4 permutation and return
		fourVal = permutation(fourVal, P4, 4);

		return fourVal;

	}

	/**
	 * A simple helper function to expand the four bit input to eight bits
	 * @param input; fout bit integer to become eight bits
	 * @return expanded four bit to eight bit integer
	 * Uses the expansion of [1, 2, 3, 4] => [4, 1, 2, 3, 2, 3, 4, 1]
	 */
	public static int fourToEight(int input) {
		// 1234 => 41232341 or dabcbcda, reversed : adcbcbad
		int a, b, c, d;
		int[] bits = popBits(input, 4);
		a = bits[0];
		b = bits[1];
		c = bits[2];
		d = bits[3];
		String bitString = new String();
		bitString += "" + a + d + c + b + c + b + a + d;
		int eightBit = Integer.parseInt(bitString, 2);
		return eightBit;

	}

	/**
	 * 
	 * Performs a simplified DES algorithm to encrypt 8 bit text
	 * 
	 * @param plainText
	 *            input text to be encrypted, (8 bits at a time)
	 * @return encrypted plainText
	 */
	public static int encrypt(int plainText) {
		// Perform initial permutation on the plainText
		int initial = permutation(plainText, IP, 8);

		// Split into two halves of plaintext
		int left = getLeft(initial);
		int right = getRight(initial);

		// Fiestel Function with right half, and k1
		int f = fiest(right, key1);

		// XOR result of F with left,
		// original right becomes left2
		int right2 = left ^ f;
		int left2 = right;

		// Fiestel with new Right half, and k2
		int f2 = fiest(right2, key2);

		// XOR result of f2 with left2
		int left3 = left2 ^ f2;
		// Convert to strings to combine easily
		String rFinal = binToString(right2, 4);
		String lFinal = binToString(left3, 4);

		String fin = lFinal + rFinal;

		// Parse binary back into an integer
		int finBin = Integer.parseInt(fin, 2);

		// Inverse the initial permutation
		finBin = permutation(finBin, IIP, 8);

		// Return the encrypted value
		return finBin;
	}

	/**
	 * 
	 * Decrypts ciphertext back to the original message using simplified DES in
	 * reverse.
	 * 
	 * @param cipherText
	 *            input text to be decrypted (8 bits)
	 * @return deciphered cipherText
	 */
	private static int decrypt(int cipherText) {

		// Initial permutation of input
		int inverse = permutation(cipherText, IP, 8);

		// Split into left and right
		int left = getLeft(inverse);
		int right = getRight(inverse);

		// Perform fiestel function with right and k2
		int f2 = fiest(right, key2);

		// XOR result of Fiestel with left
		// Right becomes left2
		int right2 = f2 ^ left;
		int left2 = right;

		// Fiestel function with k1 and new right
		int f1 = fiest(right2, key1);

		// XOR result of fiestel with left2
		// right2 becomes left final
		int left3 = f1 ^ left2;

		// Convert to Strings to combine for simplicity
		String rFinal = binToString(right2, 4);
		String lFinal = binToString(left3, 4);

		String finVal = lFinal + rFinal;

		// Parse back into an integer
		int finBin = Integer.parseInt(finVal, 2);

		// Inverse permutation for final step
		finBin = permutation(finBin, IIP, 8);

		return finBin;

	}
	
	/**
	 * Helper function for converting strings to binary
	 * @param in; String of input to be converted
	 * @return A string of '1's and '0's representing the input strings bytes
	 */
	public static String convert_to_binary(String in) 
	{
		//Get the byte array
		byte[] bytes = in.getBytes();
		StringBuilder binary = new StringBuilder();
		for (byte b : bytes) {
			int val = b;
			for (int i = 0; i < 8; i++) {
				binary.append((val & 128) == 0 ? 0 : 1);
				val <<= 1;
			}
		}
		String out = binary.toString();
		return out;
		
	}

	/**
	 * Starter function to send 8 bit chunks into encryption
	 * @param fileIn; String of binary input
	 * @return Encrypted copy of fileIn to be sent on
	 */
	public static String encrypt_file(String fileIn, String keyText) {
		String cipherText = "";
		//Convert file data to binary
		String plainBinary = convert_to_binary(fileIn);

		// Convert raw key date into an integer for manipulation
		int key = Integer.parseInt(keyText, 2);
		//Pregenerate the two keys for later use
		genKeys(key);
		for (int i = 0; i < plainBinary.length()/8; i++) {

			//Parse out each 8 bit string from the main string
			String eightBit = plainBinary.substring(i*8, (i+1) * 8);
			
			// Convert byte to an integer for ease of manipulation
			int bit = Integer.parseInt(eightBit, 2);

			// Create the two keys that will be used for en/decryption

			// encrypt the input bits
			int cipher = encrypt(bit);
			// convert back to a string for printing
			char c = (char)cipher;
			cipherText+=c;

		}
		return cipherText;
	}
	
	/**
	 * Starter functon to send 8 bit chunks into decryption
	 * @param fileIn; String of binary input of encrypted data
	 * @return Decrypted plaintext of the fileIn
	 */
	public static String decrypt_file(String fileIn, String keyText) {
		String plainText = "";
		
		//Convert file data to binary
		String plainBinary = convert_to_binary(fileIn);
		
		// Convert raw key date into an integer for manipulation
		int key = Integer.parseInt(keyText, 2);
		//Pregenerate the two keys for later use
		genKeys(key);
		for (int i = 0; i < plainBinary.length()/8; i++) {

			//Parse out each 8 bit string from the main string
			String eightBit = plainBinary.substring(i*8, (i+1) * 8);
			
			// Convert byte to an integer for ease of manipulation
			int bit = Integer.parseInt(eightBit, 2);

			// Create the two keys that will be used for en/decryption

			// encrypt the input bits
			int cipher = decrypt(bit);
			// convert back to a string for printing
			char c = (char)cipher;
			plainText+=c;

		}
		return plainText;
	}
	
	public static void main(String[] args) {

		// Name of the file to be encrypted
		// TODO? Change to console input or other input than hardcoded?
		String file = "/test.txt";

		//Create the path to the directory with the file
		String dir = System.getProperty("user.dir");
		
		//Case of input for file
		if (args.length > 1) 
		{
			file = "/" + args[0];
		}
		
		String filePath = dir + file;
		//Go through the file byte by byte and convert to a binary string
		
		//Read in all data from file into a string
		String data = ""; 
	    try {
			data = new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	    
		// Input key for encryption/decryption
		
		System.out.println("The plain is " + data);
		
		String cipherText = encrypt_file(data, "1010010101");
		
		System.out.println("The Cipher is : " + cipherText);
		
		//TODO Send encrypted file on to recipient
		
		//Checking if the file was correctly encrypted and decrypted
		String cipherBinary = convert_to_binary(cipherText);
		
		String plainText = decrypt_file(cipherBinary, "1010010101");

		System.out.println("The plaintext is : " + plainText);


	}

}
