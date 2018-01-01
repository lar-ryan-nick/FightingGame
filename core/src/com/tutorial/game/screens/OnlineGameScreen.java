package com.tutorial.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.tutorial.game.characters.Character;
import com.tutorial.game.characters.ClientCharacter;
import com.tutorial.game.constants.GameState;
import com.tutorial.game.controllers.NetworkController;
import com.tutorial.game.controllers.OnlinePlayerController;
import com.tutorial.game.maps.DefaultMap;
import com.tutorial.game.maps.Map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.UUID;

import static com.tutorial.game.constants.Constants.CATEGORY_SCENERY;
import static com.tutorial.game.constants.Constants.WORLD_HEIGHT;
import static com.tutorial.game.constants.Constants.WORLD_WIDTH;

/**
 * Created by ryanwiener on 9/27/17.
 */

public class OnlineGameScreen extends GameScreen {

	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;

	public void listenServer() {
		try{
			//socket = new Socket("76.90.139.212", 8000);
			socket = new Socket("localhost", 8000);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (UnknownHostException e) {
			System.out.println("Unknown host");
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Can't read");
			System.exit(1);
		}
		System.out.println(socket);
	}

	@Override
	public void show() {
		listenServer();
		super.show();
		String id = null;
		try {
			while ((id = in.readLine()) == null) {}
		} catch (IOException e) {
            System.err.println("Couldn't read id");
            System.exit(-1);
		}
		ClientCharacter player = new ClientCharacter(getMap().getWorld());
		player.setPosition(player.getWidth(), 0);
		getMap().addCharacter(player);
		OnlinePlayerController controller = new OnlinePlayerController(player, out, UUID.fromString(id));
	}

	@Override
	public void render(float delta) {
		try {
			String line = null;
			while (in.ready()) {
				line = in.readLine();
				//Gdx.app.log("Received", line);
			}
			//Gdx.app.log("Using", line);
			if (line != null) {
				HashMap<String, String> params = parseString(line);
				if (params != null) {
				    getMap().setGameState(GameState.valueOf(params.get("gameState")));
				    if (getMap().getGameState() != GameState.TERMINATED) {
                        for (int i = 0; i < Integer.parseInt(params.get("numPlayers")); ++i) {
                            Array<Character> characters = getMap().getCharacters();
                            boolean found = false;
                            for (int j = 0; j < characters.size; ++j) {
                                if (characters.get(j).getController() instanceof NetworkController) {
                                    if (((NetworkController) (characters.get(j).getController())).getUUID().equals(UUID.fromString(params.get("uuid" + i)))) {
                                        characters.get(j).updateFromMap(params, i);
                                        found = true;
                                        break;
                                    }
                                }
                            }
                            if (!found) {
                                ClientCharacter player = new ClientCharacter(getMap().getWorld());
                                NetworkController controller = new NetworkController(player, UUID.fromString(params.get("uuid" + i)));
                                player.setPosition(player.getWidth(), 0);
                                getMap().addCharacter(player);
                            }
                        }
                    }
				}
			}
		} catch (IOException e) {
			System.err.println("Can't read");
			System.exit(1);
		}
        super.render(delta);
	}

	public HashMap<String, String> parseString(String s) {
		HashMap<String, String> result = new HashMap<String, String>();
		String[] params = s.split("&");
		for (int i = 0; i < params.length; ++i) {
			String[] keyVal = params[i].split("=");
			if (keyVal.length > 1) {
				result.put(keyVal[0], keyVal[1]);
			}
		}
		if (result.isEmpty()) {
			return null;
		}
		return result;
	}

	@Override
	public void dispose() {
	    super.dispose();
		try {
			out.println("disconnecting");
			socket.close();
			in.close();
			out.close();
		} catch (IOException e) {
			System.err.println("Couldn't close connection");
			System.exit(1);
		}
	}
}
