package com.tutorial.game.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Timer;
import com.tutorial.game.screens.GameScreen;

/**
 * Created by ryanl on 8/6/2017.
 */

public class Character extends Actor {

    public static final short CATEGORY_CHARACTER = 0x0001;
    public static final short CATEGORY_ARM = 0x0002;
    private final float WORLD_WIDTH = 100;
    private final float WORLD_HEIGHT = 100 * 9 / 16;
    protected final float CHARACTER_SCALE = 2f / 17;
    protected String characterImagePath;
    protected Body characterBody;
    protected int health;
    protected boolean isPossessed;
    protected boolean isMovingLeft;
    protected boolean isMovingRight;
    protected boolean isFacingRight;
    protected boolean isInAir;
    protected boolean isDead;
    protected int currWalkNum;
    protected int currCrouchNum;
    protected int currPunchNum;
    protected int currFlinchNum;
    protected Timer walkingTimer;
    protected Timer crouchingTimer;
    protected Timer standingTimer;
    protected Timer punchingTimer;
    protected Timer flinchTimer;
    protected boolean needsUpdate;

    public Character(World world) {
        super();
        characterImagePath = "";
        isMovingLeft = false;
        isMovingRight = false;
        isInAir = false;
        isPossessed = false;
        isDead = false;
        needsUpdate = false;
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
        }, 0, .125f);
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
        }, 0, .25f);
        flinchTimer.stop();
        BodyDef box = new BodyDef();
        box.type = BodyDef.BodyType.DynamicBody;
        box.position.set(getX() + getWidth() / 2, getY() + getHeight() / 2);
        box.fixedRotation = true;
        characterBody = world.createBody(box);
        characterBody.setUserData(this);
        health = 100;
    }

    public void jump() {
        if (!isDead && !isInAir && currFlinchNum < 0) {
            //characterBody.setLinearVelocity(0, 500000f);
            characterBody.applyForceToCenter(0, 10000f * characterBody.getMass(), true);
            isInAir = true;
            currCrouchNum = -1;
            crouchingTimer.stop();
            standingTimer.stop();
        }
    }

    public void jab() {
        if (!isDead && currPunchNum < 0 && currFlinchNum < 0) {
            currPunchNum = 0;
            punchingTimer.start();
        }
    }

    public void flinch() {
        float knockbackForce = 1000000f * characterBody.getMass();
        if (isFacingRight) {
            knockbackForce *= - 1;
        }
        characterBody.applyForceToCenter(knockbackForce, 0, true);
        if (!isDead) {
            currPunchNum = -1;
            punchingTimer.stop();
            currFlinchNum = 1;
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

    public void setIsCrouching(boolean val)
    {
        if (!isDead) {
            if (!isInAir && currPunchNum < 0 && val) {
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
                    currWalkNum = (currWalkNum + 1) % 6;
                } else {
                    currWalkNum = -1;
                    walkingTimer.stop();
                }
            }
        }
    }

    protected void updateCrouchingAnim() {
        if (!isDead) {
            if (!isInAir && currCrouchNum >= 0 && currPunchNum < 0) {
                if (currCrouchNum < 2) {
                    ++currCrouchNum;
                } else {
                    crouchingTimer.stop();
                }
            } else if (!isInAir && currPunchNum < 0) {
                currCrouchNum = -1;
                crouchingTimer.stop();
            }
        }
    }

    protected void updateStandingAnim() {
        if (!isDead) {
            if (!isInAir && currCrouchNum >= 0 && currPunchNum < 0) {
                --currCrouchNum;
            } else if (currPunchNum < 0 && !isInAir) {
                standingTimer.stop();
            }
        }
    }

    protected void updatePunchingAnim() {
        if (!isDead) {
            if (currPunchNum == 2) {
                if (isInAir) {
                } else if (currCrouchNum >= 0) {
                } else {
                }
                currPunchNum = -1;
                punchingTimer.stop();
            } else if (currPunchNum >= 0) {
                if (isInAir) {
                    ++currPunchNum;
                } else if (currCrouchNum >= 0) {
                    ++currPunchNum;
                } else {
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
                } else {
                }
                currFlinchNum = -1;
                flinchTimer.stop();
            } else if (currFlinchNum >= 0) {
                if (isInAir) {
                    ++currFlinchNum;
                } else if (currCrouchNum >= 0) {
                    ++currFlinchNum;
                } else {
                    ++currFlinchNum;
                }
            } else {
                flinchTimer.stop();
            }
        }
    }

    protected void updateCharacterSize() {
        if (!characterBody.getWorld().isLocked()) {
            needsUpdate = false;
            World world = characterBody.getWorld();
            Vector2 velocity = characterBody.getLinearVelocity();
            world.destroyBody(characterBody);
            BodyDef box = new BodyDef();
            box.type = BodyDef.BodyType.DynamicBody;
            // make sure foot placement is constant for different animations
            if (currPunchNum < 0 && (currWalkNum >= 0)) {
                if (isFacingRight) {
                    box.position.set(getX() + getWidth() / 2, getY() + getHeight() / 2);
                } else {
                    box.position.set(getX() + getWidth() / 2, getY() + getHeight() / 2);
                }
            } else {
                if (!isFacingRight) {
                    box.position.set(getX() + getWidth() / 2, getY() + getHeight() / 2);
                } else {
                    box.position.set(getX() + getWidth() / 2, getY() + getHeight() / 2);
                }
            }
            // make sure that character didn't fall of the map
            if (box.position.x < 0) {
                box.position.set(0, box.position.y);
            } else if (box.position.x > WORLD_WIDTH - getWidth()) {
                box.position.set(WORLD_WIDTH - getWidth(), box.position.y);
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
        } else {
            needsUpdate = true;
        }
    }

    @Override
    public void act(float delta) {
        if (!isDead) {
            float xVal = 0, yVal = 0;
            // If not current player, find and attack them
            if (!isPossessed) {
                World world = characterBody.getWorld();
                Array<Body> bodies = new Array<Body>(world.getBodyCount());
                world.getBodies(bodies);
                for (Body body : bodies) {
                    if (body.getUserData() instanceof Character) {
                        Character player = ((Character) body.getUserData());
                        if (player.isPossessed) {
                            if (!player.isDead) {
                                if (player.getX() > getX()) {
                                    setFlip(false, false);
                                    if (player.getX() - getX() < 16 * CHARACTER_SCALE + getWidth() && Math.abs(player.getY() - getY()) <= getHeight()) {
                                        jab();
                                        setIsMovingRight(false);
                                    } else {
                                        setIsMovingRight(true);
                                    }
                                } else {
                                    setFlip(true, false);
                                    if (this.getX() - player.getX() < 16 * CHARACTER_SCALE + player.getWidth() && Math.abs(player.getY() - getY()) <= getHeight()) {
                                        jab();
                                        setIsMovingLeft(false);
                                    } else {
                                        setIsMovingLeft(true);
                                    }
                                }
                                if (player.getY() > getY() + getHeight() && body.getLinearVelocity().y > 0) {
                                    // because jump force will be overriden by act before it has a chance to do anything
                                    if (!isInAir) {
                                        yVal = 10000f * characterBody.getMass();
                                    }
                                    jump();
                                }
                            } else {
                                setIsMovingRight(false);
                                setIsMovingLeft(false);
                            }
                            break;
                        }
                    }
                }
            }
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
            // handle jump mechanics
            if (isInAir) {
                xVal /= 10;
                if (characterBody.getPosition().y < getHeight() / 2 + 8 * CHARACTER_SCALE) {
                    if (characterBody.getLinearVelocity().y < 1) {
                        if (characterBody.getPosition().y < getHeight() / 2 + .33f * CHARACTER_SCALE) {
                            isInAir = false;
                        } else {
                        }
                    }
                } else if (currPunchNum < 0 && currFlinchNum < 0) {
                }
            }
            characterBody.applyForceToCenter(xVal, yVal, true);
        }
        if (needsUpdate) {
            updateCharacterSize();
        }
        super.setPosition(characterBody.getPosition().x - getWidth() / 2, characterBody.getPosition().y - getHeight() / 2);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        updateCharacterSize();
    }

    public void setFlip(boolean x, boolean y) {
        if (isFacingRight == x) {
            isFacingRight = x;
            updateCharacterSize();
        }
    }

    public boolean getIsPossessed() {
        return isPossessed;
    }

    public void setIsPossessed(boolean val) {
        isPossessed = val;
    }

    @Override
    public String toString() {
        return "x=" + getX() + "&y=" + getY() + "&health=" + health + "&imageName=" + characterImagePath;
    }

    public boolean isFacingRight() {
        return isFacingRight;
    }

    public void changeHealth(int amount) {
        health += amount;
        if (health <= 0) {
            health = 0;
            isDead = true;
            walkingTimer.stop();
            crouchingTimer.stop();
            standingTimer.stop();
            punchingTimer.stop();
            flinchTimer.stop();
        }
    }
}
