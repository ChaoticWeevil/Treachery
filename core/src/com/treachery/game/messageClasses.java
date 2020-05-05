package com.treachery.game;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import java.util.ArrayList;

public class messageClasses {
    public static void registerClasses(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(mapRequest.class);
        kryo.register(mapReceive.class);
        kryo.register(playerUpdate.class);
        kryo.register(serverUpdate.class);
        kryo.register(ArrayList.class);
        kryo.register(User.class);
        kryo.register(Projectile.class);
        kryo.register(Bullet.class);
        kryo.register(Vector2D.class);
        kryo.register(Hit.class);
        kryo.register(RoundStart.class);
        kryo.register(Death.class);
        kryo.register(RoundEnd.class);
    }

    static public class mapRequest {
        String name;

        public mapRequest(String name) {
            this.name = name;
        }

        public mapRequest() {
        }
    }

    static public class mapReceive {
        public String mapName;
    }

    static public class playerUpdate {
        float x;
        float y;
        boolean alive;

        public playerUpdate(float x, float y, boolean alive) {
            this.x = x;
            this.y = y;
            this.alive = alive;
        }

        public playerUpdate() {
        }
    }

    static public class serverUpdate {
        ArrayList<User> userList;
        ArrayList<Bullet> bulletList;

        public serverUpdate(ArrayList<User> list, ArrayList<Bullet> bullets) {
            userList = list;
            bulletList = bullets;
        }

        public serverUpdate() {
        }
    }

    static public class Projectile {
        public float locationX;
        public float locationY;
        public float targetX;
        public float targetY;
        public int damage;
        public float velocity;

        public String texture;
    }

    static public class Hit {
        public int damage;

        public Hit(int damage) {
            this.damage = damage;
        }

        public Hit() {
        }
    }

    static public class RoundStart {
        public int role;

        public RoundStart(int role) {
            this.role = role;
        }

        public RoundStart() {
        }
    }

    static public class RoundEnd {
        public int role;
        public ArrayList<User> userList;
        public RoundEnd(int role, ArrayList<User> userList) {
            this.role = role;
            this.userList = userList;
        }

        public RoundEnd() {
        }
    }


    static public class Death {
        public float x;
        public float y;

        public Death(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public Death() {
        }
    }
}
