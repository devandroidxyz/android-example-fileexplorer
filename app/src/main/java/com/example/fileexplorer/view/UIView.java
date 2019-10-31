package com.example.fileexplorer.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.ListFragment;

import com.example.fileexplorer.R;
import com.example.fileexplorer.contract.OnBackKeyPressedListener;
import com.example.fileexplorer.presenter.Presenter;

/**
 * @Author Tom Farrell.   License: Whatever...
 */
public class UIView extends ListFragment implements OnBackKeyPressedListener {
    //This is a passive view, so my presenter handles all of the updating, etc.
    private Presenter presenter;

    private void setPresenter(Presenter p) {
        presenter = p;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.listfragment_main, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return presenter.homePressed();
    }

    //This is a good place to do final initialization as the Fragment is finished initializing itself.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setPresenter(new Presenter(this, getContext()));
    }

    //When we intercept a click, call through to the appropriate method in the presenter.
    @Override
    public void onListItemClick(ListView listView, android.view.View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        presenter.listItemClicked(listView, view, position, id);
    }

}