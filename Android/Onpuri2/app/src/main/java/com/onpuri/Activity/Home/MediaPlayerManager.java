package com.onpuri.Activity.Home;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by kutemsys on 2016-09-09.
 */
public class MediaPlayerManager {
    private static final String TAG = "MediaPlayerManager";

    public MediaPlayer mpfile = null;
 //   public MediaPlayerManager mPlayer = new MediaPlayerManager();

    public void PlayFile(String filenum) {
        String path = GetFilePath(filenum);

        if (mpfile != null) {
            mpfile.stop();
            mpfile.release();
            mpfile = null;
        }
        mpfile = new MediaPlayer();

        try {
            mpfile.setDataSource(path);
            mpfile.prepare();
        } catch (IOException e) {
            Log.d(TAG, "Audio Play error");
            return;
        }
        mpfile.start();
    }

    public static synchronized String GetFilePath(String filenum) {
        String sdcard = Environment.getExternalStorageState();
        File file;

        if ( !sdcard.equals(Environment.MEDIA_MOUNTED)) { file = Environment.getRootDirectory(); }
        else { file = Environment.getExternalStorageDirectory(); }

        String dir = file.getAbsolutePath();
        String path = dir + "/Daily E/"+filenum+"listen.mp3";

        return path;
    }
}
