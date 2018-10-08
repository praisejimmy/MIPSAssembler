import java.io.*;
import java.util.*;

public class lab2 {

    public static void main(String[] args) throws Exception {
        File asm = new File(args[0]);
        Scanner sc = new Scanner(asm);
        String delims = "[ ,$\t]+";
        while (sc.hasNextLine()) {
            String code_ln = sc.nextLine();
            String[] fields = code_ln.split(delims);
            for (int i = 0; i < fields.length; i++) {
                System.out.print(fields[i] + ", ");
            }
            System.out.println("");
        }
    }

}
