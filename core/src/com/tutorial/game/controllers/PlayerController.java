package com.tutorial.game.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.tutorial.game.characters.Character;

/**
 * Created by ryanl on 8/9/2017.
 */

public class PlayerController extends InputListener {

    private Character player;

    public PlayerController() {
        super();
    }

    public PlayerController(Character character) {
        super();
        possessCharacter(character);
    }

    public void possessCharacter(Character character) {
        player = character;
        player.setIsPossessed(true);
    }

    @Override
    public boolean handle(Event e) {
        return super.handle(e);
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        return super.touchDown(event, x, y, pointer, button);
    }

    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        super.touchUp(event, x, y, pointer, button);
    }

    @Override
    public void touchDragged(InputEvent event, float x, float y, int pointer) {
        super.touchDragged(event, x, y, pointer);
    }

    @Override
    public boolean mouseMoved(InputEvent event, float x, float y) {
        return super.mouseMoved(event, x, y);
    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        super.enter(event, x, y, pointer, fromActor);
    }

    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        super.exit(event, x, y, pointer, toActor);
    }

    @Override
    public boolean scrolled(InputEvent event, float x, float y, int amount) {
        return super.scrolled(event, x, y, amount);
    }

    @Override
    public boolean keyDown(InputEvent event, int keycode) {
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
                return super.keyDown(event, keycode);
        }
    }

    @Override
    public boolean keyUp(InputEvent event, int keycode) {
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
                return super.keyUp(event, keycode);
        }
    }

    @Override
    public boolean keyTyped(InputEvent event, char character) {
        return super.keyTyped(event, character);
    }
}
