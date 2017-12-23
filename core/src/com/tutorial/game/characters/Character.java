package com.tutorial.game.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Timer;
import com.tutorial.game.controllers.AIController;
import com.tutorial.game.controllers.Controller;
import com.tutorial.game.controllers.NetworkController;

import java.util.Map;

import static com.tutorial.game.constants.Constants.CATEGORY_ARM;
import static com.tutorial.game.constants.Constants.CATEGORY_CHARACTER;
import static com.tutorial.game.constants.Constants.CATEGORY_SCENERY;
import static com.tutorial.game.constants.Constants.CHARACTER_SCALE;
import static com.tutorial.game.constants.Constants.WORLD_WIDTH;

/**
 * Created by ryanl on 8/6/2017.
 */

public class Character extends Actor implements Disposable {

	protected String characterImagePath;
	private Body characterBody;
	protected int health;
	private Controller controller;
	private boolean isMovingLeft;
	private boolean isMovingRight;
	private boolean isFacingRight;
	private boolean isInAir;
	private boolean isDead;
	private int currWalkNum;
	private int currCrouchNum;
	private int currPunchNum;
	private int currFlinchNum;
	private int coolDown;
	private Timer walkingTimer;
	private Timer crouchingTimer;
	private Timer standingTimer;
	private Timer punchingTimer;
	private Timer flinchTimer;
	private boolean needsUpdate;

	public Character(World world) {
		super();
		setSize(17 * CHARACTER_SCALE, 42 * CHARACTER_SCALE);
		controller = null;
		characterImagePath = "img/character/character_idle.png";
		isMovingLeft = false;
		isMovingRight = false;
		isInAir = false;
		controller = null;
		isDead = false;
		isFacingRight = true;
		needsUpdate = false;
		currWalkNum = -1;
		currCrouchNum = -1;
		currPunchNum = -1;
		currFlinchNum = -1;
		coolDown = 0;
		walkingTimer = new Timer();
		walkingTimer.scheduleTask(new Timer.Task() {
			@Override
			public void run() {
				updateWalkingAnim();
			}
		}, 0, .1f);
		walkingTimer.stop();
		crouchingTimer = new Timer();
		crouchingTimer.scheduleTask(new Timer.Task() {
			@Override
			public void run() {
				updateCrouchingAnim();
			}
		}, 0, .05f);
		crouchingTimer.stop();
		standingTimer = new Timer();
		standingTimer.scheduleTask(new Timer.Task() {
			@Override
			public void run() {
				updateStandingAnim();
			}
		}, 0, .05f);
		standingTimer.stop();
		punchingTimer = new Timer();
		punchingTimer.scheduleTask(new Timer.Task() {
			@Override
			public void run() {
				updatePunchingAnim();
			}
		}, 0, .075f);
		punchingTimer.stop();
		flinchTimer = new Timer();
		flinchTimer.scheduleTask(new Timer.Task() {
			@Override
			public void run() {
				updateFlinchingAnim();
			}
		}, 0, .075f);
		flinchTimer.stop();
		BodyDef box = new BodyDef();
		box.type = BodyDef.BodyType.DynamicBody;
		box.position.set(getX() + getWidth() / 2, getY() + getHeight() / 2);
		box.fixedRotation = true;
		characterBody = world.createBody(box);
		characterBody.setUserData(this);
		health = 100;
		setTexture("img/character/character_idle.png");
	}

	public void jump() {
		if (!isDead && !isInAir && currFlinchNum < 0) {
			setTexture("img/character/character_jump_start.png");
			characterBody.applyForceToCenter(0, 10000f * characterBody.getMass(), true);
			isInAir = true;
			currCrouchNum = -1;
			crouchingTimer.stop();
			standingTimer.stop();
		}
	}

	public void jab() {
		if (coolDown <= 0 && !isDead && currPunchNum < 0 && currFlinchNum < 0) {
			currPunchNum = 0;
			punchingTimer.start();
		}
	}

	public void flinch() {
		float knockbackForce = 1000f * characterBody.getMass();
		if (isFacingRight) {
			knockbackForce *= - 1;
		}
		characterBody.applyForceToCenter(knockbackForce, 0, true);
		if (!isDead) {
			currPunchNum = -1;
			punchingTimer.stop();
			currFlinchNum = 0;
			flinchTimer.start();
		}
	}

