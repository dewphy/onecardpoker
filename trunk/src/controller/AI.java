package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import model.Card;
import model.ModelManager;
import model.Player;

public class AI {
	
	private ControllerManager controllerManager;
	private int Strategy;
	private NN  nn;
	private NN  nn2;
	private NN  nn3;
	
	public AI(ControllerManager controllerManager, int Strategy) {
		this.controllerManager = controllerManager;
		this.Strategy = Strategy;
		
		if (Strategy == 3 || Strategy == 4) {
			nn = new NN(2,10,1); 
			
			// nn2 = 
			nn3 = new NN(2, 10, 1);
			
			try {
				initNN(nn);
				initNN3(nn3);
			} catch(Exception e) {
				System.out.println(e);
			}
		}
	}
	
	public void play() {
		if (this.Strategy==1) {
			aialwaisraise();
		} else if (Strategy==0) {
			iarandom();
		} else if (Strategy==2) {
			decreaseproba();
		} else if (Strategy==3) {
			NNbasic();
		} else if (Strategy==5) {
			alwaysfall();
		}
	}
	
	public void alwaysfall() {
		ModelManager modelManager = controllerManager.getModelManager();
		modelManager.increaseBet(modelManager.getPlayer(modelManager.getTurnOfPlayer()),0);
		modelManager.changeTurn();
	}
	
	public void iarandom() {
		ModelManager modelManager =  controllerManager.getModelManager();
		double temp = Math.random();
		if(0.5>temp) {
			modelManager.increaseBet(modelManager.getPlayer(modelManager.getTurnOfPlayer()),0);
		} else {
			modelManager.increaseBet(modelManager.getPlayer(modelManager.getTurnOfPlayer()),1);
		}
		modelManager.changeTurn();
	}
	
	public void aialwaisraise() {
		ModelManager modelManager =  controllerManager.getModelManager();
		modelManager.increaseBet(modelManager.getPlayer(modelManager.getTurnOfPlayer()),1);
		modelManager.changeTurn();
	}
	
	public void decreaseproba() {
		ModelManager modelManager =  controllerManager.getModelManager();
		Card card = modelManager.getPlayer(modelManager.getTurnOfPlayer()).getCard(0);
		
		double probability = card.getRank()/14.0;
		double temp = Math.random();
		if(temp<probability) {
			modelManager.increaseBet(modelManager.getPlayer(modelManager.getTurnOfPlayer()),1);
		} else {
			modelManager.increaseBet(modelManager.getPlayer(modelManager.getTurnOfPlayer()),0);
		}modelManager.changeTurn();
	}
	
	public void NNbasic() {
		ModelManager modelManager = controllerManager.getModelManager();
		if (modelManager.getTurnNumber()!=2) {
			Player player = modelManager.getPlayer(modelManager.getTurnOfPlayer());
			double card = player.getCard(0).getRank();
			
			double[] input = new double[2];
			double[] payoff = new double[2];
			double[] proba = new double[2];
			
			input[0] = (card -2)/12;
			
			// Bets 0$.
			input[1] = 0;
			
			if (modelManager.getTurnNumber() == 1) {
				payoff[0] = nn.update(input)[0];
			} else if (modelManager.getTurnNumber() == 3) {
				payoff[0] = nn3.update(input)[0];
			}
			
			if (payoff[0]>1) {
				payoff[0] = 1;
			} else if(payoff[0]<-1) {
				payoff[0] = -1;
			}
			
			// Bets 1$.
			input[1] = 1;
			payoff[1] = nn.update(input)[0];
			
			if (modelManager.getTurnNumber()==1) {
				payoff[0] = nn.update(input)[0];
			} else if(modelManager.getTurnNumber()==3) {
				 payoff[0] = nn3.update(input)[0];
			}
			
			if (payoff[1] > 1) {
				payoff[1] = 1;
			} else if (payoff[1] < -1) {
				payoff[1] = -1;
			}
			proba[0] = 1+ payoff[0];
			proba[1] = 1+ payoff[1];
			
			double tot = (proba[0])+(proba[1]);
			double rand = Math.random()*tot;
			
			if(rand<proba[0]) {
				modelManager.increaseBet(modelManager.getPlayer(modelManager.getTurnOfPlayer()),0);
			} else {
				modelManager.increaseBet(modelManager.getPlayer(modelManager.getTurnOfPlayer()),1);
//				if (payoff[0]>payoff[1]) {
//					System.out.println(" With card " + card + " bet: 0" + " (payoff $0: " + payoff[0] + ", payoff $1: " + payoff[1] + ")");
//					modelManager.increaseBet(modelManager.getPlayer(modelManager.getTurnOfPlayer()),0);
//				} else {
//					System.out.println(" With card " + card + " bet: 1" + " (payoff $1: " + payoff[1] + ", payoff $0: " + payoff[0] + ")");
//					modelManager.increaseBet(modelManager.getPlayer(modelManager.getTurnOfPlayer()),1);
//				}
			}
		} else {
			iarandom();
		}
		modelManager.changeTurn();
	 }
	 
	 public void initNN(NN nn) throws FileNotFoundException {
		String filename = "games.csv";
		int nParties = 100;
		
		double[][] inputs = new double[nParties][2];
		double[][] targets = new double[nParties][1];
		
		Scanner scan = new Scanner(new File(filename));
		
		for (int i=0; scan.hasNext() && i<nParties; i++) {
			double carteJoueur1 = scan.nextDouble();
			double carteJoueur2 = scan.nextDouble();
			double mise1Joueur1 = scan.nextDouble();
			double mise1Joueur2 = scan.nextDouble();
			double mise2Joueur1 = scan.nextDouble();
			double joueurGagnant = scan.nextDouble();
			double gain = scan.nextDouble();
			
			inputs[i][0] = (carteJoueur1-1)/12.0;		// valeur carte joueur 1
			inputs[i][1] = mise1Joueur1;		// action prise
			if (joueurGagnant == 1.0) {			
				targets[i][0] = gain/2;			// gain qu'il a eu
			} else {
				targets[i][0] = -1.0*gain/2;
			}
		}
		nn.train(inputs, targets, 100, 0.5, 0.1);
		// nn.test(inputs, targets);
	}
	
	public void initNN3(NN nn)throws FileNotFoundException {
		int nNeurones = 10;
		String filename = "games.csv";
		int nParties = 200;
		
		Scanner scan = new Scanner(new File(filename));
		
		double[][] inputs = new double[nParties][2];
		double[][] targets = new double[nParties][1];
		
		for (int i=0; scan.hasNext() && i<nParties;) {
			double carteJoueur1 = scan.nextDouble();
			double carteJoueur2 = scan.nextDouble();
			double mise1Joueur1 = scan.nextDouble();
			double mise1Joueur2 = scan.nextDouble();
			double mise2Joueur1 = scan.nextDouble();
			double joueurGagnant = scan.nextDouble();
			double gain = scan.nextDouble();
			
			if (mise1Joueur1 == 0 && mise1Joueur2 == 1) {
				 inputs[i][0] = (carteJoueur1-1)/12;
				 inputs[i][1] = mise2Joueur1;
				  
					if (joueurGagnant == 1.0)
						targets[i][0] = gain/2;			// gain qu'il a eu
					else
						targets[i][0] = -1.0*gain/2;
					i++;
			}
		}
		nn.train(inputs, targets, 100, 0.01, 0.1);
		nn.test(inputs, targets);
	} 	
}