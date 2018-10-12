package com.firebase.contactsapp.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.firebase.contactsapp.MainActivity;
import com.firebase.contactsapp.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by KSTL on 16-03-2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService
{
    private static final String TAG = "MyFirebaseMsgService";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        //Calling method to generate notification
        //sendNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("shipmentId"));

        Map<String, String> data = remoteMessage.getData();
        sendNotification(data, remoteMessage.getData().get("title"), remoteMessage.getData().get("text"));
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(Map<String, String> data, String title, String shipmentId) {

        Intent launchIntent;
        PendingIntent pendingIntent;
       /* if(messageBody.equalsIgnoreCase("New Event Posted")){
            launchIntent = new Intent(this, DashBoardActivity.class);
            launchIntent.putExtra("TypeOfNotification", messageBody);
            pendingIntent = PendingIntent.getActivity(this, 0, launchIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
        }*//*else if(messageBody.contains("New Poll Available")){
            launchIntent = new Intent(this, PollsActvity.class);
            pendingIntent = PendingIntent.getActivity(this, 1, launchIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
        }*//*else{
            launchIntent = new Intent(this, DashBoardActivity.class);
            launchIntent.putExtra("FromNotification", true);
            pendingIntent = PendingIntent.getActivity(this, 1, launchIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
        }*/

        launchIntent = new Intent(this, MainActivity.class);
        launchIntent.putExtra("Shipment_ID", shipmentId);
        pendingIntent = PendingIntent.getActivity(this, 0, launchIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        //Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        notificationBuilder.setLargeIcon(drawableToBitmap(getDrawable(R.drawable.google_button)));
            notificationBuilder.setSmallIcon(R.drawable.ic_google);
        } else {
            notificationBuilder.setSmallIcon(R.drawable.ic_plus);
        }

        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(shipmentId);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setColor(Color.parseColor("#44BDC8"));
        notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
        notificationBuilder.setVibrate(new long[] { 1000, 1000});
        notificationBuilder.setLights(Color.WHITE, 3000, 3000);
        notificationBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        notificationBuilder.setContentIntent(pendingIntent);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
        turnScreenOn(1000, getApplicationContext());

    }

    public Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static void turnScreenOn(int sec, final Context context)
    {
        final int seconds = sec;

        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();

        if( !isScreenOn )
        {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE,"MyLock");
            wl.acquire(seconds*1000);
            PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyCpuLock");
            wl_cpu.acquire(seconds*1000);
        }
    }


}
