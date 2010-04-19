
import controller.ControllerManager;
import view.MainFrame;
import model.ModelManager;


public class Main {
	
	public static void main(String[] args) {
		
		ModelManager modelManager = new ModelManager();
		ControllerManager controllerManager = new ControllerManager(modelManager);
		
		if (modelManager.getAITest() == false)
			new MainFrame(modelManager);
		}
}