package com.treachery.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.treachery.game.Weapons.TraitorWeapons.Harpoon;
import com.treachery.game.Weapons.TraitorWeapons.PropDisguiser;
import com.treachery.game.Weapons.TraitorWeapons.TraitorGun;
import com.treachery.game.Weapons.TraitorWeapons.TraitorWeapon;
import com.treachery.game.Weapons.Weapon;



public class Hud {
    Game parent;
    public Window buyMenu;
    Stage stage = new Stage();
    Skin skin = new Skin(Gdx.files.internal("skin/star-soldier-ui.json"));
    boolean buyMenuOpen = false;
    Weapon [] traitorMenuList = new Weapon[]{new Harpoon(), new PropDisguiser()};
    Label buyMenuCredits = new Label("Credits: ", skin);
    public Hud(final Game parent) {
        this.parent = parent;
        buyMenu = new Window("Buy Menu", skin);
        buyMenu.setMovable(false);
        float newWidth = 600, newHeight = 400;
        buyMenu.setBounds((Gdx.graphics.getWidth() - newWidth ) / 2,
                (Gdx.graphics.getHeight() - newHeight ) / 2, newWidth , newHeight );
        buyMenu.setVisible(false);
        buyMenu.getTitleLabel().setAlignment(Align.center);
        buyMenu.add(buyMenuCredits);
        buyMenu.row();
        for (final Weapon w : traitorMenuList) {
            Image image = new Image(parent.manager.get("Weapons/BuyMenu/" + w.texture + ".png",Texture.class));
            image.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    int cost = 0;
                    if (w instanceof TraitorGun) cost = ((TraitorGun)w).cost;
                    else if (w instanceof TraitorWeapon) cost = ((TraitorWeapon)w).cost;
                    if (parent.player.credits >= cost) {
                        parent.player.inventory.addWeapon(w.getWeapon());
                        parent.player.credits -= cost;
                    }

                }
            });
            buyMenu.add(image);
        }



        stage.addActor(buyMenu);

    }

    public void render(SpriteBatch batch) {
        buyMenuCredits.setText("Credits: " + parent.player.credits);
        if (parent.gameState == parent.WAITING && !parent.winners.equals("")) {
            parent.font.draw(batch, "The " + parent.winners + " have won the round", 600, 750);
        }
        if (parent.player.alive) {
            // Inventory
            batch.draw(parent.manager.get("Hud/grey_panel.png", Texture.class), 10, 10);
            if (!parent.player.inventory.slot1.texture.equals(""))
                batch.draw(parent.manager.get("Weapons/Hotbar/" + parent.player.inventory.slot1.texture + ".png", Texture.class), 10, 10);
            parent.font.draw(batch, "1", 16, 100);

            batch.draw(parent.manager.get("Hud/grey_panel.png", Texture.class), 120, 10);
            if (!parent.player.inventory.slot2.texture.equals(""))
                batch.draw(parent.manager.get("Weapons/Hotbar/" + parent.player.inventory.slot2.texture + ".png", Texture.class), 120, 10);
            parent.font.draw(batch, "2", 126, 100);

            batch.draw(parent.manager.get("Hud/grey_panel.png", Texture.class), 230, 10);
            if (!parent.player.inventory.slot3.texture.equals(""))
                batch.draw(parent.manager.get("Weapons/Hotbar/" + parent.player.inventory.slot3.texture + ".png", Texture.class), 230, 10);
            parent.font.draw(batch, "3", 236, 100);

            batch.draw(parent.manager.get("Hud/grey_panel.png", Texture.class), 340, 10);
            if (!parent.player.inventory.slot4.texture.equals(""))
                batch.draw(parent.manager.get("Weapons/Hotbar/" + parent.player.inventory.slot4.texture + ".png", Texture.class), 340, 10);
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
        batch.end();
        stage.act();
        stage.draw();
        batch.begin();

    }
}
