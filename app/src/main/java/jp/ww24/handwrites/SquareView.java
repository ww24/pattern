package jp.ww24.handwrites;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ww24 on 2015/11/04.
 */
public class SquareView extends ViewGroup {
    private int adjust = 0;

    public SquareView(Context context) {
        this(context, null);
    }

    public SquareView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SquareView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (attrs != null) {
            TypedArray attrsArray = context.obtainStyledAttributes(attrs, R.styleable.SquareView);
            adjust = attrsArray.getInt(R.styleable.SquareView_adjust, 0);
            attrsArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int sideLength = 0;
        switch (adjust) {
            case 0:
                // 小さい方に合わせる
                int width = getMeasuredWidth();
                int height = getMeasuredHeight();
                sideLength = Math.min(width, height);
                break;
            case 1:
                // 横に合わせる
                sideLength = getMeasuredWidth();
                break;
            case 2:
                // 縦に合わせる
                sideLength = getMeasuredHeight();
                break;
        }

        setMeasuredDimension(sideLength, sideLength);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = (r - l) - getPaddingLeft() - getPaddingRight();
        int height = (b - t) - getPaddingTop() - getPaddingBottom();

        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = left + width;
        int bottom = top + height;

        View child = getChildAt(0);
        child.layout(left, top, right, bottom);
    }
}
