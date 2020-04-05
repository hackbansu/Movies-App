package com.example.movies;

import com.example.movies.model.Movie;
import com.example.movies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class MoviesListAdapter extends Adapter<MoviesListAdapter.MovieViewHolder> {


    public Movie[] moviesData;
    private final MovieItemClickListener mListener;

    MoviesListAdapter(MovieItemClickListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutForListItem = R.layout.movies_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutForListItem, viewGroup, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder movieViewHolder, int i) {
        movieViewHolder.bind(moviesData[i]);
    }

    @Override
    public int getItemCount() {
        if (moviesData != null) {
            return moviesData.length;
        }

        return 0;
    }

    public void updateMoviesData(Movie[] moviesData) {
        this.moviesData = moviesData;
        notifyDataSetChanged();
    }

    public interface MovieItemClickListener {
        void onMovieItemClick(Movie movieData);
    }

    class MovieViewHolder extends ViewHolder implements RecyclerView.OnClickListener {
        final ImageView mImageThumbnail;
        final TextView mMovieTitle;
        Movie movieData;

        MovieViewHolder(View view) {
            super(view);
            mImageThumbnail = view.findViewById(R.id.iv_movie_list_thumbnail);
            mMovieTitle = view.findViewById(R.id.tv_movie_list_title);
            view.setOnClickListener(this);
        }

        void bind(Movie data) {
            // set title
            mMovieTitle.setText(data.title);

            // set thumbnail
            Uri imageUri = NetworkUtils.prepareMovieDBImageUri(data.poster_path);
            Picasso.get().load(imageUri).into(mImageThumbnail);

            // save the data with us
            this.movieData = data;
        }

        @Override
        public void onClick(View v) {
            mListener.onMovieItemClick(this.movieData);
        }
    }
}
