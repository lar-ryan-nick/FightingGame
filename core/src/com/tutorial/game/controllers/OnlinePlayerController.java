package com.tutorial.game.controllers;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.tutorial.game.characters.Character;

import java.io.PrintWriter;
import java.util.UUID;

/**
 * Created by ryanl on 9/30/2017.
 */

public class OnlinePlayerController extends NetworkController implements InputProcessor {

    private PrintWriter out;

    public OnlinePlayerController(Character character, PrintWriter o, UUID uuid) {
        super(character, uuid);
        out = o;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.SPACE:
                //player.jump();
                out.println("1");
                return true;
            case Input.Keys.UP:
                //player.jump();
                out.println("1");
                return true;
            case Input.Keys.RIGHT:
                //player.setIsMovingRight(true);
                out.println("2");
                return true;
            case Input.Keys.LEFT:
                //player.setIsMovingLeft(true);
                out.println("4");
                return true;
            case Input.Keys.DOWN:
                //player.setIsCrouching(true);
                out.println("6");
                return true;
            case Input.Keys.W:
                //player.jump();
                out.println("1");
                return true;
            case Input.Keys.A:
                //player.setIsMovingLeft(true);
                out.println("4");
                return true;
            case Input.Keys.D:
                //player.setIsMovingRight(true);
                out.println("2");
                return true;
            case Input.Keys.S:
                //player.setIsCrouching(true);
                out.println("6");
                return true;
            case Input.Keys.C:
                //player.jab();
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
                //player.setIsMovingRight(false);
                out.println("3");
                return true;
            case Input.Keys.LEFT:
                //player.setIsMovingLeft(false);
                out.println("5");
                return true;
            case Input.Keys.DOWN:
                //player.setIsCrouching(false);
                out.println("7");
                return true;
            case Input.Keys.D:
                //player.setIsMovingRight(false);
                out.println("3");
                return true;
            case Input.Keys.A:
                //player.setIsMovingLeft(false);
                out.println("5");
                return true;
            case Input.Keys.S:
                //player.setIsCrouching(false);
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
