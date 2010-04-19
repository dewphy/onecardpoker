package view;

import java.util.EventListener;

public interface MainFrameListener extends EventListener {
	
	public void play();
	public void nextstep();
	public void startNewGame();
	public void showcard(int winner, int rank);
	public void end();
}