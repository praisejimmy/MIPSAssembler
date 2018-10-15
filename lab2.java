import java.io.*;
import java.util.*;

class Instruction {
    private String opcode;    

    public Instruction(String opcode, String[] fields) {
        this.opcode = opcode;        
    }
    
    public String toString(){
        return this.opcode;
    }
}

class Label{
    private String label;
    private int address;

    public Label(String l, int a){
        this.label = l;
        this.address = a;
    }

    public int getAddress(){
        return this.address;
    }

    public void setAddress(int address){
        this.address = address;
    }

    public String getLabel(){
        return this.label;
    }

    public String toString(){
        return getLabel() + ", " + getAddress();
    }

}

class Register {
    String reg;
    int val;

    public Register(String reg, int val) {
        this.reg = reg;
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int new_val) {
        this.val = new_val;
    }

    public String getName() {
        return reg;
    }

}                


public class lab2 {

    public static void main(String[] args) throws Exception {
        File asm = new File(args[0]);
        Scanner sc = new Scanner(asm);
        String delims = "[ ,$\t]+";

        ArrayList<Label> labels = new ArrayList<Label>();
        ArrayList<Instruction> instructions = new ArrayList<Instruction>();

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
                code_ln = code_ln.substring(colon_loc+1).trim();
            }

            if (code_ln.length() > 1){//more than just a colon. real code
                //loop through and set address
                int index = labels.size()-1;
                while(index >= 0 && labels.get(index).getAddress() == -1){
                    labels.get(index).setAddress(counter);
                    index--;
                }

                //make instruction
                instructions.add(new Instruction(code_ln, null));

                //increment address
                counter++;
            }
        }
        for(Label label : labels){
            System.out.println(label);
        }

        for(Instruction instruction: instructions){
            System.out.println(instruction);
        }
    }

}
