package net.johnbrooks.xen.engine.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface UIComponent {
    void render(SpriteBatch spriteBatch);
    void update();
    void dispose();
    boolean isHovering();
}
