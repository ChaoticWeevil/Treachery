package com.treachery.game.Weapons;

import com.badlogic.gdx.math.Vector2;
import com.treachery.game.Game;
import com.treachery.game.messageClasses.Projectile;

public class Gun extends Weapon{
    public int damage;
    public int projectiles = 1;
    public float velocity;
    public float fireRate; // Number of seconds between shots

    @Override
    public void Shoot(Vector2 startPosition, Vector2 targetPosition, Game parent) {
        if (ammoLoaded > 0 && (cooldown <= 0)) {
            cooldown = fireRate * 100;
            Projectile p = new Projectile();
            p.locationX = startPosition.x;
            p.locationY = startPosition.y;
            p.targetX = targetPosition.x;
            p.targetY = targetPosition.y;
            p.damage = damage;
            p.velocity = velocity;
            p.texture = projectileTexture;
            p.width = projectileWidth;
            p.height = projectileHeight;
            p.rotate = projectileRotate;
            p.collision = projectileCollision;
            p.removeOnHit = projectileRemoveOnHit;
            parent.client.sendTCP(p);
            ammoLoaded -= 1;

        }


    }

    @Override
    public void reload(Game parent) {
        if (parent.player.inventory.getSelectedAmmo() + ammoLoaded > clipSize) {
            parent.player.inventory.modifySelectedAmmo(-clipSize + ammoLoaded);
            ammoLoaded = clipSize;
            cooldown = 100;

        }
        else if (parent.player.inventory.getSelectedAmmo() > 0) {
            ammoLoaded = parent.player.inventory.getSelectedAmmo() + ammoLoaded;
            parent.player.inventory.modifySelectedAmmo(-parent.player.inventory.getSelectedAmmo());
            cooldown = 100;
        }
    }

    @Override
    public Weapon getWeapon() {
        return new Gun();
    }
}
