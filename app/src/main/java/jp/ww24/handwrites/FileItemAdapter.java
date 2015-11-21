package jp.ww24.handwrites;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import jp.ww24.handwrites.databinding.GalleryItemBinding;

/**
 * Created by ww24 on 2015/11/12.
 */
public class FileItemAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private ArrayList<FileItem> fileItemList;

    public FileItemAdapter(Context context) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setFileList(ArrayList<FileItem> fileItemList) {
        this.fileItemList = fileItemList;
    }

    @Override
    public int getCount() {
        return fileItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return fileItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GalleryItemBinding binding;

        if (convertView == null) {
            binding = GalleryItemBinding.inflate(layoutInflater, parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (GalleryItemBinding) convertView.getTag();
        }

        FileItem file = (FileItem) getItem(position);
        binding.setFile(file);

        return convertView;
    }
}
