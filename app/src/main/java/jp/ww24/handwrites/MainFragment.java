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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ww24 on 2015/11/06.
 */
public class MainFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_main, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Activity context
        final Activity activity = getActivity();

        // FloatingActionButton
        FloatingActionButton fab = (FloatingActionButton) activity.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawView drawView = (DrawView) activity.findViewById(R.id.draw_view);
                drawView.clear();

                try {
                    saveBitmapImage(drawView.getBitmap());
                } catch (FileNotFoundException e) {
                    Log.e("Error", e.toString());

                    Snackbar.make(view, e.toString(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null)
                            .show();
                }

                Toast.makeText(activity, "Saved.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveBitmapImage(Bitmap bitmap) throws FileNotFoundException {
        final Activity activity = getActivity();

        String dateStr = new SimpleDateFormat("yyyymmdd_HHmm:ss", Locale.JAPAN).format(new Date());
        File cacheDir = activity.getCacheDir();
        File file = new File(cacheDir, dateStr + ".png");
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
    }
}
