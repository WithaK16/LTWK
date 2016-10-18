package com.example.karl.ltwkarl;

/**
 * Created by karl on 14/10/2016.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

import static com.example.karl.ltwkarl.R.drawable.block_tower;
import static com.example.karl.ltwkarl.R.drawable.block_tower_attack_1;
import static com.example.karl.ltwkarl.R.drawable.block_tower_attack_2;
import static com.example.karl.ltwkarl.R.drawable.block_tower_attack_3;
import static com.example.karl.ltwkarl.R.drawable.build_your_tower;
import static com.example.karl.ltwkarl.R.drawable.build_your_tower_locked;
import static com.example.karl.ltwkarl.R.drawable.chibi1;
import static com.example.karl.ltwkarl.R.drawable.dollar_img;
import static com.example.karl.ltwkarl.R.drawable.figures_img;

public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {


    private static final String LOG_TAG = GameSurface.class.getSimpleName();

    private ArrayList<ChibiCharacter> listChibis = new ArrayList<ChibiCharacter>();
    private ArrayList<Explosion> listExplosions = new ArrayList<Explosion>();
    private ArrayList<Tower> listTowers = new ArrayList<Tower>();
    private RoundManager currentRound;
    private PlayerManager currentPlayer;


    /** The path finder we'll use to search our map */
    private PathFinder finder;
    /** The last path found for the current unit */
    private ArrayList<Point> listPossiblePath = new ArrayList();

    // These variable contain width and height of all object (use for calculating the grid etc.)
    private int WIDTH_OBJECT;
    private int HEIGHT_OBJECT;

    //Variable containing every bitmap used during the game
    private Bitmap scaledBackground;
    public Bitmap chibiCharacter;
    public Bitmap blockTower;
    public Bitmap[][] attackTower1;
    public Bitmap[][] attackTower2;
    public Bitmap[][] attackTower3;
    private Bitmap dollarSign;
    private Bitmap[] figures = new Bitmap[10];
    private Bitmap lifeLeft;
    private Bitmap[] buildButton = new Bitmap[2];

    // This is useful for long click management
    private boolean longClickFlag = false;
    private final Handler handler;
    private Runnable mLongPressed;
    private int initialTouchX;
    private int initialTouchY;


    private GameThread gameThread;

    public GameSurface(Context context)  {
        super(context);

        // Make Game Surface focusable so it can handle events. .
        this.setFocusable(true);
        // Sét callback.
        this.getHolder().addCallback(this);

        //this.setZOrderOnTop(true);
        this.getHolder().setFormat(PixelFormat.TRANSPARENT);

        handler = new Handler();
        mLongPressed = new Runnable() {
            public void run() {
                longClickFlag = true;
            }
        };

    }

    public void update()  {

        // First update the current round
        currentRound.update();

        //update the tower
        for (Tower tower : listTowers) {
            tower.update();
        }

        //update Chibis
        //TODO maybe add that into RoundManager?
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
        // kill chibi and update gold
        //TODO Update gold as a function of the chibi level (into function addGoldPlayer)
        currentPlayer.addGoldPlayer(chibiToKill);
        listChibis.removeAll(chibiToKill);

        //update explosion
        for (Explosion explosion : listExplosions) {
            explosion.update();
        }
    }

    @Override
    public void draw(Canvas canvas)  {
        super.draw(canvas);
        //Drawing background
        canvas.drawBitmap(scaledBackground, 0, 0, null);


        //Draw Buttons
        canvas.drawBitmap(buildButton[currentRound.isRoundFinished()], 1300, 55, null);
        //Draw current gold (power of ten + rest) and life
        //x=1200, y = 50 is arbitrary value
        canvas.drawBitmap(figures[currentPlayer.getGoldPlayer() / 10], 1500, 50, null);
        canvas.drawBitmap(figures[currentPlayer.getGoldPlayer() % 10], 1500+(WIDTH_OBJECT/2), 50, null);
        canvas.drawBitmap(dollarSign, 1500+WIDTH_OBJECT, 50, null);
        canvas.drawBitmap(figures[currentPlayer.getLifeLeft()], 1500+(2*WIDTH_OBJECT), 50, null);
        canvas.drawBitmap(lifeLeft, 1500+(3*WIDTH_OBJECT), 50, null);



        for (Tower tower : listTowers) {
            tower.draw(canvas);
        }
        for (ChibiCharacter chibi : listChibis) {
            chibi.draw(canvas);
        }
        for (Explosion explosion : listExplosions) {
            explosion.draw(canvas);
        }

    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        // Setting this important variable at the init of the surface (will be used by other method)
        // TODO Find a way to make it possible to use by other phone and not hardcode
        WIDTH_OBJECT = 96; // WARNING THE GAME IS DESIGN AROUND THE VALUE OF THIS 32 PX
        HEIGHT_OBJECT = 96; // WARNING THE GAME IS DESIGN AROUND THE VALUE OF THIS 32 PX


        //Create a player
        this.currentPlayer = new PlayerManager(this);

        //Create a scalable background
        Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.snow_template1);
        float scale = (float)background.getHeight()/(float)getHeight();
        int newWidth = Math.round(background.getWidth()/scale);
        int newHeight = Math.round(background.getHeight()/scale);
        scaledBackground = Bitmap.createScaledBitmap(background, newWidth, newHeight, true);

        //Create buttons
        buildButton[1] = BitmapFactory.decodeResource(this.getResources(), build_your_tower); // Button unlock
        buildButton[0] = BitmapFactory.decodeResource(this.getResources(), build_your_tower_locked); // Button lock

        // init figures_img and dollar_img sign
        dollarSign = BitmapFactory.decodeResource(this.getResources(), dollar_img);
        for(int i=0; i<10; i++){
            figures[i] = Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), figures_img),
                    i*WIDTH_OBJECT, 0, WIDTH_OBJECT, HEIGHT_OBJECT);
        }
        //init heart icon
        lifeLeft = BitmapFactory.decodeResource(getResources(), R.drawable.heart_img);

        // Create all the bitmap and scale them
        chibiCharacter = BitmapFactory.decodeResource(this.getResources(), chibi1);
        blockTower = BitmapFactory.decodeResource(this.getResources(), block_tower);
        blockTower = Bitmap.createBitmap(blockTower, 0, 0, WIDTH_OBJECT, HEIGHT_OBJECT);
        Bitmap attackTower1_tot = BitmapFactory.decodeResource(this.getResources(), block_tower_attack_1);
        Bitmap attackTower2_tot = BitmapFactory.decodeResource(this.getResources(), block_tower_attack_2);
        Bitmap attackTower3_tot = BitmapFactory.decodeResource(this.getResources(), block_tower_attack_3);

        attackTower1 = new Bitmap[2][4];
        attackTower2 = new Bitmap[2][4];
        attackTower3 = new Bitmap[2][4];

        // CREATE SPRITE FOR tower_attack_0
        for (int row = 0; row < 2; row++) {
            for (int column = 0; column < 4; column++) {
                attackTower1[row][column] = Bitmap.createBitmap(attackTower1_tot,
                        column * WIDTH_OBJECT, row* HEIGHT_OBJECT , WIDTH_OBJECT, HEIGHT_OBJECT);
                attackTower2[row][column] = Bitmap.createBitmap(attackTower2_tot,
                        column * WIDTH_OBJECT, row* HEIGHT_OBJECT , WIDTH_OBJECT, HEIGHT_OBJECT);
                attackTower3[row][column] = Bitmap.createBitmap(attackTower3_tot,
                        column * WIDTH_OBJECT, row* HEIGHT_OBJECT , WIDTH_OBJECT, HEIGHT_OBJECT);
            }
        }

        // CREATE AN INIT MAP
        // TODO Make it cleaner, more flexible etc.
        for (int i = 0; i<getHeight()/HEIGHT_OBJECT-1; i++) {
            listTowers.add(new Tower(this, 1, 2*WIDTH_OBJECT, i*HEIGHT_OBJECT));
        }
        for (int i = getHeight()/HEIGHT_OBJECT-1; i > 0; i--) {
            listTowers.add(new Tower(this, 2, 4*WIDTH_OBJECT, i*HEIGHT_OBJECT));
        }
        for (int i = 0; i<getHeight()/HEIGHT_OBJECT-1; i++) {
            listTowers.add(new Tower(this, 3, 6*WIDTH_OBJECT, i*HEIGHT_OBJECT));
        }
        for (int i = getHeight()/HEIGHT_OBJECT-1; i > 0; i--) {
            listTowers.add(new Tower(this, 0, 8*WIDTH_OBJECT, i*HEIGHT_OBJECT));
        }
        for (int i = 0; i<getHeight()/HEIGHT_OBJECT-1; i++) {
            listTowers.add(new Tower(this, 0, 10*WIDTH_OBJECT, i*HEIGHT_OBJECT));
        }
        for (int i = 11; i<15; i++) {
            listTowers.add(new Tower(this, 0, i*WIDTH_OBJECT, 7*HEIGHT_OBJECT));
        }

        // create a possible path grid object and get the list of possible path for the current "round"
        GameMap gameMap = new GameMap(this, listTowers);
        /** The path finder we'll use to search our map */
        finder = new AStarPathFinder(gameMap, 500, false, new ClosestHeuristic());
        listPossiblePath = finder.findPath(new ChibiCharacter(this, chibiCharacter, 0, 0, 0),
                0, 0,  // TODO STOP HARDCODING, THIS IS DEPARTURE AND ARRIVAL
                11, 0)
                .getPossiblePath();

        this.currentRound = new RoundManager(this, 20);

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

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialTouchX = (int)event.getX();
                initialTouchY = (int)event.getY();
                handler.postDelayed(mLongPressed, 1000);
                break;
            case MotionEvent.ACTION_UP:
                handler.removeCallbacks(mLongPressed);
                // CODE FOR SINGLE CLICK HERE
                if(!longClickFlag) {
                    for (Tower tower : listTowers) {
                        if ( tower.getX() < initialTouchX && initialTouchX < tower.getX() + WIDTH_OBJECT
                                && tower.getY() < initialTouchY && initialTouchY < tower.getY()+ HEIGHT_OBJECT) {
                            // UpgradetowerType
                            tower.upgradeTowerType();
                        }
                    }
                    return false;
                } // CODE FOR LONG CLICK HERE (longclickFlag is true)
                else {
                    for (Tower tower : listTowers) {
                        if ( tower.getX() < initialTouchX && initialTouchX < tower.getX() + WIDTH_OBJECT
                                && tower.getY() < initialTouchY && initialTouchY < tower.getY()+ HEIGHT_OBJECT) {
                            // UpgradetowerType
                            tower.downgradeTowerType();
                        }
                    }
                    longClickFlag = false;
                }
                break;
        }
        return true;
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

    public PlayerManager getCurrentPlayer() {
        return currentPlayer;
    }



}