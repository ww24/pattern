package jp.ww24.handwrites;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alexbbb.uploadservice.MultipartUploadRequest;
import com.alexbbb.uploadservice.UploadNotificationConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import jp.ww24.handwrites.databinding.ContentMainBinding;

/**
 * Created by ww24 on 2015/11/06.
 */
public class MainFragment extends Fragment {

    private ContentMainBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ContentMainBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Activity context
        final Activity activity = getActivity();

        // FloatingActionButton
        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawView drawView = binding.drawView;
                drawView.clear();

                try {
                    final String filepath = saveBitmapImage(drawView.getBitmap());

                    // upload image
                    final String uploadID = UUID.randomUUID().toString();
                    new MultipartUploadRequest(getContext(), uploadID, "https://kis.appcloud.info/api/")
                            .addFileToUpload(filepath, "image")
                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(3)
                            .startUpload();
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

                Toast.makeText(activity, "Saved.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String saveBitmapImage(Bitmap bitmap) throws FileNotFoundException {
        final Activity activity = getActivity();

        String dateStr = new SimpleDateFormat("yyyymmdd_HHmm:ss", Locale.JAPAN).format(new Date());
        File cacheDir = activity.getCacheDir();
        File file = new File(cacheDir, dateStr + ".png");
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

        return file.getPath();
    }
}
