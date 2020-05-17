package com.treachery.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.strongjoshua.console.Console;
import com.strongjoshua.console.GUIConsole;
import com.treachery.game.Weapons.*;
import com.treachery.game.messageClasses.mapReceive;
import com.treachery.game.messageClasses.mapRequest;
import com.treachery.game.messageClasses.playerUpdate;
import com.treachery.game.messageClasses.serverUpdate;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

public class Game implements Screen, InputProcessor {
    Main parent;
    String ip;
    int port;
    boolean debugMode = false;

    public final int WIDTH = 1366;
    public final int HEIGHT = 768;
    public final int INNOCENT = 1;
    public final int TRAITOR = 2;
    public final int MID_ROUND = 1;
    public final int WAITING = 2;

    public int gameState = 2;

    public int MAP_HEIGHT = 0;
    public int MAP_WIDTH = 0;

    public TiledMap map;
    public AssetManager manager = new AssetManager();
    OrthographicCamera camera = new OrthographicCamera();
    OrthogonalTiledMapRenderer renderer;
    SpriteBatch batch;
    StretchViewport viewport;
    public Player player = new Player(this);
    ArrayList<User> userList = new ArrayList<>();
    ArrayList<Bullet> bulletList = new ArrayList<>();
    ArrayList<Vector2D> bodyList = new ArrayList<>();
    ArrayList<Rectangle2D.Double> debugList;
    ArrayList<DroppedWeapon> droppedWeapons = new ArrayList<>();
    Weapon[] weapons = new Weapon[]{new Pistol(this), new Shotgun(this), new Smg(this), new Rifle(this)};
    BitmapFont font = new BitmapFont();
    BitmapFont fontSmall = new BitmapFont();

    Client client = new Client();
    Boolean connected = false;
    Integer updateTime = 5;
    ShapeRenderer shapeRenderer = new ShapeRenderer();
    Hud hud;
    Sprite drawBullet;
    Console console = new GUIConsole();
    String winners = "";
    boolean roundStart = false;


