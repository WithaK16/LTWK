package com.example.karl.ltwkarl;

import java.util.ArrayList;

/**
 * Created by karl on 17/10/2016.
 */

public class PlayerManager {

    private int goldPlayer;
    private int lifeLeft;
    private GameSurface gameSurface;

    public PlayerManager(GameSurface gameSurface) {
        this.goldPlayer = 20;
        this.lifeLeft = 5;
        this.gameSurface = gameSurface;
    }

    public void setGoldPlayer(int goldPlayer) {
        this.goldPlayer = goldPlayer;
    }

    public void setLifeLeft(int lifeLeft) {
        this.lifeLeft = lifeLeft;
    }

    public int getGoldPlayer() {
        return goldPlayer;
    }
    public void addGoldPlayer(ArrayList<ChibiCharacter> chibiToKill) {
        //TODO Update gold as a function of the chibi level
        if (this.goldPlayer <= 99) {
            this.goldPlayer = Math.min(chibiToKill.size()+this.goldPlayer, 99);
        }
    }


    public int getLifeLeft() {
        return lifeLeft;
    }
}