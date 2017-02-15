import java.io.*;

public class file {
    public static void credentialWriter(String name, byte[] password) throws Exception {
          File file = new File("file.txt");

          // creates a FileWriter Object
          FileWriter writer = new FileWriter(file, true);

          // Writes the content to the file
          writer.write(name + "," + password + System.getProperty( "line.separator" ));
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
}
