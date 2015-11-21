package jp.ww24.handwrites;

import android.app.Activity;
import android.content.ContentResolver;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import jp.ww24.handwrites.databinding.ContentGalleryBinding;

/**
 * Created by ww24 on 2015/11/06.
 */
public class GalleryFragment extends Fragment {

    private ContentGalleryBinding binding;

    public String message;
    public ArrayList<FileItem> files;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ContentGalleryBinding.inflate(inflater);
        binding.setGallery(this);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Activity activity = getActivity();

        // キャッシュディレクトリからファイル一覧取得
        File dir = activity.getCacheDir();
        // TODO: 再帰的に読み込む
        ArrayList<File> fileList = new ArrayList<>(Arrays.asList(dir.listFiles()));
        ArrayList<FileItem> fileItemList = new ArrayList<>();

        ContentResolver contentResolver = getContext().getContentResolver();
        for (File file: fileList) {
            if (! file.isFile()) {
                continue;
            }

            Uri uri = Uri.fromFile(file);

            try {
                InputStream inputStream = contentResolver.openInputStream(uri);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(inputStream, null, options);

                String mimeType = options.outMimeType;
                if (mimeType == null || ! mimeType.split("/")[0].equals("image")) {
                    Log.d("DEBUG", "Invalid MIME Type: " + options.outMimeType);
                }

                FileItem fileItem = new FileItem(file, contentResolver);
                fileItemList.add(fileItem);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }

        // TextView へ反映
        if (fileItemList.size() > 0) {
            message = String.valueOf(fileItemList.size()) + " files found.";
        } else {
            message = "file not found.";
        }

        // ListView へ反映
        files = fileItemList;
        ListView listView = binding.listView;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                File selectedFile = (File) listView.getItemAtPosition(position);
                Toast.makeText(activity, selectedFile.getName(), Toast.LENGTH_SHORT).show();
                Log.d("DEBUG", "tap: " + selectedFile.getName());
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                FileItem fileItem = (FileItem) listView.getItemAtPosition(position);

                Log.d("DEBUG", "long tap: " + fileItem.getName());

                return true;
            }
        });
    }

    @BindingAdapter("bind:items")
    public static void setItems(ListView listView, ArrayList<FileItem> fileItemArrayList) {
        FileItemAdapter adapter = new FileItemAdapter(listView.getContext());
        adapter.setFileList(fileItemArrayList);
        listView.setAdapter(adapter);
    }

    @BindingAdapter("bind:image")
    public static void setImage(ImageView imageView, Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }
}
