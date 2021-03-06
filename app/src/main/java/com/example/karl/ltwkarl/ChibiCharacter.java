package com.example.karl.ltwkarl;

/**
 * Created by karl on 14/10/2016.
 */

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.ListIterator;

public class ChibiCharacter extends GameObject implements Mover {

    private static final String LOG_TAG = ChibiCharacter.class.getSimpleName();

    /** The unit ID moving */
    private int unitType;

    private static final int ROW_TOP_TO_BOTTOM = 2;
    private static final int ROW_RIGHT_TO_LEFT = 1;
    private static final int ROW_LEFT_TO_RIGHT = 3;
    private static final int ROW_BOTTOM_TO_TOP = 0;

    // Row index of Image are being used.
    private int rowUsing = ROW_LEFT_TO_RIGHT;

    private int colUsing;


    private Bitmap[] leftToRights;
    private Bitmap[] rightToLefts;
    private Bitmap[] topToBottoms;
    private Bitmap[] bottomToTops;

    // Velocity of game character (pixel/millisecond)
    public static final float VELOCITY = 0.5f;

    private int speed = 3; // Speed in pixel, TODO stop hardcoding

    private int movingVectorX = 0;
    private int movingVectorY = 0;

    private int xGrid;
    private int yGrid;

    private int healthPoint;


    private long lastDrawNanoTime =-1;

    private GameSurface gameSurface;

    private ArrayList<Point> listPossiblePath;



    public ChibiCharacter(GameSurface gameSurface, Bitmap image, int unitType,  int x, int y) {
        super(image, 4, 9, x, y);

        this.unitType = unitType;

        this.gameSurface= gameSurface;

        this.xGrid = getXGrid(x);
        this.yGrid = getYGrid(y);
//TODO Stop hardcoding that, put in constructor for variable HP
        switch (unitType) {
            case 0:
                this.healthPoint = 1000;
                break;
            case 1:
                this.healthPoint = 3000;
                break;
            case 3:
                this.healthPoint = 10000;
                break;
            default:
                this.healthPoint = 1000;
                break;
        }


        listPossiblePath = gameSurface.getListPossiblePath();

        this.topToBottoms = new Bitmap[colCount]; // 3
        this.rightToLefts = new Bitmap[colCount]; // 3
        this.leftToRights = new Bitmap[colCount]; // 3
        this.bottomToTops = new Bitmap[colCount]; // 3


        for(int col = 0; col< this.colCount; col++ ) {
            this.topToBottoms[col] = this.createSubImageAt(ROW_TOP_TO_BOTTOM, col);
            this.rightToLefts[col]  = this.createSubImageAt(ROW_RIGHT_TO_LEFT, col);
            this.leftToRights[col] = this.createSubImageAt(ROW_LEFT_TO_RIGHT, col);
            this.bottomToTops[col]  = this.createSubImageAt(ROW_BOTTOM_TO_TOP, col);
        }
    }

    public Bitmap[] getMoveBitmaps()  {
        switch (rowUsing)  {
            case ROW_BOTTOM_TO_TOP:
                return  this.bottomToTops;
            case ROW_LEFT_TO_RIGHT:
                return this.leftToRights;
            case ROW_RIGHT_TO_LEFT:
                return this.rightToLefts;
            case ROW_TOP_TO_BOTTOM:
                return this.topToBottoms;
            default:
                return null;
        }
    }

    public Bitmap getCurrentMoveBitmap()  {
        Bitmap[] bitmaps = this.getMoveBitmaps();
        return bitmaps[this.colUsing];
    }

    public int getUnitType() {

        return unitType;
    }

    public int getHealthPoint() {
        return healthPoint;
    }
    public void setHealthPoint(int healthPoint) {
        this.healthPoint = healthPoint;
    }


