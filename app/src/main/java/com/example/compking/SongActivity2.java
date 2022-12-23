package com.example.compking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.Notification;
import android.app.PendingIntent;
import android.widget.RemoteViews;

public class SongActivity2 extends AppCompatActivity {

    private static final String TAG = "SongActivity2";


    private String songId;
    private ImageView mPlayPauseButton;
    private MediaPlayer mMediaPlayer;
    private String songurl;
    private SeekBar seekBar;
    private Runnable runnable;
    private Handler handler;
    private TextView timestart;
    private TextView timeend;
    private ImageView favorite;
    private String songName;
    private int ibpm;
    private String descriptiont;
    private TextView bpm2;
    private TextView bpm;
    private TextView description;



    private MediaControllerCompat mController;
    private MediaControllerCompat.TransportControls mControllerTransportControls;
    private MediaControllerCompat.Callback mControllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);

            switch (state.getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                    mPlayPauseButton.setImageResource(R.drawable.ic_pause);
                    break;
                case PlaybackStateCompat.STATE_PAUSED:
                    mPlayPauseButton.setImageResource(R.drawable.ic_play);
                    break;
                case PlaybackStateCompat.STATE_STOPPED:
                    mPlayPauseButton.setImageResource(R.drawable.ic_play);
                    break;
            }
        }
    };
    private PlaybackStateCompat.Builder mPBuilder;
    private MediaSessionCompat mSession;
    private class MediaSessionCallback extends MediaSessionCompat.Callback implements
            AudioManager.OnAudioFocusChangeListener {
        private Context mContext;
        private AudioManager mAudioManager;
        private IntentFilter mNoisyIntentFilter;
        private AudioBecommingNoisy mAudioBecommingNoisy;













        public MediaSessionCallback(Context context) {
            super();

            mContext = context;
            mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            mAudioBecommingNoisy = new AudioBecommingNoisy();
            mNoisyIntentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);


        }

        private class AudioBecommingNoisy extends BroadcastReceiver {
            @Override
            public void onReceive(Context context, Intent intent) {
                mediaPause();
            }
        }

        @Override
        public void onPlay() {
            super.onPlay();

            mediaPlay();
        }

        @Override
        public void onPause() {
            super.onPause();

            mediaPause();
        }

       @Override
              public void onStop() {
              super.onStop();

              //releaseResources();
          }



       private void releaseResources() {
           mSession.setActive(false);
           if(mMediaPlayer != null) {
               mMediaPlayer.stop();
               mMediaPlayer.reset();
               mMediaPlayer.release();
               mMediaPlayer = null;
               mSession.release();

           }

       }

        private void showNotification() {
            // Create a RemoteViews to customize the notification layout
            RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_layout);

            // Set the play/pause button on the notification layout
            if (mMediaPlayer.isPlaying()) {
                notificationLayout.setImageViewResource(R.id.play_pause_button, R.drawable.ic_pause);
            } else {
                notificationLayout.setImageViewResource(R.id.play_pause_button, R.drawable.ic_play);
            }

            // Set the song name and artist name on the notification layout
            notificationLayout.setTextViewText(R.id.song_name, songName);
            //notificationLayout.setTextViewText(R.id.artist_name, "Artist Name");

            // Set the onClickPendingIntent for the play/pause button
            PendingIntent playPauseIntent = PendingIntent.getBroadcast(this, 0,
                    new Intent(ACTION_PLAY_PAUSE), PendingIntent.FLAG_UPDATE_CURRENT);
            notificationLayout.setOnClickPendingIntent(R.id.play_pause_button, playPauseIntent);

            // Set the onClickPendingIntent for the close button
            PendingIntent closeIntent = PendingIntent.getBroadcast(this, 0,
                    new Intent(ACTION_CLOSE), PendingIntent.FLAG_UPDATE_CURRENT);
            //notificationLayout.setOnClickPendingIntent(R.id.close_button, closeIntent);

            // Build the notification
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_music)
                    .setContent(notificationLayout)
                    .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, SongActivity2.class), 0))
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .build();

            // Show the notification
            NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification);
        }

        private void mediaPlay() {
            registerReceiver(mAudioBecommingNoisy, mNoisyIntentFilter);
            int requestAudioFocusResult = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if(requestAudioFocusResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mSession.setActive(true);
                mPBuilder.setActions(PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_STOP);
                mPBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                        mMediaPlayer.getCurrentPosition(), 1.0f, SystemClock.elapsedRealtime());
                mSession.setPlaybackState(mPBuilder.build());
                mMediaPlayer.start();
                //updateSeekBar();
            }
        }


        private void mediaPause() {
            mMediaPlayer.pause();
            mPBuilder.setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_STOP);
            mPBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mMediaPlayer.getCurrentPosition(), 1.0f, SystemClock.elapsedRealtime());
            mSession.setPlaybackState(mPBuilder.build());
            mAudioManager.abandonAudioFocus(this);
            unregisterReceiver(mAudioBecommingNoisy);
        }





        //@Override
        //public void onCompletion(MediaPlayer mediaPlayer) {
        //    //mPBuilder.setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_STOP);
        //    //mPBuilder.setState(PlaybackStateCompat.STATE_STOPPED,
        //    //        mMediaPlayer.getCurrentPosition(), 1.0f, SystemClock.elapsedRealtime());
        //    //mSession.setPlaybackState(mPBuilder.build());
        //}

        @Override
        public void onAudioFocusChange(int audioFocusChanged) {
            switch (audioFocusChanged) {
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    mediaPause();
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    mediaPlay();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    mediaPause();
                    break;
            }
        }

       //public void updateSeekBar() {
       //

       //    int mCurrentPosition = mMediaPlayer.getCurrentPosition();
       //    seekBar.setProgress(mCurrentPosition);
       //    int secounds = mMediaPlayer.getDuration() - mCurrentPosition;
       //    timestart.setText(String.valueOf(mCurrentPosition / 1000));
       //    timeend.setText(String.valueOf(secounds / 1000));


       //    runnable = new Runnable() {
       //        @Override
       //        public void run() {
       //            updateSeekBar();
       //        }
       //    };

       //    handler.postDelayed(runnable, 1000);



       //}


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song2);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D4AF37")));



        Intent intent = getIntent();
        songId = intent.getStringExtra("id");
        songurl = intent.getStringExtra("downloadurl");
        songName = intent.getStringExtra("name");
        ibpm = intent.getIntExtra("bpm", 100);
        descriptiont = intent.getStringExtra("description");
        bpm = findViewById(R.id.bpmtxt);
        bpm2 = findViewById(R.id.bpm2txt);

        getSupportActionBar().setTitle(songName.substring(0,1).toUpperCase() + songName.substring(1));



        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), Uri.parse(songurl));
            mMediaPlayer.prepare();
        } catch (IOException e) {
            Log.e(TAG, "Error setting data source", e);
        }
       mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
           @Override
           public void onPrepared(MediaPlayer mediaPlayer) {
               seekBar.setMax(mMediaPlayer.getDuration());
               updateSeekBar();
               mMediaPlayer.setLooping(true);
           }
       });
        handler = new Handler();



        mPlayPauseButton = findViewById(R.id.play);
        seekBar = findViewById(R.id.seek);
        timestart = findViewById(R.id.timerstart);
        timeend = findViewById(R.id.timerend);
        favorite = findViewById(R.id.favorite);
        description = findViewById(R.id.description);

        bpm.setText(ibpm + " BPM");
        description.setText(descriptiont.substring(0,1).toUpperCase() + descriptiont.substring(1));
        bpm2.setText(ibpm + " BPM");





        mSession = new MediaSessionCompat(this, TAG);
        mSession.setCallback(new MediaSessionCallback(this));
        mPBuilder = new PlaybackStateCompat.Builder();
        mController = new MediaControllerCompat(this, mSession);
        mControllerTransportControls = mController.getTransportControls();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b){
                    seekBar.setProgress(i);
                    mMediaPlayer.pause();
                    timestart.setText(String.valueOf(seekBar.getProgress() / 1000));
                    timeend.setText(String.valueOf((seekBar.getMax() - seekBar.getProgress()) / 1000));
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMediaPlayer.seekTo(seekBar.getProgress());
                mMediaPlayer.start();

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

                   Toast.makeText(SongActivity2.this, "Added to favorites", Toast.LENGTH_SHORT).show();
               } else {
                   DocumentReference doc = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                           .collection("favorites").document(songId);
                   doc.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           Toast.makeText(SongActivity2.this, "Removed from favorites", Toast.LENGTH_SHORT).show();


                       }
                   });
               }
           }
       });

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


       int mCurrentPosition = mMediaPlayer.getCurrentPosition();
               seekBar.setProgress(mCurrentPosition);
               int secounds = mMediaPlayer.getDuration() - mCurrentPosition;
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

    public void playPauseClick(View view) {
        if(mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
            mControllerTransportControls.pause();
        } else if (mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PAUSED ||
                mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_STOPPED ||
                mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_NONE) {
            mControllerTransportControls.play();
        }

    }

    public void replayclick(View view ) {
        mMediaPlayer.seekTo(0);
        mControllerTransportControls.play();
    }




    @Override
     protected void onStop() {




        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }

        if (mSession != null) {
            mSession.setActive(false);
        }

        // mController.unregisterCallback(mControllerCallback);
        // if(mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING ||
        //         mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PAUSED) {
        //     mControllerTransportControls.stop();
        // }

         super.onStop();
     }

    @Override
    protected void onStart() {
        super.onStart();

        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            // Initialize the media player and start playback
        }

        mController.registerCallback(mControllerCallback);
        mPBuilder.setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mSession.setPlaybackState(mPBuilder.build());
    }

    @Override
    protected void onPause() {

        if(mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
            mControllerTransportControls.pause();
        }
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        mSession.release();

        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        if (mSession != null) {
            mSession.release();
            mSession = null;
        }

        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler = null;
        }



        super.onDestroy();


    }

}