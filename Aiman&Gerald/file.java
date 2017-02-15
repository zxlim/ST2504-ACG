import java.io.*;

public class file {
    public static void credentialWriter(String name, String password, String salt) throws Exception {
        File file = new File("file.txt");

        // creates a FileWriter Object
        FileWriter writer = new FileWriter(file, true);

        // Writes the content to the file
        writer.write(name + ":" + password + ":" + salt + System.getProperty( "line.separator" ));
        writer.flush();
        writer.close();

        // Creates a FileReader Object
        /*FileReader fr = new FileReader(file);
        char [] a = new char[50];
        fr.read(a);   // reads the content to the array

        for(char c : a)
        System.out.print(c);   // prints the characters one by one
        fr.close();*/
    }

    public static int validateName(String name) throws Exception {


        FileReader file = new FileReader(new File("file.txt"));
        BufferedReader read = new BufferedReader(file);

        String line = read.readLine();
        String firstWord = null;

        while ((line = read.readLine()) != null) {
            firstWord= line.substring(0, line.indexOf(":"));
            if (name.equals(firstWord)) {
                return 1;
            }
        }

        return 0;
    }

}
