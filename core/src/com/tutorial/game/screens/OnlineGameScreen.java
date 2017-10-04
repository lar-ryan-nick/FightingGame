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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.tutorial.game.characters.Character;
import com.tutorial.game.characters.ClientCharacter;
import com.tutorial.game.controllers.NetworkController;
import com.tutorial.game.controllers.OnlinePlayerController;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.UUID;

import static com.tutorial.game.constants.Constants.CATEGORY_SCENERY;
import static com.tutorial.game.constants.Constants.WORLD_HEIGHT;
import static com.tutorial.game.constants.Constants.WORLD_WIDTH;

/**
 * Created by ryanwiener on 9/27/17.
 */

public class OnlineGameScreen implements Screen {

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
        String id = null;
        try {
            id = in.readLine();
        } catch (IOException e) {
            System.err.println("Couldn't read id");
            System.exit(-1);
        }
        ClientCharacter player = new ClientCharacter(world);
        player.setPosition(player.getWidth(), 0);
        stage.addActor(player);
        OnlinePlayerController controller = new OnlinePlayerController(player, out, UUID.fromString(id));
        Gdx.input.setInputProcessor(controller);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        try {
            String line = null;
            while (in.ready()) {
                line = in.readLine();
                Gdx.app.log("Received", line);
            }
            if (line != null) {
                HashMap<String, String> params = parseString(line);
                if (params != null) {
                    for (int i = 0; i < Integer.parseInt(params.get("numPlayers")); ++i) {
                        Array<Actor> actors = stage.getActors();
                        boolean found = false;
                        for (int j = 0; j < actors.size; ++j) {
                            if (actors.get(j) instanceof Character && ((Character) actors.get(j)).getController() instanceof NetworkController) {
                                if (((NetworkController) ((Character) actors.get(j)).getController()).getUUID().equals(UUID.fromString(params.get("uuid" + i)))) {
                                    ((Character) actors.get(j)).updateFromMap(params, i);
                                    found = true;
                                    break;
                                }
                            }
                        }
                        if (!found) {
                            ClientCharacter player = new ClientCharacter(world);
                            NetworkController controller = new NetworkController(player, UUID.fromString(params.get("uuid" + i)));
                            player.setPosition(player.getWidth(), 0);
                            stage.addActor(player);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Can't read");
            System.exit(1);
        }
        world.step(1 / 60f, 8, 3);
        renderer.render(world, camera.combined);
        stage.act(delta);
        stage.draw();
    }

    public HashMap<String, String> parseString(String s) {
        HashMap<String, String> result = new HashMap<String, String>();
        String[] params = s.split("&");
        for (int i = 0; i < params.length; ++i) {
            String[] keyVal = params[i].split("=");
            if (keyVal.length > 1) {
                result.put(keyVal[0], keyVal[1]);
            }
        }
        if (result.isEmpty()) {
            return null;
        }
        return result;
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
