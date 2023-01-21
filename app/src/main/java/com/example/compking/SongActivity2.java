package com.example.compking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.browse.MediaBrowser;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.service.media.MediaBrowserService;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
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
import java.util.List;
import java.util.Map;


import android.app.Notification;
import android.app.PendingIntent;
import android.widget.RemoteViews;

import javax.xml.transform.Result;

public class SongActivity2 extends AppCompatActivity {
    public static final String ACTION_PLAY_PAUSE = "com.example.compking.ACTION_PLAY_PAUSE";
    private static final String TAG = "SongActivity2";
    public static final String ACTION_CLOSE = "com.example.compking.ACTION_CLOSE";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "channel_01";




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
    private SeekBar seekbpm;






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
            //showPlayingNotification();
        }

        @Override
        public void onPause() {
            super.onPause();

            mediaPause();
        }

       @Override
              public void onStop() {
              super.onStop();


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




        //private void showPlayingNotification() {
        //    NotificationCompat.Builder builder = MediaStyleHelper.from(mContext, mSession);
        //    if( builder == null ) {
        //        return;
        //    }
//
//
        //    builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", MediaButtonReceiver.buildMediaButtonPendingIntent(mContext, PlaybackStateCompat.ACTION_PLAY_PAUSE)));
        //   builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
        //          .setShowActionsInCompactView(0)
        //          .setMediaSession(mSession.getSessionToken()));
//
//
//
        //   builder.setSmallIcon(R.mipmap.ic_launcher);
        //   builder.setCategory(NotificationCompat.CATEGORY_TRANSPORT);
        //   NotificationManagerCompat.from(mContext).notify(1, builder.build());
        //}



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
        seekbpm = findViewById(R.id.seekbpm);



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

        seekbpm.setMax(125);
        seekbpm.setMin(75);
        seekbpm.setProgress(100);





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

        seekbpm.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                double number = ibpm * (i/100f);
                int f = (int)number;
                bpm.setText(f + " BPM");
                seekBar.setProgress(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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

    private MediaBrowserCompat.ConnectionCallback mConnectionCallback =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    // Code to handle connected event
                }

                @Override
                public void onConnectionSuspended() {
                    // Code to handle connection suspended event
                }

                @Override
                public void onConnectionFailed() {
                    // Code to handle connection failed event
                }
            };









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
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.setPlaybackParams(mMediaPlayer.getPlaybackParams().setSpeed(seekbpm.getProgress() / 100f));
        }


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