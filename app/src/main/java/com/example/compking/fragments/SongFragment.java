package com.example.compking.fragments;

import android.content.ContentUris;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.compking.R;
import com.example.compking.model.Song;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class SongFragment extends Fragment {

    public String songId;
    private TextView songname;
    private ImageView play;
    private MediaPlayer mediaPlayer;
    private Uri downloadurl;
    private String id;
    private boolean isplaying;
    private ImageView pause;
    private int length;
    private ImageView resume;
    private SeekBar seekBar;
    private Handler handler;
    private Runnable runnable;
    private ImageView favorite;
    private TextView description;


    @Override
    public void onStop() {
        super.onStop();
        mediaPlayer.stop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song, container, false);

        songId = getContext().getSharedPreferences("song", Context.MODE_PRIVATE).getString("id", "none");
        mediaPlayer = new MediaPlayer();
        isplaying = false;
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        //Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,1);


        songname = view.findViewById(R.id.songName);
        play = view.findViewById(R.id.play);
        pause = view.findViewById(R.id.pause);
        resume = view.findViewById(R.id.replay);
        seekBar = view.findViewById(R.id.seek);
        favorite = view.findViewById(R.id.favorite);
        handler = new Handler();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b){
                    mediaPlayer.seekTo(i);
                    seekBar.setProgress(i);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playsong();

            }
        });

            pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    play.setVisibility(View.VISIBLE);
                    pause.setVisibility(View.GONE);
                    mediaPlayer.pause();
                    length = mediaPlayer.getCurrentPosition();
                }
            });

            resume.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
                }
            });

        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (favorite.getTag().equals("favorite")) {


                    DocumentReference doc = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .collection("favorites").document(songId);
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", songId);
                    doc.set(map);
                    Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                } else {
                    DocumentReference doc = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .collection("favorites").document(songId);
                    doc.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext(), "Deleted from favorites", Toast.LENGTH_SHORT).show();


                        }
                    });
                }
            }
                                    });












        songinfo();
        isFavorite();



        return view;
    }

    private void isFavorite() {
        DocumentReference reference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("favorites").document(songId);

        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {

                    favorite.setImageResource(R.drawable.ic_favorited);
                    favorite.setTag("favorited");

                } else {
                    favorite.setImageResource(R.drawable.ic_favorite);
                    favorite.setTag("favorite");
                }
            }
        });
    }


    public void updateSeekBar() {

            int mCurrentPosition = mediaPlayer.getCurrentPosition();
            seekBar.setProgress(mCurrentPosition);

            runnable = new Runnable() {
                @Override
                public void run() {
                    updateSeekBar();
                }
            };

            handler.postDelayed(runnable, 1000);

    }


    private void playsong() {


            play.setVisibility(View.GONE);
            pause.setVisibility(View.VISIBLE);
            if (length == 0) {
                Toast.makeText(getActivity(), "tihi", Toast.LENGTH_SHORT).show();
                try {
                    mediaPlayer.setDataSource(getContext(), downloadurl);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.prepareAsync();

                    // mediaPlayer.setDataSource(downloadurl.toString());
                    // mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        seekBar.setMax(mediaPlayer.getDuration());
                        mediaPlayer.start();
                        updateSeekBar();
                        mediaPlayer.setLooping(true);
                    }

                });
                mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

                    }
                });
            } else
                mediaPlayer.seekTo(length);
            mediaPlayer.start();
        }


















    private void songinfo() {

        FirebaseFirestore.getInstance().collection("songs").document(songId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (getContext() == null){
                    return;
                }
                Song song = value.toObject(Song.class);
                songname.setText(song.getName());
                id = song.getId();

                FirebaseStorage.getInstance().getReference().child("songs/").child(song.getName() + ".mp3")
                        .getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                downloadurl = task.getResult();
                            }
                        });







            }
        });
    }
}