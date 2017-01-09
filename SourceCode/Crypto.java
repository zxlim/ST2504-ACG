/*
**	NOTICE:
**	This is just a proof of concept. This implementation
**	is totally 100% NOT SECURE AT ALL HONESTLY SPEAKING.
**	There are also tons of other bugs in the chat app.
**	- Zhao Xiang
*/

import java.util.Base64;
import java.nio.charset.Charset;
import java.security.Key;
//import java.security.SecureRandom;
import javax.crypto.Cipher;
//import javax.crypto.KeyGenerator;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class Crypto {

	public static byte[] encrypt_AES(final String plaintext) {
		try {
			final Key aesKey = aesKey();
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, aesKey, aesIV());
			byte[] ciphertext = cipher.doFinal(plaintext.getBytes(Charset.forName("UTF-8")));

      return ciphertext;
		} catch (Exception ex) {
			System.out.println("Exception occured: " + ex);
      ex.printStackTrace();
			return null;
		}
	}

	public static String decrypt_AES(final byte[] ciphertext) {
		try {
			final Key aesKey = aesKey();
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, aesKey, aesIV());
      byte[] plaintext = cipher.doFinal(ciphertext);

			return new String(plaintext);
		} catch (Exception ex) {
			System.out.println("Exception occured: " + ex);
      ex.printStackTrace();
			return null;
		}
	}

  private static Key aesKey() {
    try {
      /*KeyGenerator keygen = KeyGenerator.getInstance("AES");
      keygen.init(256);
      return keygen.generateKey();*/
      return (new SecretKeySpec(pbkdf2(), "AES"));
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  private static IvParameterSpec aesIV() {
    try {
      return (new IvParameterSpec("8b7bef6b03ec151f".getBytes(Charset.forName("UTF-8"))));
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  //private static byte[] secureRand() throws Exception {
  //}

  private static byte[] pbkdf2() throws Exception{
		final char[] inputChar = "TheBestSecretKey".toCharArray(); //Disclaimer: Totally not the best at all...
	  final byte[] saltBytes = Base64.getDecoder().decode("cXMys16pnqplzaNn6CJ+oNUI9LRGEmtgeZ3HNllkDGepsOfQ0A/W4phvbokG59efiqhySw4IgUBajoIdcNmafQ==");

	  final SecretKeyFactory key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
    final PBEKeySpec specification = new PBEKeySpec(inputChar, saltBytes, 32000, 256);

    return key.generateSecret(specification).getEncoded();
	}
}
