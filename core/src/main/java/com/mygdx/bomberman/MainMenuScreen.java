package com.mygdx.bomberman;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class MainMenuScreen implements Screen {
    PuigBros game;
    ButtonLayout mainMenu;
    GlyphLayout layout;

    public MainMenuScreen(PuigBros game) {
        this.game = game;

        mainMenu = new ButtonLayout(game.camera, game.manager, game.mediumFont);
        mainMenu.loadFromJson("mainmenu.json");

        layout = new GlyphLayout();
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        game.camera.update();
        game.batch.setProjectionMatrix(game.camera.combined);
        game.textBatch.setProjectionMatrix(game.textCamera.combined);

        game.batch.begin();
        game.batch.draw(game.manager.get("bombermann.png", Texture.class),
            0, 0, 800, 480, 0, 0, 1000, 750, false, true);
        game.batch.end();

        game.textBatch.begin();

        // El bomberman se mostrará centrado
        layout.setText(game.bigFont, "Bomberman");
        float titleX = (800 - layout.width) / 2f;
        game.bigFont.draw(game.textBatch, layout, titleX, 420);


        layout.setText(game.smallFont, "Puig Castellar 2025");
        float footerX = (800 - layout.width) / 2f;
        game.smallFont.draw(game.textBatch, layout, footerX, 60);

        game.textBatch.end();

        mainMenu.render(game.batch, game.textBatch);

        // Start the game!
        if (mainMenu.consumeRelease("Start")) {
            game.lives = 3;
            game.setScreen(new GameScreen(game));
            this.dispose();
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
