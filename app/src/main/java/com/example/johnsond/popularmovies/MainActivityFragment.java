package com.example.johnsond.popularmovies;

import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import junit.framework.Assert;

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
import java.util.Arrays;
import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.Language;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.MovieImages;
import info.movito.themoviedbapi.model.MovieList;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        MovieItem[] mThumbIds = {
                new MovieItem(R.drawable.sample_0),
                new MovieItem(R.drawable.sample_1),
                new MovieItem(R.drawable.sample_2),
                new MovieItem(R.drawable.sample_3)
        };

        List<MovieItem> test = new ArrayList<MovieItem>(Arrays.asList(mThumbIds));
        movieImgAdapter = new MovieImageAdapter(getActivity(), test);
        final GridView gridview = (GridView) rootView.findViewById(R.id.gridViewMovies);
        gridview.setAdapter(movieImgAdapter);


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getActivity(), "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;

    }

    public class FetchMovies extends AsyncTask<String, Void, List<MovieItem>> {

        List<MovieItem> moviesList= new ArrayList<>();

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getMovieDataFromJson(String moviesJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_ORIGIONAL_TITLE = "original_title";
            final String OWM_MOVIE_IMAGE = "poster_path";
            final String OWM_OVERVIEW = "overview";
            final String OWM_VOTE_AVERAGE = "vote_average";
            final String OWN_VOTE_COUNT = "vote_count";
            final String OWN_RELEASE_DATE = "release_date";

            JSONObject forecastJson = new JSONObject(moviesJsonStr);
            JSONArray movieArray = forecastJson.getJSONArray(OWM_LIST);

            return null;

        }

        protected MovieItem[] doInBackground(String... params) {

            TmdbApi tmdb = new TmdbApi(Integer.toString(R.string.api_Key));
            List<MovieDb> moviesListExtracted = tmdb.getMovies().getPopularMovieList("en",0).getResults();
            //check.assertTrue("No popular movies found", !moviesList.isEmpty());
            for( int i=0; i<moviesListExtracted.size(); i++) {
                moviesList.get(i).setOriginalTitle(moviesListExtracted.get(2).getOriginalTitle());
            }


            }
        }



    }


/*// These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
HttpURLConnection urlConnection = null;
BufferedReader reader = null;

// Will contain the raw JSON response as a string.
String moviesJsonStr = null;

String api_key = getString(R.string.api_Key);
String sort = "popularity.desc";

try {
// Construct the URL for the OpenWeatherMap query
// Possible parameters are avaiable at OWM's forecast API page, at
// http://openweathermap.org/API#forecast
final String MOVIE_QUERY_BASE_URL =
        "https://api.themoviedb.org/3/movie/550?api_key="+ api_key +
        "/discover/movie";
final String SORT_PARAM = "sort_by";

        Uri builtUri = Uri.parse(MOVIE_QUERY_BASE_URL).buildUpon()
        .appendQueryParameter(SORT_PARAM, sort)
        .build();

        URL url = new URL(builtUri.toString());

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
        forecastJsonStr = buffer.toString();
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
        }*/

