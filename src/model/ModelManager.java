package model;

import javax.swing.JOptionPane;
import javax.swing.event.EventListenerList;

import controller.AITurn;
import controller.EndGame;

import view.MainFrameListener;


public class ModelManager {
	
	private static final int bigBlind = 1;
	private static final int smallBlind = 1;
	
	private Player player1;
	private Player player2;
	
	private int pot;
	private int turnOfPlayer;
	private final EventListenerList listeners = new EventListenerList();
	private int button;
	private int turnNumber; // use for one card poker, because we just have 3 turn max
	
	private Flop flop;
	private Deck deck;
	
	/// for AI test
	private int nbVictories1 = 0;
	private int nbVictories2 = 0;
	private boolean AITest = true;
	private double[][] inputs;
	private double[][] targets;
	private int endturnnumber;
	private int lastplayernumber;
	
	public boolean getAITest() {
		return AITest;
	}
	
	public double [][]getinputs()
	{
		return this.inputs;
	}
	
	public int getendturnnumber()
	{
		return this.endturnnumber;
	}
	
	public int getlastplayernumber()
	{
		return this.lastplayernumber;
	}
	
	public double[][]gettargets()
	{
		return this.targets;
	}
	
	public void AIStat() {
		if (!player1.hasMoney())
			nbVictories2++;
		else
			nbVictories1++;
		
		if (nbVictories2+nbVictories1 >= 1) {
			JOptionPane.showMessageDialog(null,"player 1: "+nbVictories1+"\nplayer 2: "+nbVictories2,"End",JOptionPane.INFORMATION_MESSAGE);
			nbVictories2 = 0;
			nbVictories1 = 0;
		}
	}
	
	public void AIlearn(int player1value,int player2value)
	{
		//System.out.println (this.turnNumber);
		this.endturnnumber = this.turnNumber;
		this.lastplayernumber = this.turnOfPlayer;
		if(turnNumber==2 && this.turnOfPlayer==2)
		{
			inputs = new double[1][3];
			inputs[0][1] = this.player1.getBet()-1;
			inputs[0][2] = this.player2.getBet()-1;
			//System.out.println(inputs[0][1]);
			//System.out.println(inputs[0][2]);
		}
		else
		{
			inputs = new double[1][2];
			inputs[0][1] = this.player2.getBet()-1;
			//System.out.println(inputs[0][1]);
		}
		//System.out.println("/////");
		targets = new double[1][1];
		targets[0][0] = (player2value-(double)this.player2.getBet())/2;
		inputs[0][0] = (this.player2.getCard(0).getRank()-2)/12;
		//System.out.println(targets[0][0]);
	}
	
	public ModelManager() {
		initPlayers();
		button = 1;
		init();
	}
	
	public void init() {
		turnNumber = 1;
		turnOfPlayer = button;
		pot = 0;
		flop = new Flop();
		deck = new Deck();
		deck.shuffle();
		distributeCards2Players();
		initBets();
	}
	
	public void startNewGame() {
		if(getPlayer1().hasMoney() && getPlayer2().hasMoney()) {
			turnNumber = 1;
			switchButton();
			player1.setBet(0);
			player2.setBet(0);
			init();
			fireNewgameRequest();
		} else {
			if(AITest == true) {
				AIStat();
			}
			fireEndRequest();
			initPlayers();
			init();
			fireNewgameRequest();
		}
	}
	
	public void initPlayers() {
		player1 = new Player();
		player2 = new Player();
	}
	
	public void initBets() {
		if (button == 1) {
			increaseBet(player1, smallBlind);
			increaseBet(player2, bigBlind);
		}
		else {
			increaseBet(player1, bigBlind);
			increaseBet(player2, smallBlind);
		}
	}
	
	public void switchButton() {
		if (button == 1) {
			button = 2;
		} else {
			button = 1;
		}
	}
	
	public void payoff(int player1value,int player2value) {
		pot = 0;
		
		AIlearn(player1value,player2value);
		
		player1.setBet(0);
		player2.setBet(0);
		
		player2.setMoney(player2.getMoney() + player2value);
		player1.setMoney(player1.getMoney() + player1value);
		
		if (player1value == 0)
			fireShowCardRequest(2, player2value);
		else if(player2value == 0)
			fireShowCardRequest(1, player1value);
		else
			fireShowCardRequest(0, player2value);
	}
	
	public void giveUp() {
		if(player1.getBet() < player2.getBet()) {
			payoff(0, pot);
		} else if (player1.getBet() > player2.getBet()) {
			payoff(pot, 0);
		} else if (!player1.hasMoney()) {
			payoff(0, pot);
		} else if (!player2.hasMoney()) {
			payoff(pot, 0);
		}
		startNewGame();
	}
	
