package com.example.compking.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.compking.Adapter.SongAdapter;
import com.example.compking.R;
import com.example.compking.model.Song;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MusicFragment extends Fragment {

    private EditText searchbar;

    private RecyclerView recyclerViewPosts;

    private SongAdapter songAdapter;
    private List<Song> songList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D4AF37")));

        recyclerViewPosts = view.findViewById(R.id.recycler_view_songs);
        recyclerViewPosts.setHasFixedSize(true);
       LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
       recyclerViewPosts.setLayoutManager(linearLayoutManager);
        songList = new ArrayList<>();
        songAdapter = new SongAdapter(getContext(), songList);
        recyclerViewPosts.setAdapter(songAdapter);
        searchbar = view.findViewById(R.id.search_bar);



        readSongs();



        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {

                searchSong(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {



            }
        });


        return view;
    }

    private void searchSong(String s) {
        Query query = FirebaseFirestore.getInstance().collection("songs").orderBy("name").startAt(s)
                .endAt(s + "\uf8ff");
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                songList.clear();

                for (DocumentSnapshot ds : value) {
                    Song song = ds.toObject(Song.class);
                    songList.add(song);


                }
                songAdapter.notifyDataSetChanged();
            }
        });
    }

    private void readSongs() {

        FirebaseFirestore.getInstance().collection("songs").orderBy("name").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                songList.clear();
                for (DocumentSnapshot ds: value) {
                    Song song = ds.toObject(Song.class);
                    songList.add(song);
                }
                songAdapter.notifyDataSetChanged();
                //upToDate(new ArrayList<>());
            }

        });


    }

 //public void upToDate(List<Song> newList){
 //    songAdapter.notifyDataSetChanged();
 //    songList = new ArrayList<>();
 //    songList.addAll(newList);

 //}

}