    public void update()  {

        Log.v(LOG_TAG, "CHIBI HP = " + String.valueOf(healthPoint));
        this.colUsing++;
        if(colUsing >= this.colCount)  {
            this.colUsing =0;
        }
        // Current time in nanoseconds
        long now = System.nanoTime();

        // Never once did draw.
        if(lastDrawNanoTime==-1) {
            lastDrawNanoTime= now;
        }

        xGrid = getXGrid(x);
        yGrid = getYGrid(y);

        if (xGrid == gameSurface.getxGridArrival() && yGrid == gameSurface.getyGridArrival() && healthPoint > 0)
        {
            PlayerManager currentPlayer = gameSurface.getCurrentPlayer();
            healthPoint = 0;
            currentPlayer.removeLife(1); // TODO maybe stop hardcode because some creature do more dmg
            // Little trick here, because when a chibi is kill it generate Gold !!
            currentPlayer.setGoldPlayer(currentPlayer.getGoldPlayer()-1); //TODO Change this trick into a more robust function
        }


        Point pointGrid = new Point(xGrid, yGrid);

        ListIterator listPossiblePathIterator = listPossiblePath.listIterator();

        boolean found = false;
        // TODO how to deal with a creature on a forbidden path?
        // TODO I think I can simplify this with a next node / current node
        while (listPossiblePathIterator.hasNext()) {
            Point possiblePath = (Point)listPossiblePathIterator.next();
            if (found == true)
            {
                if (x % width == 0 && y % height == 0) {
                    if (possiblePath.x - pointGrid.x > 0) {
                        setMovingVector(speed, 0);
                    }
                    else if (possiblePath.x - pointGrid.x < 0) {
                        setMovingVector(-speed, 0);
                    }
                    else if (possiblePath.y - pointGrid.y > 0) {
                        setMovingVector(0, speed);
                    }
                    else if (possiblePath.y - pointGrid.y < 0) {
                        setMovingVector(0, -speed);
                    }
                }
                break;
            }
            else if (pointGrid.equals(possiblePath)) {
                found = true;
            }
        }


        // Calculate the new position of the game character.
        this.x = x +  movingVectorX;
        this.y = y +  movingVectorY;


        // When the game's character touches the edge of the screen, then change direction
        //TODO I let this as an example but it's not useful anymore with shortest Path
//        if(this.x < 0 )  {
//            this.x = 0;
//            this.movingVectorX = - this.movingVectorX;
//        } else if(this.x > this.gameSurface.getWidth() - width)  {
//            this.x = this.gameSurface.getWidth() - width;
//            this.movingVectorX = - this.movingVectorX;
//        }
//
//        if(this.y < 0 )  {
//            this.y = 0;
//            this.movingVectorY = - this.movingVectorY;
//        } else if(this.y > this.gameSurface.getHeight() - height)  {
//            this.y= this.gameSurface.getHeight() - height;
//            this.movingVectorY = - this.movingVectorY ;
//        }

        // rowUsing
        if( movingVectorX > 0 )  {
            if(movingVectorY > 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                this.rowUsing = ROW_TOP_TO_BOTTOM;
            }else if(movingVectorY < 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                this.rowUsing = ROW_BOTTOM_TO_TOP;
            }else  {
                this.rowUsing = ROW_LEFT_TO_RIGHT;
            }
        } else {
            if(movingVectorY > 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                this.rowUsing = ROW_TOP_TO_BOTTOM;
            }else if(movingVectorY < 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                this.rowUsing = ROW_BOTTOM_TO_TOP;
            }else  {
                this.rowUsing = ROW_RIGHT_TO_LEFT;
            }
        }
    }

    public void draw(Canvas canvas)  {
        Bitmap bitmap = this.getCurrentMoveBitmap();
        canvas.drawBitmap(bitmap, x, y, null);
        // Last draw time.
        this.lastDrawNanoTime= System.nanoTime();
    }

    public void setMovingVector(int movingVectorX, int movingVectorY)  {
        this.movingVectorX = movingVectorX;
        this.movingVectorY = movingVectorY;
    }
    /*
    Get the numberof gold in function of chibi level (currently 1, 3, 10)
     */
    public int getGold() {
        int gold;
        switch (this.unitType) {
            case(0):
                gold = 1;
                break;
            case(1):
                gold = 3;
                break;
            case(2):
                gold = 10;
                break;
            default:
                gold = 1;
        }
        return gold;
    }

    public int getXGrid(int x) {

        return x / width;
    }
    public int getYGrid(int y) {

        return y / height;
    }
}