package com.treachery.game.Weapons;

import com.treachery.game.Game;

public class Rifle extends Gun {
    public Rifle(Game parent) {
        this.parent = parent;
        damage = 40;
        velocity = 2;
        fireRate = 0.5f;
        ammoType = RIFLE;
        clipSize = 5;
        ammoLoaded = clipSize;
    }
}
