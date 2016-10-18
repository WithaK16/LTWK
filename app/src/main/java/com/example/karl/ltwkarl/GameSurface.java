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
import android.util.Log;
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


    private GameMap gameMap;
    /** The path finder we'll use to search our map */
    private PathFinder finder;
    /** The last path found for the current unit */
    private ArrayList<Point> listPossiblePath = new ArrayList();

    private int xGridArrival;
    private int yGridArrival;

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
    private Bitmap deadZone;
    private Bitmap[] buildButton = new Bitmap[2];

    //Variable for button, gold and life coordinate
    private final int xButtonBuild = 1300;
    private final int yButtonBuild = 50;

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
        canvas.drawBitmap(buildButton[currentRound.isRoundFinished()], xButtonBuild, yButtonBuild + 5, null);
        //Draw current gold (power of ten + rest) and life
        //x=1200, y = 50 is arbitrary value
        int goldPlayer = Math.max(currentPlayer.getGoldPlayer(), 0); // this to manage display for the chibi trick when dead
        canvas.drawBitmap(figures[goldPlayer / 10], xButtonBuild+200, yButtonBuild, null);
        canvas.drawBitmap(figures[goldPlayer % 10], 1500+(WIDTH_OBJECT/2), yButtonBuild, null);
        canvas.drawBitmap(dollarSign, xButtonBuild+200+WIDTH_OBJECT, yButtonBuild, null);
        canvas.drawBitmap(figures[currentPlayer.getLifeLeft()], xButtonBuild+200+(2*WIDTH_OBJECT), yButtonBuild, null);
        canvas.drawBitmap(lifeLeft, xButtonBuild+200+(3*WIDTH_OBJECT), yButtonBuild, null);

        canvas.drawBitmap(deadZone, xGridArrival*WIDTH_OBJECT, yGridArrival*HEIGHT_OBJECT, null);


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
        //init heart & dead zone icon
        lifeLeft = BitmapFactory.decodeResource(getResources(), R.drawable.heart_img);
        deadZone = BitmapFactory.decodeResource(getResources(), R.drawable.dead_zone_img);

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

        this.currentRound = new RoundManager(this, 0);
        xGridArrival = (getWidth() / WIDTH_OBJECT) - 1;
        yGridArrival = getHeight() / (HEIGHT_OBJECT*2);

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

    //TODO Divide this function in smaller part (case round finish case round not finish)
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //Click handling between round (= construction time)
        if (currentRound.isRoundFinished() == 1) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialTouchX = (int) event.getX();
                    initialTouchY = (int) event.getY();

                    // condition to make it impossible to build on menu and new round if you click on Build
                    if ((getWidth() - 7*WIDTH_OBJECT) < initialTouchX && initialTouchY < 2 * HEIGHT_OBJECT) {
                        if (xButtonBuild < initialTouchX && initialTouchX < xButtonBuild + buildButton[0].getWidth()
                                && yButtonBuild+5 < initialTouchY && initialTouchY < yButtonBuild+5 + buildButton[0].getHeight())
                        {
                            Log.v(LOG_TAG, "I clicked on Build");
                            currentRound = new RoundManager(this, 5);
                            // create a possible path grid object and get the list of possible path for the current "round"
                            gameMap = new GameMap(this, listTowers);
                            /** The path finder we'll use to search our map */
                            finder = new AStarPathFinder(gameMap, 500, false, new ClosestHeuristic());

                            listPossiblePath = finder.findPath(new ChibiCharacter(this, chibiCharacter, 0, 0, 0),
                                    0, 0,  // TODO STOP HARDCODING, THIS IS DEPARTURE AND ARRIVAL
                                    xGridArrival, yGridArrival)
                                    .getPossiblePath();
                        }
                        return true;
                    }
                    // if click on tower, upgrade tower
                    for (Tower tower : listTowers) {
                        if (tower.getX() < initialTouchX && initialTouchX < tower.getX() + WIDTH_OBJECT
                                && tower.getY() < initialTouchY && initialTouchY < tower.getY() + HEIGHT_OBJECT) {
                            // TODO Print an error message saying that it is impossible to build on existing tower
                            tower.upgradeTowerType();
                            return true; // TODO I don't know what these return value are used for, I only use them to get out of the function..
                        }
                    }
                    // if no tower touched, add a block
                    if (currentPlayer.getGoldPlayer() >= 1) { //TODO Stop hardcoding these value (2 = price of block)
                        int gridX = initialTouchX / WIDTH_OBJECT;
                        int gridY = initialTouchY / HEIGHT_OBJECT;
                        listTowers.add(new Tower(this, 0, gridX*WIDTH_OBJECT, gridY*HEIGHT_OBJECT));
                        currentPlayer.setGoldPlayer(currentPlayer.getGoldPlayer() - 1) ; // TODO stop hardcoding
                    }
                    break;
            }

        }
        else if (currentRound.isRoundFinished() == 0) {
            //Click handling in round (= possible upgrade time)
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialTouchX = (int) event.getX();
                    initialTouchY = (int) event.getY();
                    handler.postDelayed(mLongPressed, 1000);
                    break;
                case MotionEvent.ACTION_UP:
                    handler.removeCallbacks(mLongPressed);
                    // CODE FOR SINGLE CLICK HERE
                    if (!longClickFlag) {
                        for (Tower tower : listTowers) {
                            if (tower.getX() < initialTouchX && initialTouchX < tower.getX() + WIDTH_OBJECT
                                    && tower.getY() < initialTouchY && initialTouchY < tower.getY() + HEIGHT_OBJECT) {
                                // UpgradetowerType
                                tower.upgradeTowerType();
                            }
                        }
                        return false;
                    } // CODE FOR LONG CLICK HERE (longclickFlag is true)
                    else {
                        for (Tower tower : listTowers) {
                            if (tower.getX() < initialTouchX && initialTouchX < tower.getX() + WIDTH_OBJECT
                                    && tower.getY() < initialTouchY && initialTouchY < tower.getY() + HEIGHT_OBJECT) {
                                // UpgradetowerType
                                tower.downgradeTowerType();
                            }
                        }
                        longClickFlag = false;
                    }
                    break;
            }
        }
        return true;
    }



    public ArrayList getListPossiblePath() {

        return this.listPossiblePath;
    }
    public ArrayList<ChibiCharacter> getListChibis() {
        return listChibis;
    }

    public int getxGridArrival() {
        return xGridArrival;
    }

    public int getyGridArrival() {
        return yGridArrival;
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