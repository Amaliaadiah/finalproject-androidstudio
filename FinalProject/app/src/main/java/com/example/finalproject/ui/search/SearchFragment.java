package com.example.finalproject.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;
import com.example.finalproject.adapter.SearchMovieAdapter;
import com.example.finalproject.model.Genre;
import com.example.finalproject.model.GenreResponse;
import com.example.finalproject.model.Movie;
import com.example.finalproject.model.MovieResponse;
import com.example.finalproject.network.ApiClient;
import com.example.finalproject.network.MovieApiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private Spinner spinnerGenre;
    private final List<Genre> genreList = new ArrayList<>();
    private LinearLayout layoutError;
    private Button btnRetry;
    private EditText searchInput;
    private SearchMovieAdapter adapter;
    private final List<Movie> movieList = new ArrayList<>();
    private final Map<Integer, String> genreMap = new HashMap<>();
    private MovieApiService apiService;
    private final String API_KEY = "f11f24e9d2224c64ea47dc81fab72d42";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        initializeViews(view);
        setupRecyclerView();
        setupApiService();
        setupRetryButton();
        setupSearchInput();
        fetchGenres();
        loadDiscoveredMovies();

        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewSearch);
        searchInput = view.findViewById(R.id.etSearch);
        spinnerGenre = view.findViewById(R.id.spinnerGenre);
        layoutError = view.findViewById(R.id.layoutError);
        btnRetry = view.findViewById(R.id.btnRetry);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SearchMovieAdapter(requireContext(), movieList, genreMap);
        recyclerView.setAdapter(adapter);
    }

    private void setupApiService() {
        apiService = ApiClient.getClient().create(MovieApiService.class);
    }

    private void setupRetryButton() {
        btnRetry.setOnClickListener(v -> {
            hideErrorLayout();
            String query = searchInput.getText().toString().trim();
            if (!query.isEmpty()) {
                searchMovie(query);
            } else {
                loadDiscoveredMovies();
            }
        });
    }

    private void setupSearchInput() {
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            String query = searchInput.getText().toString().trim();
            if (!query.isEmpty()) {
                searchMovie(query);
            }
            return true;
        });
    }


    private void loadDiscoveredMovies() {
        apiService.discoverMovies(API_KEY).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body().getResults();
                    filterAndDisplayMovies(movies);
                    hideErrorLayout();
                } else {
                    showErrorLayout();
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                showErrorLayout();
            }
        });
    }


    private void filterAndDisplayMovies(List<Movie> movies) {
        int selectedGenrePosition = spinnerGenre.getSelectedItemPosition();
        if (selectedGenrePosition > 0 && selectedGenrePosition < genreList.size()) {
            int selectedGenreId = genreList.get(selectedGenrePosition).getId();
            List<Movie> filteredResults = new ArrayList<>();
            for (Movie movie : movies) {
                if (movie.getGenreIds() != null && movie.getGenreIds().contains(selectedGenreId)) {
                    filteredResults.add(movie);
                }
            }
            adapter.setData(filteredResults);
        } else {
            adapter.setData(movies);
        }
    }

    private void fetchGenres() {
        apiService.getGenres(API_KEY).enqueue(new Callback<GenreResponse>() {
            @Override
            public void onResponse(Call<GenreResponse> call, Response<GenreResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    genreList.clear();
                    genreList.add(new Genre(0, "All Genres"));
                    genreList.addAll(response.body().getGenres());

                    for (Genre g : response.body().getGenres()) {
                        genreMap.put(g.getId(), g.getName());
                    }

                    setupGenreSpinner();
                }
            }

            @Override
            public void onFailure(Call<GenreResponse> call, Throwable t) {
                showErrorLayout();
            }
        });
    }

    private void setupGenreSpinner() {
        List<String> genreNames = new ArrayList<>();
        for (Genre g : genreList) genreNames.add(g.getName());
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                genreNames
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(spinnerAdapter);

        spinnerGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String query = searchInput.getText().toString().trim();
                if (!query.isEmpty()) {
                    searchMovie(query);
                } else {
                    loadDiscoveredMovies();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void showErrorLayout() {
        if (recyclerView != null && layoutError != null) {
            recyclerView.setVisibility(View.GONE);
            layoutError.setVisibility(View.VISIBLE);
        }
    }

    private void hideErrorLayout() {
        if (recyclerView != null && layoutError != null) {
            recyclerView.setVisibility(View.VISIBLE);
            layoutError.setVisibility(View.GONE);
        }
    }

    private void searchMovie(String query) {
        apiService.searchMovie(API_KEY, query).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> allResults = response.body().getResults();
                    filterSearchResults(allResults);
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                showErrorLayout();
            }
        });
    }

    private void filterSearchResults(List<Movie> allResults) {
        int selectedGenrePosition = spinnerGenre.getSelectedItemPosition();
        if (selectedGenrePosition > 0 && selectedGenrePosition < genreList.size()) {
            int selectedGenreId = genreList.get(selectedGenrePosition).getId();
            List<Movie> filteredResults = new ArrayList<>();
            for (Movie movie : allResults) {
                if (movie.getGenreIds() != null && movie.getGenreIds().contains(selectedGenreId)) {
                    filteredResults.add(movie);
                }
            }
            adapter.setData(filteredResults);
        } else {
            adapter.setData(allResults);
        }
    }
}