// imports

import java.util.ArrayList;
import java.util.List;


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

    // getters and setters
    public List<Player> setPlayers(int numPlayers) {

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
            Player player = new Player("Player " + i, rank, credits, dollars);
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
