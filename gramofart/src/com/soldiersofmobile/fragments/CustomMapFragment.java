package com.soldiersofmobile.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.TextView;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.soldiersofmobile.R;

import org.joda.time.*;

import java.util.List;

public class CustomMapFragment extends SupportMapFragment
    implements GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerDragListener

    {
        private static final LatLng BRISBANE = new LatLng(-27.47093, 153.0235);
        private static final LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);
        private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);
        private static final LatLng ADELAIDE = new LatLng(-34.92873, 138.59995);
        private static final LatLng PERTH = new LatLng(-31.952854, 115.857342);
        private View mMarker;

//        @Inject
//        protected Bus BUS;


        /** Demonstrates customizing the info window and/or its contents. */
        class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

            // These a both viewgroups containing an ImageView with id "badge" and two TextViews with id
            // "title" and "snippet".

            CustomInfoWindowAdapter() {
            }

            @Override
            public View getInfoWindow(Marker marker) {

                render(marker, mMarker);
                return mMarker;
            }

            @Override
            public View getInfoContents(Marker marker) {
                render(marker, mMarker);
                return mMarker;
            }

            private void render(Marker marker, View view) {
                int badge;
//                // Use the equals() method on a Marker to check for equals.  Do not use ==.
//                if (marker.equals(mBrisbane)) {
//                    badge = R.drawable.badge_qld;
//                } else if (marker.equals(mAdelaide)) {
//                    badge = R.drawable.badge_sa;
//                } else if (marker.equals(mSydney)) {
//                    badge = R.drawable.badge_nsw;
//                } else if (marker.equals(mMelbourne)) {
//                    badge = R.drawable.badge_victoria;
//                } else if (marker.equals(mPerth)) {
//                    badge = R.drawable.badge_wa;
//                } else {
//                    // Passing 0 to setImageResource will clear the image view.
//                    badge = 0;
//                }
//
//
//                ((ImageView) view.findViewById(R.id.badge)).setImageResource(badge);
//
                String title = marker.getTitle();
                //marker.get
                TextView titleUi = ((TextView) view.findViewById(R.id.line_number_tv));
                if (title != null) {
                    // Spannable string allows us to edit the formatting of the text.
                    SpannableString titleText = new SpannableString(title);
                    titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
                    titleUi.setText(titleText);
                } else {
                    titleUi.setText("");
                }

                String snippet = marker.getSnippet();
                TextView snippetUi = ((TextView) view.findViewById(R.id.time_ago_tv));
                if (snippet != null ) {
                    SpannableString snippetText = new SpannableString(snippet);
                    snippetText.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, 10, 0);
                    snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 12, snippet.length(), 0);
                    snippetUi.setText(snippetText);
                } else {
                    snippetUi.setText("");
                }
            }
        }

        private GoogleMap mMap;

        private Marker mPerth;
        private Marker mSydney;
        private Marker mBrisbane;
        private Marker mAdelaide;
        private Marker mMelbourne;

        @Override
        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            mMarker = layoutInflater.inflate(R.layout.map_info_window, viewGroup, false);
            View view = super.onCreateView(layoutInflater, viewGroup, bundle);    //To change body of overridden methods use File | Settings | File Templates.

            return view;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

        @Override
        public void onPause() {
            super.onPause();
            //BUS.unregister(this);
        }

        @Override
        public void onResume() {
            super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
            //BUS.register(this);
            setUpMap();
            ParseUser.enableAutomaticUser();
            DateTime dateTime = new DateTime();
            dateTime = dateTime.minus(Days.TWO);


            ParseQuery query = new ParseQuery("Photo");
//            query.whereWithinKilometers("location", new ParseGeoPoint(52.2285345, 20.9747943), 10.0);
//            query.orderByDescending("createdAt");
//            query.whereGreaterThanOrEqualTo("createdAt", dateTime.toDate());

            //query.setLimit(10);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    addMarkersToMap(parseObjects);
                }

