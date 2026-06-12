package com.sd.goose.io;

import java.util.Arrays;

import com.sd.goose.game.DiceRoll;
import com.sd.goose.game.InputBoundary;

public class GooseController {

    private static final String MOVE_PREFIX = "move ";
    private static final String ADD_PLAYER_PREFIX = "add player ";
    private final InputBoundary gameBoundary;
    private final GoosePresenter presenter;

    public GooseController(InputBoundary gameBoundary, GoosePresenter presenter) {
        this.gameBoundary = gameBoundary;
        this.presenter = presenter;
    }

    public void processUserInput(String line) {
        if (line.startsWith(ADD_PLAYER_PREFIX)) addPlayer(line.substring(ADD_PLAYER_PREFIX.length()));
        if (line.startsWith(MOVE_PREFIX)) move(line.substring(MOVE_PREFIX.length()));
    }

    private void addPlayer(String remainingLine) {
        if (remainingLine.contains(" ")) {
            // TODO
            throw new RuntimeException();
        }
        String name = remainingLine;
        gameBoundary.addPlayer(name);
    }

    private void move(String remainingLine) {
        int spaceIndex = remainingLine.indexOf(' ');
        if (spaceIndex < 0) {
            String name = remainingLine;
            try {
                gameBoundary.rollAndMove(name);
            } catch(IllegalArgumentException e) {
                presenter.invalidName(name);
            }
        } else {
            String name = remainingLine.substring(0, spaceIndex);
            String commaSeperatedRolls = remainingLine.substring(spaceIndex + 1);
            String[] rolls = commaSeperatedRolls.split(",");
            int[] parsedRolls = Arrays.asList(rolls).stream().map(String::trim).mapToInt(Integer::parseInt).toArray(); // TODO number format
            if (parsedRolls.length != 2) throw new RuntimeException();
            DiceRoll roll = new DiceRoll(parsedRolls[0], parsedRolls[1]); // TODO invalid numbers
            gameBoundary.move(name, roll); // todo invalid name
        }
    }
}
