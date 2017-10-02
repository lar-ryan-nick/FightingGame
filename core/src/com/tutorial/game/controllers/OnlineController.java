package com.tutorial.game.controllers;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.tutorial.game.characters.Character;

import java.io.PrintWriter;
import java.util.UUID;

/**
 * Created by ryanwiener on 9/27/17.
 */

/*
1 = jump
2 = move right
3 = stop moving right
4 = move left
5 = stop moving left
6 = crouch
7 = stop crouching
8 = jab
 */

public class OnlineController extends Controller {

    private UUID id;

    public OnlineController(Character character, UUID uuid) {
        super(character);
        id = uuid;
    }

    public UUID getUUID() {
        return id;
    }
}
