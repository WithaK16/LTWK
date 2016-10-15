package com.example.karl.ltwkarl;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by karl on 14/10/2016.
 */

public class Tower extends GameObject {


    private static final String LOG_TAG = Tower.class.getSimpleName();

    private GameSurface gameSurface;

    public Tower(GameSurface gameSurface, Bitmap image,  int x, int y) {
        super(image, 1, 1, x, y);
        this.gameSurface = gameSurface;
    }
    public void update()  {

    }

    public void draw(Canvas canvas)  {
        canvas.drawBitmap(this.image, this.x, this.y, null);
    }
}
