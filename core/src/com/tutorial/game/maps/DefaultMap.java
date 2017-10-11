package com.tutorial.game.maps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.tutorial.game.characters.Character;
import com.tutorial.game.characters.ClientCharacter;
import com.tutorial.game.controllers.Controller;
import com.tutorial.game.controllers.PlayerController;

import static com.tutorial.game.constants.Constants.CATEGORY_SCENERY;
import static com.tutorial.game.constants.Constants.WORLD_HEIGHT;
import static com.tutorial.game.constants.Constants.WORLD_WIDTH;

/**
 * Created by ryanwiener on 10/11/17.
 */

public class DefaultMap extends Map {

    private Image backgroundImage;
    private Image floorImage;
    private Array<Character> characters;

    public DefaultMap() {
        super();
        //batch = new SpriteBatch();
        BodyDef floor = new BodyDef();
        floor.type = BodyDef.BodyType.StaticBody;
        floor.position.set(0, 0);
        EdgeShape line = new EdgeShape();
        line.set(0, 0, WORLD_WIDTH, 0);
        FixtureDef floorFixture = new FixtureDef();
        floorFixture.friction = 1f;
        floorFixture.shape = line;
        floorFixture.filter.categoryBits = CATEGORY_SCENERY;
        floorFixture.filter.maskBits = -1;
        getWorld().createBody(floor).createFixture(floorFixture);
        BodyDef leftWall = new BodyDef();
        leftWall.type = BodyDef.BodyType.StaticBody;
        leftWall.position.set(0, 0);
        line.set(0, 0, 0, WORLD_HEIGHT);
        FixtureDef wallFixture = new FixtureDef();
        wallFixture.friction = 0f;
        wallFixture.shape = line;
        wallFixture.filter.categoryBits = CATEGORY_SCENERY;
        wallFixture.filter.maskBits = -1;
        getWorld().createBody(leftWall).createFixture(wallFixture);
        BodyDef rightWall = new BodyDef();
        rightWall.type = BodyDef.BodyType.StaticBody;
        rightWall.position.set(0, 0);
        line.set(WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT);
        getWorld().createBody(rightWall).createFixture(wallFixture);
        line.dispose();
        backgroundImage = new Image(new Texture("img/GameBackgroundImage.jpg"));
        backgroundImage.setBounds(0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        //stage.addActor(backgroundImage);
        /*
        floorImage = new Image(new Texture("img/GameFloorImage.jpg"));
        floorImage.setBounds(0, 0, effectiveViewportWidth, 0);
        stage.addActor(floorImage);
        */
        getWorld().setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                if (contact.getFixtureA().getUserData() != null && contact.getFixtureB().getUserData() != null) {
                    if (!contact.getFixtureA().getUserData().toString().equals("")) {
                        //Gdx.app.log("Collision", "There was a collision!");
                        Character attacker = (Character) contact.getFixtureA().getBody().getUserData();
                        Character defender = (Character) contact.getFixtureB().getBody().getUserData();
                        Gdx.app.log("Collision", "There was a punch!");
                        if (attacker.isFacingRight()) {
                            defender.setFlip(true, false);
                        } else {
                            defender.setFlip(false, false);
                        }
                        defender.flinch();
                        defender.changeHealth(-2);
                    } else if (!contact.getFixtureB().getUserData().toString().equals("")) {
                        Character attacker = (Character) contact.getFixtureB().getBody().getUserData();
                        Character defender = (Character) contact.getFixtureA().getBody().getUserData();
                        Gdx.app.log("Collision", "There was a punch!");
                        //check if null since there was an error
                        if (attacker != null && defender != null) {
                            if (attacker.isFacingRight()) {
                                defender.setFlip(true, false);
                            } else {
                                defender.setFlip(false, false);
                            }
                            defender.flinch();
                            defender.changeHealth(-2);
                        }
                    }
                }
            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
    }
}
