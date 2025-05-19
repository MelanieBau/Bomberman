package com.mygdx.bomberman;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import com.mygdx.bomberman.jsonloaders.ButtonJson;
import com.mygdx.bomberman.jsonloaders.ButtonLayoutJson;

import java.util.HashMap;
import java.util.Map;

public class ButtonLayout implements InputProcessor {

    class Button {

        public float debug_x;
        public float debug_y;
        Rectangle rect;
        String action;
        String text;
        String imageOn, imageOff;
        boolean pressed;
        int pushes, releases;

        Button(int x, int y, int sx, int sy, String action, String text, String on, String off)
        {
            rect = new Rectangle(x, y, sx, sy);
            this.action = action;
            this.text = text;
            this.imageOn = on;
            this.imageOff = off;
            pressed = false;
            pushes = 0;
            releases = 0;
        }
    }

    Map<String,Button> buttons;
    Map<Integer,String> pointers;
    final OrthographicCamera camera;
    AssetManager manager;
    BitmapFont font;

    public ButtonLayout(OrthographicCamera camera, AssetManager manager, BitmapFont font)
    {
        this.camera = camera;
        this.manager = manager;
        this.font = font;
        buttons = new HashMap<>();
        pointers = new HashMap<>();

        setAsActiveInputProcessor();
    }

    void setAsActiveInputProcessor()
    {
        Gdx.input.setInputProcessor(this);
    }

    public void loadFromJson(String fileName)
    {
        Json json = new Json();
        FileHandle file = Gdx.files.internal(fileName);
        String fileText = file.readString();
        ButtonLayoutJson l = json.fromJson(ButtonLayoutJson.class, fileText);

        for(ButtonJson b : l.buttons)
        {
            addButton(b.x, b.y, b.width, b.height, b.action, b.text, b.image_on, b.image_off);
        }
    }

    public void addButton(int x, int y, int sx, int sy, String action, String text, String imageOn, String imageOff)
    {
        Button b = new Button(x, y, sx, sy, action, text, imageOn, imageOff);
        buttons.put(action, b);
    }

    boolean isPressed(String action)
    {
        if(buttons.get(action) != null)
            return buttons.get(action).pressed;
        else
            return false;
    }

    boolean consumePush(String action)
    {
        if(buttons.get(action) != null)
        {
            if(buttons.get(action).pushes > 0)
            {
                buttons.get(action).pushes = 0;
                return true;
            }
        }
        return false;

    }

    boolean consumeRelease(String action)
    {
        if(buttons.get(action) != null)
        {
            if(buttons.get(action).releases > 0)
            {
                buttons.get(action).releases = 0;
                return true;
            }
        }
        return false;

    }

    // Render for debug purposes
    public void render(ShapeRenderer shapeRenderer)
    {
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for(String i:buttons.keySet())
        {
            Button b = buttons.get(i);
            shapeRenderer.setColor(b.pressed ? Color.YELLOW : Color.BLACK);
            shapeRenderer.ellipse(b.rect.x, b.rect.y, b.rect.width, b.rect.height, 2);
            shapeRenderer.rect(b.rect.x, b.rect.y, b.rect.width, b.rect.height);
        }
        shapeRenderer.end();
    }

    public void render(SpriteBatch spriteBatch, SpriteBatch textBatch)
    {
        // Button images
        spriteBatch.begin();
        for(String i:buttons.keySet())
        {
            Button b = buttons.get(i);
            Texture t = manager.get(b.pressed ? b.imageOn : b.imageOff, Texture.class);
            spriteBatch.draw(t, b.rect.x, b.rect.y, b.rect.width, b.rect.height, 0, 0, t.getWidth(), t.getHeight(), false, true);

        }
        spriteBatch.end();

        // Button texts
        textBatch.begin();
        for(String i:buttons.keySet())
        {
            Button b = buttons.get(i);

            if(b.text != null) {
                GlyphLayout glyphLayout = new GlyphLayout();
                glyphLayout.setText(font, b.text);
                font.draw(textBatch, glyphLayout, b.rect.x + (b.rect.width - glyphLayout.width) / 2f, 480 - (b.rect.y + (b.rect.height - glyphLayout.height) / 2f));
            }
        }
        textBatch.end();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown (int x, int y, int pointer, int button) {

        Vector3 touchPos = new Vector3();
        touchPos.set(x, y, 0);
        camera.unproject(touchPos);

        for(String action:buttons.keySet())
        {
            if(buttons.get(action).rect.contains(touchPos.x,touchPos.y))
            {
                // Button has been pressed
                pointers.put(pointer,action);
                buttons.get(action).debug_x = touchPos.x;
                buttons.get(action).debug_y = touchPos.y;
                buttons.get(action).pressed = true;
                buttons.get(action).pushes ++;
            }
        }

        return true; // return true to indicate the event was handled
    }

    @Override
    public boolean touchUp (int x, int y, int pointer, int button) {

        if(pointers.get(pointer) != null)
        {

            buttons.get(pointers.get(pointer)).pressed = false;
            buttons.get(pointers.get(pointer)).releases++;
            pointers.remove(pointer);
        }
        return true;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        // Get screen camera coordinates of touch
        Vector3 touchPos = new Vector3();
        touchPos.set(screenX, screenY, 0);
        camera.unproject(touchPos);

        for(String i:buttons.keySet())
        {
            // Pointer is now inside a new button; release previous button
            if(buttons.get(i).rect.contains(touchPos.x,touchPos.y))
            {
                if(pointers.get(pointer) != null)
                {
                    buttons.get(pointers.get(pointer)).pressed = false;
                }
                pointers.put(pointer,buttons.get(i).action);
                buttons.get(i).pressed = true;
            }
        }
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    public void moveButton(String action, float dx, float dy) {
        Button b = buttons.get(action);
        if (b != null) {
            b.rect.x += dx;
            b.rect.y += dy;
        }
    }
}
