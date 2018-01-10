package net.johnbrooks.xen.engine.pathfinding.tiled;

import com.badlogic.gdx.math.Vector2;
import net.johnbrooks.xen.engine.pathfinding.Node;

public class TiledMapNode extends Node {

    private Vector2 worldPosition;

    public TiledMapNode(int x, int y) {
        super(x, y);
    }

    public void setWorldPosition(Vector2 position) {
        worldPosition = position;
    }

    public Vector2 getWorldPosition() {
        return worldPosition;
    }
}
