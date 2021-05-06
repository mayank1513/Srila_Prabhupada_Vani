package v;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Stack;

import v.ui.CustomWebView;
import v.ui.b;
import v.ui.d;
import v.ui.dragListView;
import v.ui.r;
import v.ui.t;

import static android.view.Gravity.CENTER_VERTICAL;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.view.View.inflate;
import static v.h.audioManager;
import static v.h.becomingNoisy;
import static v.h.musicPlayer;
import static v.h.musicPlayer1;
import static v.h.onShake;

public class k extends Activity implements h.CallBacks, b.CallBacks, t.Callbacks {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        STICKER_APP_AUTHORITY = getPackageName() + ".strprovider";
        super.onCreate(savedInstanceState);
        flg[0] = 1;
        for (int j = 1; j < filterOptions.length; j++) flg[j] = flg[j - 1] * 2;
        prefs.filterFlags = flg[flg.length - 1] - 1;
        dp10 = getResources().getDimensionPixelSize(R.dimen.dp10);
        dp = dp10 / 10;
        size.x = Resources.getSystem().getDisplayMetrics().widthPixels;
        size.y = Resources.getSystem().getDisplayMetrics().heightPixels;
        if (getStatusBarHeight() > 2.4 * dp10) size.y += getStatusBarHeight();
        dWidth = getResources().getDimensionPixelSize(size.x > 6.5 * dp10 || size.y > 6.5 * dp10 ? R.dimen.drawerWidth1 : R.dimen.drawerWidth);
        INTERNAL_DIR = getFilesDir();
        APP_NAME = getString(R.string.app_name);
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        setContentView(R.layout.k);

