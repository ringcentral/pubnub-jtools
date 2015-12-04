package com.github.pubnubjtools.pusher.model;

public class WallClock {

    public static final WallClock INSTANCE = new WallClock();

    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    private WallClock() {
    }
}
