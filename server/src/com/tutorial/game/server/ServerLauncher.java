package com.tutorial.game.server;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.tutorial.game.TutorialGame;

public class ServerLauncher {

	public static void main (String[] arg) {
		new HeadlessApplication(new ServerApplication());
	}
}
