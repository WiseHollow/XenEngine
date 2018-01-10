package net.johnbrooks.xen.engine.ui.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import net.johnbrooks.xen.engine.Engine;
import net.johnbrooks.xen.engine.manager.screens.ScreenManager;
import net.johnbrooks.xen.engine.ui.UIComponent;

public class Button implements UIComponent {

    public enum ButtonTextureType {
        NORMAL, ONCLICK, ONHOVER
    }

    private static BitmapFont font = new BitmapFont();

    private boolean clicking;

    protected Rectangle rectangle;
    protected Texture background, backgroundOnClick, backgroundOnHover;
    protected Runnable onClick;
    protected GlyphLayout text;

    public Button(int x, int y, int width, int height) {
        rectangle = new Rectangle(x, y, width, height);
        text = new GlyphLayout();
    }

    @Override
    public void render(SpriteBatch spriteBatch) {

        if (isHovering() && backgroundOnHover != null) {
            if (clicking) {
                spriteBatch.begin();
                spriteBatch.draw(backgroundOnClick != null ? backgroundOnClick : background, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                spriteBatch.end();
            } else {
                spriteBatch.begin();
                spriteBatch.draw(backgroundOnHover != null ? backgroundOnHover : background, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                spriteBatch.end();
            }
        } else if (background != null) {
            spriteBatch.begin();
            spriteBatch.draw(background, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            spriteBatch.end();
        }

        if (text != null) {
            spriteBatch.begin();
            font.draw(spriteBatch, text, rectangle.x - text.width * 0.5f + rectangle.width * 0.5f, rectangle.y + text.height * 0.5f + rectangle.height * 0.5f);
            spriteBatch.end();
        }
    }

    @Override
    public void update() {

        boolean pressed = Gdx.input.isButtonPressed(Input.Buttons.LEFT);

        if (!pressed && clicking && onClick != null) {
            Gdx.app.postRunnable(onClick);
        }

        if (isHovering()) {
            clicking = pressed;
        }
    }

    @Override
    public boolean isHovering() {
        int x = Gdx.input.getX();
        int y = (int) ScreenManager.getActive().getCamera().viewportHeight - Gdx.input.getY();

        return x >= rectangle.x && x <= rectangle.x + rectangle.width && y >= rectangle.y && y <= rectangle.y + rectangle.height;
    }

    @Override
    public void dispose() {

    }

    public Button setOnClick(Runnable _onClick) {
        onClick = _onClick;
        return this;
    }

    public Button setText(String _text) {
        text.setText(font, _text);
        return this;
    }

    public Button setTexture(ButtonTextureType type, String textureName) {

        boolean set = false;

        switch (type) {
            case NORMAL:
                background = (Texture) Engine.getAssetManager().get(textureName, Texture.class);
                set = true;
                break;
            case ONCLICK:
                backgroundOnClick = (Texture) Engine.getAssetManager().get(textureName, Texture.class);
                set = true;
                break;
            case ONHOVER:
                backgroundOnHover = (Texture) Engine.getAssetManager().get(textureName, Texture.class);
                set = true;
                break;
        }

        return set ? this : null;

    }
}
