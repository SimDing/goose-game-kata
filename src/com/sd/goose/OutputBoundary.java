package com.sd.goose;

public interface OutputBoundary {
    void roll(String player, int first, int second);
    void move(String player, Tile origin, Tile target);
    void moveAgain(String player, Tile target);
    void bounce(String player, Tile target);
    void bridge(String player, Tile target);
    void win(String player);

    interface Tile {
        TileType type();
        int position();
    }
}
