package com.tutorial.game.maps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;

import static com.tutorial.game.constants.Constants.WORLD_HEIGHT;
import static com.tutorial.game.constants.Constants.WORLD_WIDTH;

/**
 * Created by ryanwiener on 10/11/17.
 */

public abstract class Map extends Stage {

    private World world;
    private Box2DDebugRenderer renderer;
    private OrthographicCamera camera;

    Map() {
        world = new World(new Vector2(0, -200f), true);
        renderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        OrthographicCamera cam = (OrthographicCamera) this.getCamera();
        cam.zoom = WORLD_WIDTH / cam.viewportWidth;
        camera.zoom = WORLD_WIDTH / cam.viewportWidth;
        cam.position.x = WORLD_WIDTH / 2;
        cam.position.y = WORLD_HEIGHT / 2;
        camera.position.x = WORLD_WIDTH / 2;
        camera.position.y = WORLD_HEIGHT / 2;
        camera.update();
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void act(float delta) {
        world.step(1 / 60f, 8, 3);
        super.act(delta);
    }

    @Override
    public void draw() {
        renderer.render(world, camera.combined);
        super.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        renderer.dispose();
        world.dispose();
    }
}
