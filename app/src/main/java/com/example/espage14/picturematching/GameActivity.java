package com.example.espage14.picturematching;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class GameActivity extends Activity {

    private Handler h = null;
    private GameFragment gameFragment;
    private ControlFragment controlFragment;
    public static final String KEY_RESTORE = "key_restore";
    public static final String PREF_RESTORE = "pref_restore";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //set up some things
        h = new Handler();
        gameFragment = (GameFragment) getFragmentManager().findFragmentById(R.id.game_fragment);
        controlFragment = (ControlFragment) getFragmentManager().findFragmentById(R.id.fragment_game_controls);

        //see if you are restoring a previous game
        boolean restore = getIntent().getBooleanExtra(KEY_RESTORE, false);
        if(restore){
            //restore was true, so load settings from last game
            String gameData = getPreferences(MODE_PRIVATE).getString(PREF_RESTORE, null);
            if(gameData != null){
                gameFragment.putstate(gameData);
            }
        }



    }

    public void restartGame(){
        gameFragment.restartGame();
    }

    public void playAgain() {
        gameFragment.playAgain();
    }

    public void won(){
        View view1 = findViewById(R.id.play_again_button);
        View view2 = findViewById(R.id.play_again_text);
        view1.setVisibility(View.VISIBLE);
        view2.setVisibility(View.VISIBLE);
    }

    public void playAway(){
        View view1 = findViewById(R.id.play_again_button);
        View view2 = findViewById(R.id.play_again_text);
        view1.setVisibility(View.GONE);
        view2.setVisibility(View.GONE);
    }

    public void startCompare(){
        View view = findViewById(R.id.compare);
        view.setVisibility(View.VISIBLE);
    }

    public void stopCompare(){
        View view = findViewById(R.id.compare);
        view.setVisibility(View.GONE);
    }

    public void update(final int number){
        h.post(new Runnable() {
            @Override
            public void run() {
                controlFragment.clicks.setText(Integer.toString(number));
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        String gameData = gameFragment.getState(); //get the current state of the game
        getPreferences(MODE_PRIVATE).edit().putString(PREF_RESTORE, gameData).apply();
    }
}
