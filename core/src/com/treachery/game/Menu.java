package com.treachery.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.Random;

public class Menu implements Screen {
    FitViewport viewport = new FitViewport(1366f, 768f,
            new OrthographicCamera(1366f, 768f));
    final int SCREEN_HEIGHT = 768;
    final int SCREEN_WIDTH = 1366;
    Main parent;
    Stage stage = new Stage(viewport);
    Skin skin = new Skin(Gdx.files.internal("skin/star-soldier-ui.json"));

    public Menu(final Main parent) {
        this.parent = parent;

        Label lbl_title = new Label("Treachery", skin);
        lbl_title.setFontScale(3);
        lbl_title.setPosition(SCREEN_WIDTH / 2f - lbl_title.getWidth() - 75, 668);
        stage.addActor(lbl_title);

        // Username
        Label lbl_username = new Label("Username:", skin);
        lbl_username.setPosition(450, 580);
        stage.addActor(lbl_username);


        final TextField txt_username = new TextField("Player" + new Random().nextInt(100), skin);
        txt_username.setPosition(625, 568);
        txt_username.setSize(250, 50);
        stage.addActor(txt_username);


        // Online
        Label lbl_online = new Label("Play Online", skin);
        lbl_online.setPosition(250, 468);
        stage.addActor(lbl_online);

        Label lbl_ip = new Label("IP Address:", skin);
        lbl_ip.setPosition(100, 398);
        stage.addActor(lbl_ip);


        final TextField txt_ip = new TextField("localhost", skin);
        txt_ip.setPosition(275, 388);
        txt_ip.setSize(400, 50);
        stage.addActor(txt_ip);

        Label lbl_port = new Label("Port:", skin);
        lbl_port.setPosition(100, 328);
        stage.addActor(lbl_port);


        final TextField txt_port = new TextField("54555", skin);
        txt_port.setPosition(275, 318);
        txt_port.setSize(250, 50);
        txt_port.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        stage.addActor(txt_port);

        TextButton btn_onlineConnect = new TextButton("Connect", skin);
        btn_onlineConnect.setPosition(235, 238);
        btn_onlineConnect.setSize(200, 75);
        btn_onlineConnect.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String ip = txt_ip.getText();
                int port = Integer.parseInt(txt_port.getText());
                String username = txt_username.getText();
                parent.change_screen(new Game(ip, port, username, parent));
            }
        });
        stage.addActor(btn_onlineConnect);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        Pixmap pm = new Pixmap(Gdx.files.internal("Hud/cursor.png"));
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 1, 6));
        pm.dispose();


    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    // Unused Methods
    @Override
    public void resize(int width, int height) {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {}
}
