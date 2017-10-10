package com.tutorial.game;

import com.badlogic.gdx.Game;
import com.tutorial.game.screens.MainScreen;

public class TutorialGame extends Game {
	
	@Override
	public void create() {
		setScreen(new MainScreen());
	}

	@Override
	public void render() {
		super.render();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		getScreen().dispose();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}
}
