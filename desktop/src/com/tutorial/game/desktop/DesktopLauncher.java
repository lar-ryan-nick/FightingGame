package com.tutorial.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.tutorial.game.TutorialGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Fighter";
		config.vSyncEnabled = true;
		config.width = 1280;
		config.height = 720;
		new LwjglApplication(new TutorialGame(), config);
	}
}
