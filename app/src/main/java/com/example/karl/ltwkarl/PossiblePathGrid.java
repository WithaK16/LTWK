package com.example.karl.ltwkarl;

import android.graphics.Point;

import java.util.ArrayList;


/**
 * Created by karl on 15/10/2016.
 */

public class PossiblePathGrid {
    /** The value indicating a clear cell */
    private static final int CLEAR = 0;
    /** The value indicating a blocked cell */
    private static final int BLOCKED = 1;
    /** The width in grid cells of our map */
    private static final int WIDTH = 60; // gameSurface.getWidth() / 32
    /** The height in grid cells of our map */
    private static final int HEIGHT = 29; // gameSurface.getHeight() / 32
    /** The rendered size of the tile (in pixels) */
    public static final int TILE_SIZE = 20;
    /** The actual data for our map */
    private int[][] data = new int[WIDTH][HEIGHT];
    // List of the possible path
    private ArrayList<Point> listPossiblePath;
    /**
     * Create a new map with some default contents
     */

    //Reference to the gameSurface
    private GameSurface gameSurface;

    public PossiblePathGrid(GameSurface gameSurface) {
        // create some default map data - it would be way
        // cooler to load this from a file and maybe provide
        // a map editor of some sort, but since we're just doing
        // a simple tutorial here we'll manually fill the data
        // with a simple little map
        this.gameSurface = gameSurface;

        // MAKE ALL CELL BLOCKED
        for (int y=0;y<HEIGHT;y++) {
            for (int x=0; x<WIDTH;x++) {
                data[x][y] = BLOCKED;
            }
        }
        data[0][0] = CLEAR;
        data[1][0] = CLEAR;
        data[1][1] = CLEAR;
        data[1][2] = CLEAR;
        data[2][2] = CLEAR;
        data[3][2] = CLEAR;
        data[3][1] = CLEAR;
        data[3][0] = CLEAR;
        data[4][0] = CLEAR;
        data[5][0] = CLEAR;
    }

    public int[][] getData() {
        return this.data;
    }

    public void computeListPossiblePath() {
        for (int y=0 ; y<HEIGHT; y++) {
            for (int x=0; x<WIDTH; x++)
            {
                if (data[x][y] == CLEAR) {
                    listPossiblePath.add(new Point(x, y));
                }
            }
        }
    }

    public ArrayList<Point> getListPossiblePath() {
        if (listPossiblePath != null) {
            return listPossiblePath;
        }
        else {
            computeListPossiblePath();
            return listPossiblePath;
        }
    }

}