import java.io.*;
import java.util.*;

public class Register {
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
        this.val = new_val
    }

    public String getName() {
        return reg;
    }

}