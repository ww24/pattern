package jp.ww24.handwrites;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ww24 on 2015/11/12.
 */
public class FileAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private ArrayList<File> fileList;

    public FileAdapter(Context context) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setFileList(ArrayList<File> files) {
        fileList = files;
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public Object getItem(int position) {
        return fileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.gallery_item, parent, false);

        File file = (File) getItem(position);
        ((TextView) convertView.findViewById(R.id.filename)).setText(file.getName());

        return convertView;
    }
}
