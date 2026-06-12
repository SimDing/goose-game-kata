package com.sd.goose;

public interface InputBoundary {

    void addPlayer(String name);

    void rollAndMove(String player);

    void move(String player, DiceRoll roll);

}