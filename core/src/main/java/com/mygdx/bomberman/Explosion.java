package com.mygdx.bomberman;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Explosion extends GameEntity {

    private Texture texture;
    private TileMap map;
    private float aliveTime = 0.4f; // duración de la explosión
    private float timer = 0;

    public Explosion(float x, float y, Texture texture, TileMap map) {
        this.texture = texture;
        this.map = map;
        setBounds(x, y, TileMap.TILE_SIZE, TileMap.TILE_SIZE);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        timer += delta;

        // Detectar colisión con enemigos vivos y con el jugador
        for (Actor actor : getStage().getActors()) {
            if (actor instanceof Dino) {
                Dino dino = (Dino) actor;
                if (!dino.isDead() && overlaps(dino)) {
                    dino.kill();
                }
            } else if (actor instanceof Player) {
                Player player = (Player) actor;
                if (!player.isDead() && !player.hasInvulnerability() && overlaps(player)) {
                    player.kill();
                }
            }
        }

        // Eliminar la explosión tras 0.4 segundos
        if (timer >= aliveTime) {
            remove();
        }
    }

    private boolean overlaps(Actor other) {
        return getX() < other.getX() + other.getWidth() &&
            getX() + getWidth() > other.getX() &&
            getY() < other.getY() + other.getHeight() &&
            getY() + getHeight() > other.getY();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (texture != null) {
            batch.draw(texture, getX() - map.scrollX, getY(), getWidth(), getHeight());
        }
    }
}
