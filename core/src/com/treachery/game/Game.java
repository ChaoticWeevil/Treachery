package com.treachery.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.treachery.game.messageClasses.mapRequest;
import com.treachery.game.messageClasses.mapReceive;
import com.treachery.game.messageClasses.playerUpdate;
import com.treachery.game.messageClasses.serverUpdate;

import java.io.IOException;
import java.util.ArrayList;

public class Game implements Screen, InputProcessor {
    Main parent;
    String ip;
    int port;
    boolean debugMode = false;

    public final int WIDTH = 1366;
    public final int HEIGHT = 768;
    public int MAP_HEIGHT = 0;
    public int MAP_WIDTH = 0;

    public TiledMap map;
    public AssetManager manager = new AssetManager();
    OrthographicCamera camera = new OrthographicCamera();
    OrthogonalTiledMapRenderer renderer;
    SpriteBatch batch;
    StretchViewport viewport;
    Player player = new Player(this);
    ArrayList<User> userList = new ArrayList<>();
    ArrayList<Bullet> bulletList = new ArrayList<>();
    BitmapFont font = new BitmapFont();

    Client client = new Client();
    Boolean connected = false;
    Integer updateTime = 5;
    ShapeRenderer shapeRenderer = new ShapeRenderer();
    Hud hud = new Hud(this);


