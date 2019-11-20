package com.myappcompany.hardi.actors.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.myappcompany.hardi.actors.R;
import com.myappcompany.hardi.actors.db.DatabaseHelper;
import com.myappcompany.hardi.actors.db.model.Actor;
import com.myappcompany.hardi.actors.fragment.DetailFragment;
import com.myappcompany.hardi.actors.fragment.ListFragment;

import java.sql.SQLException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ListFragment.OnProductSelectedListener {

    private Toolbar toolbar;

    private DatabaseHelper databaseHelper;
    private int actorId=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();


        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ListFragment listFragment = new ListFragment();
            ft.add(R.id.displayList, listFragment, "List_Fragment");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }

    }

    private void addActor(){
        final Dialog dialog=new Dialog(this);
        dialog.setContentView(R.layout.add_actor_dialog);

        final EditText actorName=(EditText) dialog.findViewById(R.id.actor_name);
        final EditText actorBiography=(EditText) dialog.findViewById(R.id.actor_biography);
        final EditText actorDate=(EditText) dialog.findViewById(R.id.actor_birth);
        final RatingBar ratingActor=(RatingBar) dialog.findViewById(R.id.actor_rating);

        Button add=(Button) dialog.findViewById(R.id.actor_addBtn);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String name=actorName.getText().toString();
                    String biography=actorBiography.getText().toString();
                    String date=actorDate.getText().toString();

                    if(name.isEmpty()){
                        Toast.makeText(MainActivity.this,"Name must be implemented",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(biography.isEmpty()){
                        Toast.makeText(MainActivity.this,"Biography must be implemented",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(date.isEmpty()){
                        Toast.makeText(MainActivity.this,"Birth date must be implemented",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Actor actor=new Actor();
                    actor.setmName(name);
                    actor.setBiography(biography);
                    actor.setDate(date);
                    actor.setRating(ratingActor.getRating());

                    getDatabaseHelper().getActorDao().create(actor);
                    refresh();
                    dialog.dismiss();

                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
        });
        dialog.show();
    }

    private void refresh(){
        ListView listView=(ListView) findViewById(R.id.actors);

        if(listView!=null){
            ArrayAdapter<Actor> adapter=(ArrayAdapter<Actor>) listView.getAdapter();

            if(adapter!=null){
                try {
                    adapter.clear();
                    List<Actor> list=getDatabaseHelper().getActorDao().queryForAll();
                    adapter.addAll(list);

                    adapter.notifyDataSetChanged();
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
    }


    private void setupToolbar(){
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_item_detail,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add:
                    addActor();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProductSelected(int id) {
        actorId=id;
        try {
            Actor actor=getDatabaseHelper().getActorDao().queryForId(id);

            DetailFragment detailFragment=new DetailFragment();
            detailFragment.setActor(actor);
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.displayList,detailFragment,"Detail_Fragment");
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            transaction.addToBackStack("Detail_Fragment");
            transaction.commit();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        refresh();
    }

    public DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(databaseHelper!=null){
            OpenHelperManager.releaseHelper();
            databaseHelper=null;
        }
    }

}
