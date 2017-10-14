package com.tutorial.game.server;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.tutorial.game.characters.Character;
import com.tutorial.game.maps.DefaultMap;
import com.tutorial.game.maps.Map;

import java.util.UUID;

import static com.tutorial.game.constants.Constants.CATEGORY_SCENERY;
import static com.tutorial.game.constants.Constants.WORLD_HEIGHT;
import static com.tutorial.game.constants.Constants.WORLD_WIDTH;

/**
 * Created by ryanl on 9/28/2017.
 */

public class ServerGame implements Disposable {

    private Map map;
    /*
    private World world;
    private Array<Character> players;
    private Array<ServerController> controllers;
    */
    private boolean isDisconnected;

    ServerGame() {
        isDisconnected = false;
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

    public void disconnect() {
        isDisconnected = true;
    }

    public boolean getIsDisconnected() {
        return isDisconnected;
    }

    @Override
    public void dispose() {

    }

    @Override
    public String toString() {
        if (isDisconnected) {
            return "Disconnected";
        }
        Array<Character> characters = map.getCharacters();
        String serialization = "numPlayers=" + characters.size;
        for (int i = 0; i < characters.size; ++i) {
            serialization += "&" + characters.get(i).serialize(i);
        }
        return serialization;
    }
}
