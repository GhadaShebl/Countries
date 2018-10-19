package com.example.hp.countries;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Login_signup_tabs extends AppCompatActivity
{
    private Login_signup_tabs.SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    TextView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup_tabs);

        // Check if user is logged in, finish this activity and redirect user to home screen
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean logged_in = mSharedPreferences.getBoolean("logged_in",false);
        if (logged_in)
        {
            startActivity(new Intent(getApplicationContext(),Home.class));
            finish();
        }
        mSectionsPagerAdapter = new Login_signup_tabs.SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        // Change logo typeface
        logo = (TextView) findViewById(R.id.logo);
        Typeface coolvetica = Typeface.createFromAsset(getAssets(), "fonts/coolvetica.ttf");
        logo.setTypeface(coolvetica);

        // to stop keyboard from opening automatically
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    public static class PlaceholderFragment extends Fragment
    {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {}

        public static Login_signup_tabs.PlaceholderFragment newInstance(int sectionNumber)
        {
            Login_signup_tabs.PlaceholderFragment fragment = new Login_signup_tabs.PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_login, container, false);
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
            {
                Login tab = new Login();
                return tab;
            }
            if (position == 1) {
                Signup tab = new Signup();
                return tab;
            }

            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}
