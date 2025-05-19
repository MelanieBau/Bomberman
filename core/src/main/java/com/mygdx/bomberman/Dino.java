package com.mygdx.bomberman;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.Random;

public class Dino extends WalkingCharacter {

    Animation<TextureRegion> walk;
    Animation<TextureRegion> death;
    TextureRegion currentFrame;
    float stateTime;

    int tileX, tileY;
    int dirX = 0, dirY = 0;
    float moveTimer = 0;
    float moveSpeed = 200f;
    Random random = new Random();

    public Dino(float x, float y, AssetManager manager, Player player) {
        setBounds(x, y, 48, 48);

        // Animación de caminar
        TextureRegion[] frames = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            frames[i] = new TextureRegion(manager.get("dino/Walk (" + (i + 1) + ").png", Texture.class));
        }
        walk = new Animation<>(0.1f, frames);

        // Animación de morir
        TextureRegion[] deathFrames = new TextureRegion[8];
        for (int i = 0; i < 8; i++) {
            deathFrames[i] = new TextureRegion(manager.get("dino/Dead (" + (i + 1) + ").png", Texture.class));
        }
        death = new Animation<>(0.1f, deathFrames);

        currentFrame = walk.getKeyFrame(0);
        stateTime = 0f;

        tileX = (int) (x / TileMap.TILE_SIZE);
        tileY = (int) (y / TileMap.TILE_SIZE);

        chooseRandomDirection();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;

        if (dead) return;  // No moverse si está muerto

        moveTimer += delta;

        if (moveTimer > 0.6f) {
            moveTimer = 0;
            int nextX = tileX + dirX;
            int nextY = tileY + dirY;

            if (map.isWalkable(nextX, nextY)) {
                tileX = nextX;
                tileY = nextY;
                setPosition(tileX * TileMap.TILE_SIZE, tileY * TileMap.TILE_SIZE);
            } else {
                chooseRandomDirection();
            }
        }

        currentFrame = walk.getKeyFrame(stateTime, true);
    }

    private void chooseRandomDirection() {
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        int index = random.nextInt(directions.length);
        dirX = directions[index][0];
        dirY = directions[index][1];
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if (dead) {
            TextureRegion deadFrame = death.getKeyFrame(stateTime, false);
            batch.draw(deadFrame, getX() - map.scrollX, getY(), getWidth(), getHeight());
        } else {
            batch.draw(currentFrame, getX() - map.scrollX, getY(), getWidth(), getHeight());
        }
    }
}
