package com.tutorial.game.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by ryanwiener on 1/1/18.
 */

public class WaitingOverlay extends Overlay {

    public WaitingOverlay() {
        super();
        BitmapFont impactWhite48 = new BitmapFont(Gdx.files.internal("font/ImpactWhite48.fnt"), false);
		Label.LabelStyle labelStyle = new Label.LabelStyle(impactWhite48, Color.WHITE);
		CharSequence titleText = "Waiting for an Opponent...";
		Label title = new Label(titleText, labelStyle);
		title.setBounds((Gdx.graphics.getWidth() - title.getPrefWidth()) / 2, 4 * Gdx.graphics.getHeight() / 5 - title.getPrefHeight(), title.getPrefWidth(), title.getPrefHeight());
		stage.addActor(title);
    }
}
