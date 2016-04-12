package com.example.johnsond.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

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

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    private String id;
    private  String originalTitle;
    private  String movieImage;
    private  String overView;
    private  String voteAverage;
    private  String voteCount;
    private  String releaseDate;
    private String trailer;

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        // Intent package sent from mainActivityFragment is received
        // All movieItem variables are extracted
        // Extracted variables are linked to display on Fragment Movie Detail
        Intent receiveMovie = getActivity().getIntent();
        if (receiveMovie != null)
        {
            id = receiveMovie.getStringExtra("id");
            originalTitle = receiveMovie.getStringExtra("origional_title");
            movieImage = receiveMovie.getStringExtra("movie_image");
            releaseDate = receiveMovie.getStringExtra("release_date");
            voteAverage = receiveMovie.getStringExtra("vote_average");
            voteCount = receiveMovie.getStringExtra("vote_count");
            overView = receiveMovie.getStringExtra("overview");

            TextView movieTitleView = (TextView) rootView.findViewById(R.id.movie_detail_origional_title_view);
            movieTitleView.setText(originalTitle);

            // Picasso api is used to load image efficiently
            ImageView movieImageView = (ImageView) rootView.findViewById(R.id.movie_detail_imageView);
            Picasso.with(getActivity())
                    .load(movieImage)
                    .resize(300,500)
                    .centerCrop()
                    .into(movieImageView);

            TextView movieReleaseDateView = (TextView) rootView.findViewById(R.id.movie_detail_release_date);
            movieReleaseDateView.setText(releaseDate);
            RatingBar movieRatingBarView = (RatingBar) rootView.findViewById(R.id.movie_detail_ratingBar);

            // RatingBar widget looks better for users with 5 stars
            // theMovieDB rates movies with 10 being maximum
            // So each rating had to be divided by 2
            // Float is used to incorporate half stars
            float voteAverage_float=Float.parseFloat(voteAverage);
            movieRatingBarView.setRating(voteAverage_float / 2);

            TextView movieVoteCountView = (TextView) rootView.findViewById(R.id.movie_detail_vote_count);
            movieVoteCountView.setText(voteCount + " (votes)");
            TextView movieOverviewView = (TextView) rootView.findViewById(R.id.movie_detail_overview);
            movieOverviewView.setText(overView);

            //Selecting trailer button
            Button movieTrailerButton = (Button) rootView.findViewById(R.id.trailer_button_detail_imageView);
            movieTrailerButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    FetchMovieTrailer asynctaskTrailer = new FetchMovieTrailer();
                    asynctaskTrailer.execute(id);
                }
            });

            //Selecting reviews button
            Button movieReviewsButton = (Button) rootView.findViewById(R.id.reviews_button_detail_imageView);
            movieReviewsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), MovieReviewsActivity.class)
                            .putExtra("id", id)
                            .putExtra("origional_title", originalTitle)
                            .putExtra("movie_image", movieImage);
                    startActivity(intent);
                }
            });
        }



        return rootView;
    }

    public class FetchMovieTrailer extends AsyncTask<String, Void, Uri> {

        private final String LOG_TAG = FetchMovieTrailer.class.getSimpleName();

        private Uri getYoutubeTrailerDataFromJson(String movieVideoJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_RESULTS = "results";
            final String OWN_KEY = "key";

            JSONObject movieVideoJson = new JSONObject(movieVideoJsonStr);
            JSONArray movieVideoDetailsArray = movieVideoJson.getJSONArray(OWM_RESULTS);
            Uri buildYouTubeUri = null;

            for (int i = 0; i < movieVideoDetailsArray.length(); i++) {
                JSONObject movieVideoDetails = movieVideoDetailsArray.getJSONObject(i);
                String youTubeDefaultUri = getString(R.string.default_uri_youtube);
                buildYouTubeUri = Uri.parse(youTubeDefaultUri + movieVideoDetails.getString(OWN_KEY));
            }


            return buildYouTubeUri;

        }

        public Uri buildMovieTrailerUri(String movieId) {
            final String MOVIE_QUERY_BASE_URL =
                    "https://api.themoviedb.org/3";
            final String MOVIE_PATH = "movie";
            final String VIDEO_PATH = "videos";
            final String API_KEY_PARAM = "api_key";

            String api_key = getString(R.string.api_Key);

            Uri builtUri = Uri.parse(MOVIE_QUERY_BASE_URL).buildUpon()
                    .appendPath(MOVIE_PATH)
                    .appendPath(movieId)
                    .appendPath(VIDEO_PATH)
                    .appendQueryParameter(API_KEY_PARAM, api_key)
                    .build();
            return builtUri;
        }


        protected Uri doInBackground(String... params) {
// These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

// Will contain the raw JSON response as a string.
            String movieVideoJsonStr = null;

            try {

                Uri buildUri = buildMovieTrailerUri(params[0]);

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
                movieVideoJsonStr = buffer.toString();
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

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(getYoutubeTrailerDataFromJson(movieVideoJsonStr));
                // Always use string resources for UI text.
                // This says something like "Share this photo with"
                String title = getResources().getString(R.string.chooser_trailer_title);
                // Create intent to show the chooser dialog
                Intent chooser = Intent.createChooser(intent, title);
                startActivity(chooser);

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;

        }

    }
}
