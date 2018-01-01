package com.tutorial.game.overlays;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by ryanwiener on 1/1/18.
 */

public abstract class Overlay implements Disposable {

    protected Stage stage;

    public Overlay() {
        stage = new Stage();
    }

	public void resize(int width, int height) {
        stage.getViewport().update(width, height);
        stage.getCamera().update();
    }

    public void draw() {
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
