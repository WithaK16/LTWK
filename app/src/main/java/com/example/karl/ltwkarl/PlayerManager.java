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

    public void removeLife(int damage) {
        lifeLeft = Math.max(lifeLeft - damage, 0);
    }

    public int getGoldPlayer() {
        return goldPlayer;
    }

    public void addGoldPlayer(ArrayList<ChibiCharacter> chibiToKill) {
        for (ChibiCharacter chibi : chibiToKill) {
            if (this.goldPlayer <= 99) {
                this.goldPlayer = Math.min(this.goldPlayer + chibi.getGold(), 99);
            }
        }
    }


    public int getLifeLeft() {
        return lifeLeft;
    }
}
