package com.example.hp.countries;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class countryLoader extends AsyncTaskLoader<List<Country>> {

    /** Tag for log messages */
    private static final String LOG_TAG = countryLoader.class.getName();

    /** Query URL */
    private String mUrl;

    /**
     * Constructs a new {@link countryLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public countryLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<Country> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of Countries.
        List<Country> countries = QueryUtils.fetchCountryData(mUrl);
        return countries;
    }
}
