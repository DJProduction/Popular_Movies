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

    // Cache of the children views for the movie gridView item.
    public static class ViewHolder {
        private TextView movieAuthorView;
        private TextView movieReviewView;

    }

    // Create a new TextViews containing the author and reviews
    // Inflate the views into the layout
    // For each item referenced from Uri query the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_movie_reviews, parent, false);
            viewHolder = new ViewHolder();
            // Searches for the reference in the xml for the format of each review author
            // Applies the author name for current review contained on the list.
            viewHolder.movieAuthorView = (TextView) convertView.findViewById(R.id.author_name_textview);
            // Searches for the reference in the xml for the format of each movie review
            // Applies the review for current movie contained on the list.
            viewHolder.movieReviewView = (TextView) convertView.findViewById(R.id.author_review_textview);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MovieReviews movieReview = getItem(position);

        //TextView movieAuthorView = (TextView) convertView.findViewById(R.id.author_name_textview);
        viewHolder.movieAuthorView.setText(movieReview.getAuthor());

        //TextView movieReviewView = (TextView) convertView.findViewById(R.id.author_review_textview);
        viewHolder.movieReviewView.setText(movieReview.getReview());

        return convertView;
    }
}
