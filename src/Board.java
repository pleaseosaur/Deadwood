// imports

import java.util.ArrayList;
import java.util.List;
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
        return getOpenScenes() == 1;
    }


    public void dealCards() {
        for (Location location : locations.values()) {
            if (location instanceof Set set) {
                set.setScene(null); // clear the scene
                set.setScene(deck.drawScene());
            }
        }
    }

    public void displayBoard() {
        List<String> printLines = new ArrayList<>();

        int locationNameMax = 0;

        for (Map.Entry<String, Location> entry : locations.entrySet()) {
            String locationName = entry.getKey();
            locationNameMax = Math.max(locationNameMax, locationName.length());
        }

        int max = 0;
        for (Map.Entry<String, Location> entry : locations.entrySet()) {
            String locationName = entry.getKey();
            Location location = entry.getValue();
            List<Location> neighbors = location.getNeighbors();

            StringBuilder builder = new StringBuilder();

            for (Location neighbor : neighbors) {
                builder.append(neighbor.getName());
                builder.append(", ");
            }

            if (builder.length() > 0) {
                builder.setLength(builder.length() - 2);
            }

            int numDashes = locationNameMax - locationName.length() + 10;

            String dashes = "-".repeat(numDashes);
            String line = locationName + "  " + dashes + ">  " + builder;

            printLines.add(line);
            max = Math.max(max, line.length());
        }

        String borderLine = "*".repeat(max + 4);
        System.out.println(borderLine);

        for (String line : printLines) {
            System.out.println("* " + line);
        }

        System.out.println(borderLine);
    }

}
