package v;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.text.Html;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static v.k.INTERNAL_DIR;

public class Alarm extends BroadcastReceiver {
    final public static String FOR_SP_QUOTE_NOT = "spQuoteNotification";
    final public static String FOR_DAILY_NECTAR_NOT = "dailyNectarNotification";
    final public static String FOR_DAILY_NECTAR_FETCH = "fetchDailyNectar";
    final public static int SP_QUOTE_NOT_ID = 0;
    final public static int DAILY_NECTAR_NOT_ID = 1;

    @Override
    public void onReceive(final Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "brsm:Alarm");
        wl.acquire();
        (new Thread(() -> k.fetchDailyNectar(context, true))).start();
        //Get an instance of NotificationManager//
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        Toast.makeText(context,intent.getExtras().getString("for"),Toast.LENGTH_LONG).show();
        switch (intent.getExtras().getString("for")) {
            case FOR_DAILY_NECTAR_NOT:
                if (!k.prefs.bsrmNotUri.isEmpty()) soundUri = Uri.parse(k.prefs.bsrmNotUri);
                String dailyNectar = Html.fromHtml(k.getDailyNectar(context).replace("Daily Nectar:\n\n", " ")).toString();
                if (k.prefs.dailyNectarNotification != 0 && !dailyNectar.trim().equals("")) {
                    Intent notIntent = new Intent(context, k.class);
                    notIntent.setAction("showDailyNectar");
                    notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendInt = PendingIntent.getActivity(context, DAILY_NECTAR_NOT_ID,
                            notIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    Notification.Builder mBuilder =
                            new Notification.Builder(context)
                                    .setContentIntent(pendInt)
                                    .setSmallIcon(R.mipmap.ic_launcher_round)
                                    .setContentTitle("Daily Nectar")
                                    .setContentText(dailyNectar)
                                    .setPriority(Notification.PRIORITY_HIGH)
                                    .setDefaults(Notification.DEFAULT_VIBRATE)
                                    .addAction(R.drawable.send, "Share", PendingIntent.getActivity(context, 0,
                                            new Intent().setAction(Intent.ACTION_SEND).setClass(context, receiver.class).putExtra(Intent.EXTRA_TEXT,
                                                    Html.fromHtml(dailyNectar) + "\n\n" + "-His Holiness Bhakti Rasamrita Swami\n\n" + context.getString(R.string.app_url)).setType("text/html"), 0))
                                    .setWhen(System.currentTimeMillis())
                                    .setStyle(new Notification.BigTextStyle().bigText(dailyNectar));
                    if (k.prefs.dailyNectarNotification == 2)
                        mBuilder.setSound(soundUri);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel("bsrm", "Daily Nectar", NotificationManager.IMPORTANCE_DEFAULT);
                        channel.setDescription("Daily Nectar");
                        channel.enableLights(true);
                        mNotificationManager.createNotificationChannel(channel);
                        mBuilder.setChannelId("bsrm");
                    }
                    mNotificationManager.notify(DAILY_NECTAR_NOT_ID, mBuilder.build());
                }
                break;
            case FOR_SP_QUOTE_NOT:
//                Toast.makeText(context,intent.getExtras().getString("for")+" Hari Hari",Toast.LENGTH_LONG).show();
                if (!k.prefs.spNotUri.isEmpty()) soundUri = Uri.parse(k.prefs.spNotUri);
                String today = android.text.format.DateFormat.format("MMMM-dd", new Date()).toString();
                final File file = new File(context.getFilesDir().getPath() + '/' + today.replace('-', '_') + ".png");
                if (file.exists()) {
//                    Toast.makeText(context,intent.getExtras().getString("for")+" Hari Hari ...",Toast.LENGTH_LONG).show();
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                    Intent notIntent1 = new Intent(context, sQuote.class);
                    notIntent1.putExtra("action", "show_sp_quote");
                    notIntent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendInt1 = PendingIntent.getActivity(context, SP_QUOTE_NOT_ID,
                            notIntent1, PendingIntent.FLAG_UPDATE_CURRENT);

                    Notification.Builder mBuilder1 =
                            new Notification.Builder(context)
                                    .setContentIntent(pendInt1)
                                    .setSmallIcon(R.mipmap.ic_launcher_round)
                                    .setContentTitle("Srila Prabhupada Today")
                                    .setLargeIcon(bitmap)
                                    .setPriority(Notification.PRIORITY_HIGH)
                                    .setDefaults(Notification.DEFAULT_VIBRATE)
                                    .setStyle(new Notification.BigPictureStyle().bigPicture(bitmap));
                    if (k.prefs.SPQuoteNotification == 2)
                        mBuilder1.setSound(soundUri);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mBuilder1.setVisibility(Notification.VISIBILITY_PUBLIC);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel("sp", "Srila Prabhupada Today", NotificationManager.IMPORTANCE_DEFAULT);
                        channel.setDescription("Srila Prabhupada Today");
                        channel.enableLights(true);
                        mNotificationManager.createNotificationChannel(channel);
                        mBuilder1.setChannelId("sp");
                    }
                    mNotificationManager.notify(SP_QUOTE_NOT_ID, mBuilder1.build());
                }
                break;
        }
        wl.release();
    }

    ArrayList<PendingIntent> pendingIntents = new ArrayList<>();

    public void setAlarm(Context context, String setFor, int requestCode, int time) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Alarm.class);
        intent.putExtra("for", setFor);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar setTime = Calendar.getInstance();
        setTime.setTimeInMillis(System.currentTimeMillis());
        setTime.set(Calendar.HOUR_OF_DAY, time / 60);
        setTime.set(Calendar.MINUTE, time % 60);
        setTime.set(Calendar.SECOND, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, setTime.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent);
        if (pendingIntents.isEmpty())
            for (int i = 0; i < 5; i++)
                pendingIntents.add(pendingIntent);
        pendingIntents.set(requestCode, pendingIntent);
    }

    public void setAlarm(Context context) {
        File file = new File(INTERNAL_DIR, "prefs");
        if (k.audio.isEmpty()) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(context.openFileInput(file.getName())));
                String line;
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                line = stringBuilder.toString();
                line = line.replaceAll(",", " , ");
                String[] list = line.split(",");
                k.prefs.dailyNectarNotification = Integer.parseInt(list[0].trim());
                k.prefs.dailyNectarTime = Integer.parseInt(list[1].trim());
                k.prefs.SPQuoteNotification = Integer.parseInt(list[2].trim());
                k.prefs.spQuoteTime = Integer.parseInt(list[3].trim());
