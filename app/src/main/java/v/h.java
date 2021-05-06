package v;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
import static v.k.REPEAT_NONE;
import static v.k.REPEAT_ONE;
import static v.k.audio;
import static v.k.bookmark;
import static v.k.getUrlToShare;
import static v.k.prefs;
import static v.k.volume;
import static v.k.writePlaylistToFile;

public class h extends Service implements SensorListener {
    IBinder musicBinder = new MusicBinder();
    public static MediaPlayer musicPlayer1;
    public static MusicPlayer musicPlayer;
    public static k.Audio nowPlaying = null;
    public int nowPlayingQueueInd = 0, shakeDelay = 500;
    ArrayList<Integer> Queue = new ArrayList<>();

    public h() {
    }

    public static AudioManager audioManager;
    SensorManager sensorManager;

    @Override
    public void onCreate() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, SensorManager.SENSOR_ACCELEROMETER, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBinder;
    }

    float x, y, last_x, last_y;
    static final int SHAKE_THRESHOLD = 1500, SENSOR_SENSITIVITY = 10;
    long lastUpdate;
    boolean shakeAction = true;

    @Override
    public void onSensorChanged(int sensor, float[] values) {
        if (!shakeAction || prefs.onShake == 0) return;
        if (sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null &&
                values[0] >= -SENSOR_SENSITIVITY && values[0] <= SENSOR_SENSITIVITY) return;//near
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms.
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
                x = values[SensorManager.DATA_X];
                y = values[SensorManager.DATA_Y];
                float speed = Math.abs(x + y - last_x - last_y) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    switch (prefs.onShake) {
                        case 1:
                            callBacks.playNext();
                            break;
                        case 2:
                            callBacks.playPause();
                            break;
                        case 3:
                            callBacks.playPauseChantWithSP();
                            break;
                    }
                    shakeAction = false;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            shakeAction = true;
                        }
                    }, shakeDelay);
                }
                last_x = x;
                last_y = y;
            }
        }
    }

    @Override
    public void onAccuracyChanged(int i, int i1) {

    }

    class MusicBinder extends Binder {
        h getService() {
            return h.this;
        }
    }

    public static CallBacks callBacks;

    public interface CallBacks {
        void setPlayPauseButtons();

        void setSeekBarProgress(long milliSec);

        ArrayList<Integer> getQueue();

        void download(ArrayList<Integer> downloadTasks);

        void playPause();

        void playNext();

        void playPrevious();

        void setLike(int ind);

        void shareSong(int ind);

        void stopChantWithSP();

        void playPauseChantWithSP();

        void adjustSPChantVol(boolean up);

        void showNoDataWarning();
    }

    public void instantiateMusicPlayer() {
        musicPlayer = new MusicPlayer();
    }

    public void startPlaying() {
        if (musicPlayer == null) musicPlayer = new MusicPlayer();
        else {
            musicPlayer.stop();
            musicPlayer.reset();
        }
        if (nowPlaying == null) {
            if (Queue.isEmpty())
                Queue = callBacks.getQueue();
            nowPlaying = audio.get(Queue.get(nowPlayingQueueInd));
            if (!isNetworkAvailable()) {
                for (int i = 0; i < Queue.size(); i++) {
                    nowPlaying = audio.get(Queue.get(0));
                    File file = new File(getFilesDir(), "bsrm_" + nowPlaying.id + ".mp3");
                    if (file.exists())
                        break;
                }
            }
        }
        nowPlayingQueueInd = Queue.indexOf(nowPlaying.index);
        if (nowPlayingQueueInd == -1) {
            Queue.clear();
            Queue.addAll(k.maharajas.get(nowPlaying.albumId).audioIndices);
            nowPlayingQueueInd = Queue.indexOf(nowPlaying.index);
        }
        File file = new File(getFilesDir(), "bsrm_" + nowPlaying.id + ".mp3");
        if (file.exists()) {
            try {
                musicPlayer.setDataSource(Uri.fromFile(file).toString());
                if (Shuffle) {
                    shuffleQueue();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (isNetworkAvailable()) {
            ArrayList<Integer> downloadQue = new ArrayList<>();
            downloadQue.add(nowPlaying.index);
            downloadQue.add(Queue.get((nowPlayingQueueInd + 1) % Queue.size()));
            callBacks.download(downloadQue);
        } else {
            if (!preventPlay)
                callBacks.showNoDataWarning();
            preventPlay = false;
        }
    }

    Random random = new Random();
    boolean Shuffle = false, preventPlay = false;
    int Repeat = 0;

    public void shuffleQueue() {
        for (int i = 0; i < Queue.size(); i++) {
            int temp = Queue.get(i), tempInd = random.nextInt(Queue.size());
            Queue.set(i, Queue.get(tempInd));
            Queue.set(tempInd, temp);
        }
    }

    public void createNotification() {
        if (nowPlaying == null)
            return;
        Context context = h.this;
        Intent notIntent = new Intent(context, k.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(context, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(context, 0,
                new Intent("com.mayank.vaishnavasongs.prev1").setClass(h.this, receiver.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(context, 0,
                new Intent("com.mayank.vaishnavasongs.playpause1").setClass(h.this, receiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 0,
                new Intent("com.mayank.vaishnavasongs.next1").setClass(h.this, receiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent likePendingIntent = PendingIntent.getBroadcast(context, 0,
                new Intent("com.mayank.vaishnavasongs.like1").setClass(h.this, receiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent sharePendingIntent = PendingIntent.getBroadcast(context, 0,
                new Intent("com.mayank.vaishnavasongs.share1").setClass(h.this, receiver.class), PendingIntent.FLAG_UPDATE_CURRENT);

        final String arte = "arte_" + nowPlaying.arteInd;
        Bitmap albumArte;
        File file = new File(getFilesDir(), arte);
        if (file.exists()) {
            albumArte = BitmapFactory.decodeFile(file.getPath());
        } else {
            albumArte = BitmapFactory.decodeResource(getResources(), R.drawable.haribol);
        }
        int playPauseBtn = android.R.drawable.ic_media_play;
        if (musicPlayer != null && musicPlayer.isPlaying())
            playPauseBtn = android.R.drawable.ic_media_pause;
        int likeBtn = R.drawable.thumb;
        if (k.favourites.get(nowPlaying.index))
            likeBtn = R.drawable.thumb_pink;

        Notification.Builder builder = new Notification.Builder(context);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(nowPlaying.title)
                .setContentText(k.maharajas.get(nowPlaying.albumId).name)
//         Add media control buttons that invoke intents in your media service
                .addAction(android.R.drawable.ic_media_previous, "Previous", prevPendingIntent) // #0
                .addAction(playPauseBtn, "Pause", pausePendingIntent)  // #1
                .addAction(android.R.drawable.ic_media_next, "Next", nextPendingIntent) // #2
                .addAction(likeBtn, "Like", likePendingIntent)
                .addAction(R.drawable.share, "Share", sharePendingIntent)
                .setLargeIcon(albumArte);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(Notification.VISIBILITY_PUBLIC)
                    // Apply the media style template
                    .setStyle(new Notification.MediaStyle()
                            .setShowActionsInCompactView(1 /* #1: pause button */))
                    .setColor(Color.rgb(216, 27, 96));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("player", "Music Controls", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Music Controls");
            channel.enableLights(false);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId("player");
        }
        startForeground(100, builder.build());
        if (!musicPlayer.isPlaying())
            stopForeground(false);
    }

    // ----------------helpers
    Handler handler = new Handler();

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean isWifiConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiInfo != null && wifiInfo.isConnected();
    }

    public class MusicPlayer extends MediaPlayer implements AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnErrorListener,
            MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {
        private static final int MAX_VOLUME = 99;
        private BecomingNoisyReceiver myNoisyAudioStreamReceiver = new BecomingNoisyReceiver();
        private IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        boolean isPreparing = false, isPrepared = false;
        long fileLength = 0, currentPos = 0;

        MusicPlayer() {
            this.setAudioStreamType(AudioManager.STREAM_MUSIC);
            this.setOnCompletionListener(this);
            this.setOnErrorListener(this);
            this.setOnPreparedListener(this);
        }

        @Override
        public void start() throws IllegalStateException {
            if (isPreparing)
                return;
            if (!preventPlay && audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN) != AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
                return;
            if (!myNoisyAudioStreamReceiver.isRegistered) {
                registerReceiver(myNoisyAudioStreamReceiver, intentFilter);
                myNoisyAudioStreamReceiver.isRegistered = true;
            }
            super.start();
            float v1 = (float) (1 - (Math.log(MAX_VOLUME - volume.get(nowPlaying.index)) / Math.log(MAX_VOLUME)));
            musicPlayer.setVolume(v1, v1);
            if (preventPlay) musicPlayer.setVolume(0, 0);
            final int pos = musicPlayer.getCurrentPosition();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (preventPlay) {
                        pause();
                        preventPlay = false;
                    } else if (musicPlayer.getCurrentPosition() < pos + 200) {
                        currentPos = musicPlayer.getCurrentPosition() + 600;
                        startPlaying();
                    }
                    volume.set(nowPlaying.index, volume.get(nowPlaying.index) >= MAX_VOLUME ? MAX_VOLUME - 1 : volume.get(nowPlaying.index));
                    float v = (float) (1 - (Math.log(MAX_VOLUME - volume.get(nowPlaying.index)) / Math.log(MAX_VOLUME)));
                    musicPlayer.setVolume(v, v);
                }
            }, 500);
            callBacks.setPlayPauseButtons();
        }

        @Override
        public void stop() throws IllegalStateException {
            if (isPreparing)
                return;
            if (myNoisyAudioStreamReceiver.isRegistered) {
                try {
                    unregisterReceiver(myNoisyAudioStreamReceiver);
                    myNoisyAudioStreamReceiver.isRegistered = false;
                } catch (Exception ignored) {
                }
            }
            abandonAudioFocus();
            super.stop();
            callBacks.setPlayPauseButtons();
        }

        void abandonAudioFocus() {
            audioManager.abandonAudioFocus(this);
        }

        @Override
        public void pause() throws IllegalStateException {
            if (isPreparing)
                return;
            if (myNoisyAudioStreamReceiver.isRegistered) {
                try {
                    unregisterReceiver(myNoisyAudioStreamReceiver);
                } catch (Exception ignored) {
                }
                myNoisyAudioStreamReceiver.isRegistered = false;
            }
            super.pause();
            callBacks.setPlayPauseButtons();
        }

        @Override
        public void seekTo(int msec) throws IllegalStateException {
            super.seekTo(msec);
            callBacks.setSeekBarProgress(msec);
        }

        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT) {
                pause();
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                start();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                audioManager.abandonAudioFocus(this);
                pause();
            }
            if (musicPlayer1 != null) {
                if (prefs.backgroundVolume == 0) {
                    callBacks.stopChantWithSP();
                } else {
                    final float volume = (float) (1 - (Math.log(MAX_VOLUME - prefs.backgroundVolume) / Math.log(MAX_VOLUME)));
                    musicPlayer1.setVolume(volume, volume);
                }
            }
        }

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            isPrepared = false;
            isPreparing = false;
            callBacks.setPlayPauseButtons();
            File file = new File(k.INTERNAL_DIR, "bsrm_" + nowPlaying.id + ".mp3");
            if (fileLength < file.length() || Repeat == REPEAT_ONE || mediaPlayer.getDuration() < 60000) { //restart same song
                currentPos = getCurrentPosition() + 500;
                stop();
                reset();
                try {
                    setDataSource(Uri.fromFile(file).toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (!Queue.isEmpty() && Repeat != REPEAT_NONE) {
                nowPlayingQueueInd = (nowPlayingQueueInd + 1) % Queue.size();
                nowPlaying = audio.get(Queue.get(nowPlayingQueueInd));
                startPlaying();
            } else if (Repeat != REPEAT_NONE) {
                nowPlaying = null;
                startPlaying();
            } else {
                stop();
                reset();
            }
        }

        @Override
        public void reset() {
            super.reset();
            isPrepared = false;
            isPreparing = false;
        }

        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
            isPrepared = false;
            isPreparing = false;
            musicPlayer = null;
            startPlaying();
            return true;
        }

        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            isPreparing = false;
            isPrepared = true;
            start();
            fileLength = (new File(getFilesDir(), "bsrm_" + nowPlaying.id + ".mp3")).length();
            if (getDuration() > currentPos)
                seekTo((int) currentPos - 500);
            currentPos = 0;
        }

        @Override
        public void setDataSource(String path) throws IOException, IllegalArgumentException, IllegalStateException, SecurityException {
            if (isPreparing)
                return;
            super.setDataSource(path);
            prepareAsync();
            isPreparing = true;
        }

        private class BecomingNoisyReceiver extends BroadcastReceiver {
            boolean isRegistered = false;

            @Override
            public void onReceive(Context context, Intent intent) {
                if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction()))
                    switch (becomingNoisy[prefs.becomingNoisyAction]) {
                        case "Pause":
                            pause();
                            abandonAudioFocus();
                            break;
                        case "Lower Volume":
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) - 3, 0);
                    }
//                    pause();
            }
        }
    }

    public static final String[] becomingNoisy = {"Pause", "Lower Volume", "Do Nothing"},
            onShake = {"Do Nothing ", "Change Song ", "Play/Pause ", "play/pause Prabhupada Chant "};

    @Override
    public void onDestroy() {
        if (!bookmark.isEmpty()) {
            String[] s = bookmark.split("\\$b");
            if (s[2].equals("t"))
                k.bookmarks.set(k.bookmarks.indexOf(bookmark), bookmark.replace(s[1], getUrlToShare(nowPlaying.index)));
        }
        writePlaylistToFile();
        if (musicPlayer != null && musicPlayer.isPlaying()) {
            startService(new Intent(this, h.class));
        } else
            super.onDestroy();
    }
}
