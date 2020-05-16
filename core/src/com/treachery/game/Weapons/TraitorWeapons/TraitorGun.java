package com.treachery.game.Weapons.TraitorWeapons;

import com.badlogic.gdx.math.Vector2;
import com.treachery.game.Game;
import com.treachery.game.Weapons.Gun;
import com.treachery.game.Weapons.Weapon;

public class TraitorGun extends Gun {
    public int cost = 1;
    public String name = "NAME MISSING";
    public String description = "DESCRIPTION MISSING";

    @Override
    public void Shoot(Vector2 startPosition, Vector2 targetPosition, Game parent) {
        super.Shoot(startPosition, targetPosition, parent);
    }

    @Override
    public void altShoot(Vector2 startPosition, Vector2 targetPosition, Game parent) {
        super.altShoot(startPosition, targetPosition, parent);
    }

    @Override
    public void reload(Game parent) {
        super.reload(parent);
    }

    @Override
    public Weapon getWeapon() {
        return new TraitorGun();
    }

    public void bought(Game parent) {
    }

}
