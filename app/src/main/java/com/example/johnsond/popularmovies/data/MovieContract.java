package com.example.johnsond.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by JohnsonD on 3/21/16.
 */
public class MovieContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.johnsond.popularmovies";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";

    /* Inner class that defines the table contents of the movie table */
    public static final class MoviesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";

        // Movie ID issued by themoviedb as returned by API when selectable movie is displayed
        public static final String COLUMN_MOVIE_ID = "MovieID";

        // Movie origional title as returned by API when selectable movie is displayed
        public static final String COLUMN_MOVIE_ORIGIONAL_TITLE = "OriginalTitle";

        // Movie MovieImage will hold the url of the image for the movie
        public static final String COLUMN_MOVIE_IMAGE = "MovieImage";

        // Movie Overview will hold the movie information, shown during the movie detail
        public static final String COLUMN_MOVIE_OVERVIEW = "OverView";

        // Movie vote average will hold average rating for movie shown movie detail
        public static final String COLUMN_MOVIE_VOTE_AVERAGE = "VoteAverage";

        // Movie vite count holds the amount of votes for movie shows in movie detail activity
        public static final String COLUMN_MOVIE_VOTE_COUNT = "VoteCount";

        // Movie release date holds the date of the movie shows in movie detail activity
        public static final String COLUMN_MOVIE_RELEASE_DATE = "ReleaseDate";

        // Movie is either "Popular" or "Highest Rated"
        public static final String COLUMN_MOVIE_PREFERRED_SORT = "PreferredSort";

        // Movie favorite holds true/false value for the movies choosen as favorites
        public static final String COLUMN_MOVIE_FAVORITE = "Favorite";

        // Builds uri that paths to a movie records id (long)
        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // Builds uri that paths to a movie records that match preferredSort
        // COLUMN_MOVIE_RELEASE_DATE == preferredSort
        public static Uri buildMoviesWithPreferredSort(String preferredSort) {
            return CONTENT_URI.buildUpon().appendPath(preferredSort).build();
        }

        // Builds uri that paths to a movie records that match isFavorite
        // COLUMN_MOVIE_FAVORITE == isFavorite
        public static Uri buildMoviesFavorites(String isFavorite) {
            return CONTENT_URI.buildUpon().appendPath("favorite").appendPath(isFavorite).build();
        }

        // Builds uri that paths to a movie records that match MovieID
        // COLUMN_MOVIE_ID == movieID
        public static Uri buildMovieWithID(int movieID) {
            return CONTENT_URI.buildUpon().appendPath("id").appendPath(Integer.toString(movieID)).build();
        }

        // Extract the preferredSort from the current uri
        public static String getMoviePreferredSortFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        // Extract the MovieID from the current uri
        public static int getMovieIDFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(2));
        }

        // Extract the favorite from the current uri
        public static String getMovieFavorite(Uri uri) {
            return uri.getPathSegments().get(2);
        }
    }
}
