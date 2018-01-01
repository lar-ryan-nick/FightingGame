package com.tutorial.game.overlays;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.tutorial.game.screens.MainScreen;

/**
 * Created by ryanwiener on 1/1/18.
 */

public class GameOverOverlay implements Disposable {

	private boolean won;
	private Stage stage;

	public GameOverOverlay(boolean w) {
		won = w;
		stage = new Stage();
		BitmapFont impactWhite48 = new BitmapFont(Gdx.files.internal("font/ImpactWhite48.fnt"), false);
		Label.LabelStyle labelStyle = new Label.LabelStyle(impactWhite48, Color.WHITE);
		CharSequence titleText;
		if (won) {
			titleText = "Congratulations! You Won!";
		} else {
			titleText = "You Lose";
		}
		Label title = new Label(titleText, labelStyle);
		title.setBounds((Gdx.graphics.getWidth() - title.getPrefWidth()) / 2, 4 * Gdx.graphics.getHeight() / 5 - title.getPrefHeight(), title.getPrefWidth(), title.getPrefHeight());
		stage.addActor(title);
		TextButton textButton = new TextButton("Return to Main Menu", new TextButton.TextButtonStyle(null, null, null, impactWhite48));
		textButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
	    		MainScreen mainScreen = new MainScreen();
			    ((Game) Gdx.app.getApplicationListener()).getScreen().dispose();
                ((Game) Gdx.app.getApplicationListener()).setScreen(mainScreen);
			}
		});
		textButton.setBounds((Gdx.graphics.getWidth() - textButton.getPrefWidth()) / 2, title.getY() - title.getHeight() - 100, textButton.getPrefWidth(), textButton.getPrefHeight());
		stage.addActor(textButton);
        ((InputMultiplexer) Gdx.input.getInputProcessor()).addProcessor(stage);
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
        ((InputMultiplexer) Gdx.input.getInputProcessor()).removeProcessor(stage);
    }
}
