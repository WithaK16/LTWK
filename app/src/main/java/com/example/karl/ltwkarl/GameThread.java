package com.example.karl.ltwkarl;

/**
 * Created by karl on 14/10/2016.
 */

import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.ArrayList;

public class GameThread extends Thread {



    private static final String LOG_TAG = GameThread.class.getSimpleName();


    /*
 * State-tracking constants
 */
    public static final int STATE_LOSE = 1;
    public static final int STATE_PAUSE = 2;
    public static final int STATE_READY = 3;
    public static final int STATE_RUNNING = 4;
    public static final int STATE_WIN = 5;

    private static final String KEY_X_TOWER = "mXTower";
    private static final String KEY_Y_TOWER = "mYTower";
    private static final String KEY_TYPE_TOWER = "mTypeTower";

    private static final String KEY_X_CHIBI = "mListChibi";
    private static final String KEY_Y_CHIBI = "mListChibi";

    private static final String KEY_ROUND_LEVEL = "mRoundLevel";
    private static final String KEY_ROUND_FINISHED = "mRoundFinished";
    private static final String KEY_GOLD_PLAYER = "mGoldPlayer";
    private static final String KEY_LIFE_PLAYER = "mLifePlayer";

    private boolean running;
    private GameSurface mGameSurface;
    private SurfaceHolder mSurfaceHolder;

    /** The state of the game. One of READY, RUNNING, PAUSE, LOSE, or WIN */
    private int mMode;

    public GameThread(GameSurface gameSurface, SurfaceHolder surfaceHolder)  {
        this.mGameSurface = gameSurface;
        this.mSurfaceHolder = surfaceHolder;
    }

    @Override
    public void run()  {
        long startTime = System.nanoTime();
        while(running)  {
            Canvas canvas= null;
            try {
                // Get Canvas from Holder and lock it.
                canvas = this.mSurfaceHolder.lockCanvas(null);

                // Synchronized
                synchronized (mSurfaceHolder)  {
                    if (mMode == STATE_RUNNING) this.mGameSurface.update();
                    this.mGameSurface.draw(canvas);
                }
            }catch(Exception e)  {
                // Do nothing.
            } finally {
                if(canvas!= null)  {
                    // Unlock Canvas.
                    this.mSurfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            long now = System.nanoTime() ;
            // Interval to redraw game
            // (Change nanoseconds to milliseconds)
            long waitTime = (now - startTime)/1000000;
            if(waitTime < 10)  {
                waitTime = 10; // Millisecond.
            }
            System.out.print(" Wait Time=" + waitTime);

            try {
                // Sleep.
                this.sleep(waitTime);
            } catch(InterruptedException e)  {

            }
            startTime = System.nanoTime();
            System.out.print(".");
        }
        Log.v(LOG_TAG, "THREAD IS OUT OF WHILE BOUCLE SUPPOSE TO DIE");
    }

    /**
     * Pauses the physics update & animation.
     */
    public void pause() {
        synchronized (mSurfaceHolder) {
            if (mMode == STATE_RUNNING) setState(STATE_PAUSE);
        }
    }

    /**
     * Sets the game mode. That is, whether we are running, paused, in the
     * failure state, in the victory state, etc.
     *
     *
     * @param mode one of the STATE_* constants
     */
    public void setState(int mode) {
        synchronized (mSurfaceHolder) {
            mMode = mode;
        }
    }

public Bundle saveState() {
    Bundle map = new Bundle();
    synchronized (mSurfaceHolder) {

        ArrayList<Integer> xTowers = new ArrayList<>();
        ArrayList<Integer> yTowers = new ArrayList<>();
        ArrayList<Integer> typeTowers = new ArrayList<>();

        for (Tower tower : mGameSurface.getListTowers()) {
            xTowers.add(tower.getX());
            yTowers.add(tower.getY());
            typeTowers.add(tower.getTowerType());
        }
        ArrayList<Integer> xChibis = new ArrayList<>();
        ArrayList<Integer> yChibis = new ArrayList<>();
        //TODO Chibi type
        //ArrayList<Integer> typeChibis = new ArrayList<>();
        for (ChibiCharacter chibiCharacter : mGameSurface.getListChibis()) {
            xChibis.add(chibiCharacter.getX());
            yChibis.add(chibiCharacter.getY());
        }
        //Save tower
        map.putIntegerArrayList(KEY_X_TOWER, xTowers);
        map.putIntegerArrayList(KEY_Y_TOWER, yTowers);
        map.putIntegerArrayList(KEY_TYPE_TOWER, typeTowers);
        // save chibis
        map.putIntegerArrayList(KEY_X_CHIBI, xChibis);
        map.putIntegerArrayList(KEY_Y_CHIBI, yChibis);
        //life gold round etc
        map.putInt(KEY_GOLD_PLAYER, mGameSurface.getCurrentPlayer().getGoldPlayer());
        map.putInt(KEY_LIFE_PLAYER, mGameSurface.getCurrentPlayer().getLifeLeft());
        map.putInt(KEY_ROUND_FINISHED, mGameSurface.getCurrentRound().isRoundFinished());
        map.putInt(KEY_ROUND_LEVEL, mGameSurface.getCurrentRoundLevel());
    }
    return map;
}
    public void restoreSavedState(Bundle savedState) {
        ArrayList<Integer> xTower = savedState.getIntegerArrayList(KEY_X_TOWER);
        ArrayList<Integer> yTower = savedState.getIntegerArrayList(KEY_Y_TOWER);
        ArrayList<Integer> typeTower = savedState.getIntegerArrayList(KEY_TYPE_TOWER);
        ArrayList<Integer> xChibi = savedState.getIntegerArrayList(KEY_X_CHIBI);
        ArrayList<Integer> yChibi = savedState.getIntegerArrayList(KEY_Y_CHIBI);
        //TODO chibi type
        //ArrayList<Integer> xTower = savedState.getIntegerArrayList(KEY_X_TOWER);
        ArrayList<Tower> listTower = mGameSurface.getListTowers();
        ArrayList<ChibiCharacter> listChibis = mGameSurface.getListChibis();
        for (int i=0; i < xTower.size(); i++) {
            listTower.add(new Tower(mGameSurface, 0, xTower.get(i), yTower.get(i)));
            listTower.get(i).setTowerType(typeTower.get(i));
        }
        for (int i=0; i < xChibi.size(); i++) {
            listChibis.add(new ChibiCharacter(mGameSurface, mGameSurface.chibiCharacter, 0, xChibi.get(i), yChibi.get(i)));
        }
        mGameSurface.getCurrentPlayer().setGoldPlayer(savedState.getInt(KEY_GOLD_PLAYER));
        mGameSurface.getCurrentPlayer().setLifeLeft(savedState.getInt(KEY_LIFE_PLAYER));
        mGameSurface.setCurrentRoundLevel(savedState.getInt(KEY_ROUND_LEVEL));
        mGameSurface.getCurrentRound().setRoundFinish(savedState.getInt(KEY_ROUND_FINISHED));

    }


    public void setRunning(boolean running)  {
        this.running= running;
    }
}