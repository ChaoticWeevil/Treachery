package com.treachery.game.Weapons.TraitorWeapons;

import com.badlogic.gdx.math.Vector2;
import com.treachery.game.Game;
import com.treachery.game.Weapons.Weapon;

public class PropDisguiser extends TraitorWeapon {
    public PropDisguiser(){
        name = "Prop Disguiser";
        description = "Disguises the player as a prop";
        texture = "propDisguise";
        ammoLoaded = 1;
        ID = 6;
    }

    @Override
    public void Shoot(Vector2 startPosition, Vector2 targetPosition, Game parent) {
        parent.player.texture = "propDisguise";
        parent.player.showName = false;
    }

    @Override
    public void altShoot(Vector2 startPosition, Vector2 targetPosition, Game parent) {
        parent.player.texture = "ers";
        parent.player.showName = true;
    }

    @Override
    public Weapon getWeapon() {
        return new PropDisguiser();
    }
}
