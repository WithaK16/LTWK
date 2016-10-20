package com.example.karl.ltwkarl;

import java.util.ArrayList;

/**
 * Created by karl on 17/10/2016.
 */

public class RoundManager {

    private static final String LOG_TAG = RoundManager.class.getSimpleName();

    private GameSurface gameSurface;
    private boolean isRoundFinish;
    private ArrayList<ChibiCharacter> listChibis;
    private double numberOfChibisLeft;
    private long totalDurationMs;
    // Inverse of frequency of apparation
    private double periodRespawn;
    private double updateTime;



    //Round manager general case
    public RoundManager(GameSurface gameSurface, int roundLevel) {

        this.isRoundFinish = false;
        this.gameSurface = gameSurface;
        // A round currently consist of 2 times round level of chibis
        this.numberOfChibisLeft = Math.pow(1.3, roundLevel);
        this.listChibis = gameSurface.getListChibis();
        this.totalDurationMs = 0;
        this.updateTime = System.currentTimeMillis();
        this.periodRespawn = 5000 / (double)roundLevel;
        // Add the first chibi to launch the game
        if (roundLevel == 0) {
            this.isRoundFinish = true;  // Case where numberOfChibis = 0 (first initial round)
        }
        else {
            listChibis.add(new ChibiCharacter(gameSurface, gameSurface.chibiCharacter, 0, 0, 0));
        }
    }

    public void update() {
        //Add a chibi every 50 ms
        totalDurationMs += System.currentTimeMillis() - updateTime;
        if (totalDurationMs >= periodRespawn) {
            if (numberOfChibisLeft > 0) {
                listChibis.add(new ChibiCharacter(gameSurface, gameSurface.chibiCharacter, 0, 0, 0));
                numberOfChibisLeft -= 1;
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
