package com.example.johnsond.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.johnsond.popularmovies.data.MovieContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // Specify the columns the loader will use when adding values
    static final int COL_MOVIE_INDEX_ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_MOVIE_ORIGIONAL_TITLE = 2;
    static final int COL_MOVIE_IMAGE = 3;
    static final int COL_MOVIE_PREFERRED_SORT = 4;
    static final int COL_MOVIE_FAVORITE = 5;
    static final int COL_MOVIE_RELEASE_DATE = 6;
    static final int COL_MOVIE_VOTE_AVERAGE = 7;
    static final int COL_MOVIE_VOTE_COUNT = 8;
    static final int COL_MOVIE_OVERVIEW = 9;
    // Saves the string name of the key held for onSaveInstanceState bundle
    private static final String movieSavedStateKey = "MOVIE_SELECT_KEY";
    //Loader number started at 0;
    private static final int MOVIE_LOADER = 0;
    // Specify the columns the loader will need when querying
    private static final String[] MOVIE_PROJECTION_COLUMNS = {
            MovieContract.MoviesEntry.TABLE_NAME + "." + MovieContract.MoviesEntry._ID,
            MovieContract.MoviesEntry.COLUMN_MOVIE_ID,
            MovieContract.MoviesEntry.COLUMN_MOVIE_ORIGIONAL_TITLE,
            MovieContract.MoviesEntry.COLUMN_MOVIE_IMAGE,
            MovieContract.MoviesEntry.COLUMN_MOVIE_PREFERRED_SORT,
            MovieContract.MoviesEntry.COLUMN_MOVIE_FAVORITE,
            MovieContract.MoviesEntry.COLUMN_MOVIE_RELEASE_DATE,
            MovieContract.MoviesEntry.COLUMN_MOVIE_VOTE_AVERAGE,
            MovieContract.MoviesEntry.COLUMN_MOVIE_VOTE_COUNT,
            MovieContract.MoviesEntry.COLUMN_MOVIE_OVERVIEW
    };
    private SharedPreferences sort;
    private GridView movieGridview;
    private int movieViewPosition = GridView.INVALID_POSITION;
    // Adapter used in onCreatveView to capture files from themoviesdb.org and
    // add to a xml fragment_main gridViewMovie
    private MovieImageAdapter movieImgAdapter;

    public MainActivityFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovieList();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // The CursorAdapter will take data from our cursor and populate the GridView.
        movieImgAdapter = new MovieImageAdapter(getActivity(), null, 0);
        movieGridview = (GridView) rootView.findViewById(R.id.gridViewMovies);
        movieGridview.setAdapter(movieImgAdapter);

        // Listens for when user slects a specific movie
        // build the url to be passed in Callback method to the detail fragment
        movieGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    ((Callback) getActivity())
                            .onItemSelected(MovieContract.MoviesEntry
                                    .buildMovieWithID(cursor.getInt(COL_MOVIE_ID)));
                }
            }
        });

        // Save the position of the Gridview at the current view of movies
        if (savedInstanceState != null && savedInstanceState.containsKey(movieSavedStateKey)) {
            movieViewPosition = savedInstanceState.getInt(movieSavedStateKey);
        }

        // movieItem on GridView is selected the information related to that movie is sent via Intent
        // Intent opens movieDetailActivity and information is extracted in movieDetailFragment
        return rootView;

    }

    // Save the position of the Gridview at the current view of movies
    // uses movieSavedStateKey as reference
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (movieViewPosition != GridView.INVALID_POSITION) {
            outState.putInt(movieSavedStateKey, movieViewPosition);
            super.onSaveInstanceState(outState);
        }
    }

    // Update the GridView with new movies when sort selection is changed, orientation change
    // or activity is refreshed.
    void onMovieSortChanged() {
        updateMovieList();
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    //FetchMovies task is started and the sorting preference chosen from Settings Activity is passed into the method
    private void updateMovieList() {
        FetchMovies asyncGetMovies = new FetchMovies(getActivity());
        sort = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String pref_movieSort = sort.getString(getString(R.string.pref_movieSort_key),
                getString(R.string.pref_movieSort_popular_movies));
        asyncGetMovies.execute(pref_movieSort);
    }

    //Based on a stackoverflow snippet
    //Checks network is available in onStart() before FetchMovies is even initiated
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Create loader to query MovieContentProvider for all movies that match the sort.
    // If favorite is true then the uri is altered to show based on Favorites previously selected by user.
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        sort = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String pref_movieSort = sort.getString(getString(R.string.pref_movieSort_key),
                getString(R.string.pref_movieSort_popular_movies));

        Uri moviesSortedUri;
        if (pref_movieSort.equals(getString(R.string.pref_movieSort_favorites))) {
            String isFavorite = "True";
            moviesSortedUri = MovieContract.MoviesEntry.buildMoviesFavorites(isFavorite);
        } else {
            moviesSortedUri = MovieContract.MoviesEntry.buildMoviesWithPreferredSort(pref_movieSort);
        }

        return new CursorLoader(getActivity(),
                moviesSortedUri,
                MOVIE_PROJECTION_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        movieImgAdapter.swapCursor(data);
        if (movieViewPosition != GridView.INVALID_POSITION) {
            movieGridview.smoothScrollToPosition(movieViewPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieImgAdapter.swapCursor(null);
    }

    public interface Callback {
        /**
         * MovieDetailActivityFragment Callback for when an item has been selected.
         */
        public void onItemSelected(Uri movieSelectedUri);
    }

}

