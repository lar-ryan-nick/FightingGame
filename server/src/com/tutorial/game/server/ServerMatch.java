package com.tutorial.game.server;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import com.badlogic.gdx.utils.Disposable;
import com.tutorial.game.characters.Character;

import java.util.UUID;

/**
 * Created by ryanl on 9/28/2017.
 */

public class ServerMatch implements Disposable {

    public static final short CATEGORY_SCENERY = 0x0004;
    private final float WORLD_WIDTH = 100;
    private final float WORLD_HEIGHT = 100 * 9 / 16;
    private World world;
    private Array<Character> players;
    private Array<ServerController> controllers;

    ServerMatch() {
        world = new World(new Vector2(0, -200f), true);
        players = new Array<Character>();
        controllers = new Array<ServerController>();
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
        world.createBody(floor).createFixture(floorFixture);
        BodyDef leftWall = new BodyDef();
        leftWall.type = BodyDef.BodyType.StaticBody;
        leftWall.position.set(0, 0);
        line.set(0, 0, 0, WORLD_HEIGHT);
        FixtureDef wallFixture = new FixtureDef();
        wallFixture.friction = 0f;
        wallFixture.shape = line;
        wallFixture.filter.categoryBits = CATEGORY_SCENERY;
        wallFixture.filter.maskBits = -1;
        world.createBody(leftWall).createFixture(wallFixture);
        BodyDef rightWall = new BodyDef();
        rightWall.type = BodyDef.BodyType.StaticBody;
        rightWall.position.set(0, 0);
        line.set(WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT);
        world.createBody(rightWall).createFixture(wallFixture);
        line.dispose();
        world.setContactListener(new ContactListener() {
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

    public void addPlayer(UUID id) {
        Character player = new Character(world);
        player.setPosition(0, 0);
        players.add(player);
        ServerController controller = new ServerController(player, id);
        controllers.add(controller);
    }

    public void sendInput(String input, UUID uuid) {
        for (int i = 0; i < controllers.size; ++i) {
            if (controllers.get(i).getUUID().equals(uuid)) {
                controllers.get(i).processInput(Integer.parseInt(input));
            }
        }
    }

    public void render() {
        world.step(1 / 60f, 8, 3);
        for (int i = 0; i < players.size; ++i) {
            players.get(i).act(Gdx.graphics.getDeltaTime());
        }
    }

    @Override
    public void dispose() {

    }

    @Override
    public String toString() {
        String serialization = "";
        for (int i = 0; i < players.size; ++i) {
            serialization += players.get(i).toString();
        }
        return serialization;
    }
}
