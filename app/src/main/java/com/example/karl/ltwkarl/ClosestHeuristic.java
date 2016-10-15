package com.example.karl.ltwkarl;

import java.lang.Math;

/**
 * A heuristic that uses the tile that is closest to the target
 * as the next best tile.
 *
 * @author Kevin Glass
 */
public class ClosestHeuristic implements AStarHeuristic {
    /**
     * @see AStarHeuristic#getCost(TileBasedMap, Mover, int, int, int, int)
     */
    public float getCost(TileBasedMap map, Mover mover, int x, int y, int tx, int ty) {
        int dx = Math.abs(tx - x);
        int dy = Math.abs(ty - y);

        return dx + dy;
    }

}