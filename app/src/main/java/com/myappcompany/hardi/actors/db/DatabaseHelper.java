package com.myappcompany.hardi.actors.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.myappcompany.hardi.actors.db.model.Actor;
import com.myappcompany.hardi.actors.db.model.Movie;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    public static final String DATABASE_NAME="priprema.db";
    public static final int DATABASE_VERSION=1;

    private Dao<Actor,Integer> mActorDao=null;
    private Dao<Movie,Integer> mMovieDao=null;

    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource,Movie.class);
            TableUtils.createTable(connectionSource,Actor.class);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource,Movie.class,true);
            TableUtils.dropTable(connectionSource,Actor.class,true);
            onCreate(db,connectionSource);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public Dao<Actor,Integer> getActorDao() throws SQLException{
        if(mActorDao==null){
            mActorDao=getDao(Actor.class);
        }
        return mActorDao;
    }

    public Dao<Movie,Integer> getMovieDao() throws SQLException{
        if(mMovieDao==null){
            mMovieDao=getDao(Movie.class);
        }
        return mMovieDao;
    }

    @Override
    public void close() {
        mActorDao=null;
        mMovieDao=null;

        super.close();
    }
}
