/*
**	[ST2504 Applied Cryptography Assignment]
**	[Encrypted Chat Program]
**
**	Aeron Teo (P1500725)
**	Aiman Abdul Rashid (P1529335)
**	Gerald Peh (P1445972)
**	Lim Zhao Xiang (P1529559)
*/

import java.io.*;

public class File {
    public static void credentialWriter(String name, String password, String salt) throws Exception {

        // creates a FileWriter Object
        FileWriter writer = new FileWriter("passwd", true);

        // Writes the content to the file
        writer.write(name + ":" + password + ":" + salt + System.getProperty( "line.separator" ));
        writer.flush();
        writer.close();

    }

    public static int validateName(String name) throws Exception {


        final InputStreamReader file = new InputStreamReader(new FileInputStream("passwd"), "UTF-8");
        BufferedReader read = new BufferedReader(file);

        String line;

        while ((line = read.readLine()) != null) {
            String[] word = line.split(":");
            String firstWord = word[0];

            if (name.equals(firstWord)) {
                return 1;
            }
        }

        return 0;
    }

}
