
package com.soldiersofmobile.adapters;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.soldiersofmobile.R;

public class ArtAdapter extends BaseAdapter {

    private static final String TAG = ArtAdapter.class.getSimpleName();

    private final Context mContext;
    private ArrayList<ArtItem> mPosts = new ArrayList<ArtItem>();
    private LayoutInflater mInflater;

    public ArtAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        // TODO
        // return mPosts.size();
        return 8;
    }

    @Override
    public ArtItem getItem(int i) {
        return null;
        // return mPosts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
//        return getItem(i).hashCode();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.art_item, null);
            holder = new ViewHolder();
            holder.artIv = (ImageView) convertView.findViewById(R.id.art_image_view);
            holder.authorAvatarIv = (ImageView) convertView.findViewById(R.id.author_avatar_iv);
            holder.authorName = (TextView) convertView.findViewById(R.id.author_name);
            holder.adoreCounterTglBtn = (ToggleButton) convertView
                    .findViewById(R.id.adore_counter_tglbtn);
            holder.publishedTimeTv = (TextView) convertView.findViewById(R.id.published_time_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String number = Integer.toString(i + 1);
        holder.authorName.setText("John Doe " + number);
        holder.publishedTimeTv.setText(number + " days ago");
        holder.adoreCounterTglBtn.setText(number);
        holder.adoreCounterTglBtn.setTextOff(number);
        holder.adoreCounterTglBtn.setTextOn(number);
        holder.artIv.setImageBitmap(getBitmapFromAsset("art_" + number +".jpg"));

        return convertView;
    }

    public void addArts(ArrayList<ArtItem> arts) {

        if (isEmpty()) {
            notifyDataSetInvalidated();
        } else {
            notifyDataSetChanged();
        }
    }

    static class ViewHolder {
        TextView authorName;
        ImageView authorAvatarIv;
        ImageView artIv;
        TextView publishedTimeTv;
        ToggleButton adoreCounterTglBtn;
    }

    private Bitmap getBitmapFromAsset(String strName) {
        AssetManager assetManager = mContext.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(strName);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            return null;
        }

        return bitmap;
    }
   
}
