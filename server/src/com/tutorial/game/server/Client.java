package com.tutorial.game.server;

import java.net.*;
import java.io.*;

public class Client implements Runnable {

	private Socket client;

	Client(Socket cli) {
		client = cli;
	}

	public void run() {
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(client.getOutputStream(), true);
		} catch (IOException e) {
			System.err.println("in or out failed");
			System.exit(-1);
		}
		boolean connected = true;
		while(connected) {
			try {
				String line = in.readLine();
				if (line == null || line.equals("disconnecting")) {
					Thread.currentThread().interrupt();
					connected = false;
					return;
				}
				out.println(line);
				System.out.println(line);
			} catch (IOException e) {
				System.err.println("Read failed");
				System.exit(1);
			}
		}
	}
}
