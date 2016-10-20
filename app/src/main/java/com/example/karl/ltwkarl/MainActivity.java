package com.example.karl.ltwkarl;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

    /** A handle to the thread that's actually running the animation. */
    private GameThread mGameThread;
    /** A handle to the View in which the game is running. */
    private GameSurface mGameSurface;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Set no sleep mode
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Set No Title
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mGameSurface = new GameSurface(this);
        mGameThread = mGameSurface.getGameThread();
        if (savedInstanceState == null) {
            Log.w(LOG_TAG, "SIS is null");
            // we were just launched: set up a new game
            mGameThread.setState(GameThread.STATE_RUNNING);
        } else {

            Log.w(LOG_TAG, "SIS is nonnull");
//            // we are being restored: resume a previous game
//            mLunarThread.restoreState(savedInstanceState);
            // JUST TESTING HERE
            //TODO CHANGE THAT
            mGameThread.setState(GameThread.STATE_RUNNING);

        }

        //Launch the Game SurfaceView into Main Activity
        this.setContentView(mGameSurface);
    }

    /**
     * Invoked when the Activity loses user focus.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mGameThread.pause();
        Bundle a = mGameThread.saveState();
        Log.v(LOG_TAG, "ON pause activity");
        getIntent().putExtra("test", a);// pause game when Activity pauses
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        // just have the View's thread save its state into our Bundle
//        super.onSaveInstanceState(outState);
//        mGameThread.saveState(outState);
//        Log.w(LOG_TAG, "SIS called");
//    }

    @Override
    protected void onResume() {
        super.onResume();
        mGameSurface = new GameSurface(this);
        mGameThread = mGameSurface.getGameThread();
        mGameThread.setState(GameThread.STATE_RUNNING);
        this.setContentView(mGameSurface);
        Bundle a = getIntent().getExtras();
        if (a != null) {
            mGameThread.restoreSavedState(a.getBundle("test"));
        }
    }


}
