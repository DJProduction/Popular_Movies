package com.example.johnsond.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    // Adapter used in onCreatveView to capture files from themoviesdb.org and
    // add to a xml fragment_main gridViewMovie
    private MovieImageAdapter movieImgAdapter;

    public MainActivityFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //List of movieItems from FetchMovies() method that will be implemented into the movieImgAdapter
        List<MovieItem> moviesList = new ArrayList<MovieItem>();

        // movieImgAdapter will receive movies from movieList and apply them to the GridView
        movieImgAdapter = new MovieImageAdapter(getActivity(), moviesList);
        final GridView gridview = (GridView) rootView.findViewById(R.id.gridViewMovies);
        gridview.setAdapter(movieImgAdapter);

        // movieItem on GridView is selected the information related to that movie is sent via Intent
        // Intent opens movieDetailActivity and information is extracted in movieDetailFragment
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                MovieItem detailedMovie = movieImgAdapter.getItem(position);

                Intent intent = new Intent(getActivity(), MovieDetailActivity.class)
                        .putExtra("origional_title", detailedMovie.getOriginalTitle())
                        .putExtra("movie_image", detailedMovie.getMovieImage())
                        .putExtra("release_date", detailedMovie.getReleaseDate())
                        .putExtra("vote_average", detailedMovie.getVoteAverage())
                        .putExtra("vote_count", detailedMovie.getVoteCount())
                        .putExtra("overview", detailedMovie.getOverView());
                startActivity(intent);
            }
        });
        return rootView;

    }

    // Fragment starts initiate update method
    public void onStart() {
        super.onStart();
        updateMovieList();
    }

    //FetchMovies task is started and the sorting preference chosen from Settings Activity is passed into the method
    private void updateMovieList() {
        FetchMovies asyncGetMovies = new FetchMovies();
        SharedPreferences sort= PreferenceManager.getDefaultSharedPreferences(getActivity());
        String pref_movieSort = sort.getString(getString(R.string.pref_movieSort_key),
                getString(R.string.pref_movieSort_popular_movies));
        asyncGetMovies.execute(pref_movieSort);
    }

    public class FetchMovies extends AsyncTask<String, Void, List<MovieItem>> {

        private final String LOG_TAG = FetchMovies.class.getSimpleName();

        /**
         * Take the String representing the resulting movies from the query in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Each movie has its information parsed and entered into the movieList
         */

        private List<MovieItem> getMovieDataFromJson(String moviesJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_RESULTS = "results";
            final String OWM_ORIGIONAL_TITLE = "original_title";
            final String OWM_MOVIE_IMAGE = "poster_path";
            final String OWM_OVERVIEW = "overview";
            final String OWM_VOTE_AVERAGE = "vote_average";
            final String OWN_VOTE_COUNT = "vote_count";
            final String OWN_RELEASE_DATE = "release_date";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(OWM_RESULTS);

            List<MovieItem> movieList = new ArrayList<MovieItem>();

            for(int i=0; i < moviesArray.length(); i++) {
                JSONObject movieSpecific = moviesArray.getJSONObject(i);
                MovieItem movie = new MovieItem();

                movie.setOriginalTitle(movieSpecific.getString(OWM_ORIGIONAL_TITLE));
                movie.setMovieImage("http://image.tmdb.org/t/p/w300"+movieSpecific.getString(OWM_MOVIE_IMAGE));
                movie.setOverView(movieSpecific.getString(OWM_OVERVIEW));
                movie.setVoteCount(movieSpecific.getString(OWN_VOTE_COUNT));
                movie.setVoteAverage(movieSpecific.getString(OWM_VOTE_AVERAGE));
                movie.setReleaseDate(movieSpecific.getString(OWN_RELEASE_DATE));

                movieList.add(movie);

            }

            return movieList;

        }

        // Helps discover which sorting query is being requested by user preference
        // Prefs is based on which value user selected in settings menu "popular movies" or "highest rated"
        // Uri query is built based on the prefs value selected
        // Default uri sorting query is "popular movies"
        private Uri buildBasedOnPreferences (String prefs) {
            Uri buildUri;
            if(prefs == getString(R.string.pref_movieSort_popular_movies)) {
                final String MOVIE_QUERY_BASE_URL =
                        "https://api.themoviedb.org/3";
                final String DISCOVER_PATH = "discover";
                final String MOVIE_PATH = "movie";
                final String SORT_PARAM = "sort_by";
                final String API_KEY_PARAM = "api_key";

                String api_key = getString(R.string.api_Key);
                String sort = "popularity.desc";

               Uri builtUri = Uri.parse(MOVIE_QUERY_BASE_URL).buildUpon()
                        .appendPath(DISCOVER_PATH)
                        .appendPath(MOVIE_PATH)
                        .appendQueryParameter(SORT_PARAM, sort)
                        .appendQueryParameter(API_KEY_PARAM, api_key)
                        .build();
                return builtUri;
            }
            else if(prefs == getString(R.string.pref_movieSort_highest_rated)) {
                final String MOVIE_QUERY_BASE_URL =
                        "https://api.themoviedb.org/3";
                final String DISCOVER_PATH = "discover";
                final String MOVIE_PATH = "movie";
                final String SORT_PARAM = "sort_by";
                final String API_KEY_PARAM = "api_key";

                String api_key = getString(R.string.api_Key);
                String sort = "vote_average.desc";

                Uri builtUri = Uri.parse(MOVIE_QUERY_BASE_URL).buildUpon()
                        .appendPath(DISCOVER_PATH)
                        .appendPath(MOVIE_PATH)
                        .appendQueryParameter(SORT_PARAM, sort)
                        .appendQueryParameter(API_KEY_PARAM, api_key)
                        .build();
                return builtUri;
            }

            else {
                buildUri = Uri.parse(getString(R.string.default_uri_build)).buildUpon().build();
                return buildUri;
            }
        }

        protected List<MovieItem> doInBackground(String...params) {
// These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

// Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {

                Uri buildUri  = buildBasedOnPreferences(params[0]);

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
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
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

            try {
                return getMovieDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;

        }

        // The parsed movies are copied from a passed movie list to the adapter
        @Override
        protected void onPostExecute(List<MovieItem> result) {
            if (result != null) {
                movieImgAdapter.clear();
                for( int i=0; i<result.size(); i++) {
                    movieImgAdapter.add(result.get(i));
                }
            }
        }

        }



    }