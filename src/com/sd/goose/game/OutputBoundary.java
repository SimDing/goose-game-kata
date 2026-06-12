package com.sd.goose.game;

import java.util.List;

public interface OutputBoundary {
    void roll(String player, int first, int second);
    void move(String player, Tile origin, Tile target);
    void moveAgain(String player, Tile target);
    void bounce(String player, Tile target);
    void bridge(String player, Tile target);
    void win(String player);
    void finalizeMove();
    void players(List<String> players);

    interface Tile {
        TileType type();
        int position();
    }
}
