package com.example.johnsond.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.johnsond.popularmovies.data.MovieContract;
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
public class MovieDetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // Reference the fragment when utilized by FragmentManager
    static final String MOVIE_DETAIL_URI = "URI";
    // Specify the index columns the loader will use when adding values
    static final int COL_MOVIE_INDEX_ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_MOVIE_ORIGIONAL_TITLE = 2;
    static final int COL_MOVIE_IMAGE = 3;
    static final int COL_MOVIE_OVERVIEW = 4;
    static final int COL_MOVIE_VOTE_AVERAGE = 5;
    static final int COL_MOVIE_VOTE_COUNT = 6;
    static final int COL_MOVIE_RELEASE_DATE = 7;
    static final int COL_MOVIE_PREFERRED_SORT = 8;
    static final int COL_MOVIE_FAVORITE = 9;
    private static final String LOG_TAG = MovieDetailActivityFragment.class.getSimpleName();
    private static final int DETAIL_LOADER = 0;
    // Used  as projection values needed when the loader queries for details on movie
    private static final String[] MOVIE_DETAIL_COLUMNS = {
            MovieContract.MoviesEntry.TABLE_NAME + "." + MovieContract.MoviesEntry._ID,
            MovieContract.MoviesEntry.COLUMN_MOVIE_ID,
            MovieContract.MoviesEntry.COLUMN_MOVIE_ORIGIONAL_TITLE,
            MovieContract.MoviesEntry.COLUMN_MOVIE_IMAGE,
            MovieContract.MoviesEntry.COLUMN_MOVIE_OVERVIEW,
            MovieContract.MoviesEntry.COLUMN_MOVIE_VOTE_AVERAGE,
            MovieContract.MoviesEntry.COLUMN_MOVIE_VOTE_COUNT,
            MovieContract.MoviesEntry.COLUMN_MOVIE_RELEASE_DATE,
            MovieContract.MoviesEntry.COLUMN_MOVIE_PREFERRED_SORT,
            MovieContract.MoviesEntry.COLUMN_MOVIE_FAVORITE
    };
    private String movieFavoriteKey;
    // Receives parcelable data passed from MainActivity callback when user selects a movie
    private Uri mUri;
    // Reference to the favorite icon located in the action bar
    private MenuItem favoriteMenuItem;
    // Will hold the values when the loader is finished getting information on the selected movie
    private int id;
    private String originalTitle;
    private String movieImage;
    private String overView;
    private String voteAverage;
    private int voteCount;
    private String releaseDate;
    private String preferredSort;
    private String favorite;
    private String trailer;

    // Set of views that will reference the fragment_movie_detail layout
    private ImageView movieImageView;
    private TextView movieOrigionalTitleView;
    private TextView movieVoteCountView;
    private RatingBar movieRatingBarView;
    private TextView movieReleaseDateView;
    private TextView movieOverviewView;
    private Button movieTrailerButton;
    private Button movieReviewsButton;
    private ImageButton movieFavoriteButton;
    /*@BindView(R.id.movie_detail_origional_title_view) TextView movieOrigionalTitleView;
    @BindView(R.id.movie_detail_imageView) ImageView movieImageView;
    @BindView(R.id.movie_detail_vote_count) TextView movieVoteCountView;
    @BindView(R.id.movie_detail_ratingBar) RatingBar movieRatingBarView;
    @BindView(R.id.movie_detail_release_date) TextView movieReleaseDateView;
    @BindView(R.id.movie_detail_overview) TextView movieOverviewView;
    @BindView(R.id.trailer_button_detail_imageView) Button movieTrailerButton;
    @BindView(R.id.reviews_button_detail_imageView) Button movieReviewsButton;*/


    public MovieDetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    // movieFavoriteKey holds the favorite value upon fragment destruction
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(movieFavoriteKey, favorite);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Get uri from Main Activity where a user has selected a movie
        // insert uri into mUri for DetailFragment use
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(MovieDetailActivityFragment.MOVIE_DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        //ButterKnife.bind(this,rootView);

        //  Reference view created at top of class to layout objects by id (fragment_movie_detail)
        movieOrigionalTitleView = (TextView) rootView.findViewById(R.id.movie_detail_origional_title_view);
        movieImageView = (ImageView) rootView.findViewById(R.id.movie_detail_imageView);
        movieVoteCountView = (TextView) rootView.findViewById(R.id.movie_detail_vote_count);
        movieRatingBarView = (RatingBar) rootView.findViewById(R.id.movie_detail_ratingBar);
        movieReleaseDateView = (TextView) rootView.findViewById(R.id.movie_detail_release_date);
        movieOverviewView = (TextView) rootView.findViewById(R.id.movie_detail_overview);
        movieTrailerButton = (Button) rootView.findViewById(R.id.trailer_button_detail_imageView);
        movieReviewsButton = (Button) rootView.findViewById(R.id.reviews_button_detail_imageView);
        movieFavoriteButton = (ImageButton) rootView.findViewById(R.id.action_favorite);

        //Selecting trailer button
        movieTrailerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                FetchMovieTrailer asyncTaskTrailer = new FetchMovieTrailer();
                asyncTaskTrailer.execute(Integer.toString(id));
            }
        });

        //Selecting reviews button
        if(movieReviewsButton == null) {}
        movieReviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MovieReviewsActivity.class)
                        .putExtra("id", Integer.toString(id))
                        .putExtra("origional_title", originalTitle)
                        .putExtra("movie_image", movieImage);
                startActivity(intent);
            }
        });

        return rootView;
    }

    // When loader is finished this checks to see if the selected movie is a favorite of the user
    // Changes icon to correct icon depending on if favorite matches true or false
    public void setMovieFavoriteButton() {
        int favoriteIconDefault = R.drawable.ic_favorite_movie_default;
        int favoriteIconSelected = R.drawable.ic_favorite_movie_selected;

        if (null == favorite) {
            favorite = "False";
        }
        if (favoriteMenuItem == null) {
            return;
        }
        if (favorite.equals("False")) {
            favoriteMenuItem.setIcon(favoriteIconDefault);
        } else if (favorite.equals("True")) {
            favoriteMenuItem.setIcon(favoriteIconSelected);
        } else {
            Log.v(LOG_TAG, "No favorite item updated");
        }
    }

    // When user selects favorite icon in action bar.
    // Changes the favorite icon selected to default depending on the current state of the icon.
    // Alternates the favorite value for the selected movie and updates the content provider.
    public void switchSelectedMovieFavorite() {

        ContentValues movieValues = new ContentValues();
        Log.v(LOG_TAG, "Test what favorite has in switch method" + favorite);

        if (favorite.equals("False")) {
            movieValues.put(MovieContract.MoviesEntry.COLUMN_MOVIE_FAVORITE, "True");
            favoriteMenuItem.setIcon(R.drawable.ic_favorite_movie_selected);
        } else if (favorite.equals("True")) {
            movieValues.put(MovieContract.MoviesEntry.COLUMN_MOVIE_FAVORITE, "False");
            favoriteMenuItem.setIcon(R.drawable.ic_favorite_movie_default);
        } else {
            Log.v(LOG_TAG, "No favorite chosen");
        }

        // add movie to favorites in details pane = ?
        String movieSelection =
                MovieContract.MoviesEntry.TABLE_NAME +
                        "." + MovieContract.MoviesEntry.COLUMN_MOVIE_ID + " = ? ";

        String[] movieSelectionArgs = new String[]{Integer.toString(id)};


        int insertUpdate;
        insertUpdate = getActivity().getContentResolver().update(
                MovieContract.MoviesEntry.CONTENT_URI,
                movieValues,
                movieSelection,
                movieSelectionArgs);

        Log.d(LOG_TAG, "FetchWeatherTask Complete. " + insertUpdate + " Inserted");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detail_fragment, menu);

        favoriteMenuItem = menu.findItem(R.id.action_favorite);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        // When user wants to add a movie to favorites
        // or remove it from favorites they choose this
        if (id == R.id.action_favorite) {
            switchSelectedMovieFavorite();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    void onMovieSelectionChanged() {
/*        Uri uri = mUri;
        if (null != uri) {
            int movieId = MovieContract.MoviesEntry.getMovieIDFromUri(uri);
            Uri updatedMovieUri = MovieContract.MoviesEntry.buildMovieWithID(movieId);
            mUri = updatedMovieUri;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }*/

        mUri = null;
        getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
    }

    // Creates the Loader
    // Query the MovieContentProvider to find the detail information on the selected movie.
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    MOVIE_DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    // Insert the values into the corresponding Detail Fragment private values
    // Use setMovieFavoriteButton to discover if the movie is one of the user's favorites.
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            // Intent package sent from mainActivityFragment is received
            // All movieItem variables are extracted
            // Extracted variables are linked to display on Fragment Movie Detail

            id = data.getInt(COL_MOVIE_ID);
            originalTitle = data.getString(COL_MOVIE_ORIGIONAL_TITLE);
            movieImage = data.getString(COL_MOVIE_IMAGE);
            releaseDate = data.getString(COL_MOVIE_RELEASE_DATE);
            voteAverage = data.getString(COL_MOVIE_VOTE_AVERAGE);
            voteCount = data.getInt(COL_MOVIE_VOTE_COUNT);
            overView = data.getString(COL_MOVIE_OVERVIEW);
            preferredSort = data.getString((COL_MOVIE_PREFERRED_SORT));
            favorite = data.getString(COL_MOVIE_FAVORITE);


            movieOrigionalTitleView.setText(originalTitle);

            int width = getActivity().getResources().getDisplayMetrics().widthPixels;
            int height = getActivity().getResources().getDisplayMetrics().heightPixels;

            // Picasso api is used to load image efficiently
            Picasso.with(getActivity())
                    .load(movieImage)
                    .resize(width / 2, width / 2)
                    .into(movieImageView);

            movieReleaseDateView.setText(releaseDate);

            // RatingBar widget looks better for users with 5 stars
            // theMovieDB rates movies with 10 being maximum
            // So each rating had to be divided by 2
            // Float is used to incorporate half stars
            float voteAverage_float = Float.parseFloat(voteAverage);
            movieRatingBarView.setRating(voteAverage_float / 2);

            movieVoteCountView.setText(voteCount + " (votes)");
            movieOverviewView.setText(overView);

            setMovieFavoriteButton();


            Log.v(LOG_TAG, "Test what favorite has" + favorite);
        }

    }

    //Could I use Cursor data again with this method
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    // AsyncTask process to pull the trailer for the selected movie in MovieDetail.
    // This was inserted in MovieDetailFragment because this is the only time it is utilized.
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
