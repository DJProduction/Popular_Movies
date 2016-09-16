package com.example.johnsond.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.johnsond.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by JohnsonD on 4/15/16.
 */
public class FetchMovies extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchMovies.class.getSimpleName();

    private final Context mContext;

    // When FetchMovies is created activity is passed to be utilize the contentResolver.
    public FetchMovies(Context context) {
        mContext = context;
    }

    /*
     * Take the String representing the resulting movies from the query in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     * <p/>
     * Each movie has its information parsed and entered into the movieList
     */

    private void getMovieDataFromJson(String moviesJsonStr, String prefs)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_RESULTS = "results";
        final String OWN_ID = "id";
        final String OWM_ORIGIONAL_TITLE = "original_title";
        final String OWM_MOVIE_IMAGE = "poster_path";
        final String OWM_OVERVIEW = "overview";
        final String OWM_VOTE_AVERAGE = "vote_average";
        final String OWN_VOTE_COUNT = "vote_count";
        final String OWN_RELEASE_DATE = "release_date";

        try {
            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(OWM_RESULTS);

            // Vector was created with size of the movieArray length of items
            Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesArray.length());

            // Insert the new movie information into the database
            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieSpecific = moviesArray.getJSONObject(i);
                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieContract.MoviesEntry.COLUMN_MOVIE_ID,
                        movieSpecific.getInt(OWN_ID));
                movieValues.put(MovieContract.MoviesEntry.COLUMN_MOVIE_ORIGIONAL_TITLE,
                        movieSpecific.getString(OWM_ORIGIONAL_TITLE));
                movieValues.put(MovieContract.MoviesEntry.COLUMN_MOVIE_IMAGE,
                        "http://image.tmdb.org/t/p/w300" + movieSpecific.getString(OWM_MOVIE_IMAGE));
                movieValues.put(MovieContract.MoviesEntry.COLUMN_MOVIE_OVERVIEW,
                        movieSpecific.getString(OWM_OVERVIEW));
                movieValues.put(MovieContract.MoviesEntry.COLUMN_MOVIE_VOTE_COUNT,
                        movieSpecific.getInt(OWN_VOTE_COUNT));
                movieValues.put(MovieContract.MoviesEntry.COLUMN_MOVIE_VOTE_AVERAGE,
                        movieSpecific.getString(OWM_VOTE_AVERAGE));
                movieValues.put(MovieContract.MoviesEntry.COLUMN_MOVIE_RELEASE_DATE,
                        movieSpecific.getString(OWN_RELEASE_DATE));
                //Initial sorting preference for movies depending on settings selection
                movieValues.put(MovieContract.MoviesEntry.COLUMN_MOVIE_PREFERRED_SORT,
                        prefs);
                // Contains boolean value for user to sort favorite movies.
                // Default value is "False"
                movieValues.put(MovieContract.MoviesEntry.COLUMN_MOVIE_FAVORITE, "False");

                cVVector.add(movieValues);
            }
            //Using bulk insert to put movies in the MovieDB via MovieContentProvider
            int inserted = 0;
            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                // Use mContext to get the ContentResolver  for the database
                // so that the bulkinsert function can be used/
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.MoviesEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchWeatherTask Complete. " + inserted + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }


    // Helps discover which sorting query is being requested by user preference
    // Prefs is based on which value user selected in settings menu "popular movies" or "highest rated"
    // Uri query is built based on the prefs value selected
    // Default uri sorting query is "popular movies"
    private Uri buildBasedOnPreferences(String prefs) {
        Uri buildUri;
        //Resources.getSystem().getString(R.string.pref_movieSort_popular_movies)
        if (prefs == "popular movies") {
            final String MOVIE_QUERY_BASE_URL =
                    "https://api.themoviedb.org/3";
            final String DISCOVER_PATH = "discover";
            final String MOVIE_PATH = "movie";
            final String SORT_PARAM = "sort_by";
            final String API_KEY_PARAM = "api_key";

            //Resources.getSystem().getString(R.string.api_Key)
            String api_key = "355114c318820d5787910ded3fe5a939";
            String sort = "popularity.desc";
            Uri builtUri = Uri.parse(MOVIE_QUERY_BASE_URL).buildUpon()
                    .appendPath(DISCOVER_PATH)
                    .appendPath(MOVIE_PATH)
                    .appendQueryParameter(SORT_PARAM, sort)
                    .appendQueryParameter(API_KEY_PARAM, api_key)
                    .build();
            return builtUri;
        }
        //Resources.getSystem().getString(R.string.pref_movieSort_highest_rated)
        else if (prefs == "highest rated") {
            final String MOVIE_QUERY_BASE_URL =
                    "https://api.themoviedb.org/3";
            final String DISCOVER_PATH = "discover";
            final String MOVIE_PATH = "movie";
            final String SORT_PARAM = "sort_by";
            final String API_KEY_PARAM = "api_key";

            //Resources.getSystem().getString(R.string.api_Key);
            String api_key = "355114c318820d5787910ded3fe5a939";
            String sort = "vote_average.desc";

            Uri builtUri = Uri.parse(MOVIE_QUERY_BASE_URL).buildUpon()
                    .appendPath(DISCOVER_PATH)
                    .appendPath(MOVIE_PATH)
                    .appendQueryParameter(SORT_PARAM, sort)
                    .appendQueryParameter(API_KEY_PARAM, api_key)
                    .build();
            return builtUri;
        } else {
            //Resources.getSystem().getString(R.string.default_uri_build)
            String defaultUri = "https://api.themoviedb.org/3/discover/movie?" +
                    "sort_by=popularity.desc&api_key=355114c318820d5787910ded3fe5a939";
            buildUri = Uri.parse(defaultUri).buildUpon().build();
            return buildUri;
        }
    }

    protected Void doInBackground(String... params) {

// These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

// Will contain the raw JSON response as a string.
        String moviesJsonStr = null;


        try {

            Uri buildUri = buildBasedOnPreferences(params[0]);

            URL url = new URL(buildUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            moviesJsonStr = buffer.toString();
            getMovieDataFromJson(moviesJsonStr, params[0]);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }
}
