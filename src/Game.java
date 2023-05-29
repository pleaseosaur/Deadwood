/*
 * Author: Peter Hafner and Andrew Cox
 * Date: 16 May 2023
 * Purpose: Main
 */

// imports


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;

public class Game {
    private GameManager manager;
    private JFrame frame;
    private JPanel panel, buttonPanel, statsPanel, btn_move, btn_role, btn_rehearse, btn_act, btn_upgrade, btn_end;
    private JLayeredPane layeredPane;
    private JLabel playerName, playerRank, playerDollars, playerCredits, playerChips;


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

        // create action listeners for each button
        ActionListener moveListener = e -> {
            JPopupMenu locationMenu = new JPopupMenu();
            for(String location : manager.getAvailableLocations()) {
                JMenuItem menuItem = new JMenuItem(location);
                menuItem.addActionListener(a -> {
                    manager.move(location);
                    showTokens();
                    currentPlayerInfo();
                });
                locationMenu.add(menuItem);
            }
            locationMenu.show(btn_move, btn_move.getWidth()/2, btn_move.getHeight()/2);
        };

        ActionListener takeRoleListener = e -> {
            manager.takeRole("role"); // TODO - take role logic
            currentPlayerInfo();
        };

        ActionListener rehearseListener = e -> {
            manager.rehearse(); // TODO - rehearse logic
            currentPlayerInfo();
        };

        ActionListener actListener = e -> {
            manager.act(); // TODO - act logic
            currentPlayerInfo();
        };

        ActionListener upgradeListener = e -> {
            Area area = new Area(0, 0, 0, 0); // dummy area - remove
            Upgrade upgrade = new Upgrade(0, "c", 0, area); // dummy upgrade - remove
            manager.upgrade(upgrade, "$"); // TODO - upgrade logic
            currentPlayerInfo();
        };

        ActionListener endTurnListener = e -> {
            manager.endTurn();
            currentPlayerInfo();
        };


        // create buttons and add to respective panels
        this.btn_move  = createButton("Move", buttonSize, 100, 0, moveListener);
        this.btn_role  = createButton("Take Role", buttonSize, 100, 0, takeRoleListener);
        topButtons.add(btn_move);
        topButtons.add(btn_role);

        this.btn_rehearse = createButton("Rehearse", buttonSize, 10, 10, rehearseListener);
        this.btn_act = createButton("Act", buttonSize, 10, 10, actListener);
        middleButtons.add(btn_rehearse);
        middleButtons.add(btn_act);

        this.btn_upgrade = createButton("Upgrade", buttonSize, 0, 200, upgradeListener);
        this.btn_end = createButton("End Turn", buttonSize, 0, 200, endTurnListener);
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


    // TODO - refactor to allow single card images to be replaced and redisplayed
    private void showCards() {
        manager.getCards().forEach((card, area) -> { // Iterate through the cards and their areas
            String path = card.getImg(); // Get the path to the card image
            int x = area.get(0); // Get the x coordinate of the card
            int y = area.get(1); // Get the y coordinate of the card
            int w = area.get(2); // Get the width of the card
            int h = area.get(3); // Get the height of the card
            ImageIcon cardImage = getImage(path); // Create an image icon from the path
            JLabel cardLabel = new JLabel(cardImage); // Add the image icon to a label
            cardLabel.setBounds(x, y, w, h); // Set the size of the card label
            layeredPane.add(cardLabel, Integer.valueOf(1)); // Add the card to the second layer
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
        });
    }


    private void showTokens() {
        manager.getTokens().forEach((path, position) -> { // Iterate through the tokens and their positions
            int x = position[0]; // Get the x coordinate of the token
            int y = position[1]; // Get the y coordinate of the token
            ImageIcon token = getImage(path); // Create an image icon from the path
            JLabel tokenLabel = new JLabel(token); // Add the image icon to a label
            tokenLabel.setBounds(x, y, token.getIconWidth(), token.getIconHeight()); // Set the size of the token label
            layeredPane.add(tokenLabel, Integer.valueOf(2)); // Add the token to the third layer
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


    private JLabel createLabel(String text, int fontSize, int spacing) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Serif", Font.BOLD, fontSize));
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, spacing, 0));
        return label;
    }


    private JPanel createButton(String buttonName, Dimension buttonSize, int top, int bottom, ActionListener action) {
        JButton button = new JButton(buttonName);
        button.setPreferredSize(buttonSize);
        button.addActionListener(action);

//        if (buttonName.equals("Move")) {
//            JPopupMenu locationMenu = new JPopupMenu();
//            for (String location : manager.getAvailableLocations()) {
//                JMenuItem menuItem = new JMenuItem(location);
//                menuItem.addActionListener(a -> {
//                    manager.move(location);
//                    currentPlayerInfo();
//                });
//                locationMenu.add(menuItem);
//            }
//            button.addMouseListener(new MouseAdapter() {
//                public void mousePressed(MouseEvent e) {
//                    locationMenu.show(e.getComponent(), e.getX(), e.getY());
//                }
//            });
//        }

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(button);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(top, 0, bottom, 0));
        return buttonPanel;
    }

    private void showActiveButtons() {
        var availableActions = manager.getAvailableActions();

        setButtonStyle(btn_move, availableActions.contains("Move"));
        setButtonStyle(btn_role, availableActions.contains("Take Role"));
        setButtonStyle(btn_rehearse, availableActions.contains("Rehearse"));
        setButtonStyle(btn_act, availableActions.contains("Act"));
        setButtonStyle(btn_upgrade, availableActions.contains("Upgrade"));
        setButtonStyle(btn_end, availableActions.contains("End Turn"));
    }

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