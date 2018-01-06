package com.tutorial.game.overlays;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.tutorial.game.screens.GameScreen;

/**
 * Created by ryanwiener on 1/1/18.
 */

public class CountDownOverlay extends Overlay {

    private Label title;

    public CountDownOverlay() {
        super();
        BitmapFont impactWhite48 = new BitmapFont(Gdx.files.internal("font/ImpactWhite48.fnt"), false);
		Label.LabelStyle labelStyle = new Label.LabelStyle(impactWhite48, Color.WHITE);
		CharSequence titleText = "";
		title = new Label(titleText, labelStyle);
		title.setBounds(0, 4 * Gdx.graphics.getHeight() / 5 - title.getPrefHeight(), Gdx.graphics.getWidth(), title.getPrefHeight());
		title.setAlignment(Align.center);
		stage.addActor(title);
    }

    public void setCount(int count) {
        if (count == 0) {
            title.setText("Fight!");
        } else {
            title.setText(Integer.toString(count));
        }
    }

    @Override
    public void draw() {
        // didn't want to put in Game Screen class since I would need an instance of
        setCount(((GameScreen) ((Game) Gdx.app.getApplicationListener()).getScreen()).getMap().getCount());
        super.draw();
    }
}
