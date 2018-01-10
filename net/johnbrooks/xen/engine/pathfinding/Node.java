package net.johnbrooks.xen.engine.pathfinding;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Node implements Comparable<Node>{
    // G cost = distance from the starting point
    // H cost = distance from the endoing point
    // F cost = g + h

    protected int x, y;
    public int hCost;
    public int gCost;

    public Node parent;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int calculateFCost() {
        return hCost + gCost;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void render(ShapeRenderer shapeRenderer) {

    }

//    @Override
//    public boolean equals(Object o) {
//        if (o instanceof Node) {
//            Node node = (Node) o;
//            return node.x == x && node.y == y;
//        } else {
//            return false;
//        }
//    }

    @Override
    public int compareTo(Node o) {
        return Integer.compare(calculateFCost(), o.calculateFCost());
    }

    @Override
    public String toString() {
        return "(" + x + " " + y + ")";
    }
}
