// imports

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.List;

public class UI {
    // fields
    final Scanner scanner;


    // constructor
    public UI() {
        this.scanner = new Scanner(System.in);
    }


    // displayMessage: prints message to terminal
    public void displayMessage(String message){
        for(char c : message.toCharArray()){ // iterate through each character in message
            try {
                System.out.print(c); // print each character
                System.out.flush(); // flush buffer
                Thread.sleep(10); // sleep for 10 milliseconds
            } catch (InterruptedException e) {
                e.printStackTrace(); // print stack trace if interrupted
            }
        }

        System.out.println();
    }
    // displayPrompt: prints message to terminal without newline
    public void displayPrompt(String message){
        for(char c : message.toCharArray()){ // iterate through each character in message
            try {
                System.out.print(c); // print each character
                System.out.flush(); // flush buffer
                Thread.sleep(10); // sleep for 10 milliseconds
            } catch (InterruptedException e) {
                e.printStackTrace(); // print stack trace if interrupted
            }
        }
    }
    // displayWelcomeMessage: initial welcome prompt
    public void displayWelcomeMessage() {
        displayMessage("Welcome to Deadwood!\n"); // basic - add more later if desired
        displayMessage("When available actions/selections are displayed, please enter the number corresponding to your choice.");
        displayMessage("Once the game has started, you may type any of the following commands at any time:\n");
        helpMessage();
    }
    // helpMessage: displays help message
    public void helpMessage() {
        displayMessage("'help' - displays this message");
        displayMessage("'stats' - displays your current stats");
        displayMessage("'board' or 'view' - displays the board");
        displayMessage("'back' - returns to the actions menu");
        displayMessage("'quit' - ends the game\n");
    }
    // displays player's stats
    public void displayStats(Player p){
        if(p.hasRole()) { // if player has role
            displayMessage("\nHere are your current stats: \n" +
                    "Location: " + p.getLocation().getName() + "\n" +
                    "Role: " + p.getRole().getName() + "\n" +
                    "Rank: " + p.getRank() + "\n" +
                    "Credits: " + p.getCredits() + "\n" +
                    "Dollars: " + p.getDollars() + "\n" +
                    "Practice Chips: " + p.getPracticeChips() + "\n");
        } else { // if player does not have role
            displayMessage("\nHere are your current stats: \n" +
                    "Location: " + p.getLocation().getName() + "\n" +
                    "Rank: " + p.getRank() + "\n" +
                    "Credits: " + p.getCredits() + "\n" +
                    "Dollars: " + p.getDollars() + "\n" +
                    "Practice Chips: " + p.getPracticeChips() + "\n");
        }
    }
    // startDayMessage: displays message at start of each day
    public void startDayMessage(int days) {
        if(days == 1) {
            displayMessage("\nA new day has begun!");
            displayMessage("This is the last day, so make it count!");
        } else if(days == 0) {
            displayMessage("\nThe final day has come to a close and the game has ended!");
        }else {
            displayMessage("\nA new day has begun!");
            displayMessage("There are " + days + " days remaining.");
        }

    }
    // startTurnMessage: displays message at start of each turn
    public void startTurnMessage(Player player) {
        String playerName = player.getName(); // get player name
        String locationName = player.getLocation().getName(); // get player location name

        displayMessage("\nIt is " + playerName + "'s turn.");
        displayMessage("\nYour current location is: " + locationName + "\n");
    }
    // diceRollAnimation: conducts unnecessary fun
    public void diceRollAnimation() {
        displayPrompt("Rolling dice ");
        for(int i = 3; i > 0; i--) {
            displayPrompt(". ");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }




    // getPlayerCount: prompts for and gets player count from user
    public int getPlayerCount() {
        displayPrompt("Please enter the number of players: ");
        try {
            String numPlayersInput = scanner.next().toLowerCase(); // get input
            int numPlayers = 0;
            if(numPlayersInput.equals("quit")) {
                quitGame(); // quit game if user enters 'quit'
            } else {
                numPlayers = Integer.parseInt(numPlayersInput); // parse input to int
            }
            if(numPlayers < 2 || numPlayers > 8) {
                displayMessage("Sorry, the number of players should be between 2 and 8.");
                return getPlayerCount(); // recursively call getPlayerCount() until valid input is given
            } else {
                return numPlayers;
            }
        } catch(NumberFormatException e) {
            displayMessage("That doesn't look like any number I've ever seen!");
            return getPlayerCount(); // recursively call getPlayerCount() until valid input is given
        }
    }

    // promptRename: asks users if they want custom names
    public boolean promptRename(){
        Map<String, String> options = new HashMap<>(); // options for user input
        options.put("y", "yes");
        options.put("n", "no");

        displayPrompt("Would you like to create custom names? (y/n): ");

        String choice = scanner.next().toLowerCase(); // get input

        if(options.containsKey(choice)) { // check if input is valid
            return options.get(choice).equals("yes"); // return true if user wants custom names
        } else if(options.containsValue(choice)) {
            return choice.equals("yes");
        } else {
            displayMessage("Please enter 'y' ('yes') or 'n' ('no').");
            return promptRename(); // recursively call promptRename() until valid input is given
        }
    }

    // getPlayerName: assigns player name for given player
    public String getPlayerName(String name) {

        displayPrompt("\nPlease enter new name for " + name + ": ");
        String newName;

        try {
            newName = scanner.next(); // get input
        } catch (Exception e) {
            return getPlayerName(name); // recursively call getPlayerName() until valid input is given
        }

        return newName;
    }


    // getPlayerAction: prompts user for action
    public String getPlayerAction(Player player, Map<String, String> availableRoles) {

        Map<String, String> availableActions = new HashMap<>(); // available actions for user input

        switch (player.getLocation().getName()) {
            case "Trailer" -> trailerActions(player, availableActions); // get actions for trailer
            case "Casting Office" -> officeActions(player, availableActions); // get actions for casting office
            default -> setActions(player, availableActions, availableRoles); // get actions for other locations
        }

        displayMessage(buildPrompt(availableActions)); // display prompt

        displayPrompt("Please enter your desired action: ");

        return getChoiceInput(availableActions); // get input
    }


    // trailerActions: gets actions for when player is at trailer
    private void trailerActions(Player player, Map<String, String> availableActions) {
        buildActions(availableActions, "move", player.getHasMoved()); // add move action
        buildActions(availableActions, "end turn"); // add end turn action
    }

    // officeActions: gets actions for when player is at casting office
    private void officeActions(Player player, Map<String, String> availableActions) {
        if(player.getRank() < 6) { // check if player is not at max rank
            buildActions(availableActions, "move", player.getHasMoved()); // add move action
            buildActions(availableActions, "upgrade", player.getHasUpgraded()); // add upgrade action

        } else { // player is at max rank
            displayMessage("You are already at the highest rank. No remaining upgrades are available.\n\n");
            buildActions(availableActions, "move", player.getHasMoved()); // add move action

        }
        buildActions(availableActions, "end turn"); // add end turn action
    }

    // setActions: sets actions for player based on available actions
    private void setActions(Player player, Map<String, String> availableActions, Map<String, String> availableRoles) {
        if(player.hasRole() && !player.getHasActed() && !player.getHasTakenRole() && !player.getHasRehearsed()) { // check if player has role and has not acted or taken role
            if(player.getRole().isOnCard()) { // check if role is on card
                buildActions(availableActions, "rehearse", player.getHasRehearsed()); // add rehearse action
            }
            buildActions(availableActions, "act", player.getHasActed()); // add act action
        } else if(!availableRoles.containsKey("0")) {
            buildActions(availableActions, "take role", player.hasRole()); // add take role action
        }

        if(!player.getHasMoved() && !player.hasRole() && !player.getHasActed()) { // check if player has not moved
            buildActions(availableActions, "move", player.getHasMoved()); // add move action
        }

        buildActions(availableActions, "end turn"); // add end turn action
    }


    // builds hashmap connecting available actions to prompts
    private void buildActions(Map<String, String> availableActions, String action, boolean alreadyDone) {
        if(!alreadyDone) { // if action has not already been done
            int i = availableActions.size() + 1; // get next available number
            availableActions.put(String.valueOf(i), action); // add action to hashmap
        }
    }

    private void buildActions(Map<String, String> availableActions, String action) { // overloaded method
        buildActions(availableActions, action, false); // default to false
    }


    // builds prompts for player to choose action
    public String buildPrompt(Map<String, String> availableActions) {
        StringBuilder prompt = new StringBuilder(); // initialize prompt

        prompt.append("\nYour available actions are:\n"); // add header

        for(Map.Entry<String, String> input : availableActions.entrySet()) { // for each action
            prompt.append(input.getKey()).append(". ").append(input.getValue()).append("\n"); // add action
        }
        return prompt.toString(); // return prompt
    }


    // gets choice from player
    public String getChoiceInput(Map<String, String> availableActions) {
        String choice = scanner.next(); // get choice

        if(choice.equals("quit")) {
            return "quit";
        } else if (choice.equals("help")) {
            return "help";
        } else if(choice.equals("stats")) {
            return "stats";
        } else if (choice.equals("back")) {
            return "back";
        } else if(choice.equals("view") || choice.equals("board")) {
            return "view";
        } else if (availableActions.containsKey(choice)) { // if choice is key
            return availableActions.get(choice); // return action
        } else if (availableActions.containsValue(choice)) { // if choice is value
            return choice; // return action
        }

        displayMessage("That is not a valid choice.");
        displayMessage("Please enter a valid choice: ");

        return getChoiceInput(availableActions); // try again
    }


    // gets destination locations when player chooses to move
    public String promptMove(Player player) {
        List<Location> neighbors = player.getLocation().getNeighbors(); // get neighbors
        Map<String, String> options = new HashMap<>(); // initialize options
        StringBuilder prompt = new StringBuilder(); // initialize prompt

        int i = 1;

        prompt.append("\nThe available locations are: \n"); // add header

        for(Location neighbor : neighbors) { // for each neighbor
            if(neighbor.isSet()) {
                Set set = (Set) neighbor;
                if(set.getScene().isWrapped()) {
                    prompt.append(i).append(". ").append(neighbor.getName()).append(" (wrapped)").append("\n"); // add location
                } else {
                    prompt.append(i).append(". ").append(neighbor.getName()).append("\n"); // add location
                }
            } else {
                prompt.append(i).append(". ").append(neighbor.getName()).append("\n"); // add location
            }
            options.put(Integer.toString(i), neighbor.getName()); // add location to options
            i++;
        }

        displayMessage(prompt.toString()); // display prompt
        displayPrompt("\nPlease enter the location you would like to move to: ");

        return getChoiceInput(options); // get choice
    }

    // gets available roles when player decides to take role
    public String promptRole(Map<String, String> availableRoles) {
        Map<String, String> options = new HashMap<>(); // initialize options
        StringBuilder prompt = new StringBuilder(); // initialize prompt

        int i = 1;

        prompt.append("\nThe available roles are: \n"); // add header

        for(Map.Entry<String, String> role : availableRoles.entrySet()) { // for each role
            prompt.append(i).append(". ").append(role.getKey()).append(role.getValue()).append("\n"); // add role
            options.put(Integer.toString(i), role.getKey()); // add role to options
            i++;
        }

        displayMessage(prompt.toString()); // display prompt
        displayPrompt("\nPlease enter the role you would like to take: ");

        return getChoiceInput(options); // get choice
    }

    // gets available ranks when player decides to upgrade
    public String promptUpgrade(Map<Integer, List<String>> upgrades) {
        Map<String, String> options = new HashMap<>(); // initialize options
        StringBuilder prompt = new StringBuilder(); // initialize prompt

        int i = 1;

        prompt.append("\nThe available upgrades are: \n"); // add header
        for(Map.Entry<Integer, List<String>> upgrade : upgrades.entrySet()) {
            String choices = "Rank " + upgrade.getKey() + " - " + String.join(" or ", upgrade.getValue()); // build choice
            prompt.append(upgrade.getKey()).append(". ").append(choices).append("\n"); // add upgrade
            options.put(Integer.toString(upgrade.getKey()), String.valueOf(upgrade.getKey())); // add upgrade to options
            i++;
        }

        displayMessage(prompt.toString()); // display prompt
        displayPrompt("\nPlease enter the rank you would like to upgrade to: ");

        return getChoiceInput(options); // get choice
    }

    public String promptUpgradePayment() {
        StringBuilder prompt = new StringBuilder(); // initialize prompt
        Map<String, String> options = new HashMap<>(); // initialize options

        prompt.append("\nPlease enter the payment option you would like to use: \n"); // add header
        prompt.append("1. Dollars\n");
        prompt.append("2. Credits\n");
        options.put("1", "dollars");
        options.put("2", "credits");

        displayMessage(prompt.toString()); // display prompt

        return getChoiceInput(options); // get choice
    }

    // quit game
    public void quitGame() {
        displayMessage("\nYour loss! Enjoy not being a world-renown thespian.");
        System.exit(0);
    }
}