        View v = findViewById(R.id.s_bar);
        ViewGroup.LayoutParams p = v.getLayoutParams();
        p.height = getStatusBarHeight() - getResources().getDimensionPixelSize(R.dimen.status_bar_height);
        v.setLayoutParams(p);
//        if (prefs.showHk == 1) {
//            findViewById(R.id.hk__).setVisibility(VISIBLE);
//            h1 = 5 * dp10 + p.height;
//            size.y -= h1;
//        }
        findViewById(R.id.r).animate().scaleY(1).scaleX(1).alpha(1).setDuration(580).start();
        v.postDelayed(boot, 600);
    }

    NotificationManager notificationManager;
    private h mServ;
    private Intent playIntent = null;
    public boolean musicBound = false, eventHandled = false, bootComplete = false, wifiStatus = false;
    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            h.MusicBinder binder = (h.MusicBinder) service;
            mServ = binder.getService();
            musicBound = true;
            h.callBacks = k.this;
            readPreferencesFromFile();
            if (bootComplete && !eventHandled) {
                eventHandled = true;
                handleIntent(getIntent());
                if (musicPlayer != null)
                    setPlayPauseButtons();
                RunThread runThread = new RunThread();
                runThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadResources);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, h.class);
            startService(playIntent);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String appLinkAction = intent.getAction();
        Uri appLinkData = intent.getData();
        if (("showDailyNectar").equals(appLinkAction)) {
            ((TextView) findViewById(R.id.daily_nectar)).setText(Html.fromHtml(getDailyNectar(this)));
            findViewById(R.id.daily_nectar_container).setVisibility(VISIBLE);
            notificationManager.cancel(Alarm.DAILY_NECTAR_NOT_ID);
        } else if (("chant_with_prabhupada").equals(appLinkAction) && musicPlayer1 != null) {
            showChantWithSP();
        } else if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null) {
            if (appLinkData.toString().endsWith(".vpl") || appLinkData.toString().startsWith("content")) {
                try {
                    readPlaylistFromFile(new InputStreamReader(Objects.requireNonNull(getContentResolver().openInputStream(appLinkData))));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (audioIds.isEmpty()) {
                    dBuilder.setMessage("Not a Vaishnava Song Playlist (vpl) " +
                            "file corresponding to this app or playlist is empty").show();
                } else {
                    addToPlayList(drawerList, -1, -2, false);
                }
                return;
            }
            int[] linkData = getIndexFromUrl(appLinkData.toString());
            if (musicPlayer == null)
                mServ.instantiateMusicPlayer();
            musicPlayer.currentPos = linkData[1] * 1000;
            mServ.preventPlay = false;
            startPlaying(audio.get(linkData[0]));
            maharaja = h.nowPlaying.albumId;
        } else {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput("nowPlaying")));
                String line = reader.readLine();
                int[] np = {0, 0};
                if (line != null && !line.isEmpty()) {
                    try {
                        h.nowPlaying = audio.get(Integer.parseInt(line));
                    } catch (Exception e) {
                        try {
                            np = getIndexFromUrl(line);
                            h.nowPlaying = audio.get(np[0]);
                        } catch (Exception e1) {
                            h.nowPlaying = audio.get(0);
                        }
                    }
                    if (musicPlayer == null) {
                        mServ.preventPlay = true;
                        startPlaying();
                        musicPlayer.currentPos = np[1] * 1000;
                    }
                }
                reader.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    long startTime;
    public static Point size = new Point();
    InputMethodManager imm;
    public static int dp10, dp, dWidth, animDuration = 300, updateResH, maxArte = 0;
    final int RESULT_LOAD_IMAGE = 1, RESULT_BSRM_NOTIFICATION_TONE = 2, RESULT_SP_NOTIFICATION_TONE = 3;
    d drawer, rDrawer;
    dragListView wifiList, holderList, fsList;
    TextView percent, drawerName, tStartPos, tEndPos;
    ListView drawerList, list, fsMenu, searchList, storageList;
    Adapter dMenuAdapter, dMAdapter, plAdapter, mAdapter, sAdapter, eAdapter, plAudioAdapter, mAudioAdapter,
            favAdapter, authorAdapter, lyricsAdapter, lsAdapter, downloadManagerAdapter, storageAdapter,
            bookmarkAdapter, clipAdapter;
    static Adapter offlineAdapter, vbAdapter, arteAdapter;
    View btn, Preferences, albumHolder, fullscreen, drawerBtns[], searchBar, toolbar, updateRes, q, startPos, endPos, clipContainer, wifi;
    static View wifiBtns, wifiBtns_, wifi_transferring;
    static TextView wifiCount, wifi_progress, conn_to;
    static ProgressBar wifiProgressBar;
    FrameLayout waiting;
    t mainTL, holderTL, fullScreenTL, wifiTL;
    b bottomDrawer;
    EditText editText;
    String query_;
    static WifiManager wifiManager;
    int[] playPauseButtons;
    SeekBar seekBar, a, r, g, b;

    ProgressDialog mProgressDialog;
    DownloadTask downloadTask;
    boolean shouldShowProgressDialogue = true, opaqueBackground = false, lyricsReady = false, downloadManagerRunning = false, downloadingAudio = false;
    public static PowerManager.WakeLock mWakeLock;
    CustomWebView VedabaseWebView, NotesWebView, YouTubeWebView;
    r vedabaseContainer, ytContainer;
    ImageButton quickBall;
    static AlertDialog.Builder dBuilder;
    static ContentResolver contentResolver;

    Runnable boot = new Runnable() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void run() {
            startTime = System.currentTimeMillis();
            findViewById(R.id.r).animate().scaleY(1.05f).scaleX(1.05f).alpha(.9f).setDuration(3580).start();
            try {
                baseDate = simpleDateFormat1.parse("1950-01-01").getTime();
            } catch (ParseException ignored) {
            }

            mWakeLock = ((PowerManager) Objects.requireNonNull(getSystemService(Context.POWER_SERVICE))).
                    newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            contentResolver = getContentResolver();
            dBuilder = new AlertDialog.Builder(k.this);
            updateRes = findViewById(R.id.updating_resources);
            updateRes.animate().alpha(1).setDuration(0).start();
//            updateResH = updateRes.getMeasuredHeight();
            bottomDrawer = findViewById(R.id.bottom_drawer);
            strList = findViewById(R.id.stickers);
            strProgress = findViewById(R.id.strProgress);
            add_to_wa = findViewById(R.id.add_to_wa);
            fullscreen = findViewById(R.id.fullscreen_player);
            (startPos = findViewById(R.id.startPos)).setOnTouchListener(clipPos);
            (endPos = findViewById(R.id.endPos)).setOnTouchListener(clipPos);
            tStartPos = findViewById(R.id.tStartPos);
            tEndPos = findViewById(R.id.tEndPos);
            clipContainer = findViewById(R.id.clipContainer);
            hideWarning.run();
            wifi = findViewById(R.id.wifi);
            wifiBtns = findViewById(R.id.wifiBtns);
            wifiBtns_ = findViewById(R.id.wifiBtns_);
            wifi_transferring = findViewById(R.id.wifi_transferring);
            wifiCount = findViewById(R.id.wifiCount);
            wifi_progress = findViewById(R.id.wifi_progress);
            wifiProgressBar = findViewById(R.id.wifiProgressBar);
            conn_to = findViewById(R.id.conn_to);
            waiting = findViewById(R.id.waiting);
            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            mProgressDialog = new ProgressDialog(k.this);
            mProgressDialog.setMessage("Buffering");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            (a = findViewById(R.id.alpha)).setOnSeekBarChangeListener(setColor);
            (r = findViewById(R.id.red)).setOnSeekBarChangeListener(setColor);
            (g = findViewById(R.id.green)).setOnSeekBarChangeListener(setColor);
            (b = findViewById(R.id.blue)).setOnSeekBarChangeListener(setColor);
            getAudio();
            imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

            drawer = findViewById(R.id.d);
            rDrawer = findViewById(R.id.rd);
            Preferences = findViewById(R.id.pref);
            albumHolder = findViewById(R.id.album_holder);
            initDrawer();

            mainTL = findViewById(R.id.tab);
            list = mainTL.findViewById(R.id.list);
            list.setOnItemLongClickListener(itemLongClickListener);
//            eAdapter = new Adapter(adptrs.external,-1);
            plAdapter = new Adapter(adptrs.playlist, -1);
            mainTL.setTabButtons(0);

            wifiTL = findViewById(R.id.wifiTab);
            wifiList = wifiTL.findViewById(R.id.list);
            wifiTL.tContext = wifiTabs;
            ((ImageView) wifiTL.findViewById(R.id.arte)).setImageResource(R.drawable.haribol);
            wifiTL.findViewById(R.id.arte).setVisibility(VISIBLE);
            ((Button) wifiTL.findViewById(R.id.tab1_)).setText(R.string.audio);
            ((Button) wifiTL.findViewById(R.id.tab2_)).setText(R.string.arte);
            ((Button) wifiTL.findViewById(R.id.tab3_)).setText(R.string.vb);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) wifiList.getLayoutParams();
            p.topMargin = 4 * dp10;
            wifiList.setLayoutParams(p);
            wifiList.setOnItemClickListener(wifiItemClickListener);

            ((TextView) albumHolder.findViewById(R.id.album_name)).setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
            holderTL = findViewById(R.id.holder_tab);
            holderTL.findViewById(R.id.tab1_).setVisibility(GONE);
            holderTL.findViewById(R.id.tab2_).setVisibility(GONE);
            holderTL.findViewById(R.id.tab3_).setVisibility(GONE);
            holderList = holderTL.findViewById(R.id.list);
            holderList.setOnItemClickListener(audioItemClickListener);
            holderList.setDropListener(mDropListener);
            holderList.setOnItemLongClickListener(itemLongClickListener);
            setAdapters();

            authorAdapter = new Adapter(adptrs.authors, -1);
            vbAdapter = new Adapter(adptrs.vedabase, -1);
            offlineAdapter = new Adapter(adptrs.offline, -1);
            arteAdapter = new Adapter(adptrs.arts, -1);

            fsMenu = findViewById(R.id.fullscreen_player_menu_list);
            fullScreenTL = findViewById(R.id.fst);
            fullScreenTL.tContext = fullScreenTabs;
            fullScreenTL.setTabButtons(1);
            fullScreenTL.findViewById(R.id.tabs_).setVisibility(GONE);
            fullScreenTL.findViewById(R.id.divider_).animate().translationY(-3 * dp10).start();
            fullScreenTL.findViewById(R.id.container).animate().translationY(dp10).start();
            fsList = fullScreenTL.findViewById(R.id.list);
            fsList.setOnItemClickListener(audioItemClickListener);
            fsList.setDropListener(mDropListener);
            fsList.setOnItemLongClickListener(itemLongClickListener);
            percent = findViewById(R.id.percent);
            findViewById(R.id.songVolume).setOnTouchListener(onVolumeTouchListener);
            findViewById(R.id.deviceVolume).setOnTouchListener(onVolumeTouchListener);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (audioManager.isVolumeFixed())
                    findViewById(R.id.deviceVolume).setVisibility(GONE);
            }
            streamMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            findViewById(R.id.volumeControls).setOnTouchListener((view, motionEvent) -> {
                view.animate().alpha(1).setDuration(0).start();
                view.animate().alpha(0).setDuration(800).start();
                return false;
            });
            fullscreen.findViewById(R.id.arte).setOnTouchListener((view, motionEvent) -> {
                fsMenu.setAdapter(null);
                return false;
            });
            fsMenu.setOnItemClickListener((adapterView, view, i, l) -> {
                if (h.nowPlaying != null) {
                    switch (i) {
                        case 0:
                            addToPlayList(view, h.nowPlaying.index, -1, false);
                            break;
                        case 1:
                            onBackPressed();
                            onBackPressed();
                            goToAlbum(h.nowPlaying.albumId, holderTabs);
                            break;
                        case 2:
                            shareSong(-1);
                            break;
                        case 3:
                            setLike(-1);
                            break;
                        case 4:
                            tagLyrics(h.nowPlaying);
                    }
                }
                fsMenu.setAdapter(null);
            });

            toolbar = findViewById(R.id.t);
            searchBar = findViewById(R.id.ts);
            editText = findViewById(R.id.search_view);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    query_ = charSequence.toString().trim();
                    if (query_.trim().isEmpty()) return;
                    int j = 0;
                    searchResults.clear();
                    for (SearchList s : sList) {
                        if (s.val.trim().toLowerCase().contains(query_.trim().toLowerCase())) {
                            searchResults.add(s);
                            if (j++ > maxSearchResults) break;
                        }
                    }
                    searchList.setAdapter(new SearchAdapter());
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (((EditText) findViewById(R.id.search_view)).getText().length() == 0)
                        searchList.setAdapter(null);
                }
            });
            searchList = findViewById(R.id.searchList);
            searchList.setAdapter(null);
            searchList.setOnItemClickListener(searchItemClickListener);

            playPauseButtons = new int[]{R.id.playPause, R.id.playPause1};
            (seekBar = findViewById(R.id.seekBar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    if (musicPlayer != null && b)
                        musicPlayer.seekTo(1000 * i);
                    int min = i / 60, hr = min / 60;
                    int sec = i % 60;
                    min = min % 60;
                    String progressStr = "";
                    progressStr = progressStr + (hr == 0 ? "  " : ((hr > 9 ? hr : "0" + hr) + ":"));
                    progressStr = progressStr + (min > 9 ? min : "0" + min) + ":";
                    progressStr = progressStr + (sec > 9 ? sec : "0" + sec);
                    ((TextView) findViewById(R.id.progress)).setText(progressStr);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            findViewById(R.id.forward).setOnTouchListener(onTouchListener);
            findViewById(R.id.revert).setOnTouchListener(onTouchListener);

            findViewById(R.id.album_name).setSelected(true);
            findViewById(R.id.artist_).setSelected(true);
            findViewById(R.id.artist1).setSelected(true);

            findViewById(R.id.h).setVisibility(VISIBLE);
            setControllerUI();
            bootComplete = true;
            if (musicBound && !eventHandled) {
                eventHandled = true;
                handleIntent(getIntent());
                if (musicPlayer != null)
                    setPlayPauseButtons();
                RunThread runThread = new RunThread();
                runThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadResources);
            }
            if (musicPlayer1 != null) findViewById(R.id.chantWithSPFAB).setVisibility(VISIBLE);
            findViewById(R.id.chantWithSPFAB).setOnTouchListener(fab);
            (quickBall = findViewById(R.id.quickBall)).setOnTouchListener(fab);
            (q = findViewById(R.id.iq)).setOnTouchListener(fab);
            ((ListView) findViewById(R.id.rd_list)).setAdapter(bookmarkAdapter = new Adapter(adptrs.bookmark, -1));
            clipAdapter = new Adapter(adptrs.clip, -1);
            ((ListView) findViewById(R.id.rd_list)).setOnItemClickListener(bookmarkOnItemClickListener);
            VedabaseWebView = initWebView(R.id.veda_web_view, R.id.toolbar2, false);
            NotesWebView = initWebView(R.id.notes_web_view, R.id.toolbar2, false);
            YouTubeWebView = initWebView(R.id.youtube_web_view, R.id.toolbarYt, false);
            YouTubeWebView.setOnTouchListener((view, motionEvent) -> {
                youTubeOnTouch();
                return false;
            });
            findViewById(R.id.bg).setOnTouchListener((v, event) -> {
                if (q.getVisibility() == VISIBLE && event.getAction() == MotionEvent.ACTION_DOWN) {
                    onBackPressed();
                    return true;
                }
                return false;
            });
            NotesWebView.setVisibility(View.GONE);
            vedabaseContainer = findViewById(R.id.vedabase_container);
            ytContainer = findViewById(R.id.youtubeContainer);
            long tLapsed = System.currentTimeMillis() - startTime;
            ytContainer.postDelayed(() -> {
                setBackground();
                findViewById(R.id.r).animate().alpha(0).setDuration(800).start();
                drawer.collapse();
                rDrawer.collapse();
                holderTL.findViewById(R.id.arte).setVisibility(prefs.fixedBackground == 1 ? VISIBLE : GONE);
                hideQuickBall(quickBall);
                ((ImageButton) findViewById(R.id.chantWithSPFAB)).setImageURI(Uri.fromFile(new File(INTERNAL_DIR, "a1_compressed1")));
                ((ImageButton) toolbar.findViewById(R.id.vedabasebtn)).setImageURI(Uri.fromFile(new File(INTERNAL_DIR, "a1_compressed1")));
                ((ImageButton) drawer.findViewById(R.id.vedabasebtn)).setImageURI(Uri.fromFile(new File(INTERNAL_DIR, "a1_compressed1")));
                holderTL.findViewById(R.id.arte).setVisibility(prefs.fixedBackground == 0 ? VISIBLE : GONE);
            }, tLapsed > 2800 ? 200 : 3000 - tLapsed);//*/
            if (!(new File(INTERNAL_DIR.getPath() + "/toBackup/" + "favourites")).exists()) {
                for (int j = 0; j < maharajas.get(1).audioIndices.size(); j++) {
                    Audio a = audio.get(maharajas.get(1).audioIndices.get(j));
                    for (int i = 0; i < lyrics.size(); i++) {
                        if (a.title.replaceAll(" ", "").toLowerCase()
                                .contains(lyrics.get(i).name.replaceAll(" ", "").toLowerCase()) ||
                                lyrics.get(i).name.replaceAll(" ", "").toLowerCase().contains(a.title.replaceAll(" ", "").toLowerCase())) {
                            a.ref = i + "";
                            break;
                        }
                    }
                }
            }
        }
    };

    AdapterView.OnItemClickListener audioClipOnItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
            File f1 = getAudioClipFile();
            String t = audioClips.get(i);
            f1 = new File(f1, t.split("\\$b")[0] + ".mp3");
            int[] linkData = getIndexFromUrl(t.split("\\$b")[1]);
            if (musicPlayer == null)
                mServ.instantiateMusicPlayer();
            musicPlayer.currentPos = linkData[1] * 1000;
            mServ.preventPlay = false;
            startPlaying(audio.get(linkData[0]));
            maharaja = h.nowPlaying.albumId;

            if (f1.exists()) {
                startActivity(Intent.createChooser(new Intent(Intent.ACTION_SEND).setType("audio/mp3")
                        .putExtra(Intent.EXTRA_STREAM, Uri.parse("content://" +
                                authority + "/" + f1.getName()))
                        .putExtra(Intent.EXTRA_TEXT, "\uD83D\uDC96  " + getString(R.string.app_name) + " \uD83D\uDC96  \n\nhttps://play.google.com/store/apps/details?id=" + getPackageName())
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION), "Share Clip"));
            } else {
                s = linkData[1];
                e = linkData[2];
                shareClip(f1);
            }
        }
    };

    private File getAudioClipFile() {
        File f1 = new File(getFilesDir(), "clips");
        if (!f1.exists()) f1.mkdir();
        return f1;
    }

    AdapterView.OnItemClickListener bookmarkOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
            if (!bookmark.isEmpty()) {
                String[] s = bookmark.split("\\$b");
                if (s[2].equals("t"))
                    bookmarks.set(bookmarks.indexOf(bookmark), bookmark.replace(s[1], getUrlToShare(h.nowPlaying.index)));
            }
            bookmark = bookmarks.get(i);
            String s = bookmark.substring(bookmark.indexOf("$b") + 2);
            int[] linkData = getIndexFromUrl(s.substring(0, s.indexOf("$b")));
            if (musicPlayer == null)
                mServ.instantiateMusicPlayer();
            musicPlayer.currentPos = linkData[1] * 1000;
            mServ.preventPlay = false;
            startPlaying(audio.get(linkData[0]));
            maharaja = h.nowPlaying.albumId;
        }
    };

    View.OnTouchListener fab = new View.OnTouchListener() {
        float downRawX, downRawY, dX, dY;

        @Override
        public boolean onTouch(final View view, MotionEvent motionEvent) {
            int action = motionEvent.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                view.animate().alpha(1).setStartDelay(0).setDuration(0).start();
                dX = (downRawX = motionEvent.getRawX()) - view.getX();
                dY = (downRawY = motionEvent.getRawY()) - view.getY();
            } else if (action == MotionEvent.ACTION_MOVE) {
                view.setX(motionEvent.getRawX() - dX);
                view.setY(motionEvent.getRawY() - dY);
            } else if (action == MotionEvent.ACTION_UP && view.getId() != R.id.iq) {
                if (Math.abs(motionEvent.getRawX() - downRawX) < dp10 / 3f && Math.abs(motionEvent.getRawY() - downRawY) < dp10 / 3f)
                    view.performClick();
                else {
                    float x = view.getX(), y = view.getY();
                    float x1 = x < size.x / 2f ? dp10 / 2f : size.x - 7 * dp10,
                            y1 = y > size.y - dp10 * 12 ? size.y - dp10 * 12 : y < dp10 * 10 ? dp10 * 10 : y;
                    view.animate().translationXBy(x1 - x).translationYBy(y1 - y).setDuration(200).start();
                    hideQuickBall(view);
                }
            }
            if (view.getId() == R.id.iq) {
                quickBall.setX(view.getX() + 4.3f * dp10);
                quickBall.setY(view.getY() + 4.3f * dp10);
            }
            return true;
        }
    };

    public void youTubeOnTouch() {
        ytContainer.postDelayed(() -> {
            if (YouTubeWebView.canGoBack()) {
                View ytFrag = ytContainer;
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) ytFrag.getLayoutParams();
                p.topMargin = (int) (1.5 * dp10);
                ytFrag.setLayoutParams(p);
            }
        }, 300);
    }

    SeekBar.OnSeekBarChangeListener setColor = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            switch (seekBar.getId()) {
                case R.id.alpha:
                    prefs.a = i;
                    break;
                case R.id.red:
                    prefs.r = i;
                    break;
                case R.id.green:
                    prefs.g = i;
                    break;
                case R.id.blue:
                    prefs.b = i;
                    break;
            }
            findViewById(R.id.bg_arte).setBackgroundColor(Color.argb(prefs.a, prefs.r, prefs.g, prefs.b));
            findViewById(R.id.prevBackground).setBackgroundColor(Color.argb(prefs.a, prefs.r, prefs.g, prefs.b));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };
    float streamMaxVolume;
    float MAX_VOLUME = 100f;
    View.OnTouchListener onVolumeTouchListener = new View.OnTouchListener() {
        float downRowY;
        int streamVolume, songVolume, ind = 0;
        boolean setSongVolume = false;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            setSongVolume = (view.getId() == R.id.songVolume);
            int action = motionEvent.getAction();
            percent.animate().alpha(0.8f).setDuration(0).start();
            findViewById(R.id.volumeControls).animate().alpha(1).setDuration(0).start();
            if (action == MotionEvent.ACTION_DOWN) {
                downRowY = motionEvent.getRawY();
                streamVolume = (int) (100f * audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) / streamMaxVolume);
                ind = findViewById(R.id.chantWithSPController).getVisibility() == GONE ? h.nowPlaying.index : audio.size();
                songVolume = volume.get(ind);
                if (setSongVolume && h.nowPlaying != null)
                    percent.setText("Song Volume\n" + songVolume + " %");
                else
                    percent.setText("Device Volume\n" + streamVolume + " %");
            } else if (action == MotionEvent.ACTION_MOVE) {
                int v = (int) (-6 * (motionEvent.getRawY() - downRowY) / dp10);
                if (setSongVolume && (h.nowPlaying != null || ind == audio.size())) {
                    v += songVolume;
                    v = v > 100 ? 100 : Math.max(v, 0);
                    volume.set(ind, spChantVol = v);
                    percent.setText("Song Volume\n" + v + " %");
                    float v1 = (float) (1 - (Math.log(MAX_VOLUME - v) / Math.log(MAX_VOLUME)));
                    if (ind == audio.size() && musicPlayer1 != null)
                        musicPlayer1.setVolume(v1, v1);
                    else if (musicPlayer != null)
                        musicPlayer.setVolume(v1, v1);
                } else if (!setSongVolume) {
                    v += streamVolume;
                    v = v > 100 ? 100 : Math.max(v, 0);
                    percent.setText("Device Volume\n" + v + " %");
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (v * streamMaxVolume / 100f), 0);
                }
            } else if (action == MotionEvent.ACTION_UP) {
                percent.animate().alpha(0).setDuration(500).start();
                if (ind == audio.size()) createChantWithPrabhupadaNotification();
            }
            return true;
        }
    };
    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        boolean pressed = false;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            final int direction = view.getId() == R.id.forward ? 1 : -1;
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                if (!pressed)
                    musicPlayer.seekTo(musicPlayer.getCurrentPosition() + direction * 10000);
                pressed = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        musicPlayer.seekTo(musicPlayer.getCurrentPosition() + direction * 1000);
                        if (pressed)
                            view.postDelayed(this, 50);
                    }
                });
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                pressed = false;
            return true;
        }
    };
    AdapterView.OnItemClickListener wifiItemClickListener = (adapterView, view, i, l) -> {
        Adapter a = ((Adapter) adapterView.getAdapter());
        if (a.selectedItems[i] = !a.selectedItems[i]) a.wifiSelectedItems++;
        else a.wifiSelectedItems--;
        a.notifyDataSetChanged();
        findViewById(R.id.unselectAll).setVisibility(a.wifiSelectedItems == a.count ? VISIBLE : GONE);
        setWifiCount();
    };

    AdapterView.OnItemClickListener drawerMenuItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, final View view, final int i, long l) {
            drawer.collapse();
            view.postDelayed(() -> {
                if (h.nowPlaying != null)
                    setDrawerUI(h.nowPlaying.albumId);
                btn.setPressed(false);
                btn = view.findViewById(R.id.arte);
                btn.setPressed(true);
                ytContainer.setVisibility(GONE);
                switch ((new Adapter(adptrs.dList, -1)).txt[i]) {
                    case "Library":
                        albumHolder.setVisibility(GONE);
                        albumHolder.animate().alpha(0).scaleX(.1f).scaleY(.1f).setDuration(animDuration).start();
                        mainTL.animate().alpha(1).setDuration(animDuration).start();
                        break;
                    case "Video Library":
                        ytContainer.setVisibility(VISIBLE);
                        findViewById(R.id.search).setVisibility(GONE);
                        if (YouTubeWebView.getUrl() == null) YouTubeWebView.loadUrl(youTubeUrl);
                        break;
                    case "Song Book":
                        goToAlbum(0, songbookTabs);
                        dMenuAdapter.ind = 1;
                        break;
                    case "External Songs":
                        goToAlbum(0, externalAudioTabs);
                        break;
                    case "Favourites":
                        dMenuAdapter.ind = 2;
                        goToAlbum(0, plTabs);
                        break;
                    case "Offline":
                        dMenuAdapter.ind = 3;
                        goToAlbum(1, plTabs);
                        break;
                    case "Playing Queue":
                        fullScreenTL.setTabButtons(0);
                    case "Now Playing":
                        if (ytContainer.getVisibility() == GONE) {
                            fullscreen.setVisibility(VISIBLE);
                            drawer.disableDrawer();
                            bottomDrawer.expand();
                            bottomDrawer.findViewById(R.id.blurredArte).animate().alpha(0).setDuration(300).start();
                            bottomDrawer.findViewById(R.id.blurredArte).setVisibility(GONE);
                            bottomDrawer.findViewById(R.id.blurredArte_).setVisibility(GONE);
                            vedabaseContainer.setVisibility(GONE);
                            toolbar.setVisibility(GONE);
                        }
                        break;
                    case "Preferences":
                        setPrefView();
                        break;
                    case "Srila Prabhupada Stickers":
                        showSticker();
                        break;
                    case "\uD83D\uDC96 Buy Books \uD83D\uDC96":
                        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://amzn.to/2BsTL4h")));
                        break;
                    case "More Apps":
                        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(getString(R.string.all_apps_url))));
                        break;
                    case "About Us":
                        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://krishna-apps.web.app/")));
                        break;
                }
            }, animDuration);
        }
    };

    private void showSticker() {
        strList.setVisibility(VISIBLE);
        strList.animate().scaleY(1).scaleX(1).alpha(1).setDuration(250).start();
        strList.setAdapter(strAdapter = new StrAdapter());
        findViewById(R.id.search).setVisibility(GONE);
    }

    AdapterView.OnItemClickListener maharajaItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Album m = maharajas.get(mAdapter.indices.get(i));
            if (searchBar.getVisibility() == VISIBLE) onBackPressed();
            if (m.subAlbumIndices.size() == 0) {
                goToAlbum(i, holderTabs);
                drawer.collapse();
            } else {
                maharaja = drawer.isCollapsed() ? dMAdapter.indices.get(i) : mAdapter.indices.get(i);
                dMAdapter = new Adapter(adptrs.dMaharaja, maharaja);
                if (!drawer.isCollapsed()) {
                    drawerList.setAdapter(dMAdapter);
                    goToAlbum(i, holderTabs);
                    mAdapter = new Adapter(adptrs.artist, maharaja);
                } else list.setAdapter(mAdapter = new Adapter(adptrs.artist, maharaja));
                setDrawerUI(maharaja);
            }
        }
    };

    public void goToAlbum(int i, int tContext) {
        if (tContext == plTabs) {
            holderTL.maxTabs = playlist.size() + 2;
            holderTL.tContext = plTabs;
            holderTL.init();
            holderList.setDropListener(mDropListener);
            ((ImageView) holderTL.findViewById(R.id.arte)).setImageResource(R.drawable.haribol);
            holderTL.findViewById(R.id.arte).setBackgroundColor(Color.argb(prefs.a, prefs.r, prefs.g, prefs.b));
        } else {
            holderTL.maxTabs = tContext == holderTabs ? maharajas.get(mAdapter.ind).subAlbumIndices.size() : tContext == songbookTabs ? 1 :
                    tContext == authorTabs ? authors.size() : authors.get(lyricsAdapter.ind).lyricsIndices.size();
            holderTL.tContext = tContext;
            holderTL.init();
            holderList.setDropListener(null);
        }
        if (tContext == lyricsTabs) {
            holderList.setAdapter(null);
            holderTL.findViewById(R.id.lyricsHolder).setVisibility(VISIBLE);
            findViewById(R.id.invertColor).setVisibility(VISIBLE);
            findViewById(R.id.search).setVisibility(GONE);
        } else
            holderTL.findViewById(R.id.lyricsHolder).setVisibility(GONE);
        albumHolder.setVisibility(VISIBLE);
        albumHolder.animate().alpha(1).scaleX(1).scaleY(1).setDuration(animDuration).start();
        mainTL.animate().alpha(0).setDuration(animDuration).start();
        holderTL.setTabButtons(i);
    }

    AdapterView.OnItemClickListener audioItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (fullscreen.getVisibility() == GONE) {
                mServ.Queue.clear();
                mServ.Queue.addAll(((Adapter) adapterView.getAdapter()).indices);
            }
            startPlaying(audio.get(mServ.Queue.get(i)));
        }
    };

    AdapterView.OnItemLongClickListener itemLongClickListener = (parent, view, position, id) -> {
        view = view.findViewById(R.id.list_menu);
        if (view != null) view.performClick();
        return true;
    };
    AdapterView.OnItemClickListener plItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            goToAlbum(i, plTabs);
            if (searchBar.getVisibility() == VISIBLE) onBackPressed();
        }
    };
    AdapterView.OnItemClickListener authorItemClickListener = (adapterView, view, i, l) -> goToAlbum(i, authorTabs);
    AdapterView.OnItemClickListener lyricsItemClickListener = (adapterView, view, i, l) -> goToAlbum(i, lyricsTabs);
    AdapterView.OnItemClickListener searchItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            SearchList s = searchResults.get(i);
            switch (s.type) {
                case audioItem:
                    startPlaying(audio.get(s.ind));
                    break;
                case maharajaItem:
                    goToAlbum(s.ind, holderTabs);
                    break;
                case plItem:
                    goToAlbum(s.ind, plTabs);
                    break;
                case authorItem:
                    goToAlbum(s.ind, authorTabs);
                    break;
                case lyricsItem:
                    lyricsAdapter = new Adapter(adptrs.lyrics, lyrics.get(s.ind).authorInd);
                    goToAlbum(lyricsAdapter.indices.indexOf(s.ind), lyricsTabs);
                    break;
            }
            onBackPressed();
        }
    };

    public void onClick(final View view) {
        hideKeyboard();
        int i = view.getId();
        boolean disablePopup = true;
        Intent intent = null;
        if (i == R.id.searchSettings) {
            final PopupMenu popupMenu = new PopupMenu(k.this, view);
            hideKeyboard();
            if (searchIn[0]) setDefaultSearchIn();
            popupMenu.getMenu().add("Search In All");
            popupMenu.getMenu().addSubMenu("Search In");
            for (int j = 0; j < searchInOpt.length; j++) {
                popupMenu.getMenu().getItem(1).getSubMenu().add(0, j, j, searchInOpt[j]);
                popupMenu.getMenu().getItem(1).getSubMenu().getItem(j).setChecked(searchIn[j]);
            }
            popupMenu.getMenu().getItem(1).getSubMenu().setGroupCheckable(0, true, false);
            popupMenu.getMenu().addSubMenu("Search For");
            for (int j = 0; j < searchForOpt.length; j++) {
                popupMenu.getMenu().getItem(2).getSubMenu().add(1, j, j, searchForOpt[j]);
                popupMenu.getMenu().getItem(2).getSubMenu().getItem(j).setChecked(searchFor[j]);
            }
            popupMenu.getMenu().getItem(2).getSubMenu().getItem(0).setEnabled(false);
            popupMenu.getMenu().getItem(2).getSubMenu().setGroupCheckable(1, true, false);
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.isCheckable()) {
                    int i1 = item.getItemId();
                    if (i1 == 0) {
                        setDefaultSearchIn();
                    } else
                        searchIn[0] = false;
                    popupMenu.getMenu().getItem(1).getSubMenu().getItem(0).setChecked(searchIn[0]);
                    if (item.getGroupId() == 0) {
                        item.setChecked(searchIn[i1] = !searchIn[i1]);
                        if (i1 == 0) {
                            setDefaultSearchIn();
                            for (int j = 0; j < searchIn.length; j++)
                                popupMenu.getMenu().getItem(1).getSubMenu().getItem(j).setChecked(searchIn[j]);
                        } else if (i1 == 1) {
                            popupMenu.getMenu().getItem(1).getSubMenu().getItem(0).setChecked(searchIn[0] = false);
                            popupMenu.getMenu().getItem(1).getSubMenu().getItem(2).setChecked(searchIn[2] = false);
                            popupMenu.getMenu().getItem(1).getSubMenu().getItem(3).setChecked(searchIn[3] = false);
                        } else {
                            popupMenu.getMenu().getItem(1).getSubMenu().getItem(0).setChecked(searchIn[0] = false);
                            popupMenu.getMenu().getItem(1).getSubMenu().getItem(1).setChecked(searchIn[1] = false);
                            if (!searchIn[2] && !searchIn[3])
                                popupMenu.getMenu().getItem(1).getSubMenu().getItem(1).setChecked(searchIn[1] = true);
                        }
                    } else
                        item.setChecked(searchFor[i1] = !searchFor[i1]);
                    item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
                    item.setActionView(new View(getApplicationContext()));
                    item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                        @Override
                        public boolean onMenuItemActionExpand(MenuItem item) {
                            return false;
                        }

                        @Override
                        public boolean onMenuItemActionCollapse(MenuItem item) {
                            return false;
                        }
                    });
                }
                return false;
            });
            popupMenu.setOnDismissListener(popupMenu1 -> {
                imm.toggleSoftInput(0, 0);
                setSearchList();
            });
            popupMenu.show();
        } else if (i == R.id.clearSearch) {
            editText.setText("");
        } else if (searchBar.getVisibility() == VISIBLE)
            onBackPressed();
        if (i == R.id.drawer_btn || i == R.id.app_name) {
            drawer.expand();
        } else if (i == R.id.name) {
            if (((Adapter) drawerList.getAdapter()).a == adptrs.dList) {
                drawerList.setAdapter(dMAdapter);
                drawerList.setOnItemClickListener(maharajaItemClickListener);
                drawer.isM_Adapter = true;
            } else {
                drawerList.setAdapter(dMenuAdapter);
                drawerList.setOnItemClickListener(drawerMenuItemClickListener);
                drawer.isM_Adapter = false;
            }
        } else if (i == R.id.menu || i == R.id.menu1) {
            int[] position = new int[2];
            Point p = new Point();
            view.getLocationOnScreen(position);
            p.set(position[0], position[1]);
            showPopup(k.this, view);
            disablePopup = false;
        } else if (i == R.id.feedback) {
            intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "mayank.srmu@gmail.com", null))
                    .putExtra(Intent.EXTRA_SUBJECT, "Subject: " + APP_NAME + " feedback")
                    .putExtra(Intent.EXTRA_TEXT, "Thanks for approaching us.\nPlease write your feedback below.\n\nPlease do not change the subject line\n---\n");
        } else if (i == R.id.share_app || i == R.id.send) {
            if (!prefs.appSharingSign.contains("@appUrl")) prefs.appSharingSign += "\n\n@appUrl";
            intent = new Intent(Intent.ACTION_SEND).setType("text/plain")
                    .putExtra(Intent.EXTRA_TEXT, prefs.appSharingSign.replace("@appUrl", " " + getString(R.string.app_url) + " "));
        } else if (i == R.id.rate_app) {
            intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(getString(R.string.app_url)));
        } else if (i == R.id.back) {
            onBackPressed();
        } else if (i == R.id.prabhupada_today) {
            startActivity(new Intent(k.this, sQuote.class));
        } else if (i == R.id.playPauseChantWithSP) {
            playPauseChantWithSP();
        } else if (i == R.id.stopChantWithSP) {
            stopChantWithSP();
        } else if (i == R.id.chantWithSPFAB) {
            showChantWithSP();
        } else if (i == R.id.quickBall) {
            float y = view.getY(), x = view.getX(), x1 = x < size.x / 2f ? 8 * dp10 : -8 * dp10,
                    y1 = y > size.y - 20 * dp10 ? -8 * dp10 : y < 18 * dp10 ? 8 * dp10 : 0;
            q.setX(x - 4.3f * dp10);
            q.setY(y - 4.3f * dp10);
            q.animate().scaleY(.1f).scaleX(.1f).alpha(0).rotation(-180).setDuration(0).start();
            q.setVisibility(VISIBLE);
            q.animate().translationXBy(x1).translationYBy(y1).scaleX(1).scaleY(1).alpha(1).rotation(0).setDuration(200).start();
            view.animate().translationXBy(x1).translationYBy(y1).setDuration(200).start();
        } else if (i == R.id.stop) {
            stopChantWithSP();
        } else if (i == R.id.chant_with_prabhupada || i == R.id.pl_chant || i == R.id.mahamantra) {
            startChantWithSP();
        } else if (i == R.id.daily_nectar) {
            TextView textView = findViewById(R.id.daily_nectar);
            textView.setText(Html.fromHtml(getDailyNectar(this)));
            findViewById(R.id.daily_nectar_container).setVisibility(VISIBLE);
        } else if (i == R.id.daily_nectar_container) {
            view.setVisibility(GONE);
        } else if (i == R.id.send_daily_nectar) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, ((TextView) findViewById(R.id.daily_nectar)).getText() + "-His Holiness Bhakti Rasamrita Swami\n\n" + getString(R.string.app_url));
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } else if (i == R.id.search) {
            searchBar.setVisibility(VISIBLE);
            editText.setText("");
            editText.requestFocus();
            imm.toggleSoftInput(0, 0);
            if (mainTL.getAlpha() == 0 && holderTL.tContext == lyricsTabs) {
                findViewById(R.id.searchSettings).setVisibility(GONE);
                findViewById(R.id.sSettings).setVisibility(GONE);
            } else {
                findViewById(R.id.searchSettings).setVisibility(VISIBLE);
                findViewById(R.id.sSettings).setVisibility(VISIBLE);
            }
            setSearchList();
        } else if (i == R.id.filter) {
            PopupMenu p = new PopupMenu(k.this, findViewById(R.id.menu));
            for (int j = 0; j < filterOptions.length; j++)
                p.getMenu().add(0, j, j, filterOptions[j]);
            Menu m = p.getMenu();
            m.setGroupCheckable(0, true, false);
            for (int j = 0; j < filterOptions.length; j++)
                m.getItem(j).setChecked((prefs.filterFlags & flg[j]) == flg[j]);
            p.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                item.setChecked(!item.isChecked());
                if (item.isChecked()) prefs.filterFlags |= flg[id];
                else prefs.filterFlags &= ~flg[id];
                setAdapters();
                item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
                item.setActionView(new View(getApplicationContext()));
                item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return false;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        return false;
                    }
                });
                return false;
            });
            p.show();
        } else if (i == R.id.sort) {
            String[] options;
            if (mainTL.getAlpha() > 0 && mainTL.tabInd != 2)
                options = new String[]{"Sort by Name", "Sort by Song Count"};
            else if (mainTL.getAlpha() > 0 && mainTL.tabInd == 2)
                options = new String[]{"Sort by Title", "Sort by Album", "Sort by Place", "Sort by Date"};
            else
                options = new String[]{"Sort by Title", "Sort by Place", "Sort by Date"};
            PopupMenu p = new PopupMenu(k.this, popup != null && popup.isShowing() ? findViewById(R.id.menu) : view);
            for (String s : options) p.getMenu().add(s);

            p.setOnMenuItemClickListener(menuItem -> {
                final int sortAscending;
                Adapter a = (Adapter) (albumHolder.getVisibility() == VISIBLE ? holderList.getAdapter() : list.getAdapter());
                switch (menuItem.getTitle().toString()) {
                    case "Sort by Name":
                        sortAscending = (a.sortAscending[0] = !a.sortAscending[0]) ? -1 : 1;
                        if (a.a == adptrs.playlist) {
                            Collections.sort(a.indices, (i12, t1) -> sortAscending * playlist.get(i12).compareToIgnoreCase(playlist.get(t1)));
                        } else {
                            Collections.sort(a.indices, (i12, t1) -> sortAscending * maharajas.get(i12).name.compareToIgnoreCase(maharajas.get(t1).name));
                        }
                        break;
                    case "Sort by Song Count":
                        sortAscending = (a.sortAscending[1] = !a.sortAscending[1]) ? -1 : 1;
                        if (a.a == adptrs.playlist) {
                            Collections.sort(a.indices,
                                    (i12, t1) -> sortAscending * (playlistAudios.get(i12).size() - playlistAudios.get(t1).size()));
                        } else {
                            Collections.sort(a.indices, (i12, t1) -> sortAscending * (maharajas.get(i12).getCount() - maharajas.get(t1).getCount()));
                        }
                        break;
                    case "Sort by Title":
                        sortAscending = (a.sortAscending[0] = !a.sortAscending[0]) ? -1 : 1;
                        Collections.sort(a.indices, (i12, t1) -> sortAscending * (audio.get(i12).title.compareToIgnoreCase(audio.get(t1).title)));
                        break;
                    case "Sort by Album":
                        sortAscending = (a.sortAscending[0] = !a.sortAscending[0]) ? -1 : 1;
                        Collections.sort(a.indices, (i12, t1) -> sortAscending * (maharajas.get(audio.get(i12).albumId).name.compareToIgnoreCase(maharajas.get(audio.get(t1).albumId).name)));
                        break;
                    case "Sort by Place":
                        sortAscending = (a.sortAscending[1] = !a.sortAscending[1]) ? -1 : 1;
                        Collections.sort(a.indices, (i12, t1) -> {
                            String a1 = places[audio.get(i12).placeInd], b = places[audio.get(t1).placeInd];
                            if (sortAscending == 1) {
                                a1 = a1.isEmpty() ? "zzz" : a1;
                                b = b.isEmpty() ? "zzz" : b;
                            }
                            return sortAscending * (a1.compareToIgnoreCase(b));
                        });
                        break;
                    case "Sort by Date":
                        sortAscending = (a.sortAscending[2] = !a.sortAscending[2]) ? -1 : 1;
                        Collections.sort(a.indices, (i12, t1) -> {
                            long a12 = audio.get(i12).date, b = audio.get(t1).date;
                            if (sortAscending == 1) {
                                a12 = a12 == 0 ? (new Date()).getTime() / 3600000 / 24 : a12;
                                b = b == 0 ? (new Date()).getTime() / 3600000 / 24 : b;
                            }
                            return sortAscending * (a12 == b ? 0 : a12 - b < 0 ? -1 : 1);
                        });
                        break;
                }
                a.notifyDataSetChanged();
                list.setSelection(0);
                holderList.setSelection(0);
                return false;
            });
            p.show();
        } else if (i == R.id.shuffle_all) {
            mServ.Shuffle = true;
            mServ.shuffleQueue();
            playNext();
        } else if (i == R.id.preferences_btn) {
            if (fullscreen.getVisibility() == VISIBLE) onBackPressed();
            setPrefView();
        } else if (i == R.id.menu_full_screen) {
            fullScreenTL.setTabButtons(1);
            fsMenu.setAdapter(new Adapter(adptrs.fullScreenMenu, -1));
        } else if (i == R.id.tab1_ && wifi.getVisibility() == VISIBLE) {
            wifiTL.setTabButtons(0);
        } else if (i == R.id.tab2_ && wifi.getVisibility() == VISIBLE) {
            wifiTL.setTabButtons(1);
        } else if (i == R.id.tab3_ && wifi.getVisibility() == VISIBLE) {
            wifiTL.setTabButtons(2);
        } else if (i == R.id.selectAll) {
            findViewById(R.id.unselectAll).setVisibility(VISIBLE);
            Adapter adapter = (Adapter) wifiList.getAdapter();
            adapter.wifiSelectedItems = adapter.getCount();
            for (int j = 0; j < adapter.getCount(); j++) adapter.selectedItems[j] = true;
            adapter.notifyDataSetChanged();
            setWifiCount();
        } else if (i == R.id.unselectAll) {
            view.setVisibility(GONE);
            Adapter adapter = (Adapter) wifiList.getAdapter();
            adapter.wifiSelectedItems = 0;
            for (int j = 0; j < adapter.getCount(); j++) adapter.selectedItems[j] = false;
            adapter.notifyDataSetChanged();
            setWifiCount();
        } else if (i == R.id.tab1_) {
            mainTL.setTabButtons(0);
        } else if (i == R.id.tab2_) {
            mainTL.setTabButtons(1);
        } else if (i == R.id.tab3_) {
            mainTL.setTabButtons(2);
        } else if (i == R.id.shuffle) {
            mServ.Shuffle = !mServ.Shuffle;
            setShuffleAndRepeat();
            mServ.shuffleQueue();
        } else if (i == R.id.repeat) {
            mServ.Repeat = (mServ.Repeat + 1) % 3;
            setShuffleAndRepeat();
        } else if (i == R.id.share) {
            if (clipContainer.getVisibility() == VISIBLE) {
                shareAudioClip(((EditText) findViewById(R.id.clipTitle)).getText().toString());
            } else {
                PopupMenu p = new PopupMenu(k.this, view);
                p.getMenu().add("Share Url");
                p.getMenu().add("Share Clip");
                p.setOnMenuItemClickListener(item -> {
                    switch (item.getTitle().toString()) {
                        case "Share Url":
                            shareSong(-1);
                            break;
                        case "Share Clip":
                            shareAudioClip();
                            break;
                    }
                    return true;
                });
                p.show();
            }
        } else if (i == R.id.cut) {
            shareAudioClip();
        } else if (i == R.id.clip) {
            shareAudioClip(((EditText) findViewById(R.id.clipTitle)).getText().toString());
        } else if (i == R.id.like_) {
            setLike(-1);
        } else if ((i == R.id.playPause || i == R.id.playPause1)) {
            playPause();
        } else if (i == R.id.next) {
            playNext();
        } else if (i == R.id.prev) {
            playPrevious();
        } else if (i == R.id.invertColor1 || i == R.id.invertColor) {
            opaqueBackground = !opaqueBackground;
            TextView lyrics = holderTL.findViewById(R.id.lyrics);
            TextView lyrics1 = fullScreenTL.findViewById(R.id.lyrics);
            if (opaqueBackground) {
                lyrics.setBackgroundResource(R.color.colorPrimary);
                lyrics.setTextColor(Color.BLUE);
                lyrics.setShadowLayer(0, 0, 0, 0);
                lyrics1.setBackgroundResource(R.color.colorPrimary);
                lyrics1.setTextColor(Color.BLUE);
                lyrics1.setShadowLayer(0, 0, 0, 0);
            } else {
                lyrics.setBackgroundResource(android.R.color.transparent);
                lyrics.setTextColor(Color.WHITE);
                lyrics.setShadowLayer(8, 0, 0, Color.RED);
                lyrics1.setBackgroundResource(android.R.color.transparent);
                lyrics1.setTextColor(Color.WHITE);
                lyrics1.setShadowLayer(8, 0, 0, Color.RED);
            }
        } else if (i == R.id.audio) {
            if (fullscreen.getVisibility() == VISIBLE) {
                fsList.setAdapter(lsAdapter = new Adapter(adptrs.ls, h.nowPlaying.getLyricsInd()));
                fsList.setOnItemClickListener(audioItemClickListener);
                fullScreenTL.findViewById(R.id.lyricsHolder).setVisibility(GONE);
            } else {
                holderList.setAdapter(lsAdapter = new Adapter(adptrs.ls, lyricsAdapter.indices.get(holderTL.tabInd)));
                holderList.setOnItemClickListener(audioItemClickListener);
                holderTL.findViewById(R.id.lyricsHolder).setVisibility(GONE);
            }
            findViewById(R.id.invertColor).setVisibility(GONE);
            findViewById(R.id.search).setVisibility(VISIBLE);
        } else if (i == R.id.pref_gen) {
            setPrefView(R.id.pref_gen_btns);
        } else if (i == R.id.pref_notification) {
            setPrefView(R.id.pref_notification_btns);
        } else if (i == R.id.pref_storage) {
            setPrefView(R.id.storage);
        } else if (i == R.id.becomingNoisyAction) {
            PopupMenu popupMenu = new PopupMenu(k.this, view);
            for (int j = 0; j < becomingNoisy.length; j++)
                popupMenu.getMenu().add(0, j, j, becomingNoisy[j]);
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                prefs.becomingNoisyAction = menuItem.getOrder();
                ((Button) view).setText(h.becomingNoisy[menuItem.getOrder()]);
                return false;
            });
            popupMenu.show();
        } else if (i == R.id.shakeAction) {
            PopupMenu popupMenu = new PopupMenu(k.this, view);
            for (int j = 0; j < onShake.length; j++)
                popupMenu.getMenu().add(0, j, j, onShake[j]);
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                prefs.onShake = menuItem.getOrder();
                ((Button) view).setText(h.onShake[menuItem.getOrder()]);
                if (menuItem.getOrder() == 2) mServ.shakeDelay = 1000;
                return false;
            });
            popupMenu.show();
        } else if (i == R.id.setBackground) {
            PopupMenu p = new PopupMenu(k.this, view);
            p.getMenu().add(0, 0, 0, "Select Color");
            p.getMenu().add(1, 1, 1, "Select From Album Arte");
            p.getMenu().add(2, 2, 2, "Select From Device");
            p.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case 0:
                        ((ImageView) findViewById(R.id.bg_arte)).setImageBitmap(null);
                        ((ImageView) findViewById(R.id.prevBackground)).setImageBitmap(null);
                        prefs.bgUri = "";
                        setBackground();
                        findViewById(R.id.colorPicker).setVisibility(VISIBLE);
                        break;
                    case 1:
                        searchList.setAdapter(arteAdapter);
                        searchList.setOnItemClickListener((adapterView, view1, i13, l) -> {
                            if (i13 == 0 || i13 == 1) prefs.bgUri = arteList.get(i13);
                            else
                                prefs.bgUri = Uri.fromFile(new File(INTERNAL_DIR, arteList.get(i13))).toString();
                            setBackground();
                            Preferences.animate().alpha(0).setDuration(100).start();
                            Preferences.postDelayed(showPref, 300);
                            searchList.setOnItemClickListener(searchItemClickListener);
                            searchList.setAdapter(null);
                        });
                        break;
                    case 2:
                        if (checkPermissions())
                            startActivityForResult(Intent.createChooser(new Intent(Intent.ACTION_PICK,
                                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI).setType("image/*"), "Choose Image")
                                    , RESULT_LOAD_IMAGE);
                }
                return false;
            });
            p.show();
            findViewById(R.id.colorPicker).setVisibility(GONE);
        } else if (i == R.id.showFab) {
            prefs.showQuickBall = (prefs.showQuickBall + 1) % 2;
            if (prefs.showQuickBall == 1) {
                quickBall.setVisibility(VISIBLE);
                float x = quickBall.getX(), y = quickBall.getY();
                float x1 = x < size.x / 2f ? dp10 / 2f : size.x - 7 * dp10,
                        y1 = y > size.y - dp10 * 12 ? size.y - dp10 * 12 : y < dp10 * 10 ? dp10 * 10 : y;
                quickBall.animate().translationXBy(x1 - x).translationYBy(y1 - y).setDuration(200).start();
                hideQuickBall(quickBall);
            } else quickBall.setVisibility(GONE);
        } /*else if (i == R.id.showHK) {
            prefs.showHk = (prefs.showHk + 1) % 2;
            if (prefs.showHk == 1) {
                findViewById(R.id.hk__).setVisibility(VISIBLE);
                h1 = 5 * dp10;
                size.y -= h1;
            } else {
                findViewById(R.id.hk__).setVisibility(GONE);
                size.y += h1;
                h1 = 0;
            }
            bottomDrawer.collapse();
            Preferences.animate().alpha(0).setDuration(100).start();
            handler.postDelayed(showPref, 500);
        } */ else if (i == R.id.fixedBackground) {
            prefs.fixedBackground = (prefs.fixedBackground + 1) % 2;
            holderTL.findViewById(R.id.arte).setVisibility(prefs.fixedBackground == 0 ? VISIBLE : GONE);
        } else if (i == R.id.offlineVedabase) {
            prefs.offlineVedabase = (prefs.offlineVedabase + 1) % 2;
        } else if (i == R.id.pref_notification_daily_nectar) {
            prefs.dailyNectarNotification = (prefs.dailyNectarNotification + 1) % 3;
            setNotificationIcon(k.this, prefs.dailyNectarNotification, i, true);
        } else if (i == R.id.bsrm_time) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                TimePicker timePicker = findViewById(R.id.bsrm_time_picker);
                if (timePicker.getVisibility() == View.GONE) {
                    timePicker.setVisibility(VISIBLE);
                    ((Button) view).setText(R.string.set_time);
                } else {
                    timePicker.setVisibility(View.GONE);
                    prefs.dailyNectarTime = timePicker.getHour() * 60 + timePicker.getMinute();
                    setNotificationTime();
                    (new Alarm()).setAlarm(k.this, Alarm.FOR_DAILY_NECTAR_NOT, Alarm.DAILY_NECTAR_NOT_ID, prefs.dailyNectarTime);
                }
            }
        } else if (i == R.id.bsrm_not_sound) {
            setNotificationTone(RESULT_BSRM_NOTIFICATION_TONE);
        } else if (i == R.id.pref_notification_prabhupada_today) {
            prefs.SPQuoteNotification = (prefs.SPQuoteNotification + 1) % 3;
            setNotificationIcon(k.this, prefs.SPQuoteNotification, i, true);
        } else if (view.getId() == R.id.vedabasebtn) {
            if (vedabaseContainer.getVisibility() == View.GONE) {
                vedabaseContainer.setVisibility(VISIBLE);
                NotesWebView.setVisibility(View.GONE);
                VedabaseWebView.setVisibility(VISIBLE);
                ((ImageButton) findViewById(R.id.fab_notes)).setImageResource(R.drawable.note);
                if (VedabaseWebView.getUrl() == null) {
                    File f = getFileFromUrl("");
                    if ((prefs.offlineVedabase == 1 || !mServ.isNetworkAvailable()) && f.exists())
                        VedabaseWebView.loadUrl("file:///" + f.getAbsolutePath());
                    else
                        VedabaseWebView.loadUrl(vedaBaseUrl);
                }
                drawer.collapse();
            } else
                vedabaseContainer.setVisibility(View.GONE);
        } else if (i == R.id.sp_time) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                TimePicker timePicker = findViewById(R.id.sp_time_picker);
                if (timePicker.getVisibility() == View.GONE) {
                    timePicker.setVisibility(VISIBLE);
                    ((Button) view).setText(R.string.set_time);
                } else {
                    timePicker.setVisibility(View.GONE);
                    prefs.spQuoteTime = timePicker.getHour() * 60 + timePicker.getMinute();
                    setNotificationTime();
                    (new Alarm()).setAlarm(k.this, Alarm.FOR_SP_QUOTE_NOT, Alarm.SP_QUOTE_NOT_ID, prefs.spQuoteTime);
                }
            }
        } else if (i == R.id.sp_not_sound) {
            setNotificationTone(RESULT_SP_NOTIFICATION_TONE);
        } else if (i == R.id.download_manager) {
            view.setVisibility(GONE);
        } else if (i == R.id.updating_resources) {
            findViewById(R.id.download_manager).setVisibility(VISIBLE);
            ((ListView) findViewById(R.id.download_list)).setAdapter(downloadManagerAdapter = new Adapter(adptrs.downloadManager, -1));
        } else if (i == R.id.cancel_download_manager && !downloadQStack.isEmpty()) {
            DownloadQ d = downloadQStack.pop();
            for (int j = 0; j < downloadQStack.size(); j++)
                if (downloadQStack.get(j).type != -10) downloadQStack.remove(j);
            downloadQStack.push(d);
            downloadManagerAdapter.notifyDataSetChanged();
//            ((ListView)findViewById(R.id.download_list)).setAdapter(downloadManagerAdapter = new Adapter(adptrs.downloadManager, -1));
        } else if (i == R.id.done) {
            findViewById(R.id.colorPicker).setVisibility(GONE);
            Preferences.animate().alpha(0).setDuration(100).start();
            Preferences.postDelayed(showPref, 300);
        } else if (view.getId() == R.id.fab_close) {
            vedabaseContainer.setVisibility(View.GONE);
        } else if (view.getId() == R.id.reload) {
            TextView myTextView = vedabaseContainer.findViewById(R.id.textView2);
            myTextView.setText(R.string.loading);
            err = false;
            if (NotesWebView.getVisibility() == VISIBLE) {
                NotesWebView.reload();//this could result in vedabase view opening
            } else {
                VedabaseWebView.reload();
            }
        } else if (view.getId() == R.id.goForward) {
            if (findViewById(R.id.notes_web_view).getVisibility() == VISIBLE) {
                if (NotesWebView.canGoForward())
                    NotesWebView.goForward();
                else
                    Toast.makeText(k.this, "Can not go further", Toast.LENGTH_SHORT).show();
            } else {
                if (VedabaseWebView.canGoForward())
                    VedabaseWebView.goForward();
                else
                    Toast.makeText(k.this, "Can not go further", Toast.LENGTH_SHORT).show();
            }
        } else if (view.getId() == R.id.goFullScreen) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) vedabaseContainer.getLayoutParams();
            if (p.topMargin == 0) {
                if (vedabaseContainer.top_margine == 0)
                    p.topMargin = 25 * dp10;
                else
                    p.topMargin = vedabaseContainer.top_margine;
                vedabaseContainer.setLayoutParams(p);
                ((ImageButton) view).setImageResource(R.drawable.fullscreen);
            } else {
                p.topMargin = 0;
                ViewGroup.MarginLayoutParams p1 = (ViewGroup.MarginLayoutParams) VedabaseWebView.getLayoutParams();
                p1.topMargin = 0;
                VedabaseWebView.setLayoutParams(p1);
                NotesWebView.setLayoutParams(p1);
                vedabaseContainer.setLayoutParams(p);
                ((ImageButton) view).setImageResource(R.drawable.fullscreen_exit);
            }
        } else if (view.getId() == R.id.fab_notes) {
            if (findViewById(R.id.notes_web_view).getVisibility() == VISIBLE) {
                NotesWebView.setVisibility(View.GONE);
                VedabaseWebView.setVisibility(VISIBLE);
                ((ImageButton) findViewById(R.id.fab_notes)).setImageResource(R.drawable.note);
            } else {
                NotesWebView.setVisibility(VISIBLE);
                VedabaseWebView.setVisibility(GONE);
                if (NotesWebView.getUrl() == null) NotesWebView.loadUrl(notesUrl);
                ((ImageButton) findViewById(R.id.fab_notes)).setImageURI(Uri.fromFile(new File(INTERNAL_DIR, "a1_compressed1")));
            }
        } else if (i == R.id.open_in_yt) {
            Toast.makeText(k.this, "Opening current page in YouTube Application\n\nPlease subscribe to the Channel", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(Intent.ACTION_VIEW);
            intent1.setData(Uri.parse(YouTubeWebView.getUrl()));
            startActivity(intent1);
        } else if (i == R.id.forward) {
            if (YouTubeWebView.canGoForward()) YouTubeWebView.goForward();
            youTubeOnTouch();
        } else if (i == R.id.refresh) {
            YouTubeWebView.setVisibility(View.GONE);
            TextView myTextView = findViewById(R.id.textView2);
            myTextView.setText(R.string.loading);
            err = false;
            YouTubeWebView.reload();
        } else if (i == R.id.about) {
            showSupportPopup(k.this);
            disablePopup = false;
        } else if (i == R.id.bookmarks) {
            drawer.collapse();
            rDrawer.expand();
        } else if (i == R.id.rd_title) {
            TextView t = (TextView) view;
            if (t.getText().toString().trim().equals("Bookmarks")) {
                t.setText(" Audio Clips");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    t.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.music_q), null, getDrawable(R.drawable.bookmark), null);
                }
                rDrawer.findViewById(R.id.add_bookmark).setVisibility(GONE);
                rDrawer.findViewById(R.id.cut).setVisibility(VISIBLE);
                ((ListView) findViewById(R.id.rd_list)).setAdapter(clipAdapter = new Adapter(adptrs.clip, -1));
                ((ListView) findViewById(R.id.rd_list)).setOnItemClickListener(audioClipOnItemClickListener);
            } else {
                t.setText(" Bookmarks");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    t.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.bookmark), null, getDrawable(R.drawable.music_q), null);
                }
                rDrawer.findViewById(R.id.add_bookmark).setVisibility(VISIBLE);
                rDrawer.findViewById(R.id.cut).setVisibility(GONE);
                ((ListView) findViewById(R.id.rd_list)).setAdapter(bookmarkAdapter);
                ((ListView) findViewById(R.id.rd_list)).setOnItemClickListener(bookmarkOnItemClickListener);
            }
        } else if (i == R.id.add_bookmark) {
            AddBookmark(-1);
        } else if (i == R.id.addPlaylist) {
            createNewPlaylist(-10, true, -10);
        } else if (i == R.id.initWifi) {
            wifiStatus = wifiManager.isWifiEnabled();
            adapters = new Adapter[]{offlineAdapter, arteAdapter, vbAdapter};
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);// Indicates a change in the list of available peers.
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);// Indicates the state of Wi-Fi P2P connectivity has changed.
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);// Indicates this device's details have changed.
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
            mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
            mChannel = mManager.initialize(this, getMainLooper(), null);
            receiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
            registerReceiver(receiver, intentFilter);
            wifi.setVisibility(VISIBLE);
            offlineAdapter.selectedItems = new boolean[offlineAdapter.getCount()];
            arteAdapter.selectedItems = new boolean[arteAdapter.getCount()];
            vbAdapter.selectedItems = new boolean[vbAdapter.getCount()];
            wifiTL.setTabButtons(0);
            final File f = new File(INTERNAL_DIR, "wifi_name_");
            try {
                wifiName = (new BufferedReader(new InputStreamReader(new FileInputStream(f)))).readLine();
                try {
                    Method m = mManager.getClass().getMethod("setDeviceName",
                            WifiP2pManager.Channel.class, String.class, WifiP2pManager.ActionListener.class);
                    m.invoke(mManager, mChannel, wifiName, null);
                } catch (Exception ignored) {
                }
            } catch (IOException e) {
                final EditText input = new EditText(k.this);
                dBuilder.setTitle("Set Device Name").setMessage("This name will be visible to your peers.")
                        .setPositiveButton("Set", (dialog, which) -> {
                            wifiName = "sp_" + input.getText().toString();
                            try {
                                Method m = mManager.getClass().getMethod("setDeviceName",
                                        WifiP2pManager.Channel.class, String.class, WifiP2pManager.ActionListener.class);
                                m.invoke(mManager, mChannel, wifiName, null);
                                (new FileOutputStream(f)).write(wifiName.getBytes());
                            } catch (Exception ignored) {
                            }
                            dBuilder.setCancelable(true);
                        }).setNegativeButton("Cancel", (dialog, which) -> {
                    onBackPressed();
                    dBuilder.setCancelable(true);
                }).setView(input).setCancelable(false).show();
            }
