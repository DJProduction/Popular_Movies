package com.example.johnsond.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class MovieContentProvider extends ContentProvider {

    static final int MOVIE = 100;
    static final int MOVIESWITHPREFERREDSORT = 101;
    static final int MOVIEWITHID = 102;
    static final int MOVIESFAVORITES = 103;
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final SQLiteQueryBuilder sMovieReviewQueryBuilder;
    // movie.favorite = ?
    private static final String sFavoriteSelection =
            MovieContract.MoviesEntry.TABLE_NAME +
                    "." + MovieContract.MoviesEntry.COLUMN_MOVIE_FAVORITE + " = ? ";
    // movie.preferredSort = ?
    private static final String sPreferredSortSelection =
            MovieContract.MoviesEntry.TABLE_NAME +
                    "." + MovieContract.MoviesEntry.COLUMN_MOVIE_PREFERRED_SORT + " = ? ";
    // movie in details pane = ?
    private static final String sMovieSelection =
            MovieContract.MoviesEntry.TABLE_NAME +
                    "." + MovieContract.MoviesEntry.COLUMN_MOVIE_ID + " = ? ";

    static {
        sMovieReviewQueryBuilder = new SQLiteQueryBuilder();
    }

    private final String LOG_TAG = MovieContentProvider.class.getSimpleName();
    private MovieDbHelper mOpenHelper;

    public MovieContentProvider() {
    }

    /*
    Matches the uri against various uri composition types.
    The results will be assigned a specific numerical value held in reference objects.
    MOVIE, MOVIEWITHID, MOVIESFAVORITES, MOVIESWITHPREFERREDSORT.
    This will numerical reference number returned will usually be used in switch statement
    to discover the correct set of actions to be performed.
    */
    static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root URI.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/id/*", MOVIEWITHID);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/favorite/*", MOVIESFAVORITES);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/*", MOVIESWITHPREFERREDSORT);


        return matcher;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case MOVIE:
                return MovieContract.MoviesEntry.CONTENT_TYPE;
            case MOVIESWITHPREFERREDSORT:
                return MovieContract.MoviesEntry.CONTENT_TYPE;
            case MOVIESFAVORITES:
                return MovieContract.MoviesEntry.CONTENT_TYPE;
            case MOVIEWITHID:
                return MovieContract.MoviesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor returnCursor;
        switch (sUriMatcher.match(uri)) {
            // "If movies listed as Popular or Highest Rated in preferrence sort"
            case MOVIESWITHPREFERREDSORT: {
                Log.v(LOG_TAG, "Choose MOVIESWITHPREFERREDSORT in detail for query.");
                String preferredSortSetting = MovieContract.MoviesEntry.getMoviePreferredSortFromUri(uri);

                selection = sPreferredSortSelection;
                selectionArgs = new String[]{preferredSortSetting};

                returnCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "If movie is selected"
            case MOVIE: {
                Log.v(LOG_TAG, "Choose MOVIE in detail for query.");
                returnCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case MOVIEWITHID: {
                Log.v(LOG_TAG, "Choose MOVIEWITHID in detail for query.");

                String movieID = Integer.toString(MovieContract.MoviesEntry.getMovieIDFromUri(uri));
                selection = sMovieSelection;
                selectionArgs = new String[]{movieID};

                returnCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case MOVIESFAVORITES: {
                Log.v(LOG_TAG, "Choose MOVIESFAVORITES in detail for query.");

                String movieFavoriteSelection = MovieContract.MoviesEntry.getMovieFavorite(uri);
                selection = sFavoriteSelection;
                selectionArgs = new String[]{movieFavoriteSelection};

                returnCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {

                long _id = db.insert(MovieContract.MoviesEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.MoviesEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(
                        MovieContract.MoviesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case MOVIE:
                rowsUpdated = db.update(MovieContract.MoviesEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                Log.v(LOG_TAG, "Choose MOVIESWITHPREFERREDSORT due to url.");
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MoviesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }

}
