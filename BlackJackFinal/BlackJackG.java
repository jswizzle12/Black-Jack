import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;

/**
 * The main class representing the solution for a Blackjack game.
 * Extends the JFrame class to create a graphical user interface.
 */
class BlackJackG extends JFrame {
    static JFrame frame;
    static JButton startButton, exitButton;
    static JLabel label;
    static JPanel playerPanel;
    static JPanel dealerPanel;
    static JLabel playerPointsLabel;
    static JLabel dealerPointsLabel;
    static List<String> playerHand;
    static List<String> dealerHand;
    static List<String> deck;
    static JPanel mainPanel; /* Declare mainPanel as a class member variable

    /**
     * The entry point of the program.
     * Initializes the JFrame and adds components to create the main menu.
     * Handles button actions for starting the game or exiting the program.
     * Displays the JFrame on the screen.
     *
     *  args Command-line arguments
     */
    public static void main(String[] args) {
        frame = new JFrame("panel");
        label = new JLabel("BlackJack");
        startButton = new JButton("Start");
        exitButton = new JButton("Exit");

        JPanel panel = new JPanel();
        panel.add(startButton);
        panel.add(exitButton);
        panel.add(label);
        panel.setBackground(new Color(53, 101, 77));

        frame.add(panel);
        frame.setSize(600, 600);

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                label.setText("Start button clicked");
                createCardPanel();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                        null,
                        "Are you sure you want to exit?",
                        "Exit",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (choice == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        frame.setVisible(true);
    }

    /**
     * Creates the card panel for the Blackjack game.
     * Initializes the player's and dealer's hands, sets up the card panel layout, and adds buttons.
     * Manages the game logic and displays the result when the player chooses to hit or stand.
     */
    public static void createCardPanel() {
        playerHand = new ArrayList<>();
        dealerHand = new ArrayList<>();

        frame.dispose();
        JFrame cardFrame = new JFrame("Blackjack Game");
        mainPanel = new JPanel(); /* Use the class member variable */
        mainPanel.setLayout(new BorderLayout());
        playerPanel = new JPanel();
        playerPanel.setBackground(Color.GREEN);
        mainPanel.add(playerPanel, BorderLayout.NORTH);
        dealerPanel = new JPanel();
        dealerPanel.setBackground(Color.RED);
        mainPanel.add(dealerPanel, BorderLayout.SOUTH);
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(new Color(53, 101, 77));
        mainPanel.add(cardPanel, BorderLayout.CENTER);

        deck = createDeck();

        JButton hitButton = new JButton("Hit");
        hitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!deck.isEmpty()) {
                    Collections.shuffle(deck);
                    String cardImagePath = deck.remove(0);
                    addCardToPanel(cardImagePath, playerPanel);
                    playerHand.add(cardImagePath);
                    int playerTotal = calculateTotal(playerHand);
                    playerPointsLabel.setText("Player Points: " + playerTotal);

                    if (playerTotal > 21) {
                        handleGameResult("Player Busted! Dealer Wins", cardFrame);
                    }
                }
            }
        });
        mainPanel.add(hitButton, BorderLayout.WEST);

        JButton standButton = new JButton("Stand");
        standButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                while (calculateTotal(dealerHand) < 17) {
                    if (!deck.isEmpty()) {
                        Collections.shuffle(deck);
                        String cardImagePath = deck.remove(0);
                        addCardToPanel(cardImagePath, dealerPanel);
                        dealerHand.add(cardImagePath);
                        int dealerTotal = calculateTotal(dealerHand);
                        dealerPointsLabel.setText("Dealer Points: " + dealerTotal);
                    }
                }

                int playerTotal = calculateTotal(playerHand);
                int dealerTotal = calculateTotal(dealerHand);
                if (dealerTotal > 21 || playerTotal > dealerTotal) {
                    handleGameResult("Player Wins", cardFrame);
                } else if (playerTotal < dealerTotal) {
                    handleGameResult("Dealer Wins", cardFrame);
                } else {
                    handleGameResult("(Tie)", cardFrame);
                }
            }
        });
        mainPanel.add(standButton, BorderLayout.EAST);

        playerPointsLabel = new JLabel("Player Points: 0");
        playerPanel.add(playerPointsLabel);
        dealerPointsLabel = new JLabel("Dealer Points: 0");
        dealerPanel.add(dealerPointsLabel);

        cardFrame.add(mainPanel);
        cardFrame.setSize(800, 600);
        cardFrame.setVisible(true);
    }

    /**
     * Handles the result of a game, displaying a message dialog to the user.
     * Disables the hit and stand buttons, reveals the dealer's cards if necessary, and provides options to restart or exit the game.
     *
     *  result    The result of the game
     *  cardFrame The JFrame containing the card panel
     */
    public static void handleGameResult(String result, JFrame cardFrame) {
        /* Disable Hit and Stand buttons */
        mainPanel.getComponent(1).setEnabled(false); /* Hit button*/
        mainPanel.getComponent(2).setEnabled(false); /* Stand button*/

        if (result.equals("Player Busted! Dealer Wins")) {
            /* Player busted, reveal dealer's cards */
            revealDealerCards();
        }

        int choice = JOptionPane.showOptionDialog(
                null,
                result + "\nDo you want to restart?",
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{"Restart", "Exit"},
                null);

        if (choice == JOptionPane.YES_OPTION) {
            /* Restart the game */
            cardFrame.dispose();
            createCardPanel();
        } else {
            /* Exit the game */
            System.exit(0);
        }
    }

    /**
     * Reveals the dealer's cards by adding them to the dealer panel.
     */
    public static void revealDealerCards() {
        for (String cardImagePath : dealerHand) {
            addCardToPanel(cardImagePath, dealerPanel);
        }
    }

    /**
     * Adds a card image to the specified panel.
     *
     *  cardImagePath The file path of the card image
     * panel         The panel to which the card image will be added
     */
    public static void addCardToPanel(String cardImagePath, JPanel panel) {
        try {
            Image cardImage = ImageIO.read(new File(cardImagePath));
            JLabel cardLabel = new JLabel(new ImageIcon(cardImage));
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    panel.add(cardLabel);
                    panel.revalidate();
                    panel.repaint();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a standard deck of 52 playing cards.
     *
     * @return A list of file paths representing the card images in the deck
     */
    public static List<String> createDeck() {
        List<String> deck = new ArrayList<>();
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"};

        for (int i = 0; i < 52; i++) {
            int rankIndex = i % 13;
            int suitIndex = i / 13;
            String suit = suits[suitIndex];
            String rank = ranks[rankIndex];
            String cardImagePath = "./images/" + rank + "_of_" + suit + ".jpg";
            deck.add(cardImagePath);
        }

        return deck;
    }

    /**
     * Calculates the total value of a hand in Blackjack.
     * Accounts for the value of Aces as 1 or 11, depending on the total value of the hand.
     *
     * hand The hand of cards
     * @return The total value of the hand
     */
    public static int calculateTotal(List<String> hand) {
        int total = 0;
        int aceCount = 0;
        for (String card : hand) {
            int cardValue = getCardValue(card);
            if (cardValue == 11) {
                aceCount++;
            }
            total += cardValue;
        }
        while (aceCount > 0 && total + 10 <= 21) {
            total += 10;
            aceCount--;
        }
        return total;
    }

    /**
     * Retrieves the numeric value of a card based on its file path.
     * Aces are assigned a value of 1, while face cards (King, Queen, Jack) are assigned a value of 10.
     *
     * card The file path of the card image
     * @return The numeric value of the card
     */
    public static int getCardValue(String card) {
        String rank = card.substring(card.lastIndexOf('/') + 1, card.indexOf('_'));
        if (rank.equals("Ace")) {
            return 1; /* Change the value of Ace to 1*/
        } else if (rank.equals("King") || rank.equals("Queen") || rank.equals("Jack")) {
            return 10;
        } else {
            return Integer.parseInt(rank);
        }
    }
}
