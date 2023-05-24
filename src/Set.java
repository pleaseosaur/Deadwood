// imports

import java.util.List;

public class Set extends Location {
    // fields
    private Card card;
    private List<Take> takes;
    private List<Role> roles;
    private Take currentTake;

    // constructor
    public Set(String name, List<String> neighbors, Area area, Card card, List<Take> takes, List<Role> roles) {
        super(name, neighbors, area);
        setScene(card);
        this.takes = takes;
        this.roles = roles;
        currentTake = this.takes.get(0);
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
        int currentIndex = getCurrentTakeIndex();
        int nextIndex = (currentIndex + 1) % this.takes.size();

        boolean wrap = (currentIndex == takes.size() - 1);

        currentTake = takes.get(nextIndex); // move to the next take
        getScene().setWrap(wrap);
    }

    public List<Role> getRoles(){
        return this.roles;
    }

    public int getCurrentTakeIndex(){
        return takes.indexOf(currentTake);
    }
}
