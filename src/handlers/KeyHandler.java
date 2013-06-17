package handlers;

import java.awt.event.KeyEvent;

// this class contains a boolean array of current and previous key states
// for the 10 keys that are used for this game.
// a key k is down when keyState[k] is true.

public class KeyHandler {
	
	public static final int NUM_KEYS = 32;
	
	public static boolean keyState[] = new boolean[NUM_KEYS];
	public static boolean prevKeyState[] = new boolean[NUM_KEYS];
	
	public static int UP = 0;
	public static int LEFT = 1;
	public static int DOWN = 2;
	public static int RIGHT = 3;
	public static int SPACE = 4;
	public static int BUTTON_W = 5;
	public static int BUTTON_A = 6;
	public static int BUTTON_S = 7;
	public static int BUTTON_D = 8;
	public static int BUTTON_E = 9;
	public static int ENTER = 10;
	public static int ESCAPE = 11;
	public static int F1 = 12;
	public static int F2 = 13;
	public static int F3 = 14;
	public static int F4 = 15;
	public static int F5 = 16;
	
	public static int SHIFT = 17;
	
	public static int NUMBER_1 = 18;
	public static int NUMBER_2 = 19;
	public static int NUMBER_3 = 20;
	
	public static void keySet(int i, boolean b) {
		if(i == KeyEvent.VK_UP) keyState[UP] = b;
		else if(i == KeyEvent.VK_LEFT) keyState[LEFT] = b;
		else if(i == KeyEvent.VK_DOWN) keyState[DOWN] = b;
		else if(i == KeyEvent.VK_RIGHT) keyState[RIGHT] = b;
		else if(i == KeyEvent.VK_SPACE) keyState[SPACE] = b;
		else if(i == KeyEvent.VK_W) keyState[BUTTON_W] = b;
		else if(i == KeyEvent.VK_A) keyState[BUTTON_A] = b;
		else if(i == KeyEvent.VK_S) keyState[BUTTON_S] = b;
		else if(i == KeyEvent.VK_D) keyState[BUTTON_D] = b;
		else if(i == KeyEvent.VK_E) keyState[BUTTON_E] = b;
		else if(i == KeyEvent.VK_ENTER) keyState[ENTER] = b;
		else if(i == KeyEvent.VK_ESCAPE) keyState[ESCAPE] = b;
		else if(i == KeyEvent.VK_F1) keyState[F1] = b;
		else if(i == KeyEvent.VK_F2) keyState[F2] = b;
		else if(i == KeyEvent.VK_F3) keyState[F3] = b;
		else if(i == KeyEvent.VK_F4) keyState[F4] = b;
		else if(i == KeyEvent.VK_F5) keyState[F5] = b;
		else if(i == KeyEvent.VK_SHIFT) keyState[SHIFT] = b;
		else if(i == KeyEvent.VK_1) keyState[NUMBER_1] = b;
		else if(i == KeyEvent.VK_2) keyState[NUMBER_2] = b;
		else if(i == KeyEvent.VK_3) keyState[NUMBER_3] = b;
	}
	
	public static void update() {
		for(int i = 0; i < NUM_KEYS; i++) {
			prevKeyState[i] = keyState[i];
		}
	}
	
	public static boolean isPressed(int i) {
		return keyState[i] && !prevKeyState[i];
	}
	
	public static boolean anyKeyPress() {
		for(int i = 0; i < NUM_KEYS; i++) {
			if(keyState[i]) return true;
		}
		return false;
	}
	
}
