package com.hb.player;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private static final int REQ_PERM = 1;
    private final ArrayList<VideoItem> items = new ArrayList<>();
    private VideoAdapter adapter;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvEmpty = findViewById(R.id.tvEmpty);
        ListView list = findViewById(R.id.list);
        adapter = new VideoAdapter(this, items);
        list.setAdapter(adapter);

        list.setOnItemClickListener((parent, view, position, id) -> {
            ArrayList<String> uris = new ArrayList<>();
            ArrayList<String> names = new ArrayList<>();
            for (VideoItem v : items) {
                uris.add(v.uri);
                names.add(v.name);
            }
            Intent i = new Intent(this, PlayerActivity.class);
            i.putStringArrayListExtra("uris", uris);
            i.putStringArrayListExtra("names", names);
            i.putExtra("index", position);
            startActivity(i);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String perm = Build.VERSION.SDK_INT >= 33
                ? Manifest.permission.READ_MEDIA_VIDEO
                : Manifest.permission.READ_EXTERNAL_STORAGE;
        if (checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED) {
            loadVideos();
        } else {
            requestPermissions(new String[]{perm}, REQ_PERM);
        }
    }

    @Override
    public void onRequestPermissionsResult(int code, String[] perms, int[] results) {
        super.onRequestPermissionsResult(code, perms, results);
        if (code == REQ_PERM) {
            if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
                loadVideos();
            } else {
                tvEmpty.setText(R.string.permission_needed);
                tvEmpty.setVisibility(View.VISIBLE);
            }
        }
    }

    private void loadVideos() {
        new Thread(() -> {
            ArrayList<VideoItem> result = new ArrayList<>();
            String[] proj = {
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.DURATION,
                    MediaStore.Video.Media.SIZE
            };
            Uri collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI; // internal + SD card
            try (Cursor c = getContentResolver().query(collection, proj, null, null,
                    MediaStore.Video.Media.DATE_ADDED + " DESC")) {
                if (c != null) {
                    while (c.moveToNext()) {
                        long id = c.getLong(0);
                        Uri uri = ContentUris.withAppendedId(collection, id);
                        result.add(new VideoItem(uri.toString(), c.getString(1),
                                c.getLong(2), c.getLong(3)));
                    }
                }
            } catch (Exception ignored) { }

            runOnUiThread(() -> {
                items.clear();
                items.addAll(result);
                adapter.notifyDataSetChanged();
                tvEmpty.setText(R.string.no_videos);
                tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            });
        }).start();
    }
}
