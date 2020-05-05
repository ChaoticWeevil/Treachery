package com.treachery.game;

import com.strongjoshua.console.CommandExecutor;

public class Commands extends CommandExecutor {
    Game parent;
    public Commands (Game parent) {
        this.parent = parent;
    }
    public void debug (Boolean value) {
        parent.debugMode = value;
    }
    public void heal (int amount) {
        parent.player.damage(-amount);
    }
    public void modifyAmmo(int amount) {
        parent.player.inventory.modifySelectedAmmo(amount);
    }
}
