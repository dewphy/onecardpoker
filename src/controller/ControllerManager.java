package controller;

import model.ModelManager;

public class ControllerManager implements AITurn {
	
	private AI myai1;
	private AI myai2;
	
	private ModelManager modelManager;
	private EndOfGameManager endOfGameManager;
	
	public ControllerManager(ModelManager modelManager){
		this.modelManager = modelManager;
		
		myai1 = new AI(this, 1);
		myai2 = new AI(this, 3);
		
		endOfGameManager = new EndOfGameManager(this);
		getModelManager().addAITurn(this);
		if (modelManager.getAITest() == true) {
			testAI();
		}
	}
	
	public void testAI() {
		for(int i=0; i<10000000; i++) {
			if (modelManager.getTurnOfPlayer() == 1) {
				myai1.play();
			} else {
				myai2.play();
			}
		}
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