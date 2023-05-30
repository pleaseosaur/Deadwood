// imports

import java.util.*;


public class SetupGame {

    // constructor
    public SetupGame(int numPlayers) {
        // Exception catch
        try{
            GameData.initializeGameData(getClass().getResourceAsStream("/resources/xml/board.xml"),
                                        getClass().getResourceAsStream("/resources/xml/cards.xml"));
            setPlayers(numPlayers);
            setDays(numPlayers);
        }
        catch (Exception e){
            System.out.println("Error loading Game Data.");
            e.printStackTrace();
        }
    }

    public Map<String, Map<Integer, String>> setTokens() {
        Map<String, Map<Integer, String>> tokens = new HashMap<>();
        List<String> colors = Arrays.asList("b", "c", "g", "o", "p", "r", "v", "w", "y");
        String prefix = "/resources/images/tokens/";

        for(String color : colors) {
            Map<Integer, String> token = new HashMap<>();
            for(int rank = 1; rank <= 6; rank++) {
                String path = prefix + color + rank + ".png";
                token.put(rank, path);
            }
            tokens.put(color, token);
        }

        return tokens;
    }

    // getters and setters
    public List<Player> setPlayers(int numPlayers) {

        String[] colors = {"b", "c", "g", "o", "p", "r", "v", "y", "w"};
        List<Player> players = new ArrayList<>();

        int rank = 1;
        int credits = 0;
        int dollars = 0;

        if(numPlayers == 5) {
            credits = 2;
        } else if(numPlayers == 6) {
            credits = 4;
        } else if(numPlayers == 7 || numPlayers == 8) {
            rank = 2;
        }

        for(int i = 1; i <= numPlayers; i++) {
            String playerName = "Player " + i;
            Player player = new Player(playerName, rank, credits, dollars, colors[i - 1]);
            players.add(player);
        }

        return players;
    }

    public int setDays(int numPlayers) {
        int days = 0;
        if(numPlayers == 2 || numPlayers == 3) {
            days = 3;
        } else {
            days = 4;
        }
        return days;
    }
}
