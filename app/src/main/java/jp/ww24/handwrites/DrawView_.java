package jp.ww24.handwrites;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by ww24 on 2015/11/05.
 */
public class DrawView_ extends SurfaceView implements SurfaceHolder.Callback {

    private Path mPath;
    private Paint mPaint;
    private float mX, mY;

    public DrawView_(Context context) {
        this(context, null);
    }

    public DrawView_(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawView_(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setFocusable(true);
        setZOrderOnTop(true);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        holder.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mPaint = new Paint();
        // 線の色を指定
        mPaint.setColor(Color.BLACK);
        // 線の幅を指定
        mPaint.setStrokeWidth(5.0f);
        // 輪郭線を描画するスタイル
        mPaint.setStyle(Paint.Style.STROKE);
        // 線の繋目を丸く
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        // 線の端を丸く
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPath = new Path();

        Canvas canvas = holder.lockCanvas();
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
        onDraw(canvas);
        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.reset();
                mPath.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                mPath.quadTo(mX, mY, x, y);
                break;
        }

        mX = x;
        mY = y;

        SurfaceHolder holder = getHolder();
        Canvas canvas = holder.lockCanvas();
        canvas.drawPath(mPath, mPaint);
        onDraw(canvas);
        holder.unlockCanvasAndPost(canvas);

        return true;
    }
}
