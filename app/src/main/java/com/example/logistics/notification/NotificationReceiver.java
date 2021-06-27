package com.example.logistics.notification;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.logistics.MainActivity;
import com.example.logistics.R;
import com.example.logistics.fragment.DriverFragment;

import static androidx.core.content.ContextCompat.getSystemService;

public class NotificationReceiver extends BroadcastReceiver {

    private NotificationManagerCompat notificationManager ;
    private String NotificationText;

    public void setNotificationText(String notificationText){
        this.NotificationText = notificationText;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPref = context.getSharedPreferences("notificationTransport", Context.MODE_PRIVATE);
        notificationManager = NotificationManagerCompat.from(context);

        String message = sharedPref.getString("notificationMessage", "default");
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.putExtra("companyId", sharedPref.getInt("companyId", -1));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(),
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        createNotificationChannel();
        Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.marker_red);
        NotificationCompat.Builder noti = new NotificationCompat.Builder(context, "1")
                .setContentTitle("Transport to do")
                .setContentText(message)
                .setSmallIcon(R.drawable.blue_marker)
                .setLargeIcon(drawableToBitmap(unwrappedDrawable))
                .setContentIntent(pendingIntent);
        notificationManager.notify(5, noti.build());
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = ("communications");
            String description = ("Comunications from Logistics");
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }
    }
}
