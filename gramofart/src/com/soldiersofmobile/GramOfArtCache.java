package com.soldiersofmobile;


import com.soldiersofmobile.model.PhotoAtributes;

import java.util.HashMap;

public class GramOfArtCache {

    private HashMap<String, PhotoAtributes> mPhotoAttributesByPhotoId = new HashMap<String, PhotoAtributes>();

    public PhotoAtributes getPhotoAttributes(String photoId) {
        return mPhotoAttributesByPhotoId.get(photoId);
    }

    public void incrementPhotoLikes(String photoId) {
        PhotoAtributes photoAtributes = getPhotoAttributes(photoId);
        if(photoAtributes == null) {
            photoAtributes = new PhotoAtributes();
            mPhotoAttributesByPhotoId.put(photoId, photoAtributes);
        }
        photoAtributes.numberOfLikes++;



    }
}
