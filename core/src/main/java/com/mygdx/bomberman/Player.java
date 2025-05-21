package com.mygdx.bomberman;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Player extends WalkingCharacter {

    private static final float INVULNERABILITY_DURATION = 20f;
    AssetManager manager;
    ButtonLayout joypad;

    Animation<TextureRegion> walkLeft, walkRight, walkUp, walkDown;
    Animation<TextureRegion> deathAnim;
    TextureRegion currentFrame;
    float stateTime;

    boolean deathAnimFinished = false;

    //Variables para gestionar el movimiento por casillas, velocidad, dirección, estado de muerte y animaciones.
    int targetTileX, targetTileY;
    boolean moving = false;
    boolean dying = false;
    float moveSpeed = 200f;
    float invulnerability = 0f;
    String lastDirection = "Down";

    public Player(AssetManager manager) {
        setBounds(64, 64, 65, 65);
        this.manager = manager;

        walkLeft = loadAnimation("Left");
        walkRight = loadAnimation("Right");
        walkUp = loadAnimation("Up");
        walkDown = loadAnimation("Forward");

        // Cargar animación de muerte
        TextureRegion[] deathFrames = new TextureRegion[4];
        for (int i = 0; i < 4; i++) {
            deathFrames[i] = new TextureRegion(manager.get("player/dead" + i + ".png", Texture.class));
        }
        deathAnim = new Animation<>(0.2f, deathFrames);

        currentFrame = walkDown.getKeyFrame(0);
        stateTime = 0f;

        targetTileX = getTileX();
        targetTileY = getTileY();
        setPosition(targetTileX * TileMap.TILE_SIZE, targetTileY * TileMap.TILE_SIZE);
    }

    private Animation<TextureRegion> loadAnimation(String dir) {
        TextureRegion[] frames = new TextureRegion[3];
        for (int i = 0; i < 3; i++) {
            frames[i] = new TextureRegion(manager.get("player/" + dir + i + ".png", Texture.class));
        }
        return new Animation<>(0.15f, frames);
    }

    public void setJoypad(ButtonLayout joypad) {
        this.joypad = joypad;
    }

    public int getTileX() {
        return (int)(getX() / TileMap.TILE_SIZE);
    }

    public int getTileY() {
        return (int)(getY() / TileMap.TILE_SIZE);
    }


    //Si está muerto, solo actualiza el stateTime para avanzar la animación de muerte
    //Si se mueve Calcula la distancia hacia la casilla objetivo
    //Actualiza la animación dependiendo de la dirección del personaje
    @Override
    public void act(float delta) {
        super.act(delta);

        if (dead) {
            stateTime += delta;
            return;
        }

        if (invulnerability > 0f) invulnerability -= delta;
        stateTime += delta;

        if (!moving) {
            if (joypad.isPressed("Right") && map.isWalkable(targetTileX + 1, targetTileY)) {
                targetTileX += 1;
                lastDirection = "Right";
                moving = true;
            } else if (joypad.isPressed("Left") && map.isWalkable(targetTileX - 1, targetTileY)) {
                targetTileX -= 1;
                lastDirection = "Left";
                moving = true;
            } else if (joypad.isPressed("Up") && map.isWalkable(targetTileX, targetTileY - 1)) {
                targetTileY -= 1;
                lastDirection = "Up";
                moving = true;
            } else if (joypad.isPressed("Down") && map.isWalkable(targetTileX, targetTileY + 1)) {
                targetTileY += 1;
                lastDirection = "Down";
                moving = true;
            }

            if (joypad.consumePush("Bomba")) {
                placeBomb();
            }

            if (moving) stateTime = 0f;
        }

        if (moving) {
            float targetX = targetTileX * TileMap.TILE_SIZE;
            float targetY = targetTileY * TileMap.TILE_SIZE;
            float dx = targetX - getX();
            float dy = targetY - getY();
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            float step = moveSpeed * delta;

            if (dist <= step) {
                setPosition(targetX, targetY);
                moving = false;
            } else {
                float ratio = step / dist;
                setPosition(getX() + dx * ratio, getY() + dy * ratio);
            }
        }

        if (moving) {
            switch (lastDirection) {
                case "Up": currentFrame = walkDown.getKeyFrame(stateTime, true); break;
                case "Down": currentFrame = walkUp.getKeyFrame(stateTime, true); break;
                case "Left": currentFrame = walkLeft.getKeyFrame(stateTime, true); break;
                case "Right": currentFrame = walkRight.getKeyFrame(stateTime, true); break;
            }
        } else {
            switch (lastDirection) {
                case "Up": currentFrame = walkDown.getKeyFrame(0); break;
                case "Down": currentFrame = walkUp.getKeyFrame(0); break;
                case "Left": currentFrame = walkLeft.getKeyFrame(0); break;
                case "Right": currentFrame = walkRight.getKeyFrame(0); break;
            }
        }
    }


    //Si el muñeco esta muerte, dibuja las animaciones de muerte
    //Si está vivo y visible, dibuja el currentFrame en su posición
    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if (dead) {
            TextureRegion frame = deathAnim.getKeyFrame(stateTime, false);
            batch.draw(frame, getX() - map.scrollX, getY(), getWidth(), getHeight());

            // Si ya terminó la animación de muerte
            if (deathAnim.isAnimationFinished(stateTime)) {
                deathAnimFinished = true;
            }
            return;
        }

        if (invulnerability > 0f && (int)(invulnerability / 0.125f) % 2 == 0) return;

        batch.draw(currentFrame, getX() - map.scrollX, getY(), getWidth(), getHeight());
    }


    //Marca al jugador como muerto y reinicia el temporizador de animación
    @Override
    public void kill() {
        if (!dead) {
            super.kill();
            stateTime = 0f;
        }
    }

    public void getInvulnerability() {
        invulnerability = INVULNERABILITY_DURATION;
    }

    public boolean hasInvulnerability() {
        return invulnerability > 0f;
    }

    public void drawDebug(ShapeRenderer shapes) {
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(Color.NAVY);
        shapes.rect(getX() - map.scrollX, getY(), getWidth(), getHeight());
        shapes.end();
    }

    //Calcula la posición en casillas del jugador
    //Comprueba si ya hay una bomba en esa casilla recorriendola y si no hay, crea una nueva
    public void placeBomb() {
        int tileX = getTileX();
        int tileY = getTileY();

        for (Actor actor : getStage().getActors()) {
            if (actor instanceof Bomba) {
                Bomba b = (Bomba) actor;
                int bx = (int)(b.getX() / TileMap.TILE_SIZE);
                int by = (int)(b.getY() / TileMap.TILE_SIZE);
                if (bx == tileX && by == tileY) return;
            }
        }

        Bomba b = new Bomba(tileX, tileY, manager);
        b.setMap(map);
        getStage().addActor(b);
    }


    //Esto indica si la animación de muerte ya terminó(para que reinicie el juego)
    public boolean isDeathAnimationFinished() {
        return deathAnimFinished;
    }
}
