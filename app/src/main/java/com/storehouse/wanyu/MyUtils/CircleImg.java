package com.storehouse.wanyu.MyUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.storehouse.wanyu.R;

/**
 * Created by liuhaidong on 2018/6/6.
 */

public class CircleImg extends ImageView {
    float padding=0f;
    private Paint mPaintBitmap = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Bitmap mRawBitmap;
    private BitmapShader mShader;
    private Matrix mMatrix = new Matrix();

    public CircleImg(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap rawBitmap = getBitmap(getDrawable());
        padding=getPaddingTop();
        if (rawBitmap != null) {
            int viewWidth = getWidth();//图片宽度
            int viewHeight = getHeight();//图片高度
            int viewMinSize = Math.min(viewWidth, viewHeight);//哪个小选哪个作为缩放比例
            float dstWidth = viewMinSize;
            float dstHeight = viewMinSize;
            if (mShader == null || !rawBitmap.equals(mRawBitmap)) {
                mRawBitmap = rawBitmap;
                mShader = new BitmapShader(mRawBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            }
            if (mShader != null) {
                mMatrix.setScale(dstWidth / rawBitmap.getWidth(), dstHeight / rawBitmap.getHeight());
                mShader.setLocalMatrix(mMatrix);//缩放图片
            }
            mPaintBitmap.setShader(mShader);
            float radius = viewMinSize / 2.0f;//圆心X,Y为图片大小的一半，这就是圆

            canvas.drawCircle(radius, radius, radius, mPaintBitmap);
        } else {
            super.onDraw(canvas);
        }
        if (padding!=0f){
            Paint p=new Paint();
            p.setColor(getResources().getColor(R.color.color_ffffff));
            p.setAntiAlias(true);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(padding);
            canvas.drawCircle(getWidth()/2,getHeight()/2,getWidth()/2-padding/2,p);
        }
    }

    private Bitmap getBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof ColorDrawable) {
            Rect rect = drawable.getBounds();
            int width = rect.right - rect.left;
            int height = rect.bottom - rect.top;
            int color = ((ColorDrawable) drawable).getColor();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawARGB(Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color));
            return bitmap;
        } else {
            return null;
        }
    }
}
