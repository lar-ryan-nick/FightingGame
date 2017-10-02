package com.tutorial.game.controllers;

import com.tutorial.game.characters.Character;

/**
 * Created by ryanl on 9/30/2017.
 */

public class Controller {

    protected Character player;

    public Controller() {
        super();
    }

    public Controller(Character character) {
        super();
        possessCharacter(character);
    }

    public void possessCharacter(Character character) {
        player = character;
        player.setController(this);
    }
}
