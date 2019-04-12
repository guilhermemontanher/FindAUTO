package br.edu.fatecourinhos.findauto.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class Casting {
    /**
     * @param bitmap
     * @return converting bitmap and return a string
     */
    public static String BitmapToString(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            String temp = Base64.encodeToString(b, Base64.DEFAULT);

            return temp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param encodedString
     * @return bitmap (from given string)
     */
    public static Bitmap StringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                    encodeByte.length);
            System.gc();
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

}
