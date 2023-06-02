/*
 * Author: Peter Hafner and Andrew Cox
 * Date: 1 June 2023
 * Purpose: Main game class
 */

// imports
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Game {
    // Fields
    private GameManager manager;
    private JFrame frame;
    private JPanel panel, buttonPanel, statsPanel,messagePanel, btn_move, btn_role, btn_rehearse, btn_act, btn_upgrade, btn_end;
    private JLayeredPane layeredPane;
    private JLabel playerName, playerRank, playerDollars, playerCredits, playerChips, daysRemain;
    private Map<Take, JLabel> takeLabels = new HashMap<>();

    // Main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Game().GUI());
    }

    // Initialize GUI and start game
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

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e){
                endGame();
            }
        });
    }

    private void endGame() {
        manager.scoreGame();
        JFrame scoreFrame = new JFrame("Game Over");
        scoreFrame.setSize(300, 300);
        scoreFrame.setVisible(true);

        // Close original frame
        scoreFrame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                frame.dispose();
            }
        });
    }




    //********************************************************************************
    //                              Game Setup Methods
    //********************************************************************************
    private void startDay() {
        frame = new JFrame("Deadwood"); // Create and set up the window.
        frame.setIconImage(getImage("/resources/images/dw_icon.jpg").getImage());
        panel = new JPanel(); // Create a panel to hold all other components
        panel.setLayout(new BorderLayout()); // Use BorderLayout for panel

        // initialize the buttons
        setupButtons();

        // initialize the stats panel
        setupStats();

        // initialize the message panel
        setupMessagePanel();

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

        this.btn_rehearse = createButton("Rehearse", buttonSize, 50, 50, rehearseListener());
        this.btn_act = createButton("Act", buttonSize, 50, 50, actListener());
        middleButtons.add(btn_rehearse);
        middleButtons.add(btn_act);

        this.btn_upgrade = createButton("Upgrade", buttonSize, 0, 100, upgradeListener());
        this.btn_end = createButton("End Turn", buttonSize, 0, 100, endTurnListener());
        bottomButtons.add(btn_upgrade);
        bottomButtons.add(btn_end);

        buttonPanel.add(topButtons); // Add the button panels to the main panel
        buttonPanel.add(middleButtons);
        buttonPanel.add(bottomButtons);
    }


    private void setupStats() {
        statsPanel = new JPanel(new GridBagLayout()); // Use GridBagLayout for statsPanel
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add a border to the panel

        GridBagConstraints c = new GridBagConstraints(); // constraints object

        c.gridx = 0; // Column 0
        c.fill = GridBagConstraints.HORIZONTAL; // fill horizontal space
        c.weightx = 1; // take up all available horizontal space

        playerName = createLabel("", 35, 15); // Create a label to display the player's name
        playerRank = createLabel("", 15, 10); // Create a label to display the player's rank
        playerDollars = createLabel("", 15, 10); // Create a label to display the player's dollars
        playerCredits = createLabel("", 15, 10); // Create a label to display the player's credits
        playerChips = createLabel("", 15, 10); // Create a label to display the player's chips
        daysRemain = createLabel("", 12, 10); // Create a label to display the day

        c.gridy = 0; // Row 0
        statsPanel.add(playerName, c); // Add player name to the stats panel

        c.gridy = 1; // Row 1
        statsPanel.add(playerRank, c); // Add player rank to the stats panel

        c.gridy = 2; // Row 2
        statsPanel.add(playerDollars, c); // Add player dollars to the stats panel

        c.gridy = 3; // Row 3
        statsPanel.add(playerCredits, c); // Add player credits to the stats panel

        c.gridy = 4; // Row 4
        statsPanel.add(playerChips, c); // Add player practice chips to the stats panel

        c.gridy = 5; // Row 5
        statsPanel.add(daysRemain, c); // Add days remaining to the stats panel
    }

    private void setupMessagePanel() {
        messagePanel = new JPanel(new GridBagLayout()); // Use GridBagLayout for messagePanel
        messagePanel.setPreferredSize(new Dimension(300, 500)); // Set the size of the message panel
        messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add a border to the panel
        messagePanel.setOpaque(false); // Make the panel transparent
    }


    private void setupRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(statsPanel, BorderLayout.NORTH);
        rightPanel.add(messagePanel, BorderLayout.CENTER); // Add the message panel to the right panel (top
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(rightPanel, BorderLayout.EAST); // Add the right panel to the panel
    }


    private void setupBoard() {
        layeredPane = new JLayeredPane(); // Create the JLayeredPane to hold the board, cards, tokens, etc.

        ImageIcon board = getImage("/resources/images/board.jpg"); // Create the board image
        layeredPane.setPreferredSize(new Dimension(board.getIconWidth(), board.getIconHeight())); // Set the size of the game board

        JLabel boardLabel = new JLabel(board); // Add the board image to a label
        boardLabel.setBounds(0, 0, board.getIconWidth(), board.getIconHeight()); // Set the size of the board label

        layeredPane.add(boardLabel, Integer.valueOf(0)); // Add the board to the lowest layer

        JScrollPane scrollPane = new JScrollPane(layeredPane); // Create a scroll pane to hold the layered pane

        panel.add(scrollPane, BorderLayout.CENTER); // Add the layered pane to the panel
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
            int cancel = JOptionPane.showConfirmDialog(null, "Are you sure you want to cancel?", "Cancel Game Setup", JOptionPane.YES_NO_OPTION);
            if(cancel == JOptionPane.YES_OPTION) {
                System.exit(0); // Exit the program
            } else {
                selectPlayers(); // Show the player selection dialog again
            }
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




    //********************************************************************************
    //                           Update & Display Methods
    //********************************************************************************
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
            case "y" -> playerName.setForeground(Color.YELLOW);
        }

        playerName.setText(currentPlayer.getName()); // Set the player name
        playerRank.setText("Rank: " + currentPlayer.getRank()); // Set player rank
        playerDollars.setText("Dollars: " + currentPlayer.getDollars()); // Set player dollars
        playerCredits.setText("Credits: " + currentPlayer.getCredits()); // Set player credits
        playerChips.setText("Practice Chips: " + currentPlayer.getPracticeChips()); // Set player practice chips
        daysRemain.setText("Days Remaining: " + manager.getDays()); // Set days remaining
        daysRemain.setForeground(Color.GRAY);

        showActiveButtons(); // Show the buttons that the player can use
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




    //********************************************************************************
    //                               Action Listeners
    //********************************************************************************
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
        return e -> {
          manager.rehearse(); // rehearse
          displayMessage("+1 Practice Chips", Color.GREEN, 150); // display message
          currentPlayerInfo(); // update player stats
        };
    }


    private ActionListener actListener() {
        return e -> {
            int[] actResult = manager.act(); // act and get result

            // display rolled number
            int diceResult = actResult[1];

            displayMessage("You rolled a: " + diceResult, Color.BLACK, 0);

            // display success or failure message
            String message = actResult[0] == 1 ? "ACT SUCCESS!" : "ACT FAILED!";
            Color color = actResult[0] == 1 ? Color.GREEN : Color.RED;
            displayMessage(message, color, 35); // display message

            // display bonus message
            if(actResult[2] == 1){
                displayMessage("You got a bonus!", Color.BLUE, 70);
            }

            if(actResult[0] == 1){ // if the act was successful
                clearTakes(); // clear the shot counters
                showTakes(); // update the shot counters
            }

            currentPlayerInfo(); // update player stats
            showCards(); // update player cards
            showTokens(); // update player tokens
        };
    }


    private ActionListener upgradeListener() {
        return e -> {
            // Get available upgrades and player's current money
            var availableUpgrades = manager.getAvailableUpgrades();
            int playerDollars = manager.getCurrentPlayer().getDollars();
            int playerCredits = manager.getCurrentPlayer().getCredits();

            // Create the dialog and set a grid bag layout
            JDialog dialog = new JDialog();
            GridBagLayout layout = new GridBagLayout();
            dialog.setLayout(layout);

            GridBagConstraints constraints = new GridBagConstraints(); // Create constraints for the layout

            // Create a button group for all buttons
            ButtonGroup group = new ButtonGroup();

            // Create an AtomicInteger to be used as an index in the forEach loop
            AtomicInteger i = new AtomicInteger();

            // Iterate through each available upgrade
            availableUpgrades.forEach((rank, options) -> { // Iterate through each rank and its options
                constraints.gridy = i.getAndIncrement(); // Set the y position to the current index
                ImageIcon icon = getImage("/resources/images/tokens/w" + rank + ".png"); // Get the rank icon
                JLabel rankLabel = new JLabel(icon); // Create a label for the rank icon
                constraints.gridx = 0; // Set the x position to 0
                dialog.add(rankLabel, constraints); // Add the rank icon to the dialog

                boolean canUpgrade = rank > manager.getCurrentPlayer().getRank();
                for (String option : options) { // Iterate through each option
                    String[] parts = option.split(" "); // Split the option into its price and currency
                    int price = Integer.parseInt(parts[0]); // Get the price

                    // Only enable the radio button if rank > player rank and the player can afford the upgrade
                    boolean canAfford = parts[1].equals("dollars") ? playerDollars >= price : playerCredits >= price;


                    JCheckBox currencyButton = new JCheckBox(parts[0] + " " + parts[1]); // Create a radio button for the currency
                    currencyButton.setEnabled(canAfford && canUpgrade); // Enable the radio button if the player can afford the upgrade
                    currencyButton.setActionCommand(rank + " " + parts[1]); // Set the action command to the rank and currency
                    group.add(currencyButton); // Add the radio button to the group
                    if (parts[1].equals("dollars")) { // If the currency is dollars
                        constraints.gridx = 1; // Set the x position to 1
                    } else { // If the currency is credits
                        constraints.gridx = 2; // Set the x position to 2
                    }
                    dialog.add(currencyButton, constraints); // Add the radio button to the dialog
                }

                // Only enable the rank label if the player can afford any of the upgrade options
                boolean canAffordAny = options.stream().anyMatch(option -> {
                    String[] parts = option.split(" "); // Split the option into its price and currency
                    int price = Integer.parseInt(parts[0]); // Get the price
                    return parts[1].equals("dollars") ? playerDollars >= price : playerCredits >= price; // Check if the player can afford the option
                });
                rankLabel.setEnabled(canAffordAny && canUpgrade); // Enable the rank label if the player can afford any of the options
            });

            constraints.gridy = availableUpgrades.size(); // Set the y position to the number of available upgrades
            constraints.gridx = 0; // Set the x position to 0
            constraints.gridwidth = 1; // Set the grid width to 1
            constraints.insets = new Insets(20, 0, 0, 0); // Set the insets to 10, 0, 0, 0

            // Create a button to confirm the upgrade
            JButton confirmButton = new JButton("Confirm Upgrade");
            // Create a button to cancel the upgrade
            JButton cancelButton = new JButton("Cancel");

            dialog.add(confirmButton, constraints); // Add the confirm button
            constraints.gridx = 2; // Set the x position to 2
            dialog.add(cancelButton, constraints); // Add the cancel button

            confirmButton.addActionListener(a -> { // Add an action listener to the confirm button
                String actionCommand = group.getSelection().getActionCommand(); // Get the selected upgrade
                String[] parts = actionCommand.split(" "); // Split the upgrade into its rank and currency
                String rankStr = parts[0]; // Get the selected rank
                String currency = parts[1]; // Get the selected currency

                // Get the selected upgrade
                Upgrade selectedUpgrade = null;
                for (Upgrade upgrade : ((CastingOffice) manager.getCurrentPlayer().getLocation()).getUpgrades()) { // Iterate through each upgrade
                    if (upgrade.getRank() == Integer.parseInt(rankStr) && upgrade.getCurrency().equals(currency)) { // If the upgrade matches the selected rank and currency
                        selectedUpgrade = upgrade; // Set the selected upgrade
                        break;
                    }
                }

                // Perform the upgrade
                if (selectedUpgrade != null) { // If the upgrade is valid
                    manager.upgrade(selectedUpgrade, currency); // Perform the upgrade
                    dialog.dispose(); // Close the dialog
                    currentPlayerInfo(); // Update player stats
                    showTokens(); // Update player tokens
                }
            });

            cancelButton.addActionListener(a -> dialog.dispose());

            // Set the dialog properties
            dialog.setIconImage(getImage("/resources/images/tokens/w6.png").getImage()); // Set the icon of the dialog
            dialog.setTitle("   Casting Office: Pay dollars OR credits to upgrade."); // Set the title of the dialog
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // Close the dialog when the user clicks the X
            dialog.setSize(350, 350); // Set the size of the dialog
            dialog.setLocationRelativeTo(layeredPane); // Center the dialog
            dialog.setVisible(true); // Show the dialog
        };
    }


    private ActionListener endTurnListener() {
        return e -> {
            manager.endTurn();
            currentPlayerInfo();
            showTokens();
        };
    }




    //********************************************************************************
    //                               Helper Methods
    //********************************************************************************
    private ImageIcon getImage(String path) {
        URL url = getClass().getResource(path);
        ImageIcon image = new ImageIcon(url);
        image.setImage(image.getImage().getScaledInstance(image.getIconWidth(), image.getIconHeight(), Image.SCALE_DEFAULT));
        return image;
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


    private JLabel createLabel(String text, int fontSize, int spacing) { // Create a label with the given text, font size, and spacing
        JLabel label = new JLabel(text); // Create a label with the given text
        label.setFont(new Font("Serif", Font.BOLD, fontSize)); // Set the font of the label
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, spacing, 0)); // Set the border of the label
        return label;
    }


    private String renamePrompt(String name) {
        JTextField input = new JTextField(); // Create a text field for the user to enter a name
        final JComponent[] inputs = new JComponent[] {
                new JLabel("Please enter a name for " + name + ": "), // Create a label to prompt the user to enter a name
                input // Add the text field to the dialog
        };

        String newName = ""; // The new name to be returned
        do {
            JOptionPane optionPane = new JOptionPane(inputs, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION); // Create a new option pane with the inputs
            JDialog prompt = optionPane.createDialog("Rename Player"); // Create a new dialog with the option pane

            // Use a timer to request focus after the dialog is visible
            new Timer(100, e -> input.requestFocusInWindow()).start(); // Request focus on the text field

            prompt.setVisible(true); // Show the dialog
            newName = input.getText().trim(); // trim() is used to remove leading and trailing spaces
        } while(newName.isEmpty()); // Loop until the user enters a name

        return newName;
    }


    private void displayMessage(String message, Color color, int y) {

        JLabel messageLabel = new JLabel(message); // Create a label with the given message
        messageLabel.setFont(new Font("Serif", Font.BOLD, 25)); // Set the font of the label
        messageLabel.setForeground(color); // Set the color of the label

        GridBagConstraints c = new GridBagConstraints(); // Create a new constraints object
        c.gridx = 0; // Column 0
        c.gridy = y; // Row 0

        messagePanel.add(messageLabel, c); // Add the label to statsPanel
        messagePanel.revalidate(); // Revalidate the panel to update the layout

        int delay = 100; // Delay between each iteration
        int totalDuration = 2000; // Total duration of the animation

        ActionListener taskPerformer = new ActionListener() { // Create a new action listener
            int trigger = 0; // The number of times the timer has triggered
            public void actionPerformed(ActionEvent evt) { // Called each time the timer triggers
                float opacity = 1.0f - ((float) trigger * delay / totalDuration); // Calculate the opacity of the label
                messageLabel.setForeground(new Color(
                        messageLabel.getForeground().getRed() / 255f,
                        messageLabel.getForeground().getGreen() / 255f,
                        messageLabel.getForeground().getBlue() / 255f,
                        opacity)); // Set the opacity of the label
                trigger++; // Increment the trigger
                if (trigger * delay >= totalDuration) { // If the animation is complete
                    ((Timer)evt.getSource()).stop(); // Stop the timer
                    messagePanel.remove(messageLabel); // Remove the label from statsPanel
                    messagePanel.revalidate(); // Revalidate the panel to update the layout
                    messagePanel.repaint();
                }
            }
        };

        new Timer(delay, taskPerformer).start(); // Start the timer
    }
}