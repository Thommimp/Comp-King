package com.example.compking.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.compking.MainActivity;
import com.example.compking.R;
import com.example.compking.SongActivity;
import com.example.compking.SongActivity2;
import com.example.compking.VideoPlayActivity;
import com.example.compking.model.Song;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private Context mContext;
    private List<Song> mSongs;
    private LinearLayout lin;
    private FirebaseAuth fUser;

    public SongAdapter(Context mContext, List<Song> mSongs) {
        this.mContext = mContext;
        this.mSongs = mSongs;
    }

    @NonNull
    @Override
    public SongAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.song_item, parent, false);
        return new SongAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongAdapter.ViewHolder holder, int position) {

        Song song = mSongs.get(position);
        String id = song.getId();
       //holder.name.setText(song.getName());
       //holder.auther.setText(song.getAuther());
       //holder.bpm.setText("bpm " + song.getBpm());


      FirebaseFirestore.getInstance().collection("songs").document(song.getId())
                      .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                          @Override
                          public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                              Song song = value.toObject(Song.class);
                              holder.name.setText(song.getName().substring(0,1).toUpperCase() + song.getName().substring(1));
                              holder.auther.setText(song.getAuther());
                              holder.bpm.setText("bpm " + song.getBpm());
                          }
                      });

      holder.itemView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Intent intent = new Intent(mContext, VideoPlayActivity.class);
              intent.putExtra("id", song.getId());
              intent.putExtra("name", song.getName());
              intent.putExtra("downloadurl", song.getDownloadurl());
              intent.putExtra("description", song.getDescription());
              intent.putExtra("bpm", song.getBpm());
                  mContext.startActivity(intent);

          }
      });




    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView auther;
        public TextView name;
        public TextView bpm;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            auther = itemView.findViewById(R.id.auther);
            name = itemView.findViewById(R.id.name);
            bpm = itemView.findViewById(R.id.bpm);
            lin = itemView.findViewById(R.id.lin);
            fUser = FirebaseAuth.getInstance();




        }
    }
}
