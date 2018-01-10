package net.johnbrooks.xen.engine.manager.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BasicScreen extends ScreenComponent {

    private Texture backgroundTexture;

    public BasicScreen() {
        backgroundTexture = null;
    }

    public void setBackgroundTexture(Texture texture) {
        backgroundTexture = texture;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    public void render(float deltaTime, SpriteBatch spriteBatch) {
        super.render(deltaTime, spriteBatch);
        if (backgroundTexture != null) {
            spriteBatch.begin();
            spriteBatch.draw(backgroundTexture, 0, 0, camera.viewportWidth, camera.viewportHeight);
            spriteBatch.end();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }
}
