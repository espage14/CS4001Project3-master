package com.example.espage14.picturematching;


import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class GameFragment extends Fragment {

    //2 lists so each time you hit play again it'll be 8 diff images than the previous game
    private List<Integer> imageIds = new ArrayList<>();
    private List<Integer> notUsedPokemon = new ArrayList<>();

    private Map<Integer ,Drawable> tileImage = new HashMap<>(); //Tile ID -> current tile drawable
    private Map<Integer ,Integer> pairings = new HashMap<>();   //Tile ID -> id of drawable it should be
    private Map<Integer, Boolean> tileMatched = new HashMap<>();//Tile ID -> if it has been matched

    int numClicks;
    Boolean again = false;

    int[] tiles = new int[16];

    //sound stuff
    SoundPool soundPool = null;
    int correctSound = 0;
    int wrongSound = 0;
    int flipSound = 0;
    int wonSound = 0;

    View lastFlipped= null;
    View current = null;
    Handler h = null;
    Drawable defultTile = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Save this when config. changes happen
        setRetainInstance(true);
        initGame();
        //sound stuff
        this.soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        this.correctSound = this.soundPool.load(getContext(), R.raw.correct, 1);
        this.wrongSound = this.soundPool.load(getContext(), R.raw.wrong, 1);
        this.flipSound = this.soundPool.load(getContext(), R.raw.flip, 1);
        this.wonSound = this.soundPool.load(getContext(), R.raw.won, 1);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_game, container, false);
        initViews(root);
        updateTiles(root);
        return root;
    }

    public void flip(View view, Drawable d){
        flipAnimations(view);
        int id = view.getId();
        if(d == null){
            //flip to image
            Drawable image = tileImage.get(id);
            view.setBackground(image);
        } else {
            //flip back
            view.setBackground(defultTile);
        }

        //add sound effect
    }

    public void randomList(Resources r){
        for(int i = 0; i < 16; i++){
            int id = r.getIdentifier("pokemon" + (i+1), "drawable", getActivity().getPackageName());
            notUsedPokemon.add(id);
        }
        for(int i = 0; i < 8; i++){
            int current = notUsedPokemon.remove(i);
            imageIds.add(current);
            imageIds.add(current);
        }
        for(int i = 0; i < 8; i++){
            notUsedPokemon.add(notUsedPokemon.get(i));
        }
        Collections.shuffle(notUsedPokemon);
        Collections.shuffle(imageIds);

    }

    public void compare(View v1, View v2){
        int v1ID = v1.getId();
        int v2ID = v2.getId();
        int a = pairings.get(v1ID);
        int b = pairings.get(v2ID);
        if(a == b){
            soundPool.play(correctSound, 1f, 1f, 1, 0, 1f);
            tileMatched.put(v1ID, true);
            tileMatched.put(v2ID, true);
            if(checkWinner()){
                soundPool.play(wonSound, 1f, 1f, 1, 0, 1f);
                Toast.makeText(getContext(), "Congratulations! You won! :)", Toast.LENGTH_LONG).show();
            }
        } else {
            tileMatched.put(v1ID, false);
            tileMatched.put(v2ID, false);
            soundPool.play(wrongSound, 1f, 1f, 1, 0, 1f);
            flip(v1, v1.getBackground());
            flip(v2, v2.getBackground());
        }
        lastFlipped = null;
        ((GameActivity) getActivity()).stopCompare();
    }
    
    public boolean checkWinner(){
        if(tileMatched.containsValue(false)){
            return false;
        }
        ((GameActivity)getActivity()).won();
        return true;
    }

    public void restartGame(){
        //restart the game
        refreshTiles();
        initViews(getView());
    }

    public void playAgain(){
        if(again != true){
            again = true;
        } else {
            again = false;
        }
        refreshTiles();
        initViews(getView());
    }

    public void refreshTiles(){
        numClicks = 0;
        ((GameActivity)getActivity()).update(numClicks);
        //flip all the tiles
        Object[] allTiles = tileImage.keySet().toArray();

        for(int i = 0; i < allTiles.length; i++){
            View current = getView().findViewById((int)allTiles[i]);
            flip(current , defultTile);
        }

        //update the hashmaps
        tileImage.clear();
        pairings.clear();
        tileMatched.clear();
    }

    //stuff called once
    public void initGame(){
        Resources r = getResources();
        numClicks = 0;
        randomList(r);
        h = new Handler();
        defultTile = r.getDrawable(R.drawable.tile_background, null);


        for(int i = 0; i < 16; i++){
            int id = r.getIdentifier("Tile" + i, "id", getActivity().getPackageName());
            Drawable d;

            if(!again){
                d = r.getDrawable(imageIds.get(i), null);
            } else {
                d = r.getDrawable(notUsedPokemon.get(i), null);
            }

            //update the maps
            if(!again){
                pairings.put(id, imageIds.get(i));
            } else {
                pairings.put(id, notUsedPokemon.get(i));
            }
            tileMatched.put(id, false);
            tileImage.put(id, d);
        }
    }


    //stuff called everytime
    public void initViews(View root){
        Resources r = getResources();



        for(int i = 0; i < 16; i++){
            int id = r.getIdentifier("Tile" + i, "id", getActivity().getPackageName());

            tiles[i] = id;

            ImageButton ib = (ImageButton) root.findViewById(id);

            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!tileMatched.get(view.getId())) {

                        tileMatched.put(view.getId(), true);

                        numClicks++;
                        ((GameActivity)getActivity()).update(numClicks);

                        Drawable d = view.getBackground();
                        Drawable image = tileImage.get(view.getId());
                        if (d != image) {
                            //flip to image
                            flip(view, null);
                            soundPool.play(flipSound, 1f, 1f, 1, 0, 1f);
                            current = view;

                            if(lastFlipped != null){
                                ((GameActivity)getActivity()).startCompare();
                                h.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        compare(current, lastFlipped);
                                    }
                                }, 1000);

                            } else {
                                lastFlipped = current;
                            }
                        }
                    }
                }
            });
        }
    }

    //Save state of game in string
    public String getState(){
        StringBuilder currentState = new StringBuilder();
        //format will be string = tileNumber,currentImage,supposedImage,matched,tileNumber ... ,numClicks
        for(int i = 0; i < 16; i++){
            //tile number
            currentState.append(i);
            currentState.append(',');
            //currentImage
            currentState.append(tileImage.get(tiles[i]));
            currentState.append(',');
            //supposedImage
            currentState.append(pairings.get(tiles[i]));
            currentState.append(',');
            //matched
            currentState.append(tileMatched.get(tiles[i]));
            currentState.append(',');
        }
        currentState.append(numClicks);
        return currentState.toString();
    }

    //Restore game based on a string
    public void putstate(String gameData){
        refreshTiles();
        String[] currentState = gameData.split(",");
        numClicks = Integer.parseInt(currentState[currentState.length-1]);
        int index = 0;
        int currentTile = 0;
        for(int i = 0; i< currentState.length -1 ; i++){
            int current = i - index;
            switch (current){
                case 0:
                    currentTile = Integer.parseInt(currentState[i]);
                    //tile number
                    break;
                case 1:
                    //currentImage
                    int id = getResources().getIdentifier(currentState[i], "drawable", getActivity().getPackageName());
                    tileImage.put(currentTile, getResources().getDrawable(id, null));
                    break;
                case 2:
                    //supposedImage
                    pairings.put(currentTile, Integer.parseInt(currentState[i]));
                    break;
                case 3:
                    //matched
                    tileMatched.put(currentTile, Boolean.getBoolean(currentState[i]));
                    index = index + 4;
                    break;
            }
        }
    }

    public void flipAnimations(View view){
        Animator leftI = AnimatorInflater.loadAnimator(getContext(), R.animator.card_flip_left_in);
        Animator leftO = AnimatorInflater.loadAnimator(getContext(), R.animator.card_flip_left_out);
        Animator rightI = AnimatorInflater.loadAnimator(getContext(), R.animator.card_flip_right_in);
        Animator rightO = AnimatorInflater.loadAnimator(getContext(), R.animator.card_flip_right_out);
        leftI.setTarget(view);
        leftO.setTarget(view);
        rightI.setTarget(view);
        rightO.setTarget(view);
        leftI.start();
        leftO.start();
        rightI.start();
        leftO.start();
        //flip animation code (and xml files) taken from:
        //https://developer.android.com/training/animation/cardflip.html#views
    }

    public void updateTiles(View root){
        if(!tileMatched.isEmpty()) {
            for (int i = 0; i < 16; i++) {
                int currentId = tiles[i];
                if (tileMatched.get(currentId)) {
                    View tile = root.findViewById(currentId);
                    flip(tile, null);
                }
            }
        }
    }

}
