package com.tutorial.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tutorial.game.constants.GameState;
import com.tutorial.game.maps.DefaultMap;
import com.tutorial.game.maps.Map;
import com.tutorial.game.overlays.CountDownOverlay;
import com.tutorial.game.overlays.GameOverOverlay;
import com.tutorial.game.overlays.Overlay;
import com.tutorial.game.overlays.WaitingOverlay;

/**
 * Created by ryanwiener on 1/1/18.
 */

public class GameScreen implements Screen {

    private Map map;
	private Batch batch;
	private Overlay overlay;

	public Map getMap() {
	    return map;
    }

	@Override
	public void show() {
		map = new DefaultMap();
		batch = new SpriteBatch();
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
        batch.end();
        if (map.getGameState() == GameState.COUNTING) {
            if (!(overlay instanceof CountDownOverlay)) {
                overlay = new CountDownOverlay();
            }
            ((CountDownOverlay) overlay).setCount(map.getCount());
            overlay.draw();
        } else if (map.getGameState() == GameState.WAITING) {
            if (!(overlay instanceof WaitingOverlay)) {
                overlay = new WaitingOverlay();
            }
            overlay.draw();
        } else if (map.getGameState() == GameState.WON || map.getGameState() == GameState.LOST) {
		    if (!(overlay instanceof GameOverOverlay)) {
		        overlay = new GameOverOverlay(map.getGameState() == GameState.WON);
            }
			overlay.draw();
		}
	}

	@Override
	public void resize(int width, int height) {
        if (overlay != null) {
            overlay.resize(width, height);
        }
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
		if (overlay != null) {
		    overlay.dispose();
        }
	}
}
