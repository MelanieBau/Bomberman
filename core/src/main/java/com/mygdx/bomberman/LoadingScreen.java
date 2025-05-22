package com.mygdx.bomberman;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class LoadingScreen implements Screen {

    PuigBros game;
    float loadProgress;

    LoadingScreen(PuigBros game)
    {
        this.game = game;
        AssetManager  manager = game.manager;

        // Add assets for loading

        // Tile principal indestructible
        manager.load("tiles/2.png", Texture.class);

        // Imagen de background
        manager.load("bombermann.png", Texture.class);

        // GUI
        manager.load("gui/Button-off.png", Texture.class);
        manager.load("gui/Button-on.png", Texture.class);
        manager.load("gui/Left-off.png", Texture.class);
        manager.load("gui/Left-on.png", Texture.class);
        manager.load("gui/Right-off.png", Texture.class);
        manager.load("gui/Right-on.png", Texture.class);
        manager.load("gui/Up-off.png", Texture.class);
        manager.load("gui/Up-on.png", Texture.class);
        manager.load("gui/Down-off.png", Texture.class);
        manager.load("gui/Down-on.png", Texture.class);
        manager.load("gui/Pause-off.png", Texture.class);
        manager.load("gui/Pause-on.png", Texture.class);
        manager.load("gui/B_0.png", Texture.class);
        manager.load("gui/B_1.png", Texture.class);

        //Bomba
        manager.load("explosion/Bomba.png", Texture.class);

        //Explosion
        manager.load("explosion/explosion_centro.png", Texture.class);
        manager.load("explosion/explosion_izquierda.png", Texture.class);
        manager.load("explosion/explosion_derecha.png", Texture.class);
        manager.load("explosion/explosion_horizontal.png", Texture.class);
        manager.load("explosion/explosion_vertical.png", Texture.class);

        //Bloque para destruir
        manager.load("tiles/6.png", Texture.class);



        // Diseño por dirección para mi personaje
        String[] dirs = {"Left", "Right", "Up", "Forward"};
        for (String dir : dirs) {
            for (int i = 0; i < 3; i++) {
                manager.load("player/" + dir + i + ".png", Texture.class);
            }
        }

        // Dino
        for (int i = 0; i < 10; i++)
        {
            manager.load("dino/Walk (" +(i+1)+").png", Texture.class);
        }
        for (int i = 0; i < 8; i++)
        {
            manager.load("dino/Dead (" +(i+1)+").png", Texture.class);
        }

        //Muerte de mi personaje
        for(int i = 0; i < 4; i++){
            manager.load("player/dead" + i + ".png", Texture.class);
        }



        // Sonidos
        manager.load("sound/bomberman-password.mp3", Music.class);
        manager.load("sound/loselife.wav", Sound.class);
        manager.load("sound/kill.wav", Sound.class);
        manager.load("sound/jump.wav", Sound.class);
        manager.load("sound/levelcomplete.wav", Sound.class);
        manager.load("sound/explosion.mp3", Sound.class);

        loadProgress = 0f;
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        loadProgress = game.manager.getProgress();

        game.camera.update();
        game.batch.setProjectionMatrix(game.camera.combined);
        game.textBatch.setProjectionMatrix(game.textCamera.combined);
        game.shapeRenderer.setProjectionMatrix(game.camera.combined);

        ScreenUtils.clear(Color.BLACK);

        // Dibujar barra de carga
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(Color.BLUE);
        game.shapeRenderer.rect(90, 200, 620, 100); // Marco exterior
        game.shapeRenderer.setColor(Color.PINK);
        game.shapeRenderer.rect(100, 210, 600, 80); // Fondo interno
        game.shapeRenderer.setColor(Color.WHITE);
        game.shapeRenderer.rect(110, 220, 580 * loadProgress, 60); // Progreso
        game.shapeRenderer.end();

        // Dibujar texto
        game.textBatch.begin();
        game.bigFont.draw(game.textBatch, "Cargando...", 120, 400);
        game.mediumFont.draw(game.textBatch, (int)(loadProgress * 100.f) + "%", 360, 180);
        game.textBatch.end();

        if(game.manager.update()) {
            game.setScreen(new MainMenuScreen(game));
            this.dispose();
        }
    }


    @Override
    public void resize(int width, int height) {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {}
}
