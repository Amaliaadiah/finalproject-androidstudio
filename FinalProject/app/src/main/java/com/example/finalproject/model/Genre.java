package com.example.finalproject.model;

import com.google.gson.annotations.SerializedName;

public class Genre {
    public Genre(int id, String name) {
        this.id = id;
        this.name = name;
    }
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
