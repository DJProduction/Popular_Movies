package com.example.johnsond.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by JohnsonD on 2/23/16.
 */
public class MovieReviewsAdapter extends ArrayAdapter<MovieReviews> {


    // Adapter receives reviews of movies which information for each is in a String array
    public MovieReviewsAdapter(Activity context, List<MovieReviews> movieReviews) {
        super(context, 0, movieReviews);
    }

    // Create a new TextViews containing the author and reviews
    // Inflate the views into the layout
    // For each item referenced from Uri query the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieReviews movieReview = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_movie_reviews, parent, false);
        }

        // Searches for the reference in the xml for the format of each review author
        // Applies the author name for current review contained on the list.
        TextView movieAuthorView = (TextView) convertView.findViewById(R.id.author_name_textview);
        movieAuthorView.setText(movieReview.getAuthor());

        // Searches for the reference in the xml for the format of each movie review
        // Applies the review for current movie contained on the list.
        TextView movieReviewView = (TextView) convertView.findViewById(R.id.author_review_textview);
        movieReviewView.setText(movieReview.getReview());

        return convertView;
    }
}
