package jp.ww24.handwrites;

import android.app.Activity;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ww24 on 2015/11/06.
 */
public class GalleryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_gallery, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Activity activity = getActivity();

        ListView listView = (ListView) activity.findViewById(R.id.listView);
        FileAdapter fileAdapter = new FileAdapter(activity);

        File dir = activity.getCacheDir();
        ArrayList<File> fileList = new ArrayList<>(Arrays.asList(dir.listFiles()));
        fileAdapter.setFileList(fileList);

        listView.setAdapter(fileAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                File selectedFile = (File) listView.getItemAtPosition(position);
                Toast.makeText(activity, selectedFile.getName(), Toast.LENGTH_SHORT).show();
                Log.d("DEBUG", selectedFile.getName());
            }
        });
    }

}
