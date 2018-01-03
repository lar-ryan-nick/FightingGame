package com.tutorial.game.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

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
		title.setBounds((Gdx.graphics.getWidth() - title.getPrefWidth()) / 2, 4 * Gdx.graphics.getHeight() / 5 - title.getPrefHeight(), title.getPrefWidth(), title.getPrefHeight());
		stage.addActor(title);
    }

    public void setCount(int count) {
        if (count == 0) {
            title.setText("Fight!");
        } else {
            title.setText(Integer.toString(count));
        }
    }
}
