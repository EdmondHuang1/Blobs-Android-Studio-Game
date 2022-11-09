package com.example.myfirstapp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.constraint.Group;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Base game activity that contains an 8x8 RevyclerView gameboard, with a timer and score displayed.
 * TODO: Add navigation back to main menu and make different activities for different gamemodes
 *
 */
public class TimedModeActivity  extends AppCompatActivity {

    private SoundPool soundPool;
    private int sound1, game_over_sound;

    private RecyclerView mRecyclerView ;
    private MyRecyclerViewAdapter mAdapter;
    private List<Integer> mData = new ArrayList<>();
    private TextView mScoreField, mTextField, mHighscoreField;
    private Button myButton;

    private int mGameLength = 10000, mHighscore, mScore = 0;


    private SharedPreferences mPref;
    private SharedPreferences.Editor mPrefEditor;
    //For game over screen
    private ImageView mGameOverBoard;
    private Button mGameOverMenuNav,mGameOverRestart;
    private TextView mGameOverScore;
    private TextView mGameOverHighscore;
    private Group mGameOverScreen;
    private CountDownTimer mTimer;
    //For pause button
    private Button mPauseMenuNav,mPauseResume,mPauseSoundToggle,mPauseMusicToggle,mPauseButton,mPauseRestart;
    private Group mPauseScreen;
    private long mCurrentTimerTotal;

    private boolean mIsSoundMuted,mIsMusicMuted;
    private Boolean mIsGameOver;



    MediaPlayer theme;



    private List<Integer> dots = Arrays.asList(
            R.drawable.ic_dot_blue,
            R.drawable.ic_dot_green,
            R.drawable.ic_dot_yellow,
            R.drawable.ic_dot_red,
            R.drawable.ic_dot_orange
    );

