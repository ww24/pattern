package jp.ww24.handwrites;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

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
        ViewHolder holder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.gallery_item, parent, false);

            holder = new ViewHolder();
            holder.filename = ((TextView) convertView.findViewById(R.id.filename));
            holder.itemImage = ((ImageView) convertView.findViewById(R.id.itemImage));

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FileItem file = (FileItem) getItem(position);

        holder.filename.setText(file.getName());
        holder.itemImage.setImageBitmap(file.getThumbnailBitmap());

        return convertView;
    }

    private class ViewHolder {
        ImageView itemImage;
        TextView filename;
    }
}
