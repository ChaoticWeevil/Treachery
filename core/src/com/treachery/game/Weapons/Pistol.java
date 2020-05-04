package com.treachery.game.Weapons;

public class Pistol extends Gun {
    public Pistol () {
        damage = 10;
        velocity = 1;
        fireRate = 1;
        ammoType = PISTOL;
        clipSize = 8;
        ammoLoaded = clipSize;
        texture = "Weapons/pistol.png";
    }
}
