package net.johnbrooks.xen.engine.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import net.johnbrooks.xen.engine.entities.sprites.Animation;
import net.johnbrooks.xen.engine.manager.screens.ScreenManager;
import net.johnbrooks.xen.engine.manager.screens.TiledScreen;
import net.johnbrooks.xen.engine.pathfinding.tiled.TiledMapNode;

import java.util.List;

public class TiledEntity extends Entity {

    private Vector2 lastStepPosition;

    public TiledEntity(World world, Vector2 position, Animation animation) {
        super(world, position, animation);
        lastStepPosition = new Vector2(getPosition());
    }

    @Override
    public void entityMoved(float deltaTime, Vector2 direction) {
        super.entityMoved(deltaTime, direction);
        if (Math.abs(getPosition().x - lastStepPosition.x) > ((TiledScreen) ScreenManager.getActive()).getTiledMapTilePixelWidth() ||
                Math.abs(getPosition().y - lastStepPosition.y) > ((TiledScreen) ScreenManager.getActive()).getTiledMapTilePixelHeight()) {
            entityStepped(deltaTime);
            lastStepPosition.set(getPosition());
        }
    }

    public void entityStepped(float deltaTime) {

    }

    @Override
    protected Vector2 getDirectionToCurrentNode() {
        TiledMapNode node = (TiledMapNode) targetPath.get(0);
        TiledScreen ts = (TiledScreen) ScreenManager.getActive();
        Vector2 position = ts.tileToWorldPosition(new Vector2(node.getX(), node.getY()));
        return position.sub(getPosition()).nor();
    }

    @Override
    protected void changeToNextTargetNode() {
        targetPath.remove(0);
        if (targetPath.isEmpty()) {
            body.setLinearVelocity(0, 0);
        }
    }

    @Override
    protected void move(float deltaTime) {
        if (!targetPath.isEmpty()) {
            // TODO: Add a speed of entity
            Vector2 direction = new Vector2(getDirectionToCurrentNode());
            entityMoved(deltaTime, direction);
            body.setLinearVelocity(direction.scl(30));
            // Check again for it could have been updated through entityMoved.
            if (!targetPath.isEmpty()) {
                float distance = new Vector2(body.getPosition()).dst(((TiledMapNode) targetPath.get(0)).getWorldPosition());
                if (distance <= 3) {
                    // Gdx.app.log(getClass().getSimpleName(), "Reached a node on path (" + (targetPath.size() - 1) + " more).");
                    changeToNextTargetNode();
                }
            }
        }
    }

    @Override
    public void setTargetPath(Vector2 target) {
        // Request a path using world coordinates
        targetPath.clear();
        if (target != null) {
            List<TiledMapNode> path = ((TiledScreen) ScreenManager.getActive()).tiledMapAStarPathFinder.findPath(getPosition(), target);

            for (TiledMapNode node : path) {
                targetPath.add(node);
            }
        }
    }
}
