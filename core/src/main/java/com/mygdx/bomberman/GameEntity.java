package com.mygdx.bomberman;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class GameEntity extends Actor
{

    public Vector2 speed;
    protected TileMap map;

    public GameEntity()
    {
        speed = new Vector2(0,0);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        moveBy(delta*speed.x, delta* speed.y);
    }

    public Vector2 getSpeed() {
        return speed;
    }

    public void setMap(TileMap map) {
        this.map = map;
    }

}
