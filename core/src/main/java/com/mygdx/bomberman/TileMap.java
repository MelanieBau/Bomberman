package com.mygdx.bomberman;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.bomberman.jsonloaders.LevelJson;

public class TileMap {

    public static final int TILE_SIZE = 64;
    public static final int DESTRUCTIBLE_TILE_ID = 6;
    int width; //Dimensiones del mapa
    int height;
    byte tiles[][]; // Matriz de Tiles
    AssetManager manager;
    SpriteBatch batch;

    public int scrollX;

    public TileMap(AssetManager manager, SpriteBatch batch) {
        this.manager = manager;
        this.batch = batch;
    }

    void loadFromLevel(LevelJson l) {
        // Load from json file
        width = l.getMapWidth();
        height = l.getMapHeight();

        tiles = new byte[height][];

        for (int i = 0; i < height; i++) {
            tiles[i] = new byte[width];
        }

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                tiles[i][j] = l.getTileMap()[i][j];
            }
        }
    }

    // Old render with color squares
    public void render(ShapeRenderer shapeRenderer) {

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int j = 0; j < height; j++)
            for (int i = 0; i < width; i++) {
                if (tiles[j][i] != 0) {
                    shapeRenderer.setColor(Color.OLIVE);
                    shapeRenderer.rect(TILE_SIZE * i - scrollX, TILE_SIZE * j, TILE_SIZE, TILE_SIZE);
                    shapeRenderer.setColor(Color.FIREBRICK);
                    shapeRenderer.rect(TILE_SIZE * i + 6 - scrollX, TILE_SIZE * j + 6, TILE_SIZE - 12, TILE_SIZE - 12);
                }
            }
        shapeRenderer.end();
    }

    public void render() {

        batch.begin();


        //Dibuja el fondo de mi pantalla
        Texture bgTexture = manager.get("bomberman.png", Texture.class);

        // scroll
        int bgWidth = bgTexture.getWidth();
        int bgHeight = bgTexture.getHeight();
        int scrollXPos = 0 - ((scrollX / 2) % bgWidth);

        batch.draw(bgTexture, scrollXPos, 0, bgWidth, bgHeight, 0, 0, bgWidth, bgHeight, false, false);
        batch.draw(bgTexture, scrollXPos + bgWidth, 0, bgWidth, bgHeight, 0, 0, bgWidth, bgHeight, false, false);


        // Tile map
        for (int j = 0; j < height; j++)
            for (int i = 0; i < width; i++) {
                if (tiles[j][i] != 0) {
                    batch.draw(manager.get("tiles/" + tiles[j][i] + ".png", Texture.class), TILE_SIZE * i - scrollX, TILE_SIZE * j, TILE_SIZE, TILE_SIZE);
                }
            }
        batch.end();
    }


    //Devuelve true si el tile en esa posición es sólido, es decir, no se puede atravesar
    boolean isSolid(int x, int y) {
        int mapX = x / TILE_SIZE;
        int mapY = y / TILE_SIZE;

        if (mapX < 0) mapX = 0;
        if (mapY < 0) mapY = 0;
        if (mapX >= width) mapX = width - 1;
        if (mapY >= height) mapY = height - 1;

        return tiles[mapY][mapX] != 0 && tiles[mapY][mapX] < 17;
    }

    int nearestFloor(int x, int y) {
        int mapX = x / TILE_SIZE;
        int mapY = y / TILE_SIZE;

        if (mapX < 0) mapX = 0;
        if (mapY < 0) mapY = 0;
        if (mapX >= width) mapX = width - 1;
        if (mapY >= height) mapY = height - 1;

        while (mapY < height && (tiles[mapY][mapX] == 0 || tiles[mapY][mapX] >= 17)) {
            mapY++;
        }

        if (mapY >= height) {
            return 9999;
        } else {
            return mapY * TILE_SIZE;
        }
    }

    int nearestCeiling(int x, int y) {
        int mapX = x / TILE_SIZE;
        int mapY = y / TILE_SIZE;

        if (mapX < 0) mapX = 0;
        if (mapY < 0) mapY = 0;
        if (mapX >= width) mapX = width - 1;
        if (mapY >= height) mapY = height - 1;

        while (mapY >= 0 && (tiles[mapY][mapX] == 0 || tiles[mapY][mapX] >= 17)) {
            mapY--;
        }

        if (mapY < 0) {
            return -9999;
        } else {
            return ((mapY + 1) * TILE_SIZE) - 1;
        }
    }


    //Devuelve true si el personaje puede caminar sobre ese tile
    public boolean isWalkable(int tileX, int tileY) {
        if (tileX < 0 || tileY < 0 || tileX >= width || tileY >= height) return false;
        return tiles[tileY][tileX] == 0; // 0 = camino libre
    }


    //Destruye un tile si está dentro del mapa y es destructible
    public void destroyTile(int tileX, int tileY) {
        if (tileX >= 0 && tileX < width && tileY >= 0 && tileY < height) {
            if (tiles[tileY][tileX] == DESTRUCTIBLE_TILE_ID) {
                tiles[tileY][tileX] = 0;
            }
        }
    }
}
