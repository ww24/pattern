package jp.ww24.handwrites;

import android.app.Activity;
import android.content.ContentResolver;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

        RecyclerView listView = binding.listView;
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                                   RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        if (direction == ItemTouchHelper.RIGHT) {
                            final FileItem file = ((FileItemAdapter.FileItemViewHolder) viewHolder).getFile();

                            // delete file
                            files.remove(file);

                            // FIXME: 最後の要素の消え方がおかしい。
                            file.visible.set(false);

                            Snackbar.make(binding.getRoot(), file.getName() + " is removed.", Snackbar.LENGTH_LONG)
                                    .setAction("UNDO", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Log.d("DEBUG", "UNDO: " + file.getName());
                                        }
                                    })
                                    .setCallback(new Snackbar.Callback() {
                                        @Override
                                        public void onDismissed(Snackbar snackbar, int event) {
                                            super.onDismissed(snackbar, event);

                                            if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
//                                                file.delete();
                                            } else {
                                                file.visible.set(true);
                                            }
                                        }
                                    })
                                    .show();
                        }
                    }
                }
        );

        itemTouchHelper.attachToRecyclerView(listView);
        listView.addItemDecoration(itemTouchHelper);

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
    }

    @BindingAdapter("bind:items")
    public static void setItems(RecyclerView listView, ArrayList<FileItem> fileItemArrayList) {
        FileItemAdapter adapter = new FileItemAdapter(listView.getContext());
        adapter.setFileList(fileItemArrayList);
        listView.setAdapter(adapter);
    }

    @BindingAdapter("bind:image")
    public static void setImage(ImageView imageView, Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    @BindingAdapter("bind:height")
    public static void setHeight(View view, float height) {
        Log.d("DEBUG", "bind:height");
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int)height;
        view.setLayoutParams(layoutParams);
    }
}
