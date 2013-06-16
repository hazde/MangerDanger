package gamestate;

import java.awt.Graphics2D;

import java.util.ArrayList;
import java.util.List;

import main.GamePanel;
import main.Sound;
@SuppressWarnings("unused")
public class GameStateManager {
	public static final int NUMGAMESTATES = 16;
	public static final int MENUSTATE = 0;
	public static final int LEVEL1STATE = 1;	
	public static final int DEATHSCREEN = 2;
	private GamePanel panel;
	private GameState[] gameStates;
//	private List<GameState> gameStates;
	private int currentState;
	
	public GameStateManager(GamePanel panel) {
		this.panel = panel;
		gameStates = new GameState[NUMGAMESTATES];
		currentState = MENUSTATE;
		loadState(currentState);
	}
	
	private void loadState(int state) {
		if(state == MENUSTATE)
			gameStates[state] = new MenuState(this, panel);
		else if(state == LEVEL1STATE)
			gameStates[state] = new Level1State(this, panel);
		else if(state == DEATHSCREEN)
			gameStates[state] = new DeathScreen(this, panel);
	}
	
	private void unloadState(int state) {
		gameStates[state] = null;
	}
	
	public void setState(int state) {
		unloadState(currentState);
		Sound.stopAllMusic();
		currentState = state;
		loadState(currentState);
	}
	
	public void setState(int state, boolean ignoreStopMusic) {
		unloadState(currentState);
		if (!ignoreStopMusic) Sound.stopAllMusic();
		currentState = state;
		loadState(currentState);
	}
	
	public void update() {
		if(gameStates[currentState] != null) gameStates[currentState].update();
	}
	
	public void draw(Graphics2D g) {
		if(gameStates[currentState] != null) gameStates[currentState].draw(g);
	}
	
	public void keyPressed(int k) {
		if(gameStates[currentState] != null) gameStates[currentState].keyPressed(k);
	}
	
	public void keyReleased(int k) {
		if(gameStates[currentState] != null) gameStates[currentState].keyReleased(k);
	}
}
