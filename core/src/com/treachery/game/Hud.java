package com.treachery.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.treachery.game.Weapons.TraitorWeapons.*;
import com.treachery.game.Weapons.Weapon;


/**
 * Stores and renders the HUD of the game
 *
 * @author ChaoticWeevil
 */
public class Hud {
    Game parent;
    public Window buyMenu;
    public Window buyMenuDescription;
    Stage stage = new Stage();
    Skin skin = new Skin(Gdx.files.internal("skin/star-soldier-ui.json"));
    boolean buyMenuOpen = false;
    Weapon[] traitorMenuList = new Weapon[]{new Harpoon(), new PropDisguiser(), new ErsCannon(), new Radar()};
    Label lbl_buyMenuCredits = new Label("Credits: ", skin);

    Weapon selectedMenuWeapon = traitorMenuList[0].getWeapon();
    Label lbl_WeaponName = new Label("", skin);
    Label lbl_WeaponDescription = new Label("", skin);

    /**
     * Creates the Hud
     *
     * @param parent current Game
     */
    public Hud(final Game parent) {
        this.parent = parent;
        buyMenu = new Window("Buy Menu", skin);
        buyMenu.setMovable(false);
        float newWidth = 600, newHeight = 400;
        buyMenu.setBounds(150,
                (Gdx.graphics.getHeight() - newHeight) / 2, newWidth - 100, newHeight);
        buyMenu.setVisible(false);
        buyMenu.getTitleLabel().setAlignment(Align.center);
        buyMenu.row();
        for (final Weapon w : traitorMenuList) {
            Image image = new Image(parent.manager.get("Weapons/BuyMenu/" + w.texture + ".png",Texture.class));
            image.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (w instanceof  TraitorGun) {
                        lbl_WeaponName.setText(((TraitorGun)w).name);
                        lbl_WeaponDescription.setText(((TraitorGun)w).description);
                    } else {
                        lbl_WeaponName.setText(((TraitorWeapon)w).name);
                        lbl_WeaponDescription.setText(((TraitorWeapon)w).description);
                    }
                    selectedMenuWeapon = w;
                }
            });
            buyMenu.add(image);
        }
        if (selectedMenuWeapon instanceof  TraitorGun) {
            lbl_WeaponName.setText(((TraitorGun)selectedMenuWeapon).name);
            lbl_WeaponDescription.setText(((TraitorGun)selectedMenuWeapon).description);
        } else {
            lbl_WeaponName.setText(((TraitorWeapon)selectedMenuWeapon).name);
            lbl_WeaponDescription.setText(((TraitorWeapon)selectedMenuWeapon).description);
        }
        stage.addActor(buyMenu);
        buyMenuDescription = new Window("Description", skin);
        buyMenuDescription.setBounds(716,
                (Gdx.graphics.getHeight() - newHeight ) / 2, 500 , newHeight );
        buyMenuDescription.getTitleLabel().setAlignment(Align.center);

        lbl_WeaponName.setWrap(true);
        lbl_WeaponDescription.setWrap(true);
        lbl_WeaponName.setAlignment(Align.center);
        lbl_WeaponDescription.setAlignment(Align.center);
        lbl_WeaponDescription.setWidth(400);
        TextButton button = new TextButton("Buy", skin);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!(selectedMenuWeapon == null)) {
                    int cost;
                    if (selectedMenuWeapon instanceof TraitorGun) cost = ((TraitorGun)selectedMenuWeapon).cost;
                    else cost = ((TraitorWeapon)selectedMenuWeapon).cost;
                    if (parent.player.credits >= cost) {
                        parent.player.inventory.addWeapon(selectedMenuWeapon.getWeapon());
                        try {
                            ((TraitorGun) selectedMenuWeapon).bought(parent);
                        } catch (Exception e) {
                            ((TraitorWeapon) selectedMenuWeapon).bought(parent);
                        }
                        parent.player.credits -= cost;
                    }
                }
            }
        });
        buyMenuDescription.align(Align.topLeft);

        buyMenuDescription.add(lbl_buyMenuCredits).padBottom(30);
        buyMenuDescription.row();
        buyMenuDescription.add(lbl_WeaponName).fillX().center().padBottom(15);
        buyMenuDescription.row().width(480);
        buyMenuDescription.row();
        buyMenuDescription.add(lbl_WeaponDescription).center().padBottom(15);
        buyMenuDescription.row();
        buyMenuDescription.add(button).width(200).height(100);

        buyMenuDescription.getCell(lbl_WeaponDescription);
        buyMenuDescription.setMovable(false);
        buyMenuDescription.setVisible(false);
        buyMenuDescription.setDebug(false);

        stage.addActor(buyMenuDescription);


    }

    /**
     * Renders the Hud on the given batch
     *
     * @param batch batch to render on
     */
    public void render(SpriteBatch batch) {
        lbl_buyMenuCredits.setText("Credits: " + parent.player.credits);
        if (parent.gameState == parent.WAITING && !parent.winners.equals("")) {
            parent.font.draw(batch, "The " + parent.winners + " have won the round", 600, 750);
        }
        if (parent.player.alive) {
            // Inventory
            batch.draw(parent.manager.get("Hud/grey_panel.png", Texture.class), 40, 40);
            if (!parent.player.inventory.slot1.texture.equals(""))
                batch.draw(parent.manager.get("Weapons/Hotbar/" + parent.player.inventory.slot1.texture + ".png", Texture.class), 40, 40);
            parent.font.draw(batch, "1", 46, 130);

            batch.draw(parent.manager.get("Hud/grey_panel.png", Texture.class), 150, 40);
            if (!parent.player.inventory.slot2.texture.equals(""))
                batch.draw(parent.manager.get("Weapons/Hotbar/" + parent.player.inventory.slot2.texture + ".png", Texture.class), 150, 40);
            parent.font.draw(batch, "2", 156, 130);

            batch.draw(parent.manager.get("Hud/grey_panel.png", Texture.class), 260, 40);
            if (!parent.player.inventory.slot3.texture.equals(""))
                batch.draw(parent.manager.get("Weapons/Hotbar/" + parent.player.inventory.slot3.texture + ".png", Texture.class), 260, 40);
            parent.font.draw(batch, "3", 266, 130);

            batch.draw(parent.manager.get("Hud/grey_panel.png", Texture.class), 370, 40);
            if (!parent.player.inventory.slot4.texture.equals(""))
                batch.draw(parent.manager.get("Weapons/Hotbar/" + parent.player.inventory.slot4.texture + ".png", Texture.class), 370, 40);
            parent.font.draw(batch, "4", 376, 130);

            // Draws box around selected item
            batch.end();
            parent.shapeRenderer.begin();
            parent.shapeRenderer.setColor(Color.RED);
            parent.shapeRenderer.rect(40 + 110 * (parent.player.inventory.selectedSlot - 1), 40, 100, 100);
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
