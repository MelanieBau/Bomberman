package com.mygdx.bomberman;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.bomberman.jsonloaders.EnemyJson;
import com.mygdx.bomberman.jsonloaders.LevelJson;

import java.util.ArrayList;

public class GameScreen implements Screen {

    PuigBros game;
    ButtonLayout joypad, pauseMenu;

    Stage stage;
    TileMap tileMap;

    Player player;
    ArrayList<Actor> enemies;
    boolean paused;

    public GameScreen(PuigBros game) {
        this.game = game;

        pauseMenu = new ButtonLayout(game.camera, game.manager, game.mediumFont);
        pauseMenu.loadFromJson("pausemenu.json");

        joypad = new ButtonLayout(game.camera, game.manager, null);
        joypad.loadFromJson("joypad.json");

        tileMap = new TileMap(game.manager, game.batch);

        stage = new Stage();
        player = new Player(game.manager);
        enemies = new ArrayList<>();
        player.setMap(tileMap);
        player.setJoypad(joypad);
        stage.addActor(player);

        Viewport viewport = new Viewport() {};
        viewport.setCamera(game.camera);
        stage.setViewport(viewport);

        Json json = new Json();
        FileHandle file = Gdx.files.internal("Level.json");
        LevelJson l = json.fromJson(LevelJson.class, file.readString());
        tileMap.loadFromLevel(l);

        for (EnemyJson e : l.getEnemies()) {
            if (e.getType().equals("Dino")) {
                Dino d = new Dino(e.getX() * tileMap.TILE_SIZE, e.getY() * tileMap.TILE_SIZE, game.manager, player);
                d.setMap(tileMap);
                enemies.add(d);
                stage.addActor(d);
            }
        }

        paused = false;

        game.manager.get("sound/bomberman-password.mp3", Music.class).play();
        game.manager.get("sound/bomberman-password.mp3", Music.class).setLooping(true);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        game.camera.update();
        game.batch.setProjectionMatrix(game.camera.combined);
        game.shapeRenderer.setProjectionMatrix(game.camera.combined);
        ScreenUtils.clear(Color.SKY);

        tileMap.render();
        stage.draw();

        if (paused) {
            pauseMenu.render(game.batch, game.textBatch);
        } else {
            joypad.render(game.batch, game.textBatch);
            game.textBatch.begin();
            game.mediumFont.draw(game.textBatch, "Vidas: " + game.lives, 40, 460);
            game.textBatch.end();
        }

        if (paused) {
            if (pauseMenu.consumeRelease("Resume")) {
                joypad.setAsActiveInputProcessor();
                paused = false;
            }
            if (pauseMenu.consumeRelease("Quit")) {
                this.dispose();
                game.setScreen(new MainMenuScreen(game));
            }
        } else {
            updateGameLogic(delta);
            if (joypad.consumePush("Pause")) {
                paused = true;
                pauseMenu.setAsActiveInputProcessor();
            }
        }
    }

    void updateGameLogic(float delta) {
        stage.act(delta);
        tileMap.scrollX = Math.max(0, (int) ((game.camera.viewportWidth - tileMap.width * TileMap.TILE_SIZE) / 2f));

        Rectangle rect_player = new Rectangle(player.getX(), player.getY(), player.getWidth(), player.getHeight());

        for (Actor enemy : enemies) {
            Rectangle rect_enemy = new Rectangle(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight());
            WalkingCharacter wc = (WalkingCharacter) enemy;

            if (!player.isDead() && !wc.isDead()) {
                if (rect_enemy.overlaps(rect_player)) {
                    if (player.hasInvulnerability()) {
                        wc.kill();
                    } else {
                        game.manager.get("sound/bomberman-password.mp3", Music.class).stop();
                        game.manager.get("sound/loselife.wav", Sound.class).play();
                        player.kill();
                    }
                }
            }
        }

        // Verificar si todos los enemigos están muertos
        boolean allEnemiesDead = true;
        for (Actor enemy : enemies) {
            if (!((WalkingCharacter) enemy).isDead()) {
                allEnemiesDead = false;
                break;
            }
        }

        if (allEnemiesDead) {
            this.dispose();
            game.setScreen(new LevelCompleteScreen(game));
        }

        if (player.isDead() && player.isDeathAnimationFinished()) {
            if (player.deathAnimFinished) {
                game.lives--;
                if (game.lives <= 0) {
                    this.dispose();
                    game.setScreen(new MainMenuScreen(game));
                } else {
                    this.dispose();
                    game.setScreen(new GameScreen(game));
                }
            }
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() { paused = true; }
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        game.manager.get("sound/bomberman-password.mp3", Music.class).stop();
    }
}
