package com.tutorial.game.controllers;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

import java.io.PrintWriter;

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

public class OnlineController implements InputProcessor {

    private PrintWriter out;

    public OnlineController(PrintWriter o) {
        out = o;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.SPACE:
                out.println("1");
                return true;
            case Input.Keys.UP:
                out.println("1");
                return true;
            case Input.Keys.RIGHT:
                out.println("2");
                return true;
            case Input.Keys.LEFT:
                out.println("4");
                return true;
            case Input.Keys.DOWN:
                out.println("6");
                return true;
            case Input.Keys.W:
                out.println("1");
                return true;
            case Input.Keys.A:
                out.println("4");
                return true;
            case Input.Keys.D:
                out.println("2");
                return true;
            case Input.Keys.S:
                out.println("6");
                return true;
            case Input.Keys.C:
                out.println("8");
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.RIGHT:
                out.println("3");
                return true;
            case Input.Keys.LEFT:
                out.println("5");
                return true;
            case Input.Keys.DOWN:
                out.println("7");
                return true;
            case Input.Keys.D:
                out.println("3");
                return true;
            case Input.Keys.A:
                out.println("5");
                return true;
            case Input.Keys.S:
                out.println("7");
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
