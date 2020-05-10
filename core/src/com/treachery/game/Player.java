package com.treachery.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.treachery.game.Weapons.Blank;
import com.treachery.game.Weapons.Pistol;
import com.treachery.game.Weapons.Weapon;

public class Player {
    Game parent;
    public Inventory inventory = new Inventory();
    float x = 1;
    float y = 1;
    float width = 50;
    float height = 50;
    int health = 100;
    int credits = 0;
    String texture = "ers.png";
    String username;
    Pool<Rectangle> rectPool = new Pool<Rectangle>() {
        @Override
        protected Rectangle newObject() {
            return new Rectangle();
        }
    };
    boolean alive = false;

    Boolean up = false, down = false, left = false, right = false, shooting = false;
    final int speed = 3;
    int role = 3;

    Vector2 start = new Vector2();
    Vector2 end = new Vector2();
    Polygon polygon = new Polygon();


    public Player(Game parent) {
        this.parent = parent;
    }

    public void update() {
        if (shooting) shoot(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
        inventory.getSelectedWeapon().cooldown --;
        MapObjects objects = parent.map.getLayers().get("Collision").getObjects();
        if (left) {
            x -= speed;
            if (isBlocked(objects)) x += speed;
        }
        if (right) {
            x += speed;
            if (isBlocked(objects)) x -= speed;
        }
        if (down) {
            y -= speed;
            if (isBlocked(objects)) y += speed;
        }
        if (up) {
            y += speed;
            if (isBlocked(objects)) y -= speed;
        }
        parent.camera.position.x = x;
        parent.camera.position.y = y;

    }

    public void shoot(float screenX, float screenY) {
        if (alive) {
            screenX *= ((double) parent.WIDTH / Gdx.graphics.getWidth());
            screenY *= ((double) parent.HEIGHT / Gdx.graphics.getHeight());
            inventory.getSelectedWeapon().Shoot(new Vector2(x, y),
                    new Vector2(screenX + parent.camera.position.x - parent.WIDTH / 2f, screenY + parent.camera.position.y - parent.HEIGHT / 2f), parent);
        }
    }

    public void startRound() {
        x = 1;
        y = 1;
        health = 100;
        alive = true;
        inventory = new Inventory();
        if (role == parent.TRAITOR) credits = 4;
    }

    public void damage(int amount) {
        health -= amount;
        if (health <= 0) {
            parent.client.sendTCP(new messageClasses.Death(x, y));
            alive = false;
        }
    }

    public boolean canSee(float px, float py) {
        if (!alive) return true;
        for (MapObject object : parent.map.getLayers().get("Collision").getObjects()) {
            Rectangle r = ((RectangleMapObject) object).getRectangle();
            boolean wall = false;
            try {
                wall = object.getProperties().get("Wall", boolean.class);
            }catch (Exception ignored){}
            start.set(x,y);
            end.set(px,py);
            polygon.setVertices(new float[]{r.x, r.y,
                    r.x + r.width, r.y,
                    r.x, r.y + r.height,
                    r.x+r.width, r.y + r.height});
            if (Intersector.intersectSegmentPolygon(start, end, polygon) && wall) return false;
        }
        return true;
    }

    public boolean isBlocked(MapObjects mapObjects) {
        if (x < 0 || y < 0 || x + width > parent.MAP_WIDTH || y + height > parent.MAP_HEIGHT) return true;
        if (!alive) return false;
        Rectangle rect = rectPool.obtain();
        rect.set(x, y, width, height);
        for (MapObject object : mapObjects) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
            try {
                if (rect.overlaps(rectangle) && (Boolean) object.getProperties().get("Wall")) {
                    return true;
                }
            } catch (NullPointerException ignored) {
            }
        }
        Rectangle r = rectPool.obtain();
        r.set(0, 0, 0, 0);
        return false;
    }

    public void render(SpriteBatch batch) {
        if (alive) batch.draw(parent.manager.get("OtherTextures/" + texture, Texture.class), parent.WIDTH / 2f, parent.HEIGHT / 2f);
    }

    public class Inventory {
        // Ammo types
        final int NONE = 0;
        final int PISTOL = 1;
        final int SHOTGUN = 2;
        final int SMG = 3;
        final int RIFLE = 4;

        final int MAX_PISTOL_AMMO = 40;
        final int MAX_SHOTGUN_AMMO = 24;
        final int MAX_SMG_AMMO = 80;
        final int MAX_Rifle_AMMO = 20;

