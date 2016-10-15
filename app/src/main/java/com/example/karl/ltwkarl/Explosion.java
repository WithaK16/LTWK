package com.example.karl.ltwkarl;

/**
 * Created by karl on 14/10/2016.
 */

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Explosion extends GameObject {

    private static final String LOG_TAG = Explosion.class.getSimpleName();

    private int rowIndex = 0 ;
    private int colIndex = -1 ;

    private boolean finish= false;
    private GameSurface gameSurface;

    public Explosion(GameSurface GameSurface, Bitmap image, int x, int y) {
        super(image, 5, 5, x, y);
        this.gameSurface= GameSurface;
    }

    public void update()  {
        this.colIndex++;

        if(this.colIndex >= this.colCount)  {
            this.colIndex =0;
            this.rowIndex++;

            if(this.rowIndex>= this.rowCount)  {
                this.finish= true;
            }
        }
    }

    public void draw(Canvas canvas)  {
        if(!finish)  {
            Bitmap bitmap= this.createSubImageAt(rowIndex,colIndex);
            canvas.drawBitmap(bitmap, this.x, this.y, null);
        }
    }

    public boolean isFinish() {
        return finish;
    }

}