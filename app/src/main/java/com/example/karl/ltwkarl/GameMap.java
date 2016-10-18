package com.example.karl.ltwkarl;

import java.util.ArrayList;

/**
 * The data map from our example game. This holds the state and context of each tile
 * on the map. It also implements the interface required by the path finder. It's implementation
 * of the path finder related methods add specific handling for the types of units
 * and terrain in the example game.
 *
 * @author Kevin Glass
 */
public class GameMap implements TileBasedMap {

    private static final String LOG_TAG = GameSurface.class.getSimpleName();

    private static int CELL_GRID_SIZE_PIXEL;
    /** The map width in tiles */
    private static int WIDTH_GRID;
    /** The map height in tiles */
    private static int HEIGHT_GRID ;

    /** Indicate grass terrain at a given location */
    public static final int GRASS = 0;
    /** Indicate water terrain at a given location */
    public static final int TOWER = 1;

    /** The terrain settings for each tile in the map */
    private int[][] terrain;
    /** The unit in each tile of the map */
    private int[][] units;
    /** Indicator if a given tile has been visited during the search */
    private boolean[][] visited ;

    private GameSurface gameSurface;

    /**
     * Create a new test map with some default configuration
     */
    public GameMap(GameSurface gameSurface, ArrayList<Tower> listTowers) {

        this.gameSurface = gameSurface;
        this.CELL_GRID_SIZE_PIXEL = gameSurface.getWIDTH_OBJECT();
        this.WIDTH_GRID = gameSurface.getWidth() / CELL_GRID_SIZE_PIXEL;
        this.HEIGHT_GRID = gameSurface.getHeight() / CELL_GRID_SIZE_PIXEL;
        this.terrain = new int[WIDTH_GRID][HEIGHT_GRID];
        this.units = new int[WIDTH_GRID][HEIGHT_GRID];
        this.visited = new boolean[WIDTH_GRID][HEIGHT_GRID];

        fillArea(0, 0,
                WIDTH_GRID, HEIGHT_GRID,
                GRASS); // Start with filling full grass map

        // Fill the map with towers
        for (Tower tower : listTowers) {
            fillArea(tower.getX()/CELL_GRID_SIZE_PIXEL,
                    tower.getY()/CELL_GRID_SIZE_PIXEL,
                    1, 1, TOWER);
        }
    }

    /**
     * Fill an area with a given terrain type
     *
     * @param x The x coordinate to start filling at
     * @param y The y coordinate to start filling at
     * @param width The width of the area to fill
     * @param height The height of the area to fill
     * @param type The terrain type to fill with
     */
    private void fillArea(int x, int y, int width, int height, int type) {
        for (int xp=x;xp<x+width;xp++) {
            for (int yp=y;yp<y+height;yp++) {
                terrain[xp][yp] = type;
            }
        }
    }

    /**
     * Clear the array marking which tiles have been visted by the path
     * finder.
     */
    public void clearVisited() {
        for (int x=0;x<getWidthInTiles();x++) {
            for (int y=0;y<getHeightInTiles();y++) {
                visited[x][y] = false;
            }
        }
    }

    /**
     * @see TileBasedMap//visited(int, int)
     */
    public boolean visited(int x, int y) {

        return visited[x][y];
    }

    /**
     * Get the terrain at a given location
     *
     * @param x The x coordinate of the terrain tile to retrieve
     * @param y The y coordinate of the terrain tile to retrieve
     * @return The terrain tile at the given location
     */
    public int getTerrain(int x, int y) {
        return terrain[x][y];
    }

    /**
     * Get the unit at a given location
     *
     * @param x The x coordinate of the tile to check for a unit
     * @param y The y coordinate of the tile to check for a unit
     * @return The ID of the unit at the given location or 0 if there is no unit
     */
    public int getUnit(int x, int y) {
        return units[x][y];
    }

    /**
     * Set the unit at the given location
     *
     * @param x The x coordinate of the location where the unit should be set
     * @param y The y coordinate of the location where the unit should be set
     * @param unit The ID of the unit to be placed on the map, or 0 to clear the unit at the
     * given location
     */
    public void setUnit(int x, int y, int unit) {
        units[x][y] = unit;
    }

    /**
     * @see TileBasedMap#blocked(Mover, int, int)
     */
    public boolean blocked(Mover mover, int x, int y) {
//        // if theres a unit at the location, then it's blocked
//
//        if (getUnit(x,y) != 0) {
//            return true;
//        }

        int unit = ((ChibiCharacter) mover).getUnitType();

        // For now, every chibis can move everywhere and are not blocked by other chibi

//        if (unit == PLANE) {
//            return false;
//        }
//        // tanks can only move across grass
//
//        if (unit == TANK) {
//            return terrain[x][y] != GRASS;
//        }
//        // boats can only move across water
//
//        if (unit == BOAT) {
//            return terrain[x][y] != TOWER;
//        }

        // unknown unit so everything blocks

        return terrain[x][y] != GRASS;
    }

    /**
     * @see TileBasedMap#getCost(Mover, int, int, int, int)
     */
    public float getCost(Mover mover, int sx, int sy, int tx, int ty) {

        return 1;
    }

    /**
     * @see TileBasedMap#getHeightInTiles()
     */
    public int getHeightInTiles() {

        return HEIGHT_GRID;
    }

    /**
     * @see TileBasedMap#getWidthInTiles()
     */
    public int getWidthInTiles() {

        return WIDTH_GRID;
    }

    /**
     * @see TileBasedMap#pathFinderVisited(int, int)
     */
    public void pathFinderVisited(int x, int y) {

        visited[x][y] = true;
    }


}