	public void setIsMovingLeft(boolean val) {
		if (!isDead) {
			if (val) {
				isMovingRight = false;
				setFlip(true, false);
				if (currWalkNum < 0) {
					currWalkNum = 0;
					walkingTimer.start();
				}
			}
			isMovingLeft = val;
		}
	}

	public void setIsMovingRight(boolean val) {
		if (!isDead) {
			if (val) {
				isMovingLeft = false;
				setFlip(false, false);
				if (currWalkNum < 0) {
					currWalkNum = 0;
					walkingTimer.start();
				}
			}
			isMovingRight = val;
		}
	}

	public void setIsCrouching(boolean val) {
		if (!isDead) {
			if (val) {
				currCrouchNum = 0;
				standingTimer.stop();
				crouchingTimer.start();
			} else {
				crouchingTimer.stop();
				standingTimer.start();
			}
		}
	}

	protected void updateWalkingAnim() {
		if (!isDead) {
			if (!isInAir && currCrouchNum < 0 && currPunchNum < 0 && currFlinchNum < 0) {
				if (isMovingLeft || isMovingRight) {
					setFlip(isMovingLeft, false);
					setTexture("img/character/character_walk" + (currWalkNum + 1) + ".png");
					currWalkNum = (currWalkNum + 1) % 6;
				} else {
					setTexture("img/character/character_idle.png");
					currWalkNum = -1;
					walkingTimer.stop();
				}
			}
		}
	}

	protected void updateCrouchingAnim() {
		if (!isDead) {
			if (!isInAir && getIsCrouching() && !getIsPunching()) {
				if (currCrouchNum <= 2) {
					setTexture("img/character/character_crouch" + (currCrouchNum + 1) + ".png");
					++currCrouchNum;
				}
				if (currCrouchNum > 2) {
					currCrouchNum = 2;
					crouchingTimer.stop();
				}
			}
		}
	}

	protected void updateStandingAnim() {
		if (!isDead) {
			if (!isInAir && currCrouchNum >= 0 && currPunchNum < 0) {
				setTexture("img/character/character_crouch" + (currCrouchNum + 1) + ".png");
				--currCrouchNum;
			} else if (currPunchNum < 0 && !isInAir) {
				setTexture("img/character/character_idle.png");
				standingTimer.stop();
			}
		}
	}

	protected void updatePunchingAnim() {
		if (!isDead) {
			if (currPunchNum == 2) {
				if (isInAir) {
					setTexture("img/character/character_jump_loop.png");
				} else if (currCrouchNum >= 0) {
					setTexture("img/character/character_crouch" + (currCrouchNum + 1) + ".png");
				} else {
					setTexture("img/character/character_idle.png");
				}
				coolDown = 10;
				currPunchNum = -1;
				punchingTimer.stop();
			} else if (currPunchNum >= 0) {
				if (isInAir) {
					setTexture("img/character/character_jumping_jab" + (currPunchNum + 1) + ".png");
					++currPunchNum;
				} else if (currCrouchNum >= 0) {
					setTexture("img/character/character_crouching_jab" + (currPunchNum + 1) + ".png");
					++currPunchNum;
				} else {
					setTexture("img/character/character_standing_jab" + (currPunchNum + 1) + ".png");
					++currPunchNum;
				}
			} else {
				punchingTimer.stop();
			}
		}
	}

	protected void updateFlinchingAnim() {
		if (!isDead) {
			if (currFlinchNum == 2) {
				if (isInAir) {
					//setTexture("img/character/character_jump_loop.png");
					return;
				} else if (currCrouchNum >= 0) {
					setTexture("img/character/character_crouch" + (currCrouchNum + 1) + ".png");
				} else {
					setTexture("img/character/character_idle.png");
				}
				currFlinchNum = -1;
				flinchTimer.stop();
			} else if (currFlinchNum >= 0) {
				if (isInAir) {
					setTexture("img/character/character_jumping_damage" + (currFlinchNum + 1) + ".png");
					++currFlinchNum;
				} else if (currCrouchNum >= 0) {
					setTexture("img/character/character_crouching_damage" + (currFlinchNum + 1) + ".png");
					++currFlinchNum;
				} else {
					setTexture("img/character/character_standing_damage_high" + (currFlinchNum + 1) + ".png");
					++currFlinchNum;
				}
			} else {
				flinchTimer.stop();
			}
		}
	}

