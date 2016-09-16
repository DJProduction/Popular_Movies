package com.example.johnsond.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by JohnsonD on 7/14/15.
 */
public class MovieImageAdapter extends CursorAdapter {

    // Boolean used when checking the orientation of the device.
    private boolean viewLandscape;

    // Adapter receives list of movies which information for each
    public MovieImageAdapter(Activity context, Cursor movieCursor, int flags) {
        super(context, movieCursor, flags);
    }

    // Function used to check if the device is in landscape
    // Use the context (activity) calling the configuration method to check the device's orientation.
    public boolean checkOrientationLandscape(Context context) {
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return true;
        } else {
            return false;
        }
    }

    // For each new view, attach view to movieViewHolder
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_movies, parent, false);
        ViewHolder movieViewHolder = new ViewHolder(view);
        view.setTag(movieViewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder movieViewHolder = (ViewHolder) view.getTag();
        viewLandscape = checkOrientationLandscape(context);

        // Holds the display height and width of the device
        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;

        try {
            // Check if device is in landscape
            // if true make the height larger than the width
            // This will make up for the fact the image is stretched landscape
            if (viewLandscape) {
                Picasso.with(context)
                        .load(cursor.getString(MainActivityFragment.COL_MOVIE_IMAGE))
                        .resize(width / 4, height / 2)
                        .into(movieViewHolder.movieImageView);
            } else {
                Picasso.with(context)
                        .load(cursor.getString(MainActivityFragment.COL_MOVIE_IMAGE))
                        .resize(width / 4, height / 4)
                        .into(movieViewHolder.movieImageView);
            }
        } catch (IllegalArgumentException e) {
            movieViewHolder.movieImageView.setImageURI(Uri.parse(cursor.getString(MainActivityFragment.COL_MOVIE_IMAGE)));
        }

        String movieTitle = cursor.getString(MainActivityFragment.COL_MOVIE_ORIGIONAL_TITLE);
        movieViewHolder.movieTitleTextView.setText(movieTitle);

    }

    // Cache of the children views for the movie gridView item.
    public static class ViewHolder {
        public final ImageView movieImageView;
        public final TextView movieTitleTextView;

        public ViewHolder(View view) {
            movieImageView = (ImageView) view.findViewById(R.id.movieImageView);
            movieTitleTextView = (TextView) view.findViewById(R.id.movie_origional_title_view);
        }
    }
}


