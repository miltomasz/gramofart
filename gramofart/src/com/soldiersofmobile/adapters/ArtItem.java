package com.soldiersofmobile.adapters;

public class ArtItem {

	private long mArtId;
	private String mAuthorAvatarUrl;
	private long mDatePublished;
	private long mAdoreCounter;
	private String mAuthorName;
	
	
	
	
	public ArtItem(long artId, String authorAvatarUrl, long datePublished,
			long adoreCounter, String authorName) {
		mArtId = artId;
		mAuthorAvatarUrl = authorAvatarUrl;
		mDatePublished = datePublished;
		mAdoreCounter = adoreCounter;
		mAuthorName = authorName;
	}
	
	public long getArtId() {
		return mArtId;
	}
	public void setArtId(long artId) {
		mArtId = artId;
	}
	public String getAuthorAvatarUrl() {
		return mAuthorAvatarUrl;
	}
	public void setAuthorAvatarUrl(String authorAvatarUrl) {
		mAuthorAvatarUrl = authorAvatarUrl;
	}
	public long getDatePublished() {
		return mDatePublished;
	}
	public void setDatePublished(long datePublished) {
		mDatePublished = datePublished;
	}
	public long getAdoreCounter() {
		return mAdoreCounter;
	}
	public void setAdoreCounter(long adoreCounter) {
		mAdoreCounter = adoreCounter;
	}
	public String getAuthorName() {
		return mAuthorName;
	}
	public void setAuthorName(String authorName) {
		mAuthorName = authorName;
	}
	
	
	
	
	
}
