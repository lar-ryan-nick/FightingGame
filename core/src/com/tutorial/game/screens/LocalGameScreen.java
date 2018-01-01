package com.tutorial.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.tutorial.game.characters.ClientCharacter;
import com.tutorial.game.controllers.AIController;
import com.tutorial.game.controllers.LocalPlayerController;

import static com.tutorial.game.constants.Constants.WORLD_WIDTH;

/**
 * Created by ryanl on 8/6/2017.
 */

public class LocalGameScreen extends GameScreen {

	private int numEnemies;

	public LocalGameScreen() {
		this(1);
	}

	public LocalGameScreen(int enemies) {
		super();
		numEnemies = enemies;
	}

	@Override
	public void show() {
	    super.show();
		ClientCharacter player = new ClientCharacter(getMap().getWorld());
		player.setPosition(player.getWidth(), 0);
		getMap().addCharacter(player);
		LocalPlayerController localPlayerController = new LocalPlayerController(player);
		//enemies = new Array<ClientCharacter>(numEnemies);
		for (int i = 0; i < numEnemies; i++) {
			ClientCharacter enemy = new ClientCharacter(getMap().getWorld());
			AIController enemyController = new AIController(enemy);
			enemy.setPosition((float)((i + 1) * WORLD_WIDTH / (numEnemies + 1)), 0);
			getMap().addCharacter(enemy);
			//enemies.add(enemy);
		}
	}
}
