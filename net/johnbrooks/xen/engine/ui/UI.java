package net.johnbrooks.xen.engine.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import net.johnbrooks.xen.starstrife.Game;

import java.util.ArrayList;
import java.util.List;

public class UI {

    private List<UIComponent> components;
    private int width, height;

    protected SpriteBatch spriteBatch;

    public UI() {
        spriteBatch = new SpriteBatch();
        components = new ArrayList<>();
        width = Game.VIEWPORT_WIDTH;
        height = Game.VIEWPORT_HEIGHT;
    }

    public UI(int _width, int _height) {
        spriteBatch = new SpriteBatch();
        components = new ArrayList<>();
        width = _width;
        height = _height;
    }

    public boolean isHoveringOverComponent() {
        for (UIComponent component : components) {
            if (component.isHovering()) {
                return true;
            }
        }

        return false;
    }

    public void render() {
        for (UIComponent component : components) {
            component.render(spriteBatch);
        }
    }

    public void update() {
        for (UIComponent component : components) {
            component.update();
        }
    }

    public void addComponent(UIComponent... components) {
        for (UIComponent component : components) {
            this.components.add(component);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