    public Game(String ip, int port, String username, Main parent) {
        this.parent = parent;
        this.ip = ip;
        this.port = port;
        player.username = username;

        manager.setLoader(TiledMap.class, new TmxMapLoader());
        manager.load("maps/map1.tmx", TiledMap.class);
        manager.load("maps/map2.tmx", TiledMap.class);
        manager.load("maps/blank.tmx", TiledMap.class);

        String[] folders = new String[]{"Hud/", "Weapons/BuyMenu/", "Weapons/Dropped/", "Weapons/Hotbar/", "OtherTextures/", "Projectiles/"};

        for (String folder : folders) {
            FileHandle[] files;
            // Very bad code that will likely be the cause of many crashes/problems in the future
            if (!(Game.class.getResource("Game.class").toString().contains("core"))) {
                files = JarUtils.listFromJarIfNecessary(folder);
            }
            else {
                files = Gdx.files.internal("core/assets/" + folder).list();
            }
            for (FileHandle file : files) {
                manager.load((folder + file.name()), Texture.class);
            }
        }
        manager.finishLoading();
        hud = new Hud(this);

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

        drawBullet = new Sprite(manager.get("Projectiles/bullet.png", Texture.class));
        drawBullet.setOrigin(drawBullet.getWidth() / 2f, drawBullet.getHeight() / 2f);
        console.setCommandExecutor(new Commands(this));
        console.setDisplayKeyID(Input.Keys.GRAVE);
        console.setDisabled(false);
    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputMultiplexer(this, hud.stage));
        Pixmap pm = new Pixmap(Gdx.files.internal("Hud/crosshair.png"));
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 1, 6));
        pm.dispose();
        // Attempt to connect to server
        // Returns to menu if fails
        client.start();
        try {
            client.connect(5000, ip, port);
        } catch (Exception e) {
            client.stop();
            System.out.println("Connection Failed:\n" + e);
            dispose();
            parent.menu = new Menu(parent);
            parent.menu.window.setVisible(true);
            parent.change_screen(parent.menu);

        }
        client.sendTCP(new mapRequest(player.username));
        client.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof mapReceive) {
                    mapReceive response = (mapReceive) object;
                    debugList = response.list;
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
                            , 1 / 100f
                            , 1 / 100f
                    );
                } else if (object instanceof serverUpdate) {
                    serverUpdate message = (serverUpdate) object;
                    userList = message.userList;
                    bulletList = message.bulletList;
                } else if (object instanceof messageClasses.Hit) {
                    messageClasses.Hit message = (messageClasses.Hit) object;
                    player.damage(message.damage);
                } else if (object instanceof messageClasses.RoundStart) {
                    messageClasses.RoundStart message = (messageClasses.RoundStart) object;
                    player.role = message.role;
                    gameState = MID_ROUND;
                    player.startRound();
                    bodyList.clear();
                    droppedWeapons.clear();
                    roundStart = true;
                } else if (object instanceof messageClasses.RoundEnd) {
                    messageClasses.RoundEnd message = (messageClasses.RoundEnd) object;
                    gameState = WAITING;
                    userList = message.userList;
                    if (message.role == TRAITOR) winners = "traitors";
                    else winners = "innocents";
                } else if (object instanceof messageClasses.Death) {
                    messageClasses.Death message = (messageClasses.Death) object;
                    bodyList.add(new Vector2D(message.x, message.y));
                } else if (object instanceof messageClasses.ItemDropped) {
                    final messageClasses.ItemDropped message = (messageClasses.ItemDropped) object;
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            for (Weapon w : weapons) {
                                if (w.ID == message.weaponID) {
                                    droppedWeapons.add(new DroppedWeapon(message.x, message.y, w.getWeapon()));
                                }
                            }
                        }
                    });

                } else if (object instanceof messageClasses.ItemPickedUp) {
                    final Object o = object;
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            messageClasses.ItemPickedUp msg = (messageClasses.ItemPickedUp) o;
                            ArrayList<DroppedWeapon> droppedRemoveList = new ArrayList<>();
                            for (DroppedWeapon w : droppedWeapons) {
                                if (w.x == msg.x && w.y == msg.y) {
                                    droppedRemoveList.add(w);
                                }
                            }
                            droppedWeapons.removeAll(droppedRemoveList);
                        }
                    });

                }
            }
        });
        client.addListener(new Listener() {
            public void disconnected(Connection connection) {
                System.out.println("Error: Lost connection to server");
                parent.change_screen(parent.menu);

            }
        });
        console.resetInputProcessing();
    }

    // Runs 100 times per second
    public void update() {
        if (roundStart) {
            roundStart = false;
            loadMapObjects();
        }
        player.update();
        camera.update();

        updateTime -= 1;
        if (updateTime == 0) {
            client.sendTCP(new playerUpdate(player.x, player.y, player.alive, player.showName, player.texture));
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
            if (gameState == MID_ROUND) fontSmall.draw(batch, "Mid round", 1, HEIGHT - 2);
            else if (gameState == WAITING) fontSmall.draw(batch, "Waiting", 1, HEIGHT - 2);
            for (User u : userList) {
                if (u.alive) {
                    float drawX = u.x - camera.position.x + WIDTH / 2f;
                    float drawY = u.y - camera.position.y + HEIGHT / 2f;
                    if (player.canSee(u.x, u.y)) {
                        batch.draw(manager.get("OtherTextures/" + u.texture + ".png", Texture.class), drawX, drawY);
                        if (u.showName) font.draw(batch, u.username, drawX,
                                drawY + 65);
                    }
                    if (player.inventory.hasRadar && (!player.canSee(u.x, u.y) || !(drawX + 50 > 0 && drawY + 50 > 0 && drawX < WIDTH && drawY < HEIGHT))) {
                        float radarX = MathUtils.clamp(drawX, 0, WIDTH - 30);
                        float radarY = MathUtils.clamp(drawY, 0, HEIGHT - 30);
                        batch.draw(manager.get("OtherTextures/radarBlip.png", Texture.class), radarX, radarY);
                    }

                }
            }
            for (Vector2D b : bodyList) {
                if (player.canSee((float) b.x, (float) b.y)) {
                    batch.draw(manager.get("OtherTextures/ersBody.png", Texture.class), (float) b.x - camera.position.x + WIDTH / 2f, (float) b.y - camera.position.y + HEIGHT / 2f);
                }
            }
            for (Bullet b : bulletList) {
                if (player.canSee(b.x, b.y)) {
                    drawBullet.setTexture(manager.get(b.texture, Texture.class));
                    if (b.width != 0 && b.height != 0) drawBullet.setSize(b.width, b.height);
                    else drawBullet.setSize(20, 7);
                    drawBullet.setPosition(b.x - camera.position.x + WIDTH / 2f - drawBullet.getWidth() / 2f,
                            b.y - camera.position.y + HEIGHT / 2f - drawBullet.getHeight() / 2f);
                    drawBullet.setRotation(b.angle);
                    if (!b.rotate) drawBullet.setRotation(0);
                    drawBullet.draw(batch);
                }
            }
            for (DroppedWeapon w : droppedWeapons) {
                batch.draw(manager.get("Weapons/Dropped/" + w.weapon.texture + ".png", Texture.class),
                        w.x - camera.position.x + WIDTH / 2f - manager.get("Weapons/Dropped/" + w.weapon.texture + ".png", Texture.class).getWidth() / 2f,
                        w.y - camera.position.y + HEIGHT / 2f - manager.get("Weapons/Dropped/" + w.weapon.texture + ".png", Texture.class).getHeight() / 2f);
            }
            player.render(batch);
            hud.render(batch);
            batch.end();
            console.draw();

            shapeRenderer.begin();
            hud.buyMenuDescription.drawDebug(shapeRenderer);
            shapeRenderer.end();
        }

        if (debugMode) renderDebug();


    }


    public void renderDebug() {
        shapeRenderer.begin();
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(player.x - camera.position.x + WIDTH / 2f, player.y - camera.position.y + HEIGHT / 2f,
                player.width, player.height);
        shapeRenderer.setColor(Color.RED);

//        for (MapObject object : map.getLayers().get("Collision").getObjects()) {
//            Rectangle r = ((RectangleMapObject) object).getRectangle();
//            shapeRenderer.rect(r.x - camera.position.x + WIDTH / 2f, r.y - camera.position.y + HEIGHT / 2f, r.width, r.height);
//        }
        for (Rectangle2D.Double r : debugList) {
            shapeRenderer.rect((float) r.x - camera.position.x + WIDTH / 2f, (float) r.y - camera.position.y + HEIGHT / 2f, (float) r.width, (float) r.height);
        }

        shapeRenderer.line(player.x + player.width / 2f - camera.position.x + WIDTH / 2f,
                player.y + player.height / 2f - camera.position.y + HEIGHT / 2f,
                viewport.getCamera().unproject(new Vector3(new Vector2(Gdx.input.getX(), HEIGHT - Gdx.input.getY()), 1)).x + 8,
                viewport.getCamera().unproject(new Vector3(new Vector2(Gdx.input.getX(), Gdx.input.getY()), 1)).y - 8);

        shapeRenderer.end();
    }

    public void loadMapObjects() {
        for (MapObject mapObject : map.getLayers().get("Collision").getObjects()) {
            // Load weapon drops
            try {
                if (mapObject.getProperties().get("gunSpawn", boolean.class)) {
                    switch (new Random().nextInt(2)) {
                        case 0:
                            droppedWeapons.add(new DroppedWeapon(mapObject.getProperties().get("x", float.class), mapObject.getProperties().get("y", float.class),
                                    new Rifle(player.parent)));
                            break;
                        case 1:
                            droppedWeapons.add(new DroppedWeapon(mapObject.getProperties().get("x", float.class), mapObject.getProperties().get("y", float.class),
                                    new Smg(player.parent)));
                            break;
                    }

                }
            } catch (Exception ignored) {
            }
        }
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
        } else if (keycode == Input.Keys.NUM_1) {
            player.inventory.selectedSlot = 1;
        } else if (keycode == Input.Keys.NUM_2) {
            player.inventory.selectedSlot = 2;
        } else if (keycode == Input.Keys.NUM_3) {
            player.inventory.selectedSlot = 3;
        } else if (keycode == Input.Keys.NUM_4) {
            player.inventory.selectedSlot = 4;
        } else if (keycode == Input.Keys.R) {
            player.inventory.getSelectedWeapon().reload(this);
        } else if (keycode == Input.Keys.ESCAPE) {
            dispose();
            parent.change_screen(new Menu(parent));
        } else if (keycode == Input.Keys.E) {
            //Check for dropped items
            for(int i =0; i< droppedWeapons.size();++i) {
                DroppedWeapon w = droppedWeapons.get(i);
                Rectangle r = new Rectangle();
                r.set(player.x, player.y, player.width, player.height);
                if (r.contains(w.x, w.y)) {
                    player.inventory.addWeapon(w.weapon);
                    client.sendTCP(new messageClasses.ItemPickedUp(w.x, w.y, w.weapon.ID));
                    droppedWeapons.remove(w);

                }
            }
        } else if (keycode == Input.Keys.Q) {
            player.inventory.dropWeapon();
        } else if (keycode == Input.Keys.B && player.role == TRAITOR) {
            hud.buyMenuOpen = !hud.buyMenuOpen;
            hud.buyMenu.setVisible(hud.buyMenuOpen);
            hud.buyMenuDescription.setVisible(hud.buyMenuOpen);
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

    public void sendProjectile(messageClasses.Projectile p) {
        client.sendTCP(p);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        screenY = Gdx.graphics.getHeight() - screenY;
        if (button == Input.Buttons.LEFT) {
            if (!hud.buyMenuOpen) {
                if (player.inventory.getSelectedWeapon().automatic) player.shooting = true;
                else player.shoot(screenX, screenY);
            }
        }
        else if (button == Input.Buttons.RIGHT) {
            if (!hud.buyMenuOpen) {
                if (player.inventory.getSelectedWeapon().automatic) player.shooting = true;
                else player.altShoot(screenX, screenY);
            }
        }

        return false;
    }


    // Unused Methods
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        try {
            client.dispose();
            batch.dispose();
            manager.dispose();
        } catch (Exception e) {
            System.out.println("Caught Exception Disposing Game:\n");
        }
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            if (player.shooting) player.shooting = false;
        }
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
