package com.example.fileexplorer.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fileexplorer.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * FileListAdapter
 */

// @@@@@ ArrayAdapter<FileListItem> はAdapterで取り扱う型を宣言している
public class FileListAdapter extends ArrayAdapter<FileListItem> {
    private final Context mContext; //Activity context.
    private final int mResource; //Represents the list_rowl file (our rows) as an int e.g. R.layout.list_row
    //memo:ここを二次元配列にしてヒ表示名を出す必要があるかも
    private List<FileListItem> mItems; //The List of objects we got from our model.
    private boolean mIsRoot; //The List of objects we got from our model.

    public FileListAdapter(Context context, int resource, List<FileListItem> items) {
        super(context, resource, items);

        mContext = context;
        mResource = resource;
        mItems = items;
    }

    public FileListAdapter(Context context, int resource) {
        super(context, resource);

        mContext = context;
        mResource = resource;
    }

    /*Does exactly what it looks like.  Pulls out a specific File Object at a specified index.
    Remember that our FileListAdapter contains a list of Files it gets from our model's getAllFiles(),
    so getitem(0) is the first file in that List, getItem(1), the second, etc.  ListView uses this
    method internally.*/
    @Override
    public FileListItem getItem(int i) {
        return mItems.get(i);
    }

    /** Allows me to pull out specific views from the row xml file for the ListView.   I can then
     *make any modifications I want to the ImageView and TextViews inside it.
     *@param position - The position of an item in the List received from my model.
     *@param convertView - list_row.xml as a View object.
     *@param parent - The parent ViewGroup that holds the rows.  In this case, the ListView.
     ***/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*This is the entire file [list_rowl] with its RelativeLayout, ImageView, and two
        TextViews.  It will always be null the very first time, so we need to inflate it with a
           LayoutInflater.*/
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater.from(mContext));
            v = inflater.inflate(mResource, null);
        }

        /* We pull out the ImageView and TextViews so we can set their properties.*/
        ImageView iv = (ImageView) v.findViewById(R.id.imageView);

        TextView nameView = (TextView) v.findViewById(R.id.name_text_view);

        // リストビューに表示する要素を取得
        FileListItem fileListItem = getItem(position);

        /* If the file is a dir, set the image view's image to a folder, else, a file. */
        // ここに"../"判定が必要
        if (Objects.requireNonNull(fileListItem).getFile().isDirectory()) {
            //@@@@
//            iv.setImageResource(R.drawable.folderxxhdpi);
        } else {
            //@@@@
//            iv.setImageResource(R.drawable.filexxhdpi);
        }

        //Finally, set the name of the file or directory.
        nameView.setText(fileListItem.getName());

        //Send the view back so the ListView can show it as a row, the way we modified it.
        return v;
    }
}
