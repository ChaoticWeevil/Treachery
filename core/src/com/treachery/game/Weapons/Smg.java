package com.treachery.game.Weapons;

import com.treachery.game.Game;

public class Smg extends Gun {
    public Smg(Game parent) {
        this.parent = parent;
        damage = 10;
        velocity = 1;
        fireRate = 2;
        ammoType = SMG;
        clipSize = 30;
        ammoLoaded = clipSize;
        ID = 5;
    }
    @Override
    public Weapon getWeapon() {
        return new Smg(parent);
    }
}