//            findViewById(R.id.radar).startAnimation(AnimationUtils.loadAnimation(k.this, R.anim.rotate));
        } else if (i == R.id.wifiSend || i == R.id.wifiSend_) {
            if (info != null && info.groupFormed && !sendingFiles)
                (new RunThread()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, sendFile);
            else if (!searchPeers(true)) {
                Toast.makeText(k.this, "Enabling Wifi. Please try again.", Toast.LENGTH_LONG).show();
                return;
            }
        } else if (i == R.id.wifiReceive) {
            if (!searchPeers(false)) {
                Toast.makeText(k.this, "Enabling Wifi. Please try again.", Toast.LENGTH_LONG).show();
                return;
            }
        } else if (i == R.id.add_to_wa) {
            addToWa(month);
        } else if (i == R.id.prabhupada_str) {
            showSticker();
        } else {
            String uriStr = "";
            if (i == R.id.fb) {
                uriStr = fbUrl;
            } else if (i == R.id.app) {
                uriStr = getString(R.string.app_url);
            } else if (i == R.id.twitter) {
                uriStr = twitterUrl;
            } else if (i == R.id.in) {
                uriStr = linkedInUrl;
            } else if (i == R.id.yt) {
                uriStr = ytUrl;
            } else if (i == R.id.web) {
                uriStr = web;
            }
            if (!uriStr.equals("")) {
                intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(uriStr));
            }
        }
        if (popup != null && disablePopup) popup.dismiss();
        if (intent != null) startActivity(intent);
    }

    static int getWifiCount() {
        return offlineAdapter.wifiSelectedItems + arteAdapter.wifiSelectedItems + vbAdapter.wifiSelectedItems;
    }

    private static void setWifiCount() {
        int c = getWifiCount();
        wifiCount.setText(c + (c > 1 ? " files selected" : " file selected"));
        if (c > 0) {
            wifiBtns_.setVisibility(VISIBLE);
            wifiBtns.setVisibility(GONE);
        } else {
            wifiBtns_.setVisibility(GONE);
            if (!p2pConnected) wifiBtns.setVisibility(VISIBLE);
        }
    }

    private void AddBookmark(final int i) {
        drawer.collapse();
        final View input = inflate(k.this, R.layout.i, null);
        final EditText title = input.findViewById(R.id.title);
        final EditText notes = input.findViewById(R.id.notes);
        final CheckBox dynamic = input.findViewById(R.id.dynamic);
        if (i >= 0) {
            String[] str = bookmarks.get(i).split("\\$b");
            title.setText(str[0]);
            dynamic.setChecked(str[2].equals("t"));
            input.findViewById(R.id.dynamic_info).setVisibility(str[2].endsWith("t") ? VISIBLE : GONE);
            notes.setText(str.length > 3 ? str[3] : "");
        }
        final String b = getUrlToShare(h.nowPlaying.index);
        AlertDialog.Builder builder = new AlertDialog.Builder(k.this);
        builder.setTitle("Add Bookmark")
                .setNeutralButton("Save", (dialog, id) -> {
                    String t = title.getText().toString().trim();
                    String n = notes.getText().toString().trim();
                    String d = dynamic.isChecked() ? "t" : "f";
                    t = t.replaceAll("\\$b", "");
                    t = t.substring(0, 1).toUpperCase().concat(t.substring(1));
                    if (i < 0)
                        bookmarks.add(t + "$b" + b + "$b" + d + "$b" + n);
                    else
                        bookmarks.set(i, t + "$b" + b + "$b" + d + "$b" + n);
                    bookmarkAdapter.notifyDataSetChanged();
                }).setNegativeButton("Cancel", null).setView(input);
        if (i >= 0)
            builder.setPositiveButton("Delete", (dialog, which) -> {
                bookmarks.remove(i);
                bookmarkAdapter.notifyDataSetChanged();
                sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
            });
        builder.setIcon(R.drawable.bookmark).show();
        if (!rDrawer.isCollapsed()) rDrawer.expand();
    }

    private void startChantWithSP() {
        File file = new File(INTERNAL_DIR, CHANT_WITH_PRABHUPADA);
        if (!file.exists()) {
            dBuilder.setMessage("Sorry!\nSrila Prabhupada Chant is unavailable now.\n\n" +
                    "Please try in a while.").show();
        } else if (invalidateList) {
            invalidateList = false;
        } else if (musicPlayer1 == null) {
            musicPlayer1 = new MediaPlayer();
            try {
                musicPlayer1.setDataSource(Uri.fromFile(file).toString());// initialize it here
            } catch (IOException e) {
                e.printStackTrace();
            }
            musicPlayer1.setOnPreparedListener(musicPlayer -> {
                musicPlayer.start();
                pauseVisible = VISIBLE;
                findViewById(R.id.chantWithSPFAB).setVisibility(VISIBLE);
                hideQuickBall(findViewById(R.id.chantWithSPFAB));
                musicPlayer.setLooping(true);
                createChantWithPrabhupadaNotification();
            });
            musicPlayer1.prepareAsync();
        }
    }

    @Override
    public void onBackPressed() {
        findViewById(R.id.invertColor).setVisibility(GONE);
        if (ytContainer.getVisibility() == GONE)
            findViewById(R.id.search).setVisibility(VISIBLE);
        findViewById(R.id.colorPicker).setVisibility(GONE);
        searchList.setAdapter(null);
        searchList.setOnItemClickListener(searchItemClickListener);
        if (waiting.getVisibility() == VISIBLE) {
            waiting.setVisibility(GONE);
            mManager.stopPeerDiscovery(mChannel, null);
        } else if (wifi_transferring.getVisibility() == VISIBLE && wifi.getVisibility() == VISIBLE) {
            dBuilder.setMessage("Transfer will Continue in Background.\nYou can not initiate new transfer until current task is finished")
                    .setPositiveButton("Ok", (dialog, which) -> wifi.setVisibility(GONE)).setNegativeButton("Cancel", null).show();
        } else if (wifiBtns_.getVisibility() == VISIBLE) {
            findViewById(R.id.unselectAll).setVisibility(GONE);
            for (Adapter adapter : adapters) {
                adapter.selectedItems = null;
                adapter.selectedItems = new boolean[adapter.getCount()];
                offlineAdapter.notifyDataSetChanged();
                offlineAdapter.wifiSelectedItems = 0;
            }
            setWifiCount();
        } else if (wifi.getVisibility() == VISIBLE) {
            if (info != null && info.groupFormed)
                dBuilder.setMessage("Disconnect from peers and disable sharing")
                        .setPositiveButton("ok", (dialog, which) -> disableWiFi()).setNegativeButton("Cancel", null).show();
            else
                disableWiFi();
        } else if (q.getVisibility() == VISIBLE) {
            float x = quickBall.getX();
            x = x < size.x / 2f ? -x + dp10 / 2f : -x + size.x - 7 * dp10;
            quickBall.animate().translationXBy(x).setDuration(200).start();
            q.animate().translationXBy(x).scaleX(.1f).scaleY(.1f).rotation(-90).alpha(0).setDuration(200).start();
            q.postDelayed(() -> {
                q.setVisibility(GONE);
                hideQuickBall(quickBall);
            }, 220);
        } else if (findViewById(R.id.download_manager).getVisibility() == VISIBLE) {
            findViewById(R.id.download_manager).setVisibility(GONE);
        } else if (findViewById(R.id.chantWithSPController).getVisibility() == VISIBLE) {
            hideChantWithSP();
        } else if (findViewById(R.id.daily_nectar_container).getVisibility() == VISIBLE) {
            findViewById(R.id.daily_nectar_container).setVisibility(GONE);
        } else if (!rDrawer.isCollapsed()) {
            rDrawer.collapse();
        } else if (!drawer.isCollapsed()) {
            int i1 = maharajas.get(mAdapter.ind).parentAlbumIndex;
            if (dMAdapter.ind > 0 && ((Adapter) drawerList.getAdapter()).a == adptrs.dMaharaja) {
                setDrawerUI(dMAdapter.ind);
                drawerList.setAdapter(dMAdapter = new Adapter(adptrs.artist, Math.max(i1, 0)));
                list.setAdapter(mAdapter = new Adapter(adptrs.artist, Math.max(i1, 0)));
            } else
                drawer.collapse();
        } else if (searchBar.getVisibility() == VISIBLE) {
            searchBar.setVisibility(GONE);
            toolbar.setVisibility(VISIBLE);
            searchList.setAdapter(null);
        } else if (Preferences.getVisibility() == VISIBLE) {
            Preferences.setVisibility(GONE);
            drawer.collapse();
            prefs.appSharingSign = ((EditText) Preferences.findViewById(R.id.appShareTxt)).getText().toString();
            prefs.songSharingSign = ((EditText) Preferences.findViewById(R.id.songShareTxt)).getText().toString();
            drawerList.setAdapter(dMenuAdapter = new Adapter(adptrs.dList, dMenuAdapter.ind));
        } else if (fullscreen.getVisibility() == VISIBLE) {
            if (fsMenu.getAdapter() != null) {
                fsMenu.setAdapter(null);
                return;
            }
            bottomDrawer.collapse();
            fullscreen.setVisibility(GONE);
            drawer.collapse();
            clipContainer.setVisibility(GONE);
            fullScreenTL.setVisibility(VISIBLE);
            findViewById(R.id.menu_full_screen).setVisibility(VISIBLE);
            drawerList.setAdapter(dMenuAdapter = new Adapter(adptrs.dList, dMenuAdapter.ind));
            toolbar.setVisibility(VISIBLE);
        } else if (vedabaseContainer.getVisibility() == VISIBLE) {
            if (findViewById(R.id.notes_web_view).getVisibility() == VISIBLE) {
                if (NotesWebView.canGoBack())
                    NotesWebView.goBack();
                else {
                    NotesWebView.setVisibility(GONE);
                    VedabaseWebView.setVisibility(VISIBLE);
                    ((ImageButton) findViewById(R.id.fab_notes)).setImageResource(R.drawable.note);
                }
            } else {
                if (VedabaseWebView.canGoBack())
                    VedabaseWebView.goBack();
                else
                    vedabaseContainer.setVisibility(GONE);
            }
        } else if (ytContainer.getVisibility() == VISIBLE) {
            if (YouTubeWebView.canGoBack()) {
                YouTubeWebView.goBack();
                YouTubeWebView.postDelayed(() -> {
                    if (!YouTubeWebView.canGoBack()) {
                        View ytFrag1 = ytContainer;
                        ViewGroup.MarginLayoutParams p1 = (ViewGroup.MarginLayoutParams) ytFrag1.getLayoutParams();
                        p1.topMargin = -(int) (24.5 * dp10);
                        ytFrag1.setLayoutParams(p1);
                    }
                }, 300);
            } else {
                ytContainer.setVisibility(GONE);
                drawerList.setAdapter(dMenuAdapter = new Adapter(adptrs.dList, 0));
                findViewById(R.id.search).setVisibility(VISIBLE);
            }
        } else if (day > 0) {
            day = 0;
//            img1.animate().scaleX(0).scaleY(0).setDuration(200).start();
//            img1.postDelayed(() -> img1.setVisibility(GONE), 250);
            checkWhiteListed(month);
        } else if (month >= 0) {
            strList.animate().scaleX(0).scaleY(0).setDuration(200).start();
            strList.postDelayed(() -> strList.animate().scaleX(1).scaleY(1).setDuration(100).start(), 220);
            month = -1;
            strAdapter.notifyDataSetChanged();
            strList.setPadding(0, 0, 0, 0);
            strList.setNumColumns(1);
            checkWhiteListed(-1);
//            setStrListAsHome();
        } else if (strList.getVisibility() == VISIBLE) {
            strList.animate().scaleX(0).scaleY(0).setDuration(200).start();
            strList.postDelayed(() -> strList.setVisibility(GONE), 210);
            drawerList.setAdapter(dMenuAdapter = new Adapter(adptrs.dList, 0));
            findViewById(R.id.search).setVisibility(VISIBLE);
        } else if (albumHolder.getVisibility() == VISIBLE) {
            Adapter a = (Adapter) holderList.getAdapter();
            if (a == null) {
                goToAlbum(lyricsAdapter.ind, authorTabs);
            } else if (a.a == adptrs.ls) {
                goToAlbum(lyricsAdapter.indices.indexOf(lsAdapter.ind), lyricsTabs);
            } else if (a.a == adptrs.lyrics)
                goToAlbum(0, songbookTabs);
            else {
                albumHolder.animate().alpha(0).scaleX(.1f).scaleY(.1f).setDuration(animDuration).start();
                mainTL.animate().alpha(1).setDuration(animDuration).start();
                mainTL.postDelayed(() -> albumHolder.setVisibility(GONE), animDuration);
                if (h.nowPlaying != null)
                    setDrawerUI(h.nowPlaying.albumId);
                drawerList.setAdapter(dMenuAdapter = new Adapter(adptrs.dList, 0));
            }
        } else if (bottomDrawer.findViewById(R.id.ui_bottom).getAlpha() == 1) {
            bottomDrawer.collapse();
        } else if (mainTL.tabInd != 0) {
            mainTL.setTabButtons(0);
        } else if (mAdapter.ind != 0) {
            dMAdapter = new Adapter(adptrs.artist, maharajas.get(mAdapter.ind).parentAlbumIndex);
            list.setAdapter(mAdapter = new Adapter(adptrs.artist, maharajas.get(mAdapter.ind).parentAlbumIndex));
        } else if (activityVisile) {
            super.onBackPressed();
        }
    }

    @SuppressLint("SetTextI18n")
    private void disableWiFi() {
        wifi.setVisibility(GONE);
        mManager.removeGroup(mChannel, null);
        try {
            unregisterReceiver(receiver);
        } catch (Exception ignored) {
        }
        for (Adapter adapter : adapters) {
            adapter.selectedItems = null;
            adapter.wifiSelectedItems = 0;
        }
        ((TextView) findViewById(R.id.conn_to)).setText(R.string.exp_feature);
        wifiManager.setWifiEnabled(wifiStatus);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException ignored) {
        }
        activityVisile = false;
        if (!bookmark.isEmpty()) {
            String[] s = bookmark.split("\\$b");
            if (s[2].equals("t"))
                bookmarks.set(bookmarks.indexOf(bookmark), bookmark = bookmark.replace(s[1], getUrlToShare(h.nowPlaying.index)));
            bookmarkAdapter.notifyDataSetChanged();
        }
        if (!favourites.isEmpty())
            writePlaylistToFile();
        if (mServ != null)
            writePreferencesToFile();
    }

    @Override
    protected void onDestroy() {
        onPause();
        try {
            mManager.stopPeerDiscovery(mChannel, null);
        } catch (Exception ignored) {
        }
        unbindService(musicConnection);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
        activityVisile = true;
        if (btn != null)
            btn.setPressed(true);
    }


    public void startPlaying(Audio audio) {
//        maharaja = audio.albumId;   setDrawerUI(maharaja);
        h.nowPlaying = audio;
        startPlaying();
    }

    public void startPlaying() {
        mServ.startPlaying();
    }

    public void setShuffleAndRepeat() {
        ((ImageButton) findViewById(R.id.shuffle)).setColorFilter(mServ.Shuffle ? 0xFFD81B60 : Color.GRAY);
        try{
            ((ImageButton) findViewById(R.id.like_)).setColorFilter(favourites.get(h.nowPlaying.index) ? 0xFFD81B60 : Color.GRAY);
        } catch (Exception e) {
            Log.e("err", e.getMessage());
        }
        ImageButton mRepeat = findViewById(R.id.repeat);
        switch (mServ.Repeat) {
            case REPEAT_NONE:
                mRepeat.setColorFilter(Color.GRAY);
                break;
            case REPEAT_LIST:
                mRepeat.setColorFilter(0xFFD81B60);
                mRepeat.setImageResource(R.drawable.ic_repeat_black_24dp);
                break;
            case REPEAT_ONE:
                mRepeat.setColorFilter(0xFFD81B60);
                mRepeat.setImageResource(R.drawable.ic_repeat_one_black_24dp);
        }
    }

    public void setPlayPauseButtons() {
        if (h.nowPlaying != null) {
            setControllerUI();
            initSeekBar();
            if (albumHolder.getVisibility() == VISIBLE)
                holderList.setSelection(((Adapter) holderList.getAdapter()).indices.indexOf(h.nowPlaying.index));
            else if (fullscreen.getVisibility() == VISIBLE && fullScreenTL.tabInd == 0)
                fsList.setSelection(((Adapter) fsList.getAdapter()).indices.indexOf(h.nowPlaying.index));
            else if (mainTL.tabInd == 2)
                list.setSelection(sAdapter.indices.indexOf(h.nowPlaying.index));
        }
        if (musicPlayer.isPlaying()) {
            for (int b : playPauseButtons) {
                ImageButton imageButton = findViewById(b);
                if (imageButton != null)
                    imageButton.setImageResource(android.R.drawable.ic_media_pause);
            }
        } else {
            for (int b : playPauseButtons) {
                ImageButton imageButton = findViewById(b);
                if (imageButton != null)
                    imageButton.setImageResource(android.R.drawable.ic_media_play);
            }
        }
        setShuffleAndRepeat();
        mServ.createNotification();
    }

    public void setControllerUI() {
        if (h.nowPlaying == null) h.nowPlaying = audio.get(0);
        String datePlace = h.nowPlaying.date == 0 ? places[h.nowPlaying.placeInd] : simpleDateFormat.format(h.nowPlaying.date * 3600000 * 24 + baseDate) +
                " " + places[h.nowPlaying.placeInd];
        ((TextView) findViewById(R.id.date_place_)).setText(datePlace);
        ((TextView) findViewById(R.id.title1)).setText(h.nowPlaying.title);
        ((TextView) findViewById(R.id.title_)).setText(h.nowPlaying.title);
        ((TextView) findViewById(R.id.artist1)).setText(maharajas.get(h.nowPlaying.albumId).name);
        ((TextView) findViewById(R.id.artist_)).setText(maharajas.get(h.nowPlaying.albumId).name);
        int authorInd = h.nowPlaying.getLyricsInd() < 0 ? -1 : lyrics.get(h.nowPlaying.getLyricsInd()).authorInd;
        final ArrayList<ArrayList<String>> textUrls = h.nowPlaying.getTextUrls();
        TextView author = findViewById(R.id.author_);
        if (textUrls != null) {
            author.setText(String.format("%s ", textUrls.get(2).get(0)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                author.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(android.R.drawable.arrow_down_float), null);
            }
            author.setBackgroundResource(R.drawable.rect);
            author.setOnClickListener(view -> {
                final PopupMenu popupMenu = new PopupMenu(k.this, view);
                for (int i = 0; i < textUrls.get(0).size(); i++)
                    popupMenu.getMenu().add(0, i, i, textUrls.get(1).get(i));
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    int i = menuItem.getOrder();
                    showVedabase(textUrls.get(0).get(i));
                    return false;
                });
                popupMenu.show();
            });
        } else {
            author.setBackgroundColor(Color.argb(0, 0, 0, 0));
            author.setText(authorInd > 0 && authorInd < authors.size() - 1 ? "by " + authors.get(authorInd).name : "");
            author.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }

        String arte = "arte_" + h.nowPlaying.arteInd;
        if (h.nowPlaying.arteInd < 0)
            arte = "arte_" + maharajas.get(h.nowPlaying.albumId).arteInd;
        setDrawerUI(maharaja = h.nowPlaying.albumId);
        final ImageView imageView = drawer.findViewById(R.id.arte);
        final ImageView imageView1 = fullscreen.findViewById(R.id.arte);
        final ImageView imageView2 = findViewById(R.id.arte_);
        final ImageView imageView3 = findViewById(R.id.blurredArte);
        final File file = new File(INTERNAL_DIR, arte);
        if (file.exists() && file.length() > 20) {
            imageView.setImageURI(Uri.fromFile(file));
            imageView1.setImageURI(Uri.fromFile(file));
            imageView2.setImageURI(Uri.fromFile(file));
            imageView3.setImageURI(Uri.fromFile(file));
        } else {
            imageView1.setImageResource(R.drawable.haribol);
            imageView3.setImageResource(R.drawable.haribol);
            final String finalArte = audioShareBaseUrl + arte;
            (new Thread(() -> {
                downloadFile(finalArte, file.getName());
                runOnUiThread(() -> {
                    if (!(file.exists() && file.length() > 20))
                        h.nowPlaying.arteInd = maharajas.get(h.nowPlaying.albumId).arteInd;
                    setControllerUI();
                });
            })).start();
        }
        if (h.nowPlaying.getLyricsInd() == -1)
            ((TextView) fullScreenTL.findViewById(R.id.lyrics)).setText("\n\n\n\n\n\n\n\n\nLyrics not available\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        else {
            String l = fetchLyrics(h.nowPlaying.getLyricsInd(), false);
            ((TextView) fullScreenTL.findViewById(R.id.lyrics)).setText(Html.fromHtml(l.startsWith("<") ? l
                    : "<p style=\"text-align: center; font-style: italic\">" + l.replaceAll("\n", "<br/>") + "</p>"));
        }
    }

    public void setDrawerUI(int ind) {
        maharaja = ind;
        drawerName.setText(Html.fromHtml("<u>" + maharajas.get(maharaja).name + "</u>"));
//        website.setText(link.substring(link.indexOf(':')+3).replace("www.",""));
        String[] str = {fbUrl, twitterUrl, linkedInUrl, ytUrl};
        for (int j = 0; j < str.length; j++)
            drawerBtns[j].setVisibility(str[j].isEmpty() ? GONE : VISIBLE);
        final String arte = "arte_" + maharajas.get(maharaja).arteInd;
        final File file = new File(INTERNAL_DIR, arte);
        final File file1 = new File(INTERNAL_DIR, "background_img.jpg");
        final ImageView imageView = drawer.findViewById(R.id.arte);
        final ImageView imageView1 = holderTL.findViewById(R.id.arte);
        if (maharajas.get(maharaja).arteInd < 0) {
            imageView.setImageURI(Uri.fromFile(file1));
            imageView1.setImageURI(Uri.fromFile(file1));
        } else if (file.exists()) {
            imageView.setImageURI(Uri.fromFile(file));
        } else {
            imageView.setImageResource(R.drawable.haribol);
            (new Thread(() -> {
                downloadFile(audioShareBaseUrl + arte, file.getName());
                runOnUiThread(() -> {
                    imageView.setImageURI(Uri.fromFile(file));
                    if (holderTL.tContext == holderTabs && prefs.fixedBackground == 0)
                        imageView1.setImageURI(Uri.fromFile(file));
                });
            })).start();
        }
    }

    public void setSeekBarProgress(long milliSec) {
        seekBar.setProgress((int) (milliSec / 1000));
    }

    void initSeekBar() {
        k.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (musicPlayer.isPrepared) {
                    long duration_ = musicPlayer.getDuration();
                    if (duration_ > 0) {
                        seekBar.setMax((int) (duration_ / 1000));
                        seekBar.setProgress(0);

                        long sec = duration_ / 1000, min = sec / 60, hr = min / 60;
                        sec = sec % 60;
                        min = min % 60;
                        String durationStr = "";
                        durationStr = durationStr + (hr == 0 ? "  " : ((hr > 9 ? hr : "0" + hr) + ":"));
                        durationStr = durationStr + (min > 9 ? min : "0" + min) + ":";
                        durationStr = durationStr + (sec > 9 ? sec : "0" + sec);

                        TextView duration = findViewById(R.id.duration);
                        if (duration != null)
                            duration.setText(durationStr);
                    }
                    long progress = musicPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress((int) progress);
                }
                if (musicPlayer.isPlaying())
                    seekBar.postDelayed(this, 1000);
            }
        });
    }

    PopupWindow popup = null;
    int pauseVisible = GONE;

    private void showPopup(final Activity context, View view) {
        ViewGroup viewGroup = context.findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater != null ? layoutInflater.inflate(R.layout.m, viewGroup) : null;
        popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setFocusable(true);
        Objects.requireNonNull(layout).findViewById(R.id.stop).setVisibility(pauseVisible);
        if (fullscreen.getVisibility() == VISIBLE || ytContainer.getVisibility() == VISIBLE)
            layout.findViewById(R.id.buttons).setVisibility(GONE);
        popup.showAsDropDown(view, 0, -5 * dp10);
    }

    @Override
    public void showFullScreenPlayer() {
        fullscreen.setVisibility(VISIBLE);
        fullScreenTL.setTabButtons(1);
        drawer.disableDrawer();
        toolbar.setVisibility(GONE);
        bottomDrawer.expand();
        findViewById(R.id.blurredArte).animate().alpha(0).setDuration(250).start();
        findViewById(R.id.blurredArte_).animate().alpha(0).setDuration(250).start();
    }

    public ArrayList<Integer> getQueue() {
        ArrayList<Integer> Queue;
        Queue = sAdapter.indices;
        if (albumHolder.getVisibility() == VISIBLE && mAudioAdapter != null)
            Queue = mAudioAdapter.indices;
        if (Queue.isEmpty())
            Queue = sAdapter.indices;//in case user has opened empty playing queue in albumHolder
        return Queue;
    }

    public void download(ArrayList<Integer> downloadQue) {
        downloadTask = new DownloadTask(k.this);
        if (downloadQue.size() > 3) {
            for (int i = 0; i < downloadQue.size(); i++)
                downloadQStack.add(0, new DownloadQ(downloadQue.get(i), AUDIO));
        } else {
            downloadTask.shouldStartPlaying = true;
            downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadQue.get(0));
            downloadQStack.add(new DownloadQ(downloadQue.get(1), AUDIO));
        }
        if (!downloadManagerRunning) {
            RunThread r = new RunThread();
            r.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadManager);
        }
    }

    @Override
    public void playPause() {
        if (clipContainer.getVisibility() == VISIBLE && musicPlayer != null && musicPlayer.getCurrentPosition() < s * 1000)
            musicPlayer.seekTo(s * 1000);
        if (musicPlayer == null) {
            startPlaying();
        } else if (musicPlayer.isPrepared && musicPlayer.isPlaying()) {
            musicPlayer.pause();
            musicPlayer.abandonAudioFocus();
        } else if (!musicPlayer.isPrepared) {
            startPlaying();
        } else {
            musicPlayer.start();
        }
    }

    @Override
    public void playNext() {
        if (!mServ.Queue.isEmpty())
            mServ.nowPlayingQueueInd = (mServ.nowPlayingQueueInd + 1) % mServ.Queue.size();
        h.nowPlaying = null;
        startPlaying();
    }

    @Override
    public void playPrevious() {
        if (clipContainer.getVisibility() == VISIBLE && musicPlayer != null)
            musicPlayer.seekTo(s * 1000);
        else {
            if (!mServ.Queue.isEmpty())
                mServ.nowPlayingQueueInd = (mServ.Queue.size() + mServ.nowPlayingQueueInd - 1) % mServ.Queue.size();
            h.nowPlaying = null;
            startPlaying();
        }
    }

    @Override
    public void setLike(int ind) {
        if (ind == -1)
            favourites.set(h.nowPlaying.index, !favourites.get(h.nowPlaying.index));
        else
            favourites.set(ind, !favourites.get(ind));
        favAdapter = new Adapter(adptrs.fav, -1);
        plAdapter = new Adapter(adptrs.playlist, -1);
        mServ.createNotification();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && ind == -1) {
            list.scrollListBy(size.y);
            holderList.scrollListBy(size.y);
            fsList.scrollListBy(size.y);
            list.scrollListBy(-size.y);
            holderList.scrollListBy(-size.y);
            fsList.scrollListBy(-size.y);
        }
        setShuffleAndRepeat();
    }

    String authority = "com.mayank.srilaprabhupadavani.provider";

    public void sharePlaylist(int i) {
        new Thread(() -> {
            String fileName = i == 0 ? "Favourites" : i == 1 ? "offline" : playlist.get(plAdapter.indices.get(i - 2));
            File file1 = new File(getCacheDir(), fileName + ".vpl");
            ArrayList<Integer> indices = i == 0 ? favAdapter.indices : i == 1 ? offlineAdapter.indices : playlistAudios.get(plAdapter.indices.get(i - 2));
            try {
                FileOutputStream fileOutputStream1 = new FileOutputStream(file1);
                StringBuilder playlistAudioStr = new StringBuilder();
                playlistAudioStr.append("bsrm,");
                for (int j = 0; j < indices.size(); j++)
                    playlistAudioStr.append(getUrlToShare(indices.get(j))).append(",");
                fileOutputStream1.write(playlistAudioStr.toString().getBytes());
                fileOutputStream1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> startActivity(Intent.createChooser(new Intent(Intent.ACTION_SEND).setType("*/*")
                    .putExtra(Intent.EXTRA_STREAM, Uri.parse("content://" +
                            authority + "/" + fileName + ".vpl"))
                    .putExtra(Intent.EXTRA_TEXT, "\uD83D\uDC96  " + getString(R.string.app_name) + " \uD83D\uDC96  \n\nhttps://play.google.com/store/apps/details?id=" + getPackageName())
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION), "Share Playlist")));
        }).start();
    }

    public void shareSong(int ind) {
        if (ind == -1) {
            if (h.nowPlaying == null) return;
            ind = h.nowPlaying.index;
        }
        sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        if (!prefs.songSharingSign.contains("@songUrl")) prefs.songSharingSign += "\n\n@songUrl";
        startActivity(Intent.createChooser(new Intent(Intent.ACTION_SEND).setType("text/plain").
                putExtra(Intent.EXTRA_TEXT, prefs.songSharingSign.replace("@songUrl",
                        audioShareBaseUrl + getUrlToShare(ind) + " ")), "Share Link"));
    }

    private boolean invalidateList = false;

    public void stopChantWithSP() {
        if (musicPlayer1 != null) {
            musicPlayer1.stop();
            musicPlayer1.release();
            musicPlayer1 = null;
            if (findViewById(R.id.chantWithSPController).getVisibility() == VISIBLE)
                hideChantWithSP();
            pauseVisible = GONE;
            findViewById(R.id.chantWithSPFAB).setVisibility(GONE);
            invalidateList = true;
        }
        notificationManager.cancel(101);
    }

    public void playPauseChantWithSP() {
        if (musicPlayer1 == null) startChantWithSP();
        if (musicPlayer1 != null && musicPlayer1.isPlaying()) {
            musicPlayer1.pause();
            ((ImageButton) findViewById(R.id.playPauseChantWithSP)).setImageResource(android.R.drawable.ic_media_play);
            createChantWithPrabhupadaNotification();
        } else if (musicPlayer1 != null) {
            musicPlayer1.start();
            ((ImageButton) findViewById(R.id.playPauseChantWithSP)).setImageResource(android.R.drawable.ic_media_pause);
            createChantWithPrabhupadaNotification();
        }
    }

    int spChantVol = 100;

    public void adjustSPChantVol(boolean up) {
        spChantVol += up ? 10 : -10;
        spChantVol = spChantVol > 100 ? 100 : Math.max(spChantVol, 0);
        volume.set(audio.size(), spChantVol);
        float v1 = (float) (1 - (Math.log(MAX_VOLUME - spChantVol) / Math.log(MAX_VOLUME)));
        if (musicPlayer1 != null) musicPlayer1.setVolume(v1, v1);
        createChantWithPrabhupadaNotification();
    }

    @Override
    public void showNoDataWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(k.this);
        TextView input = new TextView(k.this);
        input.setText(R.string.should_play_offline);
        builder.setTitle("Network Not Available")
                .setPositiveButton("Play Offline Audios", (dialog, id) -> {
                    h.nowPlaying = null;
                    startPlaying();
                    prefs.filterFlags |= flg[flg.length - 1];
                    setAdapters();
                })
                .setNegativeButton("Cancel", null)
                .setView(input).show();
    }

    public static final int mainTabs = 0, holderTabs = 1, plTabs = 2, songbookTabs = 3, externalAudioTabs = 4, authorTabs = 5, lyricsTabs = 6, fullScreenTabs = 7, wifiTabs = 8;

    @Override
    public void setTab(int tContext, int tabInd) {
        switch (tContext) {
            case mainTabs:
                final ImageButton b = mainTL.findViewById(R.id.addPlaylist);
                switch (tabInd) {
                    case 0:
                        list.setAdapter(mAdapter);
                        list.setOnItemClickListener(maharajaItemClickListener);
                        b.animate().alpha(0).scaleY(.1f).scaleX(.1f).setDuration(250).start();
                        list.postDelayed(() -> {
                            b.setVisibility(GONE);
                            findViewById(R.id.initWifi).setVisibility(VISIBLE);
                        }, 350);
                        break;
                    case 1:
                        list.setAdapter(plAdapter);
                        list.setOnItemClickListener(plItemClickListener);
                        b.setVisibility(VISIBLE);
                        b.animate().alpha(1).scaleY(.6f).scaleX(.6f).setDuration(250).start();
                        findViewById(R.id.initWifi).setVisibility(GONE);
                        break;
                    case 2:
                        list.setAdapter(sAdapter);
                        list.setOnItemClickListener(audioItemClickListener);
                        b.animate().alpha(0).scaleY(.1f).scaleX(.1f).setDuration(250).start();
                        list.postDelayed(() -> {
                            b.setVisibility(GONE);
                            findViewById(R.id.initWifi).setVisibility(VISIBLE);
                        }, 350);
                        break;
                }
                break;
            case holderTabs:
                maharaja = mAdapter.indices.get(tabInd);
                mAudioAdapter = new Adapter(adptrs.mAudio, mAdapter.indices.get(tabInd));
                holderList.setAdapter(mAudioAdapter);
                holderList.setOnItemClickListener(audioItemClickListener);
                Album m = maharajas.get(maharaja);
                ((TextView) albumHolder.findViewById(R.id.album_name)).setText(Html.fromHtml(m.name));
                findViewById(R.id.album_name).animate().translationY(-5 * dp10).alpha(0).setDuration(0).start();
                findViewById(R.id.album_name).animate().translationY(0).alpha(1).setDuration(animDuration).start();
                if (prefs.fixedBackground == 0) {
                    String arte = "arte_" + m.arteInd;
                    final File file = new File(INTERNAL_DIR, arte);
                    ImageView imageView = holderTL.findViewById(R.id.arte);
                    if (file.exists())
                        imageView.setImageURI(Uri.fromFile(file));
                    else {
                        imageView.setImageResource(R.drawable.haribol);
                        holderTL.findViewById(R.id.arte).setBackgroundColor(Color.argb(prefs.a, prefs.r, prefs.g, prefs.b));
                    }
                }
                setDrawerUI(maharaja);
                break;
            case plTabs:
                if (tabInd == 0) {
                    holderList.setAdapter(favAdapter);
                    ((TextView) albumHolder.findViewById(R.id.album_name)).setText(R.string.fav);
                    drawerList.setAdapter(dMenuAdapter = new Adapter(adptrs.dList, 4));
                } else if (tabInd == 1) {
                    ((TextView) albumHolder.findViewById(R.id.album_name)).setText(R.string.offline);
                    holderList.setAdapter(offlineAdapter);
                    drawerList.setAdapter(dMenuAdapter = new Adapter(adptrs.dList, 5));
                } else {
                    plAudioAdapter = new Adapter(adptrs.plAudios, plAdapter.indices.get(tabInd - 2));
                    holderList.setAdapter(plAudioAdapter);
                    ((TextView) albumHolder.findViewById(R.id.album_name)).setText(playlist.get(plAdapter.indices.get(tabInd - 2)));
                    drawerList.setAdapter(dMenuAdapter = new Adapter(adptrs.dList, 0));
                }
                holderList.setOnItemClickListener(audioItemClickListener);
                break;
            case songbookTabs:
                if (prefs.fixedBackground == 0) {
                    ((ImageView) holderTL.findViewById(R.id.arte)).setImageResource(R.drawable.haribol);
                    holderTL.findViewById(R.id.arte).setBackgroundColor(Color.argb(prefs.a, prefs.r, prefs.g, prefs.b));
                }
                ((TextView) albumHolder.findViewById(R.id.album_name)).setText(R.string.vsongbook);
                holderList.setAdapter(authorAdapter);
                holderList.setOnItemClickListener(authorItemClickListener);
                break;
            case externalAudioTabs:
                if (prefs.fixedBackground == 0) {
                    ((ImageView) holderTL.findViewById(R.id.arte)).setImageResource(R.drawable.haribol);
                    holderTL.findViewById(R.id.arte).setBackgroundColor(Color.argb(prefs.a, prefs.r, prefs.g, prefs.b));
                }
                ((TextView) albumHolder.findViewById(R.id.album_name)).setText(R.string.ext_storage);
                holderList.setAdapter(eAdapter);
                holderList.setOnItemClickListener(null);
                break;
            case authorTabs:
                int i = authorAdapter.indices.get(tabInd);
                ((TextView) albumHolder.findViewById(R.id.album_name)).setText(Html
                        .fromHtml("<a href = \"" + authors.get(i).link + "\">" + authors.get(i).name + "</a>"));
                holderList.setAdapter(lyricsAdapter = new Adapter(adptrs.lyrics, i));
                holderList.setOnItemClickListener(lyricsItemClickListener);
                if (prefs.fixedBackground == 0) {
                    String arte = "a" + i;
                    final File file1 = new File(INTERNAL_DIR, arte);
                    final ImageView imageView1 = holderTL.findViewById(R.id.arte);
                    if (!arte.isEmpty() && file1.exists())
                        imageView1.setImageURI(Uri.fromFile(file1));
                    else {
                        imageView1.setImageResource(R.drawable.haribol);
                        holderTL.findViewById(R.id.arte).setBackgroundColor(Color.argb(prefs.a, prefs.r, prefs.g, prefs.b));
                        (new Thread(() -> {
                            downloadFile(vSongBaseUrl + file1.getName() + ".jpg", file1.getName());
                            runOnUiThread(() -> {
                                if (holderTL.tContext == authorTabs)
                                    imageView1.setImageURI(Uri.fromFile(file1));
                            });
                        })).start();
                    }
                }
                break;
            case lyricsTabs:
                holderList.setAdapter(null);
                holderTL.findViewById(R.id.lyricsHolder).setVisibility(VISIBLE);
                findViewById(R.id.invertColor).setVisibility(VISIBLE);
//                ((ScrollView)holderList.findViewById(R.id.lyricsHolder)).smoothScrollTo(0,0);
                i = lyricsAdapter.indices.get(tabInd);
                ((TextView) albumHolder.findViewById(R.id.album_name)).setText(lyrics.get(i).name);
                String l = fetchLyrics(i, false);
//                Toast.makeText(k.this, i + "",Toast.LENGTH_LONG).show();
                ((TextView) holderTL.findViewById(R.id.lyrics)).setText(Html.fromHtml(l.startsWith("<") ? l
                        : "<p style=\"text-align: center; font-style: italic\">" + l.replaceAll("\n", "<br/>") + "</p>"));
                if (prefs.fixedBackground == 0) {
                    ((ImageView) holderTL.findViewById(R.id.arte)).setImageResource(R.drawable.haribol);
                    holderTL.findViewById(R.id.arte).setBackgroundColor(Color.argb(240, prefs.r, prefs.g, prefs.b));
                }
                break;
            case fullScreenTabs:
                switch (tabInd) {
                    case 0:
                        fsList.setAdapter(new Adapter(adptrs.Q, -1));
                        fullScreenTL.findViewById(R.id.lyricsHolder).setVisibility(GONE);
                        findViewById(R.id.volumeControls).setVisibility(GONE);
                        fullscreen.findViewById(R.id.invertColor1).setVisibility(GONE);
                        break;
                    case 1:
                        ((ListView) fullScreenTL.findViewById(R.id.list)).setAdapter(null);
                        fullScreenTL.findViewById(R.id.lyricsHolder).setVisibility(GONE);
                        findViewById(R.id.volumeControls).setVisibility(VISIBLE);
                        fullscreen.findViewById(R.id.invertColor1).setVisibility(GONE);
                        break;
                    case 2:
                        ((ListView) fullScreenTL.findViewById(R.id.list)).setAdapter(null);
                        fullScreenTL.findViewById(R.id.lyricsHolder).setVisibility(VISIBLE);
                        findViewById(R.id.volumeControls).setVisibility(GONE);
                        fullscreen.findViewById(R.id.invertColor1).setVisibility(VISIBLE);
                        break;
                }
            case wifiTabs:
                TextView w = findViewById(R.id.w);
                findViewById(R.id.unselectAll).setVisibility(GONE);
                switch (tabInd) {
                    case 0:
                        ((ImageView) wifiTL.findViewById(R.id.arte)).setImageResource(R.drawable.haribol);
                        wifiList.setAdapter(offlineAdapter);
                        wifiList.setDropListener(mDropListener);
                        w.setText(R.string.offline_audio);
                        break;
                    case 1:
                        ((ImageView) wifiTL.findViewById(R.id.arte)).setImageResource(R.color.colorPrimaryDark);
                        wifiList.setAdapter(arteAdapter);
                        wifiList.setDropListener(null);
                        w.setText("Offline Arte");
                        break;
                    case 2:
                        wifiList.setDropListener(mDropListener);
                        wifiList.setAdapter(vbAdapter);
                        w.setText("Offline Vedabase Files");
                }
        }
    }

    @Override
    public void hideFullScreenMenu() {
        fsMenu.setAdapter(null);
    }

    static class AudioViewHolder {
        ImageView arte;
        TextView title, artist, datePlace, author, type, lang;
        ImageButton download, like, menu;
    }

    static class ListViewHolder {
        ImageView arte, menu;
        TextView title, albumDetails, lang;
    }

    Stack<ArrayList<Integer>> downloadOverWiFiStack = new Stack<>();

    enum adptrs {
        dList, dMaharaja, fullScreenMenu, playlist, artist, authors, lyrics, plAudios, mAudio,
        Q, fav, offline, allSongs, ls, external, downloadManager, arts, storage, bookmark, clip, vedabase
    }

    public class SearchAdapter extends BaseAdapter {
        String[] options;

        @Override
        public int getCount() {
            int n = searchResults.size();
            if (n == 0) {
                searchResults.add(new SearchList("", -1, -1, -1));
                return 1;
            } else
                return Math.min(n, maxSearchResults);
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            AudioViewHolder holder;
            if (view == null) {
                view = LayoutInflater.from(k.this).inflate(R.layout.lia, viewGroup, false);
                holder = new AudioViewHolder();
                holder.arte = view.findViewById(R.id.arte);
                holder.title = view.findViewById(R.id.title);
                holder.artist = view.findViewById(R.id.artist);
                holder.datePlace = view.findViewById(R.id.date_place);
                holder.author = view.findViewById(R.id.author);
                holder.download = view.findViewById(R.id.download_);
                holder.like = view.findViewById(R.id.like);
                holder.menu = view.findViewById(R.id.list_menu);
                holder.type = view.findViewById(R.id.type);
                holder.lang = view.findViewById(R.id.lang);
                view.setTag(holder);
            } else {
                holder = (AudioViewHolder) view.getTag();
            }
            SearchList s = searchResults.get(i);
            switch (s.type) {
                case audioItem:
                    options = new String[]{"Share", "Add to Playlist", "Play Next", "Play", "Add to Queue", "download", "Go to Album"};
                    Audio a = new Audio(audio.get(s.ind));
                    a.place = places[a.placeInd];
                    a.title = (a.title.replaceAll("(?i)" + query_, "<span style=\"background-color: #FFFF00\"><font size=\"2\" color=\"blue\">" + query_.toUpperCase() + "</font></span>"));//(query_,"<mark>" + query_ + "</mark>"));
                    a.place = (a.place.replaceAll("(?i)" + query_, "<span style=\"background-color: #FFFF00\"><font size=\"2\" color=\"blue\">" + query_.toUpperCase() + "</font></span>"));
                    int authorInd = a.getLyricsInd() < 0 ? -1 : lyrics.get(a.getLyricsInd()).authorInd;
                    String date = a.date == 0 ? "" : simpleDateFormat.format(a.date * 3600000 * 24 + baseDate).replaceAll("(?i)" + query_, "<span style=\"background-color: #FFFF00\"><font size=\"2\" color=\"blue\">" + query_.toUpperCase() + "</font></span>"),
                            author = authorInd < 0 ? "" : authors.get(authorInd).name.replaceAll("(?i)" + query_, "<span style=\"background-color: #FFFF00\"><font size=\"2\" color=\"blue\">" + query_.toUpperCase() + "</font></span>");

                    setAudioItem(holder, i, a, date + a.place, author, options);
                    holder.type.setText("Song");
                    break;
                case maharajaItem:
                    options = new String[]{"Play", "Shuffle All", "Add to Queue", "Add to Playlist", "Add to Favourites", "Remove from Favourites", "Download All"};
                    setAdapterMenu(holder.menu, null, i, options);
                    Album m = maharajas.get(mAdapter.indices.get(s.ind));
                    holder.title.setText(Html.fromHtml(m.name.replaceAll("(?i)" + query_,
                            "<span style=\"background-color: #FFFF00\"><font size=\"2\" color=\"blue\">" + query_.toUpperCase() + "</font></span>")));
                    holder.datePlace.setText(m.getAudioIndices().size() + " songs");
                    holder.lang.setText(Lang[m.lang]);
                    String arte = "arte_" + m.arteInd;
                    File file = new File(INTERNAL_DIR, arte + "_comp");
                    if (file.exists()) {
                        holder.arte.setImageURI(Uri.fromFile(file));
                    }
                    holder.type.setText("Album");
                    break;
                case plItem:
                    options = new String[]{"*Share Playlist", "Play", "Shuffle All", "Add to Queue", "Add to Playlist", "Add to Favourites", "Remove from Favourites", "Download All", "Remove Playlist"};
                    setAdapterMenu(holder.menu, null, i, options);
                    holder.title.setText(Html.fromHtml(playlist.get(s.ind).replaceAll("(?i)" + query_,
                            "<span style=\"background-color: #FFFF00\"><font size=\"2\" color=\"blue\">" + query_.toUpperCase() + "</font></span>")));
                    holder.datePlace.setText(playlistAudios.get(s.ind).size() + " songs");
                    holder.arte.setImageResource(R.drawable.ic_launcher_round);
                    holder.type.setText("Playlist");
                    break;
                case authorItem:
                    Author a1 = authors.get(s.ind);
                    holder.title.setText(Html.fromHtml(a1.name.replaceAll("(?i)" + query_,
                            "<span style=\"background-color: #FFFF00\"><font size=\"2\" color=\"blue\">" + query_.toUpperCase() + "</font></span>")));
                    holder.datePlace.setText(a1.lyricsIndices.size() + " songs");
                    arte = "a" + s.ind;
                    file = new File(INTERNAL_DIR, arte + "_compressed1");
                    if (!arte.trim().isEmpty() && file.exists()) {
                        holder.arte.setImageURI(Uri.fromFile(file));
                    } else
                        holder.arte.setImageResource(R.drawable.ic_launcher_round);
                    holder.type.setText("Author");
                    break;
                case lyricsItem:
                    Lyrics l = lyrics.get(s.ind);
                    holder.title.setText(Html.fromHtml(l.name.replaceAll("(?i)" + query_,
                            "<span style=\"background-color: #FFFF00\"><font size=\"2\" color=\"blue\">" + query_.toUpperCase() + "</font></span>")));
                    holder.datePlace.setText(Html.fromHtml(authors.get(l.authorInd).name.replaceAll("(?i)" + query_,
                            "<span style=\"background-color: #FFFF00\"><font size=\"2\" color=\"blue\">" + query_.toUpperCase() + "</font></span>")));
                    arte = "a" + l.authorInd;
                    file = new File(INTERNAL_DIR, arte + "_compressed1");
                    if (file.exists() && file.length() > 10) {
                        holder.arte.setImageURI(Uri.fromFile(file));
                    } else
                        holder.arte.setImageResource(R.drawable.ic_launcher_round);
                    holder.type.setText("Lyrics");
                    break;
                case -1:
                    holder.title.setText(Html.fromHtml("<i><small>No results found...</small></i>"));
                    holder.datePlace.setText("");
            }
            if (s.type == audioItem) {
                holder.download.setVisibility(VISIBLE);
                holder.like.setVisibility(VISIBLE);
            } else {
                holder.author.setText("");
                holder.artist.setText("");
                holder.download.setVisibility(GONE);
                holder.like.setVisibility(GONE);
            }
            return view;
        }
    }

    public class Adapter extends BaseAdapter {
        String[] txt = {"Library", "Video Library", "Song Book", "\uD83D\uDC96 Buy Books \uD83D\uDC96", "Favourites",
                "Offline", "Playing Queue", "Now Playing", "Srila Prabhupada Stickers", "Preferences", "More Apps", "About Us"}, options;
        int[] resources = {R.drawable.music_lib, R.drawable.video_library, R.drawable.book, R.drawable.book, R.drawable.thumb_pink, R.drawable.download,
                R.drawable.music_q, R.drawable.music_note, R.drawable.ic_baseline_photo_24, R.drawable.setting, R.drawable.near_me, R.drawable.info};
        ArrayList<Integer> indices = new ArrayList<>();
        boolean[] sortAscending = {true, true, true, true};
        boolean[] selectedItems = null;
        int count = resources.length, layoutId = R.layout.lid, ind, wifiSelectedItems = 0;
        adptrs a;
        boolean isAudioHolder;

        Adapter(adptrs a, int ind) {
            this.a = a;
            this.ind = ind;
            indices.clear();
            switch (a) {
                case fullScreenMenu:
                    layoutId = R.layout.lifm;
                    txt = new String[]{"Add to Playlist", "Go to Album", "Share", "Favourite", "Tag Lyrics"};
                    resources = new int[]{R.drawable.music_q, R.drawable.ic_launcher_round,
                            R.drawable.ic_reply_black_24dp, R.drawable.fav, R.drawable.book};
                    count = resources.length;
                    break;
                case dMaharaja:
                    indices.addAll(maharajas.get(ind).subAlbumIndices);
                    count = indices.size();
                    layoutId = R.layout.lidm;
                    break;
                case playlist:
                    options = new String[]{"*Share Playlist", "Play", "Shuffle All", "Add to Queue", "Add to Playlist", "Add to Favourites", "Remove from Favourites", "Download All", "Remove Playlist"};
                    count = playlist.size();
                    for (int i = 0; i < count; i++) indices.add(i);
                    count += 2;
                    layoutId = R.layout.lip;
                    break;
                case bookmark:
                    count = bookmarks.size();
                    layoutId = R.layout.lib;
                    break;
                case clip:
                    count = audioClips.size();
                    layoutId = R.layout.lib;
                    break;
                case artist:
                    options = new String[]{"Play", "Shuffle All", "Add to Queue", "Add to Playlist", "Add to Favourites", "Remove from Favourites", "Download All"};
                    indices.addAll(maharajas.get(ind).subAlbumIndices);
                    count = indices.size();
                    layoutId = R.layout.lip;
                    break;
                case authors:
                    count = authors.size();
                    for (int i = 0; i < count; i++) indices.add(i);
                    layoutId = R.layout.lip;
                    break;
                case lyrics:
                    indices = authors.get(ind).lyricsIndices;
                    count = indices.size();
                    layoutId = R.layout.lip;
                    break;
                case fav:
                    options = new String[]{"Share", "Add to Queue", "Add to Playlist", "Play Next", "Play", "download", "Go to Album", "Tag Lyrics"};
                    for (int i = 0; i < audio.size(); i++) if (favourites.get(i)) indices.add(i);
                    count = indices.size();
                    layoutId = R.layout.liah;
                    break;
                case offline:
                    options = new String[]{"Share", "Add to Queue", "Add to Playlist", "Play Next", "Play", "download", "Go to Album", "Tag Lyrics"};
                    layoutId = R.layout.liah;
                    for (int i = 0; i < audio.size(); i++) {
                        File file = new File(INTERNAL_DIR, "bsrm_" + audio.get(i).id + ".mp3");
                        if (file.exists()) indices.add(i);
                    }
                    count = indices.size();
                    break;
                case storage:
                    layoutId = R.layout.lias;
                    options = new String[]{};
                    for (int i = 0; i < audio.size(); i++) {
                        File file = new File(INTERNAL_DIR, "bsrm_" + audio.get(i).id + ".mp3");
                        if (file.exists()) indices.add(i);
                    }
                    count = indices.size();
                    break;
                case plAudios:
                    options = new String[]{"Share", "Remove from Playlist", "Play Next", "Play", "Add to Queue", "download", "Go to Album", "Add to Playlist", "Tag Lyrics"};
                    indices.addAll(playlistAudios.get(ind));
                    count = indices.size();
                    layoutId = R.layout.liah;
                    break;
                case mAudio:
                    options = new String[]{"Share", "Add to Playlist", "Play Next", "Play", "Add to Queue", "download", "Tag Lyrics"};
                    indices.addAll(maharajas.get(ind).getAudioIndices());
                    count = indices.size();
                    layoutId = R.layout.lia;
                    break;
                case Q:
                    options = new String[]{"Share", "Remove from Queue", "Add to Playlist", "Play Next", "Play", "download", "Go to Album", "Tag Lyrics"};
                    indices = mServ.Queue;
                    count = indices.size();
                    layoutId = R.layout.liah;
                    break;
                case allSongs:
                    options = new String[]{"Share", "Add to Playlist", "Play Next", "Play", "Add to Queue", "download", "Go to Album", "Tag Lyrics"};
                    count = audio.size();
                    for (int i = 0; i < count; i++) indices.add(i);
                    layoutId = R.layout.lia;
                    break;
                case external:
                    options = new String[]{"Share", "Add to Playlist", "Play Next", "Play", "Add to Queue", "download", "Go to Album"};
                    count = deviceSongList.size();
                    for (int i = 0; i < count; i++) indices.add(i);
                    layoutId = R.layout.lia;
                    break;
                case ls:
                    options = new String[]{"Share", "Add to Playlist", "Play Next", "Play", "Add to Queue", "download", "Go to Album"};
                    indices = lyrics.get(ind).audioIndices;
                    count = indices.size();
                    layoutId = R.layout.lia;
                    break;
                case downloadManager:
                    count = downloadQStack.size();
                    layoutId = R.layout.lid_;
                    break;
                case arts:
                    arteList.clear();
                    arteList.add("haribol");
                    arteList.add("sp");
                    arteList.add("background_img.jpg");
                    arteList.add("bg1.jpg");
                    for (int i = 0; i < 500; i++) {
                        File file = new File(INTERNAL_DIR, "arte_" + i);
                        if (file.exists() && file.length() > 10) arteList.add("arte_" + i);
                        file = new File(INTERNAL_DIR, "arte_" + i + "_comp");
                        if (file.exists() && file.length() > 10)
                            arteList.add("arte_" + i + "_comp");
                    }
                    for (int i = 0; i < authors.size(); i++) {
                        File file = new File(INTERNAL_DIR, "a" + i);
                        if (file.exists() && file.length() > 10) arteList.add("a" + i);
                        file = new File(INTERNAL_DIR, "a" + i + "_compressed1");
                        if (file.exists() && file.length() > 10)
                            arteList.add("a" + i + "_compressed1");
                    }
                    count = arteList.size();
                    layoutId = R.layout.li_arte;
                    break;
                case vedabase:
                    count = offlineVBFiles.size();
                    for (int i = 0; i < count; i++) indices.add(i);
                    layoutId = R.layout.lip;
            }
            if (isAudioHolder = layoutId == R.layout.liah || layoutId == R.layout.lia || layoutId == R.layout.lias) {
                ArrayList<Integer> abc = new ArrayList<>();
                if ((prefs.filterFlags & flg[flg.length - 1]) == flg[flg.length - 1]) {
                    for (int i = 0; i < indices.size(); i++) {
                        File file = new File(INTERNAL_DIR, "bsrm_" + audio.get(indices.get(i)).id + ".mp3");
                        if (!file.exists() || file.length() < 1000)
                            abc.add(indices.get(i));
                    }
                    indices.removeAll(abc);
                }
                if ((prefs.filterFlags & (flg[flg.length - 1] - 1)) != (flg[flg.length - 1] - 1)) {
                    abc = new ArrayList<>();
                    for (int i = 0; i < indices.size(); i++) {
                        for (int j = 0; j < flg.length - 2; j++)
                            if (((prefs.filterFlags & flg[j]) == flg[j]) && audio.get(indices.get(i)).id / 100000 == j)
                                abc.add(indices.get(i));
                    }
                    indices.clear();
                    indices.addAll(abc);
                }
                count = indices.size();
            } else if (a == adptrs.dMaharaja || a == adptrs.artist) {
                ArrayList<Integer> abc;
                if ((prefs.filterFlags & (flg[flg.length - 1] - 1)) != (flg[flg.length - 1] - 1)) {
                    abc = new ArrayList<>();
                    for (int i = 0; i < indices.size(); i++) {
                        for (int j = 0; j < flg.length - 2; j++)
                            if (((prefs.filterFlags & flg[j]) == flg[j]) && maharajas.get(indices.get(i)).lang == j)
                                abc.add(indices.get(i));
                    }
                    indices.clear();
                    indices.addAll(abc);
                }
                count = indices.size();
            }
        }

        @Override
        public int getCount() {
            if (a == adptrs.downloadManager) count = downloadQStack.size();
            else if (a == adptrs.bookmark) count = bookmarks.size();
            else if (a == adptrs.clip) count = audioClips.size();
            else if (a == adptrs.offline) count = indices.size();
            else if (a == adptrs.vedabase) count = offlineVBFiles.size();
            else if (a == adptrs.arts) count = arteList.size();
            return count;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            if (isAudioHolder) {
                final AudioViewHolder holder;
                if (view == null || a == adptrs.dList || a == adptrs.downloadManager) {
                    view = LayoutInflater.from(k.this).inflate(layoutId, viewGroup, false);
                    holder = new AudioViewHolder();
                    holder.arte = view.findViewById(R.id.arte);
                    holder.title = view.findViewById(R.id.title);
                    holder.artist = view.findViewById(R.id.artist);
                    holder.datePlace = view.findViewById(R.id.date_place);
                    holder.author = view.findViewById(R.id.author);
                    holder.download = view.findViewById(R.id.download_);
                    holder.like = view.findViewById(R.id.like);
                    holder.menu = view.findViewById(R.id.list_menu);
                    holder.lang = view.findViewById(R.id.lang);
                    view.setTag(holder);
                } else {
                    holder = (AudioViewHolder) view.getTag();
                }
                if (a == adptrs.external) {
                    Audio a = deviceSongList.get(indices.get(i));
                    holder.title.setText(a.title);
                    holder.artist.setText(a.place);
                } else {
                    Audio a = audio.get(indices.get(i));
                    String datePlace = a.date == 0 ? places[a.placeInd] : simpleDateFormat.format(a.date * 3600000 * 24 + baseDate) + " " + places[a.placeInd];
                    int lyricsInd = a.getLyricsInd(), authorInd = lyricsInd < 0 ? -1 : lyrics.get(lyricsInd).authorInd;
                    setAudioItem(holder, i, a, datePlace, authorInd > 0 && authorInd < authors.size() - 1 ? "by " + authors.get(authorInd).name : "", options);
                    if (this.a == adptrs.offline && selectedItems != null) {
                        if (selectedItems[i]) {
                            view.setBackgroundResource(R.color.colorAccent1);
                            view.findViewById(R.id.tick).setVisibility(VISIBLE);
                        } else {
                            view.setBackgroundResource(R.color.colorAccent2);
                            view.findViewById(R.id.tick).setVisibility(GONE);
                        }
                    }
                }
            } else {
                final ListViewHolder holder;
                if (view == null) {
                    holder = new ListViewHolder();
                    view = LayoutInflater.from(k.this).inflate(layoutId, viewGroup, false);
                    holder.arte = view.findViewById(R.id.arte);
                    holder.title = view.findViewById(R.id.title);
                    holder.albumDetails = view.findViewById(R.id.album_details);
                    holder.menu = view.findViewById(R.id.list_menu);
                    holder.lang = view.findViewById(R.id.lang);
                    view.setTag(holder);
                } else
                    holder = (ListViewHolder) view.getTag();
                switch (a) {
                    case fullScreenMenu:
                        holder.arte.setImageResource(resources[i]);
                        holder.title.setText(txt[i]);
                        if (i == 2) {
                            holder.arte.animate().rotationY(180).setDuration(0).start();
                        }
                        view.animate().translationY(-4.5f * dp10 * i).alpha(0).setDuration(0).start();
                        view.findViewById(R.id.title).animate().translationX(200 * dp10).alpha(0).setDuration(0).start();
                        view.findViewById(R.id.title).animate().translationX(0).alpha(1).setDuration(250 + 50 * i).start();
                        view.animate().translationY(0).alpha(1).setDuration(150 + 50 * i).start();
                        return view;
                    case dList:
                        holder.arte.setImageResource(resources[i]);
                        holder.title.setText(txt[i]);
                        if (i == ind) {
                            btn = holder.arte;
                            btn.setPressed(true);
                        } else
                            holder.arte.setPressed(false);
                        return view;
                    case artist:
                        setAdapterMenu(holder.menu, null, i, options);
                    case dMaharaja:
                        Album m = maharajas.get(indices.get(i));
                        holder.title.setText(m.name);
                        holder.albumDetails.setText((m.subAlbumIndices.size() > 0 ? m.subAlbumIndices.size() + " Albums and " : "") + m.getCount() + " songs");
                        holder.lang.setText(Lang[m.lang]);
                        String arte = "arte_" + m.arteInd;
                        File file = new File(INTERNAL_DIR, arte + "_comp");
                        if (file.exists() && file.length() > 10) {
                            holder.arte.setImageURI(Uri.fromFile(file));
                        } else
                            holder.arte.setImageResource(R.drawable.ic_launcher_round);
                        break;
                    case lyrics:
                        holder.menu.setVisibility(GONE);
                        holder.title.setText(lyrics.get(indices.get(i)).name);
                        holder.albumDetails.setText(authors.get(lyrics.get(indices.get(i)).authorInd).name);
                        arte = "a" + lyrics.get(indices.get(i)).authorInd;
                        file = new File(INTERNAL_DIR, arte + "_compressed1");
                        if (file.exists() && file.length() > 10) {
                            holder.arte.setImageURI(Uri.fromFile(file));
                        } else
                            holder.arte.setImageResource(R.drawable.ic_launcher_round);
                        break;
                    case authors:
                        holder.menu.setVisibility(GONE);
                        Author author = authors.get(indices.get(i));
                        holder.title.setText(author.name);
                        holder.albumDetails.setText(author.lyricsIndices.size() + " songs");
                        arte = "a" + indices.get(i);
                        file = new File(INTERNAL_DIR, arte + "_compressed1");
                        if (file.exists() && file.length() > 10) {
                            holder.arte.setImageURI(Uri.fromFile(file));
                        } else
                            holder.arte.setImageResource(R.drawable.ic_launcher_round);
                        break;
                    case vedabase:
                        holder.menu.setVisibility(GONE);
                        String title = offlineVBFiles.get(i).replace(".mht", "");
                        int x;
                        if ((x = title.indexOf("#")) > 0) title = title.substring(0, x);
                        if (title.startsWith("cc")) {
                            title = title.replaceFirst("_", " ");
                            title = title.replaceFirst("_", " ");
                        } else
                            title = title.replaceFirst("_", " ");
                        holder.title.setText(title.toUpperCase().replaceAll("_", "."));
                        if (selectedItems != null) {
                            if (selectedItems[i]) {
                                view.setBackgroundResource(R.color.colorAccent1);
                                view.findViewById(R.id.tick).setVisibility(VISIBLE);
                            } else {
                                view.setBackgroundResource(R.color.colorAccent2);
                                view.findViewById(R.id.tick).setVisibility(GONE);
                            }
                        }
                        break;
                    case playlist:
                        if (i == 0) {
                            setAdapterMenu(holder.menu, null, i, options);
                            holder.title.setText(R.string.fav);
                            holder.albumDetails.setText(favAdapter.count + " songs");
                        } else if (i == 1) {
                            setAdapterMenu(holder.menu, null, i, options);
                            holder.title.setText(R.string.offline);
                            holder.albumDetails.setText(offlineAdapter.count + " songs");
                        } else {
                            setAdapterMenu(holder.menu, null, i, options);
                            holder.title.setText(playlist.get(indices.get(i - 2)));
                            holder.albumDetails.setText(playlistAudios.get(indices.get(i - 2)).size() + " songs");
                        }
                        break;
                    case bookmark:
                        String[] s1 = bookmarks.get(i).split("\\$b");
                        holder.arte.setImageResource(s1[2].equals("t") ? R.drawable.bookmark : R.drawable.bookmark_blue);
                        int[] d1 = getIndexFromUrl(s1[1]);
                        int t = d1[1];
                        holder.title.setText(Html.fromHtml(s1[0] + " <span style=\"color:gray\"><small><i>(marked at " +
                                (t > 60 ? t / 60 > 60 ? t / 3600 + "h:" + (t / 60) % 60 + "m:" + t % 60 + "s" : t / 60 + "m:" + t % 60 + "s" : t + " sec") + ")</i></small></span>"));
                        holder.albumDetails.setText(audio.get(d1[0]).title);
                        holder.menu.setOnClickListener(v -> AddBookmark(i));
                        break;
                    case clip:
                        s1 = audioClips.get(i).split("\\$b");
                        holder.arte.setImageResource(R.drawable.music_note);
                        holder.menu.setImageResource(R.drawable.delete);
                        d1 = getIndexFromUrl(s1[1]);
                        int st = d1[1], et = d1[2];
                        holder.title.setText(Html.fromHtml(s1[0] + " <span style=\"color:gray\"><small><i>(from " +
                                (st > 60 ? st / 60 > 60 ? st / 3600 + "h:" + (st / 60) % 60 + "m:" + st % 60 + "s" : st / 60 + "m:" + st % 60 + "s" : st + " sec") + " to " +
                                (et > 60 ? et / 60 > 60 ? et / 3600 + "h:" + (et / 60) % 60 + "m:" + et % 60 + "s" : et / 60 + "m:" + et % 60 + "s" : et + " sec") + ")</i></small></span>"));
                        holder.albumDetails.setText(audio.get(d1[0]).title);
                        final String t_ = s1[0];
                        holder.menu.setOnClickListener(v -> {
                            File f1 = getAudioClipFile();
                            f1 = new File(f1, t_ + ".mp3");
                            if (f1.exists()) f1.delete();
                            audioClips.remove(i);
                            clipAdapter.notifyDataSetChanged();
                        });
                        break;
                    case downloadManager:
                        final DownloadQ d = downloadQStack.get(count - i - 1);
                        File file1;
                        switch (d.type) {
                            case -10:
                            case AUDIO:
                                final Audio a = audio.get(d.id);
                                m = maharajas.get(a.albumId);
                                arte = "arte_" + m.arteInd;
                                file1 = new File(INTERNAL_DIR, arte + "_comp");
                                if (file1.exists() && file1.length() > 10)
                                    holder.arte.setImageURI(Uri.fromFile(file1));
                                else
                                    holder.arte.setImageResource(R.drawable.haribol);
                                holder.title.setText(a.title);
                                holder.albumDetails.setText(m.name);
                                if (d.type == -10 || i == 0) {
                                    mainTL.postDelayed(new Runnable() {
                                        @SuppressLint("DefaultLocale")
                                        @Override
                                        public void run() {
                                            long l = (new File(INTERNAL_DIR, a.id + "")).length();
                                            holder.albumDetails.setText(l < 1024 ? l + " Bytes" : (l = l / 1024) < 1024 ? l + " kB" : String.format("%.2f", l / 1024f) + " MB");
                                            if (downloadQStack.contains(d))
                                                mainTL.postDelayed(this, 250);
                                        }
                                    }, 50);
                                }
                                break;
                            case ARTE:
                                holder.title.setText("arte_" + d.id);
                                break;
                            case M_ARTE:
                                String s = "arte_" + d.id;
                                holder.title.setText(s);
                                break;
                            case Ly_ARTE:
                                holder.title.setText("lyrics_arte_" + d.id);
                                break;
                            case RES:
                                holder.title.setText(resFile[d.id]);
                        }
                        if (!(d.type == AUDIO || d.type == -10)) {
                            holder.arte.setImageResource(R.drawable.haribol);
                            holder.albumDetails.setText("");
                        }
                        if (i == 0 || d.type == -10) {
                            holder.menu.setImageResource(R.drawable.download);
                            holder.menu.setOnClickListener(null);
                        } else {
                            holder.menu.setImageResource(R.drawable.x);
                            holder.menu.setOnClickListener(view1 -> {
                                downloadQStack.remove(count - i - 1);
                                downloadManagerAdapter.notifyDataSetChanged();
                            });
                        }
                        break;
                    case arts:
                        if (i == 0) holder.arte.setImageResource(R.drawable.haribol);
                        else if (i == 1) holder.arte.setImageResource(R.drawable.sp);
                        else
                            holder.arte.setImageURI(Uri.fromFile(new File(INTERNAL_DIR, arteList.get(i))));
                        if (selectedItems != null) {
                            if (selectedItems[i]) {
                                view.setBackgroundResource(R.color.colorAccent1);
                                view.findViewById(R.id.tick).setVisibility(VISIBLE);
                            } else {
                                view.setBackgroundResource(R.color.colorAccent2);
                                view.findViewById(R.id.tick).setVisibility(GONE);
                            }
                        }
                }
            }
            return view;
        }
    }

    public void setAdapterMenu(ImageView btn, final Audio a, final int i, final String[] options) {
        btn.setOnClickListener(view -> {
            final PopupMenu popupMenu = new PopupMenu(k.this, view);
            for (int j = 0; j < options.length; j++)
                popupMenu.getMenu().add(0, j, j, options[j]);
            if (wifi.getVisibility() == VISIBLE || (h.nowPlaying != null && a != null && a.index == h.nowPlaying.index))
                popupMenu.getMenu().removeItem(5);
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                String title = menuItem.getTitle().toString();
                switch (title) {
                    case "Shuffle All":
                        mServ.Shuffle = true;
                    case "Play":
                        if (mainTL.getAlpha() == 1 && mainTL.tabInd == 0)
                            mServ.Queue = maharajas.get(mAdapter.indices.get(i)).getAudioIndices();
                        else if (mainTL.getAlpha() == 1 && mainTL.tabInd == 1)
                            mServ.Queue = playlistAudios.get(plAdapter.indices.get(i));
                        startPlaying(a);
                        break;
                    case "Add to Queue":
                        if (a != null)
                            mServ.Queue.add(a.index);
                        else if (mainTL.tabInd == 0)
                            mServ.Queue.addAll(maharajas.get(mAdapter.indices.get(i)).getAudioIndices());
                        else
                            mServ.Queue.addAll(playlistAudios.get(plAdapter.indices.get(i)));
                        break;
                    case "Add to Playlist":
                        if (a != null)
                            addToPlayList(view, a.index, -1, false);
                        else if (mainTL.tabInd == 0)
                            addToPlayList(view, -1, mAdapter.indices.get(i), false);
                        else
                            addToPlayList(view, -1, i, true);
                        break;
                    case "Remove from Playlist":
                        int plInd = plAdapter.indices.get(holderTL.tabInd - 2);
                        playlistAudios.get(plInd).remove(i);
                        plAudioAdapter = new Adapter(adptrs.plAudios, plAdapter.indices.get(holderTL.tabInd - 2));
                        holderList.setAdapter(plAudioAdapter);
                        plAdapter = new Adapter(adptrs.playlist, -1);
                        list.setAdapter(plAdapter);
                        break;
                    case "Remove from Queue":
                        mServ.Queue.remove(i);
                        if (fullscreen.getVisibility() == VISIBLE)
                            ((Adapter) fsList.getAdapter()).notifyDataSetChanged();
                        else
                            ((Adapter) holderList.getAdapter()).notifyDataSetChanged();
                        break;
                    case "Play Next":
                        if (!mServ.Queue.isEmpty())
                            mServ.Queue.add((mServ.nowPlayingQueueInd + 1) % mServ.Queue.size(), a.index);
                        else
                            mServ.Queue.add(a.index);
                        break;
                    case "Go to Album":
                        goToAlbum(a.albumId, holderTabs);
                        break;
                    case "download":
                        downloadTask = new DownloadTask(k.this);
                        downloadTask.execute(a.index);
                        break;
                    case "Tag Lyrics":
                        tagLyrics(a);
                    case "delete":
                        File file = new File(INTERNAL_DIR, "bsrm_" + a.id + ".mp3");
                        if (file.exists() && file.delete()) {
                            Toast.makeText(k.this, "File Deleted", Toast.LENGTH_SHORT).show();
                            offlineAdapter = new Adapter(adptrs.offline, -1);
                            if (albumHolder.getVisibility() == VISIBLE && holderTL.tContext == plTabs && holderTL.tabInd == 1)
                                holderList.setAdapter(offlineAdapter);
                        }
                        break;
                    case "Share":
                        shareSong(a.index);
                        break;
                    case "Add to Favourites":
                        if (mainTL.getAlpha() == 1 && mainTL.tabInd == 0)
                            addToPlayList(mAdapter.indices.get(i), false, -2);
                        addToPlayList(i, true, -2);
                        break;
                    case "*Share Playlist":
                        sharePlaylist(i);
                        break;
                    case "Remove from Favourites":
                        addToPlayList(i, true, -3);
                        break;
                    case "Download All":
                        ArrayList<Integer> downloadTasks;
                        if (mainTL.getAlpha() == 1 && mainTL.tabInd == 1) {
                            if (i == 0) downloadTasks = favAdapter.indices;
                            else if (i == 1) {
                                Toast.makeText(k.this, "Already Downloaded", Toast.LENGTH_SHORT).show();
                                break;
                            } else
                                downloadTasks = playlistAudios.get(plAdapter.indices.get(i - 2));
                        } else
                            downloadTasks = maharajas.get(mAdapter.indices.get(i)).getAudioIndices();
                        final ArrayList<Integer> finalDownloadTasks = downloadTasks;
                        if (!mServ.isWifiConnected()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(k.this);
                            TextView input = new TextView(k.this);
                            input.setText("Downloading all audios may incure significant data charges.");
                            builder.setTitle("Not Connected to WiFi")
                                    .setPositiveButton("Download Anyways", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            download(finalDownloadTasks);
                                        }
                                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    downloadOverWiFiStack.push(finalDownloadTasks);
                                }
                            }).setView(input).show();
                        } else
                            download(downloadTasks);
                        break;
                    case "Remove Playlist":
                        if (i < 2)
                            dBuilder.setMessage("Can not remove Default Playlists").show();
                        else {
                            int j = plAdapter.indices.get(i - 2);
                            playlistAudios.remove(j);
                            (new File(INTERNAL_DIR, playlist.get(j))).delete();
                            playlist.remove(j);
                            plAdapter = new Adapter(adptrs.playlist, -1);
                            list.setAdapter(plAdapter);
                        }
                        break;
                    default:
                        Toast.makeText(k.this, "Work in Progress", Toast.LENGTH_SHORT).show();
                }
                return false;
            });
            popupMenu.show();
        });
    }

    private void tagLyrics(final Audio a) {
        searchBar.setVisibility(VISIBLE);
        editText.setText(a.getLyricsInd() < 0 ? "" : lyrics.get(a.getLyricsInd()).name);
        editText.requestFocus();
        imm.toggleSoftInput(0, 0);
        findViewById(R.id.searchSettings).setVisibility(GONE);
        findViewById(R.id.sSettings).setVisibility(GONE);
        sList.clear();
        for (int i = 0; i < lyrics.size(); i++) {
            sList.add(new SearchList(lyrics.get(i).name, lyricsItem, title, i));
            sList.add(new SearchList(authors.get(lyrics.get(i).authorInd).name, lyricsItem, author, i));
        }
        for (int i = 0; i < authors.size(); i++)
            sList.add(new SearchList(authors.get(i).name, authorItem, title, i));
        searchList.setOnItemClickListener((adapterView, view, i, l) -> {
            SearchList s = searchResults.get(i);
            lyrics.get(s.ind).audioIndices.remove((Integer) a.index);
            a.ref = s.ind + "";
            lyrics.get(s.ind).audioIndices.add(a.index);
            onBackPressed();
            searchList.setOnItemClickListener(searchItemClickListener);
        });
    }

    @SuppressLint("DefaultLocale")
    public void setAudioItem(final AudioViewHolder holder, int i, final Audio a, String datePlace, String author, String[] options) {
        holder.title.setText(Html.fromHtml(a.title));
        holder.datePlace.setText(Html.fromHtml(datePlace));
        holder.author.setText(Html.fromHtml(author));
        final ArrayList<ArrayList<String>> textUrls = a.getTextUrls();
        if (textUrls != null) {
            holder.artist.setVisibility(VISIBLE);
            holder.artist.setText(textUrls.get(2).get(0) + " ");
            holder.artist.setOnClickListener(view -> {
                final PopupMenu popupMenu = new PopupMenu(k.this, view);
                for (int i1 = 0; i1 < textUrls.get(0).size(); i1++)
                    popupMenu.getMenu().add(0, i1, i1, textUrls.get(1).get(i1));
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    int i1 = menuItem.getOrder();
                    showVedabase(textUrls.get(0).get(i1));
                    return false;
                });
                popupMenu.show();
            });
        } else
            holder.artist.setVisibility(GONE);
        holder.lang.setText(Html.fromHtml(Lang[(int) (a.id / 100000)]));
        final File file = new File(INTERNAL_DIR, "bsrm_" + a.id + ".mp3");
        File file1 = new File(INTERNAL_DIR, "arte_" + a.arteInd + "_comp");
        File file2 = new File(INTERNAL_DIR, "arte_" + maharajas.get(a.albumId).arteInd + "_comp");
        if (file1.exists() && file1.length() > 10)
            holder.arte.setImageURI(Uri.fromFile(file1));
        else if (file2.exists() && file2.length() > 10)
            holder.arte.setImageURI(Uri.fromFile(file2));
        else
            holder.arte.setImageResource(R.drawable.ic_launcher_round);
        if (options.length == 0) {
            long l = file.length();
            holder.datePlace.setText(l < 1024 ? l + " Bytes" : (l = l / 1024) < 1024 ? l + " kB" : String.format("%.2f", l / 1024f) + " MB");
            holder.menu.setOnClickListener(view -> {
                if (file.exists() && file.delete()) {
                    Toast.makeText(k.this, "File Deleted", Toast.LENGTH_SHORT).show();
                    offlineAdapter = new Adapter(adptrs.offline, -1);
                    storageList.setAdapter(storageAdapter = new Adapter(adptrs.storage, -1));
                }
            });
            return;
        }
        if (file.exists()) {
            options[5] = "delete";
            holder.download.animate().alpha(1).setDuration(0).start();
        } else {
            options[5] = "download";
            holder.download.animate().alpha(0).setDuration(0).start();
        }
        setAdapterMenu(holder.menu, a, i, options);
        if (favourites.get(a.index)) {
            holder.like.setImageResource(R.drawable.thumb_pink);
            holder.like.animate().scaleY(0.5f).scaleX(0.5f).rotation(-60).setDuration(0).start();
            holder.like.animate().scaleY(0.8f).scaleX(0.8f).translationX(dp10 / 2f).rotation(0).setDuration(350).start();
        } else {
            holder.like.setImageResource(R.drawable.thumb);
            holder.like.animate().scaleX(1.3f).scaleY(1.3f).translationX(0).start();
        }
        holder.like.setOnClickListener(view -> {
            setLike(a.index);
            ImageButton imageButton1 = (ImageButton) view;
            if (favourites.get(a.index)) {
                imageButton1.setImageResource(R.drawable.thumb_pink);
                imageButton1.animate().scaleY(0.6f).scaleX(0.6f).rotation(-30).setDuration(0).start();
                imageButton1.animate().scaleY(.9f).scaleX(.9f).translationX(dp10 / 2f).rotation(0).setDuration(350).start();
            } else {
                imageButton1.setImageResource(R.drawable.thumb);
                imageButton1.animate().scaleX(0.9f).scaleY(0.9f).translationX(0).start();
            }
        });
    }

    public static ArrayList<Audio> audio = new ArrayList<>();
    public static ArrayList<Album> maharajas = new ArrayList<>();
    public static ArrayList<Author> authors = new ArrayList<>();
    public static ArrayList<Lyrics> lyrics = new ArrayList<>();
    public static String audioShareBaseUrl = "https://srila-prabhupada-vani.firebaseapp.com/",
            vSongBaseUrl = "https://vaishnava-songs.firebaseapp.com/",
            ytUrl = "https://www.youtube.com/channel/UCAmUrQvgmwXuc0eSnH69L5w",
            fbUrl = "https://www.facebook.com/Srila-Prabhupada-Lila-158671941264206/", twitterUrl = "", linkedInUrl = "",
            web = "http://www.radhanathswami.com/srila-prabhupada/",
            youTubeUrl = "https://www.youtube.com/channel/UCAmUrQvgmwXuc0eSnH69L5w/playlists",
            notesUrl = "https://keep.google.com/",
            vedaBaseUrl = "https://vedabase.io/en/library/", APP_NAME;
    public static File INTERNAL_DIR;
    public static ArrayList<String[]> fileBaseUrls = new ArrayList<>();
    public static ArrayList<String> playlist = new ArrayList<>(), arteList = new ArrayList<>(),
            bookmarks = new ArrayList<>(), audioClips = new ArrayList<>(), offlineVBFiles = new ArrayList<>();
    public static String bookmark = "";
    public static ArrayList<Boolean> favourites = new ArrayList<>();
    public static ArrayList<Integer> volume = new ArrayList<>();
    String newPlaylistName = "untitled";
    public static ArrayList<ArrayList<Integer>> playlistAudios = new ArrayList<>();
    public static int maharaja = 0;
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd, yyyy", Locale.getDefault());
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-mm-dd");
    public static final int REPEAT_ONE = 0, REPEAT_LIST = 1, REPEAT_NONE = 2;

    public String fetchLyrics(int lyricsInd, boolean update) {
        File file = new File(INTERNAL_DIR, "lyrics_" + lyricsInd);
        BufferedReader reader;
        URLConnection urlConnection;
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        boolean addBr = false;
        try {
            if (file.exists() && !update)
                reader = new BufferedReader(new InputStreamReader(openFileInput(file.getName())));
            else {
                urlConnection = new URL(vSongBaseUrl + "lyrics_" + lyricsInd + ".html").openConnection();
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                lyrics.get(lyricsInd).lastUpdate = (new Date()).getTime() / 3600000 / 24;
            }
            while ((line = reader.readLine()) != null) {
                if (line.contains("<title>")) continue;
                if (line.contains("<pre>")) addBr = true;
                else if (line.contains("</pre>")) addBr = false;
                stringBuilder.append(line).append(addBr ? "<br/>" : "\n");
            }
            reader.close();
        } catch (Exception ignored) {
        }
        line = stringBuilder.toString();
        if ((!update) && (file.exists() || line.isEmpty())) return stringBuilder.toString();
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream((file));
            fileOutputStream.write(line.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    public static JSONObject getJsonObj(String fileName, Context context) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return new JSONObject(sb.toString());
        } catch (IOException | JSONException e) {
//            Toast.makeText(context,e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    public static void fetchDailyNectar(Context context, boolean forceFetch) {
        String urlString = "http://www.bhaktirasamritaswami.info/";
        Date d = new Date();
        String weekDay = "nectar_" + android.text.format.DateFormat.format("EEEE", d).toString(),
                day = "nectar_" + android.text.format.DateFormat.format("dd", d).toString(),
                date = "nectar_" + android.text.format.DateFormat.format("yyyy_MMMM_dd", d).toString(),
                month = "nectar_" + android.text.format.DateFormat.format("MMMM-dd", d).toString();

        File dailyNectrYear = new File(INTERNAL_DIR + "/" + month);

        try {
            if (forceFetch || !(dailyNectrYear.exists() &&
                    Objects.requireNonNull(getJsonObj(month, context)).getString("date").equals(date))) {
                BufferedReader reader;
                String line = "";
                try {
                    URLConnection urlConnection = new URL(urlString).openConnection();
                    reader = new BufferedReader(new InputStreamReader(
                            urlConnection.getInputStream()/*, "iso-8859-1"*/));
                    while ((line = reader.readLine()) != null && !line.contains("Daily Nectar")) ;
                    line = reader.readLine();
                    reader.close();
                } catch (Exception ignored) {
                }
                if (line != null && !line.equals("")) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("date", date);
                    jsonObject.put("weekday", weekDay);
                    jsonObject.put("daily_nectar", line.replace("<span>", ""));
                    try {
                        String str[] = {month, day, weekDay};
                        for (String aStr : str) {
                            FileOutputStream out = new FileOutputStream(INTERNAL_DIR.getPath() + '/' + aStr);
                            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out);
                            outputStreamWriter.write(jsonObject.toString());
                            outputStreamWriter.close();
                        }
                    } catch (IOException ignored) {
                    }
                }
            }
        } catch (JSONException ignored) {
        }
    }

    static public String getDailyNectar(Context activity) {
        String dailyNectar = "";
        Date d = new Date();
        String weekDay = "nectar_" + android.text.format.DateFormat.format("EEEE", d).toString(),
                day = "nectar_" + android.text.format.DateFormat.format("dd", d).toString(),
                month = "nectar_" + android.text.format.DateFormat.format("MMMM-dd", d).toString();

        JSONObject jsonObject = null;

        File file = new File(INTERNAL_DIR + "/" + month);
        File file1 = new File(INTERNAL_DIR + "/" + day);
        File file2 = new File(INTERNAL_DIR + "/" + weekDay);
        if (file.exists()) {
            jsonObject = getJsonObj(month, activity);
        } else if (file1.exists()) {
            jsonObject = getJsonObj(day, activity);
        } else if (file2.exists()) {
            jsonObject = getJsonObj(weekDay, activity);
        }

        if (jsonObject != null) {
            try {
                dailyNectar = "Daily Nectar:\n\n" + jsonObject.getString("daily_nectar") + "\n";
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return dailyNectar;
    }

    public static void fetchSrilaPrabhupadaQuotes() {
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < prefs.no_of_days; i++) {
            fetchSrilaPrabhupadaQuotes(calendar);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    public static void fetchSrilaPrabhupadaQuotes(Calendar calendar) {
        String date = android.text.format.DateFormat.format("MMMM_dd", calendar.getTime()).toString();
        File file = new File(INTERNAL_DIR, date + ".png");
        if (!file.exists()) {
            downloadFile(spQuoteBaseUrl + date + ".webp", file.getName());
        }
    }

    @SuppressLint("WakelockTimeout")
    public static void downloadFile(String urlString, String fileName) {
//        https://stackoverflow.com/questions/6237079/resume-http-file-download-in-java
        mWakeLock.acquire();
        File file = new File(INTERNAL_DIR, fileName);
        /*
//        =====================================================================================
        long length = 0;
        if(file.exists()) length = file.length();
        URL url = null;
        try {
            url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            FileOutputStream out;
            if(length==0)
                out = new FileOutputStream(file.getPath());
            else {
                connection.setRequestProperty("Range", "bytes="+length+"-");
                out = new FileOutputStream(file.getPath(),true);
            }
            connection.connect();
            BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
            byte[] buf = new byte[1024];
            int n = in.read(buf);
            if(n!=-1 && !(new String(buf,"UTF-8")).startsWith("<"))
                do out.write(buf, 0, n); while (-1 != (n = in.read(buf)));
            out.close(); in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } //*/
        if (!file.exists() || file.length() < 10) {
            try {
                URL url = new URL(urlString);
                InputStream in = new BufferedInputStream(url.openStream());
                FileOutputStream out = new FileOutputStream(file.getPath());
                byte[] buf = new byte[1024];
                int n = in.read(buf);
                if (n != -1 && !(new String(buf, StandardCharsets.UTF_8)).startsWith("<")) {
                    do out.write(buf, 0, n); while (-1 != (n = in.read(buf)));
                } else file.delete();
                out.close();
                in.close();
            } catch (IOException ignored) {
            }
        }//*/
        if (mWakeLock.isHeld()) mWakeLock.release();
    }

    public static final String CHANT_WITH_PRABHUPADA = "srila_prabhupada_chanting.mp3";
    final static String spQuoteBaseUrl = "https://raw.githubusercontent.com/srila-prabhupada-vani/q/master/";

    static String[] resUrl = new String[]{vSongBaseUrl + "srila_prabhupada_chanting.mp3"},
            resFile = new String[]{CHANT_WITH_PRABHUPADA};

    Runnable downloadResources = new Runnable() {
        @Override
        public void run() {
            if (!mServ.isNetworkAvailable()) return;
            runOnUiThread(showWarning);
            if (!new File(INTERNAL_DIR, resFile[resUrl.length - 1]).exists()) {
                for (int i = 0; i < authors.size(); i++)
                    downloadQStack.add(0, new DownloadQ(i, Ly_ARTE));
                for (int i = 0; i < maxArte; i++) downloadQStack.add(0, new DownloadQ(i, M_ARTE));
                for (int i = 0; i < resUrl.length; i++)
                    downloadQStack.add(0, new DownloadQ(i, RES));
                removeDownloadedResourcesFromStack();
            }
            downloadManager.run();
            runOnUiThread(() -> {
                ((ImageButton) findViewById(R.id.chantWithSPFAB)).setImageURI(Uri.fromFile(new File(INTERNAL_DIR, "a1_compressed1")));
                ((ImageButton) toolbar.findViewById(R.id.vedabasebtn)).setImageURI(Uri.fromFile(new File(INTERNAL_DIR, "a1_compressed1")));
                ((ImageButton) drawer.findViewById(R.id.vedabasebtn)).setImageURI(Uri.fromFile(new File(INTERNAL_DIR, "a1_compressed1")));
            });
            fetchSrilaPrabhupadaQuotes();
            fetchDailyNectar(k.this, false);
        }
    };

    public static void removeDownloadedResourcesFromStack() {
        for (int i = 0; i < downloadQStack.size(); i++) {
            DownloadQ d = downloadQStack.get(i);
            String fileName = "";
            switch (d.type) {
                case M_ARTE:
                    fileName = "arte_" + d.id + "_comp";
                    break;
                case Ly_ARTE:
                    fileName = "a" + d.id + "_compressed1";
                    break;
                case RES:
                    fileName = resFile[d.id];
                    break;
            }
            if (!fileName.isEmpty() && (new File(INTERNAL_DIR, fileName)).exists()) {
                downloadQStack.remove(i);
                i--;
            }
        }
    }

    private static class RunThread extends AsyncTask<Runnable, Integer, String> {
        @Override
        protected String doInBackground(Runnable... t) {
            t[0].run();
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadTask extends AsyncTask<Integer, Integer, String> {
        boolean shouldStartPlaying = false, showProgressDialogue = true;
        int audioInd = 0;
        DownloadQ downloadQ;
        Context context;

        DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Integer... audioIndex) {
            audioInd = audioIndex[0];
            downloadQStack.add(downloadQ = new DownloadQ(audioIndex[0], -10));
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            File file = new File(INTERNAL_DIR, "bsrm_" + audio.get(audioInd).id + ".mp3");
            if (file.exists()) return null;
            try {
                URL url = new URL(audio.get(audioInd).getUrl());
                connection = (HttpURLConnection) url.openConnection();
                long contentLength = connection.getContentLength();
                connection.connect();
                input = connection.getInputStream();
                output = new FileOutputStream(file);

                byte[] data = new byte[4098];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                    total += count;
                    if (contentLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / contentLength));
                    if (shouldStartPlaying) {
                        publishProgress(-1);
                        shouldStartPlaying = false;
                    }
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null) output.close();
                    if (input != null) input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @SuppressLint("WakelockTimeout")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mWakeLock.acquire();
            downloadingAudio = true;
            showWarning.run();
            if (!showProgressDialogue) return;
            if (shouldShowProgressDialogue)
                mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            if (!showProgressDialogue) return;
            else if (progress[0] == -1) {
                startPlaying(audio.get(audioInd));
                mProgressDialog.dismiss();
            } else if (!shouldShowProgressDialogue)
                return;
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (mWakeLock.isHeld()) mWakeLock.release();
            downloadingAudio = false;
            if (!showProgressDialogue) return;
            mProgressDialog.dismiss();
            downloadQStack.remove(downloadQ);
            updateDownloads.run();
            if (!downloadManagerRunning) hideWarning.run();
        }
    }

    public void getAudio() {
        File f = new File(INTERNAL_DIR, "vedabase");
        if (!f.exists()) f.mkdir();
        File[] f1 = f.listFiles();
        for (File aF1 : f1) offlineVBFiles.add(aF1.getName());
        f = new File(INTERNAL_DIR, "clips");
        if (!f.exists()) f.mkdir();
        if ((new File(INTERNAL_DIR, "lyrics_139")).exists()) lyricsReady = true;
        if (!audio.isEmpty()) return;
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/raw/" + "k");
        try {
            maharajas.add(new Album());
            ContentResolver contentResolver = getContentResolver();
            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(contentResolver.openInputStream(uri))));
            String line;
            while ((line = reader.readLine()) != null)
                maharajas.add(new Album(line.replaceAll("~", "~ ").split("~")));
            reader.close();

            uri = Uri.parse("android.resource://" + getPackageName() + "/raw/" + "u");
            reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(contentResolver.openInputStream(uri))));
            while ((line = reader.readLine()) != null)
                fileBaseUrls.add(line.split("~"));
            reader.close();

            uri = Uri.parse("android.resource://" + getPackageName() + "/raw/" + "p");
            reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(contentResolver.openInputStream(uri))));
            places = reader.readLine().split("~");
            for (int i = 0; i < places.length; i++) places[i] = decodeStr(places[i].trim());
            reader.close();

            uri = Uri.parse("android.resource://" + getPackageName() + "/raw/" + "_");
            reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(contentResolver.openInputStream(uri))));
            if ((line = reader.readLine()) != null) c = line.split("~");
            else c = new String[]{};
            for (int i = 0; i < c.length; i++) c[i] = decodeStr(c[i].trim());
            reader.close();

            uri = Uri.parse("android.resource://" + getPackageName() + "/raw/" + "c");
            reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(contentResolver.openInputStream(uri))));
            while ((line = reader.readLine()) != null)
                authors.add(new Author(line.replaceAll("~", "~ ").split("~")));
            reader.close();

            uri = Uri.parse("android.resource://" + getPackageName() + "/raw/" + "l");
            reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(contentResolver.openInputStream(uri))));
            while ((line = reader.readLine()) != null)
                lyrics.add(new Lyrics(line.replaceAll("~", "~ ").split("~")));
            reader.close();

            uri = Uri.parse("android.resource://" + getPackageName() + "/raw/" + "a");
            reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(contentResolver.openInputStream(uri))));
            while ((line = reader.readLine()) != null)
                audio.add(new Audio(line.replaceAll("~", "~ ").split("~")));
            reader.close();

            uri = Uri.parse("android.resource://" + getPackageName() + "/raw/" + "r");
            reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(contentResolver.openInputStream(uri))));
            String[] txt = reader.readLine().replaceAll("~", "~ ").split("~");
            for (int i = 0; i < txt.length; i++)
                audio.get(i).ref = txt[i].trim();
            for (int i = 0; i < maharajas.size(); i++) {
                if (maharajas.get(i).subAlbumIndices.size() > 0) {
                    maharajas.get(i).lang = maharajas.get(maharajas.get(i).subAlbumIndices.get(0)).lang;
                    maharajas.get(i).arteInd = maharajas.get(maharajas.get(i).subAlbumIndices.get(0)).arteInd;
                }
            }
        } catch (final IOException e) {
            /*runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(k.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });*/
            e.printStackTrace();
        }
        readPlaylistFromFile();
        h.nowPlaying = h.nowPlaying == null ? audio.get(0) : h.nowPlaying;

        for (int i = favourites.size(); i < audio.size(); i++)
            favourites.add(false);
        for (int i = volume.size(); i < audio.size() + 1; i++)
            volume.add(100);
        spChantVol = volume.get(audio.size());
        File file = new File(INTERNAL_DIR, "lyrics_" + (lyrics.size() - 1));
        if (!file.exists()) {
            (new Thread(() -> {
                mWakeLock.acquire();
                for (int i = 0; i < lyrics.size(); i++) fetchLyrics(i, false);
                if (mWakeLock.isHeld()) mWakeLock.release();
            })).start();
        }
