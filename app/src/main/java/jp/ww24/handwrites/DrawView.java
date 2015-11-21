package jp.ww24.handwrites;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.TextureView;

/**
 * Created by ww24 on 2015/11/05.
 */
public class DrawView extends TextureView implements TextureView.SurfaceTextureListener {
    private Path mPath;
    private Paint mPaint;
    private float mX, mY;

    public DrawView(Context context) {
        this(context, null);
    }

    public DrawView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setFocusable(true);
        setBackgroundColor(Color.WHITE);
        setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (mPath == null) {
            mPath = new Path();
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
        } else {
            // 復帰時の再描画
            Canvas canvas = lockCanvas();
            if (canvas != null) {
                canvas.drawColor(Color.WHITE);
                canvas.drawPath(mPath, mPaint);
                unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //mPath.reset();
                mPath.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                mPath.quadTo(mX, mY, x, y);
                break;
        }

        mX = x;
        mY = y;

        Canvas canvas = lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);
            canvas.drawPath(mPath, mPaint);
            unlockCanvasAndPost(canvas);
        }

        return true;
    }

    public void clear() {
        mPath = new Path();

        Canvas canvas = lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);
            unlockCanvasAndPost(canvas);
        }
    }
}
