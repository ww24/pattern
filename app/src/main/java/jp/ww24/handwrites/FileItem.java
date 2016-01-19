package jp.ww24.handwrites;

import android.content.ContentResolver;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;

import java.io.File;
import java.io.InputStream;

/**
 * Created by ww24 on 2015/11/16.
 */
public class FileItem extends File implements Closeable {
    public int thumbnailSize = 400;

    private ContentResolver contentResolver;
    private BitmapFactory.Options options = new BitmapFactory.Options();
    private Bitmap bitmap;

    public final ObservableField<Boolean> visible = new ObservableField<>(true);

    public FileItem(File file, ContentResolver contentResolver) {
        super(file.toURI());

        this.contentResolver = contentResolver;

        InputStream inputStream = getInputStream();

        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        double scale = Math.max(options.outHeight, options.outWidth) / thumbnailSize;
        if (scale >= 2) {
            this.options.inSampleSize = (int) scale;
        }

        visible.set(true);
    }

    public void setContentResolver(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public void setOptions(BitmapFactory.Options options) {
        this.options = options;
    }

    public Bitmap getBitmap() {
        InputStream inputStream = getInputStream();
        bitmap = BitmapFactory.decodeStream(inputStream);
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public Bitmap getThumbnailBitmap() {
        InputStream inputStream = getInputStream();
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private BufferedInputStream getInputStream() {
        Uri uri = Uri.fromFile(this);

        BufferedInputStream bufferedInputStream = null;
        try {
            InputStream inputStream = null;
            inputStream = contentResolver.openInputStream(uri);
            bufferedInputStream = new BufferedInputStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bufferedInputStream;
    }

    @Override
    public void close() throws IOException {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    public void onClick(View view) {
        Toast.makeText(view.getContext(), this.getName(), Toast.LENGTH_SHORT).show();
        Log.d("DEBUG", "tap: " + this.getName());
    }
}
