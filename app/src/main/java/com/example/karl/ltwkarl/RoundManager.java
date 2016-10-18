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
    private int numberOfChibisLeft;
    private long totalDurationMs;
    private long updateTime;



    public RoundManager(GameSurface gameSurface, int numberOfChibis) {

        this.isRoundFinish = false;
        this.gameSurface = gameSurface;
        this.numberOfChibisLeft = numberOfChibis;
        this.listChibis = gameSurface.getListChibis();
        this.totalDurationMs = 0;
        this.updateTime = System.currentTimeMillis();

        // Add the first chibi to launch the game
        listChibis.add(new ChibiCharacter(gameSurface, gameSurface.chibiCharacter, 0, 0, 0));
    }

    public void update() {
        //Add a chibi every 50 ms
        totalDurationMs += System.currentTimeMillis() - updateTime;
        if (totalDurationMs >= 5000) {
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


}
