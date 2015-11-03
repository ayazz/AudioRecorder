package com.audio.test.audiorecorder;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button play, stop, record;
    private MediaRecorder myAudioRecorder;
    private MediaController mediacontroller;
    MediaPlayer m;
    private String outputFile = null;
    private String watermarkFile = null;
    private VideoView videoView;
    private ImageView thumbnail;
    private RelativeLayout outerLayout;
    private RelativeLayout thumbnailLayout;
    private TextView secondsTextView;
    ProgressDialog progress;
    boolean isFFMPEGExecuted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progress = ProgressDialog.show(MainActivity.this, "Loading ffmpeg Files",
                "Please wait...", true);

        FFmpeg ffmpeg = FFmpeg.getInstance(MainActivity.this);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.d("FFMPEG", "Started.");
                }

                @Override
                public void onFailure() {
                    Log.d("FFMPEG", "onFailure.");
                    if(progress.isShowing()){
                        progress.dismiss();
                    }
                }

                @Override
                public void onSuccess() {
                    Log.d("FFMPEG", "onSuccess.");
                    if(progress.isShowing()){
                        progress.dismiss();
                    }
                }

                @Override
                public void onFinish() {
                    Log.d("FFMPEG", "onFinish.");
                    if(progress.isShowing()){
                        progress.dismiss();
                    }
                }
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
            Log.d("FFMPEG", "onException.");
            if(progress.isShowing()){
                progress.dismiss();
            }
        }

        Constants.AR_AUDIO_DIR = new File(Constants.AR_AUDIO);

        if (!Constants.AR_AUDIO_DIR.exists()) {
            //	        File wallpaperDirectory = new File("/sdcard/Vishwas/");
            Constants.AR_AUDIO_DIR.mkdirs();

            File noMedia = new File(Constants.AR_AUDIO_DIR + "/.nomedia");
            try {
                noMedia.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        Constants.AR_VIDEOS_DIR = new File(Constants.AR_VIDEOS);

        if (!Constants.AR_VIDEOS_DIR.exists()) {
            //	        File wallpaperDirectory = new File("/sdcard/Vishwas/");
            Constants.AR_VIDEOS_DIR.mkdirs();

            File noMedia = new File(Constants.AR_VIDEOS_DIR + "/.nomedia");
            try {
                noMedia.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        Constants.AR_TEMP_VIDEOS_DIR = new File(Constants.AR_TEMP_VIDEOS);

        if (!Constants.AR_TEMP_VIDEOS_DIR.exists()) {
            //	        File wallpaperDirectory = new File("/sdcard/Vishwas/");
            Constants.AR_TEMP_VIDEOS_DIR.mkdirs();

            File noMedia = new File(Constants.AR_TEMP_VIDEOS_DIR + "/.nomedia");
            try {
                noMedia.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        Constants.AR_SHARED_AUDIO_DIR = new File(Constants.AR_SHARED_AUDIO);

        if (!Constants.AR_SHARED_AUDIO_DIR.exists()) {
            Constants.AR_SHARED_AUDIO_DIR.mkdirs();
        }

        Constants.AR_SHARED_VIDEOS_DIR = new File(Constants.AR_SHARED_VIDEOS);

        if (!Constants.AR_SHARED_VIDEOS_DIR.exists()) {
            Constants.AR_SHARED_VIDEOS_DIR.mkdirs();
        }

        play = (Button) findViewById(R.id.button3);
        stop = (Button) findViewById(R.id.button2);
        record = (Button) findViewById(R.id.button);
        videoView = (VideoView) findViewById(R.id.video_view);
        outerLayout = (RelativeLayout) findViewById(R.id.video_view_layout);
        thumbnailLayout = (RelativeLayout) findViewById(R.id.replay_layout);
        thumbnail = (ImageView) findViewById(R.id.video_screen_thumbnail);
        secondsTextView = (TextView) findViewById(R.id.seconds_text);
        thumbnail.setImageResource(R.drawable.thumbnail);
        videoView.setVisibility(View.GONE);
        thumbnailLayout.setVisibility(View.VISIBLE);

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int screenWidth = display.getWidth(); // ((display.getWidth()*20)/100)

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                screenWidth, screenWidth);
        outerLayout.setLayoutParams(lp);

        /*imageOne = (ImageView) itemView.findViewById(R.id.imageOne);
        imageOne.getLayoutParams().height = screenWidth / 3;
        imageOne.getLayoutParams().width = screenWidth / 3;*/

        record.setEnabled(true);
        stop.setEnabled(false);
        play.setEnabled(false);
        outputFile = Constants.AR_AUDIO_DIR + "/recording.3gp";
        watermarkFile = Constants.AR_AUDIO_DIR + "/watermark.png";
//        watermarkFile = "android.resource://" + getPackageName() + "/" + R.raw.watermark;
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thumbnailLayout.setVisibility(View.GONE);
                videoView.setVisibility(View.VISIBLE);
                countDowntimer.start();
                startAudioRecorder();
                startVideo();
                record.setEnabled(false);
                stop.setEnabled(true);
                play.setEnabled(false);
                Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDowntimer.cancel();
                stopAudioRecorder();
                stopVideoPlayer();
                thumbnailLayout.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);
                record.setEnabled(false);
                stop.setEnabled(false);
                play.setEnabled(true);

                if (!isFFMPEGExecuted) {
                    executeFFMpeg();
                }
//                Toast.makeText(getApplicationContext(), "Audio recorded successfully", Toast.LENGTH_LONG).show();
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) throws IllegalArgumentException, SecurityException, IllegalStateException {
                thumbnailLayout.setVisibility(View.GONE);
                videoView.setVisibility(View.VISIBLE);
                record.setEnabled(false);
                stop.setEnabled(false);
                play.setEnabled(false);
                secondsTextView.setVisibility(View.GONE);
                startAudio();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startAudioRecorder() {

        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);

        try {
            myAudioRecorder.prepare();
            myAudioRecorder.start();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void startAudio() {
        m = new MediaPlayer();
        try {
            m.setDataSource(Constants.AR_VIDEOS_DIR + "/video.mp4");
            startVideo();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            m.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        m.start();

        m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stop.performClick();
                record.setEnabled(true);
                stop.setEnabled(false);
                play.setEnabled(true);
            }
        });
        Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();
    }

    private void startVideo() {
        try {
            // Start the MediaController
            /*mediacontroller = new MediaController(
                    MainActivity.this);
            mediacontroller.setAnchorView(videoView);*/
            // Get the URL from String VideoURL
//            String path = "android.resource://" + getPackageName() + "/" + R.raw.video;
            String path = Constants.AR_TEMP_VIDEOS_DIR + "/Testvideo.mp4";
            Uri video = Uri.parse(path);
//            videoView.setMediaController(mediacontroller);

            videoView.setVideoURI(video);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                thumbnailLayout.setVisibility(View.GONE);
                videoView.setVisibility(View.VISIBLE);
                videoView.start();
//                mediacontroller.show(0);
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer vmp) {
                stop.performClick();
            }
        });

    }

    @Override
    protected void onPause() {
        stopAudioRecorder();
        stopVideoPlayer();
        thumbnailLayout.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.GONE);
        record.setEnabled(true);
        stop.setEnabled(false);
        play.setEnabled(false);
        countDowntimer.cancel();

        if (m != null) {
            m.stop();
            m.release();
            m = null;
        }
        super.onPause();
    }


    private void stopAudioRecorder() {
        if (myAudioRecorder != null) {
            myAudioRecorder.stop();
            myAudioRecorder.release();
            myAudioRecorder = null;
        }
    }

    private void stopVideoPlayer() {
        videoView.cancelLongPress();
        videoView.stopPlayback();
    }

    CountDownTimer countDowntimer = new CountDownTimer(30000, 1000) {
        public void onTick(long millisUntilFinished) {
            int seconds = (int) millisUntilFinished / 1000;
            secondsTextView.setVisibility(View.VISIBLE);
            secondsTextView.setText("" + seconds + " second");
        }

        public void onFinish() {
            //                Toast.makeText(getActivity(), "Stop recording Automatically ", Toast.LENGTH_LONG).show();
//            secondsTextView.performClick();
        }
    };


    private void executeFFMpeg() {
        String path = Constants.AR_TEMP_VIDEOS_DIR + "/Testvideo.mp4";
        Uri uri = Uri.parse(path);
        File file = new File(uri.getPath());
        Log.d("File Path 4 : ", "" + file.getAbsolutePath());
        Log.d("File Name 5 : ", "" + file.getName());

        String second = secondsTextView.getText().toString().trim();
        String seconds = second.substring(0, second.indexOf(" "));
        int time = 30 - Integer.parseInt(seconds.toString().trim());
        Log.d("File Name 6 : ", "Size in seconds : " + time);
        final String filename = file.getName();
//        String cmd = "-i " + file.getAbsolutePath() + " -s 400x400 -c:a copy " + Constants.AR_VIDEOS_DIR + "/" + file.getName();    //COPY SAME VIDEO FILE TO OTHER LOCATION
//        String cmd = "-i " + file.getAbsolutePath() + " -ss 00:00:00.0 -c:a copy -t 00:00:" + time + ".0 " + Constants.AR_VIDEOS_DIR + "/video.mp4"; //CUTTING VIDEO FILE AS PER SECONDS INPUT AND SAVING TO OTHER LOCATION
        String cmdMerge = "-i " + file.getAbsolutePath() + " -i " + outputFile + " -shortest -c:a copy " + Constants.AR_VIDEOS_DIR + "/mergeVideo.mp4"; //MERGING AUDIO AND VIDEO AND SAVING VIDEO FILE TO OTHER LOCATION
//        String cmd = "-i " + file.getAbsolutePath() + " -i " + watermarkFile + " -filter_complex 'overlay=10:10' -c:a copy " + Constants.AR_VIDEOS_DIR + "/video.mp4";  //WATER MARKING TO VIDEO AND SAVING VIDEO FILE TO OTHER LOCATION
        FFmpeg ffmpegMerge = FFmpeg.getInstance(MainActivity.this);
        try {
            // to execute "ffmpeg -version" command you just need to pass "-version"
            ffmpegMerge.execute(cmdMerge, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.d("FFMPEG", "Started.");
                    progress = ProgressDialog.show(MainActivity.this, "Creating your video",
                            "Please wait", true);
                }

                @Override
                public void onProgress(String message) {
                    Log.d("FFMPEG", "Progress: " + message);
                }

                @Override
                public void onFailure(String message) {
                    Log.d("FFMPEG", "Failure: " + message);
                    if(progress.isShowing()){
                        progress.dismiss();
                    }
                }

                @Override
                public void onSuccess(String message) {
                    Log.d("FFMPEG", "Success: " + message);
                    if(progress.isShowing()){
                        progress.dismiss();
                    }
                }

                @Override
                public void onFinish() {
                    Log.d("FFMPEG", "Finished.");
                    if(progress.isShowing()){
                        progress.dismiss();
                    }
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
            Log.e("FFMPEG", "FFMPEG Exception");
            if(progress.isShowing()){
                progress.dismiss();
            }
        }

        //        String cmd = "-i " + file.getAbsolutePath() + " -s 400x400 -c:a copy " + Constants.AR_VIDEOS_DIR + "/" + file.getName();    //COPY SAME VIDEO FILE TO OTHER LOCATION
//        String cmd = "-i " + file.getAbsolutePath() + " -ss 00:00:00.0 -c:a copy -t 00:00:" + time + ".0 " + Constants.AR_VIDEOS_DIR + "/video.mp4"; //CUTTING VIDEO FILE AS PER SECONDS INPUT AND SAVING TO OTHER LOCATION
//        String cmd = "-i " + file.getAbsolutePath() + " -i " + outputFile + " -shortest -c:a copy " + Constants.AR_VIDEOS_DIR + "/mergevideo.mp4"; //MERGING AUDIO AND VIDEO AND SAVING VIDEO FILE TO OTHER LOCATION
        String cmdWatermark = "-i " + Constants.AR_VIDEOS_DIR + "/mergeVideo.mp4" + " -i " + watermarkFile + " -filter_complex overlay=10:10 -c:a copy " + Constants.AR_VIDEOS_DIR + "/watermarkVideo.mp4";  //WATER MARKING TO VIDEO AND SAVING VIDEO FILE TO OTHER LOCATION
        //(main_w-overlay_w)-20:(main_h-overlay_h)-20
        FFmpeg ffmpegWatermark = FFmpeg.getInstance(MainActivity.this);
        try {
            // to execute "ffmpeg -version" command you just need to pass "-version"
            ffmpegWatermark.execute(cmdWatermark, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.d("FFMPEG", "Started.");
                    progress = ProgressDialog.show(MainActivity.this, "Creating your video",
                            "Please wait", true);
                }

                @Override
                public void onProgress(String message) {
                    Log.d("FFMPEG", "Progress: " + message);
                }

                @Override
                public void onFailure(String message) {
                    Log.d("FFMPEG", "Failure: " + message);
                    if(progress.isShowing()){
                        progress.dismiss();
                    }
                }

                @Override
                public void onSuccess(String message) {
                    Log.d("FFMPEG", "Success: " + message);
                    if(progress.isShowing()){
                        progress.dismiss();
                    }
                }

                @Override
                public void onFinish() {
                    Log.d("FFMPEG", "Finished.");

                    if(progress.isShowing()){
                        progress.dismiss();
                    }

                    isFFMPEGExecuted = true;
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
            Log.e("FFMPEG", "FFMPEG Exception");
            if(progress.isShowing()){
                progress.dismiss();
            }
        }
    }
}