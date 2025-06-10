package com.example.finalproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalproject.R;
import com.example.finalproject.model.Movie;
import com.example.finalproject.ui.detail.DetailActivity;

import java.util.*;
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private Map<Integer, String> genreMap;

    private final Context context;
    private List<Movie> movieList;

    public MovieAdapter(Context context, List<Movie> movieList, Map<Integer, String> genreMap) {
        this.context = context;
        this.movieList = movieList;
        this.genreMap = genreMap;
    }

    public void setData(List<Movie> newList) {
        this.movieList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.tvTitle.setText(movie.getTitle());
        holder.tvRating.setText(String.format(Locale.getDefault(), "%.1f", movie.getRating()));
        if (genreMap != null && movie.getGenreIds() != null) {
            holder.tvGenre.setText(getGenreNames(movie.getGenreIds()));
        } else {
            holder.tvGenre.setText(movie.getGenreNames() != null ? movie.getGenreNames() : "Unknown");
        }

        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgPoster);

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
        return movieList != null ? movieList.size() : 0;
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPoster;
        TextView tvTitle, tvGenre, tvRating;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.imgPoster);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvGenre = itemView.findViewById(R.id.tvGenre);
            tvRating = itemView.findViewById(R.id.tvRating);
        }
    }

    public void setGenreMap(Map<Integer, String> genreMap) {
        this.genreMap = genreMap;
        notifyDataSetChanged(); // refresh tampilan adapter
    }

    private String getGenreNames(List<Integer> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) return "Unknown";
        List<String> genreNames = new ArrayList<>();
        for (int id : genreIds) {
            if (genreMap != null && genreMap.containsKey(id)) {
                genreNames.add(genreMap.get(id));
            }
        }
        return TextUtils.join(", ", genreNames);
    }
}
