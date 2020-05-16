package com.treachery.game.Weapons.TraitorWeapons;

import com.treachery.game.Game;
import com.treachery.game.Weapons.Blank;
import com.treachery.game.Weapons.Weapon;

public class Radar extends TraitorWeapon {
    public Radar() {
        texture = "radar";
        name = "Radar";
        description = "Allows you to see where other players are";
    }

    @Override
    public void bought(Game parent) {
        parent.player.inventory.hasRadar = true;
    }

    @Override
    public Weapon getWeapon() {
        return new Blank();
    }
}
