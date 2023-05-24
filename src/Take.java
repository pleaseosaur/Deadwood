public class Take {
    // fields
    private int number;
    private Area area;

    // constructor
    public Take(int n, Area a){
        setNumber(n);
        setArea(a);
    }

    // getters and setters
    public void setNumber(int n){
        this.number = n;
    }

    public int getNumber(){
        return this.number;
    }

    public void setArea(Area a){
        this.area = a;
    }

    public Area getArea(){
        return this.area;
    }
}
