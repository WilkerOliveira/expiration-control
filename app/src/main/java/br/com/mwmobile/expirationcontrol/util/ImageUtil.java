package br.com.mwmobile.expirationcontrol.util;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;

import io.reactivex.annotations.NonNull;

/**
 * Image Util class
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 11/11/2017
 */

public class ImageUtil {

    /**
     * Get a Bitmap form URI
     *
     * @param uri             URL
     * @param contentResolver Content Resolver to get the image from URI
     * @return Bitmap
     * @throws IOException ErrorType
     */
    public static Bitmap getBitmapFromUri(Uri uri, ContentResolver contentResolver) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r");

        if (parcelFileDescriptor != null) {
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        }

        return null;
    }

    /**
     * Check if the file exist
     *
     * @param filePath File Path
     * @return True or False
     */
    public static boolean existFile(String filePath) {

        if (TextUtils.isEmpty(filePath)) return false;

        File file = new File(filePath);
        return file.exists();
    }

    /**
     * Load an ImageView
     *
     * @param context              Context to Glide
     * @param imagePath            Image path
     * @param imageView            ImageView to load
     * @param defaultImageResource Image Resource in case the Image not exist
     */
    public static void loadFromGlide(Context context, String imagePath, ImageView imageView, int defaultImageResource) {
        if (existFile(imagePath))
            Glide.with(context).load(imagePath).into(imageView);
        else
            imageView.setImageResource(defaultImageResource);
    }

    /**
     * Draw the first letter
     *
     * @param imgLetter ImageView to Letter
     * @param name      Name to get the letter
     */
    public static void setLetter(@NonNull ImageView imgLetter, @NonNull String name) {
        String letter = String.valueOf(name.charAt(0));
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(letter, Color.LTGRAY);
        imgLetter.setImageDrawable(drawable);
        imgLetter.setContentDescription(name);
    }

}
