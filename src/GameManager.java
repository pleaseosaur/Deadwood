// imports

import java.util.*;

public class GameManager {
    // fields
    private List<Player> players;
    private Map<String, Map<Integer, String>> tokens;
    private Player currentPlayer;
    private int days;
    private Board board;
    private Dice dice;




    //********************************************************************************
    //                                Constructor
    //********************************************************************************
    public GameManager(int numPlayers) {
        setupGame(numPlayers);
    }




    //********************************************************************************
    //                             Game Setup & State
    //********************************************************************************
    public void setupGame(int numPlayers) {
        SetupGame setup = new SetupGame(numPlayers);
        this.players = setup.setPlayers(numPlayers);
        setDays(setup.setDays(numPlayers));
        setCurrentPlayer();
        this.board = Board.getInstance();
        this.dice = new Dice(6);
        this.tokens = setup.setTokens();
        resetPlayers();
    }


    public int endDay() {
        if(dayHasEnded()) {
            decrementDay();
            if(!gameHasEnded()){
                resetPlayers();
                resetRoles();
                setOpenScenes(10);
                board.dealCards(); // deal cards
                resetTakes();
            } else {
                return 1;
            }
        }
        return 0;
    }


    public void resetPlayers() {
        int startingX = 991 + 10; // starting x coordinate
        int startingY = 248 + 80; // starting y coordinate

        int counter = 0; // counter for player number
        for (Player player : getPlayers()) {
            int currentX = startingX + 45 * (counter % 4); // current x coordinate
            int currentY = startingY + 45 * (counter / 4); // current y coordinate

            player.setLocation(board.getLocation("Trailer")); // set all players to trailer
            player.setHasActed(false); // reset player actions
            player.setHasMoved(false);
            player.setHasRehearsed(false);
            player.setHasTakenRole(false);
            player.setHasUpgraded(false);
            player.setRole(); // remove role from all players
            player.resetPracticeChips(); // reset practice chips for all players
            player.setPosition(currentX, currentY); // return players to starting positions

            counter++; // increment counter
        }
    }


    // resetRoles: resets all off-card roles to be available for next day
    public void resetRoles() {
        // iterate through set locations and reset the default roles
        for(Location location : board.getAllLocations().values()) {
            if(location instanceof Set set) { // if location is a set
                for(Role role : set.getRoles()) {
                    role.setTaken(false);
                }
            }
        }
    }


    public void resetTakes() {
        for(Location location : board.getAllLocations().values()) {
            if(location instanceof Set set) { // if location is a set
                set.resetTakes();
            }
        }
    }

    // scoreGame: tallies scores and returns map linking players and scores
     public Map<String, Integer> scoreGame() {
         Map<String, Integer> scores = new LinkedHashMap<>();
         for (Player player : players) {
             int score = player.getDollars() + player.getCredits() + (player.getRank()*5);
             scores.put(player.getName(), score);
         }

         return scores;
     }




    //********************************************************************************
    //                               Player Actions
    //********************************************************************************
    public void move(String location) {
        Location destination = currentPlayer.getLocation().getNeighbor(location);

        int x = destination.getArea().getX();
        int y = destination.getArea().getY();

        int counter = 0;
        switch(location) {
            case "Casting Office", "Trailer" -> {
                x += 10;
                y += 80;
                for(Player anotherPlayer : players) {
                    if(!anotherPlayer.equals(currentPlayer)) {

                        if(anotherPlayer.getLocation().equals(destination) && !anotherPlayer.hasRole()) {
                            x += 45;
                            counter++;

                            if(counter == 4) {
                                x -= 180;
                                y += 45;
                                counter = 0;
                            }
                        }
                    }
                }
            }

            case "Hotel", "Church", "Bank", "Saloon", "General Store", "Train Station" -> {
                y += 120;
                for(Player anotherPlayer : players) {
                    if(!anotherPlayer.equals(currentPlayer)) {

                        if(anotherPlayer.getLocation().equals(destination) && !anotherPlayer.hasRole()) {
                            x += 45;
                            counter++;

                            if(counter == 4) {
                                x -= 180;
                                y = destination.getArea().getY();
                                counter = 0;
                            }
                        }
                    }
                }
            }

            case "Secret Hideout" -> {
                y += 120;
                for(Player anotherPlayer : players) {
                    if(!anotherPlayer.equals(currentPlayer)) {

                        if(anotherPlayer.getLocation().equals(destination) && !anotherPlayer.hasRole()) {
                            x += 45;
                        }
                    }
                }
            }

            case "Ranch" -> {
                y += 120;
                for(Player anotherPlayer : players) {
                    if(!anotherPlayer.equals(currentPlayer)) {

                        if(anotherPlayer.getLocation().equals(destination) && !anotherPlayer.hasRole()) {
                            x += 45;
                            counter++;

                            if(counter == 3) {
                                x -= 120;
                                y += 45;
                                counter = 0;
                            }
                        }
                    }
                }
            }

            case "Jail" -> {
                x += 120;
                y += 120;
                for(Player anotherPlayer : players) {
                    if(!anotherPlayer.equals(currentPlayer)) {

                        if(anotherPlayer.getLocation().equals(destination) && !anotherPlayer.hasRole()) {
                            x -= 45;
                            counter++;

                            if(counter == 4) {
                                x += 180;
                                y += 50;
                                counter = 0;
                            }
                        }
                    }
                }
            }

            default -> {
                y += 120;
                for(Player anotherPlayer : players) {
                    if(!anotherPlayer.equals(currentPlayer)) {

                        if(anotherPlayer.getLocation().equals(destination) && !anotherPlayer.hasRole()) {
                            x += 45;
                            counter++;

                            if(counter == 4) {
                                x -= 180;
                                y += 45;
                                counter = 0;
                            }
                        }
                    }
                }
            }


        }

        currentPlayer.setLocation(destination); // set player location
        currentPlayer.setPosition(x, y); // update player position
        currentPlayer.setHasMoved(true); // set hasMoved flag
    }


