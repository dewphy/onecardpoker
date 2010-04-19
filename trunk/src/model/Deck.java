package model;

import java.util.Collections;
import java.util.Stack;

public class Deck {
	
	private Stack<Card> cards;
	
	public Deck() {
		cards = new Stack<Card>();
		
		int[] ranks = {1, 2, 3, 4, 5, 6, 7, 8, 9 ,10 ,11, 12 ,13};
		String[] suits = {"spades", "diamonds", "clubs", "hearts"};
		
		for (String suit : suits) {
			for (int rank : ranks) {
				cards.add(new Card(rank, suit));
			}
		}
	}
	
	public void shuffle() {
		Collections.shuffle(cards);
	}
	
	public Card getCard() {
		return cards.pop();
	}
}