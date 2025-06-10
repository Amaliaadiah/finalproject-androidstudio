package com.example.finalproject.pref;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.finalproject.model.Movie;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoviePreferenceManager {
    private static final String PREFS_NAME = "MoviePrefs";
    private static final String MOVIES_KEY = "movies";
    private static final String GENRE_MAP_KEY = "genre_map";

    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public MoviePreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveMovies(List<Movie> movies) {
        String json = gson.toJson(movies);
        sharedPreferences.edit().putString(MOVIES_KEY, json).apply();
    }

    public List<Movie> getMovies() {
        String json = sharedPreferences.getString(MOVIES_KEY, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<Movie>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void saveGenreMap(Map<Integer, String> genreMap) {
        String json = gson.toJson(genreMap);
        sharedPreferences.edit().putString(GENRE_MAP_KEY, json).apply();
    }

    public Map<Integer, String> getGenreMap() {
        String json = sharedPreferences.getString(GENRE_MAP_KEY, null);
        if (json == null) return new HashMap<>();
        Type type = new TypeToken<Map<Integer, String>>() {}.getType();
        return gson.fromJson(json, type);
    }
}