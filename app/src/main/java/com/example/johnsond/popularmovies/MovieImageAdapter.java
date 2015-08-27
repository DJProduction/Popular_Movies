package com.example.johnsond.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by JohnsonD on 7/14/15.
 */
public class MovieImageAdapter extends ArrayAdapter<MovieItem> {

    public MovieImageAdapter(Activity context, List<MovieItem> movieItem) {
        super (context, 0,movieItem);
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieItem movieItem = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_movies, parent, false);
        }
        ImageView movieImageView = (ImageView) convertView.findViewById(R.id.movieImageView);
        Picasso.with(getContext())
                .load(movieItem.getMovieImage())
                .resize(300,500)
                .centerCrop()
                .into(movieImageView);

        return convertView;
    }

}


