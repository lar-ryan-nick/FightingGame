package com.tutorial.game.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectFloatMap;
import com.badlogic.gdx.utils.Timer;
import com.tutorial.game.screens.GameScreen;

import java.util.ArrayList;

/**
 * Created by ryanl on 8/6/2017.
 */

public class Character extends Actor {

    public static final short CATEGORY_CHARACTER = 0x0001;
    public static final short CATEGORY_ARM = 0x0002;
    private final float CHARACTER_SCALE = .2f;
    private String characterImagePath;
    private Sprite characterImage;
    private Sprite healthBar;
    private Body characterBody;
    private boolean isPossessed;
    private boolean isMovingLeft;
    private boolean isMovingRight;
    private boolean isFacingLeft;
    private boolean isInAir;
    private int currWalkNum;
    private int currCrouchNum;
    private int currPunchNum;
    private int currFlinchNum;
    private Timer walkingTimer;
    private Timer crouchingTimer;
    private Timer standingTimer;
    private Timer punchingTimer;
    private Timer flinchTimer;

    public Character(World world) {
        super();
        characterImagePath = "";
        characterImage = new Sprite(new Texture("img/character/character_idle.png"));
        healthBar = new Sprite(new Texture("img/square.png"));
        healthBar.setColor(0, 1, 0, 1);
        isMovingLeft = false;
        isMovingRight = false;
        isFacingLeft = false;
        isInAir = false;
        isPossessed = false;
        currWalkNum = -1;
        currCrouchNum = -1;
        currPunchNum = -1;
        currFlinchNum = -1;
        walkingTimer = new Timer();
        walkingTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                updateWalkingAnim();
            }
        }, 0, .15f);
        walkingTimer.stop();
        crouchingTimer = new Timer();
        crouchingTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                updateCrouchingAnim();
            }
        }, 0, .1f);
        crouchingTimer.stop();
        standingTimer = new Timer();
        standingTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                updateStandingAnim();
            }
        }, 0, .1f);
        standingTimer.stop();
        punchingTimer = new Timer();
        punchingTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                updatePunchingAnim();
            }
        }, 0, .2f);
        punchingTimer.stop();
        flinchTimer = new Timer();
        flinchTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                updateFlinchingAnim();
            }
        }, 0, .15f);
        flinchTimer.stop();
        BodyDef box = new BodyDef();
        box.type = BodyDef.BodyType.DynamicBody;
        box.position.set(getX() - Gdx.graphics.getWidth() / 2 + getWidth() / 2, getY() - Gdx.graphics.getHeight() / 2 + getHeight() / 2);
        box.fixedRotation = true;
        characterBody = world.createBody(box);
        characterBody.setUserData(this);
        setTexture("img/character/character_idle.png");
    }

    public void jump()
    {
        if (!isInAir) {
            setTexture("img/character/character_jump_start.png");
            //characterBody.setLinearVelocity(0, 500000f);
            characterBody.applyForceToCenter(0, 10000f * characterBody.getMass(), true);
            isInAir = true;
            currCrouchNum = -1;
            crouchingTimer.stop();
            standingTimer.stop();
        }
    }

    public void jab() {
        if (currPunchNum < 0 && currFlinchNum < 0) {
            currPunchNum = 0;
            punchingTimer.start();
        }
    }

    public void flinch() {
        currPunchNum = -1;
        punchingTimer.stop();
        currFlinchNum = 1;
        flinchTimer.start();
    }

    public void setIsMovingLeft(boolean val) {
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

    public void setIsMovingRight(boolean val) {
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

    public void setIsCrouching(boolean val)
    {
        if (!isInAir && currPunchNum < 0 && val) {
            currCrouchNum = 0;
            standingTimer.stop();
            crouchingTimer.start();
        } else {
            crouchingTimer.stop();
            standingTimer.start();
        }
    }

    private void updateWalkingAnim() {
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

    private void updateCrouchingAnim() {
        if (!isInAir && currCrouchNum >= 0 && currPunchNum < 0) {
            setTexture("img/character/character_crouch" + (currCrouchNum + 1) + ".png");
            if (currCrouchNum < 2) {
                ++currCrouchNum;
            } else {
                crouchingTimer.stop();
            }
        } else if (!isInAir && currPunchNum < 0){
            currCrouchNum = -1;
            crouchingTimer.stop();
        }
    }

    private void updateStandingAnim() {
        if (!isInAir && currCrouchNum >= 0 && currPunchNum < 0) {
            setTexture("img/character/character_crouch" + (currCrouchNum + 1) + ".png");
            --currCrouchNum;
        } else if (currPunchNum < 0  && !isInAir) {
            setTexture("img/character/character_idle.png");
            standingTimer.stop();
        }
    }

    private void updatePunchingAnim() {
        if (currPunchNum == 2) {
            if (isInAir) {
                setTexture("img/character/character_jump_loop.png");
            } else if (currCrouchNum >= 0) {
                setTexture("img/character/character_crouch" + (currCrouchNum + 1) + ".png");
            } else {
                setTexture("img/character/character_idle.png");
            }
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

    private void updateFlinchingAnim() {
        if (currFlinchNum == 2) {
            if (isInAir) {
                setTexture("img/character/character_jump_loop.png");
            } else if (currCrouchNum >= 0) {
                setTexture("img/character/character_crouch" + (currCrouchNum + 1) + ".png");
            } else {
                setTexture("img/character/character_idle.png");
            }
            currFlinchNum = -1;
            flinchTimer.stop();
        } else if (currFlinchNum >= 0) {
            if (isInAir) {
                //setTexture(new Texture("img/character/character_jumping_jab" + (currFlinchNum + 1) + ".png"));
                ++currFlinchNum;
            } else if (currCrouchNum >= 0) {
                setTexture("img/character/character_crouching_jab" + (currFlinchNum + 1) + ".png");
                ++currFlinchNum;
            } else {
                setTexture("img/character/character_standing_damage_high" + (currFlinchNum + 1) + ".png");
                ++currFlinchNum;
            }
        } else {
            flinchTimer.stop();
        }
    }

    private void updateCharacterSize() {
        Texture newTexture = new Texture(characterImagePath);
        float widthDiffernece = 0;
        if (isFacingLeft) {
            widthDiffernece = getWidth() - newTexture.getWidth() * CHARACTER_SCALE;
        }
        //Gdx.app.log("Width Difference", "" + widthDiffernece);
        setSize(newTexture.getWidth() * CHARACTER_SCALE, newTexture.getHeight() * CHARACTER_SCALE);
        World world = characterBody.getWorld();
        Vector2 velocity = characterBody.getLinearVelocity();
        world.destroyBody(characterBody);
        BodyDef box = new BodyDef();
        box.type = BodyDef.BodyType.DynamicBody;
        // make sure foot placement is constant for different animations
        if (currFlinchNum >= 0) {
            if (!isFacingLeft) {
                box.position.set(getX() - Gdx.graphics.getWidth() / 2 + getWidth() / 2 + widthDiffernece, getY() - Gdx.graphics.getHeight() / 2 + getHeight() / 2);
            } else {
                box.position.set(getX() - Gdx.graphics.getWidth() / 2 + getWidth() / 2, getY() - Gdx.graphics.getHeight() / 2 + getHeight() / 2);
            }
        } else {
            if (isFacingLeft) {
                box.position.set(getX() - Gdx.graphics.getWidth() / 2 + getWidth() / 2 + widthDiffernece, getY() - Gdx.graphics.getHeight() / 2 + getHeight() / 2);
            } else {
                box.position.set(getX() - Gdx.graphics.getWidth() / 2 + getWidth() / 2, getY() - Gdx.graphics.getHeight() / 2 + getHeight() / 2);
            }
        }
        // make sure that character didn't fall of the map
        if (box.position.x < -Gdx.graphics.getWidth() / 2) {
            box.position.set(0, box.position.y);
        } else if (getStage() != null) {
            OrthographicCamera cam = (OrthographicCamera)getStage().getCamera();
            if (box.position.x > -Gdx.graphics.getWidth() / 2 + cam.viewportWidth * cam.zoom - getWidth()) {
                box.position.set(-Gdx.graphics.getWidth() / 2 + cam.viewportWidth * cam.zoom - getWidth() - 0, box.position.y);
            }
        }
        box.fixedRotation = true;
        box.linearVelocity.set(velocity);
        characterBody = world.createBody(box);
        characterBody.setUserData(this);
        PolygonShape rectangle = new PolygonShape();
        Json json = new Json();
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
                if (isFacingLeft) {
                    xVal *= -1;
                }
                Vector2 verticie = new Vector2(xVal, yVal);
                verticies[j] = verticie;
                verticieJSON = verticieJSON.next();
            }
            rectangle.set(verticies);
            FixtureDef boxFixture = new FixtureDef();
            boxFixture.shape = rectangle;
            boxFixture.density = .0001f;
            boxFixture.friction = 2f;
            boxFixture.restitution = 0f;
            if (verticieJSON != null) {
                boxFixture.filter.categoryBits = CATEGORY_ARM;
                boxFixture.filter.maskBits = -1;
                boxFixture.isSensor = true;
            } else {
                boxFixture.filter.categoryBits = CATEGORY_CHARACTER;
                boxFixture.filter.maskBits = CATEGORY_ARM | GameScreen.CATEGORY_SCENERY;
            }
            Fixture fixture = characterBody.createFixture(boxFixture);
            if (verticieJSON != null) {
                fixture.setUserData("jab");
            } else {
                fixture.setUserData("");
            }
        }
        rectangle.dispose();
    }

    @Override
    public void act(float delta) {
        // If not current player, find and attack them
        if (!isPossessed) {
            World world = characterBody.getWorld();
            Array<Body> bodies = new Array<Body>(world.getBodyCount());
            world.getBodies(bodies);
            for (Body body : bodies) {
                if (body.getUserData() instanceof Character) {
                    Character player = ((Character)body.getUserData());
                    if (player.isPossessed) {
                        if (player.getX() > getX()) {
                            setFlip(false, false);
                            if (player.getX() - getX() < 12 * CHARACTER_SCALE + getWidth()) {
                               // jab();
                                setIsMovingRight(false);
                            } else {
                                setIsMovingRight(true);
                            }
                        } else {
                            setFlip(true, false);
                            if (this.getX() - player.getX() < 12 * CHARACTER_SCALE + player.getWidth()) {
                              //  jab();
                                setIsMovingLeft(false);
                            } else {
                                setIsMovingLeft(true);
                            }
                        }
                        break;
                    }
                }
            }
        }
        float xVal = 0, yVal = 0;
        if (currCrouchNum < 0 && currPunchNum < 0) {
            if (isMovingLeft) {
                xVal = -500 * characterBody.getMass();
            } else if (isMovingRight) {
                xVal = 500 * characterBody.getMass();
            }
        }
        // handle jump mechanics
        if (isInAir) {
            xVal /= 10;
            if (characterBody.getPosition().y < -Gdx.graphics.getHeight() / 2 + getHeight() / 2 + 8 * CHARACTER_SCALE) {
                if (characterBody.getLinearVelocity().y < 1) {
                    if (characterBody.getPosition().y < -Gdx.graphics.getHeight() / 2 + getHeight() / 2 + .33f * CHARACTER_SCALE) {
                        isInAir = false;
                        setTexture("img/character/character_idle.png");
                    } else {
                        setTexture("img/character/character_jump_end.png");
                    }
                } else {
                    //characterBody.applyLinearImpulse(new Vector2(0, 30000000000000000000f), characterBody.getWorldCenter(), true);
                }
            } else if (currPunchNum < 0) {
                setTexture("img/character/character_jump_loop.png");
            }
        }
        characterBody.applyForceToCenter(xVal, yVal, true);
        super.setPosition(characterBody.getPosition().x + Gdx.graphics.getWidth() / 2 - getWidth() / 2, characterBody.getPosition().y + Gdx.graphics.getHeight() / 2 - getHeight() / 2);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        characterImage.getTexture().dispose();
        characterImage.setTexture(new Texture(characterImagePath));
        if (isFacingLeft) {
            characterImage.setFlip(true, false);
        } else {
            characterImage.setFlip(false, false);
        }
        batch.draw(characterImage, getX(), getY(), getWidth(), getHeight());
        //batch.draw(healthBar, getX(), getY() + getHeight(), 15 * CHARACTER_SCALE, 10);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        updateCharacterSize();
    }

    public void setTexture(String internalFilePath) {
        if (!characterImagePath.equals(internalFilePath)) {
            characterImagePath = internalFilePath;
            updateCharacterSize();
        }
    }

    public void setFlip(boolean x, boolean y) {
        isFacingLeft = x;
    }

    public boolean getIsPossessed() {
        return isPossessed;
    }

    public void setIsPossessed(boolean val) {
        isPossessed = val;
    }

    @Override
    public String toString() {
        return "Character";
    }
}
