package net.johnbrooks.xen.engine.manager.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import net.johnbrooks.xen.engine.entities.sprites.Animation;

public abstract class LoadingScreen extends BasicScreen {

    protected boolean finished;
    protected Animation animation;
    protected Vector2 loadingAnimationPosition;
    protected BitmapFont font;
    protected GlyphLayout text;

    public LoadingScreen() {
        font = new BitmapFont();
        text = new GlyphLayout();
        text.setText(font, "Loading Content");
        animation = Animation.getSpriteAnimation("loading-1", 100, 100, 0);
        loadingAnimationPosition = new Vector2(camera.viewportWidth * 0.5f, camera.viewportHeight * 0.5f);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        animation.update(deltaTime);
        if (finished) {
            onFinish();
            ScreenManager.remove(this);
        }
    }

    @Override
    public void render(float deltaTime, SpriteBatch spriteBatch) {
        super.render(deltaTime, spriteBatch);
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.begin();
        spriteBatch.draw(animation.getFrame(), loadingAnimationPosition.x - animation.getFrame().getRegionWidth() * 0.5f, loadingAnimationPosition.y - animation.getFrame().getRegionHeight() * 0.5f);
        font.draw(spriteBatch, text, loadingAnimationPosition.x - text.width * 0.5f, loadingAnimationPosition.y - animation.getFrame().getRegionHeight() * 0.5f - 35);
        spriteBatch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        font.dispose();
    }

    public void finish() {
        finished = true;
    }

    public abstract void onFinish();
}