//            Toast.makeText(context, k.prefs.spQuoteTime/60+""+k.prefs.spQuoteTime%60,Toast.LENGTH_LONG).show();
                if (list.length > 17) {
                    k.prefs.bsrmNotUri = list[16].trim();
                    k.prefs.spNotUri = list[17].trim();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        setAlarm(context, FOR_SP_QUOTE_NOT, SP_QUOTE_NOT_ID, k.prefs.spQuoteTime);
        setAlarm(context, FOR_DAILY_NECTAR_NOT, DAILY_NECTAR_NOT_ID, k.prefs.dailyNectarTime);
        setAlarm(context, FOR_DAILY_NECTAR_FETCH, 2, 120);
        setAlarm(context, FOR_DAILY_NECTAR_FETCH, 3, 240);
        setAlarm(context, FOR_DAILY_NECTAR_FETCH, 4, 480);
        if (k.prefs.dailyNectarNotification == 0)
            CancelAlarm(context, DAILY_NECTAR_NOT_ID);
        if (k.prefs.SPQuoteNotification == 0)
            CancelAlarm(context, SP_QUOTE_NOT_ID);
    }

    public void CancelAlarm(Context context, int requestCode) {
        if (pendingIntents.get(requestCode) != null) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntents.get(requestCode));
        }
    }
}