    public void upgrade(Upgrade upgrade, String currency) {
        int rank = upgrade.getRank();
        int price = upgrade.getPrice();

        if(currency.equals("dollars")) {
            currentPlayer.setDollars(currentPlayer.getDollars() - price);
        } else {
            currentPlayer.setCredits(currentPlayer.getCredits() - price);
        }

        currentPlayer.setRank(rank);
        currentPlayer.setHasUpgraded(true);
    }


    public void takeRole(String r) {
        Set set = (Set) currentPlayer.getLocation();
        List<Role> allRoles = new ArrayList<>(set.getRoles());
        List<Role> onCardRoles = new ArrayList<>(set.getScene().getRoles());

        allRoles.removeAll(onCardRoles);
        allRoles.addAll(onCardRoles);

        for(Role role : allRoles) {
            if(!role.isTaken()) {
                if(role.getName().equals(r)) {
                    int x = role.getArea().getX() + 3;
                    int y = role.getArea().getY() + 3;

                    if(onCardRoles.contains(role)) {
                        x += set.getArea().getX() - 3;
                        y += set.getArea().getY() - 3;
                    }

                    currentPlayer.setPosition(x, y);
                    currentPlayer.setRole(role);
                    currentPlayer.setHasTakenRole(true);
                    role.setTaken(true);
                }
            }
        }
    }


    public void rehearse() {
        currentPlayer.addPracticeChips();
        currentPlayer.setHasRehearsed(true);
    }


    public int[] act() {
        Set set = (Set) currentPlayer.getLocation(); // get current set
        int budget = set.getScene().getBudget(); // get budget

        int diceResult = dice.rollDie(); // roll die

        int totalResult = diceResult + currentPlayer.getPracticeChips(); // add practice chips to result

        int isSuccess = 0;
        if(totalResult >= budget){ // acting success
            isSuccess = 1;
            
            set.decrementTakes(); // decrement takes
        }
        
        //payout
        actPay(currentPlayer.getRole().isOnCard(), isSuccess);
        currentPlayer.setHasActed(true);

        int[] wrapResults;
        int bonusRolled = 0;
        int endDay = 0;
        int endGame = 0;
        if(set.getScene().isWrapped()){
            wrapResults = wrapScene();
            bonusRolled = wrapResults[0];
            endDay = wrapResults[1];
            endGame = wrapResults[2];
        }

        return new int[] {isSuccess, diceResult, bonusRolled, endDay, endGame};
    }




    //********************************************************************************
    //                               Payouts & Wrapping
    //********************************************************************************
    public int[] wrapScene() {

        int[] results = new int[3];

        int bonusRolled = 0;
        int endDay = 0;
        int endGame = 0;

        Location location = currentPlayer.getLocation();

        List<Player> allPlayers = new ArrayList<>(); // list of all players
        List<Player> onCardPlayers = new ArrayList<Player>(); // list of on card players
        List<Player> offCardPlayers = new ArrayList<Player>(); // list of off card players

        for(Player player : getPlayers()) { // for each player
            if(player.getLocation().equals(location)) { // if player is on current location
                if(player.getRole() != null) {
                    if(player.getRole().isOnCard()) {
                        onCardPlayers.add(player); // add to on card players
                        allPlayers.add(player); // add to all players
                    } else {
                        offCardPlayers.add(player); // add to off card players
                        allPlayers.add(player); // add to all players
                    }
                }
            }
        }

        if(onCardPlayers.size() > 0) { // if there are on card players
            wrapBonus(onCardPlayers, offCardPlayers); // roll for wrap bonuses
            bonusRolled = 1;
        }

        for(Player player : allPlayers) { // for each player
            player.setRole(null); // remove role from all players
            player.resetPracticeChips(); // reset practice chips for all players
        }
        // decrement Open Scenes
        setOpenScenes(getOpenScenes()-1);
        if (dayHasEnded()) {
            endGame = endDay();
            endDay = 1;
        }

        results[0] = bonusRolled;
        results[1] = endDay;
        results[2] = endGame;

        return results;
    }


