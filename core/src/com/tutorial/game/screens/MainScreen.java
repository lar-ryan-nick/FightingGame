package com.tutorial.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by ryanl on 8/5/2017.
 */

public class MainScreen implements Screen {

    /*
    private SpriteBatch window;
    private Sprite screen;
    */
    private Stage stage;
    private Image backgroundImage;
    private BitmapFont impactWhite48;
    private Label title;
    private TextButton startLocalGame;
    private TextButton startOnlineGame;

    @Override
    public void show() {
        /*
        window = new SpriteBatch();
        screen = new Sprite(new Texture("img/MainBackgroundImage.jpg"));
        screen.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        */
        stage = new Stage();
        ((InputMultiplexer) Gdx.input.getInputProcessor()).addProcessor(stage);
        //TextureRegion textureRegion = new TextureRegion(, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        backgroundImage = new Image(new Texture("img/MainBackgroundImage.jpg"));
        backgroundImage.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(backgroundImage);
        impactWhite48 = new BitmapFont(Gdx.files.internal("font/ImpactWhite48.fnt"), false);
        Label.LabelStyle labelStyle = new Label.LabelStyle(impactWhite48, Color.WHITE);
        title = new Label("Fighter", labelStyle);
        title.setBounds((Gdx.graphics.getWidth() - title.getPrefWidth()) / 2, Gdx.graphics.getHeight() - title.getPrefHeight(), title.getPrefWidth(), title.getPrefHeight());
        stage.addActor(title);
        startLocalGame = new TextButton("Start Local Game", new TextButton.TextButtonStyle(null, null, null, impactWhite48));
        startLocalGame.setBounds(0, 0, startLocalGame.getPrefWidth(), startLocalGame.getPrefHeight());
        startLocalGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                LocalGameScreen localGameScreen = new LocalGameScreen();
                ((Game) Gdx.app.getApplicationListener()).setScreen(localGameScreen);
                dispose();
            }
        });
        stage.addActor(startLocalGame);
        startOnlineGame = new TextButton("Start Online Game", new TextButton.TextButtonStyle(null, null, null, impactWhite48));
        startOnlineGame.setBounds(Gdx.graphics.getWidth() - startOnlineGame.getPrefWidth(), 0, startOnlineGame.getPrefWidth(), startOnlineGame.getPrefHeight());
        startOnlineGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                OnlineGameScreen onlineGameScreen = new OnlineGameScreen();
                ((Game) Gdx.app.getApplicationListener()).setScreen(onlineGameScreen);
                dispose();
            }
        });
        stage.addActor(startOnlineGame);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
        stage.getCamera().update();
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
        ((InputMultiplexer) Gdx.input.getInputProcessor()).removeProcessor(stage);
        stage.dispose();
        impactWhite48.dispose();
    }
}
