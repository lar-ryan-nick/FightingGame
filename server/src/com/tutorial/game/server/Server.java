package com.tutorial.game.server;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import java.net.*;
import java.io.*;

public class Server implements Runnable, Disposable {

	private ServerSocket serverSocket;
	private Array<ServerGame> serverGames;

	Server() {
		int port = 8000;
		/*
		if (System.getenv("PORT") != null) {
			port = Integer.parseInt(System.getenv("PORT"));
		}
		*/
		serverSocket = null;
		serverGames = new Array<ServerGame>();
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Could not listen on port " + port);
			System.exit(1);
		}
		System.out.println("Listening on port " + port);
	}

	@Override
	public void run() {
		while (true) {
			Connection connection = null;
			try {
				connection = new Connection(serverSocket.accept());
				boolean added = false;
				synchronized (serverGames) {
					for (int i = 0; i < serverGames.size; ++i) {
						if (!serverGames.get(i).isFull()) {
							connection.setServerGame(serverGames.get(i));
							added = true;
							break;
						}
					}
					if (!added) {
						serverGames.add(new ServerGame());
						connection.setServerGame(serverGames.get(serverGames.size - 1));
					}
				}
			} catch (IOException e) {
				System.err.println("Accept failed");
				System.exit(1);
			}
		}
	}

	public Array<ServerGame> getServerGames() {
		return serverGames;
	}

	@Override
	public void dispose() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			System.err.println("Unable to close server socket");
			System.exit(-1);
		}
	}
}
