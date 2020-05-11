package com.treachery.game.Weapons.TraitorWeapons;

import com.badlogic.gdx.math.Vector2;
import com.treachery.game.Game;
import com.treachery.game.Weapons.Weapon;

public class ErsCannon extends TraitorGun {
    public ErsCannon () {
        name = "ErsCannon";
        description = "Fires a large ers head that deals 50 damage";
        texture = "ers";
        damage = 50;
        ammoType = 0;
        clipSize = 1;
        ammoLoaded = 1;
        velocity = 15;
        projectileCollision = false;
        projectileWidth = 200;
        projectileHeight = 200;
        projectileRotate = false;
        projectileRemoveOnHit = false;
        ID = 7;
        projectileTexture = "Projectiles/ers.png";
    }

    @Override
    public void Shoot(Vector2 startPosition, Vector2 targetPosition, Game parent) {
        super.Shoot(startPosition, targetPosition, parent);
        parent.player.inventory.dropWeapon();
    }

    @Override
    public Weapon getWeapon() {
        return new ErsCannon();
    }
}
