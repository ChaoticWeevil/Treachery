package com.treachery.game;

import com.badlogic.gdx.Screen;


/**
 * The Main class of Treachery
 *
 * @author ChaoticWeevil
 */
public class Main extends com.badlogic.gdx.Game {
    Menu menu;


    /**
     * Runs when class is created. Creates the menu.
     */
    @Override
    public void create() {
        menu = new Menu(this);
        changeScreen(menu);
    }

    /**
     * Changes the current screen to newScreen
     *
     * @param newScreen screen to change to
     */
    public void changeScreen(Screen newScreen) {
        setScreen(newScreen);
    }
}