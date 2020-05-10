package com.treachery.game.Weapons.TraitorWeapons;

import com.badlogic.gdx.math.Vector2;
import com.treachery.game.Game;
import com.treachery.game.Weapons.Weapon;

public class Harpoon extends TraitorGun {
    public Harpoon () {
        name = "Harpoon";
        description = "Throws a single projectile that does 100 damage";
        texture = "harpoon";
        damage = 100;
        ammoType = 0;
        clipSize = 1;
        ammoLoaded = clipSize;
        velocity = 100;
        ID = 5;
    }

    @Override
    public void Shoot(Vector2 startPosition, Vector2 targetPosition, Game parent) {
        super.Shoot(startPosition, targetPosition, parent);
        parent.player.inventory.dropWeapon();
    }

    @Override
    public Weapon getWeapon() {
        return new Harpoon();
    }
}
