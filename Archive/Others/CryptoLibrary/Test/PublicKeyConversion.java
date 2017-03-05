/*
** Test program for converting byte array to Public Key Object.
** Written by Zhao Xiang.
*/

import java.util.Arrays;

public class PublicKeyConversion {

    public static void main(String[] args) {
        System.out.println("Generating keys...");

        final PKI rsa = Crypto.generate_RSA();
        final PKI ecdsa = Crypto.generate_ECDSA();

        System.out.println("Generated keys.");

        final byte[] rsaBytes = rsa.getPubBytes();
        final byte[] ecdsaBytes = ecdsa.getPubBytes();

        System.out.println("Testing conversion...\n");

        try {
            final PKI rsaPub = Crypto.bytesToPublicRSA(rsaBytes);
            final byte[] rsaConv = rsaPub.getPubBytes();

            if (Arrays.equals(rsaBytes, rsaConv) && (rsa.getPublic()).equals(rsaPub.getPublic())) {
                System.out.println("RSA okay.");
            } else {
                System.out.println("RSA mismatch.");
            }
        } catch (Exception e) {
            System.out.println("Exception converting RSA Key:\n" + e + "\n");
            e.printStackTrace();
        }

        try {
            final PKI ecdsaPub = Crypto.bytesToPublicECDSA(ecdsaBytes);
            final byte[] ecdsaConv = ecdsaPub.getPubBytes();

            if (Arrays.equals(ecdsaBytes, ecdsaConv) && (ecdsa.getPublic()).equals(ecdsaPub.getPublic())) {
                System.out.println("ECDSA okay.");
            } else {
                System.out.println("ECDSA mismatch.");
            }
        } catch (Exception e) {
            System.out.println("Exception converting ECDSA Key:\n" + e + "\n");
            e.printStackTrace();
        }
        return;
    }
}
