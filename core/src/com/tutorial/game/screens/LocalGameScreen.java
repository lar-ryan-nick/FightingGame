package com.tutorial.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import com.tutorial.game.controllers.AIController;
import com.tutorial.game.controllers.Controller;
import com.tutorial.game.controllers.PlayerController;
import com.tutorial.game.maps.DefaultMap;
import com.tutorial.game.maps.Map;

import static com.tutorial.game.constants.Constants.CATEGORY_SCENERY;
import static com.tutorial.game.constants.Constants.WORLD_HEIGHT;
import static com.tutorial.game.constants.Constants.WORLD_WIDTH;

/**
 * Created by ryanl on 8/6/2017.
 */

public class LocalGameScreen implements Screen {

    private Map map;
    private int numEnemies;
    private SpriteBatch batch;
    /*
    private ClientCharacter player;
    private Array<ClientCharacter> enemies;
    */

    public LocalGameScreen() {
        super();
        numEnemies = 1;
    }

    public LocalGameScreen(int enemies) {
        super();
        numEnemies = enemies;
    }

    @Override
    public void show() {
        map = new DefaultMap();
        batch = new SpriteBatch();
        ClientCharacter player = new ClientCharacter(map.getWorld());
        player.setPosition(player.getWidth(), 0);
        map.addCharacter(player);
        PlayerController playerController = new PlayerController(player);
        Gdx.input.setInputProcessor(playerController);
        //enemies = new Array<ClientCharacter>(numEnemies);
        for (int i = 0; i < numEnemies; i++) {
            ClientCharacter enemy = new ClientCharacter(map.getWorld());
            AIController enemyController = new AIController(enemy);
            enemy.setPosition((float)((i + 1) * WORLD_WIDTH / (numEnemies + 1)), 0);
            map.addCharacter(enemy);
            //enemies.add(enemy);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        map.act(delta);
        batch.setProjectionMatrix(map.getCamera().combined);
        batch.begin();
        map.draw(batch);
        batch.end();
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
        map.dispose();
    }
}
