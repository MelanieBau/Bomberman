package com.mygdx.bomberman;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class LevelCompleteScreen implements Screen {

    PuigBros game;
    ButtonLayout endMenu;
    GlyphLayout layout = new GlyphLayout();

    public LevelCompleteScreen(PuigBros game) {
        this.game = game;

        endMenu = new ButtonLayout(game.camera, game.manager, game.mediumFont);
        endMenu.loadFromJson("endmenu.json");

        game.manager.get("sound/levelcomplete.wav", Sound.class).play();

        endMenu.moveButton("Menu", 0, -50);  // Mueve el botón 50px hacia abajo
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        game.camera.update();
        game.batch.setProjectionMatrix(game.camera.combined);
        game.textBatch.setProjectionMatrix(game.textCamera.combined);

        game.batch.begin();
        game.batch.draw(game.manager.get("bombermann.png", Texture.class), 0, 0, 800, 480, 0, 0, 1000, 750, false, false);
        game.batch.end();

        // Centramos el texto "VICTORIA!"
        String text = "VICTORIA!";
        layout.setText(game.bigFont, text);
        float textX = (800 - layout.width) / 2f; // Centrado en pantalla de 800px
        float textY = 400;

        game.textBatch.begin();
        game.bigFont.draw(game.textBatch, layout, textX, textY);
        game.textBatch.end();

        endMenu.render(game.batch, game.textBatch);

        if (endMenu.consumeRelease("Menu")) {
            this.dispose();
            game.setScreen(new MainMenuScreen(game));
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
