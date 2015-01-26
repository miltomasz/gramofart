package com.soldiersofmobile.adapters;

import java.util.Arrays;

import android.content.Context;
import android.net.ParseException;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.androidquery.AQuery;
import com.parse.GetDataCallback;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.soldiersofmobile.R;
import com.soldiersofmobile.model.Photo;

public class ArtCustomAdapter extends ParseQueryAdapter<Photo> {
	 
    public ArtCustomAdapter(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<Photo>() {
            public ParseQuery<Photo> create() {
                ParseQuery<Photo> query = new ParseQuery<Photo>("Photo");
                query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
                return query;
            }
        });
    }
    
    @Override
    public View getItemView(Photo photo, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.art_custom_item, null);
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
        
        super.getItemView(photo, convertView, parent);
        
        ParseFile photoFile = photo.getPhotoFile();
        
        if(photoFile != null && !TextUtils.isEmpty(photoFile.getUrl())){
        	AQuery aQuery = new AQuery(convertView);
        	aQuery.id(holder.artIv).image(photoFile.getUrl());
        }
        
//        if (photoFile != null) {
//        	holder.artIv.setParseFile(photoFile);
//        	holder.artIv.loadInBackground(new GetDataCallback() {
//
//				@Override
//				public void done(byte[] data, com.parse.ParseException e) {
//					
//				}
//            });
//        }
        
        holder.authorName.setText(photo.getDescription());
//        String number = Integer.toString(i + 1);
//        holder.authorName.setText("John Doe " + number);
//        holder.publishedTimeTv.setText(number + " days ago");
//        holder.adoreCounterTglBtn.setText(number);
//        holder.adoreCounterTglBtn.setTextOff(number);
//        holder.adoreCounterTglBtn.setTextOn(number);
        return convertView;
    }

    
    static class ViewHolder {
        TextView authorName;
        ImageView authorAvatarIv;
        ImageView artIv;
        TextView publishedTimeTv;
        ToggleButton adoreCounterTglBtn;
    }
}