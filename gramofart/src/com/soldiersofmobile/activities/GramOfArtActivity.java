package com.soldiersofmobile.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;
import com.soldiersofmobile.R;
import com.soldiersofmobile.fragments.CustomMapFragment;
import com.soldiersofmobile.fragments.ListOfArtFragment;
import com.viewpagerindicator.TitlePageIndicator;

public class GramOfArtActivity extends BaseActivity {

    private static final String LIST_FRAGMENT_TAG = "list_fragment_tag";
    private ParseUser mCurrentUser;
    private ViewPager mPager;
    private TitlePageIndicator mIndicator;
    private TournamentsListPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParseAnalytics.trackAppOpened(getIntent());

        mCurrentUser = ParseUser.getCurrentUser();
        if (mCurrentUser == null) {

            LoginActivity.startLogInActivity(this);
            finish();
        }

        setContentView(R.layout.gram_of_art_activity);

        mAdapter = new TournamentsListPagerAdapter(getSupportFragmentManager());

        mPager = (ViewPager) findViewById(R.id.main_vp);
        mPager.setAdapter(mAdapter);
        mIndicator = (TitlePageIndicator) findViewById(R.id.main_tpi);
        mIndicator.setViewPager(mPager);
    }

    class TournamentsListPagerAdapter extends FragmentStatePagerAdapter {

        private final String[] TITLES = new String[]{"timeline", "map"};
        private Fragment[] mListFragments = new Fragment[2];


        public TournamentsListPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(final int i) {
            if (mListFragments[i] == null) {
                if (i == 0) {
                    mListFragments[i] = new ListOfArtFragment();
                } else {
                    mListFragments[i] = new CustomMapFragment();
                }
                //Bundle arguments = new Bundle();
                //arguments.putInt(Constants.TOURNAMENT_TYPE_BUNDLE_KEY, i + 1);
                //mListFragments[i].setArguments(arguments);
            }

            return mListFragments[i];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

    }

    public static void startGramOfArtActivity(Context context) {
        Intent intent = new Intent(context, GramOfArtActivity.class);
        context.startActivity(intent);
    }


}
