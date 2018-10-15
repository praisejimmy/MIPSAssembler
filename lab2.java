import java.io.*;
import java.util.*;

class Instruction {
    private String op;
    private String[] fields;
    private int line;

    public Instruction(String op, String[] fields, int line) {
        this.op = op;
        this.fields = fields;
        this.line = line;
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

    public int getLineNum() {
        return this.line;
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
    static ArrayList<Label> labels;
    static String[] register_asm_lut = {"zero", "at", "v0", "v1", "a0", "a1", "a2",
                                 "a3", "t0", "t1", "t2", "t3", "t4", "t5",
                                 "t6", "t7", "s0", "s1", "s2", "s3", "s4", "s5",
                                 "s6", "s7", "t8", "t9", "k0", "k1", "gp",
                                 "sp", "fp", "ra", "0", "1", "2", "3", "4",
                                 "5", "6", "7", "8", "9", "10", "11", "12",
                                 "13", "14", "15", "16", "17", "18", "19",
                                 "20", "21", "22", "23", "24", "25", "26",
                                 "27", "28", "29", "30", "31"};

    static String[] register_bin_lut = {"00000", "00001", "00010", "00011", "00100",
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

        ArrayList<Instruction> instructions = new ArrayList<Instruction>();
        labels = new ArrayList<Label>();

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
                instructions.add(new Instruction(instr_parse[0], fields, counter));

                //increment address
                counter++;
            }
        }
        for (Instruction instr : instructions) {
            printInstr(instr);
        }
    }

    static String numToBinString(int to_conv, int bits) {
        String bin_ret = "";
        int bit_cnt = 0;
        while (bit_cnt < bits) {
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

    static void printInstr(Instruction instr) throws Exception {
        String bin_instr = "";
        String op = instr.getOp().toLowerCase();
        int curr_add;
        switch(op) {
            case "add":
                bin_instr = "000000 " + getRegBin(instr.getField(1)) + " " + getRegBin(instr.getField(2)) + " " + getRegBin(instr.getField(0)) + " 00000 100000";
                break;
            case "or":
                bin_instr = "000000 " + getRegBin(instr.getField(1)) + " " + getRegBin(instr.getField(2)) + " " + getRegBin(instr.getField(0)) + " 00000 100101";
                break;
            case "and":
                bin_instr = "000000 " + getRegBin(instr.getField(1)) + " " + getRegBin(instr.getField(2)) + " " + getRegBin(instr.getField(0)) + " 00000 100100";
                break;
            case "addi":
                bin_instr = "001000 " + getRegBin(instr.getField(1)) + " " + getRegBin(instr.getField(0)) + " " + numToBinString(Integer.parseInt(instr.getField(2)), 16);
                break;
            case "sll":
                bin_instr = "000000 00000 " + getRegBin(instr.getField(1)) + " " + getRegBin(instr.getField(0)) + " " + numToBinString(Integer.parseInt(instr.getField(2)), 5) + " 000000";
                break;
            case "sub":
                bin_instr = "000000 " + getRegBin(instr.getField(1)) + " " + getRegBin(instr.getField(2)) + " " + getRegBin(instr.getField(0)) + " 00000 100010";
                break;
            case "slt":
                bin_instr = "000000 " + getRegBin(instr.getField(1)) + " " + getRegBin(instr.getField(2)) + " " + getRegBin(instr.getField(0)) + " 00000 101010";
                break;
            case "beq":
                curr_add = getLabelAddress(instr.getField(2)) - (instr.getLineNum() + 1);
                bin_instr = "000100 " + getRegBin(instr.getField(0)) + " " + getRegBin(instr.getField(1)) + " " + numToBinString(curr_add, 16);
                break;
            case "bne":
                curr_add = getLabelAddress(instr.getField(2)) - (instr.getLineNum() + 1);
                bin_instr = "000101 " + getRegBin(instr.getField(0)) + " " + getRegBin(instr.getField(1)) + " " + numToBinString(curr_add, 16);
                break;
            case "lw":
                bin_instr = "100011 " + getRegBin(instr.getField(2)) + " " + getRegBin(instr.getField(0)) + " " + numToBinString(Integer.parseInt(instr.getField(1)), 16);
                break;
            case "sw":
                bin_instr = "101011 " + getRegBin(instr.getField(2)) + " " + getRegBin(instr.getField(0)) + " " + numToBinString(Integer.parseInt(instr.getField(1)), 16);
                break;
            case "j":
                curr_add = getLabelAddress(instr.getField(0)) - (instr.getLineNum() + 1);
                bin_instr = "000010 " + numToBinString(curr_add, 26);
                break;
            case "jr":
                bin_instr = "000000 " + getRegBin(instr.getField(0)) + " 000000000000000 001000";
                break;
            case "jal":
                curr_add = getLabelAddress(instr.getField(0)) - (instr.getLineNum() + 1);
                System.out.println(curr_add);
                bin_instr = "000011 " + numToBinString(curr_add, 26);
                break;
        }
        System.out.println(bin_instr);
    }

    static String getRegBin(String reg) throws Exception {
        for (int i = 0; i < 64; i++) {
            if (register_asm_lut[i].equals(reg)) {
                return register_bin_lut[i % 32];
            }
        }
        throw new Exception("Register " + reg + " not valid");
    }

    static int getLabelAddress(String match) throws Exception {
        for (Label label : labels) {
            if (match.equals(label.getLabel())) {
                return label.getAddress();
            }
        }
        throw new Exception("Label " + match + " not valid");
    }

}
