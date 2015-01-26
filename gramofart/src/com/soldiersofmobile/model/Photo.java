package com.soldiersofmobile.model;


import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.soldiersofmobile.Constants;

@ParseClassName(Constants.PHOTO_PARSE_CLASS_NAME)
public class Photo extends ParseObject {

    public Photo() {

    }
    
    public String getDescription(){
        return getString("description");
    }
 
    public int getLikesCount(){
    	return getInt("like_counter");
    }
    
    public void setTitle(String title) {
        put("title", title);
    }
    
    public void addLike(){
    	increment("like_counter");
    }
    
    public void removeLike(){
    	increment("like_counter",-1);
    }
 
    public ParseFile getPhotoFile() {
        return getParseFile("file");
    }
    
 
    public void setPhotoFile(ParseFile file) {
        put("file", file);
    }


}
