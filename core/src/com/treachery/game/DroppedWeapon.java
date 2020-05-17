package com.treachery.game;

import com.treachery.game.Weapons.Weapon;

/**
 * A weapon that is on the floor.
 * Able to be picked up by player.
 * Created by being dropped and by gunSpawn mapObjects
 */
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
