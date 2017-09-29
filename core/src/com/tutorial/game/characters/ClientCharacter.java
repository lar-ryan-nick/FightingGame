package com.tutorial.game.characters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by ryanwiener on 9/29/17.
 */

public class ClientCharacter extends Character implements Disposable {

    private Sprite characterImage;
    private Sprite healthBar;

    public ClientCharacter(World world) {
        super(world);
        characterImage = new Sprite(new Texture("img/character/character_idle.png"));
        healthBar = new Sprite(new Texture("img/square.png"));
        healthBar.setColor(0, 1, 0, 1);
        setTexture("img/character/character_idle.png");
    }

    @Override
    public void jump() {
        super.jump();
        if (!isDead && !isInAir && currFlinchNum < 0) {
            setTexture("img/character/character_jump_start.png");
        }
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
    protected void updateCharacterSize() {
        Texture newTexture = new Texture(characterImagePath);
        float widthDiffernece = 0;
        if (!isFacingRight) {
            widthDiffernece = getWidth() - newTexture.getWidth() * CHARACTER_SCALE;
        }
        //Gdx.app.log("Width Difference", "" + widthDiffernece);
        setSize(newTexture.getWidth() * CHARACTER_SCALE, newTexture.getHeight() * CHARACTER_SCALE);
        newTexture.dispose();
        super.updateCharacterSize();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!isDead) {
            // handle jump mechanics
            if (isInAir) {
                if (characterBody.getPosition().y < getHeight() / 2 + 8 * CHARACTER_SCALE) {
                    if (characterBody.getLinearVelocity().y < 1) {
                        if (characterBody.getPosition().y < getHeight() / 2 + .33f * CHARACTER_SCALE) {
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
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(characterImage, getX(), getY(), getWidth(), getHeight());
        batch.draw(healthBar, getX() + getWidth() / 2 - 20 * CHARACTER_SCALE / 2, getY() + getHeight(), 20 * CHARACTER_SCALE * health / 100, 5 * CHARACTER_SCALE);
    }

    private void setTexture(String internalFilePath) {
        if (!characterImagePath.equals(internalFilePath)) {
            characterImagePath = internalFilePath;
            updateCharacterSize();
            characterImage.getTexture().dispose();
            characterImage.setTexture(new Texture(characterImagePath));
        }
    }

    @Override
    public void dispose() {
        characterImage.getTexture().dispose();
        healthBar.getTexture().dispose();
    }

    @Override
    public void changeHealth(int amount) {
        super.changeHealth(amount);
        setTexture("img/character/character_ko.png");
    }
}
