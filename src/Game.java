/*
 * Author: Peter Hafner and Andrew Cox
 * Date: 16 May 2023
 * Purpose: Main
 */

// imports


import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Objects;

public class Game {

    private JFrame frame;
    private JPanel panel;
    private JButton btn_move, btn_take_role, btn_rehearse, btn_act;
    private JLabel playerLabel;
    private JTextArea playerStatsArea;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Game().GUI());
    }

    private void GUI() {
        frame = new JFrame("Deadwood"); // Create and set up the window.
        panel = new JPanel(); // Create a panel to hold all other components
        panel.setLayout(new BorderLayout()); // Use BorderLayout for panel

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2)); // Create a separate panel for the buttons
        btn_move = new JButton("Move"); // Create buttons
        btn_take_role = new JButton("Take Role");
        btn_rehearse = new JButton("Rehearse");
        btn_act = new JButton("Act");

        buttonPanel.add(btn_move); // Add the buttons to the button panel
        buttonPanel.add(btn_take_role);
        buttonPanel.add(btn_rehearse);
        buttonPanel.add(btn_act);

        JPanel statsPanel = new JPanel(); // Create a panel for player stats
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS)); // Use BoxLayout for statsPanel
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add a border to the panel
        playerLabel = new JLabel("Current Player: "); // Create a label to display player stats
        playerStatsArea = new JTextArea(5, 2); // Create a text area to display player stats
        statsPanel.add(playerLabel); // Add the stats label to the stats panel
        statsPanel.add(playerStatsArea); // Add the text area to the stats panel


        // Setup right panel to contain player stats and buttons
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(statsPanel, BorderLayout.NORTH);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);


        JLayeredPane layeredPane = new JLayeredPane(); // Create the JLayeredPane to hold the board, cards, tokens, etc.


        URL boardURL = getClass().getResource("/images/board.jpg");
        ImageIcon board = new ImageIcon(boardURL); // Create the board image
        layeredPane.setPreferredSize(new Dimension(board.getIconWidth(), board.getIconHeight())); // Set the size of the game board
        board.setImage(board.getImage().getScaledInstance(board.getIconWidth(), board.getIconHeight(), Image.SCALE_DEFAULT)); // Scale the board image to fit the board
        JLabel boardLabel = new JLabel(board); // Add the board image to a label
        boardLabel.setBounds(0, 0, board.getIconWidth(), board.getIconHeight()); // Set the size of the board label

        // testing - dummy player icon
        URL playerURL = getClass().getResource("/images/tokens/r1.png");
        ImageIcon playerIcon = new ImageIcon(playerURL);
        playerIcon.setImage(playerIcon.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT));
        JLabel player = new JLabel(playerIcon);
        player.setBounds(0, 0, playerIcon.getIconWidth(), playerIcon.getIconHeight());

        // testing - layering
        layeredPane.add(boardLabel, Integer.valueOf(1));
        layeredPane.add(player, Integer.valueOf(2));


        panel.add(layeredPane, BorderLayout.CENTER); // Add the layered pane to the panel
        panel.add(rightPanel, BorderLayout.EAST); // Add the right panel to the panel

        frame.add(panel); // Add the panel to the frame
        frame.setSize(1300, 1000); // Set the size of the frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

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
            // game logic
        } else { // 'Cancel' selected or dialog closed
            System.out.println("Game Setup Cancelled!");
            // exit
        }
    }
}


