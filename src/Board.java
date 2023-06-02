// imports

import java.util.Map;

public class Board {
    // fields
    private static Board board;
    private final String name;
    private final Map<String, Location> locations;
    private int openScenes;
    private final Deck deck;

    // constructor
    private Board(String n, Map<String, Location> l, int o) {
        this.name = n;
        this.locations = l;
        setOpenScenes(o);
        this.deck = Deck.getInstance();
        dealCards();
    }

    public static Board getInstance() {
        if(board == null) {
            throw new IllegalStateException("Board has not been initialized");
        }
        return board;
    }

    public static void initializeBoard(String n, Map<String, Location> l, int o) {
        if(board != null) {
            throw new IllegalStateException("Board has already been initialized");
        }
        board = new Board(n, l, o);
    }

    // getters and setters
    public String getName(){
        return this.name;
    }


    public Location getLocation(String name){
        return getAllLocations().get(name);
    }

    public Map<String, Location> getAllLocations(){
        return this.locations;
    }


    public int getOpenScenes(){
        return this.openScenes;
    }

    public void setOpenScenes(int o){
        this.openScenes = o;
    }


    public boolean checkEndDay(){
        return getOpenScenes() == 9;
    }


    public void dealCards() {
        for (Location location : locations.values()) {
            if (location instanceof Set set) {
                set.setScene(null); // clear the scene
                set.setScene(deck.drawScene());
            }
        }
    }
}