//        Update lyrics
        Thread t = new Thread(() -> {
            try {
                URLConnection urlConnection = new URL(audioShareBaseUrl + "last_updates.txt").openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line = reader.readLine();
                String[] list = line.split(",");
                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-mm-yyyy");
                if (format.parse(list[1]).getTime() / 3600000 / 24 <= lastLyricsUpdate) return;
                reader.readLine();//skipping titles
                while ((line = reader.readLine()) != null) {
                    list = line.split(",");
                    int lyricsInd = Integer.parseInt(list[0].trim());
                    fetchLyrics(lyricsInd, true);
                }
                reader.close();
            } catch (Exception ignored) {
            }
        });
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
//        retrieveDeviceSongs(); */
    }

    long baseDate;
    static String[] places;
    static String[] c;
    final public static String ss = "https://audio.iskcondesiretree.com/01_-_Srila_Prabhupada/0",
            songsBaseUrl = ss + "2_-_Bhajans/Vol-0",
            enBaseUrl = ss + "1_-_Lectures/01_-_English/01_-_Topic_wise/",
            hiBaseUrl = ss + "1_-_Lectures/03_-_Hindi/SP_Hindi_",
            bengaliBaseUrl = ss + "1_-_Lectures/02_-_Bengali/",
            baseTamil = ss + "1_-_Lectures/04_-_Translation/Tamil/BG_0",
            baseTelugu = ss + "1_-_Lectures/04_-_Translation/Telugu/SP_Telugu_Translation_SB_0",
            baseChinese = "1_-_Lectures/04_-_Translation/Chinese/SP_Chinese_Translation_",
            baseJapanese = "1_-_Lectures/04_-_Translation/Japanese/",
            basePort = "1_-_Lectures/04_-_Translation/Portuguese/";

    final public static String[] baseUrls = {songsBaseUrl, enBaseUrl, hiBaseUrl, bengaliBaseUrl, baseTamil, baseTelugu, baseChinese, baseJapanese, basePort},
            Lang = {"", "Eng", "Hindi", "Bangoli", "Tamil", "Telugu", "Chinese", "Japanese", "Portuguese"},
            filterOptions = {"Bhajans", "English", "Hindi", "Bangoli", "Tamil Translation",
                    "Telugu Translation", "Chinese Translation", "Japanese Translation", "Portuguese Translation", "Offline Only"};
    int[] flg = new int[filterOptions.length];

    public class Audio {
        long id, date = 0;
        int index, albumId, arteInd, placeInd;
        String title, url, ref, place, Size;

        Audio(String[] str) {
            index = audio.size();
            id = decode(str[0].trim());
            albumId = (int) decode(str[1].trim()) + 1;
            arteInd = (int) decode(str[2].trim());
            if (maharajas.get(albumId).audioIndices.isEmpty()) {
                maharajas.get(albumId).arteInd = arteInd;
                maharajas.get(albumId).lang = (int) (id / 100000);
            }
            maharajas.get(albumId).audioIndices.add(index);
            maxArte = Math.max(maxArte, arteInd);
            if (!str[3].trim().isEmpty())
                date = decode(str[3].trim());
            placeInd = (int) decode(str[4].trim());
            title = decodeStr(str[5].trim());
            Size = decodeStr(str[6].trim());
            url = decodeStr(str[7].trim());
        }

        public Audio(Audio a) {
            this.id = a.id;
            this.index = a.index;
            this.albumId = a.albumId;
            this.arteInd = a.arteInd;
            this.title = a.title;
            this.placeInd = a.placeInd;
            this.url = a.url;
            this.date = a.date;
            this.ref = a.ref;
            this.place = a.place;
            this.Size = a.Size;
        }

        int getLyricsInd() {
            int lyricsInd = -1;
            try {
                lyricsInd = Integer.parseInt(ref.trim());
            } catch (Exception ignored) {
            }
            return lyricsInd;
        }

        String getUrl() {
            String urlStr = baseUrls[(int) id / 100000];
            String[] ss = fileBaseUrls.get((int) id / 100000);
            if (ss.length > 0)
                urlStr = urlStr + decodeStr(ss[(int) (id / 1000) % 100]) + url;
            else
                urlStr = urlStr + url;
            for (int i = 0; i < c.length; i++)
                urlStr = urlStr.replaceAll("#" + (char) ('a' + i), c[i]);
            return urlStr;
        }

        ArrayList<ArrayList<String>> getTextUrls() {
            String[] list = ref.split(";");
            if (list.length <= 0 || list[0].isEmpty() || getLyricsInd() >= 0) return null;
            ArrayList<ArrayList<String>> textUrl = getTextUrls(list[0]);
            for (int i = 1; i < list.length; i++) {
                ArrayList<ArrayList<String>> temp = getTextUrls(list[i]);
                textUrl.get(0).addAll(temp.get(0));
                textUrl.get(1).addAll(temp.get(1));
                textUrl.get(2).set(0, textUrl.get(2).get(0) + "; " + temp.get(2).get(0));
            }
            return textUrl;
        }

        ArrayList<ArrayList<String>> getTextUrls(String text) {
            ArrayList<ArrayList<String>> textUrl = new ArrayList<>();
            ArrayList<String> textLinkUrl = new ArrayList<>();
            ArrayList<String> textString = new ArrayList<>();
            ArrayList<String> temp = new ArrayList<>(Arrays.asList(text.split("\\.")));
            String book = temp.get(0);
            boolean multipleText = false;
            if (book.startsWith("sb") && temp.size() >= 2) {
                int canto = Integer.parseInt(temp.get(1));
                int chapter = Integer.parseInt(temp.get(2));
                if (temp.size() == 5) {
                    multipleText = true;
                    int startText = Integer.parseInt(temp.get(3).trim()), endText = Integer.parseInt(temp.get(4).trim());
                    for (int i = startText; i <= endText; i++) {
                        textLinkUrl.add(book + "/" + canto + "/" + chapter + "/" + i);
                        textString.add("SB Canto " + canto + " Chapter " + chapter + " Text " + i);
                    }
                } else if (temp.size() == 4) {
                    int txt = Integer.parseInt(temp.get(3));
                    textLinkUrl.add(book + "/" + canto + "/" + chapter + "/" + txt);
                    textString.add("SB Canto " + canto + " Chapter " + chapter + " Text " + txt);
                } else if (temp.size() == 3) {
                    textString.add("SB Canto " + canto + " Chapter " + chapter);
                    textLinkUrl.add(book + "/" + canto + "/" + chapter);
                } else if (temp.size() == 2) {
                    textLinkUrl.add(book + "/" + canto);
                    textString.add("SB Canto " + canto);
                }
//            ========================================if not SB
            } else if (!book.equals("sb") && temp.size() >= 2) {
                int chapter = Integer.parseInt(temp.get(1));
                if (temp.size() == 4) {
                    multipleText = true;
                    int startText = Integer.parseInt(temp.get(2)), endText = Integer.parseInt(temp.get(3));
                    for (int i = startText; i <= endText; i++) {
                        textLinkUrl.add(book.replace('_', '/') + "/" + chapter + "/" + i);
                        textString.add(book.replace('_', ' ').toUpperCase() + " Chapter " + chapter + " Text " + i);
                    }
                } else if (temp.size() == 3) {
                    int txt = Integer.parseInt(temp.get(2));
                    textLinkUrl.add(book.replace('_', '/') + "/" + chapter + "/" + txt);
                    textString.add(book.replace('_', ' ').toUpperCase() + " Chapter " + chapter + " Text " + txt);
                } else if (temp.size() == 2) {
                    textLinkUrl.add(book.replace('_', '/') + "/" + chapter);
                    textString.add(book.replace('_', ' ').toUpperCase() + " Chapter " + chapter);
                }
            } else {
                textLinkUrl.add(book.replace('_', '/').toLowerCase());
                textString.add(book.replace('_', ' ').toUpperCase());
            }
            textUrl.add(textLinkUrl);
            textUrl.add(textString);
            ArrayList<String> label = new ArrayList<>();
            if (multipleText) {
                label.add(book.replace('_', ' ').toUpperCase() + " " + text.substring(text.indexOf('.') + 1, text.lastIndexOf(".")) + " to " + text.substring(text.lastIndexOf(".") + 1));
            } else {
                label.add(book.replace('_', ' ').toUpperCase() + " " + (text.indexOf('.') > -1 ? text.substring(text.indexOf('.') + 1) : ""));
            }
            textUrl.add(label);
            return textUrl;
        }
    }

    public class Album {
        String name = "";
        ArrayList<Integer> audioIndices = new ArrayList<>(), subAlbumIndices = new ArrayList<>();
        int parentAlbumIndex = -1, arteInd = -1, count = -1, lang;

        Album() {
        }

        Album(String[] str) {
            parentAlbumIndex = (int) decode(str[0].trim()) + 1;
            name = decodeStr(str[1].trim());
            maharajas.get(parentAlbumIndex).subAlbumIndices.add(maharajas.size());
        }

        ArrayList<Integer> getAudioIndices() {
            ArrayList<Integer> a = new ArrayList<>(audioIndices);
            for (Integer id : subAlbumIndices)
                a.addAll(maharajas.get(id).getAudioIndices());
            return a;
        }

        int getCount() {
            if (count < 0) return count = getAudioIndices().size();
            return count;
        }
    }

    public static final int audioItem = 0, maharajaItem = 1, plItem = 2, authorItem = 3, lyricsItem = 4;
    public static final int title = 0, artist = 1, author = 2, date = 3, place = 4;
    boolean[] searchIn = {true, true, false, false};
    final String[] searchInOpt = {"Default", "Current List Only", "All Songs", "All Lyrics"};
    boolean[] searchFor = {true, true, true, true, true};
    final String[] searchForOpt = {"Title", "Singer", "Author", "Date", "Place"};

    public void setDefaultSearchIn() {
        if (mainTL.getAlpha() != 1 && (holderTL.tContext == songbookTabs || holderTL.tContext == authorTabs)) {
            searchIn[0] = true;
            searchIn[1] = false;
            searchIn[2] = false;
            searchIn[3] = true;
        } else {
            searchIn[0] = true;
            searchIn[1] = true;
            searchIn[2] = false;
            searchIn[3] = false;
        }
    }

    ArrayList<SearchList> sList = new ArrayList<>(), searchResults = new ArrayList<>();
    int maxSearchResults = 50;

    public class SearchList {
        String val;
        int type, field, ind;

        SearchList(String val, int type, int field, int ind) {
            this.val = val;
            this.type = type;
            this.field = field;
            this.ind = ind;
        }
    }

    public void setSearchList() {
        sList.clear();
        if (searchIn[1]) {
            if (mainTL.getAlpha() == 1) {
                switch (mainTL.tabInd) {
                    case 0:
                        for (int i = 0; i < mAdapter.indices.size(); i++)
                            sList.add(new SearchList(maharajas.get(mAdapter.indices.get(i)).name, maharajaItem, title, i));
                        break;
                    case 1:
                        for (int i = 0; i < playlist.size(); i++)
                            sList.add(new SearchList(playlist.get(i), plItem, title, i));
                        break;
                    case 2:
                        for (Audio a : audio) {
                            String[] str = {a.title, maharajas.get(a.albumId).name, a.getLyricsInd() != -1 ? authors.get(lyrics.get(a.getLyricsInd()).authorInd).name : "",
                                    a.date == 0 ? "" : simpleDateFormat.format(a.date * 3600000 * 24 + baseDate), places[a.placeInd]};
                            for (int i = 0; i < str.length; i++)
                                if (searchFor[i] && !str[i].isEmpty())
                                    sList.add(new SearchList(str[i], audioItem, i, a.index));
                        }
                        break;
                }
            } else if (holderTL.tContext == holderTabs) {
                for (int ind : mAudioAdapter.indices) {
                    Audio a = audio.get(ind);
                    String[] str = {a.title, maharajas.get(a.albumId).name, a.getLyricsInd() != -1 ? authors.get(lyrics.get(a.getLyricsInd()).authorInd).name : "",
                            a.date == 0 ? "" : simpleDateFormat.format(a.date * 3600000 * 24 + baseDate), places[a.placeInd]};
                    for (int i = 0; i < str.length; i++)
                        if (searchFor[i] && !str[i].isEmpty())
                            sList.add(new SearchList(str[i], audioItem, i, a.index));
                }
            } else if (holderTL.tContext == plTabs) {
                ArrayList<Integer> indices = holderTL.tabInd == 0 ? favAdapter.indices : holderTL.tabInd == 1 ? offlineAdapter.indices : playlistAudios.get(holderTL.tabInd - 2);
                for (int ind : indices) {
                    Audio a = audio.get(ind);
                    String[] str = {a.title, maharajas.get(a.albumId).name, a.getLyricsInd() != -1 ? authors.get(lyrics.get(a.getLyricsInd()).authorInd).name : "",
                            a.date == 0 ? "" : simpleDateFormat.format(a.date * 3600000 * 24 + baseDate), places[a.placeInd]};
                    for (int i = 0; i < str.length; i++)
                        if (searchFor[i] && !str[i].isEmpty())
                            sList.add(new SearchList(str[i], audioItem, i, a.index));
                }
            } else if (holderTL.tContext == songbookTabs) {
                for (int i = 0; i < authors.size(); i++)
                    sList.add(new SearchList(authors.get(i).name, authorItem, title, i));
            } else if (holderTL.tContext == authorTabs) {
                for (int ind : authors.get(holderTL.tabInd).lyricsIndices)
                    sList.add(new SearchList(lyrics.get(ind).name, lyricsItem, title, ind));
            }
        } else {
            if (searchIn[2]) {
                for (Audio a : audio) {
                    String[] str = {a.title, maharajas.get(a.albumId).name, a.getLyricsInd() != -1 ? authors.get(lyrics.get(a.getLyricsInd()).authorInd).name : "",
                            a.date == 0 ? "" : simpleDateFormat.format(a.date * 3600000 * 24 + baseDate), places[a.placeInd]};
                    for (int i = 0; i < str.length; i++)
                        if (searchFor[i] && !str[i].isEmpty())
                            sList.add(new SearchList(str[i], audioItem, i, a.index));
                }
                for (int i = 0; i < maharajas.size(); i++)
                    sList.add(new SearchList(maharajas.get(i).name, maharajaItem, title, i));
                for (int i = 0; i < playlist.size(); i++)
                    sList.add(new SearchList(playlist.get(i), plItem, title, i));
            }
            if (searchIn[3]) {
                for (int i = 0; i < lyrics.size(); i++) {
                    sList.add(new SearchList(lyrics.get(i).name, lyricsItem, title, i));
                    sList.add(new SearchList(authors.get(lyrics.get(i).authorInd).name, lyricsItem, author, i));
                }
                for (int i = 0; i < authors.size(); i++)
                    sList.add(new SearchList(authors.get(i).name, authorItem, title, i));
            }
        }
        Collections.sort(sList, (searchList, t1) -> searchList.val.compareToIgnoreCase(t1.val));
        searchResults.clear();
        searchResults.addAll(sList);
    }

    long lastLyricsUpdate = 0;

    public class Lyrics {
        String name;
        long lastUpdate = 0;
        int authorInd;
        ArrayList<Integer> audioIndices = new ArrayList<>();

        Lyrics(String[] str) {
            name = decodeStr(str[0].trim());
            authorInd = (int) decode(str[1].trim());
            authors.get(authorInd).lyricsIndices.add(lyrics.size());
        }
    }

    public class Author {
        String name, link;
        ArrayList<Integer> lyricsIndices = new ArrayList<>();

        Author(String[] str) {
            name = decodeStr(str[0].trim());
            link = decodeStr(str[1].trim());
        }
    }

    public static Preferences prefs = new Preferences();

    static class Preferences {
        int dailyNectarNotification = 1, dailyNectarTime = 360, SPQuoteNotification = 1, onShake = 0,
                showQuickBall = 1, spQuoteTime = 360, becomingNoisyAction = 1, no_of_days = 5,
                a = 195, r = 150, g = 150, b = 160, filterFlags = 31, fixedBackground = 1, offlineVedabase = 1; //---
        String dailyNectarRingTone = "", spQuoteRingTone = "", appSharingSign = "Hare Krishna\nFound wonderful Vaishnava Songs App.\n" +
                "@appUrl" + "\nPlease install this light weight app and share with your friends as well." + "\nThank you\n\nYours",
                songSharingSign = "Hare Krishna\nI am listening to @songUrl.", bgUri = "", bsrmNotUri = "", spNotUri = "";
        float backgroundVolume = 0;
    }

    public void writePreferencesToFile() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(INTERNAL_DIR.getPath() + "/toBackup/" + "prefs"));
            String prefStr = prefs.dailyNectarNotification + "," + prefs.dailyNectarTime + "," + prefs.SPQuoteNotification + "," + prefs.spQuoteTime + "," +
                    prefs.dailyNectarRingTone + "," + prefs.spQuoteRingTone + "," + prefs.appSharingSign + "," + prefs.songSharingSign + "," +
                    prefs.becomingNoisyAction + "," + mServ.Repeat + "," + (mServ.Shuffle ? 1 : 0) + "," + prefs.a + "," + prefs.r + "," + prefs.b + "," +
                    prefs.g + "," + prefs.bgUri + "," + prefs.bsrmNotUri + "," + prefs.spNotUri + "," + prefs.onShake + "," + prefs.filterFlags + "," +
                    prefs.showQuickBall + "," + prefs.fixedBackground + "," + prefs.offlineVedabase;
            fileOutputStream.write((prefStr).getBytes());
            fileOutputStream.close();
            fileOutputStream = new FileOutputStream(new File(INTERNAL_DIR, "showHK"));
            fileOutputStream.write(("1").getBytes());
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }

    public void readPreferencesFromFile() {
        File file = new File(INTERNAL_DIR.getPath() + "/toBackup/" + "prefs");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            line = stringBuilder.toString();
            line.replaceAll(",", " , ");
            String[] list = line.split(",");
            for (int i = 0; i < list.length; i++) list[i] = list[i].trim();
            prefs.dailyNectarNotification = Integer.parseInt(list[0]);
            prefs.dailyNectarTime = Integer.parseInt(list[1]);
            prefs.SPQuoteNotification = Integer.parseInt(list[2]);
            prefs.spQuoteTime = Integer.parseInt(list[3]);
            prefs.dailyNectarRingTone = list[4];
            prefs.spQuoteRingTone = list[5];
            prefs.appSharingSign = list[6];
            prefs.songSharingSign = list[7];
            prefs.becomingNoisyAction = Integer.parseInt(list[8]);
            mServ.Repeat = Integer.parseInt(list[9]);
            mServ.Shuffle = Integer.parseInt(list[10]) > 0;
            if (list.length > 18) {
                prefs.a = Integer.parseInt(list[11]);
                prefs.r = Integer.parseInt(list[12]);
                prefs.b = Integer.parseInt(list[13]);
                prefs.g = Integer.parseInt(list[14]);
                prefs.bgUri = list[15];
            }
            if (list.length > 17) {
                prefs.bsrmNotUri = list[16];
                prefs.spNotUri = list[17];
            }
            if (list.length > 18)
                prefs.onShake = Integer.parseInt(list[18]);
            if (list.length > 20) {
                prefs.filterFlags = Integer.parseInt(list[19]);
                prefs.showQuickBall = Integer.parseInt(list[20]);
            }
            if (list.length > 21)
                prefs.fixedBackground = Integer.parseInt(list[21]);
            if (list.length > 22)
                prefs.offlineVedabase = Integer.parseInt(list[22]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPrefView() {
        if (fullscreen.getVisibility() == VISIBLE) onBackPressed();
        ((EditText) Preferences.findViewById(R.id.appShareTxt)).setText(prefs.appSharingSign);
        ((EditText) Preferences.findViewById(R.id.songShareTxt)).setText(prefs.songSharingSign);
        ((Button) Preferences.findViewById(R.id.becomingNoisyAction)).setText(h.becomingNoisy[prefs.becomingNoisyAction]);
        ((Button) Preferences.findViewById(R.id.shakeAction)).setText(h.onShake[prefs.onShake]);
        ((Button) findViewById(R.id.bsrm_not_sound)).setText(prefs.bsrmNotUri.substring(prefs.bsrmNotUri.lastIndexOf('/') + 1));
        ((Button) findViewById(R.id.sp_not_sound)).setText(prefs.spNotUri.substring(prefs.spNotUri.lastIndexOf('/') + 1));
        ((CheckBox) findViewById(R.id.showFab)).setChecked(prefs.showQuickBall == 1);
//        ((CheckBox) findViewById(R.id.showHK)).setChecked(prefs.showHk == 1);
        ((CheckBox) findViewById(R.id.fixedBackground)).setChecked(prefs.fixedBackground == 1);
        ((CheckBox) findViewById(R.id.offlineVedabase)).setChecked(prefs.offlineVedabase == 1);
        setNotificationTime();
        drawer.disableDrawer();
        storageList.setAdapter(storageAdapter = new Adapter(adptrs.storage, -1));
        ViewGroup.LayoutParams params = storageList.getLayoutParams();
        params.height = (int) (storageAdapter.count * 6.9 * dp10);
        storageList.setLayoutParams(params);
        Preferences.setVisibility(VISIBLE);
    }

    public void setBackground() {
        if (prefs.bgUri.isEmpty()) {
            int a1 = prefs.a, r1 = prefs.r, g1 = prefs.g, b1 = prefs.b;
            a.setMax(255);
            a.setProgress(a1);
            r.setMax(255);
            r.setProgress(r1);
            g.setMax(255);
            g.setProgress(g1);
            b.setMax(255);
            b.setProgress(b1);
        } else if (prefs.bgUri.equals("sp")) {
            ((ImageView) findViewById(R.id.prevBackground)).setImageResource(R.drawable.sp);
            ((ImageView) findViewById(R.id.bg_arte)).setImageResource(R.drawable.sp);
        } else if (prefs.bgUri.equals("haribol")) {
            ((ImageView) findViewById(R.id.prevBackground)).setImageResource(R.drawable.haribol);
            ((ImageView) findViewById(R.id.bg_arte)).setImageResource(R.drawable.haribol);
        } else {
            Uri selectedImage = Uri.parse(prefs.bgUri);
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            try {
                InputStream is = getContentResolver().openInputStream(selectedImage);
                BitmapFactory.decodeStream(is, null, options);
                if (is != null) is.close();
                options.inSampleSize = calculateInSampleSize(options, size.x, size.y);
                int prevViewSize = calculateInSampleSize(options, 10 * dp10, 10 * dp10);
                options.inJustDecodeBounds = false;
                is = getContentResolver().openInputStream(selectedImage);
                ((ImageView) findViewById(R.id.bg_arte)).setImageBitmap(BitmapFactory.decodeStream(is, null, options));
                if (is != null) is.close();
                options.inSampleSize = prevViewSize;
                is = getContentResolver().openInputStream(selectedImage);
                ((ImageView) findViewById(R.id.prevBackground)).setImageBitmap(BitmapFactory.decodeStream(is, null, options));
                if (is != null) is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public void setPrefView(int id) {
        int[] ids = {R.id.pref_gen_btns, R.id.pref_notification_btns, R.id.bsrm_time_picker, R.id.sp_time_picker, R.id.storage};
        for (int id1 : ids) {
            if (id1 == id) continue;
            findViewById(id1).setVisibility(View.GONE);
        }
        if (findViewById(id).getVisibility() == View.GONE)
            findViewById(id).setVisibility(VISIBLE);
        else
            findViewById(id).setVisibility(View.GONE);
    }

    public void setNotificationTime() {
        setNotificationTime(prefs.dailyNectarTime, (Button) findViewById(R.id.bsrm_time));
        setNotificationTime(prefs.spQuoteTime, (Button) findViewById(R.id.sp_time));
    }

    public void setNotificationTime(int time, Button btn) {
        int hours = time / 60, minutes = time % 60;
        String am = hours >= 12 ? " PM" : " AM";
        hours = hours % 12;
        String timeString = (hours > 9 ? "" + hours : "0" + hours) + ":" + (minutes > 9 ? "" + minutes : "0" + minutes) + am;
        btn.setText(timeString);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btn.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.alarm), null);
        }
    }

    public void setNotificationIcon(Activity context, int state, int resId, boolean showToast) {
        switch (state) {
            case 0:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ((Button) Preferences.findViewById(resId)).
                            setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.not_off), null);
                }
                if (showToast)
                    Toast.makeText(context, "Notification Off", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ((Button) Preferences.findViewById(resId)).
                            setCompoundDrawablesWithIntrinsicBounds(null, null, context.getDrawable(R.drawable.not), null);
                }
                if (showToast)
                    Toast.makeText(context, "Notification Shown - Notification Tone Silent", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ((Button) Preferences.findViewById(resId)).
                            setCompoundDrawablesWithIntrinsicBounds(null, null, context.getDrawable(R.drawable.not_active), null);
                }
                if (showToast)
                    Toast.makeText(context, "Notification with Alert Tone Active", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void createChantWithPrabhupadaNotification() {
        Context context = k.this;
        Intent notIntent = new Intent(context, k.class);
        notIntent.setAction("chant_with_prabhupada");
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(context, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, 0,
                new Intent("com.mayank.vaishnavasongs.pause_chant_with_sp1").setClass(k.this, receiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent playPausePendingIntent = PendingIntent.getBroadcast(context, 0,
                new Intent("com.mayank.vaishnavasongs.play_pause_chant_with_sp1").setClass(k.this, receiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent volUpPendingIntent = PendingIntent.getBroadcast(context, 0,
                new Intent("com.mayank.vaishnavasongs.sp_chant_vol_up1").setClass(k.this, receiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent volDownPendingIntent = PendingIntent.getBroadcast(context, 0,
                new Intent("com.mayank.vaishnavasongs.sp_chant_vol_down1").setClass(k.this, receiver.class), PendingIntent.FLAG_UPDATE_CURRENT);

        int plBtn = musicPlayer1 != null && musicPlayer1.isPlaying() ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play;
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.sp)
                .setContentTitle("Chant with Srila Prabhupada")
                .setContentText("Volume: " + spChantVol + " % of system volume")
                .addAction(R.drawable.ic_volume_down_black_24dp, "Decrease Volume", volDownPendingIntent)
                .addAction(R.drawable.ic_volume_up_black_24dp, "Increase Volume", volUpPendingIntent)
                .addAction(plBtn, "Pause", playPausePendingIntent)
                .addAction(R.drawable.stop, "Stop", stopPendingIntent)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.sp));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(Notification.VISIBILITY_PUBLIC)
                    // Apply the media style template
                    .setStyle(new Notification.MediaStyle()
                            .setShowActionsInCompactView(2))
                    .setColor(Color.rgb(216, 27, 96));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("spchant", "SP Chant Controls", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Music Controls for Chant with Srila Prabhupada");
            channel.enableLights(false);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId("spchant");
        }
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(101, notification);
    }

    public boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return false;
        } else return true;
    }

    static String decodeStr(String str) {
        boolean[] key = {true, false, false, false, true, false, true, true, false, false, true};
        int keyLength = key.length, strLen = str.length();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < strLen; i++) {
            if (key[i % keyLength])
                stringBuilder.append((char) (str.charAt(i) + 1));
            else
                stringBuilder.append((char) (str.charAt(i) - 1));
        }
        return stringBuilder.toString().replaceAll("`", ",");
    }

    long decode(String str) {
        if (str.trim().isEmpty()) return -1;
        int s = '!', e = '~' - 1;
        long k = 0, base = e - s;
        for (int i = 0; i < str.length(); i++) {
            k += Math.pow(base, i) * (str.charAt(i) - s);
        }
        return k;
    }

    static int baseChar = 'a';
    static int base = 'z' - baseChar;

    static String getUrlToClip(int s, int e) {
        StringBuilder str = new StringBuilder();
        int id = (int) h.nowPlaying.id, ind = h.nowPlaying.index;
        while (ind >= base) {
            str.append((char) (baseChar + ind % base));
            ind = ind / base;
        }
        str.append((char) (baseChar + ind % base));
        str.append('/');
        ind = id;
        while (ind >= base) {
            str.append((char) (baseChar + ind % base));
            ind = ind / base;
        }
        str.append((char) (baseChar + ind % base));
        str.append('/');
        ind = s;
        while (ind >= base) {
            str.append((char) (baseChar + ind % base));
            ind = ind / base;
        }
        str.append((char) (baseChar + ind % base));
        str.append('/');
        ind = e;
        while (ind >= base) {
            str.append((char) (baseChar + ind % base));
            ind = ind / base;
        }
        str.append((char) (baseChar + ind % base));
        return str.toString();
    }

    static String getUrlToShare(int ind) {
        StringBuilder str = new StringBuilder();
        int id = (int) audio.get(ind).id;
        while (ind >= base) {
            str.append((char) (baseChar + ind % base));
            ind = ind / base;
        }
        str.append((char) (baseChar + ind % base));
        str.append('/');
        ind = id;
        while (ind >= base) {
            str.append((char) (baseChar + ind % base));
            ind = ind / base;
        }
        str.append((char) (baseChar + ind % base));
        str.append('/');
        if (h.nowPlaying != null && h.nowPlaying.id == id && musicPlayer != null && musicPlayer.isPrepared) {
            ind = musicPlayer.getCurrentPosition() / 1000;
            while (ind >= base) {
                str.append((char) (baseChar + ind % base));
                ind = ind / base;
            }
            str.append((char) (baseChar + ind % base));
        } else
            str.append("?");
        return str.toString();
    }

    int[] getIndexFromUrl(String url) {
        int index = 0, id = 0, duration = 0;
        url = url.replace(audioShareBaseUrl, "");
        String[] list = url.split("/");
        String str = list[0].trim();
        for (int i = 0; i < str.length(); i++) {
            index += Math.pow(base, i) * (str.charAt(i) - baseChar);
        }
        str = list[1].trim();
        for (int i = 0; i < str.length(); i++) {
            id += Math.pow(base, i) * (str.charAt(i) - baseChar);
        }
        if (list.length > 2) {
            str = list[2].trim();
            if (!str.equals("?")) {
                for (int i = 0; i < str.length(); i++) {
                    duration += Math.pow(base, i) * (str.charAt(i) - baseChar);
                }
            }
        }
        if (audio.get(index).id != id) {
            for (int i = 0; i < audio.size(); i++) {
                if (audio.get(i).id == id) {
                    index = i;
                    break;
                }
            }
        }
        id = 0;
        if (list.length > 3) {
            str = list[3].trim();
            for (int i = 0; i < str.length(); i++) {
                id += Math.pow(base, i) * (str.charAt(i) - baseChar);
            }
        }
        return new int[]{index, duration, id};
    }

    private dragListView.DropListener mDropListener =
            new dragListView.DropListener() {
                public void drop(int from, int to) {
                    Adapter adapter = (Adapter) (fullscreen.getVisibility() == VISIBLE ? fsList.getAdapter() :
                            wifi.getVisibility() == VISIBLE ? wifiList.getAdapter() : holderList.getAdapter());
                    int direction = -1;
                    if (from < to) {
                        direction = 1;
                    }
                    int target = adapter.indices.get(from);
                    for (int i = from; i != to; i = i + direction) {
                        int item = adapter.indices.get(i + direction);
                        adapter.indices.set(i, item);
                    }
                    adapter.indices.set(to, target);
                    adapter.notifyDataSetChanged();
                }
            };

    public static void writePlaylistToFile() {
        try {
            File f = new File(INTERNAL_DIR, "toBackup");
            if (!f.exists())
                f.mkdir();
            String dir = INTERNAL_DIR.getPath() + "/toBackup/";
            File file = new File(dir + "playlists");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            StringBuilder playlistStr = new StringBuilder();
            for (int i = 0; i < playlist.size(); i++) {
                playlistStr.append(playlist.get(i)).append(",");
                FileOutputStream fOutput1 = new FileOutputStream(new File(dir + playlist.get(i)));
                StringBuilder playlistAudioStr = new StringBuilder();
                playlistAudioStr.append("bsrm,");
                for (int j = 0; j < playlistAudios.get(i).size(); j++)
                    playlistAudioStr.append(getUrlToShare(playlistAudios.get(i).get(j))).append(",");
                fOutput1.write(playlistAudioStr.toString().getBytes());
                fOutput1.close();
            }
            fileOutputStream.write(playlistStr.toString().getBytes());
            fileOutputStream.close();

            StringBuilder favStr = new StringBuilder(), volStr = new StringBuilder(), lyricsStr = new StringBuilder(), lyricsUpdateStr = new StringBuilder();
            int i = 0;
            for (; i < favourites.size(); i++) {
                favStr.append(favourites.get(i) ? "1," : "0,");
                volStr.append(volume.get(i)).append(",");
                lyricsStr.append(audio.get(i).ref).append(",");
            }
            volStr.append(volume.get(i));
            for (Lyrics l : lyrics) lyricsUpdateStr.append(l.lastUpdate).append(",");
            fileOutputStream = new FileOutputStream(new File(dir, "favourites"));
//            fileOutputStream.write(favStr.append("\n").append(volStr).append("\n").append(lyricsUpdateStr).append("\n").append(lyricsStr).toString().getBytes());
            fileOutputStream.write((favStr.toString() + "\n" + volStr.toString() + "\n" + lyricsUpdateStr.toString() + "\n" + lyricsStr.toString()).getBytes());
            fileOutputStream.close();
            fileOutputStream = new FileOutputStream(new File(INTERNAL_DIR, "nowPlaying"));
            fileOutputStream.write((h.nowPlaying == null ? "" : getUrlToShare(h.nowPlaying.index)).getBytes());
            fileOutputStream.close();
            fileOutputStream = new FileOutputStream(new File(INTERNAL_DIR, "downloadQStack"));
            favStr = new StringBuilder();
            removeDownloadedResourcesFromStack();
            for (i = 0; i < downloadQStack.size(); i++) {
                favStr.append(downloadQStack.get(i).id).append(",").append(downloadQStack.get(i).type).append('\n');
            }
            fileOutputStream.write(favStr.toString().getBytes());
            fileOutputStream.close();
            fileOutputStream = new FileOutputStream(new File(dir, "bookmarks"));
            favStr = new StringBuilder();
            for (i = 0; i < bookmarks.size(); i++) {
                favStr.append(bookmarks.get(i)).append('\n');
            }
            fileOutputStream.write(favStr.toString().getBytes());
            fileOutputStream.close();
            fileOutputStream = new FileOutputStream(new File(dir, "clips"));
            favStr = new StringBuilder();
            for (i = 0; i < audioClips.size(); i++) {
                favStr.append(audioClips.get(i)).append('\n');
            }
            fileOutputStream.write(favStr.toString().getBytes());
            fileOutputStream.close();
        } catch (IOException ignored) {
        }
    }

    ArrayList<Integer> audioIds = new ArrayList<>();

    public ArrayList<Integer> readPlaylistFromFile(InputStreamReader inputStreamReader) {
        audioIds.clear();
        try {
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            if (line == null)
                return new ArrayList<>();
            String[] list = line.split(",");
            if (!list[0].equals("bsrm"))
                return new ArrayList<>();
            for (int j = 1; j < list.length; j++)
                if (!list[j].trim().equals(""))
                    audioIds.add(getIndexFromUrl(list[j].trim())[0]);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return audioIds;
    }

    public void readPlaylistFromFile() {
        if (!playlist.isEmpty())
            return;
        readPlaylistFromFile(INTERNAL_DIR.getPath() + "/toBackup/");
        if (playlist.isEmpty()) readPlaylistFromFile(INTERNAL_DIR.getPath());
    }

    public void readPlaylistFromFile(String dir) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(dir + "playlists"))));
            String line = reader.readLine();
            if (line != null) {
                playlist = new ArrayList<>(Arrays.asList(line.split(",")));
                reader.close();
                for (int i = 0; i < playlist.size(); i++) {
                    playlistAudios.add(new ArrayList<>());
                    playlistAudios.get(i).addAll(readPlaylistFromFile(new InputStreamReader(new FileInputStream(new File(dir + playlist.get(i))))));
                }
            }
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(dir + "favourites")));
            line = reader.readLine();
            if (line == null) return;
            String[] list = line.split(",");
            for (String aList : list) {
                if (aList.equals("1"))
                    favourites.add(true);
                else
                    favourites.add(false);
            }
            line = reader.readLine();
            if (line != null && !line.trim().equals("")) {
                list = line.split(",");
                for (String aList : list) volume.add(Integer.parseInt(aList));
            }
            line = reader.readLine();
            if (line != null && !line.trim().equals("")) {
                list = line.split(",");
                for (int i = 0; i < list.length; i++) {
                    long lastUpdate = Long.parseLong(list[i]);
                    lyrics.get(i).lastUpdate = lastUpdate;
                    lastLyricsUpdate = Math.max(lastUpdate, lastLyricsUpdate);
                }
            }
            line = reader.readLine();
            if (line != null && !line.trim().equals("")) {
                list = line.split(",");
                for (int i = 0; i < list.length; i++) {
                    audio.get(i).ref = list[i];
                    if (audio.get(i).getLyricsInd() >= 0)
                        lyrics.get(audio.get(i).getLyricsInd()).audioIndices.add(i);
                }
            }
            reader.close();
            reader = new BufferedReader(new InputStreamReader(openFileInput("downloadQStack")));
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                list = line.split(",");
                downloadQStack.add(0, new DownloadQ(Integer.parseInt(list[0]), Integer.parseInt(list[1])));
            }
            reader.close();
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(dir + "bookmarks")));
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                bookmarks.add(line);
            }
            reader.close();
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(dir + "clips")));
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                audioClips.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addToPlayList(View view, final int audioIndex, final int listInd, final boolean isPlaylist) {
        PopupMenu popupMenu1 = new PopupMenu(k.this, view);
        popupMenu1.getMenu().add(0, 0, 0, "Create New Playlist");
        popupMenu1.getMenu().add(0, 1, 1, "Favourites");
        for (int i = 0; i < playlist.size(); i++) {
            popupMenu1.getMenu().add(0, i + 2, i + 2, playlist.get(i));
        }
        popupMenu1.setOnMenuItemClickListener(menuItem1 -> {
            String title = menuItem1.getTitle().toString();
            newPlaylistName = title;
            switch (title) {
                case "Create New Playlist":
                    createNewPlaylist(listInd, isPlaylist, audioIndex);
                    break;
                case "Favourites":
                    if (audioIndex >= 0) favourites.set(audioIndex, true);
                    else addToPlayList(listInd, isPlaylist, -2);
                    favAdapter = new Adapter(adptrs.fav, -1);
                    break;
                default:
                    addToPlayList(listInd, isPlaylist, audioIndex);
            }
            plAdapter = new Adapter(adptrs.playlist, -1);
            if (mainTL.tabInd == 1)
                list.setAdapter(plAdapter);
            return false;
        });
        popupMenu1.show();
    }

    public void addToPlayList(int listInd, boolean isPlaylist, int audioIndex) {
        if (listInd == -2) {
            ArrayList<Integer> indices;
            indices = audioIds;
            int ind = playlist.indexOf(newPlaylistName);
            for (int j = 0; j < indices.size(); j++) {
                int currentAudioIndex = indices.get(j);
                if (!playlistAudios.get(ind).contains(currentAudioIndex))
                    playlistAudios.get(ind).add(currentAudioIndex);
            }
        } else if (listInd != -1) {
            ArrayList<Integer> indices;
            if (isPlaylist) {
                if (listInd == 0)
                    indices = favAdapter.indices;
                else if (listInd == 1)
                    indices = offlineAdapter.indices;
                else
                    indices = playlistAudios.get(plAdapter.indices.get(listInd - 2));
            } else
                indices = maharajas.get(listInd).getAudioIndices();
            int ind = playlist.indexOf(newPlaylistName);
            if (audioIndex == -2) {
                for (int j = 0; j < indices.size(); j++)
                    favourites.set(indices.get(j), true);
            } else if (audioIndex == -3) {
                for (int j = 0; j < indices.size(); j++)
                    favourites.set(indices.get(j), false);
            } else {
                for (int j = 0; j < indices.size(); j++) {
                    int currentAudioIndex = indices.get(j);
                    if (!playlistAudios.get(ind).contains(currentAudioIndex))
                        playlistAudios.get(ind).add(currentAudioIndex);
                }
            }
        } else {
            if (playlistAudios.get(playlist.indexOf(newPlaylistName)).contains(audioIndex))
                Toast.makeText(k.this, "Already added to this playlist", Toast.LENGTH_SHORT).show();
            else
                playlistAudios.get(playlist.indexOf(newPlaylistName)).add(audioIndex);
        }
    }

    public void hideKeyboard() {
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(k.this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void setNotificationTone(int requestCode) {
        if (checkPermissions()) {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION | RingtoneManager.TYPE_ALARM)
                    .putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone")
                    .putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                            Uri.parse(requestCode == RESULT_BSRM_NOTIFICATION_TONE ? prefs.bsrmNotUri : prefs.spNotUri))
                    .putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
                    .putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
            this.startActivityForResult(Intent.createChooser(intent, "Select Notification Tone"), requestCode);
        }
    }

    Runnable hideWarning = new Runnable() {
        @Override
        public void run() {
//            bottomDrawer.updateResH = 0;
            if (fullscreen.getVisibility() != VISIBLE)
                bottomDrawer.collapse();
            updateRes.setVisibility(GONE);
        }
    };

    Runnable showWarning = new Runnable() {
        @Override
        public void run() {
            updateRes.setVisibility(VISIBLE);
//            bottomDrawer.updateResH = updateResH;
            if (fullscreen.getVisibility() != VISIBLE)
                bottomDrawer.collapse();
        }
    };
    public static final int M_ARTE = 0, Ly_ARTE = 1, ARTE = 2, AUDIO = 3, RES = 4;

    public static class DownloadQ {
        int id, type;

        DownloadQ(int id, int type) {
            this.id = id;
            this.type = type;
        }
    }

    public static Stack<DownloadQ> downloadQStack = new Stack<>();
    Runnable downloadManager = new Runnable() {
        @Override
        public void run() {
            downloadManagerRunning = true;
            runOnUiThread(showWarning);
            downloadFile(audioShareBaseUrl + "background_img", "background_img.jpg");
            downloadFile(audioShareBaseUrl + "bg1", "bg1.jpg");
            Stack<DownloadQ> temp = new Stack<>();
            while (!downloadQStack.isEmpty()) {
                DownloadQ downloadQElement = downloadQStack.peek();
                if (downloadQElement.type == -10) {
                    temp.push(downloadQElement);
                    downloadQStack.pop();
                    continue;
                }
                switch (downloadQElement.type) {
                    case AUDIO:
                        Audio a = audio.get(downloadQElement.id);
                        downloadFile(a.getUrl(), "bsrm_" + a.id + ".mp3");
                        break;
                    case ARTE:
                        downloadFile(audioShareBaseUrl + downloadQElement.id, "arte_" + downloadQElement.id);
                        break;
                    case M_ARTE:
                        String s = "arte_" + downloadQElement.id + "_comp";
                        downloadFile(audioShareBaseUrl + s, s);
                        break;
                    case Ly_ARTE:
                        s = "a" + downloadQElement.id + "_compressed1";
                        downloadFile(vSongBaseUrl + s, s);
                        break;
                    case RES:
                        downloadFile(resUrl[downloadQElement.id], resFile[downloadQElement.id]);
                }
                if (!downloadQStack.isEmpty()) downloadQStack.pop();
                runOnUiThread(updateDownloads);
            }
            while (!temp.isEmpty() && downloadingAudio) downloadQStack.push(temp.pop());
            runOnUiThread(updateDownloads);
            downloadManagerRunning = false;
            if (!downloadingAudio)
                runOnUiThread(hideWarning);
        }
    };
    Runnable updateDownloads = new Runnable() {
        @Override
        public void run() {
            if (downloadManagerAdapter != null) downloadManagerAdapter.notifyDataSetChanged();
            arteAdapter = new Adapter(adptrs.arts, -1);
            offlineAdapter = new Adapter(adptrs.offline, -1);
            if (holderTL.tContext == plTabs && holderTL.tabInd == 1)
                holderList.setAdapter(offlineAdapter);
        }
    };
    //    Query device for audio files
    private ArrayList<Audio> deviceSongList = new ArrayList<>();

    //public void retrieveDeviceSongs(){
