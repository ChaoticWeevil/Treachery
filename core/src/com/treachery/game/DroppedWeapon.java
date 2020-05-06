package com.treachery.game;

import com.treachery.game.Weapons.Weapon;

public class DroppedWeapon {
    float x;
    float y;
    Weapon weapon;
    public DroppedWeapon(float x, float y, Weapon weapon) {
        this.x = x;
        this.y = y;
        this.weapon = weapon;
    }
    public DroppedWeapon(){}
}
