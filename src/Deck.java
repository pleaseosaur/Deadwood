// imports
import java.util.Collections;
import java.util.List;

public class Deck {
    // fields
    private static Deck deck;
    private final List<Card> cards;
    private int nextCard;

    // constructor
    private Deck(List<Card> c){
        this.cards = c;
        this.nextCard = 0;
        Collections.shuffle(cards);
    }

    // getters and setters
    public static void initializeDeck(List<Card> c){
        if(deck != null){
            throw new IllegalStateException("Deck has already been initialized");
        }
        deck = new Deck(c);
    }

    public static Deck getInstance(){
        if(deck == null){
            throw new IllegalStateException("Deck has not been initialized");
        }
        return deck;
    }

    // drawScene: draws scene card to be assigned to location
    public Card drawScene(){
        if (nextCard < cards.size()) {
            Card card = cards.get(nextCard);
            nextCard++;
            return card;
        } else {
            throw new IllegalStateException("No more cards in deck");
        }
    }
}
