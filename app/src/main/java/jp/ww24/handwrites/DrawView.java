package jp.ww24.handwrites;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.SurfaceTexture;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.Toast;

import com.alexbbb.uploadservice.BinaryUploadRequest;
import com.alexbbb.uploadservice.UploadNotificationConfig;

import org.apache.commons.collections4.ListUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Created by ww24 on 2015/11/05.
 */
public class DrawView extends TextureView implements TextureView.SurfaceTextureListener {
    private Path mPath = new Path();
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Integer[] mPattern = {1, 5, 3, 6, 7, 2, 8, 4, 9};
    private List<Integer> mStroke = new ArrayList<>();
    private int mBaseColor = Color.BLACK;
    private float mX, mY;
    private long mTime;
    private Object mPositions[] = new Object[9];
    private List<Map<String, Number>> mPathList = new ArrayList<>();

    public DrawView(Context context) {
        this(context, null);
    }

    public DrawView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setFocusable(true);
        setBackgroundColor(mBaseColor);
        setSurfaceTextureListener(this);

        // 線の色を指定
        mPaint.setColor(Color.WHITE);
        // 線の幅を指定
        mPaint.setStrokeWidth(5.0f);
        // 輪郭線を描画するスタイル
        mPaint.setStyle(Paint.Style.STROKE);
        // 線の繋目を丸く
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        // 線の端を丸く
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        // set pattern marker position
        double wd = getWidth() / 3.0f;
        double hd = getHeight() / 3.0f;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int point[] = new int[2];
                point[0] = (int) (j * wd + wd / 2);
                point[1] = (int) (i * hd + hd / 2);
                mPositions[i * 3 + j] = point;
            }
        }

        // 復帰時の再描画
        Canvas canvas = lockCanvas();
        if (canvas != null) {
            initializeCanvas(canvas, mPattern);
            if (mPathList.size() > 0) {
                drawPathList(canvas, mPathList);
            }
            unlockCanvasAndPost(canvas);
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
        final float x = event.getX();
        final float y = event.getY();
        Canvas canvas;

        long time = mTime;
        mTime = System.currentTimeMillis();

        int threshold = 80;
        for (int i=0; i < mPositions.length; i++) {
            int[] pos = (int[])mPositions[i];
            if (x >= pos[0] - threshold
                    && x <= pos[0] + threshold
                    && y >= pos[1] - threshold
                    && y <= pos[1] + threshold
                    && ! mStroke.contains(i + 1))
            {
                mStroke.add(i + 1);
                Log.d("DEBUG", String.format("position: %d", i + 1));
                break;
            }
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("DEBUG", "DOWN");

                Map<String, Number> p = new HashMap<>();
                p.put("x1", x);
                p.put("y1", y);
                mPathList.add(p);

                break;
            case MotionEvent.ACTION_MOVE:
                double distance = Math.sqrt(Math.pow(mX - x, 2.0d) + Math.pow(mY - y, 2.0d));
                double td = mTime - time;
                double v = distance / Math.max(td, 1.0E-5);
                double w = Math.min(20.0d / Math.max(v, 1.0E-5), 1.0E+2);

//                Log.d("DEBUG", String.valueOf(mX) + ", " + String.valueOf(x) + ", " +
//                        String.valueOf(mY) + ", " + String.valueOf(y));
//                Log.d("DEBUG", String.valueOf(mX - x) + "," + String.valueOf(mY - y) +
//                        ", distance:" + String.valueOf(distance) +
//                        ", time:" + String.valueOf(td));
//                Log.d("DEBUG", "width:" + String.valueOf(w));
                Log.d("DEBUG", "velocity:" + String.valueOf(v));

                Map<String, Number> point0 = mPathList.get(mPathList.size() - 1);
                point0.put("x2", x);
                point0.put("y2", y);
                point0.put("w", w);
                Map<String, Number> point1 = new HashMap<>();
                point1.put("x1", x);
                point1.put("y1", y);
                mPathList.add(point1);

                canvas = lockCanvas();
                if (canvas != null) {
                    initializeCanvas(canvas, mPattern);
                    unlockCanvasAndPost(canvas);
                }

                mTime = System.currentTimeMillis();
                mPath.reset();
                mPath.moveTo(x, y);

                Log.d("DEBUG", "MOVE or UP");
                break;
            case MotionEvent.ACTION_UP:
                if (ListUtils.isEqualList(mStroke, Arrays.asList(mPattern))) {
                    canvas = lockCanvas();
                    if (canvas != null) {
                        initializeCanvas(canvas, mPattern);
                        // 軌跡の描画
                        drawPathList(canvas, mPathList);
                        unlockCanvasAndPost(canvas);

                        // Upload
                        uploadBitmap();
                    }
                } else {
                    Toast.makeText(this.getContext(), "正しいパターンを入力してください", Toast.LENGTH_SHORT).show();
                    clear();
                }
                break;
        }

        mX = x;
        mY = y;

        invalidate();
        return true;
    }

    private void drawPathList(Canvas canvas, List<Map<String, Number>> pathList) {
        for (Map<String, Number> point: pathList) {
            if (point.get("x2") == null || point.get("y2") == null) {
                continue;
            }
            Path path = new Path();
            path.moveTo(point.get("x1").intValue(), point.get("y1").intValue());
            path.quadTo(point.get("x1").intValue(), point.get("y1").intValue(),
                    point.get("x2").intValue(), point.get("y2").intValue());
            Paint paint = new Paint(mPaint);
            paint.setStrokeWidth(point.get("w").intValue());
            canvas.drawPath(path, paint);
        }
    }

    private void drawPattern(Canvas canvas, Integer[] pattern, Paint paint) {
        float[] points = new float[(pattern.length - 1) * 4];

        for (int i = 1; i < pattern.length; i++) {
            int[] positionS = (int[]) mPositions[pattern[i - 1] - 1];
            int[] positionE = (int[]) mPositions[pattern[i] - 1];
            points[(i - 1) * 4] = positionS[0];
            points[(i - 1) * 4 + 1] = positionS[1];
            points[(i - 1) * 4 + 2] = positionE[0];
            points[(i - 1) * 4 + 3] = positionE[1];
        }

        canvas.drawLines(points, paint);
    }

    private void initializeCanvas(Canvas canvas, Integer[] pattern) {
        canvas.drawColor(mBaseColor);

        Paint paint = new Paint(mPaint);
        // 線の幅を指定
        paint.setStrokeWidth(40.0f);

        // Pattern marker の描画
        for (int i = 0; i < mPositions.length; i++) {
            int[] pos = (int[])mPositions[i];
            if (mStroke.contains(i + 1)) {
                paint.setColor(Color.BLUE);
            } else if (pattern != null && Arrays.asList(pattern).contains(i + 1)) {
                paint.setColor(Color.RED);
            } else {
                paint.setColor(Color.WHITE);
            }
            canvas.drawCircle(pos[0], pos[1], 20.0f, paint);
        }

        if (pattern != null) {
            paint = new Paint(mPaint);
            paint.setColor(Color.argb(0x80, 0xff, 0, 0));
            paint.setStrokeWidth(30.0f);
            drawPattern(canvas, pattern, paint);
        }

        if (mStroke.size() > 1) {
            pattern = mStroke.toArray(new Integer[0]);
            paint = new Paint(mPaint);
            paint.setColor(Color.argb(0x80, 0, 0, 0xff));
            paint.setStrokeWidth(30.0f);
            drawPattern(canvas, pattern, paint);
        }
    }

    public void clear() {
        mPathList = new ArrayList<>();
        mStroke.clear();

        Canvas canvas = lockCanvas();
        if (canvas != null) {
            initializeCanvas(canvas, mPattern);
            unlockCanvasAndPost(canvas);
        }
    }

    public void uploadBitmap() {
        final Context context = getContext();
        final View view = getRootView().findViewById(R.id.mainView);

        try {
            final String filepath = saveBitmapImage(getBitmap());
            clear();

            // upload image
            final String uploadID = UUID.randomUUID().toString();
            new BinaryUploadRequest(getContext(), uploadID, "https://kis.appcloud.info/api/")
                    .setFileToUpload(filepath)
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(3)
                    .startUpload();

            Toast.makeText(context, "Saved.", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Log.e("Error", e.toString());

            Snackbar.make(view, e.toString(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();
        } catch (MalformedURLException e) {
            Log.e("Error", e.toString());

            Snackbar.make(view, e.toString(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();
        }
    }

    public String saveBitmapImage(Bitmap bitmap) throws FileNotFoundException {
        final Context context = getContext();

        String dateStr = new SimpleDateFormat("yyyymmdd_HHmm:ss", Locale.JAPAN).format(new Date());
        File cacheDir = context.getCacheDir();
        File file = new File(cacheDir, dateStr + ".png");
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

        return file.getPath();
    }
}
