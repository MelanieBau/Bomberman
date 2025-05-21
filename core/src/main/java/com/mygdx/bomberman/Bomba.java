package com.mygdx.bomberman;

import com.badlogic.gdx.assets.AssetManager; // Para gestionar y cargar recursos como imágenes y sonidos
import com.badlogic.gdx.graphics.Texture;  //Para manejar texturas (imágenes) en pantalla
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;

public class Bomba extends GameEntity {

    private Texture texture;
    private TileMap map; // Referencia al mapa para saber dónde explotar
    private boolean exploded = false;  // Controla si ya explotó
    private AssetManager manager;

    public Bomba(int tileX, int tileY, AssetManager manager) {
        this.manager = manager;
        this.texture = manager.get("explosion/Bomba.png", Texture.class);

        float posX = tileX * TileMap.TILE_SIZE;
        float posY = tileY * TileMap.TILE_SIZE;


        // Hacemos la bomba más pequeña y centrada visualmente
        float size = TileMap.TILE_SIZE * 0.6f;
        float offset = (TileMap.TILE_SIZE - size) / 2f;

        this.setBounds(posX + offset, posY + offset, size, size);

        // Programar la explosión
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                explode();// Ejecutamos la explosión de la bomba
            }
        }, 2.5f);
    }

    public void setMap(TileMap map) {
        this.map = map;
    }

    // Dibujamos la bomba solo si no ha explotado
    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!exploded) {
            batch.draw(texture, getX() - map.scrollX, getY(), getWidth(), getHeight());
        }
    }

    private void explode() {
        exploded = true;

        //SONIDO DE EXPLOSIÓN
        if (manager.isLoaded("sound/explosion.mp3")) {
            manager.get("sound/explosion.mp3", com.badlogic.gdx.audio.Sound.class).play();
        }

        float centerX = getX();
        float centerY = getY();
        Stage stage = getStage();

        if (stage == null) return;

        // Centro
        addExplosionAt(centerX, centerY, "explosion/explosion_centro.png");

        // Izquierda
        addExplosionAt(centerX - TileMap.TILE_SIZE, centerY, "explosion/explosion_izquierda.png");

        // Derecha
        addExplosionAt(centerX + TileMap.TILE_SIZE, centerY, "explosion/explosion_derecha.png");

        // Arriba
        addExplosionAt(centerX, centerY + TileMap.TILE_SIZE, "explosion/explosion_horizontal.png");

        // Abajo
        addExplosionAt(centerX, centerY - TileMap.TILE_SIZE, "explosion/explosion_vertical.png");

        remove(); // Eliminar la bomba luego
    }

    private void addExplosionAt(float x, float y, String texturePath) {
        if (manager.isLoaded(texturePath)) {
            Texture tex = manager.get(texturePath, Texture.class);
            Explosion e = new Explosion(x, y, tex, map);
            getStage().addActor(e);
        } else {
            System.out.println("WARNING: Texture not loaded -> " + texturePath);
        }

        // Si hay un tile destructible, se borrará del mapa
        int tileX = (int) (x / TileMap.TILE_SIZE);
        int tileY = (int) (y / TileMap.TILE_SIZE);
        if (map != null && map.tiles != null) {
            if (map.tiles[tileY][tileX] == TileMap.DESTRUCTIBLE_TILE_ID) {
                map.tiles[tileY][tileX] = 0;
            }
        }
    }
}
