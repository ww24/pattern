package jp.ww24.handwrites;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;

import java.io.File;
import java.io.InputStream;

/**
 * Created by ww24 on 2015/11/16.
 */
public class FileItem extends File implements Closeable {
    public int thumbnailSize = 400;

    private ContentResolver contentResolver;
    private BitmapFactory.Options options;
    private Bitmap bitmap;

    public FileItem(String path) {
        super(path);

        options = new BitmapFactory.Options();
    }

    public FileItem(URI uri) {
        super(uri);

        options = new BitmapFactory.Options();
    }

    public FileItem(String dirPath, String name) {
        super(dirPath, name);

        options = new BitmapFactory.Options();
    }

    public FileItem(File dir, String name) {
        super(dir, name);

        options = new BitmapFactory.Options();
    }

    public FileItem(File file, ContentResolver contentResolver) {
        super(file.toURI());

        this.contentResolver = contentResolver;
        this.options = new BitmapFactory.Options();

        InputStream inputStream = getInputStream();

        BitmapFactory.Options options = new BitmapFactory.Options();
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
}
