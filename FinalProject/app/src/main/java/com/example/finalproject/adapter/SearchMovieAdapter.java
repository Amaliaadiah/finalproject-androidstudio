package com.example.finalproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalproject.R;
import com.example.finalproject.model.Movie;
import com.example.finalproject.ui.detail.DetailActivity;

import java.util.*;

public class SearchMovieAdapter extends RecyclerView.Adapter<SearchMovieAdapter.SearchViewHolder> {

    private final Context context;
    private List<Movie> movieList;
    private final Map<Integer, String> genreMap;

    public SearchMovieAdapter(Context context, List<Movie> movieList, Map<Integer, String> genreMap) {
        this.context = context;
        this.movieList = movieList;
        this.genreMap = genreMap;
    }

    public void setData(List<Movie> movies) {
        movieList.clear();
        movieList.addAll(movies);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie_search, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        Movie movie = movieList.get(position);

        holder.tvTitle.setText(movie.getTitle());
        holder.tvRating.setText(String.format(Locale.getDefault(), "%.1f", movie.getRating()));
        holder.tvYear.setText(movie.getReleaseDate() != null ? movie.getReleaseDate() : "-");
        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                .into(holder.imgPoster);

        List<Integer> genreIds = movie.getGenreIds();
        StringBuilder genreBuilder = new StringBuilder();
        if (genreIds != null) {
            for (int id : genreIds) {
                String name = genreMap.get(id);
                if (name != null) {
                    if (genreBuilder.length() > 0) genreBuilder.append(", ");
                    genreBuilder.append(name);
                }
            }
        }

        String genres;
        if (movie.getGenreNames() != null && !movie.getGenreNames().isEmpty()) {
            genres = movie.getGenreNames();
        } else {
            genres = getGenreNames(movie.getGenreIds());
        }
        holder.tvGenre.setText(genres);

        holder.itemView.setOnClickListener(v -> {
                if (!isNetworkAvailable()) {
                    Toast.makeText(context, "You need internet to view details", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("movie_id", movie.getId());
                intent.putExtra("title", movie.getTitle());
                intent.putExtra("overview", movie.getOverview());
                intent.putExtra("poster", movie.getPosterPath());
                intent.putExtra("rating", movie.getRating());
                String genreText = (genreMap != null && movie.getGenreIds() != null)
                        ? getGenreNames(movie.getGenreIds())
                        : (movie.getGenreNames() != null ? movie.getGenreNames() : "Unknown");
                intent.putExtra("genres", genreText);

                intent.putExtra("backdropPath", movie.getBackdropPath());
                intent.putExtra("releaseDate", movie.getReleaseDate());
                intent.putExtra("runtime", movie.getRuntime());
                context.startActivity(intent);
        });
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }


    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPoster;
        TextView tvTitle, tvYear, tvDuration, tvGenre, tvRating;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.imgPoster);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvYear = itemView.findViewById(R.id.tvYear);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvGenre = itemView.findViewById(R.id.tvGenre);
            tvRating = itemView.findViewById(R.id.tvRating);
        }
    }

    private String getGenreNames(List<Integer> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) return "Unknown";
        List<String> genreNames = new ArrayList<>();
        for (int id : genreIds) {
            if (genreMap.containsKey(id)) {
                genreNames.add(genreMap.get(id));
            }
        }
        return android.text.TextUtils.join(", ", genreNames);
    }
}
