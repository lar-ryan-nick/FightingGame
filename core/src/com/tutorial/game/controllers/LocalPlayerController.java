package com.tutorial.game.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.ColorAction;
import com.badlogic.gdx.utils.Disposable;
import com.tutorial.game.characters.Character;

/**
 * Created by ryanl on 8/9/2017.
 */

public class LocalPlayerController extends Controller implements PlayerController {

    public LocalPlayerController() {
        super();
    }

    public LocalPlayerController(Character character) {
        super();
        possessCharacter(character);
        ((InputMultiplexer) Gdx.input.getInputProcessor()).addProcessor(this);
    }

    @Override
    public void possessCharacter(Character character) {
        super.possessCharacter(character);
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.SPACE:
                player.jump();
                return true;
            case Input.Keys.UP:
                player.jump();
                return true;
            case Input.Keys.RIGHT:
                player.setIsMovingRight(true);
                return true;
            case Input.Keys.LEFT:
                player.setIsMovingLeft(true);
                return true;
            case Input.Keys.DOWN:
                player.setIsCrouching(true);
                return true;
            case Input.Keys.W:
                player.jump();
                return true;
            case Input.Keys.A:
                player.setIsMovingLeft(true);
                return true;
            case Input.Keys.D:
                player.setIsMovingRight(true);
                return true;
            case Input.Keys.S:
                player.setIsCrouching(true);
                return true;
            case Input.Keys.C:
                player.jab();
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.RIGHT:
                player.setIsMovingRight(false);
                return true;
            case Input.Keys.LEFT:
                player.setIsMovingLeft(false);
                return true;
            case Input.Keys.DOWN:
                player.setIsCrouching(false);
                return true;
            case Input.Keys.D:
                player.setIsMovingRight(false);
                return true;
            case Input.Keys.A:
                player.setIsMovingLeft(false);
                return true;
            case Input.Keys.S:
                player.setIsCrouching(false);
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

    @Override
    public void dispose() {
        ((InputMultiplexer) Gdx.input.getInputProcessor()).removeProcessor(this);
    }
}
