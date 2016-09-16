package com.example.johnsond.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.johnsond.popularmovies.data.MovieContract.MoviesEntry;

/**
 * Created by JohnsonD on 3/24/16.
 */
public class MovieDbHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "movies.db";
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 34;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create MoviesEntry table
    // Description on the type of values that the each columns can hold.
    // Description of how the table will react to records entered and limitations.
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + MoviesEntry.TABLE_NAME + " (" +
                MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                // the ID of the movies entry associated with this weather data
                MoviesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MoviesEntry.COLUMN_MOVIE_ORIGIONAL_TITLE + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_MOVIE_IMAGE + " REAL NOT NULL, " +
                MoviesEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL," +
                MoviesEntry.COLUMN_MOVIE_VOTE_AVERAGE + " REAL NOT NULL, " +
                MoviesEntry.COLUMN_MOVIE_VOTE_COUNT + " INTEGER NOT NULL, " +
                MoviesEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_MOVIE_PREFERRED_SORT + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_MOVIE_FAVORITE + " TEXT NOT NULL, " +

                // To assure the application have just one weather entry per day
                // per location, it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + MoviesEntry.COLUMN_MOVIE_ID + ") ON CONFLICT IGNORE);";

        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
