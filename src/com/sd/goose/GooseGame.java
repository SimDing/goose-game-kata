package com.sd.goose;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.sd.goose.GameEvent.Roll;
import com.sd.goose.GameEvent.TileOutput;

public class GooseGame {
    private static final int STARTING_TILE = 1;
    private static final int BRIDGE_START = 6;
    static final int BRIDGE_END = 12;
    private static final int GOAL = 63;
    private static final Set<Integer> GOOSE_TILES = new HashSet<>(List.of(5, 9, 14, 18, 23, 27));

    private List<String> players = new ArrayList<>();
    private List<Integer> positions = new ArrayList<>();

    public void addPlayer(String name) {
        players.add(name);
        positions.add(STARTING_TILE);
    }

    public List<GameEvent> rollAndMove(String player) {
        return move(player, DiceRoll.roll());
    }

    public List<GameEvent> move(String player, DiceRoll roll) {
        int index = getPlayerIndex(player);
        List<GameEvent> events = new ArrayList<>();
        int result = roll.sum() + positions.get(index);

        events.add(GameEvent.move(player, createTileOutput(result)));

        if (result == BRIDGE_START) {
            result = BRIDGE_END;
            events.add(GameEvent.bridge(player));
        }

        while (isGooseTile(result)) {
            result += roll.sum();
        }

        if (result > GOAL) {
            int overshoot = result - GOAL;
            result -= overshoot;
            events.add(GameEvent.bounce(player, createTileOutput(result)));
        }

        if (result == GOAL) {
            events.add(GameEvent.win(player));
        }

        positions.set(index, result);

        return events;
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

    private boolean isBridgeTile(int position) {
        return position == BRIDGE_START;
    }

    private boolean isGooseTile(int position) {
        return GOOSE_TILES.contains(position);
    }

    public record DiceRoll (int first, int second) implements Roll {

        static DiceRoll roll() {
            Random r = new Random();
            return new DiceRoll(rollSingleDie(r), rollSingleDie(r));
        }

        static int rollSingleDie(Random random) {
            return random.nextInt(1, 7);
        }

        int sum() {
            return first() + second();
        }

        @Override
        public DiceRoll getDiceRoll() {
            return this;
        }
    }
    
    private TileOutput createTileOutput(int position) {
        if (isBridgeTile(position)) return TileOutput.bridge(position);
        if (isGooseTile(position)) return TileOutput.goose(position);
        return TileOutput.normal(position);
    }

}