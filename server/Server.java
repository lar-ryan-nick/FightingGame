import java.net.*;
import java.io.*;

public class Server {

	private ServerSocket serverSocket;
	
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
		while (true) {
			Client client = null;
			try {
				client = new Client(serverSocket.accept());
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
