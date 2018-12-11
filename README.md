"#Crypto_HW2" 
Implementation of Needham-Schroeder Symmetric Key Protocol
My implementation of this protocol uses Java, and consists of a few different files. Firstly, there is the HW2.java file. This file contains generic helper functions, mostly to facilitate dealing with the packets, and encrypting the packets. Secondly, we have the Encrypt.java file. This file contains all of the necesary functions for encrypting or decrypting any information. There were some minor tweaks made to the original HW1 code to make this run more smoothly in this setting. Lastly, we have the three files that run this protocol. 

The first of these files is KeyDist.java. This file represents the KDC in the key exchange. It acts as a server for sending and recieving data from the two clients, A and B (Alice and Bob). The other two files are appropriately named, ClientA and ClientB. These three files interact with eachother to perform this protocol in a very smooth manner. 
To begin, the three parties must first establish shared private keys in order to perform the N-S protocol. For this we use the Computational Diffie-Helman Key Exchange. 

Computational Diffie-Helman Key Exchange
In the beginning of each file, the keys are computed between the client, and the KDC. I decided on predetermined values of 997 for the prime number 'P', and 9 for the base. Each party randomly chooses a value 'a' between 0 and 997 privately, and computes base^a. They then send this number, modulus P, to the other party. This number is then raised to the other party's power, and modulated again. The resultant value is then used as the private key between the two parties. Both parties send the key back to eachother to ensure they both have the same result, and if this is the case, we can move on. 

Moving back to N-S, we now have established the private keys between the clients and the KDC. With these, we can now establish a private, secure key between the two clients, using the KDC. First, A establishes some values. For this protocol, it must send a packet containing the identities of both parties, and a Nonce. Party A defines these identities using random numbers between 0 and 2^10, one for A and one for B. The nonce is also computed in the same way, as a random number between 0 and 2^16. These values are sent together in a packet to the KDC to obtain a session key between the two Ids. When sending the packet, I was unsure of exactly how it should be structured, so I simply sent a string, with each piece of data separated by a '|' character. This allowed for easy parsing of the packets, and it also worked with my existing encryption algorithm. 

Once the KDC has sent back to Client A the encrypted packet with the key, it can be decrypted using the previously established private key for A, and the session key can be obtained. the inner packet encrypted with B's key can then be sent on to B, giving both parties the same key. To ensure that both parties have recieved the same key, one final message is sent back to verify the key. In my case, I simply sent the key back to compare the two, but in an ideal situation, you would encrypt some piece of information, send it over, and the other party would be able to decrypt, modify, and send back. This final exchange confirms that both parties have the key, and they are both communicating with who they think they are. 

Preventing Replay Attacks
To defend from replay attacks, an extra piece of data is added to the packet sent by the KDC. This is a timestamp telling when this packet was created and sent. Both parties will recieve a copy of this, and can ensure that it matches. If this timestamp were to be compromised, or the packet were to be intercepted, and resent to the original destination, this timestamp would tell if the key was compromised or not. 

To run this code, first run KeyDist.java, followed by ClientA.java and ClientB.java in relatively quick succession. This will perform the key distribution to both A and B.
