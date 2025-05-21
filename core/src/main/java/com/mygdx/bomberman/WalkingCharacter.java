package com.mygdx.bomberman;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class WalkingCharacter extends GameEntity {

    protected boolean dead = false;

    public WalkingCharacter() {
        super();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        // Aquí no hay física, solo lógica de movimiento por casillas que va en Player o enemigos
    }

    @Override
    public void drawDebug(ShapeRenderer shapes) {
        super.drawDebug(shapes);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(Color.NAVY);
        shapes.rect(getX() - map.scrollX, getY(), getWidth(), getHeight());
        shapes.end();
    }

    //Cambia el valor a true si esta muerto
    public void kill() {
        dead = true;
    }


    //Método que devuelve el estado actual de dead. Sirve para comprobar si el personaje está muerto
    public boolean isDead() {
        return dead;
    }
}
