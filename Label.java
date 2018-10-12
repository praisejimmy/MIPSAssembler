package MIPSAssembler;

public class Label{
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
