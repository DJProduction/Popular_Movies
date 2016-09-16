package com.example.johnsond.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback {

    // Tag created to indicate the detail fragment so that it can be easily retrieved again.
    private static final String MOVIEDETAILFRAGMENT_TAG = "MDFTAG";
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    // Holds the value of the current sort selected by user
    private String prevSort;

    // Boolean used to keep track of which layout is needed two-pane for bigger devices
    // Single pane for smaller devices
    private boolean twoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Use MovieUtility class to get the preferred sort from the Settings Activity
        prevSort = MovieUtility.getPreferredSort(this);

        View detailFrag = findViewById(R.id.detail_fragment);

        // If detailFrag is not null then it was able to find (detail_fragment) exists
        if (detailFrag != null) {
            // If detail_fragment exists that means the devices is using a two-pane layout
            twoPane = true;
            //Create MovieDetailActivityFragment to show the details for movie
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_fragment, new MovieDetailActivityFragment(), MOVIEDETAILFRAGMENT_TAG);
                // movieDetailFrag is created to control the Fragment jjuust created with more functions from the
                // getSupportFragmentManager.
                MovieDetailActivityFragment movieDetailFrag = (MovieDetailActivityFragment) getSupportFragmentManager().findFragmentByTag(MOVIEDETAILFRAGMENT_TAG);
                // If movieDetailFrag is not null then update detail fragment to be hidden until user selects another movie
                if (null != movieDetailFrag) {
                    getSupportFragmentManager().beginTransaction().hide(movieDetailFrag).commit();
                }

            }
        } else {
            twoPane = false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, MovieSettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check to see if sort(checked when activity is resumed) is different from prevSort
        String sort = MovieUtility.getPreferredSort(this);
        if (sort != null && !sort.equals(prevSort)) {
            // If sort doesn't equal prevSort then update main Activity using onMovieSortChanged function.
            MainActivityFragment movieActivityFrag = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
            if (null != movieActivityFrag) {
                movieActivityFrag.onMovieSortChanged();
            }
            MovieDetailActivityFragment movieDetailFrag = (MovieDetailActivityFragment) getSupportFragmentManager().findFragmentByTag(MOVIEDETAILFRAGMENT_TAG);
            // If movieDetailFrag is not null then update detail fragment to be hidden until user selects another movie
            // This is because the user has switched the sort so their new movie grid should not be accompanied
            // with the previously selected movie.
            if (null != movieDetailFrag) {
                getSupportFragmentManager().beginTransaction().hide(movieDetailFrag).commit();
            }

            // Keep the value of the sort
            prevSort = sort;
        }
    }


    @Override
    public void onItemSelected(Uri movieSelectedUri) {
        if (movieSelectedUri == null) {
            return;
        }
        if (twoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(MovieDetailActivityFragment.MOVIE_DETAIL_URI, movieSelectedUri);

            MovieDetailActivityFragment detailMovieFragment = new MovieDetailActivityFragment();
            detailMovieFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment, detailMovieFragment, MOVIEDETAILFRAGMENT_TAG)
                    .commit();
        } else {
            // If single pane then pass movie information to detail activity
            Intent intent = new Intent(this, MovieDetailActivity.class)
                    .setData(movieSelectedUri);
            startActivity(intent);
        }
    }
}
