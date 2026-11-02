package com.hb.player;

public class VideoItem {
    public final String uri;
    public final String name;
    public final long durationMs;
    public final long sizeBytes;

    public VideoItem(String uri, String name, long durationMs, long sizeBytes) {
        this.uri = uri;
        this.name = name;
        this.durationMs = durationMs;
        this.sizeBytes = sizeBytes;
    }
}
