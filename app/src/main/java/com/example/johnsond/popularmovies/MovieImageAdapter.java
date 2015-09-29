package com.example.johnsond.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by JohnsonD on 7/14/15.
 */
public class MovieImageAdapter extends ArrayAdapter<MovieItem> {

    // Adapter receives list of movies which information for each
    public MovieImageAdapter(Activity context, List<MovieItem> movieItem) {
        super (context, 0,movieItem);
    }

    // Create a new ImageView - Movie Poster
    // TextView - Movie Title
    // For each item referenced from Uri query the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieItem movieItem = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_movies, parent, false);
        }

        // Searches for the reference in the xml for the format of each movie title
        // Applies the titles for current movies contained on the list.
        TextView movieTitleView = (TextView) convertView.findViewById(R.id.movie_origional_title_view);
        movieTitleView.setText(movieItem.getOriginalTitle());

        // Searches for the reference in the xml for the format of each movie image
        // Applies the images for current movies contained on the list.
        // Uses the Picasso Api for enhanced video loading
        ImageView movieImageView = (ImageView) convertView.findViewById(R.id.movieImageView);
        Picasso.with(getContext())
                .load(movieItem.getMovieImage())
                .resize(300,500)
                .centerCrop()
                .into(movieImageView);

        return convertView;
    }

}


