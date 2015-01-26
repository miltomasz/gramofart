package com.soldiersofmobile.activities;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.aviary.android.feather.FeatherActivity;
import com.aviary.android.feather.library.filters.FilterLoaderFactory;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.soldiersofmobile.Constants;
import com.soldiersofmobile.R;
import com.soldiersofmobile.fragments.PickLocationDialogFragment;
import com.soldiersofmobile.fragments.PickLocationDialogFragment.OnLocationSelectedListener;
import com.soldiersofmobile.model.Photo;

public class AddPhotoActivity extends BaseActivity implements View.OnClickListener,OnLocationSelectedListener{

    private static final int ACTION_TAKE_PHOTO_B = 2;
    private static final String BITMAP_STORAGE_KEY = "viewbitmap";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    // Standard storage location for digital camera files
    private static final String CAMERA_DIR = "/dcim/";
	private static final String PICK_LOCATION_DIALOG_FRAGMENT_TAG = "pick_location_dialog_fragment_tag";


    private ImageView mImageView;
    private Bitmap mImageBitmap;
    private String mCurrentPhotoPath;
    private Button mAddPhotoButton;
    private EditText mPhotoDescriptionEditText;
    private Bitmap mBitmapToSave;

    public static void startAddPhotoActivity(Context context) {
        Intent intent = new Intent(context, AddPhotoActivity.class);
        context.startActivity(intent);

    }

