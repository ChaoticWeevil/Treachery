package com.treachery.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import javax.swing.*;

public class Hud {
    Game parent;
    public Hud(Game parent) {
        this.parent = parent;
    }

    public void render(SpriteBatch batch) {
        if (parent.gameState == parent.WAITING && !parent.winners.equals("")) {
            parent.font.draw(batch, "The " + parent.winners + " have won the round", 600, 750);
        }
        if (parent.player.alive) {
            // Inventory
            batch.draw(parent.manager.get("Hud/grey_panel.png", Texture.class), 10, 10);
            if (!parent.player.inventory.slot1.texture.equals("")) batch.draw(parent.manager.get(parent.player.inventory.slot1.texture + "Hotbar.png", Texture.class), 10, 10);
            parent.font.draw(batch, "1", 16, 100);

            batch.draw(parent.manager.get("Hud/grey_panel.png", Texture.class), 120, 10);
            if (!parent.player.inventory.slot2.texture.equals(""))batch.draw(parent.manager.get(parent.player.inventory.slot2.texture + "Hotbar.png", Texture.class), 120, 10);
            parent.font.draw(batch, "2", 126, 100);

            batch.draw(parent.manager.get("Hud/grey_panel.png", Texture.class), 230, 10);
            if (!parent.player.inventory.slot3.texture.equals(""))batch.draw(parent.manager.get(parent.player.inventory.slot3.texture + "Hotbar.png", Texture.class), 230, 10);
            parent.font.draw(batch, "3", 236, 100);

            batch.draw(parent.manager.get("Hud/grey_panel.png", Texture.class), 340, 10);
            if (!parent.player.inventory.slot4.texture.equals(""))batch.draw(parent.manager.get(parent.player.inventory.slot4.texture + "Hotbar.png", Texture.class), 340, 10);
            parent.font.draw(batch, "4", 346, 100);

            // Draws box around selected item
            batch.end();
            parent.shapeRenderer.begin();
            parent.shapeRenderer.setColor(Color.RED);
            parent.shapeRenderer.rect(10 + 110 * (parent.player.inventory.selectedSlot - 1), 10, 100, 100);
            parent.shapeRenderer.end();
            batch.begin();



            // Health and stuff
            batch.end();
            parent.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);


            parent.shapeRenderer.setColor(25/255f, 183/255f, 22/255f, 1);
            parent.shapeRenderer.rect(1080, 80, parent.player.health * 2.5f, 20);
            if (parent.player.role == parent.TRAITOR)  parent.shapeRenderer.setColor(Color.RED);
            parent.shapeRenderer.rect(1080, 110, 250, 20);

            parent.shapeRenderer.setColor(Color.BROWN);
            parent.shapeRenderer.rect(1080, 50, (float)parent.player.inventory.getSelectedWeapon().ammoLoaded / parent.player.inventory.getSelectedWeapon().clipSize * 250f,
                    20);

            parent.shapeRenderer.end();
            batch.begin();
            if (parent.player.role == parent.INNOCENT) parent.font.draw(batch, "Innocent", 1085, 125);
            else parent.font.draw(batch, "Traitor", 1085, 125);

            parent.font.draw(batch, Integer.toString(parent.player.health), 1290, 95);
            parent.font.draw(batch, parent.player.inventory.getSelectedWeapon().ammoLoaded + " + " + parent.player.inventory.getSelectedAmmo(), 1290, 65);
        }

    }
}
