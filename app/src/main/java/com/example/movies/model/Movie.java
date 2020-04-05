package com.example.movies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    public final String title;
    public final String poster_path;
    public final String overview;
    public final int voteAverage;
    public final String releaseDate;

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private Movie(Parcel parcel) {
        this.title = parcel.readString();
        this.poster_path = parcel.readString();
        this.overview = parcel.readString();
        this.voteAverage = parcel.readInt();
        this.releaseDate = parcel.readString();
    }

    public Movie(String title, String poster_path , String overview , int voteAverage , String releaseDate) {
        this.title = title;
        this.poster_path = poster_path;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.poster_path);
        dest.writeString(this.overview);
        dest.writeInt(this.voteAverage);
        dest.writeString(this.releaseDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
