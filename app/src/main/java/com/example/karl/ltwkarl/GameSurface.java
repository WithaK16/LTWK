package com.example.karl.ltwkarl;

/**
 * Created by karl on 14/10/2016.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

import static com.example.karl.ltwkarl.R.drawable.chibi1;

public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {


    private static final String LOG_TAG = GameSurface.class.getSimpleName();

    private ArrayList<ChibiCharacter> chibiList = new ArrayList<ChibiCharacter>();
    private ArrayList<Explosion> explosionsList = new ArrayList<Explosion>();
    private ArrayList<Tower> towersList = new ArrayList<Tower>();



    private GameThread gameThread;

    public GameSurface(Context context)  {
        super(context);

        // Make Game Surface focusable so it can handle events. .
        this.setFocusable(true);

        // Sét callback.
        this.getHolder().addCallback(this);
    }

    public void update()  {
        for (ChibiCharacter chibi : chibiList) {
            chibi.update();
        }
        for (Explosion explosion : explosionsList) {
            explosion.update();
        }
        for (Tower tower : towersList) {
            tower.update();
        }
    }

    @Override
    public void draw(Canvas canvas)  {
        super.draw(canvas);
        for (ChibiCharacter chibi : chibiList) {
            chibi.draw(canvas);
        }
        for (Explosion explosion : explosionsList) {
            explosion.draw(canvas);
        }
        for (Tower tower : towersList) {
            tower.draw(canvas);
        }
    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        Bitmap chibiBitmap1 = BitmapFactory.decodeResource(this.getResources(), chibi1);
        chibiList.add(new ChibiCharacter(this, chibiBitmap1, 100, 50));

        Bitmap chibiBitmap2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.chibi2);
        chibiList.add(new ChibiCharacter(this, chibiBitmap2, 400, 50));

        Bitmap blockBasic1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.block_basic);
        towersList.add(new Tower(this, blockBasic1, 50, 50));

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
            for (ChibiCharacter chibi : chibiList) {
                if ( chibi.getX() < x && x < chibi.getX() + chibi.getWidth()
                        && chibi.getY() < y && y < chibi.getY()+ chibi.getHeight()) {
                    chibiToKill.add(chibi);
                    // Create Explosion object.
                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.explosion);
                    Explosion explosion = new Explosion(this, bitmap,chibi.getX(),chibi.getY());
                    this.explosionsList.add(explosion);
                }
                else {
                    // Moving if you didn't touch a chibi
                    int movingVectorX = x - chibi.getX();
                    int movingVectorY = y - chibi.getY();
                    chibi.setMovingVector(movingVectorX, movingVectorY);
                }
            }
            chibiList.removeAll(chibiToKill);
            return true;
        }
        return false;
    }

}