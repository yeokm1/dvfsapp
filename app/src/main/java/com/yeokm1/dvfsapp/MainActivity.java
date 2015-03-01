package com.yeokm1.dvfsapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {


    EditText lowBoundText;
    EditText highBoundText;
    Button startStopButton;

    private DVFSHandler dvfsHandler;


    private static final String INVALID_FPS_VALUES = "Invalid FPS values provided";

    private static final String DVFS_STOP = "Stop";
    private static final String DVFS_START = "Start";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lowBoundText = (EditText) findViewById(R.id.main_edittext_fps_low_bound);
        highBoundText = (EditText) findViewById(R.id.main_edittext_fps_high_bound);

        startStopButton = (Button) findViewById(R.id.main_button_start_stop);

        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPress();
            }
        });

        dvfsHandler = new DVFSHandler(getApplicationContext());
    }

    @Override
    public void onResume(){
        super.onResume();

        refreshButtonText();
    }

    public void buttonPress(){

        if(dvfsHandler.isDVFSActive()){
            dvfsHandler.stopDVFS();
        } else {
            try {
                int fpsLowBound = Integer.parseInt(lowBoundText.getText().toString());
                int fpsHighBound = Integer.parseInt(highBoundText.getText().toString());

                dvfsHandler.startDVFS(fpsLowBound, fpsHighBound);

            }catch(NumberFormatException e){
                Toast.makeText(this, INVALID_FPS_VALUES, Toast.LENGTH_SHORT).show();
            }
        }

        refreshButtonText();
    }


    private void refreshButtonText(){

        boolean status = dvfsHandler.isDVFSActive();

        if(status){
            startStopButton.setText(DVFS_STOP);
        } else {
            startStopButton.setText(DVFS_START);
        }


    }

}
