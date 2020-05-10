package com.treachery.game.Weapons;

import com.treachery.game.Game;

public class Smg extends Gun {
    public Smg(Game parent) {
        this.parent = parent;
        damage = 10;
        velocity = 50;
        fireRate = 0.2f;
        ammoType = SMG;
        clipSize = 30;
        ammoLoaded = clipSize;
        texture = "Weapons/smg";
        automatic = true;
        ID = 5;
    }
    @Override
    public Weapon getWeapon() {
        return new Smg(parent);
    }
}
