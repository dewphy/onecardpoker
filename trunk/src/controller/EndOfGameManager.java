package controller;

import java.util.Vector;

import model.Player;

public class EndOfGameManager implements EndGame {
	
	private ControllerManager controllerManager;
	public EndOfGameManager(ControllerManager controllerManager) {
		this.controllerManager = controllerManager;
		controllerManager.getModelManager().addEndGame(this);
	}
	
	public void determineWinner() {
		
		Player player1temp = controllerManager.getModelManager().getPlayer1();
		Player player2temp = controllerManager.getModelManager().getPlayer2();
		
		if(player1temp.getCard(0).getRank()>player2temp.getCard(0).getRank()) {
			controllerManager.getModelManager().payoff(controllerManager.getModelManager().getPot(),0);
		} else if(player1temp.getCard(0).getRank()<player2temp.getCard(0).getRank()) {
			controllerManager.getModelManager().payoff(0,controllerManager.getModelManager().getPot());
		} else {
			controllerManager.getModelManager().payoff(controllerManager.getModelManager().getPot()/2,controllerManager.getModelManager().getPot()/2);
		}
	}
}