    public Game(String ip, int port, String username, Main parent) {
        this.parent = parent;
        this.ip = ip;
        this.port = port;
        player.username = username;

        manager.setLoader(TiledMap.class, new TmxMapLoader());
        manager.load("maps/map1.tmx", TiledMap.class);
        manager.load("maps/blank.tmx", TiledMap.class);
        manager.load("ers.png", Texture.class);
        manager.load("Hud/grey_panel.png", Texture.class);
        manager.load("Hud/grey_panel_wide.png", Texture.class);
        manager.load("Weapons/pistol.png", Texture.class);
        manager.finishLoading();

        messageClasses.registerClasses(client);
        batch = new SpriteBatch();
        renderer = new OrthogonalTiledMapRenderer(manager.get("maps/blank.tmx", TiledMap.class));

        camera.setToOrtho(false, WIDTH, HEIGHT);
        camera.update();
        viewport = new StretchViewport(WIDTH, HEIGHT,// camera);
                new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        viewport.apply();
        viewport.getCamera().position.x = WIDTH / 2f;
        viewport.getCamera().position.y = HEIGHT / 2f;
        shapeRenderer.setAutoShapeType(true);

    }


    @Override
    public void show() {
        // Attempt to connect to server
        // Returns to menu if fails
        client.start();
        try {
            client.connect(5000, ip, port);
        } catch (Exception e) {
            parent.change_screen(parent.menu);
            System.out.println("Connection Failed:\n" + e);

        }
        client.sendTCP(new mapRequest(player.username));
        client.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                if (object instanceof mapReceive) {
                    mapReceive response = (mapReceive) object;
                    map = manager.get("maps/" + response.mapName, TiledMap.class);
                    connected = true;
                    renderer.setMap(map);
                    MAP_WIDTH = map.getProperties().get("width", Integer.class) * 64;
                    MAP_HEIGHT = map.getProperties().get("height", Integer.class) * 64;
                    for (MapObject mapObject : map.getLayers().get("Collision").getObjects()) {
                        Rectangle r = ((RectangleMapObject) mapObject).getRectangle();
                        mapObject.getProperties().put("Y", MAP_HEIGHT - r.y);
                    }
                    // Starts the game by running update method
                    Timer.schedule(new Timer.Task() {
                                       @Override
                                       public void run() {
                                           update();
                                       }
                                   }
                            , 1/100f
                            , 1/100f
                    );
                }
                else if (object instanceof serverUpdate) {
                    serverUpdate message = (serverUpdate) object;
                    userList = message.userList;
                    bulletList = message.bulletList;
                }
            }
        });
        client.addListener(new Listener() {
            public void disconnected(Connection connection) {
                System.out.println("Error: Lost connection to server");
                parent.change_screen(parent.menu);

            }
        });
        Gdx.input.setInputProcessor(this);
        Pixmap pm = new Pixmap(Gdx.files.internal("Hud/crosshair.png"));
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 1, 6));
        pm.dispose();
    }

    // Runs 100 times per second
    public void update() {
        player.update();
        camera.update();

        updateTime -= 1;
        if (updateTime == 0) {
            client.sendTCP(new playerUpdate(player.x, player.y));
            updateTime = 2;
        }
    }


    @Override
    public void render(float delta) {
        Gdx.graphics.setTitle("Treachery | FPS: " + Gdx.graphics.getFramesPerSecond());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (connected) {
            batch.setProjectionMatrix(viewport.getCamera().combined);
            shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
            renderer.setView(camera);
            renderer.render();
            batch.begin();
            for (User u: userList) {
                batch.draw(manager.get("ers.png", Texture.class), u.x - camera.position.x + WIDTH / 2f, u.y - camera.position.y + HEIGHT / 2f);
                font.draw(batch, u.username, u.x - camera.position.x + WIDTH / 2f, u.y - camera.position.y + HEIGHT / 2f + 65);
            }
            for (Bullet b: bulletList) {
                batch.draw(manager.get("ers.png", Texture.class), b.x - camera.position.x + WIDTH / 2f, b.y - camera.position.y + HEIGHT / 2f);
            }

            hud.render(batch);
            player.render(batch);
            batch.end();

        }

        if (debugMode) renderDebug();

    }

    public void renderDebug() {
        shapeRenderer.begin();
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(player.x - camera.position.x + WIDTH/2f, player.y - camera.position.y + HEIGHT/2f,
                player.width, player.height);
        shapeRenderer.setColor(Color.RED);

        for (MapObject object : map.getLayers().get("Collision").getObjects()) {
            Rectangle r = ((RectangleMapObject) object).getRectangle();
            shapeRenderer.rect(r.x - camera.position.x + WIDTH / 2f, r.y - camera.position.y + HEIGHT / 2f, r.width, r.height);
        }

        shapeRenderer.line(player.x + player.width/2f - camera.position.x + WIDTH/2f,
                player.y + player.height/2f - camera.position.y + HEIGHT/2f,
                viewport.getCamera().unproject(new Vector3(new Vector2(Gdx.input.getX(), HEIGHT - Gdx.input.getY()), 1)).x + 8,
                viewport.getCamera().unproject(new Vector3(new Vector2(Gdx.input.getX(), Gdx.input.getY()), 1)).y - 8);

        shapeRenderer.end();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.A) {
            player.left = true;
        } else if (keycode == Input.Keys.D) {
            player.right = true;
        } else if (keycode == Input.Keys.W) {
            player.up = true;
        } else if (keycode == Input.Keys.S) {
            player.down = true;
        }

        if (keycode == Input.Keys.NUM_1) {
            player.inventory.selectedSlot = 1;
        } else if (keycode == Input.Keys.NUM_2) {
            player.inventory.selectedSlot = 2;
        } else if (keycode == Input.Keys.NUM_3) {
            player.inventory.selectedSlot = 3;
        } else if (keycode == Input.Keys.NUM_4) {
            player.inventory.selectedSlot = 4;
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.A) {
            player.left = false;
        } else if (keycode == Input.Keys.D) {
            player.right = false;
        } else if (keycode == Input.Keys.W) {
            player.up = false;
        } else if (keycode == Input.Keys.S) {
            player.down = false;
        }
        return false;
    }

    public void sendProjectile (messageClasses.Projectile p) {
        client.sendTCP(p);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//        System.out.println(screenX + ", " +  (Gdx.graphics.getHeight() -screenY));
//        Vector2 v = new Vector2(screenX, Gdx.graphics.getHeight() - screenY);
//        v = viewport.project(v);
//        System.out.println(v.x + ", " +  v.y);
        screenY = Gdx.graphics.getHeight() -screenY;
        System.out.println(screenX + ", " + screenY);
        screenX *= ((double) WIDTH / Gdx.graphics.getWidth());
        screenY *= ((double) HEIGHT / Gdx.graphics.getHeight());
        System.out.println(screenX + ", " + screenY);
        if (button == Input.Buttons.LEFT) player.inventory.getSelectedWeapon().Shoot(new Vector2(player.x, player.y),
                new Vector2(screenX + camera.position.x - WIDTH / 2f, screenY + camera.position.y - HEIGHT / 2f), this);
//                new Vector2(v.x + camera.position.x - WIDTH / 2f, v.y + camera.position.y - HEIGHT / 2f), this);
        return false;
    }



    // Unused Methods
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose () {
        batch.dispose();
        try {
            client.dispose();
        } catch (IOException ignored) {}
    }

    @Override
    public boolean keyTyped(char character) {
        if (character == "`".charAt(0)) debugMode = !debugMode;
        return false;
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
