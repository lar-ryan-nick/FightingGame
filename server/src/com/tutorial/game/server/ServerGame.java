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
        map.addActor(player);
        ServerController controller = new ServerController(player, id);
        //controllers.add(controller);
    }

    public void sendInput(String input, UUID uuid) {
        Array<Actor> actors = map.getActors();
        for (int i = 0; i < actors.size; ++i) {
            if (actors.get(i) instanceof Character && ((Character) actors.get(i)).getController() instanceof ServerController) {
                ServerController controller = (ServerController) ((Character) actors.get(i)).getController();
                if (controller.getUUID().equals(uuid)) {
                    controller.processInput(Integer.parseInt(input));
                }
            }
        }
    }

    public void act() {
        map.act();
    }

    public boolean isFull() {
        int count = 0;
        Array<Actor> actors = map.getActors();
        for (int i = 0; i < actors.size; ++i) {
            if (actors.get(i) instanceof Character) {
                count++;
                if (count >= 2) {
                    return true;
                }
            }
        }
        return false;
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
        String serialization = "";
        int count = 0;
        Array<Actor> actors = map.getActors();
        for (int i = 0; i < actors.size; ++i) {
            if (actors.get(i) instanceof Character) {
                count++;
                Character character = ((Character) actors.get(i));
                serialization += "&" + character.serialize(i);
            }
        }
        serialization = "numPlayers=" + count + serialization;
        return serialization;
    }
}
