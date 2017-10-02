package com.tutorial.game.server;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.utils.Array;

/**
 * Created by ryanl on 9/30/2017.
 */

public class ServerApplication implements ApplicationListener {

    private Server server;
    private ServerMatch match;

    @Override
    public void create() {
        server = new Server();
        Thread serverThread = new Thread(server);
        serverThread.start();
        match = new ServerMatch();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        Array<Client> clients = server.getClients();
        synchronized (clients) {
            for (int i = 0; i < clients.size; ++i) {
                if (clients.get(i).getMatch() == null) {
                    clients.get(i).setMatch(match);
                } else {
                    clients.get(i).getMatch().render();
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
