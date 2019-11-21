package com.myappcompany.hardi.actors.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.myappcompany.hardi.actors.R;
import com.myappcompany.hardi.actors.db.DatabaseHelper;
import com.myappcompany.hardi.actors.db.model.Actor;
import com.myappcompany.hardi.actors.dialog.AboutDialog;
import com.myappcompany.hardi.actors.fragment.DetailFragment;
import com.myappcompany.hardi.actors.fragment.ListFragment;
import com.myappcompany.hardi.actors.preference.Preferences;

import java.sql.SQLException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ListFragment.OnProductSelectedListener {

    private Toolbar toolbar;

    private DatabaseHelper databaseHelper;
    private int actorId=0;
    private SharedPreferences prefs;

    public static String NOTIF_TOAST="notif_toast";
    public static String NOTIF_STATUS="notif_statis";

    private String imagePath=null;
    private ImageView preview;
    public static final String TAG="PERMISSIONS";
    public static final int SELECT_PICTURE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();

        prefs= PreferenceManager.getDefaultSharedPreferences(this);

        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ListFragment listFragment = new ListFragment();
            ft.add(R.id.displayList, listFragment, "List_Fragment");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }

    }

    private void reset(){
        imagePath="";
        preview=null;
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    private void selectPicture(){
        if (isStoragePermissionGranted()) {
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, SELECT_PICTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==SELECT_PICTURE && resultCode==RESULT_OK && null != data){
            Uri selectedImage=data.getData();
            String[] filePathColum={MediaStore.Images.Media.DATA};

            if(selectedImage!=null){
                Cursor cursor=getContentResolver().query(selectedImage,filePathColum,null,null,null);
                if(cursor!=null){
                    cursor.moveToFirst();

                    int columnIndex=cursor.getColumnIndex(filePathColum[0]);
                    imagePath=cursor.getString(columnIndex);
                    cursor.close();

                    if(preview!=null){
                        preview.setImageBitmap(BitmapFactory.decodeFile(imagePath));
                    }
                    Toast.makeText(this,imagePath,Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
        }
    }

    private void addActor(){
        final Dialog dialog=new Dialog(this);
        dialog.setContentView(R.layout.add_actor_dialog);

        Button choosebtn = (Button) dialog.findViewById(R.id.choose);
        choosebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preview = (ImageView) dialog.findViewById(R.id.preview_image);
                selectPicture();
            }
        });

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

                    if (preview == null || imagePath == null){
                        Toast.makeText(MainActivity.this, "Picture must be implemented", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Actor actor=new Actor();
                    actor.setmName(name);
                    actor.setBiography(biography);
                    actor.setDate(date);
                    actor.setImage(imagePath);
                    actor.setRating(ratingActor.getRating());

                    getDatabaseHelper().getActorDao().create(actor);

                    showMessage("Added new actor");
                    refresh();
                    reset();

                }catch (SQLException e){
                    e.printStackTrace();
                }
                dialog.dismiss();
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

    public void showStatusMessage(String message){
        NotificationManager mNotificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.ic_people);
        mBuilder.setContentTitle("You've new message");
        mBuilder.setContentText(message);

        Bitmap bm=BitmapFactory.decodeResource(getResources(),R.drawable.ic_add_black_24dp);
        mBuilder.setLargeIcon(bm);

        mNotificationManager.notify(1,mBuilder.build());
    }

    public void showMessage(String message){
        boolean toast=prefs.getBoolean(NOTIF_TOAST,false);
        boolean status=prefs.getBoolean(NOTIF_STATUS,false);

        if(toast){
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        }

        if(status){
            showStatusMessage(message);
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
            case R.id.action_about:
                AlertDialog alertDialog=new AboutDialog(this).prepareDialog();
                alertDialog.show();
                break;
            case R.id.action_preferences:
                startActivity(new Intent(MainActivity.this, Preferences.class));
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
