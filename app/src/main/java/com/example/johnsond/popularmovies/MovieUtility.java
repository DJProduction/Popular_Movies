package com.example.johnsond.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by JohnsonD on 6/27/16.
 */
final public class MovieUtility {

    private MovieUtility(){}

    // Used in the MainActivity to keep track of the preferred sort order by user
    // Helps to have an easy accessible sort value to update the MainActivityFragment's movie gridview
    public static String getPreferredSort(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_movieSort_key),
                context.getString(R.string.pref_movieSort_popular_movies));
    }
}
