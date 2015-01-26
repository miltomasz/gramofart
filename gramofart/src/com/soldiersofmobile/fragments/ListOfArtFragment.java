package com.soldiersofmobile.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.parse.ParseQueryAdapter;
import com.soldiersofmobile.Constants;
import com.soldiersofmobile.R;
import com.soldiersofmobile.activities.AddPhotoActivity;
import com.soldiersofmobile.adapters.ArtAdapter;
import com.soldiersofmobile.adapters.ArtAdapter_Parse;
import com.soldiersofmobile.adapters.ArtCustomAdapter;
import com.soldiersofmobile.model.Photo;

public class ListOfArtFragment extends SherlockFragment {

	private View mView;
	private ListView mListOfArt;
	private ArtAdapter_Parse artAdapter_Parse;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.list_of_art_fragment_layout, container, false);
		return mView;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		bindFields();
		setUpAdapters();
	}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_settings) {
            AddPhotoActivity.startAddPhotoActivity(getActivity());
            return true;
        } else {
            return super.onOptionsItemSelected(item);

        }
    }

    private void bindFields() {
		mListOfArt = (ListView)mView.findViewById(R.id.list_of_art);
	}

	private void setUpAdapters() {

        // inside an Activity
        ParseQueryAdapter<Photo> adapter = new ParseQueryAdapter<Photo>(getActivity(), Photo.class);
        ArtCustomAdapter adapterCustom = new ArtCustomAdapter(getActivity());
        artAdapter_Parse = new ArtAdapter_Parse(getActivity());
        
        adapter.setTextKey(Constants.DESCRIPTION_PARSE_KEY);
        adapter.setImageKey(Constants.FILE_PARSE_KEY);       

        

        mListOfArt.setAdapter(artAdapter_Parse);

//		ArtAdapter artdAdapter = new ArtAdapter(getActivity());
//		mListOfArt.setAdapter(artdAdapter);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getView().post(new Runnable() {
			
			@Override
			public void run() {
				artAdapter_Parse.reload();
			}
		});	
	}
}
