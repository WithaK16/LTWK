package com.example.karl.ltwkarl;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by karl on 17/10/2016.
 */

public class RoundManager {

    private static final String LOG_TAG = RoundManager.class.getSimpleName();

    private GameSurface gameSurface;
    private boolean isRoundFinish;
    private ArrayList<ChibiCharacter> listChibis;
    // Chibi type as a function of level
    private Bitmap chibiUseForRound;
    private int chibiType;
    private double numberOfChibisLeft;
    private double totalDurationMs;
    // Inverse of frequency of apparation
    private double periodRespawn;
    private double updateTime;
    // Variable use for random period of respawn between 1 and 5s
    private Random r = new Random();
    private int Low = 1000;
    private int High = 5000;



    //Round manager general case
    public RoundManager(GameSurface gameSurface, int roundLevel) {
        this.isRoundFinish = false;
        this.gameSurface = gameSurface;
        // A round currently consist of 2 times round level of chibis
        this.listChibis = gameSurface.getListChibis();
        this.updateTime = System.currentTimeMillis();
        this.periodRespawn = 5000;
        this.totalDurationMs = this.periodRespawn;
        if (roundLevel > 0 && roundLevel < 13) {
            this.chibiUseForRound = gameSurface.chibiCharacterSkeleton;
            this.chibiType = 0;
            this.numberOfChibisLeft = Math.pow(1.3, roundLevel);
        } else if (roundLevel >= 13 && roundLevel < 19) {
            this.chibiUseForRound = gameSurface.chibiCharacterElf;
            this.chibiType = 1;
            this.numberOfChibisLeft = Math.pow(1.3, roundLevel) / 3;
        } else if (roundLevel >= 19) {
            this.chibiUseForRound = gameSurface.chibiCharacterOrc;
            this.chibiType = 2;
            this.numberOfChibisLeft = Math.pow(1.3, roundLevel) / 10;
        }
        // Case where numberOfChibis = 0 (first initial round)
        else if (roundLevel == 0) {
            this.isRoundFinish = true;
            this.totalDurationMs = 0;
        }
    }
    public void update() {
        //Add a chibi every 50 ms
        totalDurationMs += System.currentTimeMillis() - updateTime;
        if (totalDurationMs >= periodRespawn) {
            if (numberOfChibisLeft > 0) {
                listChibis.add(new ChibiCharacter(gameSurface, chibiUseForRound, chibiType, 0, 0));
                numberOfChibisLeft -= 1;
                periodRespawn = r.nextInt(High-Low) + Low;
            }
            else if (listChibis.size() == 0) {
                isRoundFinish = true;
            }
            totalDurationMs = 0;
        }
        else {
            updateTime = System.currentTimeMillis();
        }
    }

    //This function return 1 if round finished, 0 else (use for button)
    public int isRoundFinished() {
        if (isRoundFinish) {
            return 1;
        }
        else {
            return 0;
        }
    }

    public void setRoundFinish(int isRoundFinishInt) {
        if (isRoundFinishInt == 1) {
            this.isRoundFinish = true;
        }
        else if (isRoundFinishInt == 0) {
            this.isRoundFinish = false;
        }
    }


}
