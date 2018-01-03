package com.tutorial.game.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Timer;
import com.tutorial.game.characters.Character;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.UUID;

public class Connection implements Disposable {

	private Socket client;
	private ServerGame serverGame;
	private UUID id;
	private BufferedReader in;
	private PrintWriter out;
	private Timer outputTimer;
	private Thread inputThread;

	Connection(Socket cli) {
		client = cli;
		serverGame = null;
		id = UUID.randomUUID();
		try {
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(client.getOutputStream(), true);
			System.out.println(id);
			out.println(id);
		} catch (IOException e) {
			System.err.println("in or out failed");
			dispose();
		}
		inputThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						String line = null;
						while ((line = in.readLine()) != null) {
							Gdx.app.log("Received", line);
							if (line.equals("disconnecting")) {
							    dispose();
								return;
							} else if (serverGame != null) {
								serverGame.sendInput(line, id);
							}
						}
					} catch (IOException e) {
						System.err.println("Read failed");
						dispose();
					}
				}
			}
		});
		inputThread.start();
		outputTimer = new Timer();
		outputTimer.scheduleTask(new Timer.Task() {
			@Override
			public void run() {
				if (serverGame != null) {
                    out.println(serverGame);
					if (serverGame.getIsDisconnected()) {
						dispose();
						return;
					}
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

	public UUID getUUID() { return id; }

	@Override
	public void dispose() {
        serverGame.removePlayer(id);
        inputThread.interrupt();
        outputTimer.stop();
		try {
			in.close();
			out.close();
			client.close();
		} catch (IOException e) {
			System.err.println("Closing connection failed");
		}
	}
}