	public void changeTurn() {
		
		if (turnNumber>1 && player1.getBet() == player2.getBet()) {
			fireDetermineWinner();
			startNewGame();
		} else if (this.turnNumber==3 || (this.turnNumber==2 && this.getPlayer(turnOfPlayer).getBet()<this.getPlayer(this.button).getBet())) {
			giveUp();
		} else {
			turnNumber++;
			this.fireNextStepRequest();
			switchturn();
		}
	}
	
	public void switchturn() {
		if (turnOfPlayer == 1) {
			turnOfPlayer = 2;
			bet();
		} else if (turnOfPlayer == 2) {
			turnOfPlayer = 1;
			bet();
		}
	}
	
	public void bet() {
		if (turnOfPlayer == 2) {
			fireAITurnRequest();
		} else if(turnOfPlayer == 1)
		{
			this.fireAITurnRequest();
			//this.firePlayerTurnRequest();/////////////////////// ia vs ia
		}
	}
	
	public void distributeCards2Players() {
		for (int i=0; i<player1.getNbCards(); i++) {
			player1.setCard(i, deck.getCard());
		}
		for (int i=0; i<player2.getNbCards(); i++) {
			player2.setCard(i, deck.getCard());
		}
	}
	
	public boolean distributeFlop() {
		if(flop.getNbCards()==0) {
			for(int i=0; i<3; i++) {
				this.flop.addCard(deck.getCard());
			}
		} else if(flop.getNbCards() < 5) {
			flop.addCard(deck.getCard());
		} else {
			//fire setwinner
			fireDetermineWinner();
			startNewGame();
			return true;
		}
		return false;
	}
	
	public Player getPlayer1() {
		return player1;
	}
	
	public Player getPlayer2() {
		return player2;
	}
	
	public Flop getFlop() {
		return flop;
	}
	
	public int getPot() {
		return pot;
	}
	
	public void addToPot(int value) {
		pot += value;
	}
	
	public void addAITurn(AITurn listener) {
		listeners.add(AITurn.class, listener);
	}
	
	public AITurn[] getAITurn() {
		return (AITurn[]) listeners.getListeners(AITurn.class);
	}
	
	public void fireAITurnRequest() {	
		for(AITurn listener : getAITurn()) {
			if(this.AITest==false) {
				listener.play();
			}
		}
	}
	
	public void addMainFrameListener(MainFrameListener listener) {
		listeners.add(MainFrameListener.class, listener);
	}
	
	public MainFrameListener[] getMainFrameListener() {
		return (MainFrameListener[]) listeners.getListeners(MainFrameListener.class);
	}
	
	protected void firePlayerTurnRequest() {
		for (MainFrameListener listener : getMainFrameListener()) {
			if (this.AITest == false) {
				listener.play();
			}
		}
	}
	
	protected void fireNextStepRequest()
	{
		for(MainFrameListener listener : getMainFrameListener())
		{
			if(this.AITest == false)
				listener.nextstep();
        }
	}
	
	protected void fireNewgameRequest() {
		for(MainFrameListener listener : getMainFrameListener()) {
			if(this.AITest == false) {
				listener.startNewGame();
			}
		}
	}
	
	protected void fireShowCardRequest(int winner,int value) {
		for(MainFrameListener listener : getMainFrameListener()) {
			if(this.AITest == false) {
				listener.showcard(winner,value);
			}
		}
	}
	
	protected void fireEndRequest() {
		for(MainFrameListener listener : getMainFrameListener()) {
			if (AITest == false) {
				listener.end();}
			}
	}
	
	public void addEndGame(EndGame listener) {
		listeners.add(EndGame.class, listener);
	}
	
	public EndGame[] getEndGame() {
		return (EndGame[]) listeners.getListeners(EndGame.class);
	}
	
	protected void fireDetermineWinner() {
			for(EndGame listener : getEndGame()) {
			listener.determineWinner();
		}
	}
	
	public void increaseBet(Player player, int amonth) {
		if(player.getMoney()-amonth>=0) {
			player.setBet(player.getBet() + amonth);
			player.setMoney(player.getMoney() - amonth);
			addToPot(amonth);
		} else
			giveUp();
	}
	
	public void equalize(Player playerequalizer,Player player) {
		int value = player.getBet()-playerequalizer.getBet();
		playerequalizer.setBet(player.getBet());	
		playerequalizer.setMoney(playerequalizer.getMoney()-value);
		addToPot(value);
	}
	
	public int getTurnNumber() {
		return turnNumber;
	}
	
	public int getTurnOfPlayer() {
		return turnOfPlayer;
	}
	
	public Player getPlayer(int number) {
		if (number==1)
			return player1;
		else
			return player2;
	}
}