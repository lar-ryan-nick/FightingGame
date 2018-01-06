package com.tutorial.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tutorial.game.constants.Constants;
import com.tutorial.game.constants.GameState;
import com.tutorial.game.maps.DefaultMap;
import com.tutorial.game.maps.Map;
import com.tutorial.game.overlays.CountDownOverlay;
import com.tutorial.game.overlays.GameOverOverlay;
import com.tutorial.game.overlays.Overlay;
import com.tutorial.game.overlays.WaitingOverlay;

import java.util.HashMap;

/**
 * Created by ryanwiener on 1/1/18.
 */

public class GameScreen implements Screen {

    private Map map;
	private Batch batch;
	private java.util.Map<GameState, Overlay> overlays;
	final private Texture backgroundTexture;

	public GameScreen() {
	    super();
	    backgroundTexture = new Texture("img/GameFloorImage.jpg");
	    overlays = new HashMap<GameState, Overlay>();
	    overlays.put(GameState.WON, new GameOverOverlay(true));
        overlays.put(GameState.LOST, new GameOverOverlay(false));
        overlays.put(GameState.COUNTING, new CountDownOverlay());
        overlays.put(GameState.WAITING, new WaitingOverlay());
    }

	public Map getMap() {
	    return map;
    }

	@Override
	public void show() {
		map = new DefaultMap();
		batch = new SpriteBatch();
        //batch.setTransformMatrix(map.getCamera().view);
		batch.setProjectionMatrix(map.getCamera().combined);
	}

	@Override
	public void render(float delta) {
	    if (map.getGameState() == GameState.TERMINATED) {
            MainScreen mainMenu = new MainScreen();
            ((Game) Gdx.app.getApplicationListener()).setScreen(mainMenu);
	        dispose();
	        return;
        }
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		map.act(delta);
		batch.begin();
		map.draw(batch);
		if (Gdx.graphics.getWidth() / (float)Gdx.graphics.getHeight() < 16f / 9) {
		    float height = (float)Gdx.graphics.getHeight() / Gdx.graphics.getWidth() * Constants.WORLD_WIDTH;
		    float margin = (height - Constants.WORLD_HEIGHT) / 2;
            batch.draw(backgroundTexture, 0, -margin, Constants.WORLD_WIDTH, margin);
            batch.draw(backgroundTexture, 0, Constants.WORLD_HEIGHT, Constants.WORLD_WIDTH, margin);
        } else {
		    float width = (float)Gdx.graphics.getWidth() / Gdx.graphics.getHeight() * Constants.WORLD_HEIGHT;
		    float margin = (width - Constants.WORLD_WIDTH) / 2;
            batch.draw(backgroundTexture, -margin, 0, margin, Constants.WORLD_HEIGHT);
            batch.draw(backgroundTexture, Constants.WORLD_WIDTH, 0, margin, Constants.WORLD_HEIGHT);
        }
        batch.end();
		Overlay overlay = overlays.get(map.getGameState());
		if (overlay != null) {
            overlay.draw();
        }
	}

	@Override
	public void resize(int width, int height) {
	    for (Overlay overlay : overlays.values()) {
	        overlay.resize(width, height);
        }
        map.resize(width, height);
        //batch.setTransformMatrix(map.getCamera().view);
        batch.setProjectionMatrix(map.getCamera().combined);
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
		batch.dispose();
		for (Overlay overlay : overlays.values()) {
	        overlay.dispose();
        }
        backgroundTexture.dispose();
	}
}
