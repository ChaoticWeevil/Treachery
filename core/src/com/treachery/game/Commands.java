package com.treachery.game;

import com.strongjoshua.console.CommandExecutor;

/**
 * Stores list of commands to be run via in game console
 * Any functions within the class will be a runnable command
 */
public class Commands extends CommandExecutor {
    Game parent;

    public Commands(Game parent) {
        this.parent = parent;
    }

    public void debug(Boolean value) {
        parent.debugMode = value;
    }

    public void heal(int amount) {
        parent.player.damage(-amount);
    }

    public void modifyAmmo(int amount) {
        parent.player.inventory.modifySelectedAmmo(amount);
    }

    public void addCredits(int amount) {
        parent.player.credits += amount;
    }

    public void kill() {
        parent.player.alive = false;
        parent.client.sendTCP(new messageClasses.Death(parent.player.x, parent.player.y));
    }
}
