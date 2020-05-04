package com.treachery.game;

import com.badlogic.gdx.Screen;


public class Main extends com.badlogic.gdx.Game {
	Menu menu;
	Game game;

	@Override
	public void create() {
		menu  = new Menu(this);
		change_screen(menu);
	}

	public void change_screen(Screen newScreen) {
		setScreen(newScreen);
	}
}