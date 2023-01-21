package com.example.compking;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import java.io.IOException;
import java.util.List;

public class VideoPlayActivity extends AppCompatActivity {

    private BackgroundAudioService2 mBackgroundAudioService2;
    private boolean mIsBound = false;
    private String songUrl;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BackgroundAudioService2.LocalBinder binder = (BackgroundAudioService2.LocalBinder) service;
            mBackgroundAudioService2 = binder.getService();
            mIsBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBackgroundAudioService2 = null;
            mIsBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        Intent intent = new Intent(this, BackgroundAudioService2.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        songUrl = "https://firebasestorage.googleapis.com/v0/b/comp-king.appspot.com/o/songs%2Fstay.mp3?alt=media&token=1fc4dc1f-e401-4c4a-83eb-6df8975f095d";
    }

    public void playPauseClick(View view) {
        if (mIsBound) {
            mBackgroundAudioService2.setDataSource(songUrl);
            mBackgroundAudioService2.mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mBackgroundAudioService2.mMediaSessionCompat.getController().getTransportControls().play();
                }
            });



        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIsBound) {
            unbindService(mServiceConnection);
            mIsBound = false;
        }
    }




    }

