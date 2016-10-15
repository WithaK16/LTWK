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
    private static final int WIDTH_GRID = 60; // gameSurface.getWidth() / 32
    /** The height in grid cells of our map */
    private static final int HEIGHT_FACTOR = 29; // gameSurface.getHeight() / 32
    /** The rendered size of the tile (in pixels) */
    public static final int TILE_SIZE = 20;
    /** The actual data for our map */
    private int[][] data = new int[WIDTH_GRID][HEIGHT_FACTOR];
    // List of the possible path
    private ArrayList<Point> listPossiblePath = new ArrayList<Point>();
    // Arrival point
    private static final Point ARRIVAL = new Point(60, 29); // TODO don't hardcode it, put it in some config files or whatever
    //Departure point
    private static final Point DEPARTURE = new Point(0, 0); // TODO Don't hardcode it
    /**
     * Create a new map with some default contents
     */
    private ArrayList<Tower> listTowers = new ArrayList<Tower>();


    public PossiblePathGrid(ArrayList<Tower> listTowers) {

        this.listTowers = listTowers;
        // create some default map data - it would be way
        // cooler to load this from a file and maybe provide
        // a map editor of some sort, but since we're just doing
        // a simple tutorial here we'll manually fill the data
        // with a simple little map

        // MAKE ALL CELL BLOCKED
        for (int y = 0; y< HEIGHT_FACTOR; y++) {
            for (int x = 0; x< WIDTH_GRID; x++) {
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

    public void computeListPossiblePath() {
        listPossiblePath.add(new Point(0,0));
        listPossiblePath.add(new Point(1,0));
        listPossiblePath.add(new Point(1,1));
        listPossiblePath.add(new Point(1,2));
        listPossiblePath.add(new Point(2,2));
        listPossiblePath.add(new Point(3,2));
        listPossiblePath.add(new Point(3,1));
        listPossiblePath.add(new Point(3,0));
        listPossiblePath.add(new Point(4,0));
        listPossiblePath.add(new Point(5,0));
    }

    public ArrayList<Point> getListPossiblePath() {
            computeListPossiblePath();
            return listPossiblePath;
        }

}