    public void actPay(Boolean onCard, int isSuccess){
        if(isSuccess == 1) {
            if(onCard) { // if star
                currentPlayer.addCredits(2); // add 2 credits
            } else { // if extra
                currentPlayer.addDollars(1); // add 1 dollar
                currentPlayer.addCredits(1); // add 1 credit
            }

        } else if(!onCard) { // if unsuccessful and extra
            currentPlayer.addDollars(1); // add 1 dollar
        }
    }


    // wrapBonus: rolls for wrap bonuses if players are on card
    public void wrapBonus(List<Player> onCardPlayers, List<Player> offCardPlayers) {
        Card card = ((Set) currentPlayer.getLocation()).getScene();
        List<Role> onCardRoles = new ArrayList<>(card.getRoles());
        List<Integer> results = dice.wrapRoll(card.getBudget()); // roll number of dice equal to budget
        Map<Role, Integer> distribution = new HashMap<>(); // map of roles to results

        onCardRoles.sort(Comparator.comparing(Role::getRank).reversed()); // sorts roles by rank

        for(Role role : onCardRoles) {
            distribution.put(role, 0); // add roles to distribution map
        }

        Iterator<Integer> resultIterator = results.iterator(); // iterator for results
        Iterator<Role> roleIterator = onCardRoles.iterator(); // iterator for roles

        while(resultIterator.hasNext()) { // while there are more results
            if(!roleIterator.hasNext()) { // if there are no more roles
                roleIterator = onCardRoles.iterator(); // reset role iterator
            }

            Role role = roleIterator.next(); // get next role
            int result = resultIterator.next(); // get next result

            distribution.put(role, distribution.get(role) + result); // add result to role in distribution map
        }

        for(Player player : onCardPlayers) {
            player.addDollars(distribution.get(player.getRole())); // distribute dollars to players
        }

        for(Player player : offCardPlayers) { // for each off card player
            player.addDollars(player.getRole().getRank()); // add dollars = role rank to player
        }
    }




    //********************************************************************************
    //                               Player Modifiers
    //********************************************************************************
    public void renamePlayer(Player player, String name){
        player.setName(name);
    }


    public void endTurn() {
        int currentIndex = getPlayers().indexOf(currentPlayer); // get index of current player
        int nextIndex = (currentIndex + 1) % getPlayers().size(); // get index of next player

        currentPlayer.setHasMoved(false); // reset player actions
        currentPlayer.setHasUpgraded(false);
        currentPlayer.setHasActed(false);
        currentPlayer.setHasRehearsed(false);
        currentPlayer.setHasTakenRole(false);

        setCurrentPlayer(getPlayers().get(nextIndex)); // set next player as current player
    }
    



    //********************************************************************************
    //                               Player Options
    //********************************************************************************
    public List<String> getAvailableActions() {

        List<String> availableActions = new ArrayList<>();
        Location currentLocation = currentPlayer.getLocation();

        if(!playerHasRole()) {
            if(playerCanMove()) {
                availableActions.add("Move");
            }
            if(playerCanTakeRole(currentLocation)) {
                availableActions.add("Take Role");
            }
        }

        if(playerHasRole()) {
            if(playerCanAct()) {
                availableActions.add("Act");
                if(playerCanRehearse(currentLocation)) {
                    availableActions.add("Rehearse");
                }
            }
        }

        if(playerCanUpgrade(currentLocation)) {
            availableActions.add("Upgrade");
        }

        availableActions.add("End Turn");

        return availableActions;
    }


