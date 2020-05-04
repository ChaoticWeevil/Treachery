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
    }
    static public class mapRequest {
        String name;
        public mapRequest(String name) {
            this.name = name;
        }
    }
    static public class mapReceive {
        public String mapName;
    }
    static public class playerUpdate {
        float x;
        float y;

        public playerUpdate(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
    static public class serverUpdate {
        ArrayList<User> userList;
        public serverUpdate(ArrayList<User> list) {
            userList = list;
        }
        public  serverUpdate(){}
    }
}
