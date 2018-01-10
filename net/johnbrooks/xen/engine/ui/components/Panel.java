package net.johnbrooks.xen.engine.ui.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import net.johnbrooks.xen.engine.manager.screens.ScreenManager;
import net.johnbrooks.xen.engine.ui.UIComponent;

import java.util.ArrayList;
import java.util.List;

public class Panel implements UIComponent {

    private Texture texture;
    private Rectangle rectangle;

    private List<UIComponent> componentList;

    public Panel(Texture _texture, int x, int y, int width, int height) {
        componentList = new ArrayList<>();
        texture = _texture;
        rectangle = new Rectangle(x, y, width, height);
    }

    public void add(UIComponent... components) {
        for (UIComponent component : components) {
            componentList.add(component);
        }
    }

    public void remove(UIComponent... components) {
        for (UIComponent component : components) {
            componentList.remove(component);
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        spriteBatch.begin();
        spriteBatch.draw(texture, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        spriteBatch.end();

        for (UIComponent component : componentList) {
            component.render(spriteBatch);
        }
    }

    @Override
    public void update() {
        for (UIComponent component : componentList) {
            component.update();
        }
    }

    @Override
    public void dispose() {
        texture.dispose();
    }

    @Override
    public boolean isHovering() {
        int x = Gdx.input.getX();
        int y = (int) ScreenManager.getActive().getCamera().viewportHeight - Gdx.input.getY();

        return x >= rectangle.x && x <= rectangle.x + rectangle.width && y >= rectangle.y && y <= rectangle.y + rectangle.height;
    }
}