    public List<String> getAvailableLocations() {
        List<String> availableLocations = new ArrayList<>();
        Location currentLocation = currentPlayer.getLocation();

        for(Location neighbor : currentLocation.getNeighbors()) {
            availableLocations.add(neighbor.getName());
        }

        return availableLocations;
    }

    
    public Map<String, String> getAvailableRoles() {

        Location playerLocation = currentPlayer.getLocation();
        Map<String, String> availableRoles = new HashMap<>();


        if(playerLocation.isSet()){ // if player is on a set
            Set set = (Set) playerLocation; // cast player location to set

            if(!set.getScene().isWrapped()) {
                List<Role> offCardRoles = set.getRoles(); // get off card roles
                List<Role> onCardRoles = set.getScene().getRoles(); // get on card roles

                int rank = currentPlayer.getRank();

                for(Role role : offCardRoles) {
                    if(!role.isTaken() && role.getRank() <= rank) {
                        availableRoles.put(role.getName(), " (Off-Card) Rank: " + role.getRank());
                    }
                }

                for(Role role : onCardRoles) {
                    if(!role.isTaken() && role.getRank() <= rank) {
                        availableRoles.put(role.getName(), " (On-Card) Rank: " + role.getRank());
                    }
                }
            }
        }
        return availableRoles;
    }

    
    public Map<Integer, List<String>> getAvailableUpgrades() {
        
        Map<Integer, List<String>> availableUpgrades = new HashMap<>();

        if(currentPlayer.getLocation() instanceof CastingOffice office) {
            List<Upgrade> upgrades = office.getUpgrades();

            for (Upgrade upgrade : upgrades) {

                String options = upgrade.getPrice() + " " + upgrade.getCurrency();

                if(availableUpgrades.containsKey(upgrade.getRank())) {
                    availableUpgrades.get(upgrade.getRank()).add(options);
                } else {
                    List<String> newUpgrade = new ArrayList<>();
                    newUpgrade.add(options);
                    availableUpgrades.put(upgrade.getRank(), newUpgrade);
                }
            }
        }
        
        return availableUpgrades;
    }




    //********************************************************************************
    //                                Get Methods
    //********************************************************************************
    public Map<Card, List<Integer>> getCards() {
        Collection<Location> locations = board.getAllLocations().values();
        Map<Card, List<Integer>> cards = new HashMap<>();

        for(Location location : locations) {
            if(location instanceof Set set) {
                Area area = set.getArea();
                int x = area.getX();
                int y = area.getY();
                int w = area.getW();
                int h = area.getH();
                cards.put(set.getScene(), List.of(x, y, w, h));
            }
        }

        return cards;
    }

    
    public Map<Take, List<Integer>> getTakes() {
        Collection<Location> locations = board.getAllLocations().values();
        Map<Take, List<Integer>> takes = new HashMap<>();

        for(Location location : locations) {
            if(location instanceof Set set) {
                for(Take take : set.getTakes()) {
                    Area area = take.getArea();
                    int x = area.getX();
                    int y = area.getY();
                    int w = area.getW();
                    int h = area.getH();
                    takes.put(take, List.of(x, y, w, h));
                }
            }
        }

        return takes;
    }

    
    public Map<String, int[]> getTokens() {
        Map<String, int[]> pathmap = new HashMap<>();

        for(Player player : players) {
            int[] position = player.getPosition();
            pathmap.put(tokens.get(player.getColor()).get(player.getRank()), position);
        }

        return pathmap;
    }


    public int getDays() {
        return this.days;
    }

    public int getOpenScenes() {
        return board.getOpenScenes();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }




    //********************************************************************************
    //                                Set Methods
    //********************************************************************************

    // setCurrentPlayer: sets current player to first player in list
    public void setCurrentPlayer() {
        this.currentPlayer = getPlayers().get(0);
    }


    // setCurrentPlayer: sets current player to given player
    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }


    public void setDays(int n) {
        this.days = n;
    }

    public void decrementDay() {
        setDays(getDays() - 1);
    }

    public void setOpenScenes(int num) {
        board.setOpenScenes(num);
    }




    //********************************************************************************
    //                               Boolean Checks
    //********************************************************************************
    public boolean dayHasEnded() {
        return board.checkEndDay();
    }


    private boolean gameHasEnded() {
        return getDays() == 0;
    }


    private boolean playerHasRole() {
        return currentPlayer.getRole() != null;
    }


    private boolean playerCanMove() {
        return !currentPlayer.getHasMoved() && !currentPlayer.getHasActed();
    }


    private boolean playerCanTakeRole(Location currentLocation) {

        if(currentLocation instanceof Set) {
            //TODO - may need to add additional check for hasTakenRole
            return !((Set) currentLocation).getScene().isWrapped() && !currentPlayer.getHasTakenRole();
        }

        return false;
    }


    private boolean playerCanAct() {
        return !currentPlayer.getHasActed() && !currentPlayer.getHasRehearsed() && !currentPlayer.getHasTakenRole();
    }


    private boolean playerCanRehearse(Location currentLocation) {
        int budget = ((Set) currentLocation).getScene().getBudget();
        return currentPlayer.getRole().isOnCard() && !currentPlayer.getHasRehearsed() && !(currentPlayer.getPracticeChips() == budget-1);
    }


    private boolean playerCanUpgrade(Location currentLocation) {
        return currentLocation instanceof CastingOffice;
    }
}
