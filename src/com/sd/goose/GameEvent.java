package com.sd.goose;

import java.util.List;

import com.sd.goose.GooseGame.DiceRoll;

public record GameEvent(GameEventType type, List<Printable> arguments) {

    public static GameEvent win(String winner) {
        return new GameEvent(GameEventType.Win, List.of(new PlayerOutput(winner)));
    }

    public static GameEvent bounce(String player, TileOutput bounceTo) {
        return new GameEvent(GameEventType.Bounce, List.of(new PlayerOutput(player), bounceTo)); 
    }

    public static GameEvent bridge(String player) {
        return new GameEvent(GameEventType.BridgeJump, List.of(new PlayerOutput(player), TileOutput.normal(GooseGame.BRIDGE_END)));
    }

    public static GameEvent move(String player, TileOutput target) {
        return new GameEvent(GameEventType.Move, List.of(new PlayerOutput(player), target));
    }

    public static GameEvent moveAgain(String player, TileOutput target) {
        return new GameEvent(GameEventType.MoveAgain, List.of(new PlayerOutput(player), target));
    }

    public static GameEvent roll(String player, DiceRoll roll) {
        throw new UnsupportedOperationException();
    }

    public enum GameEventType {
        Win, Bounce, Move, MoveAgain, BridgeJump, Roll
    }

    public sealed interface Printable permits Tile, Player, Roll {}

    public non-sealed interface Tile extends Printable {
        int getNumber();
        TileType getType();
    }

    public non-sealed interface Player extends Printable {
        String getName();
    }

    public non-sealed interface Roll extends Printable {
        DiceRoll getDiceRoll();
    }

    public enum TileType {
        Normal, Bridge, Goose;
    }

    static record PlayerOutput (String name) implements Player {
        @Override
        public String getName() {
            return name();
        }
    }

    static record TileOutput(int number, TileType type) implements Tile {
        @Override
        public int getNumber() {
            return number();
        }

        @Override
        public TileType getType() {
            return type();
        }

        static TileOutput normal(int number) {
            return new TileOutput(number, TileType.Normal);
        }

        static TileOutput bridge(int number) {
            return new TileOutput(number, TileType.Bridge);
        }

        static TileOutput goose(int number) {
            return new TileOutput(number, TileType.Goose);
        }
    }
}