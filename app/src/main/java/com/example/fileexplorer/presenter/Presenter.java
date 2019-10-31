package com.example.fileexplorer.presenter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;

import androidx.core.content.FileProvider;

import com.example.fileexplorer.BuildConfig;
import com.example.fileexplorer.R;
import com.example.fileexplorer.model.FileListModel;
import com.example.fileexplorer.view.FileListAdapter;
import com.example.fileexplorer.view.FileListItem;
import com.example.fileexplorer.view.UIView;

import java.util.ArrayList;
import java.util.List;

/**
 *         The main job of the presenter is to marshall data to and from the view.   Logic in the
 *         presenter is kept to a minimum, with only the logic required to format and marshall data between
 *         the view and model done here.
 **/
    public class Presenter implements LoaderManager.LoaderCallbacks<List<FileListItem>> {
    private UIView mView; //Our view.
    private FileListModel mFileListModel; //Our model.
    private Context mContext; //Our model.
    private FileListAdapter mFileListAdapter; //The adapter containing data for our list.
    private List<FileListItem> mData; //The list of all files for a specific dir.
    private AsyncTaskLoader<List<FileListItem>> mFileLoader; /*Loads the list of files from the model in
    a background thread.*/

    // @@@@@ Context渡すのはやめたい。。
    // http://masahide318.hatenablog.jp/entry/2017/11/21/003525
    public Presenter(UIView view, Context context) {
        mView = view;
        mContext = context;
        mFileListModel = new FileListModel();
        mData = new ArrayList<>();
        init();
    }

    private void init() {
        //Instantiate and configure the file adapter with an empty list that our loader will update..
        mFileListAdapter = new FileListAdapter(mView.getActivity(), R.layout.list_row, mData);

        mView.setListAdapter(mFileListAdapter);

        /*Start the AsyncTaskLoader that will update the adapter for
        the ListView. We update the adapter in the onLoadFinished() callback.
        */
        mView.getActivity().getLoaderManager().initLoader(0, null, this);

        //Grab our first list of results from our loader.  onFinishLoad() will call updataAdapter().
        mFileLoader.forceLoad();
    }

    /*Called to update the Adapter with a new list of files when mCurrentDir changes.*/
    private void updateAdapter(List<FileListItem> data) {

        //clear the old data.
        mFileListAdapter.clear();
        //add the new data.
        mFileListAdapter.addAll(data);
        //inform the ListView to refrest itself with the new data.
        mFileListAdapter.notifyDataSetChanged();
    }

    public void listItemClicked(ListView l, View v, int position, long id) {
        //The file we clicked based on row position where we clicked.  I could probably word that better. :)
        FileListItem fileClicked = mFileListAdapter.getItem(position);

        //ルート以外であれば一段上のディレクトリへ
        if (fileClicked.isPrev()) {
            homePressed();
        } else if (fileClicked.getFile().isDirectory()) {

            //we are changing dirs, so save the previous dir as the one we are currently in.
            mFileListModel.setPreviousListItems(mFileListModel.getCurrentListItems());

            //set the current dir to the dir we clicked in the listview.
            mFileListModel.setCurrentListItems(fileClicked);

            //Let the loader know that our content has changed and we need a new load.
            if (mFileLoader.isStarted()) {
                mFileLoader.onContentChanged();
            }
        } else { //Otherwise, we have clicked a file, so attempt to open it.
            // @@@@ http://kit-lab.hatenablog.jp/entry/2017/01/08/011428
            openFile( FileProvider.getUriForFile( mContext, BuildConfig.APPLICATION_ID + ".fileprovider", fileClicked.getFile()));
//            openFile(Uri.fromFile(fileClicked.getFile()));
        }
    }

    //Fires intents to handle files of known mime types.
    private void openFile(Uri fileUri) {

        String mimeType = mFileListModel.getMimeType(fileUri);

        if (mimeType != null) { //we have determined a mime type and can probably handle the file.
            try {
                /*Implicit intent representing the action we want.  The system will determine is it
                can handle the request.*/
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setDataAndType(fileUri, mimeType);
//                mView.getActivity().startActivity(i);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(fileUri, mimeType);
                // @@@@ Intent投げてチューザー出てPhotosを選択したがコンテンツが表示されない。
                // ログを確認したところ、以下のWarningが出ていた。これがヒントになった。FLAG_GRANT_READ_URI_PERMISSIONが必要。
                // 09-25 13:35:30.508 +0000  1300  4831 W UriGrantsManagerService: No permission grants found for com.google.android.apps.photos
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if(intent.resolveActivity(mContext.getPackageManager()) != null) {
                    mView.getActivity().startActivity(intent);
                }

            } catch (ActivityNotFoundException e) {
                /*If we have figured out the mime type of the file, but have no application installed
                to handle it, send the user a message.
                 */
                Toast.makeText(mView.getActivity(), "The System understands this file type," +
                                "but no applications are installed to handle it.",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            /*if we can't figure out the mime type of the file, let the user know.*/
            Toast.makeText(mView.getActivity(), "System doesn't know how to handle that file type!",
                    Toast.LENGTH_LONG).show();
        }
    }

    /*Called when the user presses the home button on the ActionBar to navigate back to
     our previous location, if we have one.*/
    public boolean homePressed() {
        if (mFileListModel.hasPreviousDir()) {
            mFileListModel.setCurrentListItems(mFileListModel.getPreviousListItems());

            //Our content has changed, so we need a new load.
            mFileLoader.onContentChanged();
            return false;
        } else {
            return true;
        }
    }

    //Loader callbacks.
    @Override
    public Loader<List<FileListItem>> onCreateLoader(int id, Bundle args) {
        mFileLoader = new AsyncTaskLoader<List<FileListItem>>(mView.getActivity()) {

            //Get our new data load.
            @Override
            public List<FileListItem> loadInBackground() {
                Log.i("Loader", "loadInBackground()");
                //memo:ここの戻り地がonLoadFinishedの引数で展開される
                return mFileListModel.getAllFiles(mFileListModel.getCurrentListItems());
            }
        };

        return mFileLoader;
    }

    //Called when the loader has finished acquiring its load.
    @Override
    public void onLoadFinished(Loader<List<FileListItem>> loader, List<FileListItem> data) {

        this.mData = data;

        /* My data source has changed so now the adapter needs to be reset to reflect the changes
        in the ListView.*/
        updateAdapter(data);
    }

    @Override
    public void onLoaderReset(Loader<List<FileListItem>> loader) {
        //not used for this data source.
    }
}