//                @Override
//                public void done(List list, ParseException e) {
//
//                }
            });
        }

        private void setUpMap() {
        mMap = getMap();
        // Hide the zoom controls as the button panel will cover it.
        mMap.getUiSettings().setZoomControlsEnabled(false);
            //mapView.getUiSettings().setMyLocationButtonEnabled(false);

            mMap.setMyLocationEnabled(true);

        // Add lots of markers to the map.
        //addMarkersToMap();

        // Setting an info window adapter allows us to change the both the contents and look of the
        // info window.
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

        // Set listeners for marker events.  See the bottom of this class for their behavior.
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerDragListener(this);

        // Pan to see all markers in view.
        // Cannot zoom to bounds until the map has a size.
        final View mapView = getView();
        if (mapView.getViewTreeObserver().isAlive()) {
            mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation") // We use the new method when supported
                @SuppressLint("NewApi") // We check which build version we are using.
                @Override
                public void onGlobalLayout() {
                    LatLngBounds bounds = new LatLngBounds.Builder()
                            .include(PERTH)
                            .include(SYDNEY)
                            .include(ADELAIDE)
                            .include(BRISBANE)
                            .include(MELBOURNE)
                            .build();
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    //mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                }
            });
        }
    }

    private void addMarkersToMap(List<ParseObject> parseObjects) {

        for(ParseObject object : parseObjects) {
//            ParseGeoPoint location = object.getParseGeoPoint("location");
//            mMap.addMarker(new MarkerOptions()
//                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
//                    .title(object.getString("number"))
//                    .snippet("" + object.getCreatedAt().toString())
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

            double latitude = object.getDouble("latitude") ;
            double longitude = object.getDouble("longitude") ;
            String description = object.getString("description");
            Log.d("TAG", "lat:" + latitude + " long:" + longitude + " desc:" + description);
            //ParseGeoPoint location = object.getParseGeoPoint("location");
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title(description)
                    .snippet("" + object.getCreatedAt().toString())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            // Instantiates a new CircleOptions object and defines the center and radius
            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(latitude, longitude))
                    .radius(1000); // In meters

// Get back the mutable Circle
            Circle circle = mMap.addCircle(circleOptions);
        }


//        // Uses a colored icon.
//        mBrisbane = mMap.addMarker(new MarkerOptions()
//                .position(BRISBANE)
//                .title("Brisbane")
//                .snippet("Population: 2,074,200")
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
//
//        // Uses a custom icon.
//        mSydney = mMap.addMarker(new MarkerOptions()
//                .position(SYDNEY)
//                .title("Sydney")
//                .snippet("Population: 4,627,300")
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow)));
//
//        // Creates a draggable marker. Long press to drag.
//        mMelbourne = mMap.addMarker(new MarkerOptions()
//                .position(MELBOURNE)
//                .title("Melbourne")
//                .snippet("Population: 4,137,400")
//                .draggable(true));
//
//        // A few more markers for good measure.
//        mPerth = mMap.addMarker(new MarkerOptions()
//                .position(PERTH)
//                .title("Perth")
//                .snippet("Population: 1,738,800"));
//        mAdelaide = mMap.addMarker(new MarkerOptions()
//                .position(ADELAIDE)
//                .title("Adelaide")
//                .snippet("Population: 1,213,000"));
//
//        // Creates a marker rainbow demonstrating how to create default marker icons of different
//        // hues (colors).
//        int numMarkersInRainbow = 12;
//        for (int i = 0; i < numMarkersInRainbow; i++) {
//            mMap.addMarker(new MarkerOptions()
//                    .position(new LatLng(
//                            -30 + 10 * Math.sin(i * Math.PI / (numMarkersInRainbow - 1)),
//                            135 - 10 * Math.cos(i * Math.PI / (numMarkersInRainbow - 1))))
//                    .title("Marker " + i)
//                    .icon(BitmapDescriptorFactory.defaultMarker(i * 360 / numMarkersInRainbow)));
//        }
    }

    private boolean checkReady() {
        if (mMap == null) {
            //Toast.makeText(this, "not ready", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /** Called when the Clear button is clicked. */
    public void onClearMap(View view) {
        if (!checkReady()) {
            return;
        }
        mMap.clear();
    }

    /** Called when the Reset button is clicked. */
    public void onResetMap(View view) {
        if (!checkReady()) {
            return;
        }
        // Clear the map because we don't want duplicates of the markers.
        mMap.clear();
        //addMarkersToMap();
    }

    //
    // Marker related listeners.
    //

    @Override
    public boolean onMarkerClick(final Marker marker) {
        // This causes the marker at Perth to bounce into position when it is clicked.
        if (marker.equals(mPerth)) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            Projection proj = mMap.getProjection();
            Point startPoint = proj.toScreenLocation(PERTH);
            startPoint.offset(0, -100);
            final LatLng startLatLng = proj.fromScreenLocation(startPoint);
            final long duration = 1500;

            final Interpolator interpolator = new BounceInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);
                    double lng = t * PERTH.longitude + (1 - t) * startLatLng.longitude;
                    double lat = t * PERTH.latitude + (1 - t) * startLatLng.latitude;
                    marker.setPosition(new LatLng(lat, lng));

                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    }
                }
            });
        }
        // We return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //Toast.makeText(getBaseContext(), "Click Info Window", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }


}