////    https://code.tutsplus.com/tutorials/create-a-music-player-on-android-project-setup--mobile-22764?_ga=2.257181195.1642165316.1529651117-75864343.1521175706
////    https://code.tutsplus.com/tutorials/create-a-music-player-on-android-user-controls--mobile-22787
////    https://code.tutsplus.com/tutorials/create-a-music-player-on-android-song-playback--mobile-22778?_ga=2.3088176.1218340305.1539343288-75864343.1521175706
//    /*ContentResolver musicResolver = getContentResolver();
//    Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//    Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
//    if(musicCursor!=null && musicCursor.moveToFirst()){
//        //get columns
//        int titleColumn = musicCursor.getColumnIndex
//                (android.provider.MediaStore.Audio.Media.TITLE);
//        int idColumn = musicCursor.getColumnIndex
//                (android.provider.MediaStore.Audio.Media._ID);
//        int artistColumn = musicCursor.getColumnIndex
//                (android.provider.MediaStore.Audio.Media.ARTIST);
//        int albumColumn = musicCursor.getColumnIndex
//                (MediaStore.Audio.Media.ALBUM_ID);
//        //add songs to list
//        do {
//            long thisId = musicCursor.getLong(idColumn);
//            String thisTitle = musicCursor.getString(titleColumn);
//            String thisArtist = musicCursor.getString(artistColumn);
//            long thisAlbum = musicCursor.getLong(albumColumn);
//            deviceSongList.add(new Audio(thisId, thisTitle, thisArtist, thisAlbum));
//        }
//        while (musicCursor.moveToNext());
//    }*/
//}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 108) {
            strAdapter.notifyDataSetChanged();
            checkWhiteListed(month);
        } else if (resultCode == RESULT_OK && null != data) {
            Uri selectedItem = data.getData();
            prefs.bgUri = Objects.requireNonNull(selectedItem).toString();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedItem,
                    filePathColumn, null, null, null);
            Objects.requireNonNull(cursor).moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            if (requestCode == RESULT_LOAD_IMAGE) {
                prefs.bgUri = cursor.getString(columnIndex);
                if (!prefs.bgUri.startsWith("file://")) prefs.bgUri = "file://" + prefs.bgUri;
                cursor.close();
                setBackground();
                Preferences.animate().alpha(0).setDuration(100).start();
                Preferences.postDelayed(showPref, 300);
            } else if (requestCode == RESULT_BSRM_NOTIFICATION_TONE) {
                prefs.bsrmNotUri = cursor.getString(columnIndex);
                ((Button) findViewById(R.id.bsrm_not_sound)).setText(prefs.bsrmNotUri.substring(prefs.bsrmNotUri.lastIndexOf('/') + 1));
            } else if (requestCode == RESULT_SP_NOTIFICATION_TONE) {
                prefs.spNotUri = cursor.getString(columnIndex);
                ((Button) findViewById(R.id.sp_not_sound)).setText(prefs.spNotUri.substring(prefs.spNotUri.lastIndexOf('/') + 1));
            }
        }
    }

    public void showChantWithSP() {
        showFullScreenPlayer();
        ytContainer.setVisibility(GONE);
        fullScreenTL.setVisibility(GONE);
        bottomDrawer.setVisibility(GONE);
        findViewById(R.id.menu_full_screen).setVisibility(GONE);
        ImageView imageView = fullscreen.findViewById(R.id.arte);
        File f = new File(INTERNAL_DIR, "arte_0");
        findViewById(R.id.chantWithSPController).setVisibility(VISIBLE);
        if (f.exists()) imageView.setImageURI(Uri.fromFile(f));
        else imageView.setImageResource(R.drawable.sp);
    }

    public void hideChantWithSP() {
        bottomDrawer.setVisibility(VISIBLE);
        fullScreenTL.setVisibility(VISIBLE);
        findViewById(R.id.menu_full_screen).setVisibility(VISIBLE);
        findViewById(R.id.chantWithSPController).setVisibility(GONE);
        onBackPressed();
        setControllerUI();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (drawer == null) return;
        q.setVisibility(GONE);
        drawer.postDelayed(() -> {
            size.x = Resources.getSystem().getDisplayMetrics().widthPixels;
            size.y = Resources.getSystem().getDisplayMetrics().heightPixels;
            if (getStatusBarHeight() > 2.4 * dp10) size.y += getStatusBarHeight();
            drawer.removeAllViews();
            drawer.init();
            initDrawer();
            drawer.collapse();
            rDrawer.collapse();
            setDrawerUI(maharaja);
            drawerList.setOnTouchListener((v, event) -> {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            });
            quickBall.setX(-2 * dp10);
            findViewById(R.id.chantWithSPFAB).setX(-2 * dp10);
            if (clipContainer.getVisibility() == VISIBLE)
                shareAudioClip();
        }, 200);
    }

    public void initDrawer() {
        rDrawer.bg = (drawer.bg = findViewById(R.id.bg));
        ((ImageButton) drawer.findViewById(R.id.vedabasebtn)).setImageURI(Uri.fromFile(new File(INTERNAL_DIR, "a1_compressed1")));
        drawerList = findViewById(R.id.drawerList);
        dMAdapter = new Adapter(adptrs.dMaharaja, 0);
        arteAdapter = new Adapter(adptrs.arts, -1);
        drawerList.setAdapter(dMenuAdapter = new Adapter(adptrs.dList, dMenuAdapter == null ? 0 : dMenuAdapter.ind));
        drawerList.setOnItemClickListener(drawerMenuItemClickListener);
        int[] btns = {R.id.fb, R.id.twitter, R.id.in, R.id.yt, R.id.app};
        drawerBtns = new View[btns.length];
        for (int i = 0; i < btns.length; i++) drawerBtns[i] = findViewById(btns[i]);
        drawerName = drawer.findViewById(R.id.name);
        mainTL = findViewById(R.id.tab);
        mainTL.init();
        holderTL = findViewById(R.id.holder_tab);
        holderTL.init();
        fullScreenTL = findViewById(R.id.fst);
        fullScreenTL.init();
        wifiTL = findViewById(R.id.wifiTab);
        wifiTL.init();
        if (mServ != null && !mServ.preventPlay) {
            mainTL.setTabButtons(-1);
            fullScreenTL.setTabButtons(-1);
            holderTL.setTabButtons(-1);
            wifiTL.setTabButtons(-1);
        }
        if (albumHolder.getVisibility() == VISIBLE)
            mainTL.animate().alpha(0).setDuration(0).start();
        if (Preferences.getVisibility() == VISIBLE) setPrefView();
        storageList = findViewById(R.id.storage);
    }

    public static boolean err = false;

    @SuppressLint("SetJavaScriptEnabled")
    public CustomWebView initWebView(int id, int toolBarId, boolean zoom) {
        final CustomWebView webView = findViewById(id);
        final View mToolbar = findViewById(toolBarId);
        final View rootView = (View) findViewById(id).getParent();
        webView.setVisibility(View.GONE);
        WebSettings webSettings = webView.getSettings();
        webSettings.setAppCachePath(getBaseContext().getCacheDir().getAbsolutePath());
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptEnabled(true);
//        webSettings.setCacheMode( WebSettings.LOAD_DEFAULT );
        webSettings.setBuiltInZoomControls(zoom);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.setVisibility(View.GONE);
                if (url.startsWith("https://plus") || url.startsWith("https://play") || url.startsWith("market://") || url.startsWith("vnd:youtube") ||
                        url.startsWith("tel:") || url.startsWith("mailto:") || url.startsWith("sms:") || url.startsWith("twitter") || url.contains("facebook")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
                if (view.getId() == R.id.veda_web_view && (prefs.offlineVedabase == 1 || !mServ.isNetworkAvailable())) {
                    File f = getFileFromUrl(url);
                    if (f.exists()) {
                        view.loadUrl("file:///" + f.getAbsolutePath());
                        return true;
                    }
                }
                return false;
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                webView.setVisibility(View.GONE);
                err = true;
                TextView myTextView = rootView.findViewById(R.id.textView2);
                myTextView.setText("Error connecting to the Server");
            }

            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
                if (!err) {
                    if (view.getId() == R.id.veda_web_view && !url.endsWith(".mht")) {
                        File f = getFileFromUrl(url);
                        if (!f.exists()) {
                            webView.saveWebArchive(f.getAbsolutePath());
                            offlineVBFiles.add(f.getName());
                            vbAdapter.notifyDataSetChanged();
                        }
                    }
                    webView.setVisibility(VISIBLE);
                }
            }
        });
        webView.setGestureDetector(new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 == null || e2 == null) return false;
                if (e1.getPointerCount() > 1 || e2.getPointerCount() > 1) return false;
                else {
                    try {
                        if (e1.getY() - e2.getY() > 20) {
                            mToolbar.setVisibility(View.GONE);
                            ViewGroup.MarginLayoutParams p1 = (ViewGroup.MarginLayoutParams) webView.getLayoutParams();
                            p1.topMargin = 0;
                            webView.setLayoutParams(p1);
                            webView.invalidate();
                            return false;
                        } else if (e2.getY() - e1.getY() > 20) {
                            mToolbar.setVisibility(VISIBLE);
                            ViewGroup.MarginLayoutParams p1 = (ViewGroup.MarginLayoutParams) webView.getLayoutParams();
                            if (((ViewGroup.MarginLayoutParams) vedabaseContainer.getLayoutParams()).topMargin != 0)
                                p1.topMargin = dp10;
                            webView.setLayoutParams(p1);
                            webView.invalidate();
//                            findViewById(id).
                            /*if (e1.getY() < 500 && e2.getY() - e1.getY() > 400 ) {
                                myWebView.reload();
                            }*/
                            return false;
                        }

                    } catch (Exception e) {
                        webView.invalidate();
                    }
                    return false;
                }
            }
        }));
        return webView;
    }

    File getFileFromUrl(String u) {
        u = u.replace(vedaBaseUrl, "");
        if (u.isEmpty()) u = "base/";
        u = u.replaceAll(File.separator, "_").substring(0, u.length() - 1) + ".mht";
        return new File(INTERNAL_DIR.getAbsolutePath() + File.separator + "vedabase", u);
    }

    public void showVedabase(String url) {
        File f = getFileFromUrl(url);
        if ((prefs.offlineVedabase == 1 || !mServ.isNetworkAvailable()) && f.exists())
            VedabaseWebView.loadUrl("file:///" + f.getAbsolutePath());
        else
            VedabaseWebView.loadUrl(vedaBaseUrl + url);
        vedabaseContainer.setVisibility(VISIBLE);
        NotesWebView.setVisibility(View.GONE);
    }

    private void showSupportPopup(final Activity context) {
        ViewGroup viewGroup = context.findViewById(R.id.support_layout);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.support_activity, viewGroup);
        ((TextView) layout.findViewById(R.id.textView)).setText(Html.fromHtml(getString(R.string.about)));
        ((TextView) layout.findViewById(R.id.textView2)).setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
        // Creating the PopupWindow
        if (popup != null)
            popup.dismiss();
        popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth((int) (.95 * size.x));
        popup.setHeight((int) (.9 * size.y));
        popup.setFocusable(true);
        popup.showAtLocation(layout, 0, (int) (.025 * size.x), (int) (.1 * size.y));
    }

    public void setAdapters() {
        sAdapter = new Adapter(adptrs.allSongs, -1);
        if (mainTL.tabInd == 2) list.setAdapter(sAdapter);
        mAdapter = new Adapter(adptrs.artist, mAdapter == null ? 0 : mAdapter.ind);
        dMAdapter = new Adapter(adptrs.dMaharaja, dMAdapter == null ? 0 : dMAdapter.ind);
        if (mainTL.tabInd == 0) {
            list.setAdapter(mAdapter);
            list.setOnItemClickListener(maharajaItemClickListener);
        }
        if (playlistAudios.size() > 0)
            plAudioAdapter = new Adapter(adptrs.plAudios, plAudioAdapter == null ? 0 : plAudioAdapter.ind);
        offlineAdapter = new Adapter(adptrs.offline, -1);
        favAdapter = new Adapter(adptrs.fav, -1);
        if (holderTL.tContext == plTabs && holderTL.tabInd == 0) holderList.setAdapter(favAdapter);
        else if (holderTL.tContext == plTabs && holderTL.tabInd == 1)
            holderList.setAdapter(offlineAdapter);
        else if (holderTL.tContext == plTabs) holderList.setAdapter(plAudioAdapter);
        mAudioAdapter = new Adapter(adptrs.mAudio, mAudioAdapter == null ? 0 : mAudioAdapter.ind);
        if (holderTL.tContext == holderTabs) holderList.setAdapter(mAudioAdapter);
        drawer.collapse();
    }

    Runnable showPref = new Runnable() {
        @Override
        public void run() {
            Preferences.animate().alpha(1).setDuration(800);
        }
    };

    public void hideQuickBall(final View view) {
        view.animate().alpha(.7f).setStartDelay(500).setDuration(300).start();
        view.animate().setStartDelay(0);
        view.postDelayed(() -> {
            float x = view.getX(), x1 = x < size.x / 2f ? -5.6f * dp10 : size.x - dp10;
            if (view.getAlpha() != 1 && q.getVisibility() != VISIBLE)
                view.animate().translationXBy(x1 - x).setDuration(300).start();
        }, 2000);
    }

    int s = 0, e = 0;

    View.OnTouchListener clipPos = new View.OnTouchListener() {
        float dX = 0, ss = 6.1f * dp10, ee = size.x - 8.1f * dp10;
        int duration;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            float rawX = event.getRawX();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                dX = rawX - v.getX();
                ss = 6.1f * dp10;
                ee = size.x - 8.1f * dp10;
                duration = musicPlayer.getDuration() / 1000;
            }
            rawX = rawX - dX;
            v.setX(rawX = rawX > ee ? ee : (Math.max(rawX, ss)));
            int sec = (int) ((rawX - ss) * duration / (ee - ss)), min = sec / 60, hr = min / 60, s1 = sec;
            sec = sec % 60;
            min = min % 60;
            String durationStr = (hr == 0 ? "" : ((hr > 9 ? hr : "0" + hr) + ":")) +
                    (min > 9 ? min : "0" + min) + ":" + (sec > 9 ? sec : "0" + sec);
            if (v.getId() == R.id.startPos) {
                s = s1;
                tStartPos.setText(durationStr);
                tStartPos.setY(event.getRawY() - 8 * dp10);
                tStartPos.setX(rawX - dp10);
            } else {
                e = s1;
                tEndPos.setText(durationStr);
                tEndPos.setY(event.getRawY() - 8 * dp10);
                tEndPos.setX(rawX - dp10);
            }
            return true;
        }
    };

    public void shareAudioClip() {
        if (h.nowPlaying == null || !checkPermissions()) return;
        fullscreen.setVisibility(VISIBLE);
        drawer.disableDrawer();
        rDrawer.collapse();
        bottomDrawer.expand();
        bottomDrawer.findViewById(R.id.blurredArte).animate().alpha(0).setDuration(300).start();
        bottomDrawer.findViewById(R.id.blurredArte).setVisibility(GONE);
        bottomDrawer.findViewById(R.id.blurredArte_).setVisibility(GONE);
        fullScreenTL.setVisibility(GONE);
        findViewById(R.id.menu_full_screen).setVisibility(GONE);
        vedabaseContainer.setVisibility(GONE);
        ytContainer.setVisibility(GONE);
        toolbar.setVisibility(GONE);
        startPos.setX(6.1f * dp10);
        endPos.setX(size.x - 8.1f * dp10);
        s = 0;
        e = musicPlayer.getDuration() / 1000;
        tStartPos.setText("");
        tEndPos.setText("");
        clipContainer.setVisibility(VISIBLE);
    }

    public void shareAudioClip(String title) {
        if (title.isEmpty()) {
            dBuilder.setMessage("Set Title").show();
            return;
        }
        if (s > e) s = (s + e) - (e = s);
        if (e - s < 5) {
            dBuilder.setMessage("Clip can not be shorter than 5 Sec").show();
            return;
        }
        title = title.trim().replaceAll(" ", "_").replaceAll("\\$b", "").replaceAll(".mp3", "");
        title = title.substring(0, 1).toUpperCase().concat(title.substring(1));
        File f1 = getAudioClipFile();
        f1 = new File(f1, title + ".mp3");
        if (f1.exists()) {
            final File finalF = f1;
            dBuilder.setMessage("File " + f1.getName() + " already exists.").
                    setPositiveButton("Overwrite", (dialog, which) -> {
                        finalF.delete();
                        for (String clip : audioClips) {
                            clip.split("\\$b")[0].equals(finalF.getName().replace(".mp3", ""));
                        }
                        shareClip(finalF);
                    }).setNegativeButton("Rename", (dialog, which) -> {
                String ss = finalF.getName().replace(".mp3", "");
                try {
                    String[] sss = ss.trim().split("_");
                    int x = Integer.parseInt(sss[sss.length - 1]);
                    ss = ss.substring(0, ss.length() - (x < 10 ? 2 : x < 100 ? 3 : 4));
                } catch (Exception ignored) {
                }
                File f = new File(finalF.getParent(), ss + "_" + 0 + ".mp3");
                int ii = 1;
                while (f.exists()) f = new File(finalF.getParent(), ss + "_" + (ii++) + ".mp3");
                ((EditText) findViewById(R.id.clipTitle)).setText(f.getName().replace(".mp3", ""));
            }).show();
        } else
            shareClip(f1);
    }

    public void shareClip(File f1) {
        File f = new File(INTERNAL_DIR, "bsrm_" + h.nowPlaying.id + ".mp3");
        int duration = musicPlayer.getDuration() / 1000;
        int length = (int) f.length();
        int sPos = (int) ((1f * s / duration) * length), ePos = (int) ((1f * e / duration) * length);
        byte[] bytes = new byte[length];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(f));
            buf.read(bytes, 0, bytes.length);
            buf.close();
            FileOutputStream fileOutputStream = new FileOutputStream(f1);
            byte[] b = new byte[ePos - sPos];
            System.arraycopy(bytes, sPos, b, 0, b.length);
            fileOutputStream.write(b);
            fileOutputStream.close();
            audioClips.add(f1.getName().replace(".mp3", "") + "$b" + getUrlToClip(s, e));
            clipAdapter.notifyDataSetChanged();
