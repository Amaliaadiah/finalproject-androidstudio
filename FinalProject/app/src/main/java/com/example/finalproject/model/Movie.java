package com.example.finalproject.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Movie implements Serializable {
    @SerializedName("genre_ids")
    private List<Integer> genreIds;

    public List<Integer> getGenreIds() {
        return genreIds;
    }
    @SerializedName("backdrop_path")
    private String backdropPath;

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    @SerializedName("vote_average")
    private float rating;

    // Getter
    public float getRating() {
        return rating;
    }
    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("overview")
    private String overview;

    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("runtime")
    private int runtime;
    private String genreNames;

    public String getGenreNames() {
        return genreNames;
    }

    public void setGenreNames(String genreNames) {
        this.genreNames = genreNames;
    }
    public int getRuntime() {
        return runtime;
    }
    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }
    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getOverview() { return overview; }
    public String getPosterPath() { return posterPath; }
    public String getReleaseDate() { return releaseDate; }
}
