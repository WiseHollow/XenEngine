package net.johnbrooks.xen.engine.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;
import net.johnbrooks.xen.engine.entities.sprites.Animation;
import net.johnbrooks.xen.engine.manager.screens.ScreenManager;
import net.johnbrooks.xen.engine.manager.screens.TiledScreen;
import net.johnbrooks.xen.engine.pathfinding.Node;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

public class Entity implements Disposable {

    protected static final ShapeRenderer shapeRenderer = new ShapeRenderer();

    protected Body body;
    protected Vector3 position3;
    protected Rectangle rectangle;
    protected Vector2 lastPosition;

    protected Animation animation;
    protected int health, maxHealth;
    protected boolean selectable, multipleSelection, immobile, selected;
    protected float selectionRadius;

    protected List<Node> targetPath;

    public Entity(World world, Vector2 position, Animation animation) {
        this.animation = animation;
        this.createBody(world, position);
        this.selectionRadius = Math.max(animation.getFrame().getRegionWidth(), animation.getFrame().getRegionHeight()) * 0.75f;
        this.position3 = new Vector3(body.getPosition().x, body.getPosition().y, 0);
        this.rectangle = null;
        this.targetPath = new ArrayList<>();
        this.lastPosition = new Vector2(getPosition());

        Gdx.app.postRunnable(() -> create());
    }

    private void createBody(World world, Vector2 position) {
        if (body == null) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.position.set(position);
            bodyDef.fixedRotation = true;
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            body = world.createBody(bodyDef);
            body.setUserData(this);

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(4, 4);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            body.createFixture(fixtureDef);

            shape.dispose();
        } else {
            Gdx.app.error(getClass().getSimpleName(), "You called createBody when the body was already created.");
        }
    }

    public void create() {}

    public void hurt(int damage) {
        health = health - damage;
        if (health <= 0) {
            remove();
        }
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void remove() {
        ScreenManager.getActive().getEntityList().remove(this);
        dispose();
    }

    public void perspectiveChange() {
        position3.set(body.getPosition().x, body.getPosition().y, 0);
        rectangle = calculateRectangle();
    }

    protected void move(float deltaTime) {
        throw new NotImplementedException();
    }

    protected void changeToNextTargetNode() {
        throw new NotImplementedException();
    }

    public void update(float deltaTime) {
        // First, update perspectives movement
        detectMovement(deltaTime);
        // Update animation for sprite
        animation.update(deltaTime);
        // Try to move the entity, if they have somewhere they wish to move.
        if (!immobile) {
            move(deltaTime);
        }
    }

    public void entityMoved(float deltaTime, Vector2 direction) {
        perspectiveChange();
    }

    private void detectMovement(float deltaTime) {
        if ((int) getPosition().x != (int) lastPosition.x || (int) getPosition().y != (int) lastPosition.y) {
            Vector2 direction = new Vector2(getPosition()).sub(lastPosition).nor();
            lastPosition.set(getPosition());
            entityMoved(deltaTime, direction);
        }
    }

    public void render(SpriteBatch spriteBatch) {
        spriteBatch.enableBlending();
        spriteBatch.begin();
        spriteBatch.draw(animation.getFrame(), body.getPosition().x - animation.getFrame().getRegionWidth() * 0.5f, body.getPosition().y - animation.getFrame().getRegionHeight() * 0.5f);
        spriteBatch.end();

        if (selected) {
            renderSelected();
        }
    }

    protected void renderSelected() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GREEN);
        Vector3 centerOfEntity = ScreenManager.getActive().getCamera().project(new Vector3(body.getPosition().x, body.getPosition().y, 0));
        shapeRenderer.circle(centerOfEntity.x, centerOfEntity.y, selectionRadius);
        shapeRenderer.end();
    }

    public void setTargetPath(Vector2 target) {
        throw new NotImplementedException();
    }

    protected Vector2 getDirectionToCurrentNode() {
        Node node = targetPath.get(0);
        Vector2 position = ((TiledScreen) ScreenManager.getActive()).tileToWorldPosition(new Vector2(node.getX(), node.getY()));
        return position.sub(getPosition()).nor();
    }

    public List<Node> getTargetPath() {
        return targetPath;
    }

    public Animation getAnimation() {
        return animation;
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public boolean isImmobile() {
        return immobile;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public boolean isMultipleSelection() {
        return multipleSelection;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Body getBody() {
        return body;
    }

    public Rectangle getRectangle() {
        if (rectangle != null) {
            return rectangle;
        } else {
            return rectangle = calculateRectangle();
        }
    }

    private Rectangle calculateRectangle() {
        Vector3 screenPosition = ScreenManager.getActive().getCamera().project(position3);

        return new Rectangle(screenPosition.x - animation.getFrame().getRegionWidth() * 0.5f, screenPosition.y - animation.getFrame().getRegionHeight() * 0.5f,
                animation.getFrame().getRegionWidth(), animation.getFrame().getRegionHeight());
    }

    @Override
    public void dispose() {
        body.getWorld().destroyBody(body);
    }
}
