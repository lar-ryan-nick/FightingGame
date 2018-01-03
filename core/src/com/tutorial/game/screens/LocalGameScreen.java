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

	public LocalGameScreen() {
		super();
	}

	@Override
	public void show() {
	    super.show();
		ClientCharacter player = new ClientCharacter(getMap().getWorld());
        LocalPlayerController localPlayerController = new LocalPlayerController(player);
		//player.setPosition(player.getWidth(), 0);
		getMap().addCharacter(player);
        ClientCharacter enemy = new ClientCharacter(getMap().getWorld());
        AIController enemyController = new AIController(enemy);
        //enemy.setPosition((float)((i + 1) * WORLD_WIDTH / (numEnemies + 1)), 0);
        getMap().addCharacter(enemy);
	}
}
