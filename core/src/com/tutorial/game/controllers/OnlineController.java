package com.tutorial.game.controllers;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

import java.io.PrintWriter;

/**
 * Created by ryanwiener on 9/27/17.
 */

public class OnlineController implements InputProcessor {

    private PrintWriter out;

    public OnlineController(PrintWriter o) {
        out = o;
    }

    @Override
    public boolean keyDown(int keycode) {
        out.println((Input.Keys.toString(keycode) + " down"));
        return false;
        /*
        switch (keycode) {
            case Input.Keys.SPACE:
                out.println("Space down");
                return true;
            case Input.Keys.UP:
                out.println("Up down");
                return true;
            case Input.Keys.RIGHT:
                out.println("Right down");
                return true;
            case Input.Keys.LEFT:
                out.println("Left down");
                return true;
            case Input.Keys.DOWN:
                out.println("Down down");
                return true;
            case Input.Keys.W:
                out.println("W down");
                return true;
            case Input.Keys.A:
                out.println("A down");
                return true;
            case Input.Keys.D:
                out.println("D down");
                return true;
            case Input.Keys.S:
                out.println("S down");
                return true;
            case Input.Keys.C:
                out.println("C down");
                return true;
            default:
                return false;
        }
        */
    }

    @Override
    public boolean keyUp(int keycode) {
        out.println(Input.Keys.toString(keycode) + " up");
        return false;
        /*
        switch (keycode) {
            case Input.Keys.RIGHT:
                out.println("Right up");
                return true;
            case Input.Keys.LEFT:
                out.println("Left up");
                return true;
            case Input.Keys.DOWN:
                out.println("Down up");
                return true;
            case Input.Keys.D:
                out.println("D up");
                return true;
            case Input.Keys.A:
                out.println("A up");
                return true;
            case Input.Keys.S:
                out.println("S up");
                return true;
            default:
                return false;
        }
        */
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
