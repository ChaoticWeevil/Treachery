package com.treachery.game.Weapons;

public class Smg extends Gun {
    public Smg() {
        damage = 10;
        velocity = 1;
        fireRate = 2;
        ammoType = SMG;
        clipSize = 30;
        ammoLoaded = clipSize;
    }
}
