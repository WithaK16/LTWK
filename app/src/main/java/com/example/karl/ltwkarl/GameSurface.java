package com.example.karl.ltwkarl;

/**
 * Created by karl on 14/10/2016.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

import static com.example.karl.ltwkarl.R.drawable.chibi1;

public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {


    private static final String LOG_TAG = GameSurface.class.getSimpleName();

    private ArrayList<ChibiCharacter> listChibis = new ArrayList<ChibiCharacter>();
    private ArrayList<Explosion> listExplosions = new ArrayList<Explosion>();
    private ArrayList<Tower> listTowers = new ArrayList<Tower>();
    private ArrayList<Point> listPossiblePath = new ArrayList<Point>();


    private GameThread gameThread;

    public GameSurface(Context context)  {
        super(context);

        // Make Game Surface focusable so it can handle events. .
        this.setFocusable(true);

        // Sét callback.
        this.getHolder().addCallback(this);
    }

    public void update()  {
        // create a possible path grid object and get the list of possible path for the current "round"
        PossiblePathGrid possiblePathGrid = new PossiblePathGrid(listTowers);
        listPossiblePath = possiblePathGrid.getListPossiblePath();

        for (ChibiCharacter chibi : listChibis) {
            chibi.update();
        }
        for (Explosion explosion : listExplosions) {
            explosion.update();
        }
        for (Tower tower : listTowers) {
            tower.update();
        }
    }

    @Override
    public void draw(Canvas canvas)  {
        super.draw(canvas);
        for (ChibiCharacter chibi : listChibis) {
            chibi.draw(canvas);
        }
        for (Explosion explosion : listExplosions) {
            explosion.draw(canvas);
        }
        for (Tower tower : listTowers) {
            tower.draw(canvas);
        }
    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder holder) {



        Bitmap chibiBitmap1 = BitmapFactory.decodeResource(this.getResources(), chibi1);
        listChibis.add(new ChibiCharacter(this, chibiBitmap1, 0, 0));

//        Bitmap chibiBitmap2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.chibi2);
//        listChibis.add(new ChibiCharacter(this, chibiBitmap2, 400, 50));
//
//        Bitmap blockBasic1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.block_basic);
//        listTowers.add(new Tower(this, blockBasic1, 50, 50));



        this.gameThread = new GameThread(this, holder);
        this.gameThread.setRunning(true);
        this.gameThread.start();
    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry= true;
        while(retry) {
            try {
                this.gameThread.setRunning(false);

                // Parent thread must wait until the end of GameThread.
                this.gameThread.join();
            }catch(InterruptedException e)  {
                e.printStackTrace();
            }
            retry= true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int)event.getX();
            int y = (int)event.getY();

            ArrayList<ChibiCharacter> chibiToKill = new ArrayList<ChibiCharacter>();
            //Explosion if you touch chibi
            for (ChibiCharacter chibi : listChibis) {
                if ( chibi.getX() < x && x < chibi.getX() + chibi.getWidth()
                        && chibi.getY() < y && y < chibi.getY()+ chibi.getHeight()) {
                    chibiToKill.add(chibi);
                    // Create Explosion object.
                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.explosion);
                    Explosion explosion = new Explosion(this, bitmap,chibi.getX(),chibi.getY());
                    this.listExplosions.add(explosion);
                }
                else {
                    // Moving if you didn't touch a chibi
                    int movingVectorX = x - chibi.getX();
                    int movingVectorY = y - chibi.getY();
                    chibi.setMovingVector(movingVectorX, movingVectorY);
                }
            }
            listChibis.removeAll(chibiToKill);
            return true;
        }
        return false;
    }

    public ArrayList<Point> getListPossiblePath() {
        return this.listPossiblePath;
    }


}