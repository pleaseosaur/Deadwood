public class Role {
    // fields
    private String name;
    private String flavorText;
    private int rank;
    private Area area;
    private boolean onCard;
    private boolean taken;


    // constructor
    public Role(String n, int r, Area a, String f, boolean o, boolean t) {
        setName(n);
        this.flavorText = f;
        this.rank = r;
        setOnCard(o);
        setTaken(t);
        setArea(a);
    }


    // getters and setters
    public String getName(){
        return name;
    }
    public void setName(String n){
        this.name = n;
    }

    public int getRank(){
        return rank;
    }

    public boolean isOnCard(){
        return onCard;
    }
    public void setOnCard(boolean b){
        this.onCard = b;
    }

    public boolean isTaken(){
        return taken;
    }
    public void setTaken(boolean t){
        this.taken = t;
    }

    public void setArea(Area a) {
        this.area = a;
    }
    public Area getArea() {
        return this.area;
    }
}
