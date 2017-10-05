package com.tutorial.game.characters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

import static com.tutorial.game.constants.Constants.CHARACTER_SCALE;

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
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(characterImage, getX(), getY(), getWidth(), getHeight());
        batch.draw(healthBar, getX() + getWidth() / 2 - 20 * CHARACTER_SCALE / 2, getY() + getHeight(), 20 * CHARACTER_SCALE * health / 100, 5 * CHARACTER_SCALE);
    }

    @Override
    public void setFlip(boolean x, boolean y) {
        super.setFlip(x, y);
        characterImage.setFlip(x, y);
    }

    @Override
    protected void setTexture(String internalFilePath) {
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
}
