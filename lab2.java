// Name: Andrew Brown, Ryan Myers
// Section: 01
// Lab 2: MIPS Assembler
import java.io.*;
import java.util.*;

class Instruction {
    private String op;
    private String[] fields;
    private int line;
    private int branch_pc;

    public Instruction(String op, String[] fields, int line) {
        this.op = op;
        this.fields = fields;
        this.line = line;
        this.branch_pc = -1;
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

    public String[] getFields(){
        return this.fields;
    }

    public void setBranchPC(int pc){
        this.branch_pc = pc;
    }

    public int getBranchPC(){
        return this.branch_pc;
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

public class lab2 {
    static ArrayList<Label> labels;

    static String[] pipeline_title = {"if/id", "id/exe", "exe/mem", "mem/wb"};
    static Instruction[] pipeline = new Instruction[4];

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

    static int[] regfile = new int[32];

    static int[] ram = new int[8192];

    static int pc;

    static int cycles;

    static int instr_run;

    static boolean delay;

    static int stall;

    static int squash;

    static int instr_cnt;

    static boolean done;


    //TODO: Current idea for handeling branches: have some sort of stack of integers to control # cycles before real branch
    // OR we change implementation to execute in a different stage rather than first
    public static void main(String[] args) throws Exception {
        // init register file, ram, and pc
        for (int i = 0; i < 32; i++) {
            regfile[i] = 0;
        }
        for (int i = 0; i < 8192; i++) {
            ram[i] = 0;
        }
        for (int i = 0; i < 4; i++) {
            pipeline[i] = new Instruction("empty", null, -1);
        }
        pc = 0;
        cycles = 0;
        instr_run = 0;
        stall = 0;
        squash = 0;
        delay = false;
        instr_cnt = 0;
        done = false;

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
        sc.close();
        Scanner user_in;
        if (args.length > 1) {
            File script = new File(args[1]);
            user_in = new Scanner(script);
        }
        else {
            user_in = new Scanner(System.in);
        }
        String command = "";

        while (true) {
            System.out.print("mips> ");
            command = user_in.nextLine();
            if (args.length > 1)
                System.out.println(command);
            String[] command_parse = command.split(" ");
            switch(command_parse[0]) {
                case "h":
                    // show help message
                    System.out.println("\nh = show help\n" +
                    "d = dump register state\n" +
                    "p = show pipeline registers\n" +
                    "s = step through a single clock cycle step (i.e. simulate 1 cycle and stop)\n" +
                    "s num = step through num clock cycles\n" +
                    "r = run until the program ends and display timing summary\n" +
                    "m num1 num2 = display data memory from location num1 to num2\n" +
                    "c = clear all registers, memory, and the program counter to 0\n" +
                    "q = exit the program\n");
                    break;
                case "d":
                    // dump registers
                    System.out.println("\npc = " + pc + "\n" +
"$0 = " + regfile[0] + "          $v0 = " + regfile[2] + "         $v1 = " + regfile[3] + "         $a0 = " + regfile[4] + "\n" +
"$a1 = " + regfile[5] + "         $a2 = " + regfile[6] + "         $a3 = " + regfile[7] + "         $t0 = " + regfile[8] + "\n" +
"$t1 = " + regfile[9] + "         $t2 = " + regfile[10] + "         $t3 = " + regfile[11] + "         $t4 = " + regfile[12] + "\n" +
"$t5 = " + regfile[13] + "         $t6 = " + regfile[14] + "         $t7 = " + regfile[15] + "         $s0 = " + regfile[16] + "\n" +
"$s1 = " + regfile[17] + "         $s2 = " + regfile[18] + "         $s3 = " + regfile[19] + "         $s4 = " + regfile[20] + "\n" +
"$s5 = " + regfile[21] + "         $s6 = " + regfile[22] + "         $s7 = " + regfile[23] + "         $t8 = " + regfile[24] + "\n" +
"$t9 = " + regfile[25] + "         $sp = " + regfile[29] + "         $ra = " + regfile[31] + "\n");
                    break;

                case "p":
                    // show pipeline registers
                    printPipelineRegisters();
                    break;

                case "s":
                    if (done) {
                        break;
                    }
                    done = false;
                    int length = 1;
                    if (command_parse.length > 1) {
                        length = Integer.parseInt(command_parse[1]);
                    }
                    // step through length instructions
                    for(int i = 0; i < length; i++){
                        //base case to stop
                        if (pc >= counter){
                            if (pc >= counter + 3){
                                done = true;
                                break;
                            }
                            if (pipeline[0].getOp().equals("beq") || pipeline[0].getOp().equals("bne") || pipeline[0].getOp().equals("empty")){
                                //continue
                            }else{
                                done = true;
                                break;
                            }
                        }

                        if (stall > 0 || squash > 0){
                            if (stall > 0){
                                // stall 1 cycle because of use after load
                                pushStall();
                                stall--;
                                cycles++;
                                continue;
                            }
                            else if (squash > 0){
                                // squash instruction after jump and then jump
                                pushSquash();
                                squash--;
                                pc = pipeline[1].getBranchPC();
                                pipeline[1].setBranchPC(-1);
                                pc++;
                                cycles++;
                                continue;
                            }
                        }

                        Instruction instr = instructions.get(pc);
                        pushPipelineReg(instr);

                        //execute the instruction if we didn't take a branch and avoid the second execution after a stall
                        if (!delay && !pipeline[1].getOp().equals("stall")){
                            execInstr(instr);
                            instr_cnt++;
                        }

                        //check if branch that was taken is now in the mem/wb reg
                        Instruction mem_wb_instr = pipeline[pipeline.length - 1];
                        if (mem_wb_instr.getOp().equals("beq") || mem_wb_instr.getOp().equals("bne")){
                            if (mem_wb_instr.getBranchPC() != -1){
                                //branch was taken. squash prev 3 and update pc
                                delay = false;
                                for (int j = 0; j < 3; j++){
                                    pipeline[j] = new Instruction("squash", null, -1);
                                }
                                pc = mem_wb_instr.getBranchPC();
                                mem_wb_instr.setBranchPC(-1);
                            }
                        }

                        cycles++;
                        pc++;
                    }
                    printPipelineRegisters();
                    if (done) {
                        //check for instructions that need to reach the end
                        for (int j = 0; j < 4; j++){
                            if (!pipeline[j].getOp().equals("beq") && !pipeline[j].getOp().equals("bne") && !pipeline[j].getOp().equals("squash")){
                                cycles += 4-j;
                                break;
                            }
                        }
                        System.out.println("\nProgram complete");
                        System.out.printf("CPI = %.3f" + "\tCycles = " + cycles + "\tInstructions = " + instr_cnt + "\n\n", (cycles*1.0)/instr_cnt);
                    }
                    break;
                case "r":
                    // run till program ends
                    if (done) {
                        break;
                    }
                    done = false;
                    if (pc >= counter) {
                        if (!(pipeline[0].getOp().equals("beq") || pipeline[0].getOp().equals("bne") || pipeline[0].getOp().equals("empty"))){
                            if (pc > counter + 3){
                                break;
                            }
                        }else{
                            break;
                        }
                    }
                    while(pc < counter + 3){
                        //base case to stop
                        if (pc >= counter){
                            if (pc >= counter + 3){
                                done = true;
                                break;
                            }
                            if (pipeline[0].getOp().equals("beq") || pipeline[0].getOp().equals("bne") || pipeline[0].getOp().equals("empty")){
                                //continue
                            }else{
                                done = true;
                                break;
                            }
                        }

                        if (stall > 0 || squash > 0){
                            if (stall > 0){
                                // stall 1 cycle because of use after load
                                pushStall();
                                stall--;
                                cycles++;
                                continue;
                            }
                            else if (squash > 0){
                                // squash instruction after jump and then jump
                                pushSquash();
                                squash--;
                                pc = pipeline[1].getBranchPC();
                                pipeline[1].setBranchPC(-1);
                                pc++;
                                cycles++;
                                continue;
                            }
                        }

                        Instruction instr = instructions.get(pc);
                        pushPipelineReg(instr);

                        //execute the instruction if we didn't take a branch and avoid the second execution after a stall
                        if (!delay && !pipeline[1].getOp().equals("stall")){
                            execInstr(instr);
                            instr_cnt++;
                        }

                        //check if branch that was taken is now in the mem/wb reg
                        Instruction mem_wb_instr = pipeline[pipeline.length - 1];
                        if (mem_wb_instr.getOp().equals("beq") || mem_wb_instr.getOp().equals("bne")){
                            if (mem_wb_instr.getBranchPC() != -1){
                                //branch was taken. squash prev 3 and update pc
                                delay = false;
                                for (int j = 0; j < 3; j++){
                                    pipeline[j] = new Instruction("squash", null, -1);
                                }
                                pc = mem_wb_instr.getBranchPC();
                                mem_wb_instr.setBranchPC(-1);
                            }
                        }

                        cycles++;
                        pc++;
                    }
                    if (done){
                        //check for instructions that need to reach the end
                        for (int j = 0; j < 4; j++){
                            if (!pipeline[j].getOp().equals("beq") && !pipeline[j].getOp().equals("bne") && !pipeline[j].getOp().equals("squash")){
                                cycles += 4-j;
                                break;
                            }
                        }
                    }
                    System.out.println("\nProgram complete");
                    System.out.printf("CPI = %.3f" + "\tCycles = " + cycles + "\tInstructions = " + instr_cnt + "\n\n", (cycles*1.0)/instr_cnt);
                    pc = counter + 4;
                    break;
                case "m":
                    // display RAM memory from command_parse[1 -> 2]
                    int from = Integer.parseInt(command_parse[1]);
                    int to = Integer.parseInt(command_parse[2]);
                    System.out.println("\n");
                    for (; from <= to; from++) {
                        System.out.println("[" + from + "] = " + ram[from]);
                    }
                    System.out.println();
                    break;
                case "c":
                    // clear all registers, reset program
                    for (int i = 0; i < 32; i++){
                        regfile[i] = 0;
                    }
                    for (int i = 0; i < 8192; i++){
                        ram[i] = 0;
                    }
                    for (int i = 0; i < pipeline.length; i++){
                        pipeline[i] = new Instruction("empty", null, -1);
                    }
                    delay = false;
                    stall = 0;
                    squash = 0;
                    pc = 0;
                    cycles = 0;
                    instr_cnt = 0;
                    System.out.println("        Simulator reset\n");
                    break;
                case "q":
                    System.exit(0);
                default:
                    System.out.println("invalid command: " + command);
                    System.exit(-1);
            }
        }
    }

    static void pushPipelineReg(Instruction instr){
        for (int i = pipeline.length - 1; i > 0; i--){
            pipeline[i] = pipeline[i-1];
        }
        pipeline[0] = instr;
        return;
    }

    static void pushStall() {
        for (int i = pipeline.length - 1; i > 1; i--) {
            pipeline[i] = pipeline[i - 1];
        }
        pipeline[1] = new Instruction("stall", null, -1);
    }

    static void pushSquash() {
        for (int i = pipeline.length - 1; i > 0; i--){
            pipeline[i] = pipeline[i-1];
        }
        pipeline[0] = new Instruction("squash", null, -1);
        return;
    }

    static void printPipelineRegisters(){
        System.out.print("\npc");
        for(int i = 0; i < pipeline_title.length; i++){
            System.out.print("\t" + pipeline_title[i]);
        }
        System.out.println();
        System.out.print(pc);
        for(int i = 0; i < pipeline.length; i++){
            System.out.print("\t" + pipeline[i].getOp());
        }
        System.out.println("\n");
        return;
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
                //curr_add = (instr.getLineNum() + 1) - getLabelAddress(instr.getField(0));
                curr_add = getLabelAddress(instr.getField(0));
                bin_instr = "000010 " + numToBinString(curr_add, 26);
                break;
            case "jr":
                bin_instr = "000000 " + getRegBin(instr.getField(0)) + " 000000000000000 001000";
                break;
            case "jal":
                //curr_add = (instr.getLineNum() + 1) - getLabelAddress(instr.getField(0));
                curr_add = getLabelAddress(instr.getField(0));
                bin_instr = "000011 " + numToBinString(curr_add, 26);
                break;
            default:
                System.out.println("invalid instruction: " + op);
                System.exit(-1);
        }
        System.out.println(bin_instr);
    }

    static void execInstr(Instruction instr) throws Exception {
        String op = instr.getOp().toLowerCase();
        int op1;
        int op2;
        int offset;

        if (pipeline[1].getOp().equals("lw")){
            String[] fields = instr.getFields();
            for (int i = 0; i < fields.length; i++){
                if (fields[i].equals(pipeline[1].getField(0))){
                    stall++;
                    //pc++; Not sure why this is here again
                    break;
                }
            }
        }

        switch(op) {
            case "add":
                op1 = regfile[getRegNum(instr.getField(1))];
                op2 = regfile[getRegNum(instr.getField(2))];
                regfile[getRegNum(instr.getField(0))] = op1 + op2;
                break;
            case "or":
                op1 = regfile[getRegNum(instr.getField(1))];
                op2 = regfile[getRegNum(instr.getField(2))];
                regfile[getRegNum(instr.getField(0))] = op1 | op2;
                break;
            case "and":
                op1 = regfile[getRegNum(instr.getField(1))];
                op2 = regfile[getRegNum(instr.getField(2))];
                regfile[getRegNum(instr.getField(0))] = op1 & op2;
                break;
            case "addi":
                op1 = regfile[getRegNum(instr.getField(1))];
                op2 = getImmediate(instr.getField(2));
                regfile[getRegNum(instr.getField(0))] = op1 + op2;
                break;
            case "sll":
                op1 = regfile[getRegNum(instr.getField(1))];
                op2 = regfile[getRegNum(instr.getField(2))];
                regfile[getRegNum(instr.getField(0))] = op1 << op2;
                break;
            case "sub":
                op1 = regfile[getRegNum(instr.getField(1))];
                op2 = regfile[getRegNum(instr.getField(2))];
                regfile[getRegNum(instr.getField(0))] = op1 - op2;
                break;
            case "slt":
                op1 = regfile[getRegNum(instr.getField(1))];
                op2 = regfile[getRegNum(instr.getField(2))];
                if (op1 < op2) {
                    regfile[getRegNum(instr.getField(0))] = 1;
                }
                else {
                    regfile[getRegNum(instr.getField(0))] = 0;
                }
                break;
            case "beq":
                op1 = regfile[getRegNum(instr.getField(0))];
                op2 = regfile[getRegNum(instr.getField(1))];
                if (op1 == op2) {
                    instr.setBranchPC(pc + getLabelAddress(instr.getField(2)) - (instr.getLineNum() + 1));
                    delay = true;
                }
                break;
            case "bne":
                op1 = regfile[getRegNum(instr.getField(0))];
                op2 = regfile[getRegNum(instr.getField(1))];
                if (op1 != op2) {
                    instr.setBranchPC(pc + getLabelAddress(instr.getField(2)) - (instr.getLineNum() + 1));
                    delay = true;
                }
                break;
            case "lw":
                op1 = regfile[getRegNum(instr.getField(2))];
                offset = getImmediate(instr.getField(1));
                regfile[getRegNum(instr.getField(0))] = ram[offset + op1];
                break;
            case "sw":
                op1 = regfile[getRegNum(instr.getField(2))];
                offset = getImmediate(instr.getField(1));
                ram[offset + op1] = regfile[getRegNum(instr.getField(0))];
                break;
            case "j":
                instr.setBranchPC(getLabelAddress(instr.getField(0))-1);
                squash++;
                break;
            case "jr":
                instr.setBranchPC(regfile[getRegNum(instr.getField(0))]-1);
                squash++;
                break;
            case "jal":
                regfile[31] = pc + 1;
                instr.setBranchPC(getLabelAddress(instr.getField(0))-1);
                squash++;
                break;
            default:
                System.out.println("invalid instruction: " + op);
                System.exit(-1);
        }
    }

    static String getRegBin(String reg) throws Exception {
        for (int i = 0; i < 64; i++) {
            if (register_asm_lut[i].equals(reg)) {
                return register_bin_lut[i % 32];
            }
        }
        throw new Exception("Register " + reg + " not valid");
    }

    static int getRegNum(String reg) throws Exception {
        for (int i = 0; i < 64; i++) {
            if (register_asm_lut[i].equals(reg)) {
                return i % 32;
            }
        }
        throw new Exception("Register " + reg + " not valid");
    }

    static int getImmediate(String num) throws Exception {
        return Integer.parseInt(num);
    }

    static int getLabelAddress(String match) throws Exception {
        for (Label label : labels) {
            if (match.equals(label.getLabel())) {
                return label.getAddress();
            }
        }
        throw new Exception("Label " + match + " not valid");
    }

    static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
}

}
