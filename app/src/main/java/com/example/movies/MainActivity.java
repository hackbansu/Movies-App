package com.example.movies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.movies.model.Movie;
import com.example.movies.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements MoviesListAdapter.MovieItemClickListener {

    private TextView mListHeadingTextView;
    private RecyclerView mListRecyclerView;
    private TextView mListErrorMessageTextView;
    private ProgressBar mListProgressBar;

    private MoviesListAdapter mAdapter;
    private int currentMoviesFilter;

    private int calculateGridSpanCount() {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float displayWidth = displayMetrics.widthPixels / displayMetrics.density;
        int numberOfColumns = (int) (displayWidth / 150);
        return Math.max(numberOfColumns, 2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListHeadingTextView = findViewById(R.id.tv_movies_list_heading);
        mListRecyclerView = findViewById(R.id.recyclerview_movies_list);
        mListErrorMessageTextView = findViewById(R.id.tv_movies_list_error);
        mListProgressBar = findViewById(R.id.pb_movies_list);
        currentMoviesFilter = getResources().getInteger(R.integer.popular_movies);
        if (savedInstanceState != null && savedInstanceState.containsKey("currentMoviesFilter")) {
            currentMoviesFilter = savedInstanceState.getInt("currentMoviesFilter");
        }

        // set layout manager
        GridLayoutManager layoutManager = new GridLayoutManager(
                this,
                calculateGridSpanCount(),
                GridLayoutManager.VERTICAL,
                false
        );
        mListRecyclerView.setLayoutManager(layoutManager);

        // set fixed size
        mListRecyclerView.setHasFixedSize(true);

        // set adapter
        mAdapter = new MoviesListAdapter(this);
        mListRecyclerView.setAdapter(mAdapter);

        if (savedInstanceState == null || !savedInstanceState.containsKey("moviesData")) {
            loadMoviesData(currentMoviesFilter);
        } else {
            this.setHeading();
            mAdapter.updateMoviesData(
                    (Movie[]) savedInstanceState.getParcelableArray("moviesData")
            );
        }

    }

    private void setHeading() {
        String headingText = getString(R.string.popular_movies);
        if (currentMoviesFilter == getResources().getInteger(R.integer.top_rated_movies)) {
            headingText = getString(R.string.top_rated_movies);
        }
        mListHeadingTextView.setText(headingText);
    }

    private void loadMoviesData(int moviesFilter) {
        currentMoviesFilter = moviesFilter;
        this.setHeading();
        mAdapter.updateMoviesData(null);
        showMoviesList();

        new FetchMoviesTask().execute(moviesFilter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArray("moviesData", mAdapter.moviesData);
        outState.putInt("currentMoviesFilter", this.currentMoviesFilter);
        super.onSaveInstanceState(outState);
    }

    private void showMoviesList() {
        mListErrorMessageTextView.setVisibility(View.INVISIBLE);
        mListRecyclerView.setVisibility(View.VISIBLE);
        mListHeadingTextView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mListRecyclerView.setVisibility(View.INVISIBLE);
        mListHeadingTextView.setVisibility(View.INVISIBLE);
        mListErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMovieItemClick(Movie movieData) {
        Intent startDetailActivityIntent = new Intent(this, DetailActivity.class);
        startDetailActivityIntent.putExtra("android.intent.extra.movie_data", movieData);
        startActivity(startDetailActivityIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        int moviesFilter;

        if (itemId == R.id.action_sort_popular) {
            moviesFilter = getResources().getInteger(R.integer.popular_movies);
            if (currentMoviesFilter != moviesFilter) {
                loadMoviesData(moviesFilter);
            }
        } else if (itemId == R.id.action_sort_top_rated) {
            moviesFilter = getResources().getInteger(R.integer.top_rated_movies);
            if (currentMoviesFilter != moviesFilter) {
                loadMoviesData(moviesFilter);
            }
        } else if (itemId == R.id.action_refresh_movies_list) {
            loadMoviesData(currentMoviesFilter);
        }

        return super.onOptionsItemSelected(item);
    }

    class FetchMoviesTask extends AsyncTask<Integer, Void, Movie[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mListProgressBar.setVisibility(View.VISIBLE);
        }

        private URL getMoviesUrl(int filterChoice) {
            String filterPath = getString(R.string.popular_movies_url_path);
            if (filterChoice == getResources().getInteger(R.integer.top_rated_movies)) {
                filterPath = getString(R.string.top_rated_movies_url_path);
            }
            return NetworkUtils.buildUrl(
                    getString(R.string.movies_list_base_url),
                    filterPath,
                    new String[]{"api_key"},
                    new String[]{getString(R.string.the_movie_db_api_key)}
            );
        }

        private Movie[] parseMoviesListResponse(String response) {
            try {
                JSONObject data = new JSONObject(response);
                JSONArray results = data.getJSONArray("results");
                Movie[] moviesData = new Movie[results.length()];

                for (int i = 0; i < results.length(); i++) {
                    JSONObject movieObj = results.getJSONObject(i);
                    moviesData[i] = new Movie(
                            movieObj.getString("title"),
                            movieObj.getString("poster_path"),
                            movieObj.getString("overview"),
                            movieObj.getInt("vote_average"),
                            movieObj.getString("release_date")
                    );
                }
                return moviesData;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected Movie[] doInBackground(Integer... integers) {
            if (integers.length == 0) {
                return null;
            }

            // prepare url
            URL moviesUrl = getMoviesUrl(integers[0]);

            try {
                // fetch data from url
                String response = NetworkUtils.getResponseFromHttpUrl(
                        moviesUrl,
                        MainActivity.this
                );

                // decode response string to results JSONArray
                return response != null ? parseMoviesListResponse(response) : null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            if (movies != null) {
                mAdapter.updateMoviesData(movies);
                showMoviesList();
            } else {
                showErrorMessage();
            }
            mListProgressBar.setVisibility(View.INVISIBLE);
        }
    }
}
