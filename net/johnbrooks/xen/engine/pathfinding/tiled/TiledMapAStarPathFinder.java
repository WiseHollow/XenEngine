package net.johnbrooks.xen.engine.pathfinding.tiled;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import net.johnbrooks.xen.engine.entities.Entity;
import net.johnbrooks.xen.engine.manager.screens.TiledScreen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class TiledMapAStarPathFinder {
    private TiledMapNode[][] nodes;
    private List<TiledMapTileLayer> tileLayers;
    private TiledScreen tiledScreen;

    public TiledMapAStarPathFinder(TiledScreen _tiledScreen, List<TiledMapTileLayer> _tileLayers) {
        tiledScreen = _tiledScreen;
        tileLayers = _tileLayers;
        populateNodes();
    }

    private void populateNodes() {
        nodes = new TiledMapNode[tiledScreen.getTiledMapTilesHorizontally()][tiledScreen.getTiledMapTilesVertically()];
        for (int y = 0; y < tiledScreen.getTiledMapTilesVertically(); y++) {
            for (int x = 0; x < tiledScreen.getTiledMapTilesHorizontally(); x++) {
                TiledMapNode node = new TiledMapNode(x, y);
                node.setWorldPosition(tiledScreen.tileToWorldPosition(new Vector2(x, y)));
                nodes[x][y] = node;
            }
        }
        // Gdx.app.log(getClass().getSimpleName(), nodes.length + " nodes were created.");
    }

    public TiledMapNode worldPositionToNode(Vector2 worldPosition) {
        Vector2 tilePosition = tiledScreen.worldToTilePosition(worldPosition);
        return nodes[(int) tilePosition.x][(int) tilePosition.y];
    }

    private List<TiledMapNode> getNeighbors(TiledMapNode node) {
        List<TiledMapNode> neighbors = new ArrayList<>();

        for (int y = -1; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                if (x == 0 && y == 0)
                    continue;

                int _x = x + node.getX();
                int _y = y + node.getY();
                if (_x >= 0 && _x < tiledScreen.getTiledMapTilesHorizontally() &&
                        _y >= 0 && _y < tiledScreen.getTiledMapTilesVertically()) {
                    neighbors.add(nodes[_x][_y]);
                }
            }
        }

        return neighbors;
    }

    private int calculateDistance(TiledMapNode n1, TiledMapNode n2) {
        int dx = Math.abs(n1.getX() - n2.getX());
        int dy = Math.abs(n2.getY() - n2.getY());

        return Math.min(dx, dy) * 14 + Math.abs(dx - dy) * 10;
    }

    private boolean areValidCoordinates(int sourceX, int sourceY, int targetX, int targetY) {
        // Gdx.app.log(getClass().getSimpleName(), "Checking coordinates (" + sourceX + " " + sourceY + ") (" + targetX + " " + targetY + ")");
        if (sourceX == targetX && sourceY == targetY) {
            return false;
        } else if (sourceX < 0 || sourceY < 0 || sourceX >= tiledScreen.getTiledMapTilesHorizontally() || sourceY >= tiledScreen.getTiledMapTilesVertically()) {
            return false;
        } else if (targetX < 0 || targetY < 0 || targetX >= tiledScreen.getTiledMapTilesHorizontally() || targetY >= tiledScreen.getTiledMapTilesVertically()) {
            return false;
        }

        return true;
    }

    private boolean isWalkable(TiledMapNode node) {
        for (TiledMapTileLayer layer : tileLayers) {
            if (layer.getProperties().containsKey("collision") && layer.getProperties().get("collision", Boolean.class)) {
                TiledMapTileLayer.Cell cell = layer.getCell(node.getX(), node.getY());
                if (cell != null && cell.getTile() != null) {
                    return false;
                }
            }
        }
//        if (isUnitAt(node)) {
//            return false;
//        }
        return true;
    }

    private boolean isUnitAt(TiledMapNode node) {
        for (Entity entity : tiledScreen.getEntityList()) {
            TiledMapNode entityNode = worldPositionToNode(entity.getPosition());
            if (entityNode == node) {
                return true;
            }
        }

        return false;
    }

    private List<TiledMapNode> retraceSteps(TiledMapNode start, TiledMapNode target) {
        // Gdx.app.log(getClass().getSimpleName(), "Calculating path...");
        List<TiledMapNode> path = new ArrayList<>();
        path.add(target);

        TiledMapNode current = target;

        while (current.parent != start) {
            path.add((TiledMapNode) current.parent);
            current = (TiledMapNode) current.parent;
        }

        Collections.reverse(path);
        // Gdx.app.log(getClass().getSimpleName(), "Found path of " + path.size() + " nodes.");
        return path;
    }

    public List<TiledMapNode> findPath(Vector2 sourcePosition, Vector2 targetPosition) {

        sourcePosition = tiledScreen.worldToTilePosition(sourcePosition);
        targetPosition = tiledScreen.worldToTilePosition(targetPosition);

        int sourceX = (int) sourcePosition.x;
        int sourceY = (int) sourcePosition.y;
        int targetX = (int) targetPosition.x;
        int targetY = (int) targetPosition.y;

        if (!areValidCoordinates(sourceX, sourceY, targetX, targetY))
            return new ArrayList<>();

        // Gdx.app.log( getClass().getSimpleName(), "Finding path from (" + sourceX + " " + sourceY + ") to (" + targetX + " " + targetY + ").");

        // populateNodes();
        List<TiledMapNode> openList = new ArrayList<>();
        HashSet<TiledMapNode> closedList = new HashSet<>();

        TiledMapNode start = nodes[sourceX][sourceY];
        TiledMapNode target = nodes[targetX][targetY];
        openList.add(start);

        while (openList.size() > 0) {
            TiledMapNode currentNode = openList.get(0); // Get the node with the lowest fCost.

            // Make the current node equal to the lowest f cost, or equal f cost and lower h cost.
            for (TiledMapNode compareTo : openList) {
                int fCost = currentNode.calculateFCost();
                int _fCost = compareTo.calculateFCost();
                if (_fCost < fCost || _fCost == fCost && compareTo.hCost < currentNode.hCost) {
                    currentNode = compareTo;
                }
            }

            openList.remove(currentNode);
            closedList.add(currentNode);

            if (currentNode == target) {
                // Found the target node.
                // Gdx.app.log(getClass().getSimpleName(), "found the target node.");
                return retraceSteps(start, target);
            }

            for (TiledMapNode neighbor : getNeighbors(currentNode)) {
                if (!isWalkable(neighbor) || closedList.contains(neighbor)) {
                    continue;
                }

                int newMovementCostToNeighbor = currentNode.gCost + calculateDistance(currentNode, neighbor);

                if (newMovementCostToNeighbor < neighbor.gCost || !openList.contains(neighbor)) {
                    neighbor.gCost = newMovementCostToNeighbor;
                    neighbor.hCost = calculateDistance(neighbor, target);
                    neighbor.parent = currentNode;
                    if (!openList.contains(neighbor)) {
                        openList.add(neighbor);
                    }
                }
            }
        }

        return new ArrayList<>();
    }
}
