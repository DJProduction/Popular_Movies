package com.example.johnsond.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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

public class MovieReviewsActivityFragment extends Fragment {

    private static final String LOG_TAG = MovieReviewsActivityFragment.class.getSimpleName();
    //List of movieItems from FetchMovies() method that will be implemented into the movieImgAdapter
    ArrayList<MovieReviews> savedListOfMovieReviews = new ArrayList<MovieReviews>();
    // Group of references to the values used in each review.
    private MovieReviewsAdapter movieRevAdapter;
    private String id;
    private String originalTitle;
    private String movieImage;

    public MovieReviewsActivityFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isNetworkAvailable()) {
            updateReviewList();
        } else {
            Toast connectStatus = new Toast(getActivity());
            connectStatus.setText("Not connected to the network");
            Log.e("NetworkCheck", "Device not connected to internet");
        }
    }

    // Saves the array of reviews just in case for orientation change
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("REVIEW_KEY", savedListOfMovieReviews);
    }

    //Based on a stackoverflow snippet
    //Checks network is available in onStart() before FetchMovies is even initiated
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void updateReviewList() {
        FetchMovieReviews asyncReviewsMovies = new FetchMovieReviews();
        asyncReviewsMovies.execute(id);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_movie_reviews, container, false);

        // recovers current list state when activity has ended
        if (savedInstanceState != null) {
            savedListOfMovieReviews = (ArrayList<MovieReviews>) savedInstanceState.get("REVIEW_KEY");
        }

        // Intent package sent from mainActivityFragment is received
        // All movieItem variables are extracted
        // Extracted variables are linked to display on Fragment Movie Detail
        Intent receiveMovie = getActivity().getIntent();
        if (receiveMovie != null) {
            id = receiveMovie.getStringExtra("id");
            originalTitle = receiveMovie.getStringExtra("origional_title");
            movieImage = receiveMovie.getStringExtra("movie_image");

            TextView movieTitleView = (TextView) rootView.findViewById(R.id.movie_review_origional_title_view);
            movieTitleView.setText(originalTitle);

            // Picasso api is used to load image efficiently
            ImageView movieImageView = (ImageView) rootView.findViewById(R.id.movie_review_imageView);
            Picasso.with(getActivity())
                    .load(movieImage)
                    .resize(300, 500)
                    .centerCrop()
                    .into(movieImageView);

            // movieReviewsAdapter will receive movies from movieList and apply them to the GridView
            movieRevAdapter = new MovieReviewsAdapter(getActivity(), savedListOfMovieReviews);
            final ListView reviewsList = (ListView) rootView.findViewById(R.id.movie_reviews_listview);
            reviewsList.setAdapter(movieRevAdapter);
        }

        return rootView;
    }


    public class FetchMovieReviews extends AsyncTask<String, Void, List<MovieReviews>> {
        private final String LOG_TAG = FetchMovieReviews.class.getSimpleName();

        private List<MovieReviews> getMovieReviewsDataFromJson(String movieReviewsJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_RESULTS = "results";
            final String OWN_AUTHOR = "author";
            final String OWN_CONTENT = "content";

            JSONObject movieReviewsJson = new JSONObject(movieReviewsJsonStr);
            JSONArray movieReviewsArray = movieReviewsJson.getJSONArray(OWM_RESULTS);
            List<MovieReviews> movieReviewList = new ArrayList<MovieReviews>();

            for (int i = 0; i < movieReviewsArray.length(); i++) {
                JSONObject movieReviews = movieReviewsArray.getJSONObject(i);
                MovieReviews movieReview = new MovieReviews();
                movieReview.setAuthor(movieReviews.getString(OWN_AUTHOR));
                movieReview.setReview(movieReviews.getString(OWN_CONTENT));
                movieReviewList.add(movieReview);
            }

            return movieReviewList;

        }

        public Uri buildMovieReviewsUri(String movieId) {
            final String MOVIE_QUERY_BASE_URL =
                    "https://api.themoviedb.org/3";
            final String MOVIE_PATH = "movie";
            final String REVIEW_PATH = "reviews";
            final String API_KEY_PARAM = "api_key";

            String api_key = getString(R.string.api_Key);

            Uri builtUri = Uri.parse(MOVIE_QUERY_BASE_URL).buildUpon()
                    .appendPath(MOVIE_PATH)
                    .appendPath(movieId)
                    .appendPath(REVIEW_PATH)
                    .appendQueryParameter(API_KEY_PARAM, api_key)
                    .build();
            return builtUri;
        }

        protected List<MovieReviews> doInBackground(String... params) {
// These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

// Will contain the raw JSON response as a string.
            String movieReviewJsonStr = null;

            try {

                Uri buildUri = buildMovieReviewsUri(params[0]);

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
                movieReviewJsonStr = buffer.toString();
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
                return getMovieReviewsDataFromJson(movieReviewJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;

        }

        // The parsed movies are copied from a passed movie list to the adapter
        @Override
        protected void onPostExecute(List<MovieReviews> result) {
            if (result != null) {
                movieRevAdapter.clear();
                for (int i = 0; i < result.size(); i++) {
                    movieRevAdapter.add(result.get(i));
                }
            }
        }
    }

}
