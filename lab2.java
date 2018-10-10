package MIPSAssembler;

import java.io.*;
import java.util.*;

public class lab2 {

    public static void main(String[] args) throws Exception {
        File asm = new File(args[0]);
        Scanner sc = new Scanner(asm);
        String delims = "[ ,$\t]+";

        ArrayList<Label> labels = new ArrayList<Label>();


        int counter = 0x00;
        while (sc.hasNextLine()) {
            String code_ln = sc.nextLine().trim();
            int comment_loc = code_ln.indexOf('#');
            int colon_loc = code_ln.indexOf(':');
            if ((comment_loc != -1 && colon_loc != -1 && colon_loc < comment_loc) || (colon_loc != -1 && comment_loc == -1)){
                Label new_label = new Label(line_stuff[0], counter);
                labels.add(new_label);
            }            
            counter ++;
        }
        for(Label label : labels){
            System.out.println(label);
        }
    }

}
