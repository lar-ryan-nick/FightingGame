package com.tutorial.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

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
    private TextButton startGame;

    @Override
    public void show() {
        /*
        window = new SpriteBatch();
        screen = new Sprite(new Texture("img/MainBackgroundImage.jpg"));
        screen.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        */
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        //TextureRegion textureRegion = new TextureRegion(, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        backgroundImage = new Image(new Texture("img/MainBackgroundImage.jpg"));
        backgroundImage.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(backgroundImage);
        impactWhite48 = new BitmapFont(Gdx.files.internal("font/ImpactWhite48.fnt"), false);
        Label.LabelStyle labelStyle = new Label.LabelStyle(impactWhite48, Color.WHITE);
        title = new Label("Fighter", labelStyle);
        title.setBounds((Gdx.graphics.getWidth() - title.getPrefWidth()) / 2, Gdx.graphics.getHeight() - title.getPrefHeight(), title.getPrefWidth(), title.getPrefHeight());
        stage.addActor(title);
        startGame = new TextButton("Start Game", new TextButton.TextButtonStyle(null, null, null, impactWhite48));
        startGame.setBounds(0, 0, startGame.getPrefWidth(), startGame.getPrefHeight());
        startGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameScreen gameScreen = new GameScreen(4);
                ((Game) Gdx.app.getApplicationListener()).setScreen(gameScreen);
            }
        });
        stage.addActor(startGame);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //window.begin();
        stage.act(delta);
        stage.draw();
        //screen.draw(window);
        //window.end();
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

    }

    @Override
    public void dispose() {
        /*
        window.dispose();
        screen.getTexture().dispose();
        */
        stage.dispose();
        impactWhite48.dispose();
    }
}
