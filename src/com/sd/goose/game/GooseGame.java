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
    public void rollAndExecuteTurn(String player) {
        executeTurn(player, DiceRoll.roll());
    }

    @Override
    public void executeTurn(String player, DiceRoll roll) {
        int playerId = getPlayerId(player);
        output.roll(player, roll.first(), roll.second());
        int currentPosition = move(player, roll, playerId);
        currentPosition = handleSpecialCases(player, currentPosition, roll);
        positions.set(playerId, currentPosition);
        output.finalizeMove();
    }

    private int getPlayerId(String player) {
        int playerId = players.indexOf(player);
        if (playerId < 0) {
            throw new IllegalArgumentException("Unknown Player");
        }
        return playerId;
    }

    private int move(String player, DiceRoll roll, int playerId) {
        int from = positions.get(playerId);
        int result = roll.sum() + from;
        output.move(player, createTileOutput(from), createTileOutput(result));
        return result;
    }

    private int handleSpecialCases(String player, int currentPosition, DiceRoll roll) {
        currentPosition = handleBridge(player, currentPosition);
        currentPosition = handleGooseTiles(player, roll, currentPosition);
        currentPosition = handleOvershoot(player, currentPosition);
        handleGoal(player, currentPosition);
        return currentPosition;
    }

    private int handleBridge(String player, int currentPosition) {
        if (currentPosition == BRIDGE_START) {
            currentPosition = BRIDGE_END;
            output.bridge(player, createTileOutput(currentPosition));
        }
        return currentPosition;
    }

    private int handleGooseTiles(String player, DiceRoll roll, int currentPosition) {
        while (isGooseTile(currentPosition)) {
            currentPosition += roll.sum();
            output.moveAgain(player, createTileOutput(currentPosition));
        }
        return currentPosition;
    }

    private int handleOvershoot(String player, int currentPosition) {
        if (currentPosition > GOAL) {
            int overshoot = currentPosition - GOAL;
            currentPosition -= overshoot * 2;
            output.bounce(player, createTileOutput(currentPosition));
        }
        return currentPosition;
    }

    private void handleGoal(String player, int currentPosition) {
        if (currentPosition == GOAL) {
            output.win(player);
        }
    }

    public int getPosition(String player) {
        int playerId = getPlayerId(player);
        return positions.get(playerId);
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
        position = Math.min(position, GOAL);
        if (isStartTile(position))
            return TileOutput.start();
        if (isBridgeTile(position))
            return TileOutput.bridge(position);
        if (isGooseTile(position))
            return TileOutput.goose(position);
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