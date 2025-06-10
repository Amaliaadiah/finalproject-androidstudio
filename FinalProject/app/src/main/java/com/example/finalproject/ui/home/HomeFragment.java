package com.example.finalproject.ui.home;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalproject.R;
import com.example.finalproject.adapter.MovieAdapter;
import com.example.finalproject.model.Genre;
import com.example.finalproject.model.GenreResponse;
import com.example.finalproject.model.Movie;
import com.example.finalproject.model.MovieResponse;
import com.example.finalproject.network.ApiClient;
import com.example.finalproject.network.MovieApiService;
import com.example.finalproject.pref.MoviePreferenceManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private MovieApiService apiService;
    private MoviePreferenceManager prefManager;
    private final Map<Integer, String> genreMap = new HashMap<>();
    private final List<Movie> allMovies = new ArrayList<>();
    private int currentPage = 1;
    private int totalPages = 1;
    private boolean isLoading = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = root.findViewById(R.id.rvMovies);

        setupAdapters();
        prefManager = new MoviePreferenceManager(requireContext());
        if (isOnline()) {
            apiService = ApiClient.getClient().create(MovieApiService.class);
            setupScrollListener();
            loadGenresWithFirstPage();
        } else {
            loadOfflineData();
        }

        return root;
    }

    private void setupAdapters() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        movieAdapter = new MovieAdapter(getContext(), new ArrayList<>(), genreMap);
        recyclerView.setAdapter(movieAdapter);
    }


    private void setupScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading &&
                        layoutManager.findLastCompletelyVisibleItemPosition() == allMovies.size() - 1) {
                    if (currentPage < totalPages) {
                        currentPage++;
                        loadNextPage();
                    }
                }
            }
        });
    }

    private void loadOfflineData() {
        List<Movie> cachedMovies = prefManager.getMovies();
        Map<Integer, String> cachedGenres = prefManager.getGenreMap();

        if (cachedMovies != null && !cachedMovies.isEmpty()) {
            allMovies.addAll(cachedMovies);
            movieAdapter.setData(allMovies);
        }

        if (cachedGenres != null && !cachedGenres.isEmpty()) {
            genreMap.putAll(cachedGenres);
            movieAdapter.setGenreMap(genreMap);
        }
    }

    private void loadGenresWithFirstPage() {
        apiService.getGenres("f11f24e9d2224c64ea47dc81fab72d42")
                .enqueue(new Callback<GenreResponse>() {
                    @Override
                    public void onResponse(Call<GenreResponse> call, Response<GenreResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            genreMap.clear();
                            for (Genre genre : response.body().getGenres()) {
                                genreMap.put(genre.getId(), genre.getName());
                            }
                            prefManager.saveGenreMap(genreMap);
                            movieAdapter.setGenreMap(genreMap);
                            loadNextPage();
                        }
                    }

                    @Override
                    public void onFailure(Call<GenreResponse> call, Throwable t) {
                        Log.e("GenreError", t.getMessage());
                        // Try to load cached genres
                        Map<Integer, String> cachedGenres = prefManager.getGenreMap();
                        if (cachedGenres != null && !cachedGenres.isEmpty()) {
                            genreMap.putAll(cachedGenres);
                            movieAdapter.setGenreMap(genreMap);
                        }
                        loadNextPage();
                    }
                });
    }

    private void loadNextPage() {
        isLoading = true;
        apiService.discoverMovies("f11f24e9d2224c64ea47dc81fab72d42", currentPage)
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        isLoading = false;
                        if (response.isSuccessful() && response.body() != null) {
                            totalPages = response.body().getTotalPages();
                            List<Movie> newMovies = response.body().getResults();
                            allMovies.addAll(newMovies);
                            movieAdapter.setData(allMovies);
                            prefManager.saveMovies(allMovies);
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        isLoading = false;
                        Log.e("MovieLoadError", t.getMessage());
                    }
                });
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}