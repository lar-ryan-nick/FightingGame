package com.tutorial.game.server;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import java.net.*;
import java.io.*;

public class Server implements Runnable, Disposable {

	private ServerSocket serverSocket;
	private Array<ServerGame> serverGames;

	Server() {
		int port = 8000;
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
				System.out.println("Waiting for a connection...");
				connection = new Connection(serverSocket.accept());
				System.out.println("Made a connection!");
				synchronized (serverGames) {
					if (serverGames.size > 0 && !serverGames.get(serverGames.size - 1).isFull()) {
						connection.setServerGame(serverGames.get(serverGames.size - 1));
						break;
					} else {
						serverGames.add(new ServerGame());
						connection.setServerGame(serverGames.get(serverGames.size - 1));
					}
				}
				System.out.println(serverGames.size);
			} catch (IOException e) {
				System.out.println("Accept failed");
				System.exit(1);
			}
		}
	}

	public Array<ServerGame> getServerGames() {
		return serverGames;
	}

	@Override
	public void dispose() {
		synchronized (serverGames) {
			for (int i = 0; i < serverGames.size; ++i) {
				serverGames.get(i).dispose();
			}
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
			System.err.println("Unable to close server socket");
			System.exit(-1);
		}
	}
}