        Weapon slot1 = new Pistol(parent);
        Weapon slot2 = new Blank();
        Weapon slot3 = new Blank();
        Weapon slot4 = new Blank();
        int selectedSlot = 1;

        int pistolAmmo = 999;
        int shotgunAmmo = 99;
        int smgAmmo = 99;
        int rifleAmmo = 99;

        public void addWeapon(Weapon w) {
            if (slot1.blank) {
                slot1 = w;
            }
            else if (slot2.blank) {
                slot2 = w;
            }
            else if (slot3.blank) {
                slot3 = w;
            }
            else if (slot4.blank) {
                slot4 = w;
            }
            else {
                parent.droppedWeapons.add(new DroppedWeapon(x, y, w));
                parent.client.sendTCP(new messageClasses.ItemDropped(x, y, w.ID));
            }

        }

        public void dropWeapon() {
            switch (selectedSlot) {
                case 1:
                    if (!(slot1.ammoType == NONE && slot1.ammoLoaded == 0)) {
                        parent.droppedWeapons.add(new DroppedWeapon(x, y, slot1));
                        parent.client.sendTCP(new messageClasses.ItemDropped(x, y, slot1.ID));
                    }
                    slot1 = new Blank();
                    break;
                case 2:
                    if (!(slot2.ammoType == NONE && slot2.ammoLoaded == 0)) {
                        parent.droppedWeapons.add(new DroppedWeapon(x, y, slot2));
                        parent.client.sendTCP(new messageClasses.ItemDropped(x, y, slot1.ID));
                    }
                    slot2 = new Blank();
                    break;
                case 3:
                    if (!(slot3.ammoType == NONE && slot3.ammoLoaded == 0)) {
                        parent.droppedWeapons.add(new DroppedWeapon(x, y, slot3));
                        parent.client.sendTCP(new messageClasses.ItemDropped(x, y, slot1.ID));
                    }
                    slot3 = new Blank();
                    break;
                case 4:
                    if (!(slot4.ammoType == NONE && slot4.ammoLoaded == 0)) {
                        parent.droppedWeapons.add(new DroppedWeapon(x, y, slot4));
                        parent.client.sendTCP(new messageClasses.ItemDropped(x, y, slot1.ID));
                    }
                    slot4 = new Blank();
                    break;
            }
        }

        public boolean hasPistol() {
            return slot1.ammoType == PISTOL || slot2.ammoType == PISTOL || slot3.ammoType == PISTOL || slot4.ammoType == PISTOL;
        }

        public boolean hasShotgun() {
            return slot1.ammoType == SHOTGUN || slot2.ammoType == SHOTGUN || slot3.ammoType == SHOTGUN || slot4.ammoType == SHOTGUN;
        }

        public boolean hasSmg() {
            return slot1.ammoType == SMG || slot2.ammoType == SMG || slot3.ammoType == SMG || slot4.ammoType == SMG;
        }

        public boolean hasRifle() {
            return slot1.ammoType == RIFLE || slot2.ammoType == RIFLE || slot3.ammoType == RIFLE || slot4.ammoType == RIFLE;
        }

        public Weapon getSelectedWeapon() {
            switch (selectedSlot) {
                case 1:
                    return slot1;
                case 2:
                    return slot2;
                case 3:
                    return slot3;
                case 4:
                    return slot4;
            }
            return new Weapon();
        }

        public int getSelectedAmmo() {
            switch (getSelectedWeapon().ammoType) {
                case NONE:
                    return 0;
                case PISTOL:
                    return pistolAmmo;
                case SMG:
                    return smgAmmo;
                case SHOTGUN:
                    return shotgunAmmo;
                case RIFLE:
                    return rifleAmmo;
            }
            return 0;
        }

        public void modifySelectedAmmo(int amount) {
            switch (getSelectedWeapon().ammoType) {
                case PISTOL:
                    pistolAmmo += amount;
                    break;
                case SMG:
                    smgAmmo += amount;
                    break;
                case SHOTGUN:
                    shotgunAmmo += amount;
                    break;
                case RIFLE:
                    rifleAmmo += amount;
                    break;
            }
        }

        public int getMaxAmmo() {
            switch (getSelectedWeapon().ammoType) {
                case NONE:
                    return 0;
                case PISTOL:
                    return MAX_PISTOL_AMMO;
                case SMG:
                    return MAX_SMG_AMMO;
                case SHOTGUN:
                    return MAX_SHOTGUN_AMMO;
                case RIFLE:
                    return MAX_Rifle_AMMO;
            }
            return 0;
        }

    }


}