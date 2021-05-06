package v;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;

public class Provider extends ContentProvider {
    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.e("query - Uri", uri.toString());
        MatrixCursor cursor = new MatrixCursor(new String[]{"_id", "_data", "mime_type", "_display_name"});
        String s = uri.getLastPathSegment();
        if (s.endsWith(".mp3"))
            cursor.addRow(new Object[]{
                    0,
                    new File(getContext().getFilesDir() + "/clips/" + s),
                    "audio/mp3", s.replaceAll("_", " ")
            });
        else
            cursor.addRow(new Object[]{
                    0,
                    new File(getContext().getCacheDir(), s),
                    s.endsWith(".png") ? "image/png" : "*/*", s.replaceAll("_", " ")
            });
        return cursor;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        Log.e("openFile", uri.toString());
        String s = uri.getLastPathSegment();
        Context ctx = getContext();
        return ParcelFileDescriptor.open(new File(s.endsWith(".mp3") ? new File(ctx.getFilesDir(), "clips") : ctx.getCacheDir(), s), ParcelFileDescriptor.MODE_READ_ONLY);
    }

    @Override
    public String getType(Uri uri) {
        String s = uri.getLastPathSegment();
        return s.endsWith(".png") ? "image/png" : s.endsWith(".mp3") ? "audio/mp3" : "*/*";
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException("Not supported");
    }
}
