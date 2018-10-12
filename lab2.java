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
            if (comment_loc != -1)
                code_ln = code_ln.substring(0, comment_loc).trim();

            int colon_loc = code_ln.indexOf(':');
            if (colon_loc != -1){
                Label new_label = new Label(code_ln.substring(0,colon_loc), -1);
                labels.add(new_label);
                code_ln = code_ln.substring(colon_loc).trim();
            }

            if (code_ln.length() > 1){//more than just a colon. real code
                //loop through and set address
                int index = labels.size()-1;
                while(index >= 0 && labels.get(index).getAddress() == -1){
                    labels.get(index).setAddress(counter);
                    index--;
                }

                //make instruction


                //increment address
                counter++;
            }
        }
        for(Label label : labels){
            System.out.println(label);
        }
    }

}
