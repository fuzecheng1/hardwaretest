package com.hollyland.hardwaretest.manager;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;


import java.io.IOException;

public class MediaManager {

    private static MediaManager mediaManager;
    private AudioManager audioManager;
    private MediaPlayer mediaPlayer;
    private Context context;

    
    public static MediaManager getInstance(Context context){
        if (mediaManager == null){
            return new MediaManager(context);
        }
        return mediaManager;
    }

    private MediaManager(Context context){
        this.context = context;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(MediaPlayer::start);
        mediaPlayer.setOnErrorListener((mediaPlayer, i, i1) -> false);
    }

    public void startMedia(String mp3Name){
        if (audioManager != null) {
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume/2, 0);
        }
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor assetFileDescriptor = assetManager.openFd(mp3Name);
            mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            mediaPlayer.setLooping(true);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void stopPlay(){
        if(mediaPlayer!=null){
            mediaPlayer.stop();
        }
    }

    public void destroyPlayer(){
        audioManager = null;
        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }


}
