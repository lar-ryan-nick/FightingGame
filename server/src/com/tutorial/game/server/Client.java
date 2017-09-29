package com.tutorial.game.server;

import java.net.*;
import java.io.*;

public class Client implements Runnable {

	private Socket client;
	private ServerMatch match;

	Client(Socket cli) {
		client = cli;
		match = null;
	}

	public void setMatch(ServerMatch serverMatch) {
		match = serverMatch;
        match.addPlayer();
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
		while(true) {
			try {
				String line = in.readLine();
				if (line == null || line.equals("disconnecting")) {
					System.out.println(line);
					Thread.currentThread().interrupt();
					return;
				}
				if (match != null) {
                    match.sendInput(line);
                    out.println(match);
                } else {
                    out.println("Waiting for other player");
                }
				System.out.println(line);
			} catch (IOException e) {
				System.err.println("Read failed");
				System.exit(1);
			}
		}
	}
}
