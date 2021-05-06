package v;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;

import static v.k.INTERNAL_DIR;
import static v.k.dp10;

public class sQuote extends Activity {
    // Remember some things for zooming
    PointF start = new PointF();
    ImageView imageView;
    Calendar calendar = Calendar.getInstance();
    int days = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        INTERNAL_DIR = getFilesDir();
        String today = android.text.format.DateFormat.format("MMMM_dd", calendar).toString();
        final File file = new File(INTERNAL_DIR.getPath() + '/' + today + ".png");
        setContentView(R.layout.p);
        imageView = findViewById(R.id.prabhupada_quotes);

        imageView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    start.set(event.getX(), event.getY());
                case MotionEvent.ACTION_MOVE:
                    float scale = 1 - Math.abs(event.getX() - start.x) / imageView.getMeasuredWidth() / 2;
                    imageView.animate().alpha(scale).scaleX(scale).scaleY(scale)
                            .setDuration(0).start();
                    break;
                case MotionEvent.ACTION_UP:
                    if (start.x - event.getRawX() >= dp10) {
                        setQuote(1);
                    } else if (event.getRawX() - start.x > dp10) {
                        setQuote(-1);
                    } else {
                        imageView.performClick();
                    }
            }
            return true;
        });

        if (file.exists()) {
            setQuote(0);
            findViewById(R.id.button).setOnClickListener(view -> new Thread(() -> {
                String fileName = android.text.format.DateFormat.format("MMMM_dd", calendar).toString() + ".png";
                String authority = "com.mayank.srilaprabhupadavani.provider";
                try {
                    newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File(getCacheDir(), fileName)));
                    runOnUiThread(() -> startActivity(Intent.createChooser(new Intent(Intent.ACTION_SEND).setType("image/png")
                            .putExtra(Intent.EXTRA_STREAM, Uri.parse("content://" +
                                    authority + "/" + fileName))
                            .putExtra(Intent.EXTRA_TEXT, "\uD83D\uDC96  " + getString(R.string.app_name) + " \uD83D\uDC96  \n\nhttps://play.google.com/store/apps/details?id=" + getPackageName())
                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION), "Share Today's Quote")));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }).start());
        } else {
            Toast.makeText(sQuote.this, "Sorry!!!\n\nCould not fetch the quote", Toast.LENGTH_SHORT).show();
            finish();
        }
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Alarm.SP_QUOTE_NOT_ID);
    }

    Bitmap newBitmap;

    public void setQuote(int direction) {
        calendar.add(Calendar.DAY_OF_YEAR, direction);
        days += direction;
        String dateStr = android.text.format.DateFormat.format("MMMM-dd", calendar).toString();
        final File file = new File(INTERNAL_DIR.getPath() + '/' + dateStr.replace('-', '_') + ".png");
        if (file.exists()) {
            final Bitmap image = BitmapFactory.decodeFile(file.getPath());
            newBitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), image.getConfig());
            Canvas canvas = new Canvas(newBitmap);
            canvas.drawColor(Color.parseColor("#f6e3e3"));
            canvas.drawBitmap(image, 0, 0, null);
            imageView.setImageBitmap(newBitmap);
        } else {
            calendar.add(Calendar.DAY_OF_YEAR, -direction);
        }
        imageView.animate().alpha(1).scaleX(1).scaleY(1).setDuration(0).start();
        calendar.add(Calendar.DAY_OF_YEAR, direction);
        dateStr = android.text.format.DateFormat.format("MMMM-dd", calendar).toString();
        final File file1 = new File(INTERNAL_DIR.getPath() + '/' + dateStr.replace('-', '_') + ".png");
        if (!file1.exists()) {
            (new Thread(() -> {
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DAY_OF_YEAR, days);
                k.fetchSrilaPrabhupadaQuotes(c);
            })).start();
        }
        calendar.add(Calendar.DAY_OF_YEAR, -direction);
    }

//    private File takeScreenshot(View view, String fileName) {
//        File imageFile = null;
//        try {
//            view.setDrawingCacheEnabled(true);
//            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
//            view.setDrawingCacheEnabled(false);
//
//            File imageDir = new File(Environment.getExternalStorageDirectory().toString() + "/SrilaPrabhupadaQuotes");
//            if (!imageDir.exists()) imageDir.mkdir();
//            imageFile = new File(imageDir.getPath() + "/" + fileName);
//            FileOutputStream outputStream = new FileOutputStream(imageFile);
//            int quality = 100;
//            bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream);
//            outputStream.flush();
//            outputStream.close();
//
////            openScreenshot(imageFile);
//        } catch (Throwable e) {
//            // Several error may come out with file handling or DOM
//            e.printStackTrace();
//        }
//        return imageFile;
//    }

//    public boolean checkPermissions() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
//                (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
//                        checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
//            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//            Toast.makeText(sQuote.this, "You must grant permission to access storage to avail this functionality", Toast.LENGTH_SHORT).show();
//            return false;
//        } else return true;
//    }

//    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
//
//        @Override
//        public boolean onDown(MotionEvent e) {
//            return true;
//        }
//
//        // event when double tap occurs
//        @Override
//        public boolean onDoubleTap(MotionEvent e) {
//            float x = e.getX();
//            float y = e.getY();
//
//            Log.d("Double Tap", "Tapped at: (" + x + "," + y + ")");
//
//            return true;
//        }
//
//    }
}
