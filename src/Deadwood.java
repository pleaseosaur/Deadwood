/*
 * Author: Peter Hafner and Andrew Cox
 * Date: 1 June 2023
 * Purpose: Main game class
 */

// imports
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Deadwood {
    // Fields
    private GameManager manager;
    private JFrame frame;
    private JPanel panel, buttonPanel, statsPanel,messagePanel, btn_move, btn_role, btn_rehearse, btn_act, btn_upgrade, btn_end, standingsPanel;
    private JLayeredPane layeredPane;
    private JLabel playerName, playerRank, playerDollars, playerCredits, playerChips, daysRemain;
    private final Map<Take, JLabel> takeLabels = new HashMap<>();


    // Main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Deadwood().GUI());
    }


    // Initialize GUI and start game
    private void GUI() {
        // initialize frame, pane, and panels and display game
        startDay();

        selectPlayers();
        renamePlayers();

        currentPlayerInfo();

        showCards();
        showTakes();
        showTokens();

        // display quit confirmation dialog
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e){
                int cancel = JOptionPane.showConfirmDialog(frame, "Are you sure you want to quit? \nYour game will not be saved.", "Exit Game", JOptionPane.YES_NO_OPTION);
                if(cancel == JOptionPane.YES_OPTION) {
                    System.exit(0); // Exit the program
                } else {
                    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }
        });
    }




    //********************************************************************************
    //                              Game Setup Methods
    //********************************************************************************
    private void startDay() {
        frame = new JFrame("Deadwood"); // Create and set up the window.
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setIconImage(getImage("/resources/images/dw_icon.jpg").getImage());
        panel = new JPanel(); // Create a panel to hold all other components
        panel.setLayout(new BorderLayout());

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

        frame.add(panel);
        frame.setSize(1500, 1000);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.setVisible(true);
    }


    private void setupButtons() {
        buttonPanel = new JPanel(); // Create a separate panel for the buttons
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        Dimension buttonSize = new Dimension(100, 50);

        JPanel topButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
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
        statsPanel = new JPanel(new GridBagLayout());
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        // initialize player stat labels
        playerName = createLabel("", 35, 15);
        playerRank = createLabel("", 15, 10);
        playerDollars = createLabel("", 15, 10);
        playerCredits = createLabel("", 15, 10);
        playerChips = createLabel("", 15, 10);
        daysRemain = createLabel("", 12, 10);

        // add player stat labels to stats panel
        c.gridy = 0;
        statsPanel.add(playerName, c);

        c.gridy = 1;
        statsPanel.add(playerRank, c);

        c.gridy = 2;
        statsPanel.add(playerDollars, c);

        c.gridy = 3;
        statsPanel.add(playerCredits, c);

        c.gridy = 4;
        statsPanel.add(playerChips, c);

        c.gridy = 5;
        statsPanel.add(daysRemain, c);

        // display player standings on mouse hover
        statsPanel.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                standingsPanel = createStandingsPanel();
                showStandingsPanel(statsPanel, standingsPanel);
            }
            public void mouseExited(MouseEvent e) {
                hideStandingsPanel(standingsPanel);
            }
        });
    }


    private JPanel createStandingsPanel() {
        JPanel standingsPanel = new JPanel();
        standingsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        Map<String, Integer> playerScores = manager.scoreGame();

        // sort entries by score in descending order
        List<Map.Entry<String, Integer>> sortedScores = playerScores.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .toList();

        // create a string to display the scores
        StringBuilder html = new StringBuilder("<html><center>");
        html.append("<center><h2><u>Standings</u></h2></center>");
        html.append("<br>");
        for (Map.Entry<String, Integer> entry : sortedScores) {
            html.append(entry.getKey()).append(": ").append(entry.getValue()).append("<br><br>");
        }
        html.append("</center></html>");
        JLabel scoreLabel = new JLabel(html.toString());
        standingsPanel.add(scoreLabel);

        return standingsPanel;
    }


    private void showStandingsPanel(Component parent, JPanel standingsPanel) {
        JWindow popup = new JWindow();
        popup.getContentPane().add(standingsPanel);
        popup.pack(); // resize the window to fit the contents

        Point parentLocation = parent.getLocationOnScreen();
        popup.setLocation(parentLocation.x + 10, parentLocation.y + parent.getHeight());
        popup.setAlwaysOnTop(true);
        popup.setVisible(true);
    }


    private void hideStandingsPanel(JPanel standingsPanel) {
        Window window = SwingUtilities.getWindowAncestor(standingsPanel);
        if (window instanceof JWindow) {
            window.setVisible(false);
            window.dispose();
        }
    }


    // Blank panel to display dynamic messages
    private void setupMessagePanel() {
        messagePanel = new JPanel(new GridBagLayout());
        messagePanel.setPreferredSize(new Dimension(300, 500));
        messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        messagePanel.setOpaque(false);
    }


    // Main side panel that holds stats, messages, and buttons
    private void setupRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(statsPanel, BorderLayout.NORTH);
        rightPanel.add(messagePanel, BorderLayout.CENTER);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(rightPanel, BorderLayout.EAST);
    }


    private void setupBoard() {
        layeredPane = new JLayeredPane(); // holds the board, cards, tokens, etc.

        ImageIcon board = getImage("/resources/images/board.jpg");
        layeredPane.setPreferredSize(new Dimension(board.getIconWidth(), board.getIconHeight()));

        JLabel boardLabel = new JLabel(board);
        boardLabel.setBounds(0, 0, board.getIconWidth(), board.getIconHeight());

        layeredPane.add(boardLabel, Integer.valueOf(0));

        JScrollPane scrollPane = new JScrollPane(layeredPane);

        panel.add(scrollPane, BorderLayout.CENTER);
    }


    private void selectPlayers() throws NullPointerException {

        Integer[] choices = { 2, 3, 4, 5, 6, 7, 8 }; // restrict the number of players to 2-8
        JComboBox<Integer> numPlayers = new JComboBox<>(choices);

        Object[] message = {"Please select the number of players:", numPlayers};

        Object[] buttons = { "Start Game", "Cancel" };

        int option = JOptionPane.showOptionDialog(frame, message, "Game Setup", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[0]);

        if (option == 0) { // 'Start Game' selected
            Integer input = (Integer) numPlayers.getSelectedItem(); // get input
            this.manager = new GameManager(Objects.requireNonNull(input)); // Create a new game manager with the selected number of players
        }

        else { // 'Cancel' selected or dialog closed
            int cancel = JOptionPane.showConfirmDialog(frame, "Are you sure you want to cancel?", "Cancel Game Setup", JOptionPane.YES_NO_OPTION);
            if(cancel == JOptionPane.YES_OPTION) {
                System.exit(0);
            } else {
                selectPlayers();
            }
        }
    }


    private void renamePlayers() {
        int renameOption = JOptionPane.showConfirmDialog(frame, "Would you like to enter custom player names?", "Rename Players", JOptionPane.YES_NO_OPTION);
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

        playerName.setText(currentPlayer.getName());
        playerRank.setText("Rank: " + currentPlayer.getRank());
        playerDollars.setText("Dollars: " + currentPlayer.getDollars());
        playerCredits.setText("Credits: " + currentPlayer.getCredits());
        playerChips.setText("Practice Chips: " + currentPlayer.getPracticeChips());
        daysRemain.setText("Days Remaining: " + manager.getDays());
        daysRemain.setForeground(Color.GRAY);

        showActiveButtons();
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


    // Set the style of the button based on the enabled status
    private void setButtonStyle(JPanel button, boolean enabled) {
        button.setEnabled(enabled);
        for(Component c : button.getComponents()) {
            c.setEnabled(enabled);
            if(c instanceof JLabel label) {
                label.setForeground(enabled ? Color.BLACK : Color.GRAY);
            } else if(c instanceof JButton btn) {
                btn.setForeground(enabled ? Color.BLACK : Color.GRAY);
            }
        }
    }


    private void showCards() {
        // Remove all components in layer 2
        Component[] components = layeredPane.getComponentsInLayer(2);
        for (Component c : components) {
            layeredPane.remove(c);
        }

        layeredPane.revalidate();
        layeredPane.repaint();

        manager.getCards().forEach((card, area) -> {
            String path = card.getImg();

            int x = area.get(0);
            int y = area.get(1);
            int w = area.get(2);
            int h = area.get(3);

            ImageIcon cardImage = getImage(path);
            ImageIcon scaledImage = new ImageIcon(cardImage.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
            JLabel cardLabel = new JLabel(scaledImage);
            cardLabel.setBounds(x, y, w, h);

            if(!Objects.equals(path, "/resources/images/cards/CardBack.jpg")) { // If the card is not the card back
                cardLabel.addMouseListener(new MouseAdapter() { // Add a mouse listener to the card

                    // Enlarge the card on mouse hover
                    public void mouseEntered(MouseEvent e) {
                        cardLabel.setBounds(x, y, cardImage.getIconWidth(), cardImage.getIconHeight());
                        cardLabel.setIcon(cardImage);
                        layeredPane.setLayer(cardLabel, 7);
                    }

                    // Return the card to its original size on mouse exit
                    public void mouseExited(MouseEvent e) {
                        cardLabel.setBounds(x, y, w, h);
                        cardLabel.setIcon(scaledImage);
                        layeredPane.setLayer(cardLabel, 2);
                    }
                });
            }

            layeredPane.add(cardLabel, Integer.valueOf(2));
        });
    }


    private void showTakes() {
        manager.getTakes().forEach((take, area) -> {
            String path = take.getImg();

            int x = area.get(0);
            int y = area.get(1);
            int w = area.get(2);
            int h = area.get(3);

            ImageIcon takeImage = getImage(path);
            JLabel takeLabel = new JLabel(takeImage);
            takeLabel.setBounds(x, y, w, h);
            layeredPane.add(takeLabel, Integer.valueOf(1));

            takeLabels.put(take, takeLabel);
        });
    }


    private void clearTakes() {
        for (JLabel takeLabel : takeLabels.values()) {
            layeredPane.remove(takeLabel);
        }
        takeLabels.clear();
        layeredPane.repaint();
    }


    // Display player tokens on the board
    private void showTokens() {
        // Remove all components in layer 3
        Component[] components = layeredPane.getComponentsInLayer(3);
        for (Component c : components) {
            layeredPane.remove(c);
        }

        layeredPane.revalidate();
        layeredPane.repaint();

        manager.getTokens().forEach((path, position) -> {
            int x = position[0];
            int y = position[1];

            ImageIcon token = getImage(path);
            JLabel tokenLabel = new JLabel(token);
            tokenLabel.setBounds(x, y, token.getIconWidth(), token.getIconHeight());
            layeredPane.add(tokenLabel, Integer.valueOf(3));
        });
    }




    //********************************************************************************
    //                               Action Listeners
    //********************************************************************************
    private ActionListener moveListener() { // Move button action listener
        return e -> {
            JPopupMenu locationMenu = new JPopupMenu();

            for(String location : manager.getAvailableLocations()) {
                JMenuItem locationItem = new JMenuItem(location);

                locationItem.addActionListener(a -> {
                    manager.move(location);
                    showTokens();
                    currentPlayerInfo();
                });

                locationMenu.add(locationItem);
            }

            locationMenu.show(btn_move, btn_move.getWidth()/2, btn_move.getHeight()/2); // Show the location menu
        };
    }


    private ActionListener takeRoleListener() { // Take Role button action listener
        return e -> {
            JPopupMenu roleMenu = new JPopupMenu();

            for(Map.Entry<String, String> role : manager.getAvailableRoles().entrySet()) {
                JMenuItem roleItem = new JMenuItem(role.getKey() + role.getValue());

                roleItem.addActionListener(a -> {
                    manager.takeRole(role.getKey());
                    showTokens();
                    currentPlayerInfo();
                });

                roleMenu.add(roleItem);
            }

            roleMenu.show(btn_role, btn_role.getWidth()/2, btn_role.getHeight()/2);
        };
    }


    private ActionListener rehearseListener() { // Rehearse button action listener
        return e -> {
          manager.rehearse();
          displayMessage("+1 Practice Chips", Color.GREEN, 150);
          currentPlayerInfo();
        };
    }


    private ActionListener actListener() { // Act button action listener
        return e -> {
            int[] actResult = manager.act(); // get hacky act results

            // display rolled number
            int diceResult = actResult[1];
            displayMessage("You rolled a: " + diceResult, Color.BLACK, 0);

            // display success or failure message
            String message = actResult[0] == 1 ? "ACT SUCCESS!" : "ACT FAILED!";
            Color color = actResult[0] == 1 ? Color.GREEN : Color.RED;
            displayMessage(message, color, 35);

            // display bonus message
            if(actResult[2] == 1){
                displayMessage("You got a bonus!", Color.BLUE, 70);
            }

            if(actResult[0] == 1){ // if the act was successful
                clearTakes();
                showTakes();
            }

            if(actResult[3] == 1){ // if the day is over
                JOptionPane endDayMessage = new JOptionPane("Scenes are wrapped and the day has ended!", JOptionPane.INFORMATION_MESSAGE);
                JDialog dialog = endDayMessage.createDialog(layeredPane, "End of Day");
                dialog.setVisible(true);

                manager.endTurn();
                currentPlayerInfo();
                showTokens();
            }

            if(actResult[4] == 1){ // if the game is over
                Map<String, Integer> playerScores = manager.scoreGame();

                // sort entries by score in descending order
                List<Map.Entry<String, Integer>> sortedScores = playerScores.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .toList();

                StringBuilder scores = new StringBuilder();

                // get winner(s)
                Iterator<Map.Entry<String, Integer>> iterator = playerScores.entrySet().iterator();
                Map.Entry<String, Integer> firstEntry = iterator.next();
                String currWinner = firstEntry.getKey();
                int currScore = firstEntry.getValue();
                boolean tie = iterator.hasNext() && iterator.next().getValue() == currScore;

                scores.append(tie ? "It's a tie!\n\n" : currWinner + " wins!\n\n");
                
                // get scores
                for(Map.Entry<String, Integer> entry : sortedScores){
                    scores.append(entry.getKey()).append(":   ").append(entry.getValue()).append(" points\n\n");
                }

                // display the scores
                JOptionPane endGameMessage = new JOptionPane(scores, JOptionPane.INFORMATION_MESSAGE);
                JDialog dialog = endGameMessage.createDialog(layeredPane, "Game Over: Final Scores");
                dialog.setVisible(true);

                System.exit(0);
            }

            currentPlayerInfo();
            showCards();
            showTokens();
        };
    }


    private ActionListener upgradeListener() { // Upgrade button action listener
        return e -> {
            // Get available upgrades and player's current money
            var availableUpgrades = manager.getAvailableUpgrades();
            int playerDollars = manager.getCurrentPlayer().getDollars();
            int playerCredits = manager.getCurrentPlayer().getCredits();

            JDialog dialog = new JDialog();
            GridBagLayout layout = new GridBagLayout();
            dialog.setLayout(layout);

            JButton confirmButton = new JButton("Confirm");
            JButton cancelButton = new JButton("Cancel");

            GridBagConstraints constraints = new GridBagConstraints();

            ButtonGroup group = new ButtonGroup();

            confirmButton.setEnabled(false);

            // Create an AtomicInteger to be used as an index in the forEach loop
            AtomicInteger i = new AtomicInteger();

            // Add upgrade options to the dialog
            availableUpgrades.forEach((rank, options) -> {
                constraints.gridy = i.getAndIncrement(); // Set the y position to the current index
                ImageIcon icon = getImage("/resources/images/tokens/w" + rank + ".png");
                JLabel rankLabel = new JLabel(icon);
                constraints.gridx = 0;
                dialog.add(rankLabel, constraints);

                boolean canUpgrade = rank > manager.getCurrentPlayer().getRank();

                for (String option : options) {
                    String[] parts = option.split(" "); // Split the option into its price and currency
                    int price = Integer.parseInt(parts[0]); // Get the price

                    // Only enable the radio button if rank > player rank and the player can afford the upgrade
                    boolean canAfford = parts[1].equals("dollars") ? playerDollars >= price : playerCredits >= price;

                    JCheckBox currencyButton = new JCheckBox(parts[0] + " " + parts[1]);
                    currencyButton.addItemListener(s -> confirmButton.setEnabled(currencyButton.isSelected())); // Enable the confirm button iff the radio button is selected
                    currencyButton.setEnabled(canAfford && canUpgrade);
                    currencyButton.setActionCommand(rank + " " + parts[1]);
                    group.add(currencyButton);

                    if (parts[1].equals("dollars")) { // If the currency is dollars
                        constraints.gridx = 1;
                    } else { // If the currency is credits
                        constraints.gridx = 2;
                    }

                    dialog.add(currencyButton, constraints);
                }

                // Only enable the rank label if the player can afford any of the upgrade options
                boolean canAffordAny = options.stream().anyMatch(option -> {
                    String[] parts = option.split(" "); // Split the option into its price and currency
                    int price = Integer.parseInt(parts[0]); // Get the price
                    return parts[1].equals("dollars") ? playerDollars >= price : playerCredits >= price;
                });

                rankLabel.setEnabled(canAffordAny && canUpgrade); // Enable the rank label if the player can afford any of the options
            });

            constraints.gridy = availableUpgrades.size();
            constraints.gridx = 0;
            constraints.gridwidth = 1;
            constraints.insets = new Insets(20, 0, 0, 0);

            dialog.add(confirmButton, constraints);
            constraints.gridx = 2;
            dialog.add(cancelButton, constraints);

            confirmButton.addActionListener(a -> {
                String actionCommand = group.getSelection().getActionCommand();
                String[] parts = actionCommand.split(" "); // Split the upgrade into its rank and currency
                String rankStr = parts[0]; // Get the selected rank
                String currency = parts[1]; // Get the selected currency

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
                    dialog.dispose();
                    currentPlayerInfo();
                    showTokens();
                } else { // If the upgrade is invalid
                    JOptionPane.showMessageDialog(layeredPane, "Please select an upgrade");
                }
            });

            cancelButton.addActionListener(a -> dialog.dispose());

            // Set the dialog properties
            dialog.setIconImage(getImage("/resources/images/tokens/w6.png").getImage());
            dialog.setTitle("   Casting Office: Pay dollars OR credits to upgrade.");
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setSize(400, 400);
            dialog.setLocationRelativeTo(layeredPane);
            dialog.setVisible(true);
        };
    }


    private ActionListener endTurnListener() { // End Turn button action listener
        return e -> {
            manager.endTurn();
            currentPlayerInfo();
            showTokens();
        };
    }




    //********************************************************************************
    //                               Helper Methods
    //********************************************************************************
    private ImageIcon getImage(String path) throws NullPointerException {
        URL url = getClass().getResource(path);
        ImageIcon image = new ImageIcon(Objects.requireNonNull(url));
        image.setImage(image.getImage().getScaledInstance(image.getIconWidth(), image.getIconHeight(), Image.SCALE_DEFAULT));
        return image;
    }


    private JPanel createButton(String buttonName, Dimension buttonSize, int top, int bottom, ActionListener action) {
        JButton button = new JButton(buttonName);
        button.setPreferredSize(buttonSize);
        button.addActionListener(action);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(button);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(top, 0, bottom, 0));
        return buttonPanel;
    }


    private JLabel createLabel(String text, int fontSize, int spacing) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Serif", Font.BOLD, fontSize));
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, spacing, 0));
        return label;
    }


    private String renamePrompt(String name) {
        JTextField input = new JTextField();
        final JComponent[] inputs = new JComponent[] {
                new JLabel("Please enter a name for " + name + ": "),
                input
        };

        String newName;
        do {
            JOptionPane optionPane = new JOptionPane(inputs, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION);
            JDialog prompt = optionPane.createDialog(layeredPane, "Rename Player");

            // Use a timer to request focus after the dialog is visible
            new Timer(100, e -> input.requestFocusInWindow()).start(); // Request focus on the text field

            prompt.setVisible(true);
            newName = input.getText().trim(); // remove leading and trailing whitespace
        } while(newName.isEmpty());

        return newName;
    }


    // Display a gradually fading message in the message panel
    private void displayMessage(String message, Color color, int y) {

        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Serif", Font.BOLD, 25));
        messageLabel.setForeground(color);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = y;

        messagePanel.add(messageLabel, c);
        messagePanel.revalidate();

        int delay = 100; // Delay between each iteration
        int totalDuration = 2000; // Total duration of the animation

        ActionListener messageFader = new ActionListener() {
            int trigger = 0; // The number of times the timer has triggered
            public void actionPerformed(ActionEvent evt) { // on each trigger
                float opacity = 1.0f - ((float) trigger * delay / totalDuration); // Calculate the opacity of the label
                messageLabel.setForeground(new Color(
                        messageLabel.getForeground().getRed() / 255f,
                        messageLabel.getForeground().getGreen() / 255f,
                        messageLabel.getForeground().getBlue() / 255f,
                        opacity)); // Set the opacity of the label
                trigger++;

                if (trigger * delay >= totalDuration) { // If the animation is complete
                    ((Timer)evt.getSource()).stop(); // Stop the timer
                    messagePanel.remove(messageLabel);
                    messagePanel.revalidate();
                    messagePanel.repaint();
                }
            }
        };

        new Timer(delay, messageFader).start(); // Start the timer
    }
}