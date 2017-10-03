package com.tutorial.game.server;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.utils.Array;

/**
 * Created by ryanl on 9/30/2017.
 */

public class ServerApplication implements ApplicationListener {

    private Server server;
    private ServerGame serverGame;

    @Override
    public void create() {
        server = new Server();
        Thread serverThread = new Thread(server);
        serverThread.start();
        serverGame = new ServerGame();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        serverGame.render();
        Array<Connection> connections = server.getConnections();
        synchronized (connections) {
            for (int i = 0; i < connections.size; ++i) {
                if (connections.get(i).getServerGame() == null) {
                    connections.get(i).setServerGame(serverGame);
                } else {
                    //connections.get(i).getServerGame().render();
                }
            }
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
