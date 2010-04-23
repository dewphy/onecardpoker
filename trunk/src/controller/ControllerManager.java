package controller;

import javax.swing.JOptionPane;

import model.ModelManager;

public class ControllerManager implements AITurn {
	
	private AI myai1;
	private AI myai2;
	
	private ModelManager modelManager;
	private EndOfGameManager endOfGameManager;
	
	public ControllerManager(ModelManager modelManager){
		this.modelManager = modelManager;
		
		myai1 = new AI(this, 2);
		myai2 = new AI(this, 4);
		
		endOfGameManager = new EndOfGameManager(this);
		getModelManager().addAITurn(this);
		if (modelManager.getAITest() == true) {
			testAI();
		}
	}
	
	public void testAI() {
		
		for(int i=0; i<1000000000; i++) {
			if (modelManager.getTurnOfPlayer() == 1) {
				myai1.play();
			} else {
				myai2.play();
			}
			
			if(this.modelManager.getTurnNumber()==1)
				;//this.ailearning();
		}
	}
	
	public void ailearning()
	{
		double[][] inputs = this.modelManager.getinputs();
		double[][] targets = this.modelManager.gettargets();
		if(this.modelManager.getendturnnumber()==2 && this.modelManager.getlastplayernumber()==2)
		{
			myai2.getnn(2).train(inputs, targets, 100, 0.01, 0.1);
		}
		else if(this.modelManager.getendturnnumber()==3)
		{
			myai2.getnn(3).train(inputs, targets, 100, 0.01, 0.1);
		}
		else
			myai2.getnn(1).train(inputs, targets, 100, 0.5, 0.1);
	}
	
	public ModelManager getModelManager() {
		return modelManager;
	}
	
	public void play() {
		if (modelManager.getTurnOfPlayer() == 1) {
			myai1.play();
		} else if (modelManager.getTurnOfPlayer() == 2) {
			myai2.play();
		}
	}
}