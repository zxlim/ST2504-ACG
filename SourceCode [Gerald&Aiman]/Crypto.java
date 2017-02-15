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
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.security.cert.Certificate;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
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

	private static final char[] keystorePass = {'1', 'q', 'w', 'e', 'r' ,'$', '#', '@', '!'};

	//Encryption functions
	static AES encrypt_AES(final byte[] plaintext, final byte[] key) {
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

	static byte[] encrypt_RSA(final byte[] plaintext, final PublicKey pubKey) {
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
	static byte[] decrypt_AES(final AES aesObj, final byte[] key) {
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

	static byte[] decrypt_RSA(final byte[] ciphertext, final PrivateKey privKey) {
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
	static byte[] sign_ECDSA(final byte[] input, final PrivateKey privKey) {
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

	static boolean verify_ECDSA(final byte[] input, final PublicKey pubKey, final byte[] signature) {
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

	//Keystore functions
	static PKI ksPublicKey(final String ksPath, final String alias) {
		try {
			final FileInputStream ksFile = new FileInputStream(ksPath);
			final KeyStore keystore = KeyStore.getInstance("JKS");
			keystore.load(ksFile, keystorePass);
			final Key key = keystore.getKey(alias, keystorePass);

			if (ksFile != null) {
				ksFile.close();
			}

			if (key instanceof PrivateKey) {
				return new PKI(keystore.getCertificate(alias).getPublicKey());
			} else {
				//Shouldn't happen, if not it means something screwed up badly
				return null;
			}
		} catch (Exception e) {
			System.out.println("Exception occured: " + e + "\n");
			e.printStackTrace();
			return null;
		}
	}

	static PKI ksPrivateKey(final String ksPath, final String alias) {
		try {
			final FileInputStream ksFile = new FileInputStream(ksPath);
			final KeyStore keystore = KeyStore.getInstance("JKS");
			keystore.load(ksFile, keystorePass);

			final PrivateKey privKey = (PrivateKey) keystore.getKey(alias, keystorePass);

			if (ksFile != null) {
				ksFile.close();
			}

			return new PKI(privKey);
		} catch (Exception e) {
			System.out.println("Exception occured: " + e + "\n");
			e.printStackTrace();
			return null;
		}
	}

	//Generators
	static byte[] generate_aesKey() {
		try {
			final KeyGenerator keygen = KeyGenerator.getInstance("AES", "SunJCE");
			keygen.init(256, new SecureRandom());

			return (keygen.generateKey()).getEncoded();
		} catch (Exception e) {
			System.out.println("Exception occured: " + e + "\n");
			e.printStackTrace();
			return null;
		}
	}

	static PKI generate_RSA() {
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

	static PKI generate_ECDSA() {
		try {
			final KeyPairGenerator kpGen = KeyPairGenerator.getInstance("EC", "SunEC");
			final ECGenParameterSpec kpGenParams = new ECGenParameterSpec("secp384r1");
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

	static byte[] secureRand(final int length) {
		//For password salts, use a minimum length of 16 (128-bit)
		//Save the salt with base64 encoding
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
	static byte[] hash_sha384(final byte[] input) {
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

	static byte[] pbkdf2(final String input, final byte[] salt) throws Exception {
		//For password hashing
		//Salt should be generated with secureRand method
		try {
			final char[] inputChar = input.toCharArray();

			final SecretKeyFactory key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA384");
			final PBEKeySpec specification = new PBEKeySpec(inputChar, salt, 128000, 384);

			return key.generateSecret(specification).getEncoded();
		} catch (Exception e) {
			System.out.println("Exception occured: " + e + "\n");
			e.printStackTrace();
			return null;
		}
	}

	//Misc functions
	static byte[] strToBytes(final String input) {
		return input.getBytes(Charset.forName("UTF-8"));
	}

	static String bytesToStr(final byte[] input) {
		return new String(input);
	}

	static String bytesToBase64(final byte[] input) {
		return Base64.getEncoder().encodeToString(input);
	}

	static byte[] base64ToBytes(final String input) {
		return Base64.getDecoder().decode(input);
	}
} //class
