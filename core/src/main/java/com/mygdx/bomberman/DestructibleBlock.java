package com.mygdx.bomberman;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class DestructibleBlock extends GameEntity {

    private Texture texture;
    private boolean destroyed = false;

    public DestructibleBlock(float x, float y, Texture texture) {
        this.texture = texture;
        setBounds(x, y, TileMap.TILE_SIZE, TileMap.TILE_SIZE);
    }


    //Dibuja los bloques que se pueden destruir con el fuego
    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!destroyed) {
            batch.draw(texture, getX() - map.scrollX, getY(), getWidth(), getHeight());
        }
    }

    public void destroy() {
        destroyed = true;
        remove(); // Elimina del escenario visual
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}
