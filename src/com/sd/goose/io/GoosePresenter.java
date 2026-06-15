package com.sd.goose.io;

import java.io.PrintStream;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import com.sd.goose.game.OutputBoundary;
import com.sd.goose.game.TileType;

public class GoosePresenter implements OutputBoundary {

    private StringJoiner currentLine = new StringJoiner(". ");
    private final PrintStream out;

    public GoosePresenter() {
        this(System.out);
    }

    public GoosePresenter(PrintStream out) {
        this.out = out;
    }

    public void invalidName(String name) {
        out.println("Unkown player: " + name);
    }

    @Override
    public void finalizeMove() {
        out.println(currentLine.toString());
        currentLine = new StringJoiner(". ");
    }

    @Override
    public void players(List<String> players) {
        out.println("players: " + players.stream().collect(Collectors.joining(", ")));
    }

    @Override
    public void roll(String player, int first, int second) {
        currentLine.add(player + " rolls " + first + ", " + second);
    }

    @Override
    public void move(String player, Tile origin, Tile target) {
        currentLine.add(player + " moves from " + printTile(origin) + " to " + printTile(target));
    }

    private String printTile(Tile tile) {
        return switch (tile.type()) {
            case TileType.Bridge -> "The Bridge";
            case TileType.Goose -> tile.position() + ", The Goose";
            case TileType.Normal -> ""+ tile.position();
            case TileType.Start -> "Start";
        };
    }

    @Override
    public void moveAgain(String player, Tile target) {
        currentLine.add(player + " moves again to " + printTile(target));
    }

    @Override
    public void bounce(String player, Tile target) {
        currentLine.add(player + " bounces! " + player + " returns to " + printTile(target));
    }

    @Override
    public void bridge(String player, Tile target) {
        currentLine.add(player + " jumps to " + printTile(target));
    }

    @Override
    public void win(String player) {
        currentLine.add(player + " Wins!!");
    }

}