	protected void updateCharacterSize() {
		TextureData textureData = TextureData.Factory.loadFromFile(Gdx.files.internal(characterImagePath), true);
		float widthDifference = 0;
		if (!isFacingRight) {
			widthDifference = textureData.getWidth() * CHARACTER_SCALE - getWidth();
		}
		setSize(textureData.getWidth() * CHARACTER_SCALE, textureData.getHeight() * CHARACTER_SCALE);
		textureData.disposePixmap();
		World world = characterBody.getWorld();
		Vector2 velocity = characterBody.getLinearVelocity();
		BodyDef box = new BodyDef();
		box.type = BodyDef.BodyType.DynamicBody;
		// make sure foot placement is constant for different animations
		if (getIsPunching() || getIsFlinching() || (!getIsDead() && !getIsCrouching() && !isInAir)) {
			box.position.set(getX() + getWidth() / 2 - widthDifference, getY() + getHeight() / 2);
		} else {
			box.position.set(getX() + getWidth() / 2, getY() + getHeight() / 2);
		}
		// make sure that character didn't fall of the map
		if (box.position.x < 0) {
			box.position.set(0, box.position.y);
		} else if (box.position.x > WORLD_WIDTH - getWidth()) {
			box.position.set(WORLD_WIDTH - getWidth(), box.position.y);
		}
		box.fixedRotation = true;
		box.linearVelocity.set(velocity);
		if (!world.isLocked()) {
			world.destroyBody(characterBody);
            characterBody = world.createBody(box);
			needsUpdate = false;
			characterBody.setUserData(this);
			PolygonShape rectangle = new PolygonShape();
			//Json json = new Json();
			int leftIndex = characterImagePath.lastIndexOf("/") + 1;
			int rightIndex = characterImagePath.lastIndexOf(".");
			JsonValue fixtureJSON = new JsonReader().parse(Gdx.files.internal("json/" + characterImagePath.substring(leftIndex, rightIndex) + "_verticies.json")).child();
			int numFixtures = fixtureJSON.asInt();
			fixtureJSON = fixtureJSON.next();
			for (int i = 0; i < numFixtures; ++i) {
				JsonValue verticieJSON = fixtureJSON.get(i).child();
				Vector2[] verticies = new Vector2[verticieJSON.asInt()];
				verticieJSON = verticieJSON.next();
				for (int j = 0; j < verticies.length; ++j) {
					float xVal = verticieJSON.child().asFloat() * CHARACTER_SCALE;
					float yVal = verticieJSON.child().next().asFloat() * CHARACTER_SCALE;
					if (!isFacingRight) {
						xVal *= -1;
					}
					Vector2 verticie = new Vector2(xVal, yVal);
					verticies[j] = verticie;
					verticieJSON = verticieJSON.next();
				}
				rectangle.set(verticies);
				FixtureDef boxFixture = new FixtureDef();
				boxFixture.shape = rectangle;
				boxFixture.density = 1f;
				boxFixture.friction = 1f;
				boxFixture.restitution = 0f;
				if (verticieJSON != null) {
					boxFixture.filter.categoryBits = CATEGORY_ARM;
					boxFixture.filter.maskBits = -1;
					boxFixture.isSensor = true;
				} else {
					boxFixture.filter.categoryBits = CATEGORY_CHARACTER;
					boxFixture.filter.maskBits = CATEGORY_ARM | CATEGORY_SCENERY;
				}
				Fixture fixture = characterBody.createFixture(boxFixture);
				if (verticieJSON != null) {
					fixture.setUserData("jab");
				} else {
					fixture.setUserData("");
				}
			}
			rectangle.dispose();
		} else {
			needsUpdate = true;
		}
	}

