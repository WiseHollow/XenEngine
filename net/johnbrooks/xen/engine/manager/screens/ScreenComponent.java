package net.johnbrooks.xen.engine.manager.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import net.johnbrooks.xen.engine.Engine;
import net.johnbrooks.xen.engine.entities.Entity;
import net.johnbrooks.xen.engine.ui.UI;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

public abstract class ScreenComponent implements Disposable {

    protected Stage stage;
    protected OrthographicCamera camera;

    public ScreenComponent() {
        // Setup this screen's camera.
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Engine.VIEWPORT_WIDTH, Engine.VIEWPORT_HEIGHT);
        stage = new Stage();
    }

    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    public void hide() {

    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public Stage getStage() {
        return stage;
    }

    public UI getUi() {
        throw new NotImplementedException();
    }

    public void update(float deltaTime) {
        stage.act();
    }

    public void render(float deltaTime, SpriteBatch spriteBatch) {
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public List<Entity> getEntityList() {
        throw new NotImplementedException();
    }
}
