package com.sd.goose.game;

import java.util.Random;

public record DiceRoll (int first, int second) {

    private final static int MINIMUM_ROLL_INCLUSIVE = 1;
    private final static int MAXIMUM_ROLL_EXCLUSIVE = 7;

    static DiceRoll roll() {
        Random r = new Random();
        return new DiceRoll(rollSingleDie(r), rollSingleDie(r));
    }

    static int rollSingleDie(Random random) {
        return random.nextInt(MINIMUM_ROLL_INCLUSIVE, MAXIMUM_ROLL_EXCLUSIVE);
    }

    int sum() {
        return first() + second();
    }

}