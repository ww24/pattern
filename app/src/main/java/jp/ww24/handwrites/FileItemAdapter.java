package jp.ww24.handwrites;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import jp.ww24.handwrites.databinding.GalleryItemBinding;

/**
 * Created by ww24 on 2015/11/12.
 */
public class FileItemAdapter extends RecyclerView.Adapter<FileItemAdapter.FileItemViewHolder> {

    private LayoutInflater layoutInflater;
    private ArrayList<FileItem> fileItemList;

    public FileItemAdapter(Context context) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setFileList(ArrayList<FileItem> fileItemList) {
        this.fileItemList = fileItemList;
    }

    @Deprecated
    public int getCount() {
        return fileItemList.size();
    }

    @Override
    public int getItemCount() {
        return fileItemList.size();
    }

    public Object getItem(int position) {
        return fileItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Deprecated
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

    @Override
    public FileItemViewHolder onCreateViewHolder(ViewGroup container, int viewType) {
        GalleryItemBinding binding = GalleryItemBinding.inflate(layoutInflater, container, false);
        return new FileItemViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(FileItemViewHolder holder, int position) {
        FileItem fileItem = (FileItem) getItem(position);
        holder.binding.setFile(fileItem);
    }

    public static class FileItemViewHolder extends RecyclerView.ViewHolder {

        private GalleryItemBinding binding;

        public FileItemViewHolder(View rootView) {
            super(rootView);

            binding = DataBindingUtil.bind(rootView);
        }

        public FileItem getFile() {
            return binding.getFile();
        }
    }
}
