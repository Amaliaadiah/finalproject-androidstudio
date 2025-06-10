package com.example.finalproject.ui.favorit;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;
import com.example.finalproject.adapter.MovieAdapter;
import com.example.finalproject.model.Movie;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.*;

public class FavoritFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private MovieAdapter adapter;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "MoviePrefs";
    private static final String FAVORITES_KEY = "favorites";
    private static final String GENRE_MAP_KEY = "genre_map";

    public FavoritFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorit, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewFavorites);
        tvEmpty = view.findViewById(R.id.tvEmptyFavorites);

        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, 0);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new MovieAdapter(requireContext(), new ArrayList<>(), getGenreMapFromPrefs());
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void loadFavorites() {
        List<Movie> favoriteMovies = getFavoritesFromPrefs();
        if (favoriteMovies.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.setData(favoriteMovies);
        }
    }

    private List<Movie> getFavoritesFromPrefs() {
        String json = sharedPreferences.getString(FAVORITES_KEY, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<Movie>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    private Map<Integer, String> getGenreMapFromPrefs() {
        String json = sharedPreferences.getString(GENRE_MAP_KEY, null);
        if (json == null) return new HashMap<>();
        Type type = new TypeToken<Map<Integer, String>>() {}.getType();
        return new Gson().fromJson(json, type);
    }
}
