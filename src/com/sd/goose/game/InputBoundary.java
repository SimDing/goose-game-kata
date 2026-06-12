package com.sd.goose.game;

public interface InputBoundary {

    void addPlayer(String name);

    void rollAndMove(String player);

    void move(String player, DiceRoll roll);

}