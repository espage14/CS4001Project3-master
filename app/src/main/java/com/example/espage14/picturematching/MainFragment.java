package com.example.espage14.picturematching;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by espage14 on 9/19/2016.
 */
public class MainFragment extends Fragment {

    Button newGame = null;
    Button continueGame = null;
    Button about = null;

    private AlertDialog msg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //Find Buttons
        this.newGame = (Button) rootView.findViewById(R.id.new_game_button);
        this.continueGame = (Button) rootView.findViewById(R.id.continue_game_button);
        this.about =(Button)  rootView.findViewById(R.id.about_game_button);

        //Make Buttons do stuff when clicked
        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GameActivity.class);
                getActivity().startActivity(intent);
            }
        });

        continueGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GameActivity.class);
                intent.putExtra(GameActivity.KEY_RESTORE, true);
                getActivity().startActivity(intent);
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //display a msg enplaning the game
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.about_game);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                msg = builder.show();
            }
        });

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();

        // If the msg from the about button is up, dismiss it
        if (msg != null)
            msg.dismiss();
    }
}
