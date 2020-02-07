package com.graspery.www.spicemeup.Models;

public class ArchiveModelMovie {
    String id;
    String movieName;
    String posterPath;

    public ArchiveModelMovie(String id, String movieName, String posterPath) {
        this.id = id;
        this.movieName = movieName;
        this.posterPath = posterPath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
}
