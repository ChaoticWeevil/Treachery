package com.treachery.game.Weapons;

import com.badlogic.gdx.math.Vector2;
import com.treachery.game.Game;

public class Weapon {
    public int ID;
    //Ammo types
    public final int NONE = 0; //Ammo not lootable
    public final int PISTOL = 1;
    public final int SHOTGUN = 2;
    public final int SMG = 3;
    public final int RIFLE = 4;

    public String texture = "ers";

    public String projectileTexture = "Projectiles/bullet.png";
    public int projectileWidth = 0;
    public int projectileHeight = 0;
    public boolean projectileCollision = true;
    public boolean projectileRotate = true;
    public boolean projectileRemoveOnHit = true;

    public int clipSize = 1;
    public int ammoType = 0;
    public int ammoLoaded;
    public float cooldown = 0;
    public boolean blank = false;
    public boolean automatic = false;
    Game parent;

    public void Shoot(Vector2 startPosition, Vector2 targetPosition, Game parent) {
    }

    public void altShoot(Vector2 startPosition, Vector2 targetPosition, Game parent) {
    }

    public void reload(Game parent) {
    }

    public Weapon getWeapon() {
        return new Weapon();
    }

}
