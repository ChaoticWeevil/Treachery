package com.treachery.game.Weapons;

public class Rifle extends Gun {
    public Rifle() {
        damage = 40;
        velocity = 2;
        fireRate = 0.5f;
        ammoType = RIFLE;
        clipSize = 5;
        ammoLoaded = clipSize;
    }
}
