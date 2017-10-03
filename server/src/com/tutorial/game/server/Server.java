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
	private Array<Connection> connections;

	Server() {
		int port = 8000;
		/*
		if (System.getenv("PORT") != null) {
			port = Integer.parseInt(System.getenv("PORT"));
		}
		*/
		serverSocket = null;
		connections = new Array<Connection>();
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
				synchronized (connections) {
					connections.add(connection);
				}
			} catch (IOException e) {
				System.err.println("Accept failed");
				System.exit(1);
			}
		}
	}

	public Array<Connection> getConnections() {
		return connections;
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
