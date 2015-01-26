package com.soldiersofmobile.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.androidquery.AQuery;
import com.androidquery.callback.BitmapAjaxCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.soldiersofmobile.R;
import com.soldiersofmobile.model.Photo;
import com.soldiersofmobile.model.PhotoActivity;

public class ArtAdapter_Parse extends BaseAdapter {

	private static final String TAG = ArtAdapter_Parse.class.getSimpleName();
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

	private final Context mContext;
	private ArrayList<Photo> mPosts = new ArrayList<Photo>();
	private LayoutInflater mInflater;

	private Bitmap mPlaceholder;

	private int mWidth;
	private int mListMargin;

	public ArtAdapter_Parse(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mPlaceholder = ((BitmapDrawable) mContext.getResources().getDrawable(R.drawable.art_1)).getBitmap();
		mListMargin = mContext.getResources().getDimensionPixelSize(R.dimen.list_of_art_margin);
		mWidth = mContext.getResources().getDisplayMetrics().widthPixels - 2 * mListMargin;
	}

	@Override
	public int getCount() {
		return mPosts.size();
	}

	@Override
	public Photo getItem(int i) {
		return mPosts.get(i);
	}

	@Override
	public long getItemId(int i) {
		return mPosts.get(i).getObjectId().hashCode();
	}

	@Override
	public boolean isEmpty() {
		return mPosts.isEmpty();
	}
	
	@Override
	public View getView(int i, View convertView, ViewGroup viewGroup) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.art_custom_item, null);
			holder = new ViewHolder();
			holder.artIv = (ImageView) convertView.findViewById(R.id.art_image_view);
			holder.authorAvatarIv = (ImageView) convertView.findViewById(R.id.author_avatar_iv);
			holder.authorName = (TextView) convertView.findViewById(R.id.author_name);
			holder.adoreCounterTglBtn = (ToggleButton) convertView.findViewById(R.id.adore_counter_tglbtn);
			holder.publishedTimeTv = (TextView) convertView.findViewById(R.id.published_time_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Photo photo = getItem(i);
		if(photo != null){			
			ParseFile photoFile = photo.getPhotoFile();
			String photoUrl = photoFile.getUrl();
	
			if (photoFile != null && !TextUtils.isEmpty(photoUrl)) {
				 	AQuery aq = new AQuery(convertView);

			        Bitmap bitmap = BitmapAjaxCallback.getMemoryCached(photoUrl, mWidth);

			        if (aq.shouldDelay(i, convertView, viewGroup, photoUrl)) {
			            aq.id(holder.artIv).image(bitmap == null ? mPlaceholder : bitmap, AQuery.RATIO_PRESERVE);
			        } else {
			            aq.id(holder.artIv).image(photoUrl, true, true, mWidth, 0, mPlaceholder, 0);
			        }
			}
	
			holder.authorName.setText(photo.getDescription());
			holder.publishedTimeTv.setText(dateFormat.format(photo.getCreatedAt()));
			String number = Integer.toString(photo.getLikesCount());
//			 holder.authorName.setText("John Doe " + number);
			holder.adoreCounterTglBtn.setText(number);
			holder.adoreCounterTglBtn.setTextOff(number);
			holder.adoreCounterTglBtn.setTextOn(number);
			
			
			holder.adoreCounterTglBtn.setEnabled(false);
			
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Photo");			 
			query.whereEqualTo("likes", ParseUser.getCurrentUser());
			query.whereEqualTo("objectId", photo.getObjectId());
			query.findInBackground(new FindCallback<ParseObject>() {
				
				@Override
				public void done(List<ParseObject> arg0, ParseException arg1) {
					if(arg0 != null && arg0.size()>0){									
							holder.adoreCounterTglBtn.setChecked(true);							
					}
					holder.adoreCounterTglBtn.setEnabled(true);
				}
			});
			
			
			holder.adoreCounterTglBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(final View buttonView) {					
					
						holder.adoreCounterTglBtn.setEnabled(false);
						
						ParseRelation<ParseObject> relation = photo.getRelation("likes");
	                    
	                    if(holder.adoreCounterTglBtn.isChecked()){	                    
	                    	photo.addLike();
	                    	relation.add(ParseUser.getCurrentUser());
	                    }else{
	                    	photo.removeLike();
	                    	relation.remove(ParseUser.getCurrentUser());
	                    }
	                    
	                    photo.saveInBackground( new SaveCallback() {
							
							@Override
							public void done(ParseException arg0) {
								((CompoundButton)buttonView).setText(Integer.toString(photo.getLikesCount()));
								holder.adoreCounterTglBtn.setEnabled(true);
							}
						});
				}
			});
			
//            holder.adoreCounterTglBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
//                    if(isChecked) {
//                    	ParseRelation<ParseObject> relation = photo.getRelation("likes");
//                        relation.add(ParseUser.getCurrentUser());
//                        photo.addLike();
//                        photo.saveInBackground( new SaveCallback() {
//							
//							@Override
//							public void done(ParseException arg0) {
//								buttonView.setText(Integer.toString(photo.getLikesCount()));
//							}
//						});
//                    	
//                    	
////                        PhotoActivity activity = new PhotoActivity();
////                        activity.put("type", "like");
////                        activity.put("fromUser", ParseUser.getCurrentUser());
////                        activity.put("toUser", photo.getParseUser("user"));
////                        activity.put("photo", photo);
////                        activity.saveInBackground(new SaveCallback() {
////                            @Override
////                            public void done(ParseException e) {
////                                buttonView.setText("1");
//////                                buttonView.setTextOff("1");
//////                                buttonView.setTextOn("1");
////                            }
////                        });
//                    } else {
//                    	buttonView.setEnabled(false);
////                        buttonView.setChecked(true);
//                    }
//                }
//            });
		
		}
		return convertView;
	}

	static class ViewHolder {
		TextView authorName;
		ImageView authorAvatarIv;
		ImageView artIv;
		TextView publishedTimeTv;
		ToggleButton adoreCounterTglBtn;
	}


	public void reload(){
    	ParseQuery<Photo> query = new ParseQuery<Photo>(Photo.class);
    	query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
    	query.findInBackground(new FindCallback<Photo>() {
			

			@Override
			public void done(List<Photo> photoList, ParseException ex) {
				if(ex == null){
					if(photoList != null && !photoList.isEmpty()){
						mPosts.clear();
						mPosts.addAll(photoList);
						notifyDataSetChanged();
					}else{
						notifyDataSetInvalidated();
					}
				}else{
					Toast.makeText(mContext, "Problem occured!" + ex , Toast.LENGTH_SHORT).show();
				}
			}
		});
    }
}
