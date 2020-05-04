package com.treachery.game.Weapons;

public class Shotgun extends Gun {
    public Shotgun() {
        damage = 5;
        velocity = 1;
        projectiles = 5;
        fireRate = 0.5f;
        ammoType = SHOTGUN;
        clipSize = 8;
        ammoLoaded = clipSize;
    }
}
