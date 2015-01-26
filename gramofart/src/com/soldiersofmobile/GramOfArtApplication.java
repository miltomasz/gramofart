package com.soldiersofmobile;

import android.app.Application;
import android.os.Bundle;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.BitmapAjaxCallback;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.soldiersofmobile.model.Photo;
import com.soldiersofmobile.model.PhotoActivity;


public class GramOfArtApplication extends Application {
	
	
    private GramOfArtCache mCache = new GramOfArtCache();
    
    private static GramOfArtApplication sInstance;

    @Override
    public void onCreate() {
        sInstance = this;
        super.onCreate();
        ParseObject.registerSubclass(Photo.class);
        ParseObject.registerSubclass(PhotoActivity.class);

        Parse.initialize(this, Secrets.PARSE_APP_ID, Secrets.PARSE_CLIENT_KEY);
        ParseFacebookUtils.initialize("356703764460252");
        //356703764460252
        setupAQuery();
    }
    
    private void setupAQuery() {
        //set the max number of concurrent network connections, default is 4
        AjaxCallback.setNetworkLimit(8);

        //set the max number of icons (image width <= 50) to be cached in memory, default is 20
        BitmapAjaxCallback.setIconCacheLimit(20);

        //set the max number of images (image width > 50) to be cached in memory, default is 20
        BitmapAjaxCallback.setCacheLimit(40);

        float densityFactor = getResources().getDisplayMetrics().density;
        //set the max size of an image to be cached in memory, default is 1600 pixels (ie. 400x400)
        BitmapAjaxCallback.setPixelLimit((int) (400 * 400 * densityFactor * densityFactor));

        //set the max size of the memory cache, default is 1M pixels (4MB)
        BitmapAjaxCallback.setMaxPixelLimit((int) (2000000 * densityFactor * densityFactor));
    }

    public GramOfArtCache getCache() {
        return mCache;
    }

    public static GramOfArtApplication getInstance() {
        return sInstance;
    }

	public void startLoginUserWithFacebookToken(Bundle dataBundle) {
		
	}
}
