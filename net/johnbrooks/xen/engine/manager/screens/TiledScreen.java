package net.johnbrooks.xen.engine.manager.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import net.johnbrooks.xen.engine.Engine;
import net.johnbrooks.xen.engine.entities.Entity;
import net.johnbrooks.xen.engine.pathfinding.tiled.TiledMapAStarPathFinder;

import java.util.ArrayList;
import java.util.List;

public abstract class TiledScreen extends ScreenComponent {

    private BatchTiledMapRenderer mapRenderer;
    private Box2DDebugRenderer debugRenderer;

    private final int tiledMapTilesVertically, tiledMapTilesHorizontally, tiledMapTilePixelWidth, tiledMapTilePixelHeight;

    protected TiledMap tiledMap;
    protected World world;
    protected int cameraSpeed;
    public TiledMapAStarPathFinder tiledMapAStarPathFinder;

    public TiledScreen(BatchTiledMapRenderer _mapRenderer) {
        tiledMap = _mapRenderer.getMap();
        mapRenderer = _mapRenderer;
        debugRenderer = new Box2DDebugRenderer();
        world = new World(new Vector2(0, 0), true);
        // How fast the camera can translate
        cameraSpeed = 6;

        // Get details about tiled map
        MapProperties tiledMapProperties = tiledMap.getProperties();
        tiledMapTilesHorizontally = tiledMapProperties.get("width", Integer.class);
        tiledMapTilesVertically = tiledMapProperties.get("height", Integer.class);
        tiledMapTilePixelWidth = tiledMapProperties.get("tilewidth", Integer.class);
        tiledMapTilePixelHeight = tiledMapProperties.get("tileheight", Integer.class);


        // Position camera in the center of the map
        Vector3 mapCenter = new Vector3((tiledMapTilePixelWidth * tiledMapTilesHorizontally * 0.5f), (tiledMapTilePixelHeight * tiledMapTilesVertically * 0.5f), 0);
        camera.position.set(mapCenter);
        camera.update();

        createBodies();
        setupPathFinder();
    }

    private void setupPathFinder() {
        List<TiledMapTileLayer> layers = new ArrayList<>();
        for (MapLayer mapLayer : tiledMap.getLayers()) {
            if (mapLayer.getProperties().containsKey("collision") && mapLayer.getProperties().get("collision", Boolean.class) == true) {
                layers.add((TiledMapTileLayer) mapLayer);
            }
        }
        tiledMapAStarPathFinder = new TiledMapAStarPathFinder(this, layers);
        Gdx.app.log(getClass().getSimpleName(), "TiledMapAStarPathFinder is now enabled.");
    }

    private void createBodies() {

        for (MapLayer mapLayer : tiledMap.getLayers()) {
            if (mapLayer.getProperties().containsKey("collision") && mapLayer.getProperties().get("collision", Boolean.class) == true) {
                TiledMapTileLayer tileLayer = (TiledMapTileLayer) mapLayer;

                PolygonShape tileShape = new PolygonShape();
                tileShape.setAsBox(tiledMapTilePixelWidth * 0.5f, tiledMapTilePixelHeight * 0.5f);

                for (int y = 0; y < tileLayer.getHeight(); y++) {
                    for (int x = 0; x < tileLayer.getWidth(); x++) {
                        TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);
                        if (cell != null && cell.getTile() != null) {
                            BodyDef tileBodyDef = new BodyDef();
                            tileBodyDef.fixedRotation = true;
                            tileBodyDef.type = BodyDef.BodyType.StaticBody;
                            Vector2 position = new Vector2(x, y);
                            tileBodyDef.position.set(tileToWorldPosition(position));

                            FixtureDef tileFixtureDef = new FixtureDef();
                            tileFixtureDef.shape = tileShape;
                            // tileFixtureDef.filter.categoryBits = SSVars.BIT_MIDDLE; // What BIT are we?
                            // tileFixtureDef.filter.maskBits = SSVars.BIT_UNIT; // The bits that we can collide with.

                            Body body = world.createBody(tileBodyDef);
                            body.createFixture(tileFixtureDef);
                        }
                    }
                }

                // Cleanup no longer needed objects.
                tileShape.dispose();
            }
        }
    }

    public Vector2 tileToWorldPosition(Vector2 tilePosition) {
        Vector2 worldPosition = new Vector2(tilePosition);
        worldPosition.x *= tiledMapTilePixelWidth;
        worldPosition.y *= tiledMapTilePixelHeight;
        worldPosition.x += 8;
        worldPosition.y += 8;
        return worldPosition;
    }

    public Vector2 worldToTilePosition(Vector2 worldPosition) {
        Vector2 tilePosition = new Vector2(worldPosition);
        // tilePosition.x -= 8;
        // tilePosition.y -= 8;
        tilePosition.x = (int) tilePosition.x / tiledMapTilePixelWidth;
        tilePosition.y = (int) tilePosition.y / tiledMapTilePixelHeight;
        return tilePosition;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        world.step(deltaTime, 6, 2);
        input();
    }

    @Override
    public void render(float deltaTime, SpriteBatch spriteBatch) {
        super.render(deltaTime, spriteBatch);
        mapRenderer.setView(camera);
        mapRenderer.render();
        if (Engine.isDebugMode()) {
            debugRenderer.render(world, camera.combined);
        }
    }

    @Override
    public void dispose() {
        tiledMap.dispose();
        mapRenderer.dispose();
        debugRenderer.dispose();
    }

    protected void translateCamera(Vector2 translation) {
        // Translate by the amount passed
        camera.translate(translation);
        // Get position of camera for checking
        Vector3 cameraPosition = camera.position;
        // Check if camera is outside the bounds of the tiled map, and correct it if needed.
        if (cameraPosition.x < camera.viewportWidth * 0.5f * camera.zoom)
            cameraPosition.x = camera.viewportWidth * 0.5f * camera.zoom;
        if (cameraPosition.x > tiledMapTilePixelWidth * tiledMapTilesHorizontally - camera.viewportWidth * 0.5f * camera.zoom)
            cameraPosition.x = tiledMapTilePixelWidth * tiledMapTilesHorizontally - camera.viewportWidth * 0.5f * camera.zoom;
        if (cameraPosition.y < camera.viewportHeight * 0.5f * camera.zoom)
            cameraPosition.y = camera.viewportHeight * 0.5f * camera.zoom;
        if (cameraPosition.y > tiledMapTilePixelHeight * tiledMapTilesVertically - camera.viewportHeight * 0.5f * camera.zoom)
            cameraPosition.y = tiledMapTilePixelHeight * tiledMapTilesVertically - camera.viewportHeight * 0.5f * camera.zoom;
        // Update camera based on movement changes
        camera.update();
        // Update any perspective objects that may have been changed because of camera movement.
        for (Entity e : getEntityList()) {
            e.perspectiveChange();
        }
    }

    protected void input() {
    }

    public int getTiledMapTilesVertically() {
        return tiledMapTilesVertically;
    }

    public int getTiledMapTilesHorizontally() {
        return tiledMapTilesHorizontally;
    }

    public int getTiledMapTilePixelWidth() {
        return tiledMapTilePixelWidth;
    }

    public int getTiledMapTilePixelHeight() {
        return tiledMapTilePixelHeight;
    }
}
