package com.soldiersofmobile.fragments;

import static com.soldiersofmobile.utils.LogUtils.LOGE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.soldiersofmobile.R;
import com.soldiersofmobile.utils.LogUtils;
import com.soldiersofmobile.views.CustomFontEditText;

public class PickLocationDialogFragment extends DialogFragment {

	protected String TAG = LogUtils.makeLogTag(this.getClass());
	private String mLocation;
	private List<Address> mAddressList = new ArrayList<Address>();
	private View mView;
	private EditText mLocationQueryEditText;
	private ListView mLocationsListView;
//	private ImageButton mBackImageButton;
	private Handler mHandler;
	private Runnable queryGeocoderRunnable;

	private class LocationListAdapter extends BaseAdapter {

		private final LayoutInflater mInflater;

		public LocationListAdapter(Context context) {
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return mAddressList.size();
		}

		@Override
		public Address getItem(int i) {
			return mAddressList.get(i);
		}

		@Override
		public long getItemId(int i) {
			return getItem(i).hashCode();
		}

		public void refresh() {
			if (getCount() > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			LocationViewHolder viewHolder = null;
			if (view == null) {
				viewHolder = new LocationViewHolder();
				view = mInflater.inflate(R.layout.location_item, null);
				viewHolder.locationNameTextView = (TextView) view.findViewById(R.id.location_name_tv);
				view.setTag(viewHolder);

			} else {
				viewHolder = (LocationViewHolder) view.getTag();
			}
			viewHolder.locationNameTextView.setText(addressToString(getItem(i)));

			return view;
		}

		private class LocationViewHolder {
			public TextView locationNameTextView;
		}
	}

	private LocationListAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.dialog_full_screen);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mHandler = new Handler();
		queryGeocoderRunnable = new Runnable() {
			@Override
			public void run() {
				if (getActivity() != null) {
					Geocoder geocoder = new Geocoder(getActivity());
					try {
						List<Address> addresses = geocoder.getFromLocationName(mLocationQueryEditText.getText().toString(), 10);
						mAddressList.clear();
						if (addresses != null)
							mAddressList.addAll(addresses);
						if (mAdapter != null)
							mAdapter.refresh();
					} catch (IOException e) {
						LOGE(TAG, "Error in geocoder", e);
					}
				}
			}
		};

		mView = inflater.inflate(R.layout.dialog_pick_location, container,false);
		mLocationQueryEditText = (EditText) mView.findViewById(R.id.location_query_et);
		mLocationQueryEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				runGeocoderQuery();
			}
		});

		if (mLocation != null) {
			mLocationQueryEditText.setText(mLocation);
		}
		mLocationsListView = (ListView) mView.findViewById(R.id.locations_lv);
		mAdapter = new LocationListAdapter(getActivity()
				.getApplicationContext());
		mLocationsListView.setAdapter(mAdapter);
		mLocationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> adapterView,
							View view, int i, long l) {
						Address address = mAdapter.getItem(i);
						mCallback.onLocationSelected(addressToString(address), address.getLatitude(), address.getLongitude());
						PickLocationDialogFragment.this.getDialog().dismiss();
					}
				});

		runGeocoderQuery();

		return mView;
	}

	protected void runGeocoderQuery() {
		if (mAdapter == null)
			return;
		mHandler.removeCallbacks(queryGeocoderRunnable);
		mHandler.postDelayed(queryGeocoderRunnable, 200);
	}

	private String addressToString(Address address) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
			stringBuilder.append(address.getAddressLine(i));
			if (i != address.getMaxAddressLineIndex()) {
				stringBuilder.append(", ");
			}
		}
		return stringBuilder.toString();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}

	public void setLocation(String location) {
		mLocation = location;
		if (mLocationQueryEditText != null) {

			mLocationQueryEditText.setText(mLocation);
		}
	}
	
	OnLocationSelectedListener mCallback;

    // Container Activity must implement this interface
    public interface OnLocationSelectedListener {
        public void onLocationSelected(String address, double latitude, double longitude);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnLocationSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


}
