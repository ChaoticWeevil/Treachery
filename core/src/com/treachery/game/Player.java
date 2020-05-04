package com.treachery.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;

public class Player {
    Game parent;
    float x = 1;
    float y = 1;
    float width = 50;
    float height = 50;
    int health;
    String texture = "ers.png";
    String username;
    Pool<Rectangle> rectPool = new Pool<Rectangle>() {
        @Override
        protected Rectangle newObject() {
            return new Rectangle();
        }
    };

    Boolean up = false, down = false, left = false, right = false;
    final int speed = 4;

    public Player(Game parent) {
        this.parent = parent;
    }

    public void update() {
        MapObjects objects = parent.map.getLayers().get("Collision").getObjects();
        Rectangle rectangle = rectPool.obtain();
        if (left) {
            x -= speed;
            if(isBlocked(objects)) x += speed;
        }
        if (right) {
            x += speed;
            if(isBlocked(objects)) x -= speed;
        }
        if (down) {
            y -= speed;
            if(isBlocked(objects)) y += speed;
        }
        if (up) {
            y += speed;
            if(isBlocked(objects)) y -= speed;
        }
        parent.camera.position.x = x;
        parent.camera.position.y = y;
    }

    public boolean isBlocked(MapObjects mapObjects) {
        if (x < 0 || y < 0 || x + width > parent.MAP_WIDTH || y + height > parent.MAP_HEIGHT) return true;


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
        r.set(0,0,0,0);
        return false;
    }


    public void render(SpriteBatch batch) {
        batch.draw(parent.manager.get(texture, Texture.class), parent.WIDTH / 2f, parent.HEIGHT / 2f);
    }


}