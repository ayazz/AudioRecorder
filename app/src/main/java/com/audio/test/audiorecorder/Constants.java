package com.audio.test.audiorecorder;

import android.os.Environment;

import java.io.File;

/**
 * Created by ayaansar on 11/3/2015.
 */
public class Constants {
    public static File AR_AUDIO_DIR;
    public static File AR_VIDEOS_DIR;
    public static File AR_TEMP_VIDEOS_DIR;
    public static final String AR_AUDIO 		= Environment.getExternalStorageDirectory()+"/AR/Booms/Audios/";
    public static final String AR_VIDEOS 		= Environment.getExternalStorageDirectory()+"/AR/Booms/Videos/";
    public static final String AR_TEMP_VIDEOS   = Environment.getExternalStorageDirectory()+"/AR/Booms/Videos/Temp/";
    public static File AR_SHARED_AUDIO_DIR;
    public static File AR_SHARED_VIDEOS_DIR;
    public static final String AR_SHARED_AUDIO 		= Environment.getExternalStorageDirectory()+"/AR/Shared/Audios/";
    public static final String AR_SHARED_VIDEOS 		= Environment.getExternalStorageDirectory()+"/AR/Shared/Videos/";
}
