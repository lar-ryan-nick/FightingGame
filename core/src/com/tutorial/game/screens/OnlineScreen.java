package com.tutorial.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.tutorial.game.controllers.OnlineController;
import com.tutorial.game.controllers.PlayerController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by ryanwiener on 9/27/17.
 */

public class OnlineScreen implements Screen {

    public static final short CATEGORY_SCENERY = 0x0004;
    private final float WORLD_WIDTH = 100;
    private final float WORLD_HEIGHT = 100 * 9 / 16;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private World world;
    private Box2DDebugRenderer renderer;
    private OrthographicCamera camera;
    private Stage stage;

    public void listenServer() {
        try{
            socket = new Socket("localhost", 8000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
            System.out.println("Unknown host: localhost");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Can't read");
            System.exit(1);
        }
    }

    @Override
    public void show() {
        listenServer();
        world = new World(new Vector2(0, -200f), true);
        renderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        //batch = new SpriteBatch();
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        OrthographicCamera cam = (OrthographicCamera)stage.getCamera();
        cam.zoom -= WORLD_WIDTH / cam.viewportWidth;
        camera.zoom -= WORLD_WIDTH / cam.viewportWidth;
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
        OnlineController controller = new OnlineController(out);
        Gdx.input.setInputProcessor(controller);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        try {
            if (in.ready()) {
                String line = in.readLine();
                Gdx.app.log("Received", line);
            }
        } catch (IOException e) {
            System.err.println("Can't read");
            System.exit(1);
        }
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

    }

    @Override
    public void dispose() {
        try {
            out.println("disconnecting");
            socket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            System.err.println("Couldn't close connection");
            System.exit(1);
        }
    }
}
