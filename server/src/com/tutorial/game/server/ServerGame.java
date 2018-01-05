package com.tutorial.game.server;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.tutorial.game.characters.Character;
import com.tutorial.game.constants.GameState;
import com.tutorial.game.maps.DefaultMap;
import com.tutorial.game.maps.Map;

import java.util.UUID;

/**
 * Created by ryanl on 9/28/2017.
 */

public class ServerGame implements Disposable {

    private Map map;

    ServerGame() {
        map = new DefaultMap();
    }

    public void addPlayer(UUID id) {
        Character player = new Character(map.getWorld());
        player.setPosition(player.getWidth(), 0);
        //players.add(player);
        map.addCharacter(player);
        ServerController controller = new ServerController(player, id);
        //controllers.add(controller);
    }

    public void removePlayer(UUID id) {
        Array<Character> players = map.getCharacters();
        for (int i = 0; i < players.size; ++i) {
            if (players.get(i).getController() instanceof ServerController) {
                if (((ServerController) players.get(i).getController()).getUUID().equals(id)) {
                    map.disconnectPlayer();
                    //map.removeCharacter(players.get(i));
                    break;
                }
            }
        }
        if (map.getActivePlayerCount() < 1) {
            map.terminate();
        } else if (map.getGameState() != GameState.LOST && map.getGameState() != GameState.WON) {
            map.terminate();
            // may want to alert user of opponent leaving
        }
    }

    public void sendInput(String input, UUID uuid) {
        Array<Character> characters = map.getCharacters();
        for (int i = 0; i < characters.size; ++i) {
            if (characters.get(i).getController() instanceof ServerController) {
                ServerController controller = (ServerController) characters.get(i).getController();
                if (controller.getUUID().equals(uuid)) {
                    controller.processInput(Integer.parseInt(input));
                }
            }
        }
    }

    public void act(float delta) {
        map.act(delta);
    }

    public boolean isFull() {
        return map.getCharacters().size >= 2;
    }

    public boolean getIsDisconnected() {
        return map.getGameState() == GameState.TERMINATED;
    }

    public boolean isReadyToRemove() {
        return (map.getGameState() == GameState.TERMINATED && map.getActivePlayerCount() <= 0);
    }

    @Override
    public String toString() {
        return map.toString();
    }

    @Override
    public void dispose() {
        map.dispose();
    }
}