    /**
     * Indicates whether the specified action can be used as an intent. This
     * method queries the package manager for installed packages that can
     * respond to an intent with the specified action. If no suitable package is
     * found, this method returns false.
     * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
     *
     * @param context The application's environment.
     * @param action  The Intent action to check for availability.
     * @return True if an Intent with the specified action can be sent and
     * responded to, false otherwise.
     */
    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public File getAlbumStorageDir(String albumName) {
        return new File(
                Environment.getExternalStorageDirectory()
                        + CAMERA_DIR
                        + albumName
        );
    }

    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.album_name);
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    private Bitmap setPic(int targetW, int targetH) {

		/* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */


		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
        return  BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);


    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void callPickLocationDialog(){
    	PickLocationDialogFragment locationDialogFragment = new PickLocationDialogFragment();
        locationDialogFragment.setLocation("");
        locationDialogFragment.show(getSupportFragmentManager(), PICK_LOCATION_DIALOG_FRAGMENT_TAG);
    }
    
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


        File f = null;

        try {
            f = setUpPhotoFile();
            mCurrentPhotoPath = f.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            mCurrentPhotoPath = null;
        }

        startActivityForResult(takePictureIntent, ACTION_TAKE_PHOTO_B);
    }

    private void handleSmallCameraPhoto(Intent intent) {
        Bundle extras = intent.getExtras();
        mImageBitmap = (Bitmap) extras.get("data");
        mImageView.setImageBitmap(mImageBitmap);
        mImageView.setVisibility(View.VISIBLE);
    }

    private void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {

            startFeather(Uri.parse(mCurrentPhotoPath));
//            Intent newIntent = new Intent( this, FeatherActivity.class );
//            newIntent.setData( Uri.parse(mCurrentPhotoPath) );
//            startActivityForResult(newIntent, 1);
//            Bitmap bitmap = setPic(mImageView.getWidth(), mImageView.getHeight());
//            mImageView.setImageBitmap(bitmap);
//
//            galleryAddPic();

        }

    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

        mImageView = (ImageView) findViewById(R.id.imageView1);
        mImageBitmap = null;
        mAddPhotoButton = (Button) findViewById(R.id.add_photo_button);
        mAddPhotoButton.setOnClickListener(this);
        mPhotoDescriptionEditText = (EditText) findViewById(R.id.add_photo_description_et);

        Button picBtn = (Button) findViewById(R.id.btnIntend);
        setBtnListenerOrDisable(
                picBtn,
                this,
                MediaStore.ACTION_IMAGE_CAPTURE
        );
        
        
        // TODO: getRid of THIS DIRTY HACKS //
        this.mLatitude = 52.2297;
        this.mLongitude = 21.0122;
        this.mAddres = "Warszawa";
        //-------------------------------//

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTION_TAKE_PHOTO_B: {
                if (resultCode == RESULT_OK) {
                    handleBigCameraPhoto();
                }
                break;
             } // ACTION_TAKE_PHOTO_B
            case ACTION_REQUEST_FEATHER:
                // output image path
                Uri mImageUri = data.getData();
                Bundle extra = data.getExtras();
                if( null != extra ) {
                    // image has been changed by the user?
                    boolean changed = extra.getBoolean( Constants.EXTRA_OUT_BITMAP_CHANGED );
                    mCurrentPhotoPath = mImageUri.getPath();
                    mImageView.setImageURI(mImageUri);
                }
                break;

        } // switch
    }

    String mOutputFilePath;
    Uri mImageUri;
    int imageWidth, imageHeight;
    private File mGalleryFolder;
	private String mAddres;
	private double mLatitude;
	private double mLongitude;


    /**
     * Return a new image file. Name is based on the current time. Parent folder will be the one created with createFolders
     *
     * @return
     * @see #createFolders()
     */
    private File getNextFileName() {
        if (mGalleryFolder != null) {
            if (mGalleryFolder.exists()) {
                File file = new File(mGalleryFolder, "aviary_" + System.currentTimeMillis() + ".jpg");
                return file;
            }
        }
        return null;
    }
    /**
     * Once you've chosen an image you can start the feather activity
     *
     * @param uri
     */
    @SuppressWarnings("deprecation")
    private void startFeather(Uri uri) {

        //LOGD(LOG_TAG, "uri: " + uri);

        // first check the external storage availability
        if (!isExternalStorageAvilable()) {
            showDialog(EXTERNAL_STORAGE_UNAVAILABLE);
            return;
        }

//        // create a temporary file where to store the resulting image
//        File file = getNextFileName();
//
//
//        if (null != file) {
//            mOutputFilePath = file.getAbsolutePath();
//        } else {
//            new AlertDialog.Builder(this).setTitle(android.R.string.dialog_alert_title).setMessage("Failed to create a new File")
//                    .show();
//            return;
//        }

        // Create the intent needed to start feather
        Intent newIntent = new Intent(this, FeatherActivity.class);

        // === INPUT IMAGE URI ( MANDATORY )===
        // Set the source image uri
        newIntent.setData(uri);

        // === OUTPUT ====
        // Optional
        // Pass the uri of the destination image file.
        // This will be the same uri you will receive in the onActivityResult
        newIntent.putExtra(com.aviary.android.feather.library.Constants.EXTRA_OUTPUT, uri);

        // === OUTPUT FORMAT ===
        // Optional
        // Format of the destination image
        newIntent.putExtra(com.aviary.android.feather.library.Constants.EXTRA_OUTPUT_FORMAT, Bitmap.CompressFormat.JPEG.name());

        // === OUTPUT QUALITY ===
        // Optional
        // Output format quality (jpeg only)
        newIntent.putExtra(com.aviary.android.feather.library.Constants.EXTRA_OUTPUT_QUALITY, 90);

        // === ENABLE/DISABLE IAP FOR EFFECTS ===
        // Optional
        // If you want to disable the external effects
        // newIntent.putExtra( Constants.EXTRA_EFFECTS_ENABLE_EXTERNAL_PACKS, false );

        // === ENABLE/DISABLE IAP FOR FRAMES===
        // Optional
        // If you want to disable the external borders.
        // Note that this will remove the frames tool.
        // newIntent.putExtra( Constants.EXTRA_FRAMES_ENABLE_EXTERNAL_PACKS, false );

        // == ENABLE/DISABLE IAP FOR STICKERS ===
        // Optional
        // If you want to disable the external stickers. In this case you must have a folder called "stickers" in your assets folder
        // containing a list of .png files, which will be your default stickers
        // newIntent.putExtra( Constants.EXTRA_STICKERS_ENABLE_EXTERNAL_PACKS, false );

        // enable fast rendering preview
        // newIntent.putExtra( Constants.EXTRA_EFFECTS_ENABLE_FAST_PREVIEW, true );

        // == TOOLS LIST ===
        // Optional
        // You can force feather to display only some tools ( see FilterLoaderFactory#Filters )
        // you can omit this if you just want to display the default tools


        newIntent.putExtra("tools-list", new String[]{
                FilterLoaderFactory.Filters.ENHANCE.name(),
                FilterLoaderFactory.Filters.EFFECTS.name(),
                FilterLoaderFactory.Filters.BORDERS.name(),
                FilterLoaderFactory.Filters.STICKERS.name(),
//                FilterLoaderFactory.Filters.CROP.name(),

                FilterLoaderFactory.Filters.TILT_SHIFT.name(),
                FilterLoaderFactory.Filters.ADJUST.name(),
                FilterLoaderFactory.Filters.BRIGHTNESS.name(),
                FilterLoaderFactory.Filters.CONTRAST.name(),
                FilterLoaderFactory.Filters.SATURATION.name(),
                FilterLoaderFactory.Filters.COLORTEMP.name(),
                FilterLoaderFactory.Filters.SHARPNESS.name(),
                FilterLoaderFactory.Filters.COLOR_SPLASH.name(),
                FilterLoaderFactory.Filters.DRAWING.name(),
                FilterLoaderFactory.Filters.TEXT.name(),
                FilterLoaderFactory.Filters.RED_EYE.name(),
                FilterLoaderFactory.Filters.WHITEN.name(),
                FilterLoaderFactory.Filters.BLEMISH.name(),
                FilterLoaderFactory.Filters.MEME.name(),
        });


        // === EXIT ALERT ===
        // Optional
        // Uou want to hide the exit alert dialog shown when back is pressed
        // without saving image first
        // newIntent.putExtra( Constants.EXTRA_HIDE_EXIT_UNSAVE_CONFIRMATION, true );

        // === VIBRATION ===
        // Optional
        // Some aviary tools use the device vibration in order to give a better experience
        // to the final user. But if you want to disable this feature, just pass
        // any value with the key "tools-vibration-disabled" in the calling intent.
        // This option has been added to version 2.1.5 of the Aviary SDK
        // newIntent.putExtra( Constants.EXTRA_TOOLS_DISABLE_VIBRATION, true );

        // === MAX SIZE ===
        // Optional
        // you can pass the maximum allowed image size (for the preview), otherwise feather will determine
        // the max size based on the device informations.
        // This will not affect the hi-res image size.
        // Here we're passing the current display size as max image size because after
        // the execution of Aviary we're saving the HI-RES image so we don't need a big
        // image for the preview
        final DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int max_size = Math.max(metrics.widthPixels, metrics.heightPixels);
        max_size = (int) ((float) max_size / 1.2f);
        newIntent.putExtra(com.aviary.android.feather.library.Constants.EXTRA_MAX_IMAGE_SIZE, max_size);

        // === HI-RES ===
        // You need to generate a new session id key to pass to Aviary feather
        // this is the key used to operate with the hi-res image ( and must be unique for every new instance of Feather )
        // The session-id key must be 64 char length.
        // In your "onActivityResult" method, if the resultCode is RESULT_OK, the returned
        // bundle data will also contain the "session" key/value you are passing here.
//        mSessionId = StringUtils.getSha256(System.currentTimeMillis() + API_KEY);
//        LOGD(LOG_TAG, "session: " + mSessionId + ", size: " + mSessionId.length());
//        newIntent.putExtra( com.aviary.android.feather.library.Constants.EXTRA_OUTPUT_HIRES_SESSION_ID, mSessionId );

        // === NO CHANGES ==
        // With this extra param you can tell to FeatherActivity how to manage
        // the press on the Done button even when no real changes were made to
        // the image.
        // If the value is true then the image will be always saved, a RESULT_OK
        // will be returned to your onActivityResult and the result Bundle will
        // contain an extra value "EXTRA_OUT_BITMAP_CHANGED" indicating if the
        // image was changed during the session.
        // If "false" is passed then a RESULT_CANCEL will be sent when an user will
        // hit the 'Done' button without any modifications ( also the EXTRA_OUT_BITMAP_CHANGED
        // extra will be sent back to the onActivityResult.
        // By default this value is true ( even if you omit it )
        newIntent.putExtra(com.aviary.android.feather.library.Constants.EXTRA_IN_SAVE_ON_NO_CHANGES, true);

        // ..and start feather
        startActivityForResult(newIntent, ACTION_REQUEST_FEATHER);
    }

    private static final int ACTION_REQUEST_FEATHER = 100;
    private static final int EXTERNAL_STORAGE_UNAVAILABLE = 1;

    public static final String LOG_TAG = "aviary-launcher";

    /**
     * Delete a file without throwing any exception
     *
     * @param path
     * @return
     */
    private boolean deleteFileNoThrow(String path) {
        File file;
        try {
            file = new File(path);
        } catch (NullPointerException e) {
            return false;
        }

        if (file.exists()) {
            return file.delete();
        }
        return false;
    }