	@Override
	public void act(float delta) {
		//if (needsUpdate) {
			updateCharacterSize();
		//}
		if (coolDown > 0) {
			--coolDown;
		}
		if (!isDead) {
			float xVal = 0, yVal = 0;
			if (currCrouchNum < 0 && currPunchNum < 0 && currFlinchNum < 0) {
				if (isMovingLeft) {
					xVal = -5000 * characterBody.getMass();
				} else if (isMovingRight) {
					xVal = 5000 * characterBody.getMass();
				}
				if (Math.abs(characterBody.getLinearVelocity().x) > 5) {
					xVal /= Math.abs(characterBody.getLinearVelocity().x);
				} else {
					xVal /= 5;
				}
			}
			// handle jump animation
			if (isInAir) {
				xVal /= 10;
				if (characterBody.getPosition().y < getHeight() / 2 + 10 * CHARACTER_SCALE) {
					if (characterBody.getLinearVelocity().y < 1) {
						if (characterBody.getPosition().y < getHeight() / 2 + 3 * CHARACTER_SCALE) {
							isInAir = false;
							setTexture("img/character/character_idle.png");
						} else {
							setTexture("img/character/character_jump_end.png");
						}
					}
				} else if (currPunchNum < 0 && currFlinchNum < 0) {
					setTexture("img/character/character_jump_loop.png");
				}
			}
			characterBody.applyForceToCenter(xVal, yVal, true);
			if (controller instanceof AIController) {
				((AIController) controller).act();
			}
		}
		super.setPosition(characterBody.getPosition().x - getWidth() / 2, characterBody.getPosition().y - getHeight() / 2);
	}

	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		//updateCharacterSize();
	}

	public void setFlip(boolean x, boolean y) {
		if (isFacingRight == x) {
			isFacingRight = !x;
			//updateCharacterSize();
		}
	}

	public Controller getController() {
		return controller;
	}

	public void setController(Controller control) {
		controller = control;
	}

	protected void setTexture(String internalFilePath) {
		if (!characterImagePath.equals(internalFilePath)) {
			characterImagePath = internalFilePath;
			//updateCharacterSize();
		}
	}

	@Override
	public String toString() {
		String uuid = "";
		if (getController() instanceof NetworkController) {
			uuid = "uuid=" + ((NetworkController) getController()).getUUID().toString() + "&";
		}
		return uuid + "x=" + getX() + "&y=" + getY() + "&velX=" + characterBody.getLinearVelocity().x + "&velY=" + characterBody.getLinearVelocity().y + "&facingRight=" + isFacingRight + "&health=" + health + "&imageName=" + characterImagePath;
	}

	public String serialize(int i) {
		String uuid = "";
		if (getController() instanceof NetworkController) {
			uuid = "uuid" + i + "=" + ((NetworkController) getController()).getUUID().toString() + "&";
		}
		return uuid + "x" + i + "=" + getX() + "&y" + i + "=" + getY() + "&velX" + i + "=" + characterBody.getLinearVelocity().x + "&velY" + i + "=" + characterBody.getLinearVelocity().y + "&facingRight" + i + "=" + isFacingRight + "&health" + i + "=" + health + "&imageName" + i + "=" + characterImagePath;
	}

	public void updateFromMap(Map<String, String> vals, int index) {
		//HashMap<String, String> vals = parseString(s);
		if (vals != null) {
			super.setPosition(Float.parseFloat(vals.get("x" + index)), Float.parseFloat(vals.get("y" + index)));
			characterBody.setLinearVelocity(Float.parseFloat(vals.get("velX" + index)), Float.parseFloat(vals.get("velY" + index)));
			setFlip(!Boolean.parseBoolean(vals.get("facingRight" + index)), false);
			health = Integer.parseInt(vals.get("health" + index));
			setTexture(vals.get("imageName" + index));
			//updateCharacterSize();
		}
	}
/*
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
*/
	public boolean isFacingRight() {
		return isFacingRight;
	}

	public void changeHealth(int amount) {
		health += amount;
		if (health <= 0) {
			health = 0;
			isDead = true;
			setTexture("img/character/character_ko.png");
			walkingTimer.stop();
			crouchingTimer.stop();
			standingTimer.stop();
			punchingTimer.stop();
			flinchTimer.stop();
		}
	}

	public boolean getIsDead() {
		return isDead;
	}

	public Body getCharacterBody() {
		return characterBody;
	}

	public boolean getIsInAir() {
		return isInAir;
	}

	public boolean getIsCrouching() {
		return currCrouchNum >= 0;
	}

	public boolean getIsPunching() {
		return currPunchNum >= 0;
	}

	public boolean getIsFlinching() {
		return currFlinchNum >= 0;
	}

	@Override
	public void dispose() {
		/*
		if (!characterBody.getWorld().isLocked()) {
			characterBody.getWorld().destroyBody(characterBody);
		}
		*/
	}
}
