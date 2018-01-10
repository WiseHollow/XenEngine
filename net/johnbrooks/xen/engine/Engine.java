package net.johnbrooks.xen.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import net.johnbrooks.xen.engine.manager.assets.AssetManager;
import net.johnbrooks.xen.engine.manager.screens.ScreenManager;

public class Engine implements Disposable {

    private static AssetManager assetManager = new AssetManager();
    private static boolean debugMode = false;

    public static boolean isDebugMode() {
        return debugMode;
    }
    public static void setDebugMode(boolean debug) {
        debugMode = debug;
    }
    public static AssetManager getAssetManager() {
        return assetManager;
    }

    public static int VIEWPORT_WIDTH = 1280, VIEWPORT_HEIGHT = 720;

    private SpriteBatch spriteBatch;

    public Engine(int viewportWidth, int viewportHeight) {
        VIEWPORT_WIDTH = viewportWidth;
        VIEWPORT_HEIGHT = viewportHeight;
        spriteBatch = new SpriteBatch();
    }

    public void render() {
        // Clear the screen.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Get delta time to be used within the engine.
        float deltaTime = Gdx.graphics.getDeltaTime();
        // Call update before render.
        ScreenManager.update(deltaTime);
        // Render the active screen.
        ScreenManager.render(deltaTime, spriteBatch);
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        ScreenManager.dispose();
        assetManager.dispose();
    }
}
