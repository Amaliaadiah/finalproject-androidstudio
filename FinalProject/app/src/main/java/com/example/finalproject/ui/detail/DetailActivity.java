package com.example.finalproject.ui.detail;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.finalproject.R;
import com.example.finalproject.model.Movie;
import com.example.finalproject.network.ApiClient;
import com.example.finalproject.network.MovieApiService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    private ImageView ivPoster, ivFavorite, imgBackdrop;
    private TextView tvTitle, tvOverview, tvRating, tvDuration, tvGenres;

    private boolean isFavorited = false;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "MoviePrefs";
    private static final String FAVORITES_KEY = "favorites";

    private int movieId;
    private Movie currentMovie = new Movie();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Movie Detail");
        }

        ivPoster = findViewById(R.id.ivPoster);
        ivFavorite = findViewById(R.id.ivFavorite);
        imgBackdrop = findViewById(R.id.imgBackdrop);
        tvTitle = findViewById(R.id.tvTitle);
        tvOverview = findViewById(R.id.tvOverview);
        tvRating = findViewById(R.id.tvRating);
        tvDuration = findViewById(R.id.tvDuration);
        tvGenres = findViewById(R.id.tvGenres);
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        Intent intent = getIntent();
        movieId = intent.getIntExtra("movie_id", -1);
        String title = intent.getStringExtra("title");
        String overview = intent.getStringExtra("overview");
        String poster = intent.getStringExtra("poster");
        float rating = intent.getFloatExtra("rating", 0);
        String releaseDate = intent.getStringExtra("releaseDate");
        String backdropPath = intent.getStringExtra("backdropPath");
        String genres = intent.getStringExtra("genres");

        tvTitle.setText(title != null ? title : "-");
        tvOverview.setText(overview != null ? overview : "-");
        tvRating.setText(String.format(Locale.getDefault(), "%.1f", rating));
        tvGenres.setText(genres != null ? genres : "Unknown");
        tvDuration.setText("-");
        if (poster != null && !poster.isEmpty()) {
            Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w500" + poster)
                    .into(ivPoster);
        }

        if (backdropPath != null && !backdropPath.isEmpty()) {
            Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w780" + backdropPath)
                    .into(imgBackdrop);
        } else {
            imgBackdrop.setImageResource(R.drawable.ic_launcher_background);
        }

        currentMovie.setId(movieId);
        currentMovie.setTitle(title);
        currentMovie.setOverview(overview);
        currentMovie.setPosterPath(poster);
        currentMovie.setRating(rating);
        currentMovie.setGenreNames(genres);
        currentMovie.setBackdropPath(backdropPath);
        currentMovie.setReleaseDate(releaseDate);

        fetchMovieDetailsFromApi();

        isFavorited = isMovieInFavorites(movieId);
        updateFavoriteIcon();

        ivFavorite.setOnClickListener(v -> toggleFavorite());
    }

    private void fetchMovieDetailsFromApi() {
        MovieApiService apiService = ApiClient.getClient().create(MovieApiService.class);
        Call<Movie> call = apiService.getMovieDetails(movieId, "f11f24e9d2224c64ea47dc81fab72d42");

        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Movie movie = response.body();

                    int runtime = movie.getRuntime();
                    String durationText = runtime > 0 ? runtime + " min" : "-";
                    tvDuration.setText(durationText);

                    // Update runtime ke currentMovie
                    currentMovie.setRuntime(runtime);
                } else {
                    tvDuration.setText("-");
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                tvDuration.setText("-");
            }
        });
    }

    private void toggleFavorite() {
        new Thread(() -> {
            List<Movie> favorites = getFavoritesFromPrefs();

            runOnUiThread(() -> {
                if (isFavorited) {
                    // Hapus dari favorit
                    for (int i = 0; i < favorites.size(); i++) {
                        if (favorites.get(i).getId() == movieId) {
                            favorites.remove(i);
                            new Thread(() -> saveFavoritesToPrefs(favorites)).start();
                            runOnUiThread(() -> Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show());
                            break;
                        }
                    }
                } else {
                    // Tambah ke favorit
                    if (!isMovieInFavorites(movieId, favorites)) {
                        favorites.add(currentMovie);
                        new Thread(() -> saveFavoritesToPrefs(favorites)).start();
                        runOnUiThread(() -> Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show());
                    }
                }

                isFavorited = !isFavorited;
                updateFavoriteIcon();
            });
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // atau finish();
        return true;
    }

    private void updateFavoriteIcon() {
        ivFavorite.setImageResource(isFavorited ?
                R.drawable.ic_favorite : R.drawable.ic_favorite_border);
    }

    private List<Movie> getFavoritesFromPrefs() {
        String json = sharedPreferences.getString(FAVORITES_KEY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<Movie>>() {}.getType();
        return new Gson().fromJson(json, type);
    }


    private void saveFavoritesToPrefs(List<Movie> favorites) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = new Gson().toJson(favorites);
        editor.putString(FAVORITES_KEY, json);
        editor.apply();
    }

    private boolean isMovieInFavorites(int movieId) {
        return isMovieInFavorites(movieId, getFavoritesFromPrefs());
    }

    private boolean isMovieInFavorites(int movieId, List<Movie> favorites) {
        for (Movie movie : favorites) {
            if (movie.getId() == movieId) {
                return true;
            }
        }
        return false;
    }
}
