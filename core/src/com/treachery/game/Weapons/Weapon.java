package com.treachery.game.Weapons;

public class Weapon {
    //Ammo types
    public final int NONE = 0; //Ammo not lootable
    public final int PISTOL = 1;
    public final int SHOTGUN = 2;
    public final int SMG = 3;
    public final int RIFLE = 4;

    public String texture = "ers.png";
    public int clipSize = 1;
    public int ammoType = 0;
    public int ammoLoaded;

    public void Shoot() {
    }

    public void altShoot() {
    }

}
