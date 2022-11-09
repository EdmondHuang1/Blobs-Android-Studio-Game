package com.example.myfirstapp;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Title screen activity with simple navigation to GamingActivity and SettingsActivity
 * TODO: implement gamemode selection
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private SoundPool soundPool;
    private int sound1, sound2;

    /**
     * Simple buttons for navigation to the SettingsActivity and GamingActivity
     * @param v button View
     */
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.playBTN:

                Intent gamingActivity = new Intent(this, GamingActivity.class);
                soundPool.play(sound2, 1, 1, 1, 0, 1);
                startActivity(gamingActivity);
                break;

            case R.id.settingBTN:
                Intent settingActivity = new Intent(this, SettingActivity.class);
                soundPool.play(sound2, 1, 1, 1, 0, 1);
                startActivity(settingActivity);
                break;

            case R.id.timed_mode_button:
                Intent timedModeActivity= new Intent(this,TimedModeActivity.class);
                soundPool.play(sound2, 1, 1, 1, 0, 1);
                startActivity(timedModeActivity);
                break;

            case R.id.zen_mode_button:
                Intent zenModeActivity = new Intent(this,ZenModeActivity.class);
                soundPool.play(sound2, 1, 1, 1, 0, 1);
                startActivity(zenModeActivity);
                break;

            case R.id.moves_mode_button:
                Intent movesModeActivity = new Intent(this, MovesModeActivity.class);
                soundPool.play(sound2, 1, 1, 1, 0, 1);
                startActivity(movesModeActivity);


        }
    }

    private ImageView mPlayBTN;
    private ImageView mSettingBTN;
    private Button mTimedModeButton;
    private Button mZenModeButton;
    private Button mMovesModeButton;

    /**
     * Called when the activity is created. Links button variables to the buttons.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            AudioAttributes audioAttributes = new AudioAttributes.Builder().
                    setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_GAME).build();

            soundPool = new SoundPool.Builder().setMaxStreams(4).setAudioAttributes(audioAttributes).build();

        }
        else
        {
            soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC,0);
        }

        sound1 = soundPool.load(this,R.raw.pop1,1);
        sound2 = soundPool.load(this,R.raw.pop2,1);

        setContentView(R.layout.activity_main);

        mPlayBTN = findViewById(R.id.playBTN);
        mPlayBTN.setOnClickListener(this);

        mSettingBTN = findViewById(R.id.settingBTN);
        mSettingBTN.setOnClickListener(this);

        mTimedModeButton = findViewById(R.id.timed_mode_button);
        mTimedModeButton.setOnClickListener(this);

        mZenModeButton = findViewById(R.id.zen_mode_button);
        mZenModeButton.setOnClickListener(this);

        mMovesModeButton = findViewById(R.id.moves_mode_button);
        mMovesModeButton.setOnClickListener(this);


    }
}