//    @Override
//    protected Dialog onCreateDialog(int id) {
//        Dialog dialog = null;
//        switch (id) {
//            // external sdcard is not mounted!
//            case EXTERNAL_STORAGE_UNAVAILABLE:
//                dialog = new AlertDialog.Builder(this).setTitle(R.string.external_storage_na_title)
//                        .setMessage(R.string.external_storage_na_message).create();
//                break;
//        }
//        return dialog;
//    }

    /**
     * apikey is required http://developers.aviary.com/
     */
    private static final String API_KEY = "xxxxx";

    /**
     * Folder name on the sdcard where the images will be saved *
     */
    private static final String FOLDER_NAME = "aviary";

    /**
     * Check the external storage status
     *
     * @return
     */
    private boolean isExternalStorageAvilable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    // Some lifecycle callbacks so that the image can survive orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
        outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
        mImageView.setImageBitmap(mImageBitmap);
        mImageView.setVisibility(
                savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ?
                        ImageView.VISIBLE : ImageView.INVISIBLE
        );
    }

    private void setBtnListenerOrDisable(
            Button btn,
            Button.OnClickListener onClickListener,
            String intentName
    ) {
        if (isIntentAvailable(this, intentName)) {
            btn.setOnClickListener(onClickListener);
        } else {
            btn.setText(
                    getText(R.string.cannot).toString() + " " + btn.getText());
            btn.setClickable(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnIntend:
                dispatchTakePictureIntent();
                break;
            case R.id.add_photo_button:
                addPhotoToParse();
                break;                
            case R.id.add_photo_location_tv:
            	callPickLocationDialog();
            	break;
        }
    }

    private void addPhotoToParse() {

        String description = mPhotoDescriptionEditText.getText().toString().trim();


        Photo photoObject = new Photo();
        Bitmap bitmap = setPic(Constants.PARSE_IMAGE_MAX_SIZE, Constants.PARSE_IMAGE_MAX_SIZE);
        if(bitmap == null) {
            Toast.makeText(AddPhotoActivity.this, getString(R.string.photo_save_error)
                    + getString(R.string.image_not_fount_error), Toast.LENGTH_SHORT).show();
            return;
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] data = stream.toByteArray();

        final ParseFile file = new ParseFile(data);
        file.saveInBackground();


        photoObject.put(Constants.USER_PARSE_KEY, ParseUser.getCurrentUser());
        photoObject.put(Constants.DESCRIPTION_PARSE_KEY, description);
        photoObject.put(Constants.FILE_PARSE_KEY, file);
        
        if(!TextUtils.isEmpty(mAddres)){
	        photoObject.put(Constants.ADDRESS_PARSE_KEY, mAddres);
	        photoObject.put(Constants.LONGITUDE_PARSE_KEY, mLongitude);
	        photoObject.put(Constants.LATITUDE_PARSE_KEY, mLatitude);
        }
        

        ParseACL photoACL = new ParseACL(ParseUser.getCurrentUser());
        photoACL.setPublicReadAccess(true);
//        photoACL.setPublicWriteAccess(true);        

        photoObject.setACL(photoACL);

        photoObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    Toast.makeText(AddPhotoActivity.this, getString(R.string.photo_save_error) + e.getMessage(),
                            Toast.LENGTH_SHORT).show();

                } else {
                    finish();
                }
            }
        });


    }

	@Override
	public void onLocationSelected(String address, double latitude,double longitude) {
		this.mAddres = address;
		this.mLatitude = latitude;
		this.mLongitude = longitude;
		
	}
}
