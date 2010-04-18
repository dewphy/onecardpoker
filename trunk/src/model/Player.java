package model;

public class Player {
	
	private int money;
	private int bet;
	private int nbCards;
	private Card[] cards;
	
	public Player() {
		money = 15;
		bet = 0;
		nbCards  = 1;
		cards = new Card[nbCards];
	}
	
	public int getNbCards() {
		return nbCards;
	}
	
	public int getMoney() {
		return money;
	}
	
	public int getBet() {
		return bet;
	}
	
	public Card getCard(int index) {
		return cards[index];
	}
	
	public void setMoney(int money) {
		this.money = money;
	}
	
	public void setBet(int bet) {
		this.bet = bet;
	}
	
	public void setCard(int index, Card card) {
		cards[index] = card;
	}
}