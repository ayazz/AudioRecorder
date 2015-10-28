package com.audio.test.audiorecorder;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;

import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button play, stop, record;
    private MediaRecorder myAudioRecorder;
    private MediaController mediacontroller;
    private String outputFile = null;
    private VideoView videoView;
    private ImageView thumbnail;
    private RelativeLayout thumbnailLayout;
    private TextView secondsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        play = (Button) findViewById(R.id.button3);
        stop = (Button) findViewById(R.id.button2);
        record = (Button) findViewById(R.id.button);
        videoView = (VideoView) findViewById(R.id.video_view);
        thumbnailLayout = (RelativeLayout) findViewById(R.id.replay_layout);
        thumbnail = (ImageView) findViewById(R.id.video_screen_thumbnail);
        secondsTextView = (TextView) findViewById(R.id.seconds_text);
        thumbnail.setImageResource(R.drawable.thumbnail);
        videoView.setVisibility(View.GONE);
        thumbnailLayout.setVisibility(View.VISIBLE);

        record.setEnabled(true);
        stop.setEnabled(false);
        play.setEnabled(false);
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";

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
                Toast.makeText(getApplicationContext(), "Audio recorded successfully", Toast.LENGTH_LONG).show();
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

    private void startAudioRecorder(){

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

    private void startAudio(){
        MediaPlayer m = new MediaPlayer();
        try {
            m.setDataSource(outputFile);
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
            String path = "android.resource://" + getPackageName() + "/" + R.raw.video;
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
        super.onPause();
    }


    private void stopAudioRecorder(){
        if (myAudioRecorder != null) {
            myAudioRecorder.stop();
            myAudioRecorder.release();
            myAudioRecorder = null;
        }
    }

    private void stopVideoPlayer(){
        videoView.cancelLongPress();
        videoView.stopPlayback();
    }

    CountDownTimer countDowntimer = new CountDownTimer(30000, 1000) {
        public void onTick(long millisUntilFinished) {
            int seconds = (int)  millisUntilFinished/1000;
            secondsTextView.setText(""+seconds+" second");
        }

        public void onFinish() {
            //                Toast.makeText(getActivity(), "Stop recording Automatically ", Toast.LENGTH_LONG).show();
//            secondsTextView.performClick();
        }
    };
}