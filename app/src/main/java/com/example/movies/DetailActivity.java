package com.example.movies;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.movies.model.Movie;
import com.example.movies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private Movie movieData;

    private ImageView mPosterImageView;
    private TextView mTitleTextView;
    private TextView mOverviewTextView;
    private TextView mVoteAverageTextView;
    private TextView mReleaseDateTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mPosterImageView = findViewById(R.id.iv_movie_detail_poster);
        mTitleTextView = findViewById(R.id.tv_movie_detail_title);
        mOverviewTextView = findViewById(R.id.tv_movie_detail_overview);
        mVoteAverageTextView = findViewById(R.id.tv_movie_detail_vote_average);
        mReleaseDateTextView = findViewById(R.id.tv_movie_detail_release_date);

        Intent parentIntent = getIntent();

        if (parentIntent != null) {
            if (parentIntent.hasExtra("android.intent.extra.movie_data")) {
                this.movieData = parentIntent.getParcelableExtra(
                        "android.intent.extra.movie_data"
                );

                // populate UI
                this.populateUI();
            }
        }
    }

    private void populateUI() {
        if (this.movieData == null) {
            return;
        }

        mTitleTextView.setText(movieData.title);
        mOverviewTextView.setText(movieData.overview);
        mVoteAverageTextView.setText(String.valueOf(movieData.voteAverage));
        mReleaseDateTextView.setText(movieData.releaseDate);
        if (movieData.poster_path != null) {
            Picasso.get()
                    .load(NetworkUtils.prepareMovieDBImageUri(movieData.poster_path))
                    .into(mPosterImageView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.moviedetail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_share_movie) {
            shareMovie();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void shareMovie() {
        String movieText = String.format(
                "Movie: %s\nRating: %s/10 \nRelease Date: %s",
                this.movieData.title,
                String.valueOf(this.movieData.voteAverage),
                this.movieData.releaseDate
        );

        ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setChooserTitle(R.string.share_movie_chooser_title)
                .setText(movieText)
                .startChooser();
    }
}
