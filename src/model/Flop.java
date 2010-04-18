package model;

public class Flop {
	
	private Card[] cards;
	private int nbCards;
	
	public Flop() {
		cards = new Card[5];
		nbCards = 0;
	}
	
	public Card getCard(int index) {
		return cards[index];
	}
	
	public void addCard(Card card) {
		cards[nbCards++] = card;
	}
	
	public int getNbCards() {
		return nbCards;
	}
}