//            fileOutputStream.write((nowPlaying.index+","+nowPlaying.id+","+s+","+e).getBytes());
        } catch (IOException ignored) {
        }
        if (!f1.exists()) return;
        startActivity(Intent.createChooser(new Intent(Intent.ACTION_SEND).setType("audio/mp3")
                .putExtra(Intent.EXTRA_STREAM, Uri.parse("content://" +
                        authority + "/" + f1.getName()))
                .putExtra(Intent.EXTRA_TEXT, "\uD83D\uDC96  " + getString(R.string.app_name) + " \uD83D\uDC96  \n\nhttps://play.google.com/store/apps/details?id=" + getPackageName())
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION), "Share Clip"));
    }

    private final IntentFilter intentFilter = new IntentFilter();
    WiFiDirectBroadcastReceiver receiver;
    static WifiP2pManager.Channel mChannel;
    static WifiP2pManager mManager;
    WifiP2pDevice device;
    static String wifiName = "";
    static boolean activityVisile = true, wifiP2pEnabled = false, p2pConnected = false, sending = false;
    static List<WifiP2pDevice> peers = new ArrayList<>();

    public void setIsWifiP2pEnabled(Boolean wifiP2pEnabled) {
        k.wifiP2pEnabled = wifiP2pEnabled;
    }

    float[][] wifiPos = {{0, -.24f}, {-.24f, 0}, {.25f, 0}, {-.15f, .23f}, {.17f, .23f}};
    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            Collection<WifiP2pDevice> refreshedPeers = peerList.getDeviceList();
            if (sending && !refreshedPeers.equals(peers)) {
                for (int i = 0; i < peers.size(); i++)
                    waiting.removeView(waiting.findViewById(i));
                peers.clear();
                peers.addAll(refreshedPeers);
                for (int i = 0; i < peers.size(); i++) {
                    String deviceName = peers.get(i).deviceName;
                    if (!deviceName.startsWith("sp_")) continue;
                    View user = inflate(k.this, R.layout.device, null);
                    user.setId(i);
                    waiting.addView(user);
                    user.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    ((TextView) user.findViewById(R.id.name)).setText(deviceName.substring(3));
                    float x = Math.min(size.x, size.y),
                            x1 = 5 * dp10, y1 = 10 * dp10;
                    user.setX(size.x / 2f - x1 + wifiPos[i % wifiPos.length][0] * x);
                    user.setY(size.y / 2f - y1 + wifiPos[i % wifiPos.length][1] * x);
                    final int finalI = i;
                    user.setOnClickListener(v -> connect(finalI));
                }
            }
        }
    };
    static WifiP2pInfo info;
    WifiP2pManager.ConnectionInfoListener connectionListener = info -> {
        k.info = info;
        if (info.groupFormed) {
            p2pConnected = true;
            onBackPressed();
            (new establishP2p()).execute();
        } else p2pConnected = false;
    };

    public void connect(int i) {
        device = peers.get(i);
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        } else {
            mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onFailure(int reason) {
                    Toast.makeText(k.this, "Connect failed. Retry.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    static int port = 1513, transfer_port = 4287;
    static PrintWriter output;
    static BufferedReader input;

    public static class establishP2p extends AsyncTask<Void, String, String> {
        boolean p2pEstablished = false;

        @SuppressLint("WakelockTimeout")
        @Override
        protected void onPreExecute() {
            mWakeLock.acquire();
            wifi_progress.postDelayed(() -> {
                if (!p2pEstablished) {
                    establishP2p.this.cancel(true);
                    onPostExecute("");
                }
            }, 5000);
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                getSocket();
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true); //Autoflush
                output.println("Hare Krishna..." + "sp_vani," + wifiName);
                String s = input.readLine();
                String[] s1 = s.split(",");
                if (s1[0].equals("Hare Krishna..." + "sp_vani")) p2pEstablished = true;
                else return "connection failed";
                publishProgress(s);
                s = input.readLine();
                if (s == null) s = input.readLine();
                while (!s.equals("hk_done")) {
                    s = decodeStr(s);
                    if (s.startsWith("info: sending ")) {
                        publishProgress(s);
                        output.println(encodeUrl(s.replace("info: sending ", "info: receiving ")));
                        s = input.readLine();
                        continue;
                    } else if (s.startsWith("info: receiving ")) {
                        publishProgress(s);
                        s = input.readLine();
                        continue;
                    } else if (s.startsWith("newFile: ")) {
                        publishProgress(s);
                        s = input.readLine();
                        continue;
                    } else if (s.equals("Hari Bol")) {
                        publishProgress(s);
                        output.println(encodeUrl("Radhe Radhe"));
                        s = input.readLine();
                        continue;
                    } else if (s.equals("Radhe Radhe")) {
                        publishProgress("Hari Bol");
                        s = input.readLine();
                        continue;
                    } else if (s.equals("+")) {
                        publishProgress(s);
                        s = input.readLine();
                        continue;
                    }
                    s1 = s.split(",");
                    publishProgress("newFile: " + s);
                    output.println(encodeUrl("newFile: " + s));
                    FileOutputStream fileOutputStream = null;
                    switch (s1[0]) {
                        case "0":
                            fileOutputStream = new FileOutputStream(new File(INTERNAL_DIR, "bsrm_" + s1[1] + ".mp3"));
                            break;
                        case "1":
                            fileOutputStream = new FileOutputStream(new File(INTERNAL_DIR, s1[1]));
                            break;
                        case "2":
                            fileOutputStream = new FileOutputStream(new File(new File(INTERNAL_DIR, "vedabase"), s1[1]));
                    }
                    s = input.readLine();
                    while (!decodeStr(s).equals("hk_eof") && fileOutputStream != null) {
                        fileOutputStream.write(Base64.decode(s, Base64.DEFAULT));
                        s = input.readLine();
                        publishProgress("+");
                        output.println(encodeUrl("+"));
                    }
                    publishProgress(decodeStr(s));
                    s = input.readLine();
                }
                input.close();
                output.close();
                return s;
            } catch (IOException e1) {
                return e1.getMessage();
            }
        }

        int n = 0, ind = -1, progress = 0, current_max = 0;
        boolean receiving;

        @Override
        protected void onProgressUpdate(String... values) {
            String s = values[0];
//            dBuilder.setMessage(s).show();
            if (s.startsWith("Hare Krishna") && p2pEstablished) {
                if (getWifiCount() > 0)
                    (new RunThread()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, sendFile);
                s = s.split(",")[1];
                conn_to.setText("Connected to " + s.substring(3, s.length()));
            } else if (s.startsWith("info: sending ")) {
                receiving = true;
                n = Integer.parseInt(s.replace("info: sending ", ""));
                wifi_progress.setText("Receiving 0 of " + n + " files.");
                wifi_transferring.setVisibility(VISIBLE);
            } else if (s.startsWith("info: receiving ")) {
                receiving = false;
                n = Integer.parseInt(s.replace("info: receiving ", ""));
                wifi_progress.setText("Transferring 0 of " + n + " files.");
                wifi_transferring.setVisibility(VISIBLE);
            } else if (s.equals("Hari Bol")) {
                wifi_transferring.setVisibility(GONE);
                ind = -1;
                for (Adapter adapter : adapters) {
                    adapter.selectedItems = new boolean[adapter.getCount()];
                    adapter.wifiSelectedItems = 0;
                    adapter.notifyDataSetChanged();
                }
                setWifiCount();
            } else if (s.startsWith("newFile: ")) {
                String[] s1 = s.replace("newFile: ", "").split(",");
                ind++;
                progress = 0;
                switch (s1[0]) {
                    case "0":
                        int ind = Integer.parseInt(s1[3]);
                        if (!offlineAdapter.indices.contains(ind)) offlineAdapter.indices.add(ind);
                        break;
                    case "1":
                        if (!arteList.contains(s1[1])) arteList.add(s1[1]);
                        break;
                    case "2":
                        if (!offlineVBFiles.contains(s1[1])) offlineVBFiles.add(s1[1]);
                }
                try {
                    current_max = (int) (Long.parseLong(s1[2]) / 53);
                } catch (Exception e) {
                    dBuilder.setMessage(s).show();
                    current_max = 0;
                }
                wifiProgressBar.setMax(current_max);
            } else if (s.equals("+")) {
                progress++;
            }
            wifi_progress.setText((receiving ? "Receiving " : "Transferring ") + ind + " of " + n +
                    " files. (" + (current_max == 0 ? 0 : 100 * progress / current_max) + "%)");
            wifiProgressBar.setProgress(progress);
        }

        @Override
        protected void onPostExecute(String s) {
            wifi_transferring.setVisibility(GONE);
            for (Adapter adapter : adapters) {
                adapter.selectedItems = new boolean[adapter.getCount()];
                adapter.wifiSelectedItems = 0;
                adapter.notifyDataSetChanged();
            }
            setWifiCount();
            mManager.removeGroup(mChannel, null);
            mManager.stopPeerDiscovery(mChannel, null);
            wifiManager.setWifiEnabled(false);
            wifiManager.setWifiEnabled(true);
            p2pConnected = false;
            if (activityVisile) {
                if (s.equals("connection failed"))
                    dBuilder.setMessage("Could not Connect to the device\n\nPlease make sure that " +
                            "your friend has started or received connection using " + APP_NAME + " App.").setPositiveButton("Ok", null).show();
                else
                    dBuilder.setMessage("Connection Interrupted").setPositiveButton("Ok", null).show();
            }
            conn_to.setText("Experimental Feature...");
            mWakeLock.release();
        }
    }

    static boolean sendingFiles = false;
    static Adapter[] adapters;
    public static Runnable sendFile = new Runnable() {
        @Override
        public void run() {
            sendingFiles = true;
            output.println(encodeUrl("info: sending " + getWifiCount()));
            for (int j = 0; j < adapters.length; j++) {
                Adapter adapter = adapters[j];
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (adapter.selectedItems[i]) {
                        File f = null;
                        switch (j) {
                            case 0:
                                int ind = offlineAdapter.indices.get(i);
                                long id = audio.get(ind).id;
                                f = new File(INTERNAL_DIR, "bsrm_" + id + ".mp3");
                                output.println(encodeUrl(0 + "," + id + "," + f.length() + "," + ind));
                                break;
                            case 1:
                                if (i > 1) {
                                    f = new File(INTERNAL_DIR, arteList.get(i));
                                    output.println(encodeUrl(1 + "," + arteList.get(i) + "," + f.length()));
                                } else f = null;
                                break;
                            case 2:
                                f = new File(new File(INTERNAL_DIR, "vedabase"), offlineVBFiles.get(i));
                                output.println(encodeUrl(1 + "," + offlineVBFiles.get(i) + "," + f.length()));
                                break;
                        }
                        if (f == null) continue;
                        int len;
                        String line;
                        byte[] buf = new byte[1024];
                        try {
                            InputStream inputStream = contentResolver.openInputStream(Uri.fromFile(f));
                            while ((len = Objects.requireNonNull(inputStream).read(buf)) != -1) {
                                line = Base64.encodeToString(buf, 0, len, Base64.DEFAULT);
                                output.println(line);
                            }
                            inputStream.close();
                        } catch (IOException ignored) {
                        }
                        output.println(encodeUrl("hk_eof"));
                    }
                }
            }
            output.println(encodeUrl("Hari Bol"));
            sendingFiles = false;
        }
    };

    public void createNewPlaylist(final int listInd, final boolean isPlaylist, final int audioIndex) {
        final EditText input = new EditText(k.this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(k.this);
        builder.setTitle("Create New Playlist")
                .setPositiveButton("Create", (dialog, id) -> {
                    newPlaylistName = input.getText().toString().trim();
                    if (newPlaylistName.equals("Favourites") || newPlaylistName.equals("Offline")) {
                        dBuilder.setMessage("Reserved Names!!!\n\n" + newPlaylistName +
                                "\nCan not be used for playlist name").show();
                    } else {
                        if (!playlist.contains(newPlaylistName)) {
                            playlist.add(newPlaylistName);
                            playlistAudios.add(new ArrayList<>());
                        }
                        if (listInd == -10)
                            dBuilder.setMessage("You can add bhajans, lectures, " +
                                    "albums or other playlists to this playlist by selecting \"Add to Playlist\" option " +
                                    "from their context menu.\nIf another playlist with same name already exists, new playlist will not be created.").show();
                        else
                            addToPlayList(listInd, isPlaylist, audioIndex);
                        plAdapter = new Adapter(adptrs.playlist, -1);
                        if (mainTL.tabInd == 1)
                            list.setAdapter(plAdapter);
                    }
                }).setNegativeButton("Cancel", null).setView(input).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean result = true;
        for (int r : grantResults)
            if (r != PackageManager.PERMISSION_GRANTED) {
                result = false;
                break;
            }
        if (!result)
            dBuilder.setMessage("You must grant permission to access storage to avail this functionality").
                    setPositiveButton("Grant Permission", (dialog, which) -> checkPermissions()).show();
    }

    public boolean searchPeers(final boolean send) {
        sending = send;
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return false;
        } else {
            mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    waiting.setVisibility(VISIBLE);
                    ((TextView) findViewById(R.id.stat)).setText(send ? "Searching for Peers...\n\nAsk your friend to tap on Receive button.\n" :
                            "Waiting for Sender to Connect...\n\nChant Hare Krishna and Be Happy");
                    wifi_progress.postDelayed(new Runnable() {
                        boolean setPressed = false;

                        @Override
                        public void run() {
                            findViewById(R.id.pulse).setPressed(setPressed = !setPressed);
                            if (!p2pConnected && (!activityVisile || waiting.getVisibility() == VISIBLE))
                                wifi_progress.postDelayed(this, 300);
                        }
                    }, 300);
                }

                @Override
                public void onFailure(int reasonCode) {
                    Toast.makeText(k.this, "Discovery Failed : " + reasonCode + "\nPlease try again later.",
                            Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }
    }

    static Socket socket = null;

    public static void getSocket() throws IOException {
        if (socket != null && !socket.isClosed()) return;
        if (info.isGroupOwner) {
            ServerSocket serverSocket = new ServerSocket(port);
            socket = serverSocket.accept();
        } else {
            socket = new Socket();
            socket.connect((new InetSocketAddress(info.groupOwnerAddress.getHostAddress(), port)), 1000);
        }
    }

    public static byte[] encodeBytes(byte[] bytes) { //Not working properly
        boolean[] key = {true, false, false, false, true, false, true, true, false, false, true};
        int x = key.length;
        for (int i = 0; i < bytes.length / 2 - 1; i++) {
            if (key[i % x]) {
                byte temp = bytes[2 * i + 1];
                bytes[2 * i + 1] = bytes[2 * i];
                bytes[2 * i] = temp;
            }
        }
        return bytes;
    }

    static String encodeUrl(String str) {
        boolean[] key = {true, false, false, false, true, false, true, true, false, false, true};
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0, j = 0; i < str.length(); i++, j++) {
            if (key[j % key.length])
                stringBuilder.append((char) (str.charAt(i) - 1));
            else
                stringBuilder.append((char) (str.charAt(i) + 1));
        }
        return stringBuilder.toString();
    }

    public int getStatusBarHeight() {
        int resCode = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resCode > 0)
            return getResources().getDimensionPixelSize(resCode);
        else
            return 0;
    }

    //    ----------------Stickers---------------------
