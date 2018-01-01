package com.tutorial.game.controllers;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.tutorial.game.characters.Character;

import static com.tutorial.game.constants.Constants.CHARACTER_SCALE;

/**
 * Created by ryanwiener on 10/4/17.
 */

public class AIController extends Controller {

    public AIController() {
        super();
    }

    public AIController(Character character) {
        super(character);
    }

    public void act() {
        if (!player.getIsDead()) {
            World world = player.getCharacterBody().getWorld();
            Array<Body> bodies = new Array<Body>(world.getBodyCount());
            world.getBodies(bodies);
            for (Body body : bodies) {
                if (body.getUserData() instanceof Character) {
                    Character character = ((Character) body.getUserData());
                    if (character.getController() instanceof LocalPlayerController) {
                        if (!character.getIsDead()) {
                            if (character.getX() > player.getX()) {
                                player.setFlip(false, false);
                                if (character.getX() - player.getX() < 16 * CHARACTER_SCALE + player.getWidth() && Math.abs(character.getY() - player.getY()) <= player.getHeight()) {
                                    if (Math.random() * 100 < 5) {
                                        player.jab();
                                    }
                                    player.setIsMovingRight(false);
                                } else {
                                    player.setIsMovingRight(true);
                                }
                            } else {
                                player.setFlip(true, false);
                                if (player.getX() - character.getX() < 16 * CHARACTER_SCALE + player.getWidth() && Math.abs(character.getY() - player.getY()) <= player.getHeight()) {
                                    if (Math.random() * 100 < 5) {
                                        player.jab();
                                    }
                                    player.setIsMovingLeft(false);
                                } else {
                                    player.setIsMovingLeft(true);
                                }
                            }
                            if (character.getY() > player.getY() + player.getHeight() && body.getLinearVelocity().y > 0) {
                                /*
                                // because jump force will be overriden by act before it has a chance to do anything
                                if (!player.getIsInAir()) {
                                    player.getCharacterBody().applyForceToCenter(new Vector2(0, 1000f * player.getCharacterBody().getMass()), true);
                                }
                                */
                                player.jump();
                            }
                        } else {
                            player.setIsMovingRight(false);
                            player.setIsMovingLeft(false);
                        }
                        break;
                    }
                }
            }
        }
    }
}
