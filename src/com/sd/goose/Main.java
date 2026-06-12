package com.sd.goose;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.sd.goose.game.GooseGame;
import com.sd.goose.io.GooseController;
import com.sd.goose.io.GoosePresenter;

public class Main {
    
    public static void main(String[] args) throws IOException {
        if (args.length > 0 && args[0].equals("test")) {
            new Test().test();
        } else {
            runGame();
        }
    }

    private static void runGame() throws IOException {
        GoosePresenter presenter = new GoosePresenter();
        GooseGame game = new GooseGame(presenter);
        GooseController controller = new GooseController(game, presenter);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                String line = in.readLine();
                controller.processUserInput(line);
            }
        }
    }
}
