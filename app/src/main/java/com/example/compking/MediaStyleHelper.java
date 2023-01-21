package com.example.compking;


import android.app.Notification;
import android.app.NotificationChannel;


import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;


import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.session.MediaButtonReceiver;

public class MediaStyleHelper {
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "channel_01";
    //public static NotificationCompat.Builder from(
    //        Context context, MediaSessionCompat mediaSession) {
    //    int NOTIFICATION_ID = 234;
    //    MediaControllerCompat controller = mediaSession.getController();
    //    MediaMetadataCompat mediaMetadata = controller.getMetadata();
    //    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
    //    if (mediaMetadata != null) {
    //        builder
    //                .setContentTitle(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE))
    //                .setContentText(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST))
    //                .setSubText(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM))
    //                .setLargeIcon(mediaMetadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ART))
    //                .setContentIntent(controller.getSessionActivity())
    //                .setDeleteIntent(
    //                        MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP))
    //                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
    //        //NotificationManagerCompat.from(context.getApplicationContext()).notify(NOTIFICATION_ID, builder.build());
//
//
    //    }
    //    return builder;
//
    //}

    //public static NotificationCompat.Builder from(Context context, MediaSessionCompat mediaSession) {
    //    MediaControllerCompat controller = mediaSession.getController();
    //    MediaMetadataCompat mediaMetadata = controller.getMetadata();
    //    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
    //    if (mediaMetadata != null) {
    //    builder
    //            .setContentTitle(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE))
    //            .setContentText(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST))
    //            .setSubText(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM))
    //            .setLargeIcon(mediaMetadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART))
    //            .setContentIntent(controller.getSessionActivity())
    //            .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP))
    //            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
    //    }
    //    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    //        // Create a notification channel for Android Oreo and above
    //        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    //        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "channel_name", NotificationManager.IMPORTANCE_HIGH);
    //        notificationManager.createNotificationChannel(channel);
    //        builder.setChannelId(CHANNEL_ID);
    //    }
    //    return builder;
    //}

    public static Notification.Builder from(Context context, MediaSessionCompat mediaSession) {
        MediaControllerCompat controller = mediaSession.getController();
        MediaMetadataCompat mediaMetadata = controller.getMetadata();
        Notification.Builder builder = new Notification.Builder(context);
        builder
                .setContentTitle(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE))
                    .setContentText(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST))
                    .setSubText(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM))
                    .setLargeIcon(mediaMetadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART))
                    .setContentIntent(controller.getSessionActivity())
                    .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP))
                    .setVisibility(Notification.VISIBILITY_PUBLIC);

        return builder;

    }



}
