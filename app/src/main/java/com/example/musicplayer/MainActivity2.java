package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
        updateTime.interrupt();
    }

    TextView textView;
    TextView currentDuration;
    TextView maxDuration;
    SeekBar seekBar;
    ImageView play;
    ImageView loop;
    ImageView previous;
    ImageView next;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    Thread updateSeek;
    Thread updateTime;
    int position;
    boolean loop_on;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        textView = findViewById(R.id.textView);
        currentDuration = findViewById(R.id.currentDuration);
        maxDuration = findViewById(R.id.maxDuration);
        seekBar = findViewById(R.id.seekBar);
        play = findViewById(R.id.play);
        loop = findViewById(R.id.loop);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String currentSong = intent.getStringExtra("currentSong");
        songs = (ArrayList) bundle.getParcelableArrayList("songs");
        position = intent.getIntExtra("position", 0);


        textView.setText(currentSong);
        textView.setSelected(true);

//        Play the song
        Uri uri = Uri.parse(songs.get(position).toString());

        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.start();

        loop_on = false;


        seekBar.setMax(mediaPlayer.getDuration());

//        Update the seekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        updateSeek = new Thread(){
            @Override
            public void run() {
                int currentPosition = 0;
                try {
                    while(currentPosition < mediaPlayer.getDuration()){
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        sleep(1000);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();

        updateTime = new Thread(){
            @Override
            public void run() {
                try {
                    while(!updateTime.isInterrupted()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String currentDur = DateUtils.formatElapsedTime(mediaPlayer.getCurrentPosition() / 1000);
                                String maxDur = DateUtils.formatElapsedTime(mediaPlayer.getDuration() / 1000);
                                currentDuration.setText(String.valueOf(currentDur));
                                maxDuration.setText(String.valueOf(maxDur));
                            }
                        });

                        Thread.sleep(1000);
                    }
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        };

        updateTime.start();


//        On clicking play button
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    play.setImageResource(R.drawable.play_icon);
                    mediaPlayer.pause();
                }
                else{
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });

//        On clicking previous button
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();

                play.setImageResource(R.drawable.pause);

                if(position != 0){
                    position = position - 1;
                }
                else{
                    position = songs.size() - 1;
                }

                Uri uri = Uri.parse(songs.get(position).toString());

                textView.setText(songs.get(position).getName().toString());

                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                seekBar.setProgress(0);
                seekBar.setMax(mediaPlayer.getDuration());
            }
        });

//        On clicking next button
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();

                play.setImageResource(R.drawable.pause);

                if(position != songs.size() - 1){
                    position = position + 1;
                }
                else{
                    position = 0;
                }

                Uri uri = Uri.parse(songs.get(position).toString());

                textView.setText(songs.get(position).getName().toString());

                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                seekBar.setProgress(0);
                seekBar.setMax(mediaPlayer.getDuration());
            }
        });

//        On clicking Loop button
        loop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(loop_on == true){
                    loop_on = false;
                    loop.setImageResource(R.drawable.loop);
                }
                else{
                    loop_on = true;
                    loop.setImageResource(R.drawable.loop_on);
                }

                mediaPlayer.setLooping(loop_on);

            }
        });


//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.bg2)
//                .setContentTitle("Hello")
//                .setContentText("world")
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
    }
}