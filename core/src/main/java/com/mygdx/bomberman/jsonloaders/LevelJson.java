package com.mygdx.bomberman.jsonloaders;

import java.util.ArrayList;

public class LevelJson
{
    private int mapWidth;
    private int mapHeight;

    private byte tileMap[][];

    private ArrayList<EnemyJson> enemies;
    private ArrayList<CollectableJson> collectables;

    public LevelJson()
    {
    }

    public LevelJson(int mapWidth, int mapHeight, byte[][] tileMap)
    {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.tileMap = tileMap;

        enemies = new ArrayList<>();
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public void setMapWidth(int mapWidth) {
        this.mapWidth = mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public void setMapHeight(int mapHeight) {
        this.mapHeight = mapHeight;
    }

    public byte[][] getTileMap() {
        return tileMap;
    }

    public void setTileMap(byte[][] tileMap) {
        this.tileMap = tileMap;
    }

    public ArrayList<EnemyJson> getEnemies() {
        return enemies;
    }

    public void setEnemies(ArrayList<EnemyJson> enemies) {
        this.enemies = enemies;
    }

    public void addEnemy(EnemyJson e)
    {
        enemies.add(e);
    }

    public ArrayList<CollectableJson> getCollectables() {
        return collectables;
    }

    public void setCollectables(ArrayList<CollectableJson> collectables) {
        this.collectables = collectables;
    }

}
