package com.example.johnsond.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    private  String originalTitle;
    private  String movieImage;
    private  String overView;
    private  String voteAverage;
    private  String voteCount;
    private  String releaseDate;

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        // Intent package sent from mainActivityFragment is received
        // All movieItem variables are extracted
        // Extracted variables are linked to display on Fragment Movie Detail
        Intent receiveMovie = getActivity().getIntent();
        if (receiveMovie != null)
        {
            originalTitle = receiveMovie.getStringExtra("origional_title");
            movieImage = receiveMovie.getStringExtra("movie_image");
            releaseDate = receiveMovie.getStringExtra("release_date");
            voteAverage = receiveMovie.getStringExtra("vote_average");
            voteCount = receiveMovie.getStringExtra("vote_count");
            overView = receiveMovie.getStringExtra("overview");

            TextView movieTitleView = (TextView) rootView.findViewById(R.id.movie_detail_origional_title_view);
            movieTitleView.setText(originalTitle);

            // Picasso api is used to load image efficiently
            ImageView movieImageView = (ImageView) rootView.findViewById(R.id.movie_detail_imageView);
            Picasso.with(getActivity())
                    .load(movieImage)
                    .resize(300,500)
                    .centerCrop()
                    .into(movieImageView);

            TextView movieReleaseDateView = (TextView) rootView.findViewById(R.id.movie_detail_release_date);
            movieReleaseDateView.setText(releaseDate);
            RatingBar movieRatingBarView = (RatingBar) rootView.findViewById(R.id.movie_detail_ratingBar);

            // RatingBar widget looks better for users with 5 stars
            // theMovieDB rates movies with 10 being maximum
            // So each rating had to be divided by 2
            // Float is used to incorporate half stars
            float voteAverage_float=Float.parseFloat(voteAverage);
            movieRatingBarView.setRating( voteAverage_float/2 );

            TextView movieVoteCountView = (TextView) rootView.findViewById(R.id.movie_detail_vote_count);
            movieVoteCountView.setText(voteCount + " (votes)");
            TextView movieOverviewView = (TextView) rootView.findViewById(R.id.movie_detail_overview);
            movieOverviewView.setText(overView);
        }



        return rootView;
    }
}
