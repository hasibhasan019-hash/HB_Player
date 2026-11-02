package com.hb.player;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class VideoAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final List<VideoItem> items;

    public VideoAdapter(Context ctx, List<VideoItem> items) {
        this.inflater = LayoutInflater.from(ctx);
        this.items = items;
    }

    @Override public int getCount() { return items.size(); }
    @Override public Object getItem(int i) { return items.get(i); }
    @Override public long getItemId(int i) { return i; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_video, parent, false);
        }
        VideoItem v = items.get(position);
        ((TextView) convertView.findViewById(R.id.tvName)).setText(v.name);
        ((TextView) convertView.findViewById(R.id.tvInfo))
                .setText(formatTime(v.durationMs) + "  •  " + formatSize(v.sizeBytes));
        return convertView;
    }

    static String formatTime(long ms) {
        long s = ms / 1000;
        long h = s / 3600, m = (s % 3600) / 60, sec = s % 60;
        return h > 0
                ? String.format(Locale.US, "%d:%02d:%02d", h, m, sec)
                : String.format(Locale.US, "%02d:%02d", m, sec);
    }

    static String formatSize(long bytes) {
        double mb = bytes / (1024.0 * 1024.0);
        if (mb >= 1024) return String.format(Locale.US, "%.2f GB", mb / 1024);
        return String.format(Locale.US, "%.1f MB", mb);
    }
}
