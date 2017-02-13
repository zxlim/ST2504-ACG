/*
**	[ST2504 Applied Cryptography Assignment]
**	[Encrypted Chat Program]
**
**	Aeron Teo (P1500725)
**	Aiman Abdul Rashid (P1529335)
**	Gerald Peh (P1445972)
**	Lim Zhao Xiang (P1529559)
*/

import java.util.Base64;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {

	//Encryption functions
	protected static AES encrypt_AES(final byte[] plaintext, final byte[] key) {
		try {
			final byte[] iv = generate_aesIV();
			final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));

			return new AES(cipher.doFinal(plaintext), iv);
		} catch (Exception e) {
			System.out.println("Exception occured: " + e + "\n");
			e.printStackTrace();
			return null;
		}
	}

	protected static byte[] encrypt_RSA(final byte[] plaintext, final PublicKey pubKey) {
		try {
			final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "SunJCE");
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);

			return cipher.doFinal(plaintext);
		} catch (Exception e) {
			System.out.println("Exception occured: " + e + "\n");
			e.printStackTrace();
			return null;
		}
	}

	//Decryption functions
	protected static byte[] decrypt_AES(final AES aesObj, final byte[] key) {
		try {
			final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(aesObj.getIV()));

			return cipher.doFinal(aesObj.getMessage());
		} catch (Exception e) {
			System.out.println("Exception occured: " + e + "\n");
			e.printStackTrace();
			return null;
		}
	}

	protected static byte[] decrypt_RSA(final byte[] ciphertext, final PrivateKey privKey) {
		try {
			final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "SunJCE");
			cipher.init(Cipher.DECRYPT_MODE, privKey);

			return cipher.doFinal(ciphertext);
		} catch (Exception e) {
			System.out.println("Exception occured: " + e + "\n");
			e.printStackTrace();
			return null;
		}
	}

	//Signing and verification functions
	protected static byte[] sign_ECDSA(final byte[] input, final PrivateKey privKey) {
		try {
			final Signature ecdsa = Signature.getInstance("SHA384withECDSA", "SunEC");
			ecdsa.initSign(privKey);
			ecdsa.update(input);

			return ecdsa.sign();
		} catch (Exception e) {
			System.out.println("Exception occured: " + e + "\n");
			e.printStackTrace();
			return null;
		}
	}

	protected static boolean verify_ECDSA(final byte[] input, final PublicKey pubKey, final byte[] signature) {
		try {
			final Signature ecdsa = Signature.getInstance("SHA384withECDSA", "SunEC");
			ecdsa.initVerify(pubKey);
			ecdsa.update(input);

			return ecdsa.verify(signature);
		} catch (Exception e) {
			System.out.println("Exception occured: " + e + "\n");
			e.printStackTrace();
			return false;
		}
	}

	//Generators
	static byte[] generate_aesKey() {
		try {
			final KeyGenerator keygen = KeyGenerator.getInstance("AES", "SunJCE");
			keygen.init(128, new SecureRandom());

			return (keygen.generateKey()).getEncoded();
		} catch (Exception e) {
			System.out.println("Exception occured: " + e + "\n");
			e.printStackTrace();
			return null;
		}
	}

	protected static PKI generate_RSA() {
		try {
			final KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "SunRsaSign");
			kpGen.initialize(3072);
			final KeyPair rsaKey = kpGen.genKeyPair();

			return new PKI(rsaKey.getPublic(), rsaKey.getPrivate());
		} catch (Exception e) {
			System.out.println("Exception occured: " + e + "\n");
			e.printStackTrace();
			return null;
		}
	}

	protected static PKI generate_ECDSA() {
		try {
			final KeyPairGenerator kpGen = KeyPairGenerator.getInstance("EC", "SunEC");
			final ECGenParameterSpec kpGenParams = new ECGenParameterSpec("secp256r1");
			kpGen.initialize(kpGenParams);
			final KeyPair ecdsaKey = kpGen.genKeyPair();

			return new PKI(ecdsaKey.getPublic(), ecdsaKey.getPrivate());
		} catch (Exception e) {
			System.out.println("Exception occured: " + e + "\n");
			e.printStackTrace();
			return null;
		}
	}

	private static byte[] generate_aesIV() {
		try {
			return secureRand(16);
		} catch (Exception e) {
			System.out.println("Exception occured: " + e + "\n");
			e.printStackTrace();
			return null;
		}
	}

	private static byte[] secureRand(final int length) {
		try {
			byte[] rand = new byte[length];
			//SecureRandom generate = SecureRandom.getInstance("SHA1PRNG", "SUN"); //Force SUN SHA1PRNG implementation
			SecureRandom generate = new SecureRandom(); //Use native OS preferred implementation
			generate.nextBytes(rand);

			return rand;
		} catch (Exception e) {
			System.out.println("Exception occured: " + e + "\n");
			e.printStackTrace();
			return null;
		}
	}

	//Hashing functions
	protected static byte[] hash_sha512(final byte[] input) {
		try {
			final MessageDigest hash = MessageDigest.getInstance("SHA-512");
			hash.update(input);

			return hash.digest();
		} catch (Exception e) {
			System.out.println("Exception occured: " + e + "\n");
			e.printStackTrace();
			return null;
		}
	}

	protected static byte[] hash_sha384(final byte[] input) {
		try {
			final MessageDigest hash = MessageDigest.getInstance("SHA-384");
			hash.update(input);

			return hash.digest();
		} catch (Exception e) {
			System.out.println("Exception occured: " + e + "\n");
			e.printStackTrace();
			return null;
		}
	}

	private static byte[] pbkdf2(final String input, final byte[] salt, final int length) throws Exception {
		try {
			final char[] inputChar = input.toCharArray();

			final SecretKeyFactory key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
			final PBEKeySpec specification = new PBEKeySpec(inputChar, salt, 128000, length);

			return key.generateSecret(specification).getEncoded();
		} catch (Exception e) {
			System.out.println("Exception occured: " + e + "\n");
			e.printStackTrace();
			return null;
		}
	}

	//Misc functions
	protected static byte[] strToBytes(final String input) {
		return input.getBytes(Charset.forName("UTF-8"));
	}

	protected static String bytesToStr(final byte[] input) {
		return new String(input);
	}
} //class
