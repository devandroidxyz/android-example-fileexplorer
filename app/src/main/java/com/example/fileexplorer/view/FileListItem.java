package com.example.fileexplorer.view;

import java.io.File;

public class FileListItem {
    private Boolean mIsPrev;
    private String mName;
    private File mFile = null;

//    FileListItem(boolean isRoot, String name, FileListItem back) {};

    public FileListItem(Boolean isPrev, String name, File file) {
        mIsPrev = isPrev;
        mName = name;
        mFile = file;
    }

    public Boolean isPrev() {
        return mIsPrev;
    }

    public void setPrev(Boolean isPrev) {
        mIsPrev = isPrev;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public File getFile() {
        return mFile;
    }

    public void setFile(File file) {
        mFile = file;
    }

    public static String getLogFileListItem(FileListItem file_list_item) {
        return  "IsPrev = " + file_list_item.isPrev() +
                " : Name = " + file_list_item.getName() +
                " : File = " + file_list_item.getFile();
    }
}
