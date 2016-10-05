package com.example.espage14.picturematching;


import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ControlFragment extends Fragment {

    Button menu = null;
    Button restart = null;
    Button playAgain = null;
    TextView clicks = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Make rootView to return
        View rootView = inflater.inflate(R.layout.fragment_control, container, false);

        //Get the buttons
        this.menu = (Button) rootView.findViewById(R.id.home_button);
        this.restart = (Button) rootView.findViewById(R.id.restart_button);
        this.playAgain = (Button) rootView.findViewById(R.id.play_again_button);

        this.clicks = (TextView) rootView.findViewById(R.id.clicks);

        //Make them do stuff when clicked
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GameActivity) getActivity()).restartGame();
            }
        });

        playAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //play game again with diff pictures
                ((GameActivity)getActivity()).playAgain();
                ((GameActivity)getActivity()).playAway();

            }
        });

        return rootView;
    }


}
