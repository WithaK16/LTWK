package com.example.karl.ltwkarl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

/**
 * Created by karl on 14/10/2016.
 */

public class Tower extends GameObject {


    private static final String LOG_TAG = Tower.class.getSimpleName();

    private GameSurface gameSurface;
    // This 3 variables are useful for attack
    private double rangeAttack;
    private int ammunition;
    private int damageAttack;


    private int towerType; // 0 if just blocking tower 1 if can attack

    public Tower(GameSurface gameSurface, Bitmap image, int towerType,  int x, int y) {
        super(image, 1, 1, x, y);
        this.gameSurface = gameSurface;
        this.towerType = towerType;
        // TODO right now range is constant and within a circle around tower
        // Add in constructor for variable range
        this.rangeAttack = Math.hypot(width, height);
        //TODO Same, add constructor for ammunition, right now it's hardcode as 1 ammu per update
        this.ammunition = 1;
        //TODO Same
        this.damageAttack = 2;
    }

    public void setTowerType (int towerType) {
        // TODO How to deal with the bitmap image? Feel like redunduncy between gamesurface and tower
        // TODO Implement a better way to deal with it
        if (towerType == 0) {
            this.image = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.block_basic);
            this.towerType = towerType;
        }
        else if (towerType == 1) {
            this.image = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.block_tower_attack_2);
            this.towerType = towerType;
        }

    }

    public int getTowerType() {
        return towerType;
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
            }
        }
    }



    public void draw(Canvas canvas)  {
        canvas.drawBitmap(this.image, this.x, this.y, null);
    }
}
