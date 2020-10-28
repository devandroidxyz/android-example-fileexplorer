//ビジネるロジックを記載

package com.example.fileexplorer.model;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.example.fileexplorer.view.FileListItem;
import com.example.fileexplorer.util.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

/**
 * FileListModel
 */
public class FileListModel {
    private FileListItem mCurrentListItems; //Our current location.
    private Stack<FileListItem> mHistoryListItems; //Our navigation History.
    private static final String TAG = "FileListModel"; //for debugging purposes.

    public FileListModel() {
        init();
    }

    private void init() {
        mCurrentListItems = new FileListItem(false, null, null);
        mHistoryListItems = new Stack<>();

    /* The first thing I need to do is check to see if the device's storage is read/write accessible.  If it is not,
    then why bother continuing?  I guess I could do everything in read only mode, but I'd rather not.
    */
        //if the storage device is writable and readable, set the current directory to the external storage location.
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            mCurrentListItems.setFile(Environment.getExternalStorageDirectory());

            Log.i(TAG, String.valueOf(mCurrentListItems.getFile()));
        } else {
            Log.i(TAG, "External storage unavailable");
        }
    }

    /* Now for the getters, setters, and utility methods.*/

    //get the current directory.
    public FileListItem getCurrentListItems() {
        return mCurrentListItems;
    }

    //set the current directory.
    public void setCurrentListItems(FileListItem current_list_items) {
        //rel001 setCurrentListItems START
        LogUtil.d( "CurrentList is " + FileListItem.getLogFileListItem(current_list_items) );
        this.mCurrentListItems = current_list_items;
        //rel001 setCurrentListItems END
    }

    //Returns whether or not we have a previous dir in our history.  If the stack is not empty, we have one.
    public boolean hasPreviousDir() {
        return !mHistoryListItems.isEmpty();
    }

    //return the previous dir and remove it from the stack.
    public FileListItem getPreviousListItems() {
        return mHistoryListItems.pop();
    }

    //set the previous dir for navigation.
    public void setPreviousListItems(FileListItem previous_list_items) {
        //dev001 setPreviousListItems START
        LogUtil.d( "PreviousList & HistoryList is " + FileListItem.getLogFileListItem(previous_list_items) );
        mHistoryListItems.add(previous_list_items);
        //dev001 setPreviousListItems END
    }

    //memo:
    private FileListItem getPrevDir() {
        //dev001 getPrevDir START
        if (hasPreviousDir()) {
            FileListItem file_list_item = mHistoryListItems.peek();
            LogUtil.d( "HistoryList is " + FileListItem.getLogFileListItem(file_list_item));
            return file_list_item;
        } else {
            LogUtil.d( "getPrevDir is null");
            return null;
        }
        //dev001 getPrevDir END
    }

    //Returns a sorted list of all dirs and files in a given directory.
    public List<FileListItem> getAllFiles(@org.jetbrains.annotations.NotNull FileListItem file_list_item) {
        //dev001 getAllFiles START amend
        //memo:ここでnullが帰ってくる理由はパーミッション問題だった
        File[] allFiles = file_list_item.getFile().listFiles();

        /* I want all directories to appear before files do, so I have separate lists for both that are merged into one later.*/
        List<FileListItem> alls = new ArrayList<>();
        List<FileListItem> dirs = new ArrayList<>();
        List<FileListItem> files = new ArrayList<>();
        FileListItem prevls = getPrevDir();

        for (File file: allFiles) {
            if (file.isDirectory()) {
                FileListItem dirFileListItem = new FileListItem(false, file.getName(), file);
                dirs.add(dirFileListItem);
            } else {
                FileListItem fileFileListItem = new FileListItem(false, file.getName(), file);
                files.add(fileFileListItem);
            }
        }

        Collections.sort(dirs, new FileInfoComparator());
        Collections.sort(files, new FileInfoComparator());

        //prevlsに値があればルートではないと判断し、"../"リストを追加
        if(!Objects.isNull(prevls)){
            alls.add(new FileListItem(true, "../", prevls.getFile()));
        }
        //ディレクトリとファイルのそれぞれがソートされているため、ディレクトリリストにファイルリストを追加すればソートされる
        alls.addAll(dirs);
        alls.addAll(files);

        //dev001 getAllFiles END amend
        return alls;
    }

    private class FileInfoComparator implements Comparator<FileListItem> {

        @Override
        public int compare(FileListItem p1, FileListItem p2) {
            //dev001 compare START
            return p1.getFile().compareTo(p2.getFile());
            //dev001 compare END
        }
    }

    //Try to determine the mime type of a file based on extension.
    public String getMimeType(Uri uri) {
        //dev001 getMimeType START
        String mimeType = null;

        String extension = MimeTypeMap.getFileExtensionFromUrl(uri.getPath());

        if (MimeTypeMap.getSingleton().hasExtension(extension)) {

            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        //dev002 getMimeType END
        return mimeType;
    }



}