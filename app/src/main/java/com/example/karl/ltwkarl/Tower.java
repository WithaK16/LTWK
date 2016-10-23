package com.example.karl.ltwkarl;

import android.graphics.Canvas;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by karl on 14/10/2016.
 */

public class Tower {

    private static final String LOG_TAG = Tower.class.getSimpleName();

    private GameSurface gameSurface;
    private int WIDTH_OBJECT;
    private int HEIGHT_OBJECT;

    private int x;
    private int y;

    // This 3 variables are useful for attack
    private double rangeAttack;
    private int ammunition;
    private int damageAttack;

    private String angleWithTarget = "W";

    private int costTower;

    private int towerType; // 0 if just blocking tower 1 if can attack

    private BulletShot bulletShot;

    private long lastUpdateTime = -1;
    private long countTime = 500;

    public Tower(GameSurface gameSurface, int towerType,  int x, int y) {
        // TODO Bad practice of hardcoding, puting null in a inherited variable etc...
        this.gameSurface = gameSurface;
        this.towerType = towerType;
        this.x = x;
        this.y = y;
        this.WIDTH_OBJECT = gameSurface.getWIDTH_OBJECT();
        this.HEIGHT_OBJECT = gameSurface.getHEIGHT_OBJECT();

    }

    // TODO add upgrading charachteristic in this function
    public void setTowerType (int towerType) {
        this.towerType = towerType;
        // Setting tower characteristics
        double baseRange = Math.hypot(WIDTH_OBJECT, HEIGHT_OBJECT);
        switch (towerType) {
            case(1):
                rangeAttack = baseRange;
                ammunition = 1;
                damageAttack = 2;
                break;
            case(2):
                rangeAttack = 2 * baseRange;
                ammunition = 1;
                damageAttack = 4;
                break;
            case(3):
                rangeAttack = 3 * baseRange;
                ammunition = 2;
                damageAttack = 8;
                break;
            default:
                rangeAttack = baseRange;
                ammunition = 1;
                damageAttack = 2;
                break;
        }
    }

    public int getTowerType() {

        return towerType;
    }


    //TODO add upgrading characteristic in setTowerType function
    //TODO A different way of interaction during construction time?
    public void upgradeTowerType () {
        PlayerManager currentPlayer = gameSurface.getCurrentPlayer();
        switch (towerType) {
            case 0:
                costTower = 2;
                break;
            case 1:
                costTower = 4;
                break;
            case 2:
                costTower = 8;
                break;
        }
        if (currentPlayer.getGoldPlayer() - costTower >= 0 && towerType < 3) {
            currentPlayer.setGoldPlayer(currentPlayer.getGoldPlayer() - costTower);
            setTowerType(towerType + 1);
        } else if (towerType == 3) {
            //TODO stop hardcoding message, should be in string file easier to translate
            Toast.makeText(gameSurface.getContext(), "This tower is already at maximum level", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(gameSurface.getContext(), "Not enough gold to upgrade tower", Toast.LENGTH_SHORT).show();
        }
    }
    //TODO stop hardcoding cost tower (same in upgrade)
    //TODO maybe destroy tower?
    //down grade tower type to block type
    public void downgradeTowerType() {
        if (towerType > 0) {
            switch (towerType) {
                case 1:
                    costTower = -1;
                    break;
                case 2:
                    costTower = -3;
                    break;
                case 3:
                    costTower = -6;
                    break;
            }
            PlayerManager currentPlayer = gameSurface.getCurrentPlayer();

            // If gold after sell is > 99, put 99
            currentPlayer.setGoldPlayer(Math.min(currentPlayer.getGoldPlayer() - costTower, 99));
            setTowerType(0);
        }
    }

    public int getX()  {

        return this.x;
    }

    public int getY()  {

        return this.y;
    }

    //TODO Differentiate between tower that can attack or not (to not loop on block)
    public void update()  {
        long now = System.currentTimeMillis();
        if (lastUpdateTime == -1) {
            //first update
            lastUpdateTime = now;
        }
        countTime = countTime + (now - lastUpdateTime);
        if (towerType == 0){
            return;
        } else {
            int currentAmmunition = ammunition;
            for (ChibiCharacter chibiCharacter : gameSurface.getListChibis()) {
                Log.v(LOG_TAG, "Test value ammunition = " + String.valueOf(ammunition));
                Log.v(LOG_TAG, "Test value currentAmmunition = " + String.valueOf(currentAmmunition));
                if (currentAmmunition == 0) {
                    break; // stop looking for chibi if no more ammunition
                }
                // else if chibis is in range then attack
                else if (Math.hypot((chibiCharacter.getX() - x), (chibiCharacter.getY() - y)) <= rangeAttack) {
                    chibiCharacter.setHealthPoint(chibiCharacter.getHealthPoint() - damageAttack);
                    currentAmmunition -= 1;
                    angleWithTarget = Utilities.getAngle((double) chibiCharacter.getX() - x,
                            (double) chibiCharacter.getY() - y);
                    // Bullet management
                    //TODO see if this trigger point is chose well (width object)
                    if (countTime >= 500) {
                        countTime = 0;
                        bulletShot = new BulletShot(gameSurface.bulletShot, this.x, this.y, chibiCharacter);
                        gameSurface.getListBullet().add(bulletShot);
                    }
                } else {
                    angleWithTarget = "W";
                }
            }
            this.lastUpdateTime= System.currentTimeMillis();
            return;
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
                default:
                    row = 0;
                    col = 2;
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
