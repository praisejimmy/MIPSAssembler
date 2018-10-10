package MIPSAssembler;

public class Label{
    private String label;
    private int address;

    public Label(String l, int a){
        this.label = l;
        this.address = a;
    }

    public int getAddress(){
        return address;
    }

    public String getLabel(){
        return label;
    }

    public String toString(){
        return getLabel() + ", " + getAddress();
    }

}
