package com.example.karl.ltwkarl;

import android.graphics.Canvas;

/**
 * Created by karl on 14/10/2016.
 */

public class Tower {

    private static final String LOG_TAG = Tower.class.getSimpleName();

    private GameSurface gameSurface;

    private int x;
    private int y;

    // This 3 variables are useful for attack
    private double rangeAttack;
    private int ammunition;
    private int damageAttack;

    private String angleWithTarget;

    private int towerType; // 0 if just blocking tower 1 if can attack

    public Tower(GameSurface gameSurface, int towerType,  int x, int y) {
        // TODO Bad practice of hardcoding, puting null in a inherited variable etc...
        this.gameSurface = gameSurface;
        this.towerType = towerType;
        this.x = x;
        this.y = y;
        // TODO right now range is constant and within a circle around tower
        // Add in constructor for variable range
        this.rangeAttack = Math.hypot(gameSurface.getWIDTH_OBJECT(), gameSurface.getHEIGHT_OBJECT());
        //TODO Same, add constructor for ammunition, right now it's hardcode as 1 ammu per update
        this.ammunition = 1;
        //TODO Same
        this.damageAttack = 2;
    }

    public void setTowerType (int towerType) {
        this.towerType = towerType;
    }

    public int getTowerType() {

        return towerType;
    }
    public void upgradeTowerType () {
        if (towerType == 3) {
            towerType = 0;
        }
        else {
            towerType += 1;
        }
    }
    public int getX()  {

        return this.x;
    }

    public int getY()  {

        return this.y;
    }

    public void update()  {

        if (towerType == 0){
            return;
        }
        //TODO Differentiate between tower that can attack or not (to not loop on block)
        ammunition = 1; //TODO I'm sure there is a better way to do that
        for (ChibiCharacter chibiCharacter : gameSurface.getListChibis()) {
            if (ammunition == 0) {
                break; // stop looking for chibi if no more ammunition
            }
            // else if chibis is in range then attack
            else if (Math.hypot((chibiCharacter.getX()-x), (chibiCharacter.getY()-y)) <= rangeAttack) {
                chibiCharacter.setHealthPoint(chibiCharacter.getHealthPoint() - damageAttack);
                ammunition = ammunition -1;
                angleWithTarget = Utilities.getAngle((double)chibiCharacter.getX()-x,
                        (double)chibiCharacter.getY()-y);
            }
            else {
                angleWithTarget = "W";
            }
        }
    }

    public void draw(Canvas canvas) {
        // If towertype == 0 then it's fix, then no need to calculate

        if (towerType == 0) {
            canvas.drawBitmap(gameSurface.blockTower, this.x, this.y, null);
        } else {
            int row = 0;
            int col = 0;
            switch (angleWithTarget) {
                case "N":
                    row = 0;
                    col = 0;
                    break;
                case "NW":
                    row = 0;
                    col = 1;
                    break;
                case "W":
                    row = 0;
                    col = 2;
                    break;
                case "SW":
                    row = 0;
                    col = 3;
                    break;
                case "S":
                    row = 1;
                    col = 0;
                    break;
                case "SE":
                    row = 1;
                    col = 1;
                    break;
                case "E":
                    row = 1;
                    col = 2;
                    break;
                case "NE":
                    row = 1;
                    col = 3;
                    break;
            }
            if (towerType == 1) {
                canvas.drawBitmap(gameSurface.attackTower1[row][col], this.x, this.y, null);
            } else if (towerType == 2) {
                canvas.drawBitmap(gameSurface.attackTower2[row][col], this.x, this.y, null);
            } else if (towerType == 3) {
                canvas.drawBitmap(gameSurface.attackTower3[row][col], this.x, this.y, null);
            }
        }
    }


}
