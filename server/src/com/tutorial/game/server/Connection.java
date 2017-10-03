package com.tutorial.game.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Timer;

import java.net.*;
import java.io.*;
import java.util.UUID;

public class Connection {

	private Socket client;
	private ServerGame serverGame;
	private UUID id;
	BufferedReader in;
	PrintWriter out;

	Connection(Socket cli) {
		client = cli;
		serverGame = null;
		id = UUID.randomUUID();
		try {
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(client.getOutputStream(), true);
			out.println(id.toString());
		} catch (IOException e) {
			System.err.println("in or out failed");
			System.exit(-1);
		}
		Thread inputThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						String line = null;
						while ((line = in.readLine()) != null) {
							Gdx.app.log("Received", line);
							if (line.equals("disconnecting")) {
								Thread.currentThread().interrupt();
								return;
							} else {
								serverGame.sendInput(line, id);
							}
						}
					} catch (IOException e) {
						System.err.println("Read failed");
						System.exit(1);
					}
				}
			}
		});
		inputThread.start();
		Timer outputTimer = new Timer();
		outputTimer.scheduleTask(new Timer.Task() {
			@Override
			public void run() {
				if (serverGame != null) {
					out.println(serverGame);
				}
			}
		}, 0, 1 / 100f);
	}

	public void setServerGame(ServerGame game) {
		serverGame = game;
        serverGame.addPlayer(id);
	}

	public ServerGame getServerGame() {
		return serverGame;
	}
}