//    ------------------------------------------------------------------------------------------------
    public static final String EXTRA_STICKER_PACK_ID = "sticker_pack_id",
            EXTRA_STICKER_PACK_AUTHORITY = "sticker_pack_authority",
            EXTRA_STICKER_PACK_NAME = "sticker_pack_name",
            WA_PKG = "com.whatsapp",
            WA_BIZ_PKG = "com.whatsapp.w4b";
    public static String STICKER_APP_AUTHORITY;
    final static int N_STICKERS = 138;
    int month = -1, day = 0;
    String baseUrl = "https://raw.githubusercontent.com/krishna-apps/s/master/";
    final static int waBgColor = 0xFFEDE6DE, nStrPerPack = 28;
    int nPack = N_STICKERS / nStrPerPack + (N_STICKERS % nStrPerPack == 0 ? 0 : 1);
    GridView strList;
    Button add_to_wa;
    StrAdapter strAdapter;
    ProgressBar strProgress;

    public void checkWhiteListed(int m) {
        runOnUiThread(() -> {
            if (m < 0) {
                add_to_wa.setVisibility(GONE);
            } else {
                add_to_wa.setVisibility(VISIBLE);
                String s = (m + 1) + "";
                boolean whitelisted = WhitelistCheck.isWhitelisted(k.this, s);
                runOnUiThread(() -> {
                    if (whitelisted) {
                        add_to_wa.setEnabled(false);
                        add_to_wa.setText(R.string.already_added_to_wa);
                    } else if (isPackDownloaded(m)) {
                        add_to_wa.setEnabled(true);
                        add_to_wa.setText(R.string.add_sticker_pack_to_whatsapp);
                    } else {
                        add_to_wa.setEnabled(true);
                        add_to_wa.setText(R.string.get_str_pack);
                    }
                });
            }
        });
    }

    public boolean isPackDownloaded(int m) {
        return getImgFile(m, 7).exists();
    }

    public File getImgFile(int m, int d) {
        return new File(getFilesDir(), (m * nStrPerPack + d) + "");
    }

    public void addToWa(int month) {
        File file = new File(getFilesDir(), "tray_" + (month + 1) + ".png");
        if (!file.exists()) {
            new Thread(() -> {
                try {
                    FileOutputStream outputStream = new FileOutputStream(file);
                    Bitmap.createScaledBitmap(BitmapFactory.decodeStream(new FileInputStream(getImgFile(month, 1))), 96, 96, false)
                            .compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                runOnUiThread(() -> {
                    AddSticker((month + 1) + "", "Strila Prabhupada Sticker Pack - " + (month + 1));
                });
            }).start();
        }
    }

    public void AddSticker(String identifier, String stickerPackName) {
        Intent intentToAddStickerPack = createIntentToAddStickerPack(identifier, stickerPackName);
        try {
            startActivityForResult(intentToAddStickerPack.setPackage(WA_PKG), 108);
        } catch (Exception e) {
            try {
                startActivityForResult(intentToAddStickerPack.setPackage(WA_BIZ_PKG), 108);
            } catch (Exception e1) {
                new AlertDialog.Builder(k.this).setTitle("Package Not Installed")
                        .setMessage("WhatsApp is not installed")
                        .setPositiveButton("Install WhatsApp", (dialogInterface, i) -> startActivity(new Intent(Intent.ACTION_VIEW).setData(
                                Uri.parse("https://play.google.com/store/apps/details?id=" + WA_PKG))));
            }
        }
    }

    private Intent createIntentToAddStickerPack(String identifier, String stickerPackName) {
        Intent intent = new Intent();
        intent.setAction("com.whatsapp.intent.action.ENABLE_STICKER_PACK");
        intent.putExtra(EXTRA_STICKER_PACK_ID, identifier);
        intent.putExtra(EXTRA_STICKER_PACK_AUTHORITY, getPackageName() + ".strprovider");
        intent.putExtra(EXTRA_STICKER_PACK_NAME, stickerPackName);
        return intent;
    }

    interface hk {
        void create(Bitmap b, File f) throws IOException;
    }

    interface hk1 {
        void onErr();
    }

    public class StrAdapter extends BaseAdapter {
        k mContext;
        int actionH = 57 * dp;

        StrAdapter() {
            mContext = k.this;
            strList.setNumColumns(1);
        }

        protected void createStr(File file, Bitmap src) {
            new Thread(() -> {
                Bitmap sticker = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                Canvas canvas = new Canvas(sticker);

                int w = src.getWidth(), h = src.getHeight();
                float s = 480f / Math.max(w, h);

                canvas.drawBitmap(src, new Rect(0, 0, w, h),
                        new RectF((512 - s * w) / 2, (512 - s * h) / 2, (512 + s * w) / 2, (512 + s * h) / 2), paint);

                try {
                    FileOutputStream stream = new FileOutputStream(file);
                    sticker.compress(Bitmap.CompressFormat.WEBP, 75, stream);
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        protected int getN(int m) {
            return m < 0 ? nPack : (m == nPack ? nStrPerPack : N_STICKERS % nStrPerPack);
        }

        @Override
        public int getCount() {
            return month < 0 ? nPack : (month == nPack ? nStrPerPack : N_STICKERS % nStrPerPack);
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        protected void getPackStr(int m, int i) {
            String[] sParams = getSParams(m, i);
            File file = new File(getFilesDir(), sParams[0]);
            if (file.exists()) {
                getNextPackStr(m, i);
                return;
            }

            fetch(sParams, ((src, f) -> {
                createStr(file, src);
                getNextPackStr(m, i);
            }), null);
        }

        public void fetch(String[] fileAndUrl, hk h, hk1 h1) {
            new Thread(() -> {
                if (!mWakeLock.isHeld())
                    mWakeLock.acquire();
                File f = new File(getFilesDir(), fileAndUrl[0]);
                if (!f.exists()) {
                    InputStream input = null;
                    HttpURLConnection connection = null;
                    try {
                        connection = (HttpURLConnection) new URL(fileAndUrl[1]).openConnection();
                        connection.connect();
                        // expect HTTP 200 OK, so we don't mistakenly save error report
                        // instead of the file
                        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
//                            if (isDebug)
                            Log.e("Server returned HTTP", connection.getResponseCode()
                                    + " " + connection.getResponseMessage() + ", url = " + fileAndUrl[1]);
                            return;
                        }
                        // download the file
                        input = connection.getInputStream();
                        Bitmap b = BitmapFactory.decodeStream(input);
                        h.create(b, f);
                    } catch (IOException e) {
                        Log.e("fetch", fileAndUrl[0] + "; " + e.getMessage());
                        if (h1 != null) h1.onErr();
                    } finally {
                        try {
                            if (input != null)
                                input.close();
                        } catch (IOException ignored) {
                        }
                        if (connection != null)
                            connection.disconnect();
                    }
                }
            }).start();
        }

        protected void getNextPackStr(int m, int i) {
            i++;
            int n = getN(m);
            if (i < n) {
                int finalI = i;
                runOnUiThread(() -> {
                    strProgress.setVisibility(VISIBLE);
                    strProgress.setMax(n);
                    strProgress.setProgress(finalI);
                });
                new Thread(() -> getPackStr(m, finalI)).start();
            } else {
                runOnUiThread(() -> {
                    strProgress.setVisibility(GONE);
                    strAdapter.notifyDataSetChanged();
                });
            }
        }

        protected String[] getSParams(int m, int i) {
            return getStrFileAndUrl(m * nStrPerPack + i);
        }

        public String[] getStrFileAndUrl(int i) {
            return new String[]{(i + 1) + "", baseUrl + i};
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (month < 0) {
                LinearLayout l;
                if (!(convertView instanceof LinearLayout)) {
                    l = new LinearLayout(mContext);
                    l.setOrientation(LinearLayout.VERTICAL);
//                    l.setBackgroundResource(R.drawable.rect1);
                    l.setPadding(5 * dp, 5 * dp, 5 * dp, 5 * dp);
                    l.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    LinearLayout layout = new LinearLayout(mContext);
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    layout.setPadding(5 * dp, 5 * dp, 5 * dp, 5 * dp);
                    layout.setGravity(CENTER_VERTICAL);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, actionH);
                    params.weight = 1;
                    for (int i = 0; i < 5; i++) {
                        ImageView imageView = new ImageView(mContext);
                        imageView.setLayoutParams(params);
                        imageView.setPadding(3 * dp, 3 * dp, 3 * dp, 3 * dp);
                        layout.addView(imageView);
                    }
                    ImageView button = new ImageView(mContext);
                    button.setLayoutParams(new ViewGroup.LayoutParams(actionH, actionH));
                    button.setPadding(15 * dp, 15 * dp, 15 * dp, 15 * dp);
//                    button.setBackgroundResource(R.drawable.ripple);
                    layout.addView(button);

                    TextView textView = new TextView(mContext);
                    textView.setPadding(5 * dp, 5 * dp, 5 * dp, 5 * dp);
                    textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    l.addView(textView);
                    l.addView(layout);
                } else l = (LinearLayout) convertView;
                TextView t = (TextView) l.getChildAt(0);
                t.setText("Srila Prabhupada Stickers " + (position + 1));
                LinearLayout layout = (LinearLayout) l.getChildAt(1);
                int trayPos = 1;
                for (int i = 0; i < 5 && i < getN(position); i++) {
                    File f = getImgFile(position, (i + trayPos - 1) % getN(position) + 1);
                    ImageView imageView = (ImageView) layout.getChildAt(i);
//                    Glide.with(mContext).load(f).signature(new ObjectKey(f.lastModified())).error(R.drawable.spl)
//                            .apply(new RequestOptions().override(actionH, actionH)).into(imageView);
                    if (f.exists())
                        (imageView).setImageURI(Uri.fromFile(f));
                    else {
                        (imageView).setImageResource(R.drawable.sp);
                        String[] sParams = getSParams(position, i);
                        fetch(sParams, ((src, file) -> {
                            createStr(file, src);
                            runOnUiThread(this::notifyDataSetChanged);
                        }), null);
                    }
                }
                if (isPackDownloaded(position)) {
                    ((ImageView) layout.getChildAt(5)).setImageResource(android.R.drawable.ic_input_add);
                    layout.getChildAt(5).setOnClickListener((v) -> addToWa(position));
                } else {
                    ((ImageView) layout.getChildAt(5)).setImageResource(R.drawable.download);
                    layout.getChildAt(5).setOnClickListener((v) -> getPackStr(position, 0));
                }
                convertView = l;
            } else {
                final ViewHolder holder;
                if (!(convertView instanceof FrameLayout)) {
                    convertView = getLayoutInflater().inflate(R.layout.liq, parent, false);
                    convertView.setTag(holder = new ViewHolder(convertView));
                } else
                    holder = (ViewHolder) convertView.getTag();

                strList.setColumnWidth(170 * dp);
                convertView.setLayoutParams(new ViewGroup.LayoutParams(170 * dp, 170 * dp));
                int giw = 160 * dp;
                final File f = getImgFile(month, position + 1);
//                    Glide.with(mContext).load(f).signature(new ObjectKey(f.lastModified())).error(R.drawable.spl)
//                            .apply(new RequestOptions().override(giw, giw).transform(new RoundedCorners(8 * dp))).into(holder.arte);
                if (f.exists()) {
                    holder.arte.setImageURI(Uri.fromFile(f));
                } else {
                    holder.arte.setImageResource(R.drawable.sp);
                    String[] sParams = getSParams(month, position);
                    fetch(sParams, ((src, file) -> {
                        createStr(file, src);
                        runOnUiThread(this::notifyDataSetChanged);
                    }), null);
                }
            }
            convertView.setOnClickListener(v -> {
                if (month < 0) {
                    if (isPackDownloaded(position)) {
                        setPackAdapter(position);
                    } else {
                        new AlertDialog.Builder(mContext).setMessage("Get This Sticker Pack?")
                                .setPositiveButton("Get", (dialogInterface, i) -> getPackStr(position, 0)).show();
                    }
                }/* else {
                    day = position + 1;
                }*/
//                ((ImageView) findViewById(R.id.home)).setImageResource(R.drawable.ic_arrow_back_black_24dp);
            });
            return convertView;
        }

        void setPackAdapter(int position) {
            strList.animate().scaleY(0).scaleX(0).setDuration(0).start();
            month = position;
            notifyDataSetChanged();
            strList.animate().scaleY(1).scaleX(1).setDuration(300).start();
            strList.setPadding(0, 0, 0, 48 * dp);
            strList.setNumColumns(GridView.AUTO_FIT);
            new Thread(() -> checkWhiteListed(month)).start();
        }
    }

    static class ViewHolder {
        ImageView arte;

        ViewHolder(View v) {
            arte = v.findViewById(R.id.arte);
        }
    }
}