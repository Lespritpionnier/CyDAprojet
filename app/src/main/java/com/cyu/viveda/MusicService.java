package com.cyu.viveda;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.FileDescriptor;
import java.io.IOException;

public class MusicService extends Service {
    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBinder();
    }



    MediaPlayer mediaPlayer;

    private class MusicBinder extends Binder {
        public MusicBinder() {
            mediaPlayer = new MediaPlayer();
        }

        public void playMusic(String musicPath) {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(musicPath);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void pauseMusic() {
            mediaPlayer.pause();
        }

        public void startMusic() {
            mediaPlayer.start();
        }

        public MediaPlayer getPlayer() {
            return mediaPlayer;
        }
    }

    //Donc ici c'est juste pour d'être propre (fermer mediaPlayer sinon il continue)
    @Override
    public boolean onUnbind(Intent intent) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
        return super.onUnbind(intent);
    }
}