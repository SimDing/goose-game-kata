package com.sd.goose.game;

public interface InputBoundary {

    void addPlayer(String name);

    void rollAndExecuteTurn(String player);

    void executeTurn(String player, DiceRoll roll);

}