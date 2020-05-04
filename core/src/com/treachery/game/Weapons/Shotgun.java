package com.treachery.game.Weapons;

import com.treachery.game.Game;

public class Shotgun extends Gun {
    public Shotgun(Game parent) {
        this.parent = parent;
        damage = 5;
        velocity = 1;
        projectiles = 5;
        fireRate = 0.5f;
        ammoType = SHOTGUN;
        clipSize = 8;
        ammoLoaded = clipSize;
    }
}
