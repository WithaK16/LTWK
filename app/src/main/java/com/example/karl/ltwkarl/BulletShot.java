package com.example.karl.ltwkarl;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by karl on 22/10/2016.
 */

public class BulletShot extends GameObject {

    private long lastDrawNanoTime =-1;


    private static final String LOG_TAG = BulletShot.class.getSimpleName();

    private long initialDistance;

    private ChibiCharacter targetChibi;

    private double movingVectorLength;


    // Velocity of bullet (pixel/millisecond)
    public static final float VELOCITY = 0.5f;

    public BulletShot(Bitmap image, int x, int y, ChibiCharacter targetChibi) {
        super(image, 1, 1, x, y);
        this.targetChibi = targetChibi;
    }

    public void update() {
        // Current time in nanoseconds
        long now = System.nanoTime();

        // Never once did draw.
        if (lastDrawNanoTime == -1) {
            lastDrawNanoTime = now;
        }
        // Change nanoseconds to milliseconds (1 nanosecond = 1000000 milliseconds).
        int deltaTime = (int) ((now - lastDrawNanoTime) / 1000000);

        // Distance moves
        float distance = VELOCITY * deltaTime;

        int movingVectorX = targetChibi.getX() - x ;
        int movingVectorY = targetChibi.getY() - y ;


        movingVectorLength = Math.sqrt(movingVectorX * movingVectorX + movingVectorY * movingVectorY);

        // Calculate the new position of the bullet.
        this.x = x + (int) (distance * movingVectorX / movingVectorLength);
        this.y = y + (int) (distance * movingVectorY / movingVectorLength);
    }

    public void draw(Canvas canvas)  {
        canvas.drawBitmap(image, x, y, null);
        // Last draw time.
        this.lastDrawNanoTime= System.nanoTime();
    }

    public double getMovingVectorLength() {
        return movingVectorLength;
    }

}