    /**
     * Called when activity is initialized. Sets up timer, current score, temporary reset button,
     * and RecyclerView. Adds a random id for drawable image allocation(what colour blob to use).
     * All gameboard setup methods are called here.
     *
     * @param savedInstanceState reference to bundle object containing the activity's active state.
     *                           Used to restore state if activity is killed unintentionally.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {


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
        theme = MediaPlayer.create(TimedModeActivity.this,R.raw.bgm);
        sound1 = soundPool.load(this,R.raw.pop1,1);
        game_over_sound = soundPool.load(this,R.raw.game_over,2);

        play();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timed_mode);
        mTextField = findViewById(R.id.time_display);
        mScoreField = findViewById(R.id.score_indicator);
        mHighscoreField = findViewById(R.id.highscore_indicator);
        mRecyclerView =  findViewById(R.id.gameBoard);
        myButton = findViewById(R.id.start_btn);
        mPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        mPrefEditor = mPref.edit();
        mHighscore = mPref.getInt("timed_highscore", 0);
        mHighscoreField.setText("Highscore: " + mHighscore);

        mTextField.setText("Time   " + mGameLength/1000);


        //For the game over screen
        mGameOverBoard = findViewById(R.id.game_over_board);
        mGameOverMenuNav = findViewById(R.id.game_over_main_menu_nav);
        mGameOverRestart = findViewById(R.id.game_over_restart);
        mGameOverScore = findViewById(R.id.game_over_score);
        mGameOverHighscore = findViewById(R.id.game_over_highscore);
        mGameOverScreen = (Group) findViewById(R.id.game_over_group);
        mGameOverScreen.setVisibility(View.GONE);

        //For the pause screen
        mPauseScreen = (Group) findViewById(R.id.pause_screen);
        mPauseMenuNav = findViewById(R.id.pause_main_menu_nav);
        mPauseResume = findViewById(R.id.pause_resume_button);
        mPauseSoundToggle = findViewById(R.id.pause_sound_toggle);
        mPauseMusicToggle = findViewById(R.id.pause_music_toggle);
        mPauseButton = findViewById(R.id.pause_button);
        mPauseRestart = findViewById(R.id.pause_restart);

        //Get sound mute status and set sound button's drawable accordingly
        mIsSoundMuted = mPref.getBoolean("sound_effects_are_muted",false);
        if (mIsSoundMuted) mPauseSoundToggle.setBackground(getDrawable(R.drawable.sound_muted));
        //Do the same for music
        mIsMusicMuted = mPref.getBoolean("music_is_muted",false);
        if (mIsMusicMuted) mPauseMusicToggle.setBackground(getDrawable(R.drawable.music_muted));


        mIsGameOver = false;




        for(int i = 0; i < 64; i++) {
            int randomDotId = (int) (Math.random() * dots.size());
            mData.add(dots.get(randomDotId));
        }

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 8, GridLayoutManager.HORIZONTAL, false) {
            /**
             * prevents touch events form being intercepted by scroll mechanism.
             * @return  always false
             */
            @Override
            public boolean canScrollVertically() {
                return false;
            }
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        });

        //for equally spacing items in recycleview
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);
        mRecyclerView.addItemDecoration(itemDecoration);

        mAdapter = new MyRecyclerViewAdapter(this, mData);
        mRecyclerView.setAdapter(mAdapter);




        //recyclerView.setOnClickListener(new Onclick);
        final RecyclerViewTouchListener itemListener = new RecyclerViewTouchListener(this, mRecyclerView ) {


            /**
             * Sets list of RecyclerView Items passed by the touch listener to empty blobs and
             * updates the RecyclerView adapter to reflect the changes. Increments player score depending
             * on the number of blobs removed his way
             * @param selectList  list of selected blobs
             */
            @Override
            public void removeBlobs(List<View> selectList) {

                soundPool.play(sound1,1,1,1,0,1);

                super.removeBlobs(selectList);
                for(View bulb : selectList) {
                    int pos = mRecyclerView.getChildAdapterPosition(bulb);
                    mData.set(pos, R.drawable.ic_dot_empty);
                    mAdapter.notifyItemChanged(pos);
                }
                //update the score
                mScore += selectList.size() * (selectList.size() - 1);
                mScoreField.setText("Score   " + String.valueOf(mScore));
            }

            /**
             * Checks board for newly removed blobs and updates the RecyclerView Adapter to reflect
             * These changes while repopulating the gameboard from the top.
             */
            @Override
            public void refillBoard() {
                super.refillBoard();
                Queue<Integer> emptySpotQueue = new PriorityQueue<>(Collections.reverseOrder());
                for(int i = 0; i <= 56; i += 8) {
                    for(int j = i + 7; j >= i; j--) {
                        if(mData.get(j) != R.drawable.ic_dot_empty) {
                            if(emptySpotQueue.isEmpty()) continue;
                            int emptySpot = emptySpotQueue.poll();
                            mData.set(emptySpot, mData.get(j));
                            mData.set(j, R.drawable.ic_dot_empty);
                            mAdapter.notifyItemMoved(j, emptySpot);
                        }
                        emptySpotQueue.add(j);
                    }
                    while(!emptySpotQueue.isEmpty()) {
                        int emptySpot = emptySpotQueue.poll();
                        int randomDotId = (int) (Math.random() * dots.size());
                        mData.set(emptySpot, dots.get(randomDotId));
                    }
                }
            }
        };



        /**
         * Implementation of the temporary "start" button functionality; sets countdown timer on
         * click, which updates the time TextView's text. On timer finish, removes the touch listener
         * from the RecyclerView, checks to see if we should log a new
         *
         * @param v button view being clicked
         */
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.removeOnItemTouchListener(itemListener);
                restartGameState(itemListener);
                mGameOverScreen.setVisibility(View.GONE);
            }
        });

        /**
         * On click, makes a call to restart the game state from the game over screen, hiding the
         * gameOverScreen.
         */
        mGameOverRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restartGameState(itemListener);
                mGameOverScreen.setVisibility(View.GONE);
            }
        });

        /**
         * On click, navigates to the main menu from the game over screen
         */
        mGameOverMenuNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent MainActivity = new Intent(getApplication().getApplicationContext(), MainActivity.class);
                startActivity(MainActivity);
            }
        });

        /**
         * On click, cancels the timer, displays the pause screen, and removes the touchListener from the
         * RecyclerView gameboard.
         */
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIsGameOver) {
                    if (mTimer != null) mTimer.cancel();
                    mPauseScreen.setVisibility(View.VISIBLE);
                    mRecyclerView.removeOnItemTouchListener(itemListener);
                }
            }
        });

        /**
         * On click, hides the pause screen, restarts the timer, and returns the touchListener to the
         * RecyclerView gameboard
         */
        mPauseResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPauseScreen.setVisibility(View.GONE);
                if (mCurrentTimerTotal != 0) startTimer(itemListener,mCurrentTimerTotal);

                mRecyclerView.addOnItemTouchListener(itemListener);
            }
        });

        /**
         * On click, navigates to the main menu from the pause screen.
         */
        mPauseMenuNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                soundPool.play(sound1,1,1,1,0,1);
                Intent MainActivity = new Intent(getApplication().getApplicationContext(), MainActivity.class);
                startActivity(MainActivity);
            }
        });


        //TODO Hey Edmond, this is where sound gets toggled
        mPauseSoundToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //toggle the sound boolean
                mIsSoundMuted = !mIsSoundMuted;
                //Store sound mute preference
                mPrefEditor.putBoolean("sound_effects_are_muted",mIsSoundMuted);
                mPrefEditor.commit();
                //Change the button's drawable depending on boolean
                if (mIsSoundMuted) mPauseSoundToggle.setBackground(getDrawable(R.drawable.sound_muted));
                else mPauseSoundToggle.setBackground(getDrawable(R.drawable.sound_unmuted));

            }
        });

        //TODO Hey Edmond, this is where music gets toggled
        mPauseMusicToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //toggle the music button
                mIsMusicMuted = !mIsMusicMuted;
                //Store music mute preference
                mPrefEditor.putBoolean("music_is_muted",mIsMusicMuted);
                mPrefEditor.commit();
                //change the button's drawable depending on boolean
                if (mIsMusicMuted) mPauseMusicToggle.setBackground(getDrawable(R.drawable.music_muted));
                else mPauseMusicToggle.setBackground(getDrawable(R.drawable.music_unmuted));
            }
        });


        /**
         * On click, makes a call to restart the game state
         */
        mPauseRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                soundPool.play(sound1,1,1,1,0,1);
                restartGameState(itemListener);
                mPauseScreen.setVisibility(View.GONE);
            }
        });



    }


    /**
     * Resets the gameboard
     * @param itemListener
     */
    public void restartGameState(final RecyclerViewTouchListener itemListener) {

        mIsGameOver = false;

        mScore = 0;
        mScoreField.setText("Score   " + mScore);
        mTextField.setText("Time   " + mGameLength);

        startTimer(itemListener,mGameLength);
        mRecyclerView.addOnItemTouchListener(itemListener);
    }

    /**
     * Sets mTimer to a specified length. Updates the live timer of mTextField every 500 ms.
     * On timer end, pull up game over screen and store new highscore if there is one.
     * @param itemListener RecyclerView gameboard's touch listener
     * @param length length of timer to set
     */
    public void startTimer(final RecyclerViewTouchListener itemListener,long length) {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        mTimer = new CountDownTimer(length, 500) {
            public void onTick(long total) {
                mTextField.setText("Time   " + total / 1000);
                mCurrentTimerTotal = total;
            }
            public void onFinish() {

                mIsGameOver = true;
                mTextField.setText("done!");
                mGameOverScreen.setVisibility(View.VISIBLE);
                if (mScore > mHighscore) {
                    mHighscore = mScore;
                    mHighscoreField.setText("Highscore: " + mHighscore);
                    mGameOverHighscore.setText("New Highscore: " + mHighscore);
                    mPrefEditor.putInt("timed_highscore", mHighscore);
                    mPrefEditor.commit();

                } else {
                    mGameOverHighscore.setText(("Highscore: " + mHighscore));
                }

                soundPool.play(game_over_sound,1,1,1,0,1);
                mGameOverScore.setText("Score: " + mScore);
                mRecyclerView.removeOnItemTouchListener(itemListener);
            }
        };
        mTimer.start();

    }


    public void play()
    {
        if(theme == null)
        {
            theme = MediaPlayer.create(this,R.raw.bgm);
            theme.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopPlayer();
                }
            });
        }
        theme.start();
    }
    public void pause(View v)
    {
        if(theme != null)
        {
            theme.pause();
        }
    }

    public void stop(View v)
    {
        stopPlayer();
    }

    private void stopPlayer()
    {
        if(theme != null){
            theme.release();
            theme = null;
            Toast.makeText(this,"MediaPlayer Released",Toast.LENGTH_SHORT).show();
        }
    }

    protected void onStop(){
        super.onStop();
        stopPlayer();
    }
}
