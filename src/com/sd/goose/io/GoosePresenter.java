package com.sd.goose.io;

import java.util.List;
import java.util.stream.Collectors;

import com.sd.goose.game.OutputBoundary;
import com.sd.goose.game.TileType;

public class GoosePresenter implements OutputBoundary {

    private StringBuilder currentLine = new StringBuilder();

    public void invalidName(String name) {
        System.out.println("Unkown player: " + name);
    }

    @Override
    public void finalizeMove() {
        System.out.println(currentLine.toString());
        currentLine = new StringBuilder();
    }

    private void addTerminalDot() {
        currentLine.append(". ");
    }

    @Override
    public void players(List<String> players) {
        System.out.println("players: " + players.stream().collect(Collectors.joining(", ")));
    }

    @Override
    public void roll(String player, int first, int second) {
        currentLine
                .append(player)
                .append(" rolls ")
                .append(first)
                .append(", ")
                .append(second);
    }

    @Override
    public void move(String player, Tile origin, Tile target) {
        addTerminalDot();
        currentLine.append(player)
                .append(" moves from ")
                .append(printTile(origin))
                .append(" to ")
                .append(printTile(target));
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
        addTerminalDot();
        currentLine.append(' ')
                .append(player)
                .append(" moves again to ")
                .append(printTile(target));
    }

    @Override
    public void bounce(String player, Tile target) {
        addTerminalDot();
        currentLine.append(player)
                .append(" bounces! ")
                .append(player)
                .append(" returns to ")
                .append(printTile(target));
    }

    @Override
    public void bridge(String player, Tile target) {
        addTerminalDot();
        currentLine.append(player)
                .append(" jumps to ")
                .append(printTile(target));
    }

    @Override
    public void win(String player) {
        addTerminalDot();
        currentLine.append(player)
                .append(" Wins!!");
    }

}
