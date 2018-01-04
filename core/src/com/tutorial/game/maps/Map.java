package com.tutorial.game.maps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Timer;
import com.tutorial.game.characters.Character;
import com.tutorial.game.characters.ClientCharacter;
import com.tutorial.game.constants.GameState;
import com.tutorial.game.controllers.LocalPlayerController;
import com.tutorial.game.controllers.PlayerController;

import static com.tutorial.game.constants.Constants.WORLD_HEIGHT;
import static com.tutorial.game.constants.Constants.WORLD_WIDTH;

/**
 * Created by ryanwiener on 10/11/17.
 */

public abstract class Map {

	private World world;
	//private Box2DDebugRenderer renderer;
	private GameState gameState;
	private OrthographicCamera camera;
	private Array<Character> characters;
	private int count;

	public Map() {
		gameState = GameState.WAITING;
		count = 0;
		world = new World(new Vector2(0, -200f), true);
		//renderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		characters = new Array<Character>();
		/*
		OrthographicCamera cam = (OrthographicCamera) stage.getCamera();
		cam.zoom = WORLD_WIDTH / cam.viewportWidth;
		cam.position.x = WORLD_WIDTH / 2;
		cam.position.y = WORLD_HEIGHT / 2;
		*/
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		/*
		if (Gdx.graphics.getHeight() != 0) {
			if (Gdx.graphics.getWidth() / Gdx.graphics.getHeight() < 16f / 9) {
				camera.zoom = WORLD_WIDTH / Gdx.graphics.getWidth();
			} else {
				camera.zoom = WORLD_HEIGHT / Gdx.graphics.getHeight();
			}
		}
		camera.position.x = WORLD_WIDTH / 2;
		camera.position.y = WORLD_HEIGHT / 2;
		camera.update();
		*/
		getWorld().setContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				if (contact.getFixtureA().getUserData() != null && contact.getFixtureB().getUserData() != null) {
					if (!contact.getFixtureA().getUserData().toString().equals("")) {
						Character attacker = (Character) contact.getFixtureA().getBody().getUserData();
						Character defender = (Character) contact.getFixtureB().getBody().getUserData();
						if (attacker.isFacingRight()) {
							defender.setFlip(true, false);
						} else {
							defender.setFlip(false, false);
						}
						defender.flinch();
						defender.changeHealth(-2);
						if (defender.getIsDead()) {
							if (defender.getController() instanceof PlayerController) {
                                gameState = GameState.LOST;
							} else {
                                gameState = GameState.WON;
							}
						}
					} else if (!contact.getFixtureB().getUserData().toString().equals("")) {
						Character attacker = (Character) contact.getFixtureB().getBody().getUserData();
						Character defender = (Character) contact.getFixtureA().getBody().getUserData();
						if (attacker != null && defender != null) {
							if (attacker.isFacingRight()) {
								defender.setFlip(true, false);
							} else {
								defender.setFlip(false, false);
							}
							defender.flinch();
							defender.changeHealth(-2);
							if (defender.getIsDead()) {
								if (defender.getController() instanceof PlayerController) {
                                    gameState = GameState.LOST;
								} else {
                                    gameState = GameState.WON;
								}
							}
						}
					}
				}
			}

			@Override
			public void endContact(Contact contact) {

			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {

			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {

			}
		});
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gs) {
	    gameState = gs;
    }

	public World getWorld() {
		return world;
	}

	public void act(float delta) {
        world.step(1 / 60f, 8, 3);
        for (int i = 0; i < characters.size; ++i) {
            characters.get(i).act(delta);
        }
	}

	public void resize(int width, int height) {
	    if (width != 0 && height != 0) {
			if ((float)width / height < 16f / 9) {
				//camera.zoom = WORLD_WIDTH / width;
                camera.viewportWidth = WORLD_WIDTH;
                camera.viewportHeight = height * WORLD_WIDTH / width;
			} else {
				//camera.zoom = WORLD_HEIGHT / height;
                camera.viewportHeight = WORLD_HEIGHT;
                camera.viewportWidth = width * WORLD_HEIGHT / height;
			}
		}
        camera.position.x = WORLD_WIDTH / 2;
        camera.position.y = WORLD_HEIGHT / 2;
		camera.update();
    }

	public void draw(Batch batch) {
		//renderer.render(world, camera.combined); uncommenting this line will block out character sprites only uncomment for debugging box2D
		for (int i = 0; i < characters.size; ++i) {
			characters.get(i).draw(batch, 1);
		}
	}

	public int getCount() {
	    return count;
    }

    public void setCharacterDisable(boolean dis) {
	    for (Character character : characters) {
            character.setDisabled(dis);
        }
    }

	public void terminate() {
	    gameState = GameState.TERMINATED;
    }

	public Array<Character> getCharacters() {
		return characters;
	}

	public void addCharacter(Character character) {
		characters.add(character);
		if (characters.size > 1) {
		    beginGame();
        }
	}

	public void removeCharacter(Character character) {
	    world.destroyBody(character.getCharacterBody());
        int index = characters.indexOf(character, false);
        characters.get(index).dispose();
        characters.removeIndex(index);
    }

	public void beginGame() {
	    gameState = GameState.COUNTING;
        characters.get(0).setPosition(characters.get(0).getWidth(), 0);
        characters.get(0).setFlip(false, false);
        characters.get(1).setPosition(WORLD_WIDTH - 2 * characters.get(1).getWidth(), 0);
        characters.get(1).setFlip(true, false);
        setCharacterDisable(true);
        count = 3;
        final Timer countDown = new Timer();
        countDown.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                count--;
                if (count < 1) {
                    setCharacterDisable(false);
                    if (count < 0) {
                        gameState = GameState.PLAYING;
                        countDown.stop();
                    }
                }
            }
        }, 1, 1);
    }

	public Camera getCamera() {
		return camera;
	}

	public void dispose() {
		for (int i = 0; i < characters.size; ++i) {
		    characters.get(i).dispose();
		}
		//renderer.dispose();
		world.dispose();
	}
}
