package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import model.Card;
import model.ModelManager;
import model.Player;

public class AI {
	
	private ControllerManager controllerManager;
	private int strategy;
	private NN  nn1;
	private NN  nn2;
	private NN  nn3;
	
	public AI(ControllerManager controllerManager, int strategy) {
		this.controllerManager = controllerManager;
		this.strategy = strategy;
		
		if (strategy == 3 || strategy == 4) {
			nn1 = new NN(2,10,1); 
			nn2 = new NN(3, 15, 1);
			nn3 = new NN(2, 10, 1);
			
			try {
				initNN(nn1);
				initNN2(nn2);
				initNN3(nn3);
				
				test();
				
			} catch(Exception e) {
				System.out.println(e);
			}
		}
	}
	
	public void play() {
		if (strategy==1) {
			aialwaisraise();
		} else if (strategy==0) {
			iarandom();
		} else if (strategy==2) {
			decreaseproba();
		} else if (strategy == 3 || strategy == 4){
			NNbasic();
		} else if (strategy==5) {
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
		if (modelManager.getTurnNumber() != 2) {
			Player player = modelManager.getPlayer(modelManager.getTurnOfPlayer());
			double card = player.getCard(0).getRank();
			
			double[] input = new double[2];
			double[] payoff = new double[2];
			double[] proba = new double[2];
			
			input[0] = (card -2)/12;
			
			// Bets 0$.
			input[1] = 0;
			
			if (modelManager.getTurnNumber() == 1) {
				payoff[0] = nn1.update(input)[0];
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
			payoff[1] = nn1.update(input)[0];
			
			if (modelManager.getTurnNumber()==1) {
				payoff[0] = nn1.update(input)[0];
			} else if(modelManager.getTurnNumber()==3) {
				 payoff[0] = nn3.update(input)[0];
			}
			
			if (payoff[1] > 1) {
				payoff[1] = 1;
			} else if (payoff[1] < -1) {
				payoff[1] = -1;
			}
			
			if (this.strategy==3)
			{
				proba[0] = 1 + payoff[0];
				proba[1] = 1 + payoff[1];
			}
			else if(this.strategy==4)
			{
				
				if (payoff[0]>payoff[1]) {
					//System.out.println(" => with card " + card + " bet: 0" + " payoff $0: " + payoff[0] + " payoff $1: " + payoff[1]);
					//System.out.println(" => prob to bet 0 : "+fProba(payoff[0]-payoff[1]));
					proba[0] = fProba(payoff[0]-payoff[1]);
					proba[1] = 1-proba[0];
				}
				else {
					//System.out.println(" => with card " + card + " bet: 1" + " payoff $1: " + payoff[1] + " payoff $0: " + payoff[0]);
					//System.out.println(" => prob to bet 1 : "+fProba(payoff[1]-payoff[0]));
					proba[1] = fProba(payoff[1]-payoff[0]);
					proba[0] = 1-proba[1];
				}
			}
			
			double tot = (proba[0])+(proba[1]);
			double rand = Math.random()*tot;
			
			if(rand<proba[0]) {
				modelManager.increaseBet(modelManager.getPlayer(modelManager.getTurnOfPlayer()),0);
			} else {
				modelManager.increaseBet(modelManager.getPlayer(modelManager.getTurnOfPlayer()),1);
			}
		} else {
			NN2basic();
		}
		modelManager.changeTurn();
	}
	
	public void NN2basic() {
		ModelManager modelManager = controllerManager.getModelManager();
		Player player = modelManager.getPlayer(modelManager.getTurnOfPlayer());
		Player opponent;
		if (modelManager.getTurnOfPlayer() == 1) {
			opponent = modelManager.getPlayer2();
		} else {
			opponent = modelManager.getPlayer1();
		}
		
		double card = player.getCard(0).getRank();
		
		double prob = 0.0;
		double[] input = new double[3];
		double[] payoff = new double[2];
		double[] proba = new double[2];
		
		input[0] = (card-2)/12;
		input[1] = opponent.getBet();
		
		// Bets 0$.
		input[2] = 0.0;
		payoff[0] = nn2.update(input)[0];
		
		// Bets 1$.
		input[2] = 1.0;
		payoff[1] = nn2.update(input)[0];
		
		if(this.strategy==3)
		{
			proba[0] = 1 + payoff[0];
			proba[1] = 1 + payoff[1];
		}
		else if(this.strategy==4)
		{
			
			if (payoff[0]>payoff[1]) {
				//System.out.println(" => with card " + card + " bet: 0" + " payoff $0: " + payoff[0] + " payoff $1: " + payoff[1]);
				//System.out.println(" => prob to bet 0 : "+fProba(payoff[0]-payoff[1]));
				proba[0] = fProba(payoff[0]-payoff[1]);
				proba[1] = 1-proba[0];
			}
			else {
				//System.out.println(" => with card " + card + " bet: 1" + " payoff $1: " + payoff[1] + " payoff $0: " + payoff[0]);
				//System.out.println(" => prob to bet 1 : "+fProba(payoff[1]-payoff[0]));
				proba[1] = fProba(payoff[1]-payoff[0]);
				proba[0] = 1-proba[1];
			}
			
			/*System.out.println("--- If opponent bets 0$ ---");
			if (input[1] == 0) {
				if (payoff[0]>payoff[1]) {
					System.out.println(" => with card " + card + " bet: 0" + " payoff $0: " + payoff[0] + " payoff $1: " + payoff[1]);
					System.out.println(" => prob to bet 0 : "+fProba(payoff[0]-payoff[1]));
				}
				else {
					System.out.println(" => with card " + card + " bet: 1" + " payoff $1: " + payoff[1] + " payoff $0: " + payoff[0]);
					System.out.println(" => prob to bet 1 : "+fProba(payoff[1]-payoff[0]));
				}
			} else {
				if (payoff[0]>payoff[1]) {
					System.out.println(" => with card " + card + " bet: 0" + " payoff $0: " + payoff[0] + " payoff $1: " + payoff[1]);
					System.out.println(" => prob to bet 0 : "+fProba(payoff[0]-payoff[1]));
				}
				else {
					System.out.println(" => with card " + card + " bet: 1" + " payoff $1: " + payoff[1] + " payoff $0: " + payoff[0]);
					System.out.println(" => prob to bet 1 : "+fProba(payoff[1]-payoff[0]));
				}
			}*/
		}
		
		double tot = (proba[0])+(proba[1]);
		double rand = Math.random()*tot;
		
		if(rand<proba[0]) {
			modelManager.increaseBet(modelManager.getPlayer(modelManager.getTurnOfPlayer()),0);
		} else {
			modelManager.increaseBet(modelManager.getPlayer(modelManager.getTurnOfPlayer()),1);
		}
		
	}
	
	public void initNN(NN nn) throws FileNotFoundException {
		String filename = "results100.txt";
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
	
	public void initNN3(NN nn) throws FileNotFoundException {
		int nNeurones = 10;
		String filename = "results100.txt";
		int nParties = 100;
		
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
	
	public void initNN2(NN nn) throws FileNotFoundException {
		String filename = "results.txt";
		int nParties = 200;
		
		Scanner scan = new Scanner(new File(filename));
		
		double[][] inputs = new double[nParties][3];
		double[][] targets = new double[nParties][1];
		
		int i=0;
		
		while (scan.hasNext() && i <nParties)
		{
			double carteJoueur1 = scan.nextDouble();
			double carteJoueur2 = scan.nextDouble();
			double mise1Joueur1 = scan.nextDouble();
			double mise1Joueur2 = scan.nextDouble();
			double mise2Joueur1 = scan.nextDouble();
			double joueurGagnant = scan.nextDouble();
			double gain = scan.nextDouble();
			
			inputs[i][0] = (carteJoueur2-1.0)/12.0;		// valeur carte joueur 1
			//inputs[i][1] = (carteJoueur2-1.0)/12.0;
			inputs[i][1] = mise1Joueur1;				// action prise
			inputs[i][2] = mise1Joueur2;
			if (joueurGagnant == 2)
				targets[i][0] = gain/2.0;				// gain qu'il a eu
			else
				targets[i][0] = -1.0*(gain/2.0);
			i++;
		}
		
		nn.train(inputs, targets, 1000, 0.01, 0.1);
		nn.test(inputs, targets);

	}
	
	/**
	 * Returns a probability between 0.0 and 1.0 depending on the payoffs
	 * @param x	the delta between the payoffs
	 * @return the probability
	 */
	public static double fProba(double x) {
		double lambda = 5;
		return 1.0/(1+Math.exp(-1.0*x*lambda));
	}
	
	public void test() {
		
		double[] input = new double[2];
		double[] payoff = new double[2];
		double[] proba = new double[2];
		
		System.out.println("NN1:");
		for (int card=1; card<14; card++) {
			input[0] = (card-1)/12.0;
			
			// Bets 0$, then $1.
			input[1] = 0;	payoff[0] = nn1.update(input)[0];
			input[1] = 1;	payoff[1] = nn1.update(input)[0];
			
			if (payoff[0]>payoff[1]) {
				//System.out.println(" => with card " + card + " bet: 0" + " payoff $0: " + payoff[0] + " payoff $1: " + payoff[1]);
				//System.out.println(" => prob to bet 0 : "+fProba(payoff[0]-payoff[1]));
				proba[0] = fProba(payoff[0]-payoff[1]);
				proba[1] = 1-proba[0];
				System.out.println(card + ", " + payoff[0] + ", " + payoff[1] + ", " + proba[0] + ", " + proba[1]);
			}
			else {
				//System.out.println(" => with card " + card + " bet: 1" + " payoff $1: " + payoff[1] + " payoff $0: " + payoff[0]);
				//System.out.println(" => prob to bet 1 : "+fProba(payoff[1]-payoff[0]));
				proba[1] = fProba(payoff[1]-payoff[0]);
				proba[0] = 1-proba[1];
				System.out.println(card + ", " + payoff[0] + ", " + payoff[1] + ", " + proba[0] + ", " + proba[1]);
			}
		}
		
		double prob = 0.0;
		input = new double[3];
		payoff = new double[2];
		
		System.out.println("--- If opponent bets 0$ ---");
		for (int card=1; card<14; card++) 
		{	
			input[0] = (card-1)/12.0;
			input[1] = 0.0; // Opponent bets 0$
			// Bets 0$.
			input[2] = 0.0;
			payoff[0] = nn2.update(input)[0];
			
			// Bets 1$.
			input[2] = 1.0;
			payoff[1] = nn2.update(input)[0];
			
			//System.out.println("Card " + card + ", input[0]: [" + input[0] +
			//		", payoff($0): " + payoff[0] + ", payoff($1): " + payoff[1]);
			
			if (payoff[0]>payoff[1]) 
			{
				//System.out.println(" => with card " + card + " bet: 0" + " payoff $0: " + payoff[0] + " payoff $1: " + payoff[1]);
				//System.out.println(" => prob to bet 0 : "+fProba(payoff[0]-payoff[1]));
				System.out.println(card+","+payoff[0]+","+payoff[1]+","+fProba(payoff[0]-payoff[1])+","+(1-fProba(payoff[0]-payoff[1])));
			} 
			else 
			{
				//System.out.println(" => with card " + card + " bet: 1" + " payoff $1: " + payoff[1] + " payoff $0: " + payoff[0]);
				//System.out.println(" => prob to bet 1 : "+fProba(payoff[1]-payoff[0]));
				System.out.println(card+","+payoff[0]+","+payoff[1]+","+(1-fProba(payoff[1]-payoff[0]))+","+fProba(payoff[1]-payoff[0]));
			}
		}
		System.out.println("--- If opponent bets 1$ ---");
		for (int card=1; card < 14; card++)
		{
			input[0] = (card-1)/12.0;
			
			input[1] = 1.0; // Opponent bets 1$
			// Bets 0$.
			input[2] = 0.0;
			payoff[0] = nn2.update(input)[0];
			
			// Bets 1$.
			input[2] = 1.0;
			payoff[1] = nn2.update(input)[0];
			
			//System.out.println("Card " + card + ", input[0]: [" + input[0] +
			//		", payoff($0): " + payoff[0] + ", payoff($1): " + payoff[1]);
			
			if (payoff[0]>payoff[1]) 
			{
				//System.out.println(" => with card " + card + " bet: 0" + " payoff $0: " + payoff[0] + " payoff $1: " + payoff[1]);
				//System.out.println(" => prob to bet 0 : "+fProba(payoff[0]-payoff[1]));
				System.out.println(card+","+payoff[0]+","+payoff[1]+","+fProba(payoff[0]-payoff[1])+","+(1-fProba(payoff[0]-payoff[1])));
			} 
			else 
			{
				//System.out.println(" => with card " + card + " bet: 1" + " payoff $1: " + payoff[1] + " payoff $0: " + payoff[0]);
				//System.out.println(" => prob to bet 1 : "+fProba(payoff[1]-payoff[0]));
				System.out.println(card+","+payoff[0]+","+payoff[1]+","+(1-fProba(payoff[1]-payoff[0]))+","+fProba(payoff[1]-payoff[0]));
			}
		}
		
		input = new double[2];
		System.out.println("NN3:");
		for (int card=1; card<14; card++) {
			
			input[0] = (card-1)/12.0;
			
			// Bets 0$, then $1.
			input[1] = 0;	payoff[0] = nn3.update(input)[0];
			input[1] = 1;	payoff[1] = nn3.update(input)[0];
			
			if (payoff[0]>payoff[1]) {
				//System.out.println(" => with card " + card + " bet: 0" + " payoff $0: " + payoff[0] + " payoff $1: " + payoff[1]);
				//System.out.println(" => prob to bet 0 : "+fProba(payoff[0]-payoff[1]));
				proba[0] = fProba(payoff[0]-payoff[1]);
				proba[1] = 1-proba[0];
				System.out.println(card + ", " + payoff[0] + ", " + payoff[1] + ", " + proba[0] + ", " + proba[1]);
			}
			else {
				//System.out.println(" => with card " + card + " bet: 1" + " payoff $1: " + payoff[1] + " payoff $0: " + payoff[0]);
				//System.out.println(" => prob to bet 1 : "+fProba(payoff[1]-payoff[0]));
				proba[1] = fProba(payoff[1]-payoff[0]);
				proba[0] = 1-proba[1];
				System.out.println(card + ", " + payoff[0] + ", " + payoff[1] + ", " + proba[0] + ", " + proba[1]);
			}
		}
	}
	
	public NN getnn(int number)
	{
		if(number == 1)
			return this.nn1;
		else if(number==2)
			return this.nn2;
		else if(number==3)
			return this.nn3;
		
		return null;
	}
}