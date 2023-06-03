// imports

import java.util.List;

public abstract class Location {
    // fields
    private String name;
    private List<String> temp; // temporary list of neighbors
    private List<Location> neighbors; // list of neighbors as Location objects
    private Area area;

    // constructor
    public Location(String name, List<String> temp, Area area){
        this.name = name;
        this.temp = temp;
        setArea(area);
    }


    // getters and setters
    public String getName(){
        return this.name;
    }

    public List<String> getTemp(){
        return temp;
    }


    public List<Location> getNeighbors(){
        return this.neighbors;
    }
    public void setNeighbors(List<Location> n){
        this.neighbors = n;
    }
    public Location getNeighbor(String name){
        for (Location neighbor : getNeighbors()) {
            if (neighbor.getName().equals(name)) {
                return neighbor;
            }
        }
        return null;
    }


    public void setArea(Area a){
        this.area = a;
    }
    public Area getArea(){
        return this.area;
    }
}
