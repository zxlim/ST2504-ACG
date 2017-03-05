/*
** A program to generate hashed password and its salt.
** Written by Zhao Xiang.
*/

import java.util.Scanner;

public class PasswordGenerator {

    public static void main(String[] args) {
        final Scanner scan = new Scanner(System.in);
        System.out.print("Username: ");
        final String username = scan.nextLine();
        final String password = new String(System.console().readPassword("Password: "));

        final byte[] saltBae = Crypto.secureRand(32);
        final String pwHash = Crypto.bytesToBase64(Crypto.pbkdf2(password, saltBae));

        System.out.println("Password Hash:\t" + pwHash);
        System.out.println("Password Salt:\t" + Crypto.bytesToBase64(saltBae));
        System.out.println("\nPPAP PASSWD Format:\n" + username + ":" + pwHash + ":" + Crypto.bytesToBase64(saltBae));

        scan.close();
        return;
    }
}
