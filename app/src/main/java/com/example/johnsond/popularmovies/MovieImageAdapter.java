package com.example.johnsond.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by JohnsonD on 7/14/15.
 */
public class MovieImageAdapter extends BaseAdapter {

    private Context mContext;
    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7,
            R.drawable.sample_0, R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7,
            R.drawable.sample_0, R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7
    };

    public MovieImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView view = (ImageView) convertView;
        if (view == null) {
            view = new ImageView(mContext);
        }
        int url = mThumbIds[position];
        Picasso.with(mContext)
                .load(url)
                .resize(300,500)
                .centerCrop()
                .into(view);

        return view;
    }

}

   /* public MovieImageAdapter(Activity context, List<MovieItem> movieItem) {
        super (context, 0, movieItem);
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieItem movieItem = getItem(position);
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_movies, parent, false);
        ImageView movieImageView;

        if (convertView == null) {
            convertView = new ImageView(getContext());
        }

        movieImageView = (ImageView) convertView.findViewById(R.id.moiveImageView);
        Picasso.with(getContext()).load(movieItem.getMovieImage()).centerCrop().into(movieImageView);

        return convertView;

    }*/

