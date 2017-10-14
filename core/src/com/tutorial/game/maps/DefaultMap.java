package com.tutorial.game.maps;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import static com.tutorial.game.constants.Constants.CATEGORY_SCENERY;
import static com.tutorial.game.constants.Constants.WORLD_HEIGHT;
import static com.tutorial.game.constants.Constants.WORLD_WIDTH;

/**
 * Created by ryanwiener on 10/11/17.
 */

public class DefaultMap extends Map {

    //private Image backgroundImage;
    //private Image floorImage;

    public DefaultMap() {
        super();
        BodyDef floor = new BodyDef();
        floor.type = BodyDef.BodyType.StaticBody;
        floor.position.set(0, 0);
        EdgeShape line = new EdgeShape();
        line.set(0, 0, WORLD_WIDTH, 0);
        FixtureDef floorFixture = new FixtureDef();
        floorFixture.friction = 1f;
        floorFixture.shape = line;
        floorFixture.filter.categoryBits = CATEGORY_SCENERY;
        floorFixture.filter.maskBits = -1;
        getWorld().createBody(floor).createFixture(floorFixture);
        BodyDef leftWall = new BodyDef();
        leftWall.type = BodyDef.BodyType.StaticBody;
        leftWall.position.set(0, 0);
        line.set(0, 0, 0, WORLD_HEIGHT);
        FixtureDef wallFixture = new FixtureDef();
        wallFixture.friction = 0f;
        wallFixture.shape = line;
        wallFixture.filter.categoryBits = CATEGORY_SCENERY;
        wallFixture.filter.maskBits = -1;
        getWorld().createBody(leftWall).createFixture(wallFixture);
        BodyDef rightWall = new BodyDef();
        rightWall.type = BodyDef.BodyType.StaticBody;
        rightWall.position.set(0, 0);
        line.set(WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT);
        getWorld().createBody(rightWall).createFixture(wallFixture);
        line.dispose();
        /*
        backgroundImage = new Image(new Texture("img/GameBackgroundImage.jpg"));
        backgroundImage.setBounds(0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        //stage.addActor(backgroundImage);
        floorImage = new Image(new Texture("img/GameFloorImage.jpg"));
        floorImage.setBounds(0, 0, effectiveViewportWidth, 0);
        stage.addActor(floorImage);
        */
    }
}
