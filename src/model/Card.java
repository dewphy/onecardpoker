package model;

public class Card {
	
	private final int rank;
	private final String suit;
	
	public Card(int rank, String suit) {
		this.rank = rank;
		this.suit = suit;
	}
	
	public int getRank() {
		if (rank == 1)
			return 14;
		return rank;
	}
	
	public String getSuit() {
		;
		return suit;
	}
}