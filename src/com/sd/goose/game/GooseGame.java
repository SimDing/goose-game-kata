package com.sd.goose.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sd.goose.game.OutputBoundary.Tile;

public class GooseGame implements InputBoundary {
    private static final int STARTING_TILE = 0;
    private static final int BRIDGE_START = 6;
    private static final int BRIDGE_END = 12;
    private static final int GOAL = 63;
    private static final Set<Integer> GOOSE_TILES = new HashSet<>(List.of(5, 9, 14, 18, 23, 27));

    private final List<String> players = new ArrayList<>();
    private final List<Integer> positions = new ArrayList<>();
    private final OutputBoundary output;

    public GooseGame(OutputBoundary output) {
        this.output = output;
    }

    @Override
    public void addPlayer(String name) {
        players.add(name);
        positions.add(STARTING_TILE);
        output.players(players);
    }

    @Override
    public void rollAndMove(String player) {
        move(player, DiceRoll.roll());
    }

    @Override
    public void move(String player, DiceRoll roll) {
        int index = getPlayerIndex(player);
        output.roll(player, roll.first(), roll.second());

        int from = positions.get(index);
        int result = roll.sum() + from;
        output.move(player, createTileOutput(from), createTileOutput(result));

        if (result == BRIDGE_START) {
            result = BRIDGE_END;
            output.bridge(player, createTileOutput(result));
        }

        while (isGooseTile(result)) {
            result += roll.sum();
            output.moveAgain(player, createTileOutput(result));
        }

        if (result > GOAL) {
            int overshoot = result - GOAL;
            result -= overshoot * 2;
            output.bounce(player, createTileOutput(result));
        }

        if (result == GOAL) {
            output.win(player);
        }

        positions.set(index, result);
        output.finalizeMove();
    }

    public int getPosition(String player) {
        int index = getPlayerIndex(player);
        return positions.get(index);
    }

    private int getPlayerIndex(String player) {
        int index = players.indexOf(player);
        if (index < 0) {
            throw new IllegalArgumentException("Unknown Player");
        }
        return index;
    }

    private boolean isStartTile(int position) {
        return position == STARTING_TILE;
    }

    private boolean isBridgeTile(int position) {
        return position == BRIDGE_START;
    }

    private boolean isGooseTile(int position) {
        return GOOSE_TILES.contains(position);
    }

    private TileOutput createTileOutput(int position) {
        if (isStartTile(position)) return TileOutput.start();
        if (isBridgeTile(position)) return TileOutput.bridge(position);
        if (isGooseTile(position)) return TileOutput.goose(position);
        return TileOutput.normal(position);
    }

    private record TileOutput(TileType type, int position) implements Tile {
        public static TileOutput bridge(int position) {
            return new TileOutput(TileType.Bridge, position);
        }

        public static TileOutput goose(int position) {
            return new TileOutput(TileType.Goose, position);
        }

        public static TileOutput normal(int position) {
            return new TileOutput(TileType.Normal, position);
        }

        public static TileOutput start() {
            return new TileOutput(TileType.Start, 0);
        }
    }

}