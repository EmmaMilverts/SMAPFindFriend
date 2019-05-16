package com.emmamilverts.friendfinder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.emmamilverts.friendfinder.FriendList.FriendListFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "mFirebaseIIDService";
    FirebaseAuth mAuth;
    private final String ADMIN_CHANNEL_ID = "admin_channel";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String type = remoteMessage.getData().get("notificationType");

        if(type.equals(FriendListFragment.NOTIFICATION_TYPE_SEND_LOCATION)){
            createLocationNotification(remoteMessage);
        }

        if(type.equals(FriendListFragment.NOTIFICATION_TYPE_REQUEST_LOCATION)){
            createNotificationRequestNotification(remoteMessage);
        }
    }

    private void createNotificationRequestNotification(RemoteMessage remoteMessage) {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationID = new Random().nextInt(3000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            setupChannels(notificationManager);
        }

        String senderId = remoteMessage.getData().get("senderid");
        Intent notificationIntent = new Intent(this, MainActivity.class).setAction(senderId);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, Intent.FILL_IN_ACTION);

        Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(remoteMessage.getData().get("Username"))
                .setContentText("Requests your location")
                .setAutoCancel(true)
                .setSound(notificationUri)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_notifications_black_24dp, "Send location", pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            notificationBuilder.setColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        notificationManager.notify(notificationID, notificationBuilder.build());

    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth != null)
        {
            Log.d("NEW_TOKEN",s);
        }
    }

    public void createLocationNotification(RemoteMessage remoteMessage){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=" + remoteMessage.getData().get("Coordinates")));
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationID = new Random().nextInt(3000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            setupChannels(notificationManager);
        }

        browserIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, browserIntent, PendingIntent.FLAG_ONE_SHOT);

        Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(remoteMessage.getData().get("Username"))
                .setContentText("Click here to see the location")
                .setAutoCancel(true)
                .setSound(notificationUri)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            notificationBuilder.setColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        notificationManager.notify(notificationID, notificationBuilder.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(NotificationManager notificationManager)
    {
        CharSequence adminChannelName = "New Notification";
        String adminChannelDescription = "DeviceToDeviceNotification";
        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.BLUE);
        adminChannel.enableVibration(true);
        if (notificationManager != null)
        {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }
}
