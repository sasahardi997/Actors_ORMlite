package com.myappcompany.hardi.actors.db.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import androidx.annotation.NonNull;

@DatabaseTable(tableName = Actor.TABLE_NAME_USER)
public class Actor {

    public static final String TABLE_NAME_USER="products";

    public static final String FIELD_NAME_ID="id";
    public static final String FIELD_NAME_NAME="name";
    public static final String FIELD_NAME_BIOGRAPHY="biography";
    public static final String FIELD_NAME_RATING="rating";
    public static final String FIELD_NAME_DATE="date";
    public static final String FIELD_NAME_IMAGE="image";
    public static final String TABLE_MOVIE_MOVIES="movies";


    @DatabaseField(columnName = FIELD_NAME_ID,generatedId = true)
    private int mId;

    @DatabaseField(columnName = FIELD_NAME_NAME)
    private String mName;

    @DatabaseField(columnName = FIELD_NAME_BIOGRAPHY)
    private String biography;

    @DatabaseField(columnName =FIELD_NAME_RATING )
    private Float rating;

    @DatabaseField(columnName = FIELD_NAME_DATE)
    private String date;

    @DatabaseField(columnName = FIELD_NAME_IMAGE)
    private String image;

    @ForeignCollectionField(columnName = Actor.TABLE_MOVIE_MOVIES,eager = true)
    private ForeignCollection<Movie> movies;

    public Actor(){

    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ForeignCollection<Movie> getMovies() {
        return movies;
    }

    public void setMovies(ForeignCollection<Movie> movies) {
        this.movies = movies;
    }

    @NonNull
    @Override
    public String toString() {
        return mName;
    }
}
