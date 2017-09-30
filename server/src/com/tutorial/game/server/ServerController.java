package com.tutorial.game.server;

import com.tutorial.game.characters.Character;

import java.util.UUID;

/**
 * Created by ryanl on 9/30/2017.
 */

/*
1 = jump
2 = move right
3 = stop moving right
4 = move left
5 = stop moving left
6 = crouch
7 = stop crouching
8 = jab
 */

public class ServerController {

    private Character player;
    private UUID id;

    public ServerController(Character character, UUID uuid) {
        super();
        possessCharacter(character);
        id = UUID.fromString(uuid.toString());
    }

    public void possessCharacter(Character character) {
        player = character;
        player.setIsPossessed(true);
    }

    public void processInput(int input) {
        switch (input) {
            case 1:
                player.jump();
                break;
            case 2:
                player.setIsMovingRight(true);
                break;
            case 3:
                player.setIsMovingRight(false);
                break;
            case 4:
                player.setIsMovingLeft(true);
                break;
            case 5:
                player.setIsMovingLeft(false);
                break;
            case 6:
                player.setIsCrouching(true);
                break;
            case 7:
                player.setIsCrouching(false);
                break;
            case 8:
                player.jab();
                break;
            default:
                break;
        }
    }

    public UUID getUUID() {
        return id;
    }
}
