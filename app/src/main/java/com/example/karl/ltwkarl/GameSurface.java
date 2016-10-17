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

    /** The path finder we'll use to search our map */
    private PathFinder finder;
    /** The last path found for the current unit */
    private ArrayList<Point> listPossiblePath = new ArrayList();

    // These variable contain width and height of all object (use for calculating the grid etc.)
    private int WIDTH_OBJECT;
    private int HEIGHT_OBJECT;


    private GameThread gameThread;

    public GameSurface(Context context)  {
        super(context);

        // Make Game Surface focusable so it can handle events. .
        this.setFocusable(true);

        // Sét callback.
        this.getHolder().addCallback(this);
    }

    public void update()  {

        for (Tower tower : listTowers) {
            tower.update();
        }
        ArrayList<ChibiCharacter> chibiToKill = new ArrayList<ChibiCharacter>();
        //Explosion if chibi as no more HP
        for (ChibiCharacter chibi : listChibis) {
            if (chibi.getHealthPoint() <= 0) {
                chibiToKill.add(chibi);
                // Create Explosion object.
                Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.explosion);
                Explosion explosion = new Explosion(this, bitmap,chibi.getX(),chibi.getY());
                this.listExplosions.add(explosion);
            }
            chibi.update();
        }
        listChibis.removeAll(chibiToKill);

        for (Explosion explosion : listExplosions) {
            explosion.update();
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
        Bitmap blockBasic1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.block_basic);
        // Setting this important variable at the init of the surface (will be used by other method)
        WIDTH_OBJECT = blockBasic1.getWidth();
        HEIGHT_OBJECT = blockBasic1.getHeight();
        for (int i = 0; i<getHeight()/HEIGHT_OBJECT-1; i++) {
            listTowers.add(new Tower(this, blockBasic1, 0, 2*WIDTH_OBJECT, i*HEIGHT_OBJECT));
        }
        for (int i = getHeight()/HEIGHT_OBJECT-1; i > 0; i--) {
            listTowers.add(new Tower(this, blockBasic1, 0, 4*WIDTH_OBJECT, i*HEIGHT_OBJECT));
        }
        for (int i = 0; i<getHeight()/HEIGHT_OBJECT-1; i++) {
            listTowers.add(new Tower(this, blockBasic1, 0, 6*WIDTH_OBJECT, i*HEIGHT_OBJECT));
        }
        for (int i = getHeight()/HEIGHT_OBJECT-1; i > 0; i--) {
            listTowers.add(new Tower(this, blockBasic1, 0, 8*WIDTH_OBJECT, i*HEIGHT_OBJECT));
        }
        for (int i = 0; i<getHeight()/HEIGHT_OBJECT-1; i++) {
            listTowers.add(new Tower(this, blockBasic1, 0, 10*WIDTH_OBJECT, i*HEIGHT_OBJECT));
        }
        for (int i = 11; i<15; i++) {
            listTowers.add(new Tower(this, blockBasic1, 0, i*WIDTH_OBJECT, 7*HEIGHT_OBJECT));
        }


        // create a possible path grid object and get the list of possible path for the current "round"
        GameMap gameMap = new GameMap(this, listTowers);
        /** The path finder we'll use to search our map */
        finder = new AStarPathFinder(gameMap, 500, false, new ClosestHeuristic());
        listPossiblePath = finder.findPath(new ChibiCharacter(this, chibiBitmap1, 0, 0, 0),
                0, 0,  // TODO STOP HARDCODING, THIS IS DEPARTURE AND ARRIVAL
                11, 0)
                .getPossiblePath();

        listChibis.add(new ChibiCharacter(this, chibiBitmap1, 0, 0, 0));

//        Bitmap chibiBitmap2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.chibi2);
//        listChibis.add(new ChibiCharacter(this, chibiBitmap2, 400, 50));

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

            boolean createChibi = true;
            ArrayList<ChibiCharacter> chibiToKill = new ArrayList<ChibiCharacter>();
            for (Tower tower : listTowers) {
                if ( tower.getX() < x && x < tower.getX() + tower.getWidth()
                        && tower.getY() < y && y < tower.getY()+ tower.getHeight()) {
                    // Change the tower type (to attack if block , to block if attack)
                    tower.setTowerType(1-tower.getTowerType());
                    createChibi = false;
                }
            }

            if (createChibi) {
                Bitmap chibiBitmap1 = BitmapFactory.decodeResource(this.getResources(), chibi1);
                listChibis.add(new ChibiCharacter(this, chibiBitmap1, 0, 0, 0));
            }
            return true;
        }
        return false;
    }

    public ArrayList getListPossiblePath() {

        return this.listPossiblePath;
    }
    public ArrayList<ChibiCharacter> getListChibis() {
        return listChibis;
    }

    public int getWIDTH_OBJECT() {
        return WIDTH_OBJECT;
    }
    public int getHEIGHT_OBJECT() {
        return HEIGHT_OBJECT;
    }


}