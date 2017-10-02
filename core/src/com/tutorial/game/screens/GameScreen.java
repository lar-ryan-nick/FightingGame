package com.tutorial.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
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
import com.tutorial.game.controllers.PlayerController;

/**
 * Created by ryanl on 8/6/2017.
 */

public class GameScreen implements Screen {

    public static final short CATEGORY_SCENERY = 0x0004;
    private final float WORLD_WIDTH = 100;
    private final float WORLD_HEIGHT = 100 * 9 / 16;
    private int numEnemies;
    private World world;
    private Box2DDebugRenderer renderer;
    private OrthographicCamera camera;
    private Stage stage;
    private Image backgroundImage;
    private Image floorImage;
    private ClientCharacter player;
    private Array<ClientCharacter> enemies;

    public GameScreen() {
        super();
        numEnemies = 1;
    }

    public GameScreen(int enemies) {
        super();
        numEnemies = enemies;
    }

    @Override
    public void show() {
        world = new World(new Vector2(0, -200f), true);
        renderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        //batch = new SpriteBatch();
        stage = new Stage();
        OrthographicCamera cam = (OrthographicCamera)stage.getCamera();
        cam.zoom = WORLD_WIDTH / cam.viewportWidth;
        camera.zoom = WORLD_WIDTH / cam.viewportWidth;
        cam.position.x = WORLD_WIDTH / 2;
        cam.position.y = WORLD_HEIGHT / 2;
        camera.position.x = WORLD_WIDTH / 2;
        camera.position.y = WORLD_HEIGHT / 2;
        camera.update();
        BodyDef floor = new BodyDef();
        floor.type = BodyDef.BodyType.StaticBody;
        floor.position.set(0, 0);
        EdgeShape line = new EdgeShape();
        line.set(0, 0, WORLD_WIDTH,  0);
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
        backgroundImage = new Image(new Texture("img/GameBackgroundImage.jpg"));
        backgroundImage.setBounds(0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        //stage.addActor(backgroundImage);
        /*
        floorImage = new Image(new Texture("img/GameFloorImage.jpg"));
        floorImage.setBounds(0, 0, effectiveViewportWidth, 0);
        stage.addActor(floorImage);
        */
        player = new ClientCharacter(world);
        player.setPosition(player.getWidth(), 0);
        stage.addActor(player);
        PlayerController playerController = new PlayerController(player);
        Gdx.input.setInputProcessor(playerController);
        enemies = new Array<ClientCharacter>(numEnemies);
        for (int i = 0; i < numEnemies; i++) {
            ClientCharacter enemy = new ClientCharacter(world);
            enemy.setPosition((float)((i + 1) * WORLD_WIDTH / (numEnemies + 1)), 0);
            stage.addActor(enemy);
            enemies.add(enemy);
        }
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

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        world.step(delta, 8, 3);
        renderer.render(world, camera.combined);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        //dispose();
    }

    @Override
    public void dispose() {
        world.dispose();
        player.dispose();
        for (ClientCharacter enemy : enemies) {
            enemy.dispose();
        }
        renderer.dispose();
    }
}
