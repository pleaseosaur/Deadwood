/*
 * Author: Peter Hafner and Andrew Cox
 * Date: 16 May 2023
 * Purpose: Main
 */

// imports


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Map;
import java.util.HashMap;

public class Game {
    private GameManager manager;
    private JFrame frame;
    private JPanel panel, buttonPanel, statsPanel, btn_move, btn_role, btn_rehearse, btn_act, btn_upgrade, btn_end;
    private JLayeredPane layeredPane;
    private JLabel playerName, playerRank, playerDollars, playerCredits, playerChips;
    private Map<Take, JLabel> takeLabels = new HashMap<>();


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Game().GUI());
    }


    private void GUI() {
        // initialize frame, pane, and panels
        startDay();

        // these should only be used once at the start of the game
        selectPlayers(); // select number of players and start game
        renamePlayers(); // option to rename players

        currentPlayerInfo(); // display current player stats

        showCards(); // display cards on board
        showTakes(); // display shot counters on board
        showTokens(); // display player tokens on board
    }


    private void startDay() {
        frame = new JFrame("Deadwood"); // Create and set up the window.
        panel = new JPanel(); // Create a panel to hold all other components
        panel.setLayout(new BorderLayout()); // Use BorderLayout for panel

        // initialize the buttons
        setupButtons();

        // initialize the stats panel
        setupStats();

        // Setup right panel to contain player stats and buttons
        setupRightPanel();

        // Setup the board
        setupBoard();

        frame.add(panel); // Add the panel to the frame
        frame.setSize(1500, 1000); // Set the size of the frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit program when the frame is closed
        frame.setVisible(true); // Make the frame visible
    }


    private void setupButtons() {
        buttonPanel = new JPanel(); // Create a separate panel for the buttons
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        Dimension buttonSize = new Dimension(100, 50); // Set the size of the buttons

        JPanel topButtons = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Create panels for button order
        JPanel middleButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel bottomButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));


        // create buttons and add to respective panels
        this.btn_move  = createButton("Move", buttonSize, 100, 0, moveListener());
        this.btn_role  = createButton("Take Role", buttonSize, 100, 0, takeRoleListener());
        topButtons.add(btn_move);
        topButtons.add(btn_role);

        this.btn_rehearse = createButton("Rehearse", buttonSize, 10, 10, rehearseListener());
        this.btn_act = createButton("Act", buttonSize, 10, 10, actListener());
        middleButtons.add(btn_rehearse);
        middleButtons.add(btn_act);

        this.btn_upgrade = createButton("Upgrade", buttonSize, 0, 200, upgradeListener());
        this.btn_end = createButton("End Turn", buttonSize, 0, 200, endTurnListener());
        bottomButtons.add(btn_upgrade);
        bottomButtons.add(btn_end);

        buttonPanel.add(topButtons); // Add the button panels to the main panel
        buttonPanel.add(middleButtons);
        buttonPanel.add(bottomButtons);
    }


    private void setupStats() {
        statsPanel = new JPanel(); // Create a panel for player stats
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS)); // Use BoxLayout for statsPanel
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add a border to the panel

        playerName = createLabel("", 35, 15); // Create a label to display the player's name
        playerRank = createLabel("", 15, 10); // Create a label to display the player's rank
        playerDollars = createLabel("", 15, 10); // Create a label to display the player's dollars
        playerCredits = createLabel("", 15, 10); // Create a label to display the player's credits
        playerChips = createLabel("", 15, 10); // Create a label to display the player's chips

        statsPanel.add(playerName); // Add the stats label to the stats panel
        statsPanel.add(playerRank);
        statsPanel.add(playerDollars);
        statsPanel.add(playerCredits);
        statsPanel.add(playerChips);
    }


    private ActionListener moveListener() {
        return e -> {
            JPopupMenu locationMenu = new JPopupMenu(); // Create a popup menu for the locations
            for(String location : manager.getAvailableLocations()) {
                JMenuItem locationItem = new JMenuItem(location); // Create a menu item for the location
                locationItem.addActionListener(a -> {
                    manager.move(location); // Move player to the selected location
                    showTokens();
                    currentPlayerInfo();
                });
                locationMenu.add(locationItem); // Add location to the location menu
            }
            locationMenu.show(btn_move, btn_move.getWidth()/2, btn_move.getHeight()/2); // Show the location menu
        };
    }

    private ActionListener takeRoleListener() {
        return e -> {
            JPopupMenu roleMenu = new JPopupMenu(); // create popup menu
            for(Map.Entry<String, String> role : manager.getAvailableRoles().entrySet()) { // add each role to the menu
                JMenuItem roleItem = new JMenuItem(role.getKey() + role.getValue());
                roleItem.addActionListener(a -> { // add action listener to each role
                    manager.takeRole(role.getKey()); // take the selected role
                    showTokens(); // update player tokens
                    currentPlayerInfo(); // update player stats
                });
                roleMenu.add(roleItem); // add the menu item to the menu
            }
            roleMenu.show(btn_role, btn_role.getWidth()/2, btn_role.getHeight()/2); // show the menu
        };
    }

    private ActionListener rehearseListener() {
        return e -> { // TODO - rehearse logic
            manager.rehearse(); // rehearse
            currentPlayerInfo(); // update player stats
        };
    }

    private ActionListener actListener() {
        return e -> {
            boolean actSuccess = manager.act(); // act and get result

            // Get the player's location
            int[] playerPosition = manager.getCurrentPlayer().getPosition(); // get player position

            // Create a JLabel for the message
            JLabel messageLabel = new JLabel(actSuccess ? "ACT SUCCESS!" : "ACT FAILED!"); // message to display
            messageLabel.setFont(new Font("Serif", Font.BOLD, 30)); // set font and size
            messageLabel.setForeground(actSuccess ? Color.GREEN : Color.RED); // set color
            messageLabel.setBounds(playerPosition[0], playerPosition[1], 200, 50); // message position = player position

            // Add the label to your layeredPane or main panel
            layeredPane.add(messageLabel, JLayeredPane.POPUP_LAYER); // add message to layered pane

            // Create a Timer
            int delay = 100; // delay
            int totalDuration = 2000; // total duration

            ActionListener taskPerformer = new ActionListener() { // fade out the message

                int trigger = 0; // trigger counter
                public void actionPerformed(ActionEvent evt) { // action to perform
                    float opacity = 1.0f - ((float) trigger * delay / totalDuration); // calculate opacity
                    // Set the opacity
                    messageLabel.setForeground(new Color(
                            messageLabel.getForeground().getRed() / 255f,
                            messageLabel.getForeground().getGreen() / 255f,
                            messageLabel.getForeground().getBlue() / 255f,
                            opacity));
                    trigger++; // increment trigger counter
                    if (trigger * delay >= totalDuration) { // stop the timer when total delay is reached
                        ((Timer)evt.getSource()).stop();
                        layeredPane.remove(messageLabel); // remove the message
                        layeredPane.repaint(); // repaint the layered pane
                    }
                }
            };

            // Start the timer
            new Timer(delay, taskPerformer).start(); // start the timer

            if(actSuccess){ // if the act was successful
                clearTakes(); // clear the shot counters
                showTakes(); // update the shot counters
            }
            currentPlayerInfo(); // update player stats
            showCards(); // update player cards
        };
    }

    private ActionListener upgradeListener() {
        return e -> {
            // Get available upgrades and player's current money
            var availableUpgrades = manager.getAvailableUpgrades();
            int playerDollars = manager.getCurrentPlayer().getDollars();
            int playerCredits = manager.getCurrentPlayer().getCredits();

            // Create the dialog and set a grid layout
            JDialog dialog = new JDialog();
            dialog.setLayout(new GridLayout(0, 1));

            // Create a button group for the rank radio buttons
            ButtonGroup rankGroup = new ButtonGroup();

            // Create a map to hold the button groups for the currency radio buttons
            Map<Integer, ButtonGroup> currencyGroups = new HashMap<>();

            // Iterate through each available upgrade
            availableUpgrades.forEach((rank, currency) -> {
                JRadioButton rankButton = new JRadioButton("Rank " + rank);
                rankButton.setActionCommand(String.valueOf(rank));
                rankGroup.add(rankButton);
                dialog.add(rankButton);

                ButtonGroup currencyGroup = new ButtonGroup();
                currencyGroups.put(rank, currencyGroup);

                for (String option : currency) {
                    String[] parts = option.split(" ");
                    int price = Integer.parseInt(parts[0]);

                    // Only enable the radio button if the player can afford the upgrade
                    boolean canAfford = parts[1].equals("dollars") ? playerDollars >= price : playerCredits >= price;

                    JRadioButton currencyButton = new JRadioButton(parts[0] + " " + parts[1]);
                    currencyButton.setEnabled(canAfford);
                    currencyButton.setActionCommand(parts[1]);
                    currencyGroup.add(currencyButton);
                    dialog.add(currencyButton);
                }

                // Only enable the rank radio button if the player can afford any of the upgrade options
                boolean canAffordAny = currency.stream().anyMatch(option -> {
                    String[] parts = option.split(" ");
                    int price = Integer.parseInt(parts[0]);
                    return parts[1].equals("dollars") ? playerDollars >= price : playerCredits >= price;
                });
                rankButton.setEnabled(canAffordAny);
            });

            // Create a button to confirm the upgrade
            JButton confirmButton = new JButton("Confirm Upgrade");
            confirmButton.addActionListener(a -> {
                String rankStr = rankGroup.getSelection().getActionCommand();
                String currency = currencyGroups.get(Integer.parseInt(rankStr)).getSelection().getActionCommand();

                // Get the selected upgrade
                Upgrade selectedUpgrade = null;
                for (Upgrade upgrade : ((CastingOffice) manager.getCurrentPlayer().getLocation()).getUpgrades()) {
                    if (upgrade.getRank() == Integer.parseInt(rankStr) && upgrade.getCurrency().equals(currency)) {
                        selectedUpgrade = upgrade;
                        break;
                    }
                }

                // Perform the upgrade
                if (selectedUpgrade != null) {
                    manager.upgrade(selectedUpgrade, currency);
                    dialog.dispose(); // Close the dialog
                    currentPlayerInfo(); // Update player stats
                    showTokens();
                }
            });
            dialog.add(confirmButton);

            // Create a button to cancel the upgrade
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(a -> dialog.dispose());
            dialog.add(cancelButton);

            // Set the dialog properties
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.pack();
            dialog.setVisible(true);
        };
    }



    private ActionListener endTurnListener() {
        return e -> {
            manager.endTurn();
            currentPlayerInfo();
            showTokens();
        };
    }


    private void setupRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(statsPanel, BorderLayout.NORTH);
        rightPanel.add(buttonPanel, BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.EAST); // Add the right panel to the panel
    }


    private void setupBoard() {
        layeredPane = new JLayeredPane(); // Create the JLayeredPane to hold the board, cards, tokens, etc.

        ImageIcon board = getImage("/resources/images/board.jpg"); // Create the board image
        layeredPane.setPreferredSize(new Dimension(board.getIconWidth(), board.getIconHeight())); // Set the size of the game board

        JLabel boardLabel = new JLabel(board); // Add the board image to a label
        boardLabel.setBounds(0, 0, board.getIconWidth(), board.getIconHeight()); // Set the size of the board label

        layeredPane.add(boardLabel, Integer.valueOf(0)); // Add the board to the lowest layer

        panel.add(layeredPane, BorderLayout.CENTER); // Add the layered pane to the panel
    }


    private void showCards() {
        // Remove all components in layer 2
        Component[] components = layeredPane.getComponentsInLayer(2);
        for (Component c : components) {
            layeredPane.remove(c);
        }
        // Refresh the layeredPane after removals
        layeredPane.revalidate();
        layeredPane.repaint();

        manager.getCards().forEach((card, area) -> { // Iterate through the cards and their areas
            String path = card.getImg(); // Get the path to the card image
            int x = area.get(0); // Get the x coordinate of the card
            int y = area.get(1); // Get the y coordinate of the card
            int w = area.get(2); // Get the width of the card
            int h = area.get(3); // Get the height of the card
            ImageIcon cardImage = getImage(path); // Create an image icon from the path
            JLabel cardLabel = new JLabel(cardImage); // Add the image icon to a label
            cardLabel.setBounds(x, y, w, h); // Set the size of the card label
            layeredPane.add(cardLabel, Integer.valueOf(2)); // Add the card to the second layer
        });
    }



    private void showTakes() {
        manager.getTakes().forEach((take, area) -> { // Iterate through the takes and their areas
            String path = take.getImg(); // Get the path to the take image
            int x = area.get(0); // Get the x coordinate of the take
            int y = area.get(1); // Get the y coordinate of the take
            int w = area.get(2); // Get the width of the take
            int h = area.get(3); // Get the height of the take
            ImageIcon takeImage = getImage(path); // Create an image icon from the path
            JLabel takeLabel = new JLabel(takeImage); // Add the image icon to a label
            takeLabel.setBounds(x, y, w, h); // Set the size of the take label
            layeredPane.add(takeLabel, Integer.valueOf(1)); // Add the take to the second layer

            takeLabels.put(take, takeLabel); // Stores take label into Map
        });
    }

    private void clearTakes() {
        for (JLabel takeLabel : takeLabels.values()) { // Iterate through the take labels
            layeredPane.remove(takeLabel); // Remove the take label from the layered pane
        }
        takeLabels.clear(); // Clear the take labels map
        layeredPane.repaint(); // Repaint the layered pane
    }


    private void showTokens() {
        // Remove all components in layer 3
        Component[] components = layeredPane.getComponentsInLayer(3);
        for (Component c : components) {
            layeredPane.remove(c);
        }
        // Refresh the layeredPane after removals
        layeredPane.revalidate();
        layeredPane.repaint();

        manager.getTokens().forEach((path, position) -> { // Iterate through the tokens and their positions
            int x = position[0]; // Get the x coordinate of the token
            int y = position[1]; // Get the y coordinate of the token
            ImageIcon token = getImage(path); // Create an image icon from the path
            JLabel tokenLabel = new JLabel(token); // Add the image icon to a label
            tokenLabel.setBounds(x, y, token.getIconWidth(), token.getIconHeight()); // Set the size of the token label
            layeredPane.add(tokenLabel, Integer.valueOf(3)); // Add the token to the third layer
        });
    }



    private void currentPlayerInfo() {

        Player currentPlayer = manager.getCurrentPlayer();
        String color = currentPlayer.getColor();

        switch (color) { // Set the color of the player name based on their color
            case "b" -> playerName.setForeground(Color.BLUE);
            case "c" -> playerName.setForeground(Color.CYAN);
            case "g" -> playerName.setForeground(Color.GREEN);
            case "o" -> playerName.setForeground(Color.ORANGE);
            case "p" -> playerName.setForeground(Color.PINK);
            case "r" -> playerName.setForeground(Color.RED);
            case "v" -> playerName.setForeground(Color.MAGENTA);
            case "w" -> playerName.setForeground(Color.WHITE);
            case "y" -> playerName.setForeground(Color.YELLOW);
        }

        playerName.setText(currentPlayer.getName()); // Set the player name
        playerRank.setText("Rank: " + currentPlayer.getRank()); // Set player rank
        playerDollars.setText("Dollars: " + currentPlayer.getDollars()); // Set player dollars
        playerCredits.setText("Credits: " + currentPlayer.getCredits()); // Set player credits
        playerChips.setText("Practice Chips: " + currentPlayer.getPracticeChips()); // Set player practice chips

        showActiveButtons(); // Show the buttons that the player can use
    }


    private JLabel createLabel(String text, int fontSize, int spacing) { // Create a label with the given text, font size, and spacing
        JLabel label = new JLabel(text); // Create a label with the given text
        label.setFont(new Font("Serif", Font.BOLD, fontSize)); // Set the font of the label
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, spacing, 0)); // Set the border of the label
        return label;
    }


    private JPanel createButton(String buttonName, Dimension buttonSize, int top, int bottom, ActionListener action) {
        JButton button = new JButton(buttonName); // Create a button with the given name
        button.setPreferredSize(buttonSize); // Set the size of the button
        button.addActionListener(action); // Add the action listener to the button

        JPanel buttonPanel = new JPanel(new FlowLayout()); // Create a panel for the button
        buttonPanel.add(button); // Add the button to the panel
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(top, 0, bottom, 0)); // Set the border of the panel
        return buttonPanel;
    }

    private void showActiveButtons() { // Show the buttons that the player can use
        var availableActions = manager.getAvailableActions(); // Get the available actions for the player

        // Set the style of the buttons based on the available actions
        setButtonStyle(btn_move, availableActions.contains("Move"));
        setButtonStyle(btn_role, availableActions.contains("Take Role") && !manager.getAvailableRoles().isEmpty());
        setButtonStyle(btn_rehearse, availableActions.contains("Rehearse"));
        setButtonStyle(btn_act, availableActions.contains("Act"));
        setButtonStyle(btn_upgrade, availableActions.contains("Upgrade"));
        setButtonStyle(btn_end, availableActions.contains("End Turn"));
    }

    private void setButtonStyle(JPanel button, boolean enabled) { // Set the style of the button based on the enabled status
        button.setEnabled(enabled); // Set the enabled status of the button
        for(Component c : button.getComponents()) { // Iterate through the components of the button
            c.setEnabled(enabled); // Set the enabled status of the component
            if(c instanceof JLabel label) { // If the component is a label
                label.setForeground(enabled ? Color.BLACK : Color.GRAY); // Set the color of the label
            } else if(c instanceof JButton btn) { // If the component is a button
                btn.setForeground(enabled ? Color.BLACK : Color.GRAY); // Set the color of the button
            }
        }
    }


    private void selectPlayers() {
        // Player selection dialog
        Integer[] choices = { 2, 3, 4, 5, 6, 7, 8 }; // Number of players choices
        JComboBox<Integer> numPlayers = new JComboBox<>(choices); // Create a combo box for the number of players

        Object[] message = {
                "Please select the number of players:", numPlayers
        };

        Object[] options = { "Start Game", "Cancel" }; // Buttons for the dialog

        int option = JOptionPane.showOptionDialog(null, message, "Game Setup", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (option == 0) { // 'Start Game' selected
            Integer input = (Integer) numPlayers.getSelectedItem(); // Get the number of players from the combo box
            System.out.println("Starting game with " + input + " players!");
            this.manager = new GameManager(input); // Create a new game manager with the selected number of players
        } else { // 'Cancel' selected or dialog closed
            System.out.println("Game Setup Cancelled!");
            // exit
        }
    }


    private void renamePlayers() {
        int renameOption = JOptionPane.showConfirmDialog(null, "Would you like to enter custom player names?", "Rename Players", JOptionPane.YES_NO_OPTION);
        if(renameOption == JOptionPane.YES_OPTION) {
            for(Player player : manager.getPlayers()) {
                String name = renamePrompt(player.getName());
                manager.renamePlayer(player, name);
            }
        }
    }


    private String renamePrompt(String name) {
        JTextField input = new JTextField();
        final JComponent[] inputs = new JComponent[] {
                new JLabel("Please enter a name for " + name + ": "),
                input
        };
        JOptionPane.showMessageDialog(null, inputs, "Rename Player", JOptionPane.PLAIN_MESSAGE);
        return input.getText();
    }


    private ImageIcon getImage(String path) {
        URL url = getClass().getResource(path);
        ImageIcon image = new ImageIcon(url);
        image.setImage(image.getImage().getScaledInstance(image.getIconWidth(), image.getIconHeight(), Image.SCALE_DEFAULT));
        return image;
    }
}