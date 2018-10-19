package com.example.hp.countries;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Country>> {

    private static final int INITIAL_LOADER_ID = 1;

    /** URL used to fetch all countries (name,capital,flag,sub region) */
    private static final String INITIAL_REQUEST_URL = "https://restcountries.eu/rest/v2?fields=name;capital;flag;subregion";

    LinearLayout noConnectionView;
    ProgressBar loadingIndicator;
    RecyclerView countriesRecyclerView;
    countriesRecyclerViewAdapter countriesRecyclerViewAdapter;
    ArrayList<Country>countriesList;
    FloatingActionButton logout_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // References to UI elements.
        loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        countriesRecyclerView = (RecyclerView) findViewById(R.id.countriesList);
        noConnectionView = (LinearLayout) findViewById(R.id.noConnectionView);
        logout_btn = (FloatingActionButton) findViewById(R.id.logout_btn);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);


        // Setup the list view with an empty adapter until the data is fetched
        countriesRecyclerViewAdapter = new countriesRecyclerViewAdapter(new ArrayList<Country>(), getApplicationContext(),Home.this);
        countriesRecyclerView.setLayoutManager(layoutManager);
        countriesRecyclerView.setAdapter(countriesRecyclerViewAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();

                // Set the logged in boolean in shared preferences to false so that when user reopens the app
                // it opens to the login screen
                SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putBoolean("logged_in", false);
                editor.commit();
                startActivity(new Intent(getApplicationContext(),Login_signup_tabs.class));
                finish();
            }
        });
        init_loader();

    }

    private void init_loader()
    {
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(INITIAL_LOADER_ID, null, this);
    }

    @Override
    public Loader<List<Country>> onCreateLoader(int i, Bundle bundle) {
        return new countryLoader(this, INITIAL_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Country>> loader, List<Country> Countries) {
        loadingIndicator.setVisibility(View.GONE);
        if (Countries==null)
        {
            countriesRecyclerView.setVisibility(View.GONE);
            noConnectionView.setVisibility(View.VISIBLE);
        }

        else
        {
            countriesRecyclerView.setVisibility(View.VISIBLE);
            noConnectionView.setVisibility(View.GONE);
        }
        // Clear the adapter of previous countries data and load new data
        countriesRecyclerViewAdapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (Countries != null && !Countries.isEmpty()) {
            countriesRecyclerViewAdapter.addAll(Countries);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Country>> loader) {
        // Loader reset, so we can clear out our existing data.
        countriesRecyclerViewAdapter.clear();
    }
}
