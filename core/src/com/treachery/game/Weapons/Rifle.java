package com.treachery.game.Weapons;

import com.treachery.game.Game;

public class Rifle extends Gun {
    public Rifle(Game parent) {
        this.parent = parent;
        damage = 40;
        velocity = 100;
        fireRate = 0.6f;
        ammoType = RIFLE;
        clipSize = 5;
        ammoLoaded = clipSize;
        texture = "Weapons/rifle";
        ID = 4;
    }
    @Override
    public Weapon getWeapon() {
        return new Rifle(parent);
    }
}
