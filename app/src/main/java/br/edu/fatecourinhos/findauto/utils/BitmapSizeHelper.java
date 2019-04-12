package br.edu.fatecourinhos.findauto.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class BitmapSizeHelper {

    public static enum ScalingLogic {
        CROP, FIT
    }
    public static enum Type {
        ROUND, CIRCLE,NORMAL
    }
    public static Bitmap getBitmapFromResources(Resources res, int resId, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, dstWidth, dstHeight, scalingLogic);
        Bitmap unscaledBitmap = BitmapFactory.decodeResource(res, resId, options);

        return unscaledBitmap;

    }

    public static Bitmap getBitmapFromPath(int targetW, int targetH, String photopath, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photopath, options);
        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, dstWidth, dstHeight, scalingLogic);
        Bitmap bitmap = BitmapFactory.decodeFile(photopath, options);

        return bitmap;

    }

    public static int calculateSampleSize(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.FIT) {
            final float srcAspect = (float) srcWidth / (float) srcHeight;
            final float dstAspect = (float) dstWidth / (float) dstHeight;

            if (srcAspect > dstAspect) {
                return srcWidth / dstWidth;
            } else {
                return srcHeight / dstHeight;
            }
        } else {
            final float srcAspect = (float) srcWidth / (float) srcHeight;
            final float dstAspect = (float) dstWidth / (float) dstHeight;

            if (srcAspect > dstAspect) {
                return srcHeight / dstHeight;
            } else {
                return srcWidth / dstWidth;
            }
        }
    }

    public static Bitmap createScaledBitmap(Bitmap unscaledBitmap, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
        try {
            Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight, scalingLogic);
            Rect dstRect = calculateDstRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight, scalingLogic);
            Bitmap scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(scaledBitmap);
            canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));
            return scaledBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Bitmap transformBitmap(Bitmap unscaledBitmap, int scale, Type mType){
        Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), scale, scale, ScalingLogic.CROP);
        Rect dstRect = calculateDstRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), scale, scale, ScalingLogic.CROP);
        Bitmap scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(scaledBitmap);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        if(mType == Type.CIRCLE){
            canvas.drawCircle(scaledBitmap.getWidth() / 2, scaledBitmap.getHeight() / 2,
                    scaledBitmap.getWidth() / 2, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, paint);
        }else if(mType == Type.ROUND){
            Path clipPath = new Path();
            RectF rectF = new RectF(0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight());
            clipPath.addRoundRect(rectF, 11, 11, Path.Direction.CW);
            canvas.drawPath(clipPath, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, paint);
        }else {
            canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));
        }
        return scaledBitmap;
    }

    public static Rect calculateSrcRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.CROP) {
            final float srcAspect = (float) srcWidth / (float) srcHeight;
            final float dstAspect = (float) dstWidth / (float) dstHeight;

            if (srcAspect > dstAspect) {
                final int srcRectWidth = (int) (srcHeight * dstAspect);
                final int srcRectLeft = (srcWidth - srcRectWidth) / 2;
                return new Rect(srcRectLeft, 0, srcRectLeft + srcRectWidth, srcHeight);
            } else {
                final int srcRectHeight = (int) (srcWidth / dstAspect);
                final int scrRectTop = (int) (srcHeight - srcRectHeight) / 2;
                return new Rect(0, scrRectTop, srcWidth, scrRectTop + srcRectHeight);
            }
        } else {
            return new Rect(0, 0, srcWidth, srcHeight);
        }
    }

    public static Rect calculateDstRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.FIT) {
            final float srcAspect = (float) srcWidth / (float) srcHeight;
            final float dstAspect = (float) dstWidth / (float) dstHeight;

            if (srcAspect > dstAspect) {
                return new Rect(0, 0, dstWidth, (int) (dstWidth / srcAspect));
            } else {
                return new Rect(0, 0, (int) (dstHeight * srcAspect), dstHeight);
            }
        } else {
            return new Rect(0, 0, dstWidth, dstHeight);
        }
    }

}