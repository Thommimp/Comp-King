package com.example.compking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.compking.model.Song;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SongActivity extends AppCompatActivity {
    public String songId;
    private String songName;
    private ImageView play;
    private MediaPlayer mediaPlayer;
    private Uri downloadurl;
    private String id;
    private ImageView pause;
    private int length;
    private ImageView resume;
    private SeekBar seekBar;
    private Handler handler;
    private Runnable runnable;
    private ImageView favorite;
    private TextView description;
    private TextView timestart;
    private TextView timeend;
    private SeekBar seekbpm;
    private TextView bpm;
    private int speed;
    private MediaSession mSession;
    private int ibpm;
    private String songurl;
    private String descriptiont;
    private TextView bpm2;





    @Override
    protected void onDestroy() {
        mediaPlayer.pause();
        mediaPlayer.release();
        mediaPlayer = null;
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D4AF37")));


        //songId = getApplication().getSharedPreferences("song", Context.MODE_PRIVATE).getString("id", "none");
        Intent intent = getIntent();
        songId = intent.getStringExtra("id");
        songName = intent.getStringExtra("name");
        songurl = intent.getStringExtra("downloadurl");
        ibpm = intent.getIntExtra("bpm", 100);
        descriptiont = intent.getStringExtra("description");



        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        //Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,1);



        play = findViewById(R.id.play);
        pause = findViewById(R.id.pause);
        resume = findViewById(R.id.replay);
        seekBar = findViewById(R.id.seek);
        favorite = findViewById(R.id.favorite);
        handler = new Handler();
        description = findViewById(R.id.description);
        timeend = findViewById(R.id.timerend);
        timestart = findViewById(R.id.timerstart);
        seekbpm = findViewById(R.id.seekbpm);
        bpm = findViewById(R.id.bpmtxt);
        bpm2 = findViewById(R.id.bpm2txt);

        getSupportActionBar().setTitle(songName.substring(0,1).toUpperCase() + songName.substring(1));

        seekbpm.setMax(125);
        seekbpm.setMin(75);
        seekbpm.setProgress(100);
        bpm.setText(ibpm + " BPM");
        description.setText(descriptiont.substring(0,1).toUpperCase() + descriptiont.substring(1));
        bpm2.setText(ibpm + " BPM");













        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b){
                    seekBar.setProgress(i);
                    mediaPlayer.pause();
                    timestart.setText(String.valueOf(seekBar.getProgress() / 1000));
                    timeend.setText(String.valueOf((seekBar.getMax() - seekBar.getProgress()) / 1000));
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
                mediaPlayer.start();

            }
        });


        seekbpm.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    double number = ibpm * (i/100f);
                    int f = (int)number;
                    bpm.setText(f + " BPM");
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
                play.setVisibility(View.INVISIBLE);
                pause.setVisibility(View.VISIBLE);

                if (length == 0) {
                    //playsong();

                } else {
                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
                }




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
                    map.put("name", songName);
                    doc.set(map);

                    Toast.makeText(SongActivity.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                } else {
                    DocumentReference doc = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .collection("favorites").document(songId);
                    doc.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(SongActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();


                        }
                    });
                }
            }
        });













        //songinfo();
        isFavorite();






    }







    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
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
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(seekbpm.getProgress() / 100f));
                int mCurrentPosition = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(mCurrentPosition);
                int secounds = mediaPlayer.getDuration() - mCurrentPosition;
                timestart.setText(String.valueOf(mCurrentPosition / 1000));
                timeend.setText(String.valueOf(secounds / 1000));


                runnable = new Runnable() {
                    @Override
                    public void run() {
                        updateSeekBar();
                    }
                };

                handler.postDelayed(runnable, 1000);
            } else {
                int mCurrentPosition = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(mCurrentPosition);
                int secounds = mediaPlayer.getDuration() - mCurrentPosition;
                timestart.setText(String.valueOf(mCurrentPosition / 1000));
                timeend.setText(String.valueOf(secounds / 1000));


                runnable = new Runnable() {
                    @Override
                    public void run() {
                        updateSeekBar();
                    }
                };

                handler.postDelayed(runnable, 1000);

            }

        }
    }


    private void playsong() {


        play.setVisibility(View.INVISIBLE);
        pause.setVisibility(View.VISIBLE);
        if (length != 0) {
            mediaPlayer.seekTo(seekBar.getProgress());
            mediaPlayer.start();
            Toast.makeText(this, "hey", Toast.LENGTH_SHORT).show();


        } else {



            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(getApplication(), Uri.parse(songurl));
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepareAsync();

            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    seekBar.setMax(mediaPlayer.getDuration());
                    updateSeekBar();
                    mediaPlayer.seekTo(seekBar.getProgress());
                    mediaPlayer.start();
                    mediaPlayer.setLooping(true);


                }

            });
            mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

                }
            });
        }

    }






   //private void songinfo() {

   //    FirebaseFirestore.getInstance().collection("songs").document(songId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
   //        @Override
   //        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
   //            Song song = task.getResult().toObject(Song.class);
   //            description.setText(song.getDescription());
   //            ibpm = song.getBpm();

   //            seekbpm.setMax(125);
   //            seekbpm.setMin(75);
   //            seekbpm.setProgress(100);
   //            bpm.setText(song.getBpm() + " BPM");
   //            downloadurl = Uri.parse(song.getDownloadurl());


   //            //FirebaseStorage.getInstance().getReference().child("songs/").child(songName + ".mp3")
   //            //        .getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
   //            //            @Override
   //            //            public void onComplete(@NonNull Task<Uri> task) {
   //            //                downloadurl = task.getResult();
   //            //            }
   //            //        });

   //        }
   //    });
   //}

}