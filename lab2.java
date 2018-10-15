import java.io.*;
import java.util.*;

class Instruction {
    private String op;
    private String[] fields;

    public Instruction(String op, String[] fields) {
        this.op = op;
        this.fields = fields;
    }

    public String toString(){
        System.out.print(op + ", ");
        for (int i = 0; i < fields.length; i++) {
            System.out.print(fields[i]);
            if (i < fields.length - 1) {
                System.out.print(", ");
            }
        }
        return "";
    }

    public String getOp() {
        return this.op;
    }

    public String getField(int field_num) {
        return this.fields[field_num];
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
    String[] register_asm_lut = {"zero", "at", "v0", "v1", "a0", "a1", "a2",
                                 "a3", "t0", "t1", "t2", "t3", "t4", "t5",
                                 "t6", "t7", "s0", "s1", "s2", "s3", "s4", "s5",
                                 "s6", "s7", "t8", "t9", "k0", "k1", "gp",
                                 "sp", "fp", "ra"};

    String[] register_bin_lut = {"00000", "00001", "00010", "00011", "00100",
                                 "00101", "00110", "00111", "01000", "01001",
                                 "01010", "01011", "01100", "01101", "01110",
                                 "01111", "10000", "10001", "10010", "10011",
                                 "10100", "10101", "10110", "10111", "11000",
                                 "11001", "11010", "11011", "11100", "11101",
                                 "11110", "11111"};

    public static void main(String[] args) throws Exception {
        File asm = new File(args[0]);
        Scanner sc = new Scanner(asm);
        String delims = "[() ,$\t]+";

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
                String[] instr_parse = code_ln.split(delims);
                String[] fields = new String[instr_parse.length - 1];
                for (int i = 0; i < fields.length; i++) {
                    fields[i] = instr_parse[i + 1];
                }
                instructions.add(new Instruction(instr_parse[0], fields));

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
        System.out.println(numToBinString(45));
        System.out.println(numToBinString(12452));
        System.out.println(numToBinString(0));
        System.out.println(numToBinString(-6));
    }

    static String numToBinString(int to_conv) {
        String bin_ret = "";
        int bit_cnt = 0;
        while (bit_cnt < 32) {
            if ((to_conv & 0x01) > 0) {
                bin_ret = "1" + bin_ret;
            }
            else {
                bin_ret = "0" + bin_ret;
            }
            to_conv = to_conv >>> 1;
            bit_cnt++;
        }
        return bin_ret;
    }

    void printInstr(Instruction instr) {
        String bin_instr = "";
        if (instr.getOp().toLowerCase().equals("add")) {

        }
    }

}
