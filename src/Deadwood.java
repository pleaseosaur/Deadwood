// imports

import java.util.*;

public class Deadwood {
    // fields
    final UI ui;
    private final GameManager manager;
    private boolean gameActive;

    // constructor
    public Deadwood(GameManager manager) {
        this.manager = manager;
        this.ui = new UI();
    }

    // startGame: queries for player count, calls GameManager to set up accordingly, then starts game
    public void startGame() {
        ui.displayWelcomeMessage();
        // int players = ui.getPlayerCount();
        // manager.setupGame(players);
        setGameActive(true);
        // if(ui.promptRename()){
        //     renamePlayers();
        // }
        runGame();
    }

    public void setGameActive(boolean b) {
        this.gameActive = b;
    }

    public boolean getGameActive() {
        return this.gameActive;
    }


    // runGame: Main gameplay loop
    public void runGame() {
        // while game is active (no. days > 0)
        while(getGameActive()) { // TODO -- convert to a for loop for days in GUI version
            ui.startDayMessage(manager.getDays());
            // while day is active (no. open Scenes > 1)
            if(manager.getDays() == 0) { // this is kinda hacky but it works
                endGame();
            }
            while(!endDay()) {
                // while current player is active
                Player currentPlayer = manager.getCurrentPlayer();
                ui.startTurnMessage(currentPlayer);
                // display available actions (universal actions always available)
                boolean turnActive = true;
                while(turnActive) {
                    Map<String, String> availableRoles = getAvailableRoles(currentPlayer);
                    String action = ui.getPlayerAction(currentPlayer, availableRoles);
                    switch (action) {
                        case "move" -> {
                            String choice = ui.promptMove(currentPlayer);
                            switch (choice) {
                                case "quit" -> endGame();
                                case "help" -> ui.helpMessage();
                                case "back" -> ui.displayMessage("\nNo problem!");
                                case "stats" -> ui.displayStats(currentPlayer);
                                case "view" -> manager.displayBoard();
                                default -> {
                                    move(choice);
                                    ui.displayMessage("\nYou have moved to: " + currentPlayer.getLocation().getName());
                                }
                            }
                        }
                        case "take role" -> {
                            String choice = ui.promptRole(availableRoles);
                            switch (choice) {
                                case "quit" -> endGame();
                                case "help" -> ui.helpMessage();
                                case "back" -> ui.displayMessage("\nNo problem!");
                                case "stats" -> ui.displayStats(currentPlayer);
                                case "view" -> manager.displayBoard();
                                default -> {
                                    takeRole(choice);
                                    if (currentPlayer.getHasTakenRole()) {
                                        ui.displayMessage("\nYou have taken the role of: " + currentPlayer.getRole().getName());
                                    }
                                }
                            }
                        }
                        case "rehearse" -> {
                            Set set = (Set) currentPlayer.getLocation();
                            if(currentPlayer.getPracticeChips() == set.getScene().getBudget()-1) {
                                ui.displayMessage("\nYou have maxed out your practice chips! Try acting instead!");
                            } else {
                                rehearse();
                                ui.displayMessage("\nAnd a great rehearsal it was!");
                                ui.displayMessage("\nYou now have " + currentPlayer.getPracticeChips() + " practice chips!");
                            }
                        }
                        case "act" -> {
                            if(act()) {
                                ui.diceRollAnimation();
                                ui.displayMessage("model.Scene is wrapped");
                            }
                        }
                        case "upgrade" -> {
                            String choice = ui.promptUpgrade(manager.getAvailableUpgrades());
                            switch (choice) {
                                case "quit" -> endGame();
                                case "help" -> ui.helpMessage();
                                case "back" -> ui.displayMessage("\nNo problem!");
                                case "stats" -> ui.displayStats(currentPlayer);
                                case "view" -> manager.displayBoard();
                                default -> {
                                    String currency = ui.promptUpgradePayment();
                                    CastingOffice office = (CastingOffice) currentPlayer.getLocation();
                                    for(Upgrade upgrade : office.getUpgrades()) {
                                        if(upgrade.getRank() == Integer.parseInt(choice)) {
                                            if(upgrade.getCurrency().equals(currency)){
                                                if((currency.equals("dollars") && upgrade.getPrice() > currentPlayer.getDollars()) ||
                                                (currency.equals("credits") && upgrade.getPrice() > currentPlayer.getCredits())){
                                                    ui.displayMessage("\nYou don't have enough " + currency + "!");
                                                } else {
                                                    upgrade(upgrade, currency);
                                                    ui.displayMessage("\nYou have upgraded to rank " + upgrade.getRank());
                                                    ui.displayMessage(upgrade.getPrice() + " " + currency + " have been deducted from your account");
                                                    ui.displayMessage("You now have " + currentPlayer.getDollars() + " dollars \nand " + currentPlayer.getCredits() + " credits");
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        case "end turn" -> {
                            endTurn();
                            turnActive = false;
                        }
                        case "help" -> ui.helpMessage();
                        case "quit" -> endGame();
                        case "stats" -> ui.displayStats(currentPlayer);
                        case "view" -> manager.displayBoard();
                        default -> ui.displayMessage("Invalid action");
                    }
                }
            }
            ui.displayMessage("The final scene has wrapped and the day is over!");
            manager.decrementDay();
        }
        // end day will check no. of days and trigger end game if necessary
    }

    public Map<String, String> getAvailableRoles(Player player) {
        Map<String, String> availableRoles = new HashMap<>();
        if(!player.hasRole()) {
            availableRoles = manager.getAvailableRoles();
            if(availableRoles.size() == 0) {
                availableRoles.put("0", "Unfortunately, all available roles have been taken");
            }
        }
        return availableRoles;
    }

    // move: do movement for active player
    public void move(String location) {
        manager.move(location);
    }

    // upgrade: do upgrade for active player
    public void upgrade(Upgrade upgrade, String currency) {
        manager.upgrade(upgrade, currency);
    }

    // takeRole: active player takes role
    public void takeRole(String role) {
        manager.takeRole(role);
    }

    // rehearse: active player rehearses
    public void rehearse() {
        manager.rehearse();
    }

    // act: active player acts
    public boolean act() {
        ui.diceRollAnimation();
        return manager.act();
    }

    // endTurn: ends current turn
    public void endTurn() {
        manager.endTurn();
    }

    // endDay: ends current day
    public boolean endDay() {
        return manager.endDay();
    }

    // endGame: calculates score and allows premature end of game
    public void endGame() {
        // end game logic
        LinkedList<String> winners = manager.scoreGame();
        if(winners.size()==1){
            ui.displayMessage(winners.get(0)+" wins!");
        } else {
            for(int i = 0; i < winners.size()-1; i++) {
                ui.displayPrompt(winners.get(i)+", ");
            }
            ui.displayMessage("and "+winners.get(winners.size()-1)+" win!");
        }
        setGameActive(false);
        System.exit(0); // could be a better way to do this
    }

    // rename player loop -- duplicate names okay
    public void renamePlayers(){
        for(Player player : manager.getPlayers()){
            String name = ui.getPlayerName(player.getName());
            manager.renamePlayer(player, name);
        }
    }
}
