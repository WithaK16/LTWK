package com.example.karl.ltwkarl;

import android.graphics.Canvas;

import java.util.ArrayList;

/**
 * Created by karl on 19/10/2016.
 */

public class PossibleConstructionGrid {

    private GameSurface gameSurface;
    private GameMap currentGameMap;
    private ArrayList<Tower> listTowers;
    private static int CELL_GRID_SIZE_PIXEL;
    /** The map width in tiles */
    private static int WIDTH_GRID;
    /** The map height in tiles */
    private static int HEIGHT_GRID;

    private int[][] possibleGrid;

    private int IMPOSSIBLE = 1;
    private int POSSIBLE = 0;
    private int NONCONSTRUCTION = -1;

    public PossibleConstructionGrid(GameSurface gameSurface) {
        this.gameSurface = gameSurface;
        this.CELL_GRID_SIZE_PIXEL = gameSurface.getWIDTH_OBJECT();
        this.listTowers = gameSurface.getListTowers();
        this.currentGameMap = new GameMap(gameSurface, listTowers);
        this.WIDTH_GRID = currentGameMap.getWidthInTiles();
        this.HEIGHT_GRID = currentGameMap.getHeightInTiles();
        this.possibleGrid = new int[WIDTH_GRID][HEIGHT_GRID];

        for (int i = 0; i < WIDTH_GRID; i++) {
            for (int j = 0; j < HEIGHT_GRID; j++) {
                // This long condition is a summary of impossible building zone (departure + menu)
                if (currentGameMap.getTerrain(i, j) != 0 ||
                        (i == 0 && j == 0) || // TODO STOP HARDCODING, THIS IS DEPARTURE AND ARRIVAL
                        (i==gameSurface.getxGridArrival() && j==gameSurface.getyGridArrival()) ||
                        (i > WIDTH_GRID - 8 && j < 2)) {
                    possibleGrid[i][j] = NONCONSTRUCTION;
                }
                else {
                    GameMap tmpGameMap = new GameMap(gameSurface, listTowers);
                    tmpGameMap.fillArea(i, j, 1, 1, 1);
                    AStarPathFinder finder = new AStarPathFinder(tmpGameMap, 500, false, new ClosestHeuristic());
                    Path isPathPossible = finder.findPath(null,
                            0, 0,  // TODO STOP HARDCODING, THIS IS DEPARTURE AND ARRIVAL
                            gameSurface.getxGridArrival(), gameSurface.getyGridArrival());
                    if (isPathPossible == null) {
                        possibleGrid[i][j] = IMPOSSIBLE;
                    }
                    else {
                        possibleGrid[i][j] = POSSIBLE;
                    }
                }
            }
        }
    }

    public void draw(Canvas canvas) {
        for (int i = 0; i < WIDTH_GRID; i++) {
            for (int j = 0; j < HEIGHT_GRID; j++) {
                int val = possibleGrid[i][j];
                if (val != NONCONSTRUCTION) {
                    canvas.drawBitmap(gameSurface.possibleConstruction[val],
                            i*CELL_GRID_SIZE_PIXEL,
                            j*CELL_GRID_SIZE_PIXEL, null);
                }
            }
        }
    }



    public int[][] getPossibleGrid() {

        return this.possibleGrid;
    }
}
