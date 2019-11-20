package com.myappcompany.hardi.actors.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.myappcompany.hardi.actors.R;
import com.myappcompany.hardi.actors.db.DatabaseHelper;
import com.myappcompany.hardi.actors.db.model.Actor;

import java.sql.SQLException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ListFragment extends Fragment {

    private DatabaseHelper databaseHelper;

    public interface OnProductSelectedListener{
        void onProductSelected(int id);
    }

    OnProductSelectedListener listener;
    ListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            final List<Actor> list=getDatabaseHelper().getActorDao().queryForAll();

            adapter=new ArrayAdapter<Actor>(getActivity(),R.layout.list_item,list);
            final ListView listView=(ListView) getActivity().findViewById(R.id.actors);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Actor a=(Actor) listView.getItemAtPosition(position);

                    listener.onProductSelected(a.getmId());
                }
            });
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(container==null){
            return null;
        }
        View view=inflater.inflate(R.layout.list_fragment,container,false);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener=(OnProductSelectedListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString()+" Must implement OnProductSelectedListener");
        }
    }

    public DatabaseHelper getDatabaseHelper(){
        if(databaseHelper==null){
            databaseHelper= OpenHelperManager.getHelper(getActivity(),DatabaseHelper.class);
        }
        return databaseHelper;
    }
}
