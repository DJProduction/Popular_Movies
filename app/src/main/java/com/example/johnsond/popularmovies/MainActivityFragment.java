package com.example.johnsond.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    // Adapter used in onCreatveView to capture files from themoviesdb.org and
    // add to a xml fragment_main gridViewMovie
    private MovieImageAdapter movieImgAdapter;

    public MainActivityFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

/*        MovieItem[] mThumbIds= new MovieItem[3];
        mThumbIds[0].setMovieImage(R.drawable.sample_2);
                mThumbIds[1].setMovieImage(R.drawable.sample_3);
                mThumbIds[2].setMovieImage(R.drawable.sample_4);
        List<MovieItem> test = new ArrayList<MovieItem>(Arrays.asList(mThumbIds));*/

        movieImgAdapter = new MovieImageAdapter(getActivity());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        final GridView gridview = (GridView) rootView.findViewById(R.id.gridViewMovies);
        gridview.setAdapter(movieImgAdapter);

        //TmdbMovies movieApi;

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getActivity(), "" + position,
                        Toast.LENGTH_SHORT).show();

            }

        });
        return rootView;

    }


}
