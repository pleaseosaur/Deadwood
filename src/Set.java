// imports

import java.util.ArrayList;
import java.util.List;

public class Set extends Location {
    // fields
    private Card card;
    private List<Take> takes;
    private final List<Take> backupTakes;
    private List<Role> roles;

    // constructor
    public Set(String name, List<String> neighbors, Area area, Card card, List<Take> takes, List<Role> roles) {
        super(name, neighbors, area);
        setScene(card);
        this.takes = takes;
        this.roles = roles;
        this.backupTakes = new ArrayList<>(takes);
    }


    // getters and setters
    public void setScene(Card s){
        this.card = s;
    }
    public Card getScene(){
        return this.card;
    }


    public List<Take> getTakes(){
        return this.takes;
    }

    // decrementTakes: decrements the current take and wraps the scene if necessary
    public void decrementTakes() {
        takes.remove(takes.size()-1);
        boolean wrap = takes.isEmpty();
        getScene().setWrap(wrap);
    }

    public void resetTakes() {
        takes.clear();
        takes.addAll(backupTakes);
    }

    public List<Role> getRoles(){
        return this.roles;
    }
}
