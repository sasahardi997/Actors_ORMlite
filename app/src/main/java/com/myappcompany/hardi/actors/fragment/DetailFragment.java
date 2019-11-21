package com.myappcompany.hardi.actors.fragment;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.myappcompany.hardi.actors.R;
import com.myappcompany.hardi.actors.activities.MainActivity;
import com.myappcompany.hardi.actors.db.model.Actor;
import com.myappcompany.hardi.actors.db.model.Movie;

import java.sql.SQLException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

public class DetailFragment extends Fragment {

    private Actor actor;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState!=null){
            actor=new Actor();
            actor.setmId(savedInstanceState.getInt("id"));
            actor.setmName(savedInstanceState.getString("name"));
            actor.setBiography(savedInstanceState.getString("biography"));
            actor.setRating(savedInstanceState.getFloat("rating"));
            actor.setDate(savedInstanceState.getString("date"));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        if(savedInstanceState!=null){
            savedInstanceState.putInt("id",actor.getmId());
            savedInstanceState.putString("name",actor.getmName());
            savedInstanceState.putString("biography",actor.getBiography());
            savedInstanceState.putFloat("rating",actor.getRating());
            savedInstanceState.putString("date",actor.getDate());
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.detail_fragment_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add_movie:
                addMoview();
                break;
            case R.id.action_edit:
                editActor();
                break;
            case R.id.action_delete:
                deleteActor();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v("DetailFragment","onCreateView()");
        setHasOptionsMenu(true);
        View view=inflater.inflate(R.layout.detail_fragment,container,false);

       ImageView imageView=(ImageView) view.findViewById(R.id.image);
        Uri mUri=Uri.parse(actor.getImage());
        imageView.setImageURI(mUri);

        EditText name=(EditText) view.findViewById(R.id.actor_name);
        name.setText(actor.getmName());

        EditText biography=(EditText) view.findViewById(R.id.actor_biography);
        biography.setText(actor.getBiography());

        RatingBar rating=(RatingBar) view.findViewById(R.id.actor_ratig);
        rating.setRating(actor.getRating());

        EditText date=(EditText) view.findViewById(R.id.actor_date);
        date.setText(actor.getDate());

        final ListView listView=(ListView) view.findViewById(R.id.actor_movies);

        try {
            List<Movie> list=((MainActivity)getActivity()).getDatabaseHelper().getMovieDao().queryBuilder()
                    .where()
                    .eq(Movie.FIELD_NAME_ACTOR, actor.getmId())
                    .query();

            ListAdapter adapter=new ArrayAdapter<>(getActivity(),R.layout.list_item,list);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Movie movie=(Movie) listView.getItemAtPosition(position);
                    Toast.makeText(getActivity(),movie.getmName()+"\n"+movie.getGenre()+"\n"+movie.getYear(),Toast.LENGTH_LONG).show();
                }
            });

        }catch (SQLException e){
            e.printStackTrace();
        }

        return view;
    }



    public void setActor(Actor actor){
        this.actor=actor;
    }

    private void deleteActor() {
        try {
            if (actor != null) {
                ((MainActivity) getActivity()).getDatabaseHelper().getActorDao().delete(actor);

                ((MainActivity)getActivity()).showMessage("Actor deleted");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        getActivity().onBackPressed();
    }

    private void editActor(){
        EditText actorName=(EditText) getActivity().findViewById(R.id.actor_name);
        EditText actorBiography=(EditText) getActivity().findViewById(R.id.actor_biography);
        EditText actorDate=(EditText) getActivity().findViewById(R.id.actor_date);
        RatingBar ratingActor=(RatingBar) getActivity().findViewById(R.id.actor_ratig);

        actor.setmName(actorName.getText().toString());
        actor.setBiography(actorBiography.getText().toString());
        actor.setDate(actorDate.getText().toString());
        actor.setRating(ratingActor.getRating());

        try {
            ((MainActivity)getActivity()).getDatabaseHelper().getActorDao().update(actor);

            ((MainActivity)getActivity()).showMessage("Actor detail updated");

        }catch (SQLException E){
            E.printStackTrace();
        }
        getActivity().onBackPressed();
    }

    private void addMoview(){
        final Dialog dialog=new Dialog(getActivity());
        dialog.setContentView(R.layout.add_movie_dialog);

        Button add=(Button) dialog.findViewById(R.id.add_movie);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText name=(EditText) dialog.findViewById(R.id.movie_name);
                EditText genre=(EditText) dialog.findViewById(R.id.movie_genre);
                EditText year=(EditText) dialog.findViewById(R.id.movie_year);

                Movie movie=new Movie();
                movie.setmName(name.getText().toString());
                movie.setGenre(genre.getText().toString());
                movie.setYear(year.getText().toString());
                movie.setActor(actor);

                try {
                    ((MainActivity)getActivity()).getDatabaseHelper().getMovieDao().create(movie);
                }catch (SQLException e){
                    e.printStackTrace();
                }
                refresh();

                ((MainActivity)getActivity()).showMessage("New moview added to actor");

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void refresh(){
        ListView listView=(ListView) getActivity().findViewById(R.id.actor_movies);

        if(listView!=null){
            ArrayAdapter<Movie> adapter=(ArrayAdapter<Movie>)listView.getAdapter();

            if(adapter!=null){
                try {
                    adapter.clear();
                    List<Movie> list=((MainActivity)getActivity()).getDatabaseHelper().getMovieDao().queryBuilder()
                            .where()
                            .eq(Movie.FIELD_NAME_ACTOR,actor.getmId())
                            .query();

                    adapter.addAll(list);
                    adapter.notifyDataSetChanged();
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
