package com.tutorial.game.server;

import com.badlogic.gdx.utils.Array;

import java.net.*;
import java.io.*;

public class Server {

	private ServerSocket serverSocket;
	private Array<Client> clients;

	Server() {
		serverSocket = null;
		clients = new Array<Client>();
	}

	public void listenSocket() {
		int port = 8000;
		/*
		if (System.getenv("PORT") != null) {
			port = Integer.parseInt(System.getenv("PORT"));
		}
		*/
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Could not listen on port " + port);
			System.exit(1);
		}
		System.out.println("Listening on port " + port);
		ServerMatch match = new ServerMatch();
		while (true) {
			Client client = null;
			try {
				client = new Client(serverSocket.accept());
				clients.add(client);
				client.setMatch(match);
				System.out.println("" + clients.size);
				Thread t = new Thread(client);
				t.start();
			} catch (IOException e) {
				System.err.println("Accept failed");
				System.exit(1);
			}
		}
	}

	protected void finalize() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			System.err.println("Unable to close server socket");
			System.exit(-1);
		}
	}
}
