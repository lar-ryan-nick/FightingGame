package com.tutorial.game.server;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

/**
 * Created by ryanl on 9/30/2017.
 */

public class ServerApplication implements ApplicationListener {

    private Server server;

    @Override
    public void create() {
        server = new Server();
        Thread serverThread = new Thread(server);
        serverThread.start();
        //serverGame = new ServerGame();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        Array<ServerGame> serverGames = server.getServerGames();
        synchronized (serverGames) {
            for (int i = 0; i < serverGames.size; ++i) {
				if (serverGames.get(i).isReadyToRemove()) {
					serverGames.get(i).dispose();
					serverGames.removeIndex(i);
                    i--;
				} else if (!serverGames.get(i).getIsDisconnected()) {
                    serverGames.get(i).act(Gdx.graphics.getDeltaTime());
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
        server.dispose();
    }
}
