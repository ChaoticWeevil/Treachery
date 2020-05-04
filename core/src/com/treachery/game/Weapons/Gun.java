package com.treachery.game.Weapons;

import com.badlogic.gdx.math.Vector2;
import com.treachery.game.Game;
import com.treachery.game.messageClasses.Projectile;

public class Gun extends Weapon{
    public int damage;
    public int projectiles = 1;
    public float velocity;
    public float fireRate;

    @Override
    public void Shoot(Vector2 startPosition, Vector2 targetPosition, Game parent) {
        Projectile p = new Projectile();
        p.locationX = startPosition.x;
        p.locationY = startPosition.y;
        p.targetX = targetPosition.x;
        p.targetY = targetPosition.y;
        p.damage = damage;
        p.velocity = velocity;
        parent.sendProjectile(p);
        ammoLoaded -= 1;

    